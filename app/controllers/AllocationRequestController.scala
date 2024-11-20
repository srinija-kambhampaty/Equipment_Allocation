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

  // Endpoint to get all allocation requests
  def getAllocations: Action[AnyContent] = Action.async {
    allocationRequestService.listAllocations().map { allocations =>
      Ok(Json.toJson(allocations))
    }
  }

  // Endpoint to create a new allocation request
  def createAllocation(equipmentId: Long): Action[JsValue] = Action.async(parse.json) { request =>
    request.body.validate[AllocationRequest] match {
      case JsSuccess(allocationRequest, _) if allocationRequest.equipmentId == equipmentId =>
        allocationRequestService.createAllocation(allocationRequest).map {
          case Some(id) => Created(Json.obj("message" -> "Allocation request created successfully", "id" -> id))
          case None => BadRequest(Json.obj("message" -> "Equipment is not available for allocation"))
        }
      case JsSuccess(_, _) =>
        Future.successful(BadRequest(Json.obj("message" -> "Equipment ID mismatch")))
      case JsError(errors) =>
        Future.successful(BadRequest(Json.obj("message" -> "Invalid allocation request data", "errors" -> JsError.toJson(errors))))
    }
  }

  // Endpoint to return an item and update fields
  def returnItem(id: Long): Action[JsValue] = Action.async(parse.json) { request =>
    (request.body \ "returnCondition").asOpt[String] match {
      case Some(returnCondition) =>
        allocationRequestService.returnItem(id, returnCondition).map {
          case true => Ok(Json.obj("message" -> "Item returned successfully"))
          case false => NotFound(Json.obj("message" -> s"Allocation request with id $id not found"))
        }
      case None =>
        Future.successful(BadRequest(Json.obj("message" -> "Return condition is required")))
    }
  }
}

