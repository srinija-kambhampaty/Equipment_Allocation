package services

import java.time.LocalDate
import javax.inject.{Inject, Singleton}
import models.AllocationRequest
import repositories.{AllocationRequestRepository, EquipmentRepository, MaintenanceRepository}
import kafka.{MaintenanceAlertProducer, InventoryAlertProducer,OverdueReminderProducer} // Import both producers
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AllocationRequestService @Inject()(
                                          allocationRequestRepository: AllocationRequestRepository,
                                          equipmentRepository: EquipmentRepository,
                                          maintenanceService: MaintenanceService,
                                          maintenanceAlertProducer: MaintenanceAlertProducer, // Inject MaintenanceAlertProducer
                                          inventoryAlertProducer: InventoryAlertProducer, // Inject InventoryAlertProducer
                                          overdueReminderProducer: OverdueReminderProducer
                                        )(implicit ec: ExecutionContext) {

  // Fetch all allocation requests
  def listAllocations(): Future[Seq[AllocationRequest]] = allocationRequestRepository.list()

  // Create a new allocation request and send inventory alert
  def createAllocation(request: AllocationRequest): Future[Option[Long]] = {
    allocationRequestRepository.isEquipmentAvailable(request.equipmentId).flatMap {
      case true =>
        allocationRequestRepository.create(request).map { id =>
          // Fetch the equipment details and send inventory alert
          equipmentRepository.getEquipmentById(request.equipmentId).foreach {
            case Some(equipment) => inventoryAlertProducer.sendInventoryAlert(equipment)
            case None => // Handle the case if equipment is not found
          }
          Some(id)
        }
      case false => Future.successful(None)
    }
  }

  // Return an item and handle maintenance and inventory alerts if needed
  def returnItem(allocationId: Long, returnCondition: String): Future[Boolean] = {
    val returnDate = LocalDate.now

    // Update allocation request details
    allocationRequestRepository.updateReturnDetails(allocationId, returnDate, returnCondition).flatMap { updated =>
      if (updated) {
        // Fetch the allocation request to get the correct equipmentId
        allocationRequestRepository.getAllocationById(allocationId).flatMap {
          case Some(allocationRequest) =>
            val equipmentId = allocationRequest.equipmentId

            // Fetch equipment details and send inventory alert
            equipmentRepository.getEquipmentById(equipmentId).foreach {
              case Some(equipment) => inventoryAlertProducer.sendInventoryAlert(equipment)
              case None => // Handle the case if equipment is not found
            }

            if (returnCondition != "good") {
              // Mark equipment as "maintenance" and create a maintenance record
              for {
                _ <- equipmentRepository.updateConditionStatus(equipmentId, "maintenance")
                _ <- maintenanceService.createMaintenance(equipmentId, s"Returned with condition: $returnCondition")
                equipment <- equipmentRepository.getEquipmentById(equipmentId) // Fetch equipment details
              } yield {
                // Send maintenance alert to Kafka
                equipment.foreach(maintenanceAlertProducer.sendMaintenanceAlert)
                true
              }
            } else {
              Future.successful(true)
            }
          case None =>
            Future.failed(new Exception("Allocation request not found"))
        }
      } else {
        Future.successful(false)
      }
    }
  }

  // Method to check for overdue equipment and send reminders
  def checkOverdueAllocations(): Future[Unit] = {
    val currentDate = LocalDate.now

    allocationRequestRepository.list().map { allocationRequests =>
      allocationRequests.foreach { allocationRequest =>
        if (allocationRequest.expectedReturnDate.isBefore(currentDate) && allocationRequest.returnStatus != "returned") {
          // Send overdue reminder to Kafka
          overdueReminderProducer.sendOverdueReminder(allocationRequest)
        }
      }
    }
  }


}
