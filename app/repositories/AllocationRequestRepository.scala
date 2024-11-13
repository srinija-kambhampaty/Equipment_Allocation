package repositories

import models.AllocationRequest
import models.db.AllocationRequestTable
import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AllocationRequestRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]
  import dbConfig._
  import profile.api._

  private val allocationRequests = TableQuery[AllocationRequestTable]

  def list(): Future[Seq[AllocationRequest]] = db.run(allocationRequests.result)

  def get(id: Long): Future[Option[AllocationRequest]] =
    db.run(allocationRequests.filter(_.id === id).result.headOption)

  def create(request: AllocationRequest): Future[Long] = {
    val insertQuery = allocationRequests returning allocationRequests.map(_.id)
    db.run(insertQuery += request)
  }

  def update(id: Long, request: AllocationRequest): Future[Option[AllocationRequest]] = {
    val updateQuery = allocationRequests.filter(_.id === id)
      .map(r => (r.userId, r.equipmentId, r.status, r.purpose, r.requestDate, r.expectedReturnDate, r.returnStatus))
      .update((request.userId, request.equipmentId, request.status, request.purpose, request.requestDate, request.expectedReturnDate, request.returnStatus))

    db.run(updateQuery).flatMap {
      case 0 => Future.successful(None)
      case _ => get(id)
    }
  }

  def delete(id: Long): Future[Boolean] = {
    db.run(allocationRequests.filter(_.id === id).delete).map(_ > 0)
  }
}
