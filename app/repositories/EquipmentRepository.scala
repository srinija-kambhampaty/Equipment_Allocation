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

  private val equipmentTableQuery = TableQuery[EquipmentTable]

  // Fetch equipment by product ID
  def findByProductId(productId: Long): Future[Seq[Equipment]] =
    db.run(equipmentTableQuery.filter(_.productId === productId).result)

  // Add new equipment
  def add(newEquipment: Equipment): Future[Long] = {
    val insertQuery = equipmentTableQuery returning equipmentTableQuery.map(_.id)
    db.run(insertQuery += newEquipment)
  }

  // Update the condition status of an equipment item
  def updateConditionStatus(equipmentId: Long, newStatus: String): Future[Int] = {
    db.run(equipmentTableQuery.filter(_.id === equipmentId).map(_.conditionStatus).update(newStatus))
  }

  // Fetch equipment by ID
  def getEquipmentById(equipmentId: Long): Future[Option[Equipment]] = {
    db.run(equipmentTableQuery.filter(_.id === equipmentId).result.headOption)
  }
}

