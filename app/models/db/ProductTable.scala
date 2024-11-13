package models.db

import models.Product
import slick.jdbc.MySQLProfile.api._

class ProductTable(tag: Tag) extends Table[Product](tag, "products") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name")
  def description = column[String]("description")
  def categoryType = column[String]("categorytype")

  def * = (id, name, description, categoryType) <> ((Product.apply _).tupled, Product.unapply)
}
