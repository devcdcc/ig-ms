package services.ig.wrapper

import org.scalatest.mockito.MockitoSugar
import org.scalatest._
import org.scalatestplus.play.PlaySpec
import play.api.libs.ws.WSClient

class IGRequestSelectorSpec extends PlaySpec with MockitoSugar {
  var ws: WSClient = mock[WSClient]
  var subject      = new IGRequestSelector(ws)

  it "return fa"
}
