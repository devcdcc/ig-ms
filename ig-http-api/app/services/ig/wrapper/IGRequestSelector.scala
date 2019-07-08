package services.ig.wrapper

import com.google.inject.Inject
import play.api.libs.ws.{WSClient, WSResponse}

import scala.concurrent.Future

class IGRequestSelector @Inject()(ws: WSClient) {
  def makeCall(): Future[WSResponse] = ws.url("").execute()
}
