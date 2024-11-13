package controllers

import models.User
import play.api.mvc._
import services.UserService
import play.api.libs.json._

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UserController @Inject()(
                                val cc: ControllerComponents,
                                userService: UserService
                              )(implicit ec: ExecutionContext) extends AbstractController(cc) {

  def list(): Action[AnyContent] = Action.async {
    userService.list().map(users => Ok(Json.toJson(users)))
  }

  def get(id: Long): Action[AnyContent] = Action.async {
    userService.get(id).map {
      case Some(user) => Ok(Json.toJson(user))
      case None => NotFound(Json.obj("message" -> s"User with id $id not found"))
    }
  }

  def create(): Action[JsValue] = Action.async(parse.json) { request =>
    request.body.validate[User] match {
      case JsSuccess(user, _) =>
        userService.create(user).map(created =>
          Created(Json.toJson(created)))
      case JsError(errors) =>
        Future.successful(BadRequest(Json.obj(
          "message" -> "Invalid user data",
          "errors" -> JsError.toJson(errors))))
    }
  }

  def update(id: Long): Action[JsValue] = Action.async(parse.json) { request =>
    request.body.validate[User] match {
      case JsSuccess(user, _) =>
        userService.update(id, user).map {
          case Some(updated) => Ok(Json.toJson(updated))
          case None => NotFound(Json.obj("message" -> s"User with id $id not found"))
        }
      case JsError(errors) =>
        Future.successful(BadRequest(Json.obj(
          "message" -> "Invalid user data",
          "errors" -> JsError.toJson(errors))))
    }
  }

  def delete(id: Long): Action[AnyContent] = Action.async {
    userService.delete(id).map {
      case true => NoContent
      case false => NotFound(Json.obj("message" -> s"User with id $id not found"))
    }
  }
}
