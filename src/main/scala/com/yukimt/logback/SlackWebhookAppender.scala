package com.yukimt.logback

import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.Layout
import ch.qos.logback.core.LayoutBase
import ch.qos.logback.core.UnsynchronizedAppenderBase
import scalaj.http._
import org.json4s.DefaultFormats
import org.json4s.native.Serialization

class SlackWebhookAppender extends UnsynchronizedAppenderBase[ILoggingEvent]{
  implicit val formats = DefaultFormats

  private var channel: Option[String] = None
  private var webhookUrl: Option[String] = None
  private var username: Option[String] = None
  private var iconEmoji: Option[String] = None
  private var layout: Layout[ILoggingEvent]  = defaultLayout

  case class Payload(channel: String, username: String, icon_emoji: String, text: String)

  private def defaultLayout:Layout[ILoggingEvent] = new LayoutBase[ILoggingEvent]{
    def doLayout(event: ILoggingEvent) :String =
      s"-- [${event.getLevel}] - ${event.getFormattedMessage.replaceAll("\n", "\n\t")}"
  }

  override def append(evt: ILoggingEvent) = {
    val optResult = for {
      c <- channel
      w <- webhookUrl
    } yield {
      val payload = Payload(c,
        username.getOrElse("Slack Logback Appender"),
        iconEmoji.getOrElse(":japanese_goblin:"),
        layout.doLayout(evt))
      val res = Http(w).postForm(Seq("payload" -> Serialization.write(payload))).asString
      if(res.code == 200) Result.Success
      else Result.Failure(res.body.toString)
    }

    optResult.getOrElse(Result.Failure("Channel Name is not set")) match {
      case Result.Success => ()
      case Result.Failure(msg) =>
        val errorMessage = s"Error in Logback-Slack Webhook Appender: $msg"
        new RuntimeException(errorMessage).printStackTrace
        addError(errorMessage)
    }

  }

  def setChannel(t: String) = { channel = Some(if(t.startsWith("#")) t else "#" + t) }
  def setWebhookUrl(w: String) = {webhookUrl = Some(w)}
  def setUsername(u: String) = {username = Some(u)}
  def setIconEmoji(i: String) = {
    val prefix = if(i.startsWith(":")) "" else ":"
    val suffix = if(i.endsWith(":")) "" else ":"
    iconEmoji = Some(prefix + i + suffix)
  }
  def setLayout(l: Layout[ILoggingEvent]) = {layout = l}
}
