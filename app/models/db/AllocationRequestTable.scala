package models.db

import models.AllocationRequest
import slick.jdbc.MySQLProfile.api._

class AllocationRequestTable(tag: Tag) extends Table[AllocationRequest](tag, "allocation_request") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def userId = column[Long]("user_id") // Foreign key to User
  def equipmentId = column[Long]("equipment_id") // Foreign key to Equipment
  def status = column[String]("status")
  def purpose = column[String]("purpose")
  def requestDate = column[String]("request_date")
  def expectedReturnDate = column[String]("expected_return_date")
  def returnStatus = column[Option[String]]("return_status")

  def * = (id.?, userId, equipmentId, status, purpose, requestDate, expectedReturnDate, returnStatus) <> ((AllocationRequest.apply _).tupled, AllocationRequest.unapply)
}
