package controllers

import models.{Equipment, Product}
import play.api.mvc._
import services.{EquipmentService, ProductService}
import play.api.libs.json._

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ProductController @Inject()(
                                   cc: ControllerComponents,
                                   productService: ProductService,
                                   equipmentService: EquipmentService
                                 )(implicit ec: ExecutionContext) extends AbstractController(cc) {

  // Endpoint to get all products
  def getProducts: Action[AnyContent] = Action.async {
    productService.listProducts().map(products => Ok(Json.toJson(products)))
  }

  // Endpoint to get all equipment by product ID
  def getEquipmentByProductId(productId: Long): Action[AnyContent] = Action.async {
    equipmentService.getEquipmentByProductId(productId).map(equipment => Ok(Json.toJson(equipment)))
  }

  // Endpoint to add a new product
  def addProduct(): Action[JsValue] = Action.async(parse.json) { request =>
    request.body.validate[Product] match {
      case JsSuccess(product, _) =>
        productService.addProduct(product).map { productId =>
          Created(Json.obj("message" -> "Product added successfully", "id" -> productId))
        }
      case JsError(errors) =>
        Future.successful(BadRequest(Json.obj("message" -> "Invalid product data", "errors" -> JsError.toJson(errors))))
    }
  }

  // Endpoint to add equipment by product ID
  def addEquipmentByProductId(id: Long): Action[JsValue] = Action.async(parse.json) { request =>
    request.body.validate[Equipment] match {
      case JsSuccess(equipment, _) if equipment.productId == id =>
        equipmentService.addEquipment(equipment).map { equipmentId =>
          Created(Json.obj("message" -> "Equipment added successfully", "id" -> equipmentId))
        }
      case JsSuccess(_, _) =>
        Future.successful(BadRequest(Json.obj("message" -> "Product ID mismatch")))
      case JsError(errors) =>
        Future.successful(BadRequest(Json.obj("message" -> "Invalid equipment data", "errors" -> JsError.toJson(errors))))
    }
  }

}
