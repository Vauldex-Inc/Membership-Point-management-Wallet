package tech.vauldex.poin.models.domain

import java.util.UUID
import java.time.Instant

case class Device(
    id: String,
    idPass: String,
    pushToken: String,
    createdAt: Instant = Instant.now,
    updatedAt: Option[Instant] = None)

object Device {
  val tupled = (apply _).tupled
}