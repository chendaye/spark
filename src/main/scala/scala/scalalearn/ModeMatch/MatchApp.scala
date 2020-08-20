package scala.scalalearn.ModeMatch

import scala.util.Random

object MatchApp extends App {
  val arr = Array("Lengo", "long", "chen")
  val name = arr(Random.nextInt(arr.length))
  name match {
    case "Lengo" => println("Lengo")
    case "long" => println("long")
    case "chen" => println("chen")
    case _ => println("fuck off")
  }

  // 双重过滤
  def judge(name:String, score:String): Unit ={
    score match {
      case "A" => println("A")
      case "B" => println("B")
      case "C" => println("C")
      case _  if(name == "long") => println("great")
      case _ => println("bad")
    }
  }
  judge("li", "D")
  judge("long", "D")

  // 数组匹配
  def arr(array:Array[String]): Unit ={
    array match {
      case Array("lengo") => println("lengo") // 存在元素 lengo
      case Array(x, y) => println(x + y)  // 只有任意两个元素
      case Array(x, y, z) => println(x + y + z)  // 只有任意3个元素
      case Array("long", _*) => println("long and others")  // 第一个元素是long
      case _ => println("no body")
    }
  }

  arr(Array("long", "dd", "df", "ccc"))

  // List 匹配
  def list(name:List[String]): Unit ={
    name match {
      case "long" :: Nil => println("long")
      case x :: y :: Nil => println(x + y)
      case "long":: tail => println("long and others")
      case _ => println("no body")
    }
  }

  list(List("lon88g", "77", "88"))

  // 类型匹配
  def typematch(obj:Any): Unit ={
    obj match{
      case x:Int => println("Int")
      case x:String => println("String")
      case x:Float => println("Float")
      case x:Map[_,_] => x.foreach(println)
      case _:Any => println("any")
    }
  }

  typematch(Array("lengo"))


  // case class
  class Person{}
  case class boss(name:String, age:Int) extends Person
  case class cto(name:String, age:Int) extends Person
  case class noobe(name:String) extends Person

  // 传一个顶层的类
  def caseClass(person:Person): Unit ={
    person match {
      case boss(name, age) => println("boos" + name + age)
      case cto(name, age) => println("cto" + name + age)
      case _ => println("noobe")
    }
  }

  caseClass(boss("lengo", 18))
  caseClass(cto("long", 18))

}
