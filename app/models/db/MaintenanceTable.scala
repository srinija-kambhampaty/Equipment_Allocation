package models.db

import models.Maintenance
import slick.jdbc.MySQLProfile.api._
import java.time.LocalDate

class MaintenanceTable(tag: Tag) extends Table[Maintenance](tag, "maintenances") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def equipmentId = column[Long]("equipment_id")
  def startDate = column[LocalDate]("start_date")
  def endDate = column[Option[LocalDate]]("end_date")
  def description = column[String]("description")
  def maintenanceStatus = column[String]("maintenance_status") // "completed" or "inprogress"


  def * = (id.?, equipmentId, startDate, endDate, description, maintenanceStatus) <> ((Maintenance.apply _).tupled, Maintenance.unapply)
}
