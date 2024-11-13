package models.db

import models.Equipment
import slick.jdbc.MySQLProfile.api._

class EquipmentTable(tag: Tag) extends Table[Equipment](tag, "equipment") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name")
  def category = column[String]("category")
  def description = column[String]("description")
  def conditionStatus = column[String]("condition_status")
  def availabilityStatus = column[Boolean]("availability_status", O.Default(true))

  def * = (id.?, name, category, description, conditionStatus, availabilityStatus) <> ((Equipment.apply _).tupled, Equipment.unapply)
}

