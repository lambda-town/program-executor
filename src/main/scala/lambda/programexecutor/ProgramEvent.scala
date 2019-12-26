package lambda.programexecutor

sealed trait ProgramEvent

object ProgramEvent {
  case class StdOut(line: String) extends ProgramEvent
  case class StdErr(line: String) extends ProgramEvent
  case class Exit(code: Int) extends ProgramEvent
}

