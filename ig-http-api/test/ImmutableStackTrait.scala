class ImmutableStack[A](iterable: A*) extends Seq[A] {
  override def iterator: Iterator[A] = iterable.toIterator

  override def length: Int = {
    var result = 0
    for (x <- this) result += 1
    result
  }

  override def apply(idx: Int): A = {
    var result = 0
    for (x <- this) {
      if (result == idx) return x
      result += 1
    }
    throw new IndexOutOfBoundsException(s"Current stack size: $length and you want to retrieve $idx element")
  }

  def push(element: A): ImmutableStack[A] = {
    val newValue = iterator ++ Seq(element)
    new ImmutableStack(newValue.toSeq: _*)
  }

  def pop: ImmutableStack[A] = new ImmutableStack[A](this.dropRight(1): _*)

  def minValue(implicit order: Ordering[A]): A =
    if (this.isEmpty) throw new Exception("Error, empty stack.")
    else {
      this.reduce(order.min)
    }
}

object MainTest1 extends App {
  val stack = new ImmutableStack(1, 2, 3, 4, 5)
//  println(stack)
//  println(stack.push(6))
//  println(stack.pop)
//  println(stack)
//  println(stack(3))
//  println(stack.minValue == 1)
//  println(stack.push(0).minValue == 0)

  def pow(value: BigDecimal, exp: Int) = {
    var out: BigDecimal = 1 //1
    //i = index 1
    //i incremento n
    //i comparación n +1
    for (i <- 1 to exp) {
      //multiplicación n
      //asignación n
      out *= value
    }
    println(s"complejidad: n = ${exp * 4} + 3")
    out
  }

  def polinomio(value: BigDecimal, n: Int) = (0 to n).reverse.map(pow(value, _)).sum

//  println(s"pow:${pow(3, 0)}")
//  println(s"polinomio ${polinomio(2, 3)}")
  trait ValidationTrait {
    def value: String
    def validate: String
  }
  case class OkayValidation(value: String) extends ValidationTrait {
    def validate: String = "Put here your format"
  }
  case class FailValidation(value: String) extends ValidationTrait {
    def validate: String = "error"
  }
  case class DefaultValidation(value: String) extends ValidationTrait {
    def validate: String = null
  }
  val validations: List[ValidationTrait] = List(OkayValidation("OK"), FailValidation("FAIL"))
  def resolve(value: String)             = validations.find(_.value == value).getOrElse(default = DefaultValidation(value))
  resolve("response").validate
}
