package repositories

import models.User
import models.db.UserTable
import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UserRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  private val users = TableQuery[UserTable]

  // Fetch all users
  def list(): Future[Seq[User]] = db.run(users.result)

  // Get a user by ID
  def get(id: Long): Future[Option[User]] = db.run(users.filter(_.id === id).result.headOption)

  // Create a new user and return the auto-generated ID
  def create(user: User): Future[Long] = {
    val insertQueryThenReturnId = users returning users.map(_.id)
    db.run(insertQueryThenReturnId += user)
  }

  // Update a user by ID
  def update(id: Long, user: User): Future[Option[User]] = {
    val updateQuery = users.filter(_.id === id)
      .map(u => (u.name, u.email, u.role))
      .update((user.name, user.email, user.role))

    // Return the updated user if successfully updated
    db.run(updateQuery).flatMap {
      case 0 => Future.successful(None)
      case _ => get(id)
    }
  }

  // Delete a user by ID (this will permanently delete the user)
  def delete(id: Long): Future[Boolean] = {
    db.run(users.filter(_.id === id).delete).map(_ > 0)
  }
}
