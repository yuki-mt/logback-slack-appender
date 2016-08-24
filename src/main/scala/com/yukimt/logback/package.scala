package com.yukimt

package object logback{
  sealed trait Result
  object Result{
    case object Success extends Result
    case class Failure(message: String) extends Result
  }
}
