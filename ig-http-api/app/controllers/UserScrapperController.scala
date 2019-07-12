package controllers

import com.github.devcdcc.services.queue.{CirceToStringMessageValueConverter, Message, MessageValueConverter, Publisher}
import controllers.authentication.AccessTokenHelper
import controllers.helps.PublisherHelper
import io.circe.Json
import javax.inject._
import play.api.{Configuration, Logging}
import play.api.mvc._
import services.ig.wrapper.UserRequest

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

  final private val APP_ID = config.get[String]("app.id")

  private def setMDCProgress(tx: Option[String] = None)(implicit request: Request[AnyContent]) = {
    MDC.clear()
    tx.foreach(tx => MDC.put("tx", tx))
    MDC.put("path", request.path)
    MDC.put("app.id", APP_ID)
  }

  private def addStatusAsText(json: Json, status: String, fieldName: String = "status") =
    (json deepMerge (fieldName, status).asJson).toString()

  def scrapUser(userId: String) = Action.async { implicit request =>
    val destinationTopic = userScrapperTopic
    authenticatedPrivateSiteIdAsync(_ => basicRequestMaker(userId, destinationTopic))
  }

  protected def basicRequestMaker(userId: String, destinationTopic: String)(implicit request: Request[AnyContent]) = {
    implicit val user = UserRequest(userId = userId, id = Option(randomService.generate()))
    setMDCProgress(user.id)
    logger.info(addStatusAsText(user.asJson, "start"))
    val future = publisher.sendAsync(Message(destinationTopic, user.asJson))
    futureToWebResponse(future)
  }

  protected def futureToWebResponse(response: Future[Message[String, Json, String]])(implicit user: UserRequest) =
    response
      .map(messageToWebResponse)
      .recoverWith(recoverToWebResponseWrapper)

  protected def recoverToWebResponseWrapper(implicit user: UserRequest): PartialFunction[Throwable, Future[Result]] = {
    case fail: Throwable => Future.successful(recoverToWebResponse(fail))
  }

  protected def recoverToWebResponse(fail: Throwable)(implicit user: UserRequest) = {
    logger.info(addStatusAsText(user.asJson, "error"))
    logger.error("Error on scrapping user.", fail)
    InternalServerError(user.asJson)
  }

  protected def messageToWebResponse(message: Message[String, Json, String]) = {
    logger.info(addStatusAsText(message.value, "enqueued"))
    Accepted(message.value)
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
