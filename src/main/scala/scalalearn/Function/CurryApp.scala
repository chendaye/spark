package scalalearn.Function

/**
 * curry 函数
 * */
object CurryApp extends App {
  // 一般形式
  def sum1(x:Int, y:Int) = {
    x+y
  }
  println(sum1(1, 2))

  // curry 函数, 把多个参数放在多个括号里面
  def sum2(a:Int)(b:Int) = a+b

  println(sum2(1)(2))
}
