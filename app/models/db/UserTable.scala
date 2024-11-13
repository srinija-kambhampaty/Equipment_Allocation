package models.db

import models.User
import slick.jdbc.MySQLProfile.api._

class UserTable(tag: Tag) extends Table[User](tag, "users") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name")
  def email = column[String]("email")
  def role = column[String]("role")

  def * = (id, name, email, role) <> ((User.apply _).tupled, User.unapply)
}
