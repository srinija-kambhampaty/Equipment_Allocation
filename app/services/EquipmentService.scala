package services

import models.Equipment
import repositories.EquipmentRepository
import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

@Singleton
class EquipmentService @Inject() (equipmentRepository: EquipmentRepository) {

  def list(): Future[Seq[Equipment]] = equipmentRepository.list()

  def get(id: Long): Future[Option[Equipment]] = equipmentRepository.get(id)

  def create(equipment: Equipment): Future[Long] = equipmentRepository.create(equipment)

  def update(id: Long, equipment: Equipment): Future[Option[Equipment]] =
    equipmentRepository.update(id, equipment)

  def delete(id: Long): Future[Boolean] = equipmentRepository.delete(id)
}
