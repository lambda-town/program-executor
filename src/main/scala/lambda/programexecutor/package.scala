package lambda

import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets

import cats.effect.IO
import com.zaxxer.nuprocess.{NuAbstractProcessHandler, NuProcess, NuProcessBuilder}
import fs2._
import fs2.concurrent.Queue
import lambda.programexecutor.Executor._
import lambda.programexecutor.ProgramEvent.{Exit, StdErr, StdOut}

package object programexecutor {


  def runProcess(commands: List[String], destroy: IO[Unit] = IO.unit): IO[Stream[IO, ProgramEvent]] = {

    for {
      queue <- Queue.unbounded[IO, Option[ProgramEvent]]
      _ <- IO
        .cancelable[Unit] { cb =>
          val processHandler = new NuAbstractProcessHandler {

            override def onStart(nuProcess: NuProcess): Unit = {
              cb(Right())
            }

            override def onExit(statusCode: Int): Unit = {
              queue.offer1(Some(Exit(statusCode)))
              queue.offer1(None)
            }

            override def onStdout(buffer: ByteBuffer, closed: Boolean): Unit = {
              val bytes = new Array[Byte](buffer.remaining)
              buffer.get(bytes)
              queue
                .offer1(Some(StdOut(new String(bytes, StandardCharsets.UTF_8))))
                .unsafeRunAsyncAndForget()
            }

            override def onStderr(buffer: ByteBuffer, closed: Boolean): Unit = {
              val bytes = new Array[Byte](buffer.remaining)
              buffer.get(bytes)
              queue
                .offer1(Some(StdErr(new String(bytes, StandardCharsets.UTF_8))))
                .unsafeRunAsyncAndForget()
            }

          }
          val pb = new NuProcessBuilder(commands: _*)
          pb.setProcessListener(processHandler)
          val process = pb.start()

          destroy.flatMap(_ => IO(process.destroy(true)))
        }
    } yield queue.dequeue.unNoneTerminate

  }

}
