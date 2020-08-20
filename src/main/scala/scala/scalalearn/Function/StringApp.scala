package scala.scalalearn.Function

object StringApp extends App {
  // 拼接字符串
  val s:String = "string app"
  val l:String = "lengo"
  println(s+l)
  println(s"$s----$l")

  // 多行字符串 连续按两次双引号
  val b =
    """
      |lengo
      |is
      |very
      |rich
      |and
      |very
      |hadsome
      |""".stripMargin
  println("Exception for ARG" + b)
}
