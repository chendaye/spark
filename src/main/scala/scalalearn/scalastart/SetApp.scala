package scalalearn.scalastart

import scala.collection.mutable

object SetApp {
  def main(args: Array[String]): Unit = {

    // 不可变
    val s = Set(1, 2, 3, 4, 5, 6)
    println(s)

    // 可变 没有new
    var ss = scala.collection.mutable.Set[Int]()
    ss += 1
    ss += 2
    ss += 3
    ss += (7, 8, 9)
    println(ss)

  }
}
