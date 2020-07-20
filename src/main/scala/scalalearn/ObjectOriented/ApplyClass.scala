package scalalearn.ObjectOriented

object ApplyClass {
  def main(args: Array[String]): Unit = {
//    for (i <- 1 to 10){
//      ApplyTest.incr()
//    }
//    print(ApplyTest.count)  // 10  说明 object ApplyTest 是单例对象


    var b = ApplyTest()  // 调用 object.apply() 方法
    println("%%%%%%%%%%%%%%%%%%%%%%%%")
    var c = new ApplyTest()
    c() // class.apply() 方法

    // 对象或类名() 调用的是 object.apply()
    // 类的实例()   调用的是 class.apply()
  }
}

// 伴生类
// 如果有相互同名的一个class和一个object，则互为伴生对象 伴生类
class  ApplyTest {
  def apply() = {
    println("class ApplyTest apply")
  }
}

// 伴生对象 对象可以直接使用不需要实例化
object ApplyTest {
  println("object ApplyTest enter")
  var count  = 0
  def incr() = {
    count += 1
  }

  // apply 方法
  def apply() = {
    println("object ApplyTest apply")

    // 最佳实践, 在 object.apply() 中实例划class
    new ApplyTest()

  }

  println("object ApplyTest leave")
}
