package repositories

import models.Maintenance
import models.db.MaintenanceTable
import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import scala.concurrent.{ExecutionContext, Future}
import java.time.LocalDate

@Singleton
class MaintenanceRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  private val maintenances = TableQuery[MaintenanceTable]

  // Fetch all maintenance records
  def list(): Future[Seq[Maintenance]] = db.run(maintenances.result)

  // Add a new maintenance record
  def create(maintenance: Maintenance): Future[Long] = {
    val insertQuery = maintenances returning maintenances.map(_.id)
    db.run(insertQuery += maintenance)
  }
}

