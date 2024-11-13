package controllers

import models.Equipment
import play.api.mvc._
import services.EquipmentService
import play.api.libs.json._
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class EquipmentController @Inject()(
                                     cc: ControllerComponents,
                                     equipmentService: EquipmentService
                                   )(implicit ec: ExecutionContext) extends AbstractController(cc) {

  def list(): Action[AnyContent] = Action.async {
    equipmentService.list().map(equipment => Ok(Json.toJson(equipment)))
  }

  def get(id: Long): Action[AnyContent] = Action.async {
    equipmentService.get(id).map {
      case Some(equipment) => Ok(Json.toJson(equipment))
      case None => NotFound(Json.obj("message" -> s"Equipment with id $id not found"))
    }
  }

  def create(): Action[JsValue] = Action.async(parse.json) { request =>
    request.body.validate[Equipment] match {
      case JsSuccess(equipment, _) =>
        equipmentService.create(equipment).map(created =>
          Created(Json.toJson(created)))
      case JsError(errors) =>
        Future.successful(BadRequest(Json.obj(
          "message" -> "Invalid equipment data",
          "errors" -> JsError.toJson(errors))))
    }
  }

  def update(id: Long): Action[JsValue] = Action.async(parse.json) { request =>
    request.body.validate[Equipment] match {
      case JsSuccess(equipment, _) =>
        equipmentService.update(id, equipment).map {
          case Some(updated) => Ok(Json.toJson(updated))
          case None => NotFound(Json.obj("message" -> s"Equipment with id $id not found"))
        }
      case JsError(errors) =>
        Future.successful(BadRequest(Json.obj(
          "message" -> "Invalid equipment data",
          "errors" -> JsError.toJson(errors))))
    }
  }

  def delete(id: Long): Action[AnyContent] = Action.async {
    equipmentService.delete(id).map {
      case true => NoContent
      case false => NotFound(Json.obj("message" -> s"Equipment with id $id not found"))
    }
  }
}
