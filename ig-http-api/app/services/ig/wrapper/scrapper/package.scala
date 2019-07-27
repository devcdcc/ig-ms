package services.ig.wrapper

package object scrapper {

  trait IGRequest {
    def userId: String
    def next_max_id: Option[String]
    def hasNext: Option[Boolean]
    def requestId: Option[String]
  }
  case class UserRequest(
      userId: String,
      recursive: Option[Boolean] = None,
      next_max_id: Option[String] = None,
      hasNext: Option[Boolean] = None,
      requestId: Option[String] = None)
      extends IGRequest

}
