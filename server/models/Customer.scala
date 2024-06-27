package tech.vauldex.poin.models.domain

import java.util.UUID
import java.time.Instant

case class Customer(
    id: UUID,
    firstName: String,
    lastName: String,
    email: String,
    mobileNo: String,
    createdAt: Instant)

object Customer {
  val tupled = (apply _).tupled

  def apply(
    firstName: String,
    lastName: String,
    email: String,
    mobileNo: String): Customer =
    Customer(UUID.randomUUID(), firstName, lastName, email, mobileNo, Instant.now)
}