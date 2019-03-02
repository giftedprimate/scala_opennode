package com.github.scala_opennode.entities

import play.api.libs.json.{Json, OFormat}

object OpenNodeError {
  implicit val openNodeErrorFormat: OFormat[OpenNodeError] = Json.format[OpenNodeError]
}

case class OpenNodeError(success: Boolean, message: String)

