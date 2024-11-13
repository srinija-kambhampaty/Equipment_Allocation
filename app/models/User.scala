package models

import play.api.libs.json._
import play.api.libs.functional.syntax._

case class User(
                 id: Option[Long] = None,  // id will be auto-incremented
                 name: String,
                 department: String,
                 role: String,
                 active: Boolean = true
               )

object User {
  // Read for the User fields
  private val idReads: Reads[Option[Long]] = (JsPath \ "id").readNullable[Long]
  private val nameReads: Reads[String] = (JsPath \ "name").read[String]
  private val departmentReads: Reads[String] = (JsPath \ "department").read[String]
  private val roleReads: Reads[String] = (JsPath \ "role").read[String]
  private val activeReads: Reads[Boolean] = (JsPath \ "active")
    .readNullable[Boolean]
    .map(_.getOrElse(true)) // Default to true if missing

  // Combine all the reads
  implicit val userReads: Reads[User] = (
    idReads and
      nameReads and
      departmentReads and
      roleReads and
      activeReads
    )(User.apply _)

  // Use Json.writes to generate Writes automatically
  implicit val userWrites: Writes[User] = Json.writes[User]

  // Combine Reads and Writes into Format
  implicit val userFormat: Format[User] = Format(userReads, userWrites)
}
