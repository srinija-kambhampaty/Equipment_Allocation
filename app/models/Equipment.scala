package models

import play.api.libs.json._

case class Equipment(
                      id: Long,
                      productId: Long,
                      conditionStatus: String, // "maintenance", "good", "removed"
                      availableStatus: Boolean
                    )

object Equipment {
  implicit val equipmentFormat: Format[Equipment] = Json.format[Equipment]
}
