package models.db

import models.User
import slick.jdbc.MySQLProfile.api._

class UserTable(tag: Tag) extends Table[User](tag, "users") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name")
  def department = column[String]("department")
  def role = column[String]("role")
  def active = column[Boolean]("active", O.Default(true))

  def * = (id.?, name, department, role, active) <> ((User.apply _).tupled, User.unapply)
}
