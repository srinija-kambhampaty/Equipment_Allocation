package models

import play.api.libs.json._
import play.api.libs.functional.syntax._

case class Equipment(
                      id: Option[Long] = None,  // id will be auto-incremented
                      name: String,
                      category: String,
                      description: String,
                      conditionStatus: String,
                      availabilityStatus: Boolean = true
                    )

object Equipment {
  implicit val equipmentFormat: Format[Equipment] = Json.format[Equipment]
}
