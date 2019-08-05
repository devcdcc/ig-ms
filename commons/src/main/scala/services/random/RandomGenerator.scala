package services.random

import java.security.SecureRandom
import java.util.Base64;

trait RandomGenerator {

  def generate(): String

}

class RandomGeneratorImpl extends RandomGenerator {
  val random: SecureRandom    = new SecureRandom()
  val encoder: Base64.Encoder = Base64.getUrlEncoder.withoutPadding()

  def generate(): String = {
    val buffer = new Array[Byte](20)
    random.nextBytes(buffer)
    encoder.encodeToString(buffer)
  }
}
