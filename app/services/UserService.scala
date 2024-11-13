package services

import models.User
import repositories.UserRepository
import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

@Singleton
class UserService @Inject() (userRepository: UserRepository) {

  // Fetch all users
  def list(): Future[Seq[User]] = userRepository.list()

  // Get a user by id
  def get(id: Long): Future[Option[User]] = userRepository.get(id)

  // Create a new user
  def create(user: User): Future[Long] = userRepository.create(user)

  // Update an existing user
  def update(id: Long, user: User): Future[Option[User]] =
    userRepository.update(id, user)

  // Delete a user by id
  def delete(id: Long): Future[Boolean] = userRepository.delete(id)
}
