package tech.vauldex.poin.controllers

import javax.inject.{ Inject, Singleton }
import scala.concurrent.{ ExecutionContext, Future }
import play.api.Configuration
import play.api.mvc._
import com.google.crypto.tink.apps.paymentmethodtoken._
import tech.vauldex.poin._
import models.service.{ PassTemplateService, PreferenceService }

@Singleton 
class GoogleWalletController @Inject()(
  passTemplateService: PassTemplateService,
  preferenceService: PreferenceService,
  val configuration: Configuration,
  implicit val ec: ExecutionContext,
  val controllerComponents: ControllerComponents
) extends BaseController with play.api.i18n.I18nSupport {

  private lazy val RECIPIENT_ID = preferenceService.predef.idIssuer
  private lazy val PUBLIC_KEY_URL = preferenceService.predef.googleWalletPublicKeyUrl
  private lazy val SENDER_ID = preferenceService.predef.idSender
  private lazy val PROTOCOL = preferenceService.predef.googleWalletProtocol
  private val keysManager: GooglePaymentsPublicKeysManager = 
    new GooglePaymentsPublicKeysManager.Builder()
      .setKeysUrl(PUBLIC_KEY_URL)
      .build()

  def verifyMessage = Action.async { implicit request =>
    val recipient =
      new PaymentMethodTokenRecipient.Builder()
        .protocolVersion(PROTOCOL)
        .fetchSenderVerifyingKeysWith(keysManager)
        .senderId(SENDER_ID)
        .recipientId(RECIPIENT_ID)
        .build()
    request.body.asJson
      .map { json =>
        passTemplateService
          .googleWalletAddCallback(recipient.unseal(json.toString))
          .fold(error => Status(error.code)(error.reason.getOrElse("")), _ => Ok)
      }
      .getOrElse {
        Future.successful(BadRequest)
      }
  }
}
