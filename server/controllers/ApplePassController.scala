package tech.vauldex.poin.controllers

import javax.inject.{ Inject, Singleton }
import scala.concurrent.{ ExecutionContext, Future }
import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json._
import tech.vauldex.poin._
import models.domain.Device
import errors.PoinError._
import models.service.AppleWalletPassService

@Singleton 
class ApplePassController @Inject()(
  appleWalletPassService: AppleWalletPassService,
  implicit val ec: ExecutionContext,
  val controllerComponents: ControllerComponents
) extends BaseController with play.api.i18n.I18nSupport {

  val pushTokenform = Form(single("pushToken" -> nonEmptyText))

  val passesUpdatedSince = Form(single("passesUpdatedSince" -> optional(text)))

  def registerDevice(
    idDevice: String, 
    passTypeIdentifier: String, 
    serialNumber: String
  ) = Action.async { implicit request: Request[AnyContent] =>
    pushTokenform.bindFromRequest().fold(
      formWithErrors => Future.successful(BadRequest(formWithErrors.errorsAsJson)),
      token => 
        appleWalletPassService.addDevice(
          Device(idDevice, serialNumber, token), 
          passTypeIdentifier
        ).fold({ 
          case error: DeviceAlreadyExists  => Ok
          case _ => Unauthorized
        }, _ => Created)
      )
  }

  def unRegisterDevice(
    idDevice: String, 
    passTypeIdentifier: String, 
    serialNumber: String
  ) = Action.async { implicit request: Request[AnyContent] =>
      appleWalletPassService.removeDevice(
        idDevice, 
        serialNumber, 
        passTypeIdentifier, 
        request.headers.get("Authorization").map(_.split(" ")(1)).getOrElse("")
      ).fold({
          case error: DeviceNotExists  => Ok
          case _ => Unauthorized
      }, _ => Ok)
  }

  def appleLogger = Action.async { implicit request: Request[AnyContent] =>
    println("APPLE LOGGER", request.body)
    Future.successful(Ok)
  }

  def fetchPassUpdates(
    passTypeIdentifier: String, 
    serialNumber: String
  ) = Action.async { implicit request: Request[AnyContent] =>
      appleWalletPassService.fetchPassUpdates(passTypeIdentifier, serialNumber)
        .fold(
          _ => Unauthorized,
          {
            case (pass, lastUpdatedAt) => 
              Ok.sendFile(
                content = pass, 
                fileName = _ => Some(s"${serialNumber}.pkpass")
              ).withHeaders(("last-modified", lastUpdatedAt))
          }
        )
  }

  def listOfUpdatablePass(
    idDevice: String, 
    passTypeIdentifier: String
  ) = Action.async { implicit request: Request[AnyContent] =>
    passesUpdatedSince.bindFromRequest().fold(
      formWithErrors => Future.successful(BadRequest(formWithErrors.errorsAsJson)),
      passesUpdatedSince => {
        appleWalletPassService.listOfUpdatablePass(idDevice, passTypeIdentifier)
          .fold(
            _ => Unauthorized, 
            {
              case (serialNumbers, updatedSince) => 
                Ok(
                  Json.obj(
                    "serialNumbers" -> serialNumbers,
                    "lastUpdated" -> updatedSince
                  )
                )
            }
          )
      })
  }
}
