package controllers

import com.github.devcdcc.services.queue.{
  CirceToStringMessageValueConverter,
  Message,
  MessageValueConverter,
  Publisher,
  SimpleStringMessageValueConverter
}
import controllers.authentication.AccessTokenHelper
import controllers.helps.PublisherHelper
import io.circe.{Decoder, Encoder, Json, Printer}
import javax.inject._
import play.api.{Configuration, Logging}
import play.api.mvc._
import services.ig.wrapper.User

import io.circe.generic.auto._
import io.circe.syntax._
import org.slf4j.MDC
import scala.concurrent.{ExecutionContext, Future}
import services.util.RandomGenerator
import play.api.libs.circe._

@Singleton
class UserScrapperController @Inject()(
    val config: Configuration,
    randomService: RandomGenerator,
    cc: ControllerComponents,
    publisher: Publisher[String, String])
    extends AbstractController(cc)
    with AccessTokenHelper
    with PublisherHelper
    with Logging
    with Circe {
  implicit private lazy val executionContext: ExecutionContext = defaultExecutionContext
//  implicit val printer: Printer                                = Printer.noSpaces.copy(dropNullValues = true)
  implicit val simpleStringMessageValueConverter: MessageValueConverter[Json, String] =
    new CirceToStringMessageValueConverter

  private def setMDCProgress(tx: Option[String] = None) = {
    MDC.clear()
    tx.foreach(tx => MDC.put("tx", tx))
  }

  private def addStatusAsText(json: Json, status: String, fieldName: String = "status") =
    (json deepMerge (fieldName, status).asJson).toString()

  def scrapUser(userId: String) = Action.async { implicit request =>
    authenticatedPrivateSiteIdAsync { authenticatedUser =>
      val user = User(userId = userId, id = Option(randomService.generate()))
      setMDCProgress(user.id)
      logger.info(addStatusAsText(user.asJson, "start"))
      publisher
        .sendAsync(
          Message(userScrapperTopic, user.asJson)
        )
        .map(message => {
          logger.info(addStatusAsText(user.asJson, "enqueued"))
          Accepted(message.value)
        })
        .recoverWith {
          case fail =>
            logger.info(addStatusAsText(user.asJson, "error"))
            logger.error("Error on scrapping user.", fail)
            Future.successful(InternalServerError(user.asJson))
        }
    }
  }

  def scrapMedia(userId: String) = Action { implicit request: Request[AnyContent] =>
    ???
  }

  def scrapFollowing(userId: String) = Action { implicit request: Request[AnyContent] =>
    ???
  }

  def scrapFollowers(userId: String) = Action { implicit request: Request[AnyContent] =>
    ???
  }

}
