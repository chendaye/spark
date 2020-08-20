package scalalearn.ObjectOriented

object CaseClass {
  def main(args: Array[String]): Unit = {
    println(Dog("旺财"))
    var person = new Person2()
    println(person.name)
  }
}

// case class 不用实例化直接调用， 多用于模式匹配
case class Dog(name:String)


// 类
class Person2{
  // 属性
  var name: String = "wangcai"
  val age: Int = 17
  var heihgt: Int = _  // 占位符

  // 私有属性
  private [this] val gender = "woman"

  // 定义方法
  def eat():String = {
    name = "eat apple"
    name
  }
}


