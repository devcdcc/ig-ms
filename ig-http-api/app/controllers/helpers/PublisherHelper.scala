package controllers.helpers

import com.github.devcdcc.domain.QueueRequest
import com.github.devcdcc.helpers.TopicsHelper
import com.github.devcdcc.services.queue.{CirceToStringMessageValueConverter, Message, MessageValueConverter, Publisher}
import com.google.inject.Inject
import io.circe.Json
import io.circe.generic.auto._
import io.circe.syntax._
import org.slf4j.MDC
import play.api.libs.circe.Circe
import play.api.mvc.{AbstractController, ControllerComponents, Result}
import play.api.{Configuration, Logging}
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

  protected def basicRequestMaker(userId: String, path: String)(implicit queueRequest: QueueRequest): Future[Result] = {
    setMDCProgress(tx = queueRequest.requestId, path = path)
    logger.info(addStatusAsText(queueRequest.asJson, "start"))
    val future = publisher.sendAsync(Message(TopicsHelper.appenderTopic, queueRequest.asJson))
    futureToWebResponse(future)
  }

  private def setMDCProgress(tx: Option[String] = None, path: String): Unit = {
    MDC.clear()
    tx.foreach(tx => MDC.put("tx", tx))
    MDC.put("path", path)
    MDC.put("app.id", APP_ID)
  }

  protected def futureToWebResponse(
      response: Future[Message[String, Json, String]]
    )(implicit user: QueueRequest,
      executionContext: ExecutionContext
    ): Future[Result] =
    response
      .map(messageToWebResponse)
      .recoverWith(recoverToWebResponseWrapper)

  protected def recoverToWebResponseWrapper(implicit user: QueueRequest): PartialFunction[Throwable, Future[Result]] = {
    case fail: Throwable => Future.successful(recoverToWebResponse(fail))
  }

  protected def recoverToWebResponse(fail: Throwable)(implicit queueRequest: QueueRequest): Result = {
    logger.info(addStatusAsText(queueRequest.asJson, "error"))
    logger.error("Error on scrapping user.", fail)
    InternalServerError(queueRequest.asJson)
  }

  protected def messageToWebResponse(message: Message[String, Json, String]): Result = {
    logger.info(addStatusAsText(message.value, "enqueued"))
    Accepted(message.value)
  }
}
