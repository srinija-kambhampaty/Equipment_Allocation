package controllers

import play.api.mvc._
import services.MaintenanceService
import play.api.libs.json._

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class MaintenanceController @Inject()(
                                       cc: ControllerComponents,
                                       maintenanceService: MaintenanceService
                                     )(implicit ec: ExecutionContext) extends AbstractController(cc) {

  // Endpoint to get all maintenance records
  def listMaintenances: Action[AnyContent] = Action.async {
    maintenanceService.listMaintenances().map { maintenances =>
      Ok(Json.toJson(maintenances))
    }
  }
}

