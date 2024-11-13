package models

import play.api.libs.json._

case class User(
                 id: Long,
                 name: String,
                 email: String,
                 role: String
               )

object User {
  implicit val userFormat: Format[User] = Json.format[User]
}
