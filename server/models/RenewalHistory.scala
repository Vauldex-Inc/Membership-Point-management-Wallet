package tech.vauldex.poin.models.domain

import java.util.UUID
import java.time.Instant

case class RenewalHistory(
    idMerchant: UUID,
    idPass: String,
    months: Int,
    expiredFrom: Instant,
    expiredTo: Instant,
    renewedAt: Instant = Instant.now)

object RenewalHistory {
  val tupled =(apply _).tupled
}