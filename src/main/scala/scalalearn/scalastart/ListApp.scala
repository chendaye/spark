package scalalearn.scalastart

object ListApp {
  def main(args: Array[String]): Unit = {

    // 不可变List
    val l = List(1, 2, 3, 4)
    println(l.head)  // List 第一个元素
    println(l.tail)  // List 第一个元素之后的所有元素
    println(Nil) // Nil 是一个空的不可变数组

    val l2 = 1 :: Nil
    val l3 = 6 :: l2

    println(l2)
    println(l3)

    // 可变List
    var list = new scala.collection.mutable.ListBuffer[Int]()
    list += 1
    list += (2, 3, 4)
    list ++= List(5, 6, 7)
    println(list)
    list -= 1
    list -= (2, 3, 4)
    list --= List(6, 7)
    println(list)

    // 相互转换
    var l5 = List(1, 2, 3)

    println(l5.toList)
    println(l5.head)
    println(l5.isEmpty)


    println(sum(1, 2, 3, 4, 5))
  }
  // 求和
  def sum(nums:Int*): Int ={
    if(nums.length == 0){
      0
    }else{
      // :_* List转化为多个参数
      nums.head + sum(nums.tail:_*)
    }
  }

}
