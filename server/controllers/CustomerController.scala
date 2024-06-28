package tech.vauldex.poin.controllers

import javax.inject.{ Inject, Singleton }
import java.util.UUID
import scala.concurrent.{ ExecutionContext, Future }
import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._
import tech.vauldex.poin._
import models.utils.CustomForms._
import models.service.CustomerService
import models.domain.Customer
import models.mapper.JsonSerializers._
import security.SecureCustomerAction
import errors.PoinError._

@Singleton
class CustomerController @Inject()(
  customerService: CustomerService,
  secureCustomerAction: SecureCustomerAction,
  implicit val ec: ExecutionContext,
  val controllerComponents: ControllerComponents
) extends BaseController with play.api.i18n.I18nSupport {

  private val customerForm = Form(
    tuple(
      "firstName" -> nonEmptyText,
      "lastName" -> nonEmptyText,
      "email" -> emailText,
      "mobileNo" -> nonEmptyDigit(minLength = 11, maxLength = 11)
    )
  )

  def add(
    idPassTemplate: UUID,
    period: Option[String] = None
  ) = Action.async { implicit request: Request[AnyContent] =>
    customerForm.bindFromRequest().fold(
      formWithErrors => Future.successful(BadRequest(formWithErrors.errorsAsJson)),
      {
        case (firstName, lastName, email, mobileNo) => {
        customerService.addCustomer(idPassTemplate)(
            Customer(
              firstName,
              lastName,
              email,
              mobileNo),
            period.map(_.splitAt(10)._2)
          )
          .fold(
            error => Status(error.code)(error.reason.getOrElse("")), 
            {
              case (idCustomer, links) => 
                Created(
                  addToWalletLinks(idCustomer, links)
                ).addingToSession(secureCustomerAction.key -> idCustomer.toString)
            }
          )
        }
      }
    )
  }
 
  def getPassLinks(idPassTemplate: UUID) = secureCustomerAction.async { implicit request =>
    customerService.getPassLink(idPassTemplate,request.customer.id)
      .fold(
        error => Status(error.code)(error.reason.getOrElse("")), 
        links => Ok(walletLinks(links))
      )
  }

  def getPass(id: UUID, idPassTemplate: UUID) = Action.async { implicit request: Request[AnyContent] =>
    customerService.getPass(id, idPassTemplate).map {
      customerPasses => Ok(passes(customerPasses))
    }
  }
}
