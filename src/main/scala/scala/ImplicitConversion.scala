package scala

/**
 * todo: 隐式转换
 * 隐式转换是Scala中一种非常有特色的功能，是其他编程语言所不具有的，
 * todo: 可以实现将某种类型的对象转换为另一种类型的对象。数据分析工作中，
 *      最常使用到的就是java和scala集合之间的互相转换，转换以后就可以调用另一种类型的方法。
 * scala提供了scala.collection.JavaConversions类，只要引入此类中相应的隐式转化方法，在程序中就可以用相应的类型来代替要求的类型。
 */
object ImplicitConversion {
  def main(args: Array[String]): Unit = {
    // 如通过以下转换，scala.collection.mutable.Buffer自动转换成了java.util.List。
    import scala.collection.JavaConversions.bufferAsJavaList
    //scala.collection.mutable.Buffer => java.util.List

    //java.util.List也可以转换成scala.collection.mutable.Buffer。
    import scala.collection.JavaConversions.asScalaBuffer
    //java.util.List => scala.collection.mutable.Buffer

    /**
    所有可能的转换汇总如下，双向箭头表示可互相转换，单箭头则表示只有左边可转换到右边。
     import scala.collection.JavaConversions._

scala.collection.Iterable <=> java.lang.Iterable
scala.collection.Iterable <=> java.util.Collection
scala.collection.Iterator <=> java.util.{ Iterator, Enumeration }
scala.collection.mutable.Buffer <=> java.util.List
scala.collection.mutable.Set <=> java.util.Set
scala.collection.mutable.Map <=> java.util.{ Map, Dictionary }
scala.collection.concurrent.Map <=> java.util.concurrent.ConcurrentMap

scala.collection.Seq         => java.util.List
scala.collection.mutable.Seq => java.util.List
scala.collection.Set         => java.util.Set
scala.collection.Map         => java.util.Map
java.util.Properties   => scala.collection.mutable.Map[String, String
     */
  }
}


/**
 * todo: 使用隐式转换
 *    - 隐式转换指的是以 implicit 关键字声明带有单个参数的转换函数，
 *    - 它将值从一种类型转换为另一种类型，以便使用之前类型所没有的功能
 *
 * todo: 隐式转换规则
 *
 * 并不是你使用 implicit 转换后，隐式转换就一定会发生，比如上面如果不调用 hammer() 方法的时候，普通人就还是普通人。
 * 通常程序会在以下情况下尝试执行隐式转换
 *
 * todo: 当对象访问一个不存在的成员时，即调用的方法不存在或者访问的成员变量不存在
 *      当对象调用某个方法，该方法存在，但是方法的声明参数与传入参数不匹配时
 *
 * todo: 以下三种情况下编译器不会尝试执行隐式转换：
 *    - 如果代码能够在不使用隐式转换的前提下通过编译，则不会使用隐式转换；
 *    - 编译器不会尝试同时执行多个转换，比如 convert1(convert2(a))*b；
 *    - 转换存在二义性，也不会发生转换。
 */
object ImplicitObject{
  def main(args: Array[String]): Unit = {

  }
}

// 普通人
class Person(val name: String)
// 在伴生对象中定义隐式转换函数
object Person{
  implicit def person2Thor(p: Person): Thor = new Thor(p.name)
}

// 雷神
class Thor(val name: String) {
  // 正常情况下只有雷神才能举起雷神之锤
  def hammer(): Unit = {
    println(name + "举起雷神之锤")
  }
}

object Thor extends App {
  //todo: 定义隐式转换方法 将普通人转换为雷神 通常建议方法名使用 source2Target,即：被转换对象 To 转换对象
  //implicit def person2Thor(p: Person): Thor = new Thor(p.name)
  // 这样普通人也能举起雷神之锤
  new Person("普通人").hammer()
}

//todo: 多个转换问题
class ClassA {
  override def toString = "This is Class A"
}

class ClassB {
  override def toString = "This is Class B"
  def printB(b: ClassB): Unit = println(b)
}

class ClassC

class ClassD

object ImplicitTest extends App {
  implicit def A2B(a: ClassA): ClassB = {
    println("A2B")
    new ClassB
  }

  implicit def C2B(c: ClassC): ClassB = {
    println("C2B")
    new ClassB
  }

  implicit def D2C(d: ClassD): ClassC = {
    println("D2C")
    new ClassC
  }

  // 这行代码无法通过编译，因为要调用到 printB 方法，需要执行两次转换 C2B(D2C(ClassD))
  //new ClassD().printB(new ClassA)

  /*
   *  下面的这一行代码虽然也进行了两次隐式转换，但是两次的转换对象并不是一个对象,所以它是生效的:
   *  转换流程如下:
   *  1. ClassC 中并没有 printB 方法,因此隐式转换为 ClassB,然后调用 printB 方法;
   *  2. 但是 printB 参数类型为 ClassB,然而传入的参数类型是 ClassA,所以需要将参数 ClassA 转换为 ClassB,这是第二次;
   *  即: C2B(ClassC) -> ClassB.printB(ClassA) -> ClassB.printB(A2B(ClassA)) -> ClassB.printB(ClassB)
   *  转换过程 1 的对象是 ClassC,而转换过程 2 的转换对象是 ClassA,所以虽然是一行代码两次转换，但是仍然是有效转换
   */
  new ClassC().printB(new ClassA)
}

// 输出：
//C2B
//A2B
//This is Class B

//todo: 定义在一个公共的对象中：
// 注：Scala 自身的隐式转换函数大部分定义在 Predef.scala 中，你可以打开源文件查看，也可以在 Scala 交互式命令行中采用 :implicit -v 查看全部隐式转换函数
object Convert {
  implicit def person2Thor(p: Person): Thor = new Thor(p.name)
}
// 导入 Convert 下所有的隐式转换函数
import Convert._

object ScalaApp extends App {
  new Person("普通人").hammer()
}








/**
 * todo:隐式参数
 * 所谓隐式参数，指的是在函数或者方法中，定义使用implicit修饰的参数。
 * 当调用该函数或方法时，scala会尝试在变量作用域中找到一个与指定类型相匹配的使用implicit修饰的对象，
 * 即隐式值，注入到函数参数中函数体使用。示例如下：
 *
 * todo: 值得注意的是，隐式参数是根据类型匹配的，因此作用域中不能同时出现两个相同类型的隐式变量，否则编译时会抛出隐式变量模糊的异常。
 *
 * todo: 引入隐式参数和引入隐式转换函数方法是一样的，有以下三种方式：
 *  - 定义在隐式参数对应类的伴生对象中；
 *  - 直接定义在执行代码的上下文作用域中；
 *  - 统一定义在一个文件中，在使用时候导入
 */
object ImplicitParam{
  def main(args: Array[String]): Unit = {
    implicit val sayHello=new SayHello

    def saySomething(name:String)(implicit sayHello:SayHello){
      sayHello.write("Hello," + name)
    }
  }
}
class SayHello{
  def write(content:String) = println(content)
}

//todo: 定义在隐式参数对应类的伴生对象中
class Delimiters(val left: String, val right: String)

object Delimiters {
  implicit val bracket = new Delimiters("(", ")")
}
// 此时执行代码的上下文中不用定义
object ScalaApp2 extends App {

  def formatted(context: String)(implicit deli: Delimiters): Unit = {
    println(deli.left + context + deli.right)
  }
  formatted("this is context")
}

//todo: 统一定义在一个文件中，在使用时候导入
object Convert2 {
  implicit val bracket = new Delimiters("(", ")")
}
// 在使用的时候导入
import Convert2.bracket

object ScalaApp extends App {
  def formatted(context: String)(implicit deli: Delimiters): Unit = {
    println(deli.left + context + deli.right)
  }
  formatted("this is context") // 输出: (this is context)
}

//todo: 利用隐式参数进行隐式转换
object Pair extends App {

  // order 既是一个隐式参数也是一个隐式转换，即如果 a 不存在 < 方法，则转换为 order(a)<b
  def smaller[T](a: T, b: T)(implicit order: T => Ordered[T]) = if (a < b) a else b

  println(smaller(1,2)) //输出 1
}




