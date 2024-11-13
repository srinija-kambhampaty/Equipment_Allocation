package models

import java.time.LocalDate
import play.api.libs.json._

case class Maintenance(
                        id: Option[Long] = None, // Auto-incremented
                        equipmentId: Long,
                        startDate: LocalDate,
                        endDate: Option[LocalDate] = None,
                        description: String,
                        maintenanceStatus: String // "completed" or "inprogress"
                      )

object Maintenance {
  implicit val maintenanceFormat: Format[Maintenance] = Json.format[Maintenance]
}
