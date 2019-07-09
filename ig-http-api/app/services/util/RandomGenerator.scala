package services.util
import java.security.SecureRandom
import java.util.{Base64, Calendar};

object RandomGenerator {
  val random: SecureRandom    = new SecureRandom()
  val encoder: Base64.Encoder = Base64.getUrlEncoder.withoutPadding()

  def generate(): String = {
    val buffer = new Array[Byte](20)
    random.nextBytes(buffer)
    encoder.encodeToString(buffer)
  }
}
