package repositories

import models.Product
import models.db.ProductTable
import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ProductRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  private val products = TableQuery[ProductTable]

  // Fetch all products
  def list(): Future[Seq[Product]] = db.run(products.result)

  // Add a new product
  def add(product: Product): Future[Long] = {
    val insertQuery = products returning products.map(_.id)
    db.run(insertQuery += product)
  }
}
