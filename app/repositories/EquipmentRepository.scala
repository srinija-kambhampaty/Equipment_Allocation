package repositories

import models.Equipment
import models.db.EquipmentTable
import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class EquipmentRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]
  import dbConfig._
  import profile.api._

  private val equipmentTable = TableQuery[EquipmentTable] // Renamed to `equipmentTable`

  // Fetch all available equipment
  def list(): Future[Seq[Equipment]] = db.run(equipmentTable.filter(_.availabilityStatus === true).result)

  // Get equipment by ID if available
  def get(id: Long): Future[Option[Equipment]] =
    db.run(equipmentTable.filter(e => e.id === id && e.availabilityStatus === true).result.headOption)

  // Create a new equipment entry and return the auto-generated ID
  def create(newEquipment: Equipment): Future[Long] = { // Renamed parameter to `newEquipment`
    val insertQuery = equipmentTable returning equipmentTable.map(_.id)
    db.run(insertQuery += newEquipment)
  }

  // Update an existing equipment entry by ID
  def update(id: Long, updatedEquipment: Equipment): Future[Option[Equipment]] = { // Renamed parameter to `updatedEquipment`
    val updateQuery = equipmentTable.filter(e => e.id === id && e.availabilityStatus === true)
      .map(e => (e.name, e.category, e.description, e.conditionStatus, e.availabilityStatus))
      .update((updatedEquipment.name, updatedEquipment.category, updatedEquipment.description, updatedEquipment.conditionStatus, updatedEquipment.availabilityStatus))

    db.run(updateQuery).flatMap {
      case 0 => Future.successful(None)
      case _ => get(id)
    }
  }

  // Mark equipment as unavailable (soft delete)
  def delete(id: Long): Future[Boolean] = {
    db.run(equipmentTable.filter(e => e.id === id && e.availabilityStatus === true)
      .map(e => e.availabilityStatus).update(false)).map(_ > 0)
  }
}
