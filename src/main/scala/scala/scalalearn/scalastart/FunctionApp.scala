package scala.scalalearn.scalastart

import org.apache.spark.{SparkConf, SparkContext}
object FunctionApp {
  def main(args: Array[String]): Unit = {
//    println(add(6, 6))
//    println(three(1, 2))
//    println(four)
      sayName("chendaye")
//      val conf = new SparkConf().setAppName("taobao").setMaster("local[2]")
//      val sc = new SparkContext(conf)

    // 可以打乱参数的时序
    val a = distanceTime(100, 10)
    val b:Float = distanceTime(time = 10, distance = 100)
    println(a)
    println(b)
    println(manyArg(1, 2, 3, 4, 5, 6, 7, 8, 9 ))

  }

  def add(x:Int, y:Int): Int ={
    x + y
  }

  def three(x:Int, y:Int) = x + y

  // 没有入参的函数可以直接写方法名调用,括号是可以省略
  def  four() = 2 + 2

  // 默认参数
  def sayName(name:String = "PK") = {
    println(name)
  }

  def loadConf(conf:String = "spark.default.conf") = {
     println(conf)
  }

  // 命名参数
  def distanceTime(distance:Float, time:Float):Float = {
    distance / time
  }

  // 可变参数：参数个数不定
  def manyArg(num: Int*) = {
    var sum  = 0
    for(elem <- num){
      sum += elem
    }
    sum
  }

}
