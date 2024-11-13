package models

import play.api.libs.json._
import play.api.libs.functional.syntax._

case class AllocationRequest(
                              id: Option[Long] = None,  // id will be auto-incremented
                              userId: Long,             // Foreign key to User
                              equipmentId: Long,        // Foreign key to Equipment
                              status: String,
                              purpose: String,
                              requestDate: String,      // Consider using a date format
                              expectedReturnDate: String,
                              returnStatus: Option[String] = None // Nullable, can be updated later
                            )

object AllocationRequest {
  implicit val allocationRequestFormat: Format[AllocationRequest] = Json.format[AllocationRequest]
}

