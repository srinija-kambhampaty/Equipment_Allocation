package models.db

import models.AllocationRequest
import slick.jdbc.MySQLProfile.api._
import java.time.LocalDate

class AllocationRequestTable(tag: Tag) extends Table[AllocationRequest](tag, "allocation_requests") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def userId = column[Long]("user_id")
  def equipmentId = column[Long]("equipment_id")
  def purpose = column[String]("purpose")
  def requestDate = column[LocalDate]("request_date")
  def expectedReturnDate = column[LocalDate]("expected_return_date")
  def returnStatus = column[String]("return_status")
  def returnDate = column[Option[LocalDate]]("return_date")
  def returnCondition = column[Option[String]]("return_condition")

  def * = (id, userId, equipmentId, purpose, requestDate, expectedReturnDate, returnStatus, returnDate, returnCondition) <> ((AllocationRequest.apply _).tupled, AllocationRequest.unapply)
}
