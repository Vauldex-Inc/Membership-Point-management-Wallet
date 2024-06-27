package tech.vauldex.poin.models.domain

import java.util.UUID
import java.time.Instant
import tech.vauldex.poin.models.utils._

case class PassTemplate(
  id: UUID,
  idMerchant: UUID,
  logoUrl: String,
  bannerUrl: String,
  companyName: String,
  programName: String,
  barcodeType: String,
  backgroundColor: String,
  textColor: String,
  passType: PassTypes.Value,
  expirySetting: Expiry.Value,
  createdAt: Instant,
  updatedAt: Option[Instant] = None)

object PassTemplate {
  val tupled = (apply: (
    UUID,
    UUID,
    String,
    String,
    String,
    String,
    String,
    String,
    String,
    PassTypes.Value,
    Expiry.Value,
    Instant,
    Option[Instant]) => PassTemplate).tupled
  
  def apply (
    id: UUID,
    idMerchant: UUID,
    logoUrl: String,
    bannerUrl: String,
    companyName: String,
    programName: String,
    barcodeType: String,
    backgroundColor: String,
    textColor: String,
    passType: PassTypes.Value,
    expirySetting: Expiry.Value): PassTemplate =
    PassTemplate(
      id,
      idMerchant, 
      logoUrl, 
      bannerUrl, 
      companyName, 
      programName,
      barcodeType, 
      backgroundColor,
      textColor, 
      passType,
      expirySetting,
      Instant.now,
      None)
}