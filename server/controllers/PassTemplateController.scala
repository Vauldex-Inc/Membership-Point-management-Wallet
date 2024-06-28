package tech.vauldex.poin.controllers

import java.util.UUID
import javax.inject.{ Inject, Singleton }
import scala.concurrent.{ ExecutionContext, Future }
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.libs.json._
import tech.vauldex.poin._
import models.service.PassTemplateService
import models.repo.PassTemplateRepo
import models.mapper.JsonSerializers._
import security.SecureMerchantAction
import models.utils.CustomForms._

@Singleton
class PassTemplateController @Inject()(
    passTemplateRepo: PassTemplateRepo,
    passTemplateService: PassTemplateService,
    secureMerchantAction: SecureMerchantAction,
    val controllerComponents: ControllerComponents,
    implicit val ec: ExecutionContext
  ) extends BaseController with play.api.i18n.I18nSupport {

  private val passTemplateForm = Form(
    tuple(
      "companyName" -> nonEmptyText,
      "programName" -> nonEmptyText,
      "barcodeType" -> nonEmptyText,
      "backgroundColor" -> nonEmptyText,
      "textColor" -> nonEmptyText,
      "passType" -> passTypeMapping,
      "expirySetting" -> expirySettingMapping
    )
  )

  private val updateTemplateForm = Form(
    tuple(
      "companyName" -> optional(nonEmptyText),
      "programName" -> optional(nonEmptyText),
      "backgroundColor" -> optional(nonEmptyText),
      "textColor" -> optional(nonEmptyText)
    )
  )

  private val markerForm = Form(
    tuple(
      "passType" -> passTypeMapping,
      "isActive" -> default(boolean, true),
      "marker" -> markerTuple
    )
  )

  def createPassTemplate = secureMerchantAction(parse.multipartFormData).async { implicit request =>
    passTemplateForm.bindFromRequest().fold(
      formWithErrors => Future.successful(BadRequest(formWithErrors.errorsAsJson)),
      {
        case(
          companyName,
          programName,
          barcodeType,
          backgroundColor,
          textColor,
          passType,
          expirySetting) => {
            passTemplateService
              .addPassTemplate(
                  request.merchant.id,
                  companyName,
                  programName,
                  barcodeType,
                  backgroundColor,
                  textColor,
                  passType,
                  expirySetting,
                  request.body.file("logo"),
                  request.body.file("banner")
            ).fold(
              error => Status(error.code)(error.reason.getOrElse("")),
              passTemplate => Created(Json.toJson(passTemplate))
            )
          }
      }
    )
  }

  def allMine = secureMerchantAction.async { implicit request =>
    passTemplateService.allMine(request.merchant.id).map(
      passTemplate => Ok(Json.toJson(passTemplate))
    )
  }

  def get(id: UUID) = secureMerchantAction.async { implicit request => 
    passTemplateService.get(id, request.merchant.id).fold (
      error => Status(error.code)(error.reason.getOrElse("")),
      passTemplate => Ok(Json.toJson(passTemplate))
    )
  }

  def update(id: UUID) = secureMerchantAction(parse.multipartFormData).async { implicit request =>
    updateTemplateForm.bindFromRequest().fold(
      formWithErrors => Future.successful(BadRequest(formWithErrors.errorsAsJson)),
      {
        case (companyName, programName, backgroundColor, textColor) =>
          passTemplateService.update(id)(
            request.body.file("logo"),
            request.body.file("banner"),
            companyName, 
            programName, 
            backgroundColor, 
            textColor)
          .fold(
            error => Status(error.code)(error.reason.getOrElse("")),
            _ => Ok)
      })
  }
      
  def getCustomers = secureMerchantAction.async { implicit request =>
    passTemplateService.getCustomers(request.merchant.id).map {
      customers => Ok(passes(customers))
    }
  }

  def getAllCustomers = secureMerchantAction.async { implicit request =>
    markerForm.bindFromRequest().fold(
      formWithErrors => Future.successful(BadRequest(formWithErrors.errorsAsJson)),
      {
        case (passType, isActive, marker) =>
        passTemplateRepo.allCustomersWithPass(request.merchant.id, passType, isActive, marker).map {
          page => Ok(page.copy(page.results.map(customersJson)).toJson)
        }
      }
    )
  }
}
