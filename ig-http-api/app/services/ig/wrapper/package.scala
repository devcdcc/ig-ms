package services.ig

package object wrapper {

  trait IGRequest {
    def userId: String
    def next_max_id: Option[String]
    def hasNext: Option[Boolean]
    def id: Option[String]
  }
  case class UserRequest(
      userId: String,
      recursive: Option[Boolean] = None,
      next_max_id: Option[String] = None,
      hasNext: Option[Boolean] = None,
      id: Option[String] = None)
      extends IGRequest

}
