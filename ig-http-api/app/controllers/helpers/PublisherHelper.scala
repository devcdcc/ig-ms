package controllers.helpers

import com.github.devcdcc.services.queue.{CirceToStringMessageValueConverter, Message, MessageValueConverter, Publisher}
import com.google.inject.Inject
import io.circe.Json
import io.circe.generic.auto._
import io.circe.syntax._
import org.slf4j.MDC
import play.api.{Configuration, Logging}
import play.api.libs.circe.Circe
import play.api.mvc.{AbstractController, AnyContent, ControllerComponents, Request, Result}
import com.github.devcdcc.domain.UserRequest
import services.random.RandomGenerator

import scala.concurrent.{ExecutionContext, Future}

class PublisherHelper @Inject()(
    val config: Configuration,
    cc: ControllerComponents,
    randomService: RandomGenerator,
    publisher: Publisher[String, String])
    extends AbstractController(cc)
    with Logging
    with Circe {

  implicit protected lazy val executionContext: ExecutionContext = defaultExecutionContext

  final protected val APP_ID = config.get[String]("app.id")
  implicit val simpleStringMessageValueConverter: MessageValueConverter[Json, String] =
    new CirceToStringMessageValueConverter

  private def addStatusAsText(json: Json, status: String, fieldName: String = "status") =
    (json deepMerge (fieldName, status).asJson).toString()

  protected def basicRequestMaker(
      userId: String,
      destinationTopic: String
    )(implicit request: Request[AnyContent]
    ): Future[Result] = {
    implicit val user: UserRequest = UserRequest(userId = userId, requestId = Option(randomService.generate()))
    setMDCProgress(user.requestId)
    logger.info(addStatusAsText(user.asJson, "start"))
    val future = publisher.sendAsync(Message(destinationTopic, user.asJson))
    futureToWebResponse(future)
  }

  private def setMDCProgress(tx: Option[String] = None)(implicit request: Request[AnyContent]): Unit = {
    MDC.clear()
    tx.foreach(tx => MDC.put("tx", tx))
    MDC.put("path", request.path)
    MDC.put("app.id", APP_ID)
  }

  protected def futureToWebResponse(
      response: Future[Message[String, Json, String]]
    )(implicit user: UserRequest,
      executionContext: ExecutionContext
    ): Future[Result] =
    response
      .map(messageToWebResponse)
      .recoverWith(recoverToWebResponseWrapper)

  protected def recoverToWebResponseWrapper(implicit user: UserRequest): PartialFunction[Throwable, Future[Result]] = {
    case fail: Throwable => Future.successful(recoverToWebResponse(fail))
  }

  protected def recoverToWebResponse(fail: Throwable)(implicit user: UserRequest): Result = {
    logger.info(addStatusAsText(user.asJson, "error"))
    logger.error("Error on scrapping user.", fail)
    InternalServerError(user.asJson)
  }

  protected def messageToWebResponse(message: Message[String, Json, String]): Result = {
    logger.info(addStatusAsText(message.value, "enqueued"))
    Accepted(message.value)
  }
}
