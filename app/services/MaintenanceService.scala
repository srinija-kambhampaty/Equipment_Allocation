package services

import models.Maintenance
import repositories.MaintenanceRepository
import javax.inject.{Inject, Singleton}
import java.time.LocalDate
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class MaintenanceService @Inject()(maintenanceRepository: MaintenanceRepository)(implicit ec: ExecutionContext) {

  // Fetch all maintenance records
  def listMaintenances(): Future[Seq[Maintenance]] = maintenanceRepository.list()

  // Create a new maintenance record
  def createMaintenance(equipmentId: Long, description: String): Future[Long] = {
    val maintenance = Maintenance(
      equipmentId = equipmentId,
      startDate = LocalDate.now,
      description = description,
      maintenanceStatus = "inprogress"
    )
    maintenanceRepository.create(maintenance)
  }
}
