package services.random

import java.security.SecureRandom
import java.util.Base64

trait RandomGenerator {

  protected val defaultLength: Int
  def generate(length: Int): String
  def generate(): String = generate(defaultLength)

}

class RandomGeneratorImpl extends RandomGenerator {
  protected val defaultLength         = 24
  private val random: SecureRandom    = new SecureRandom()
  private val encoder: Base64.Encoder = Base64.getUrlEncoder.withoutPadding()

  def generate(length: Int): String = {
    val buffer = new Array[Byte](length)
    random.nextBytes(buffer)
    encoder.encodeToString(buffer)
  }
}
