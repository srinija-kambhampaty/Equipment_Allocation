package services

import models.AllocationRequest
import repositories.{AllocationRequestRepository, EquipmentRepository, UserRepository}
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AllocationRequestService @Inject() (
                                           allocationRequestRepository: AllocationRequestRepository,
                                           equipmentRepository: EquipmentRepository,
                                           userRepository: UserRepository
                                         )(implicit ec: ExecutionContext) {

  def list(): Future[Seq[AllocationRequest]] = allocationRequestRepository.list()

  def get(id: Long): Future[Option[AllocationRequest]] = allocationRequestRepository.get(id)

  def create(allocationRequest: AllocationRequest): Future[Either[String, Long]] = {
    for {
      userExists <- userRepository.get(allocationRequest.userId)
      equipmentExists <- equipmentRepository.get(allocationRequest.equipmentId)
      result <- (userExists, equipmentExists) match {
        case (Some(_), Some(equipment)) if equipment.availabilityStatus =>
          allocationRequestRepository.create(allocationRequest).map(Right(_))
        case (Some(_), Some(_)) =>
          Future.successful(Left("The requested equipment is not available."))
        case (None, _) => Future.successful(Left("The specified user does not exist."))
        case (_, None) => Future.successful(Left("The specified equipment does not exist."))
      }
    } yield result
  }

  def update(id: Long, allocationRequest: AllocationRequest): Future[Option[AllocationRequest]] = {
    allocationRequestRepository.get(id).flatMap {
      case Some(_) => allocationRequestRepository.update(id, allocationRequest)
      case None => Future.successful(None)
    }
  }

  def delete(id: Long): Future[Boolean] = allocationRequestRepository.delete(id)
}
