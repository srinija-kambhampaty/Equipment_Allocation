package services

import models.Product
import repositories.ProductRepository
import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

@Singleton
class ProductService @Inject()(productRepository: ProductRepository) {

  // Fetch all products
  def listProducts(): Future[Seq[Product]] = productRepository.list()

  // Add a new product
  def addProduct(product: Product): Future[Long] = productRepository.add(product)
}
