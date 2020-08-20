package scalalearn.scalastart

object ArrayApp extends App {
  // 新建数组方法 1
  var a = new Array[String](6)
  println(a.length)
  a(2) = "lengo"


  // 新建数组2  调用 object.Array.apply()
  var b = Array("hadoop", "spark", "scala")

  var c = Array(1, 2, 3, 4, 5, 6, 7, 8, 9)

  println(c.sum) // 求和
  println(c.min) // 最小值
  println(c.max) // 最大值
  println(c.mkString)
  println(c.mkString("*"))
  println(c.mkString("<", "*", ">"))


  println("~~~~~~~~~~~~~可变数组～～～～～～～～～～～～")

  val d = scala.collection.mutable.ArrayBuffer[Int]()
  d += 1
  d += 2
  d += 3
  d += (4, 5, 6)
  d ++= Array(7, 8, 9)
  d.insert(2, 10, 11, 12) // 在指定位置插入若干元素
  println(d)

  d.remove(1)  // 删除指定位置的元素
  d.remove(1, 3)  // 删除指定位置的元素
  d.trimEnd(2)
  println(d)

  for(i <- 0 until(d.length)){
    println(d(i))
  }

  for(i <- (0  until d.length).reverse){
    println(d(i))
  }

  for(elem <- d){
    println(elem)
  }
}
