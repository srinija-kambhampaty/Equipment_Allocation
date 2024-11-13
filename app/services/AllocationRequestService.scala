package services

import models.AllocationRequest
import repositories.AllocationRequestRepository
import javax.inject.{Inject, Singleton}
import java.time.LocalDate
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AllocationRequestService @Inject()(allocationRequestRepository: AllocationRequestRepository)(implicit ec: ExecutionContext) {

  // Fetch all allocation requests
  def listAllocations(): Future[Seq[AllocationRequest]] = allocationRequestRepository.list()

  // Create a new allocation request with business logic
  def createAllocation(request: AllocationRequest): Future[Option[Long]] = {
    // Check if equipment is available before creating the request
    allocationRequestRepository.isEquipmentAvailable(request.equipmentId).flatMap {
      case true => allocationRequestRepository.create(request).map(Some(_))
      case false => Future.successful(None)
    }
  }

  // Return an item and update the fields
  def returnItem(id: Long, returnCondition: String): Future[Boolean] = {
    val returnDate = LocalDate.now
    allocationRequestRepository.updateReturnDetails(id, returnDate, returnCondition)
  }
}

