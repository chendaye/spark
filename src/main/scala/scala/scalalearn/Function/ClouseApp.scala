package scala.scalalearn.Function

/**
 * 匿名函数
 * */
object ClouseApp extends App{
  def say(word:String): Unit ={
    println(word)
  }
  say("say hello word")

  // 匿名函数 (参数) => 函数体
  val fun = (x:Int) => {
    x+1
    x+2
  }

  println(fun(2))

}
