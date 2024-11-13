package models.db

import models.Equipment
import slick.jdbc.MySQLProfile.api._

class EquipmentTable(tag: Tag) extends Table[Equipment](tag, "equipment") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def productId = column[Long]("product_id")
  def conditionStatus = column[String]("conditionStatus")
  def availableStatus = column[Boolean]("availableStatus")

  def * = (id, productId, conditionStatus, availableStatus) <> ((Equipment.apply _).tupled, Equipment.unapply)
}
