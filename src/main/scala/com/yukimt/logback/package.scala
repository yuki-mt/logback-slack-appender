package com.yukimt

package object logback{
  sealed trait Result
  object Result{
    case object Success extends Result
    case class Failure(message: String) extends Result
  }

  //case classes that for Incoming WebHook
  case class Payload(channel: String, username: String, icon_emoji: String, text: String, attachments: Option[Seq[Attachment]])
  case class Attachment(text: String, color: String)
}
