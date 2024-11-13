package controllers

import models.AllocationRequest
import play.api.mvc._
import services.AllocationRequestService
import play.api.libs.json._
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AllocationRequestController @Inject()(
                                             cc: ControllerComponents,
                                             allocationRequestService: AllocationRequestService
                                           )(implicit ec: ExecutionContext) extends AbstractController(cc) {

  def list(): Action[AnyContent] = Action.async {
    allocationRequestService.list().map(requests => Ok(Json.toJson(requests)))
  }

  def get(id: Long): Action[AnyContent] = Action.async {
    allocationRequestService.get(id).map {
      case Some(request) => Ok(Json.toJson(request))
      case None => NotFound(Json.obj("message" -> s"Allocation request with id $id not found"))
    }
  }

  def create(): Action[JsValue] = Action.async(parse.json) { request =>
    request.body.validate[AllocationRequest] match {
      case JsSuccess(allocationRequest, _) =>
        allocationRequestService.create(allocationRequest).map {
          case Right(createdId) => Created(Json.obj("id" -> createdId))
          case Left(errorMessage) => BadRequest(Json.obj("message" -> errorMessage))
        }
      case JsError(errors) =>
        Future.successful(BadRequest(Json.obj(
          "message" -> "Invalid allocation request data",
          "errors" -> JsError.toJson(errors))))
    }
  }

  def update(id: Long): Action[JsValue] = Action.async(parse.json) { request =>
    request.body.validate[AllocationRequest] match {
      case JsSuccess(allocationRequest, _) =>
        allocationRequestService.update(id, allocationRequest).map {
          case Some(updated) => Ok(Json.toJson(updated))
          case None => NotFound(Json.obj("message" -> s"Allocation request with id $id not found"))
        }
      case JsError(errors) =>
        Future.successful(BadRequest(Json.obj(
          "message" -> "Invalid allocation request data",
          "errors" -> JsError.toJson(errors))))
    }
  }

  def delete(id: Long): Action[AnyContent] = Action.async {
    allocationRequestService.delete(id).map {
      case true => NoContent
      case false => NotFound(Json.obj("message" -> s"Allocation request with id $id not found"))
    }
  }
}
