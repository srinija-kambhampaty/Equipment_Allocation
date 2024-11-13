package services

import models.AllocationRequest
import repositories.{AllocationRequestRepository, EquipmentRepository, MaintenanceRepository}
import javax.inject.{Inject, Singleton}
import java.time.LocalDate
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AllocationRequestService @Inject()(
                                          allocationRequestRepository: AllocationRequestRepository,
                                          equipmentRepository: EquipmentRepository,
                                          maintenanceService: MaintenanceService // Injected to create maintenance records
                                        )(implicit ec: ExecutionContext) {

  // Fetch all allocation requests
  def listAllocations(): Future[Seq[AllocationRequest]] = allocationRequestRepository.list()

  // Create a new allocation request
  def createAllocation(request: AllocationRequest): Future[Option[Long]] = {
    allocationRequestRepository.isEquipmentAvailable(request.equipmentId).flatMap {
      case true => allocationRequestRepository.create(request).map(Some(_))
      case false => Future.successful(None)
    }
  }

  // Return an item and add maintenance if needed
  def returnItem(id: Long, returnCondition: String): Future[Boolean] = {
    val returnDate = LocalDate.now

    // Update allocation request details
    allocationRequestRepository.updateReturnDetails(id, returnDate, returnCondition).flatMap { updated =>
      if (updated) {
        // If the return condition is not "good", mark equipment as "maintenance" and create a maintenance record
        if (returnCondition != "good") {
          for {
            _ <- equipmentRepository.updateConditionStatus(id, "maintenance")
            _ <- maintenanceService.createMaintenance(id, s"Returned with condition: $returnCondition")
          } yield true
        } else {
          Future.successful(true)
        }
      } else {
        Future.successful(false)
      }
    }
  }
}

