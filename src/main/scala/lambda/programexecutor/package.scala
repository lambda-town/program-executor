package lambda

import cats.implicits._
import cats.data.EitherT
import cats.effect.IO
import fs2._
import fs2.concurrent.Queue
import lambda.programexecutor.Executor._
import lambda.programexecutor.ProgramEvent.{Exit, StdErr, StdOut}

import scala.sys.process._

package object programexecutor {

  def runProcess(commands: List[String]): Stream[IO, ProgramEvent] = {

    val streamIO = for {
      queue <- Queue.unbounded[IO, Option[ProgramEvent]]
      _ <- (IO {
        val processLogger = new ProcessLogger {
          def out(s: => String): Unit = {
            queue.offer1(Some(StdOut(s))).unsafeRunAsyncAndForget()
          }

          def err(s: => String): Unit = {
            queue.offer1(Some(StdErr(s))).unsafeRunAsyncAndForget()
          }

          def buffer[T](f: => T): T = f
        }

        Process(commands).run(processLogger).exitValue()
      }
        .recover({ case _ => 1 })
        .flatMap(code => queue.offer1(Some(Exit(code)))) *> queue.offer1(None))
        .start
    } yield queue.dequeue.unNoneTerminate

    Stream.eval(streamIO).parJoinUnbounded
  }

  def toEitherT(stream: Stream[IO, ProgramEvent]): EitherT[IO, String, String] = EitherT {
    stream.compile.toList.map {
      case events :+ Exit(0) => Right(events.collect({
        case StdOut(line) => line
      }).mkString("\n"))
      case events => Left(events.collect({
        case StdErr(line) => line
      }).mkString("\n"))
    }
  }

}
