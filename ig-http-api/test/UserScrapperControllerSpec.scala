import com.github.devcdcc.services.queue.Publisher
import controllers.UserScrapperController
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.mvc.ControllerComponents
import play.api.test.Helpers

class UserScrapperControllerSpec extends PlaySpec with MockitoSugar {
  var controllerComponents = mock[ControllerComponents]
  var publisher            = mock[Publisher[String, String]]
  // subject
  val subject = new UserScrapperController(Helpers.stubControllerComponents(), publisher)
}
