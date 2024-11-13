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

  private val equipmentTableQuery = TableQuery[EquipmentTable] // Renamed for clarity

  // Fetch equipment by product ID
  def findByProductId(productId: Long): Future[Seq[Equipment]] =
    db.run(equipmentTableQuery.filter(_.productId === productId).result)

  // Add new equipment
  def add(newEquipment: Equipment): Future[Long] = { // Renamed parameter for clarity
    val insertQuery = equipmentTableQuery returning equipmentTableQuery.map(_.id)
    db.run(insertQuery += newEquipment)
  }
}

