package scala.scalalearn.Function

object AdvanceApp extends App{
  val l = List(1, 2, 3, 4, 5, 6)

  // map 操作，每一个元素
  l.map((x:Int) => x + 1)
  println(l.map((x:Int) => x + 1)) // 每个元素加一
  println(l.map(_*2))  // 每个元素乘以2

  // foreach 遍历
  l.map(_*2).foreach(println)

  //filter 过滤
  l.map(_*2).filter(_ > 8).foreach(println)

  // take 取list前几个
  println(l.take(2))

  // reduce
  println(l.reduce(_+_)) // 相邻的两两相加
  println(l.reduce(_-_)) // 相邻的两两相减
  println(l.reduceLeft(_-_)) // 相邻的两两相减
  println(l.reduceRight(_+_)) // 相邻的两两相减

  // flod
  println(l.fold(0)(_-_))
  println(l.foldLeft(0)(_-_))
  println(l.foldRight(0)(_-_))

  // 最大 最小 求和
  l.min
  l.max
  l.sum

  // flatten
  var f = List(List(1, 2), List(1, 2), List(1, 2))
  println(f.flatten)

  // flatMap
  println(f.map(_.map(_*2)))
//  println(f.flatMap(_*2))


}
