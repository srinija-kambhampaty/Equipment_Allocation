package repositories

import models.AllocationRequest
import models.db.AllocationRequestTable
import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import java.time.LocalDate
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AllocationRequestRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  private val allocationRequests = TableQuery[AllocationRequestTable]

  // Fetch all allocation requests
  def list(): Future[Seq[AllocationRequest]] = db.run(allocationRequests.result)

  // Check if equipment is available
  def isEquipmentAvailable(equipmentId: Long): Future[Boolean] = {
    val query = allocationRequests.filter(req => req.equipmentId === equipmentId && req.returnStatus === "pending").exists.result
    db.run(query).map(!_) // Return true if no pending allocation exists
  }

  // Create a new allocation request
  def create(request: AllocationRequest): Future[Long] = {
    val insertQuery = allocationRequests returning allocationRequests.map(_.id)
    db.run(insertQuery += request)
  }

  // Update return details of an allocation request
  def updateReturnDetails(id: Long, returnDate: LocalDate, returnCondition: String): Future[Boolean] = {
    val query = allocationRequests.filter(_.id === id)
      .map(req => (req.returnStatus, req.returnDate, req.returnCondition))
      .update(("returned", Some(returnDate), Some(returnCondition)))

    db.run(query).map(_ > 0) // Return true if the update was successful
  }
}
