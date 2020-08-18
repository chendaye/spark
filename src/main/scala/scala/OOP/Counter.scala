package scala.OOP

/**
 * value字段设置为private，这样它就成为私有字段，外界无法访问，只有在类内部可以访问该字段。
 * 如果字段前面什么修饰符都没有，就默认是public，外部可以访问该字段。对于类而言，
 * 我们并不需要声明为public，Scala文件中包含的多个类之间，都是彼此可见的
 *
 *
 * 对于方法的定义，是通过def实现的。上面的代码“def increment(): Unit = { value += 1}”中，increment()是方法，
 * 没有参数，冒号后
 *
 * 因为increment()方法只是对value的值进行了增加1的操作，并没有返回任何值，所以，返回值类型是Unit。
 * Unit后面的等号和大括号后面，包含了该方法要执行的具体操作语句。如果大括号里面只有一行语句，
 * 那么也可以直接去掉大括号，写成下面的形式
 */
class Counter() {
  private var n = 0
  def increment() = {n += 1}
  def current() = {
    n
  }

  // 去掉大括号
  def increment2() = n += 1
  def increment3() {n += num} //去掉了返回值类型和等号，只保留大括号

  // getter setter
  private var num = 1
  def value: Int = num
  def value_=(newValue: Int){
    if (newValue > 0) num = newValue //只有提供的新值是正数，才允许修改
  }

  //todo: 构造器
  private var name = "" //表示计数器的名称
  private var mode = 1 //mode用来表示计数器类型（比如，1表示步数计数器，2表示时间计数器）
  def this(name: String){ //第一个辅助构造器
    // Scala的每个类都有主构造器。Scala的主构造器是整个类体，需要在类名称后面罗列出构造器所需的所有参数，
    // 这些参数被编译成字段，字段的值就是创建对象时传入的参数的值
    this() //调用主构造器
    this.name = name
  }
  def this (name: String, mode: Int){ //第二个辅助构造器
    this(name) //调用前一个辅助构造器
    this.mode = mode
  }
}

object MyCounter {
  def main(args: Array[String]): Unit = {
    //todo: 创建实例
    val counter = new Counter() // 主构造器
    val counter2 = new Counter("spark") // 辅助构造器 1
    val counter3 = new Counter("spark", 666) // 辅助构造器 2

    counter.increment()
    println(counter.current())

    println(counter.value)
    counter.value = 6
    counter.increment3()
    println(counter.current())
    //    println(counter.spark)

  }
}

/**
 * todo: Scala中的继承与Java有着显著的不同：
 *  - 重写一个非抽象方法必须使用override修饰符。
 *  - 只有主构造器可以调用超类的主构造器。
 *  - 在子类中重写超类的抽象方法时，不需要使用override关键字。
 *  - 可以重写超类中的字段。
 *
 * Scala和Java一样，不允许类从多个超类继承，因此，下面我们只讨论继承自一个类的情形
 *
 * todo: 抽象类
 */
abstract class Car2{   //是抽象类，不能直接被实例化
  val carBrand: String //字段没有初始化值，就是一个抽象字段
  def info() //抽象方法，不需要使用abstract关键字
  def greeting() {println("Welcome to my car!")}
}

class BMWCar extends Car2 {
  override val carBrand = "BMW"  //重写超类字段，需要使用override关键字，否则编译会报错
  def info() {printf("This is a %s car. It is on sale", carBrand)} //重写超类的抽象方法时，不需要使用override关键字，不过，如果加上override编译也不错报错
  override def greeting() {println("Welcome to my BMW car!")} //重写超类的非抽象方法，必须使用override关键字
}
//todo: 扩展类 抽象类不能直接被实例化，所以，下面我们定义几个扩展类，它们都是扩展了Car类，或者说继承自Car类
class BYDCar extends Car2 {
  override val carBrand = "BYD" //重写超类字段，需要使用override关键字，否则编译会报错
  def info() {printf("This is a %s car. It is cheap.", carBrand)} //重写超类的抽象方法时，不需要使用override关键字，不过，如果加上override编译也不错报错
  override def greeting() {println("Welcome to my BYD car!")} //重写超类的非抽象方法，必须使用override关键字
}


/**
 * todo:特质(trait)
 *    - Java中提供了接口，允许一个类实现任意数量的接口。在Scala中没有接口的概念，而是提供了“特质(trait)”，它不仅实现了接口的功能，还具备了很多其他的特性。
 *    - Scala的特质，是代码重用的基本单元，可以同时拥有抽象方法和具体方法。Scala中，一个类只能继承自一个超类，却可以实现多个特质，从而重用特质中的方法和字段，实现了多重继承。
 *
 *    特质的定义和类的定义非常相似，有区别的是，特质定义使用关键字trait
 */
trait CarId{
  // 包含一个抽象字段id和抽象方法currentId。注意，抽象方法不需要使用abstract关键字，特质中没有方法体的方法，默认就是抽象方法
  var id: Int
  def currentId(): Int     //定义了一个抽象方法
}
//todo:特质可以包含具体实现
trait CarGreeting{
  def greeting(msg: String) {println(msg)}
}

//todo: 把特质混入类中 特质定义好以后，就可以使用extends或with关键字把特质混入类中。
class BYDCarId extends CarId with CarGreeting{ //使用extends关键字混入第1个特质，后面可以反复使用with关键字混入更多特质
  override var id = 10000 //BYD汽车编号从10000开始
  def currentId(): Int = {id += 1; id} //返回汽车编号
}
class BMWCarId extends CarId with CarGreeting{ //使用extends关键字混入第1个特质，后面可以反复使用with关键字混入更多特质
  override var id = 20000 //BMW汽车编号从10000开始
  def currentId(): Int = {id += 1; id} //返回汽车编号
}
object MyCar {
  def main(args: Array[String]){
    val myCarId1 = new BYDCarId()
    val myCarId2 = new BMWCarId()
    myCarId1.greeting("Welcome my first car.")
    printf("My first CarId is %d.\n",myCarId1.currentId)
    myCarId2.greeting("Welcome my second car.")
    printf("My second CarId is %d.\n",myCarId2.currentId)
  }
}