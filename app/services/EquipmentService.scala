package services

import models.Equipment
import repositories.EquipmentRepository
import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

@Singleton
class EquipmentService @Inject()(equipmentRepository: EquipmentRepository) {

  // Fetch equipment by product ID
  def getEquipmentByProductId(productId: Long): Future[Seq[Equipment]] =
    equipmentRepository.findByProductId(productId)

  // Add new equipment
  def addEquipment(equipment: Equipment): Future[Long] = equipmentRepository.add(equipment)
}

