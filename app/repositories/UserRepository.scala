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

  // Fetch all active users
  def list(): Future[Seq[User]] = db.run(users.filter(_.active === true).result)

  // Get a user by ID if active
  def get(id: Long): Future[Option[User]] =
    db.run(users.filter(user => user.id === id && user.active === true).result.headOption)

  // Create a new user and return the auto-generated ID
  def create(user: User): Future[Long] = {
    val insertQueryThenReturnId = users returning users.map(_.id)
    val insertQueryThenReturnUser = users returning users.map(_.id) into ((user, id) => user.copy(id = Some(id)))

    db.run(insertQueryThenReturnId += user)
  }

  // Update a user by ID if active
  def update(id: Long, user: User): Future[Option[User]] = {
    val updateQuery = users.filter(user => user.id === id && user.active === true)
      .map(u => (u.name, u.department, u.role, u.active))
      .update((user.name, user.department, user.role, user.active))

    // Return updated user if successfully updated
    db.run(updateQuery).flatMap {
      case 0 => Future.successful(None) // No rows updated, return None
      case _ => get(id) // User updated, fetch the updated record
    }
  }

  // Deactivate a user by setting 'active' to false
  def delete(id: Long): Future[Boolean] = {
    db.run(users.filter(user => user.id === id && user.active === true)
      .map(u => u.active).update(false)).map(_ > 0)
  }
}
