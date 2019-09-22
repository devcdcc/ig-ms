trait Config {
  def key: String
  def value: String
}

class Generic[T <: Config] {}

class Config1 extends Config {
  override def key: String   = "key1"
  override def value: String = "value1"
}

object Config1 {
  implicit class Implementations(value: Generic[Config1]) {
    def println(): Unit = Console.println(value.toString)
  }
}

class Config2 extends Config {
  override def key: String   = "key2"
  override def value: String = "value2"
}

object Main2 extends App {
  val c1: Generic[Config1] = null
  val c2: Generic[Config2] = null
  import Config1.Implementations
  c1.println()
}
