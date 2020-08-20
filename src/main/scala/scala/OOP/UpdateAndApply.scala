package scala.OOP

/**
 * 我们经常会用到对象的apply方法和update方法，虽然我们表面上并没有察觉，
 *
 * 实际上，在Scala中，apply方法和update方法都会遵循相关的约定被调用，
 *
 * todo: 约定如下
 *    - 用括号传递给变量(对象)一个或多个参数时，Scala 会把它转换成对apply方法的调用；用括号赋值的时候调用apply
 *    - 对带有括号且包括若干参数的对象进行赋值时，编译器将调用对象的update方法，
 *      在调用时，是把括号里的参数和等号右边的对象一起作为update方法的输入参数来执行调用
 *
 * todo: 总结
 *      - 用括号给对象传参 -> 调用 apply()
 *      - 给带括号参数对象赋值时 -> 调用 update()
 */
object UpdateAndApply {
  def main(args: Array[String]): Unit = {
    //todo: apply 被调用
    val clazz = new TestApplyClass
    println(clazz("fucking"))

    //todo: 在单例对象中定义apply方法的例子
    val group = TestApplySingleObject("Zhangfei", "Liubei")
    // 在执行TestApplySingleObject(“Zhangfei”, “Liubei”)时调用了apply方法，并且把“Zhangfei and Liubei”作为返回值，
    // 赋值给group变量，因此，println(group)语句会打印出“Zhangfei and Liubei”
    println(group)
  }
}

/**
 * 测试一下apply方法是否被调用
 */
class TestApplyClass {

  def apply(param: String): String = {

    println("apply method called, parameter is: " + param)

    "Hello World!"
  }
}

/**
 * 在单例对象中定义apply方法的例子
 */
object TestApplySingleObject {
  def apply(param1: String, param2: String): String = {
    println("apply method called")
    param1 + " and " + param2
  }
}

/**
 * 测试一个伴生类和伴生对象中的apply方法实例
 *
 * todo： 过程
 *    当我们执行val a = ApplyTest()时，会导致apply方法的调用并返回该方法调用的值，也就是ApplyTest的实例化对象。
 *    当执行a()时，又会导致调用伴生类的apply方法，
 *    如果我们愿意，就可以在伴生类的apply方法中写入一些处理逻辑，这样就可以把传入的参数赋值给实例化对象的变量
 *
 * 例子：
 * 由于Scala中的Array对象定义了apply方法，因此，我们就可以采用如下方式初始化一个数组
 * 也就是说，不需要new关键字，不用构造器，直接给对象传递3个参数，Scala就会转换成对apply方法的调用，
 * 也就是调用Array类的伴生对象Array的apply方法，完成数组的初始化 val myStrArr = Array("BigData","Hadoop","Spark")
 */
class TestApplyClassAndObject {
}
object  TestApplyClassAndObject{
  def main (args: Array[String]) {
    // 没有 new 关键字
    val a = ApplyTest() //这里会调用伴生对象中的apply方法, 返回 ApplyTest 的实例
    a.greetingOfClass
    a() // 这里会调用伴生类中的apply方法
  }
}
class ApplyTest{
  def apply() = println("apply method in class is called!")
  def greetingOfClass: Unit ={
    println("Greeting method in class is called.")
  }
}
object ApplyTest{
  def apply() = {
    println("apply method in object is called")
    new ApplyTest()
  }
}

/**
 * 伴生对象作为工厂
 * Scala中伴生对象的一个重要用途，你就不会存在理解障碍了。在Scala中，伴生对象有一个重要用途，
 * 那就是，我们通常将伴生对象作为工厂使用，这样就不需要使用关键字new来创建一个实例化对象了，具体实例如下
 *
 * @param name
 */
class Car(name: String){
  def info() {println("Car name is "+ name)}
}
object Car {
  def apply(name: String) = new Car(name) //apply方法会调用伴生类Car的构造方法创建一个Car类的实例化对象
}
object MyTest{
  def main (args: Array[String]) {
    val mycar = Car("BMW") //这里会调用伴生对象中的apply方法，apply方法会创建一个Car类的实例化对象
    mycar.info()
  }
}