package scala

import java.io.PrintWriter

import scala.io.Source

object Base {


  /**
   * 变量声明
   */
  def base() = {
    //todo: val 声明变量
    val s:String = "hello ward"
    val s1 = "hello world" // 类型推断
    // String类型全称是java.lang.String，也就是说，Scala的字符串是由Java的String类来实现的，
    // 因此，我们也可以使用java.lang.String来声明
    val s2:java.lang.String = "hello world"

    //todo: var 声明变量
    var s4 = "hello world" // 可变
  }

  /**
   * todo: Scala的数据类型包括：
   *   - Byte
   *   - Char
   *   - Short
   *   - Int
   *   - Long
   *   - Float
   *   - Double
   *   - Boolean。
   *   和Java不同的是，在Scala中，这些类型都是“类”，并且都是包scala的成员，
   *   比如，Int的全名是scala.Int。对于字符串，Scala用java.lang.String类来表示字符串。
   *
   * todo: 字面量包括:
   *    - 整数字面量
   *    - 浮点数字面量
   *    - 布尔型字面量
   *    - 字符字面量
   *    - 字符串字面量
   *    - 符号字面量
   *    - 函数字面量
   *    - 元组字面量
   */
  def data_type() = {
    val i = 123  //123就是整数字面量
    val i1 = 3.14 //3.14就是浮点数字面量
    val i2 = true //true就是布尔型字面量
    val i3 = 'A' //'A'就是字符字面量
    val i4 = "Hello" //"Hello"就是字符串字面量

    // Scala允许对“字面量”直接执行方法
    5.toString() //产生字符串"5"
    "abc".intersect("bcd")  //输出"bc"
    print(i)
  }

  /**
   * todo: 操作符
   *
   * 在Scala中，可以使用加(+)、减(-) 、乘(*) 、除(/) 、余数（%）等操作符，而且，这些操作符就是方法。
   * 例如，5 + 3和(5).+(3)是等价的，也就是说
   *
   * 二者是等价的。前者是后者的简写形式，这里的+是方法名，是Int类中的一个方法
   * a 方法 b
   * a.方法(b)
   */
  def oprate() = {
    5.+(1)
    5 + 1
    // Java不同，在Scala中并没有提供++和–操作符，当需要递增和递减时，可以采用如下方式表达
    var i = 5
    i += 1
    print(i)
  }

  /**
   * 占位符语法
   * 为了让函数字面量更加简洁，我们可以使用下划线作为一个或多个参数的占位符，只要每个参数在函数字面量内仅出现一次。
   *
   * todo: x => x>0 和 _ > 0 是等价的
   *
   * todo: 当采用下划线的表示方法时，对于列表numList中的每个元素，都会依次传入用来替换下划线，
   * 比如，首先传入-3，然后判断-3>0是否成立，如果成立，就把该值放入结果集合，如果不成立，则舍弃，接着再传入-5，
   * 然后判断-5>0是否成立，依此类推
   */
  def placeholder() ={
    val numList = List(-3, -5, 1, 6, 9)
    numList.filter(x => x > 0 )
    numList.filter(_ > 0)
  }

  /**
   * todo: Range 循环
   */
  def range() = {
    // 创建一个从1到5的数值序列，包含区间终点5，步长为1
    1 to 5

    // 在Scala中允许对“字面量”直接执行方法
    1.to(5)

    // 创建一个从1到5的数值序列，不包含区间终点5，步长为1
    1 until 5

    // 创建一个从1到10的数值序列，包含区间终点10，步长为2
    1 to 10 by 2

    // 创建一个Float类型的数值序列，从0.5f到5.9f，步长为0.8f
    0.5f to 5.9f by 0.8f
  }

  /**
   * todo: 打印语句
   */
  def print_method() = {
    // 打印结果连在一起
    print("My name is:")
    print("Ziyu")

    // 如果要每次打印后追加一个换行符，实现换行的效果，就要采用println语句
    println("My name is:")
    println("Ziyu")

    // Scala还带有C语言风格的格式化字符串的printf函数：
    val i = 5;
    val j = 8;
    printf("My name is %s. I hava %d apples and %d eggs.\n","Ziyu",i,j)
    // 更多关于printf函数的使用方法，读者可以参考C语言书籍
  }

  /**
   * todo: 处理文件
   */
  def file() ={
    // 写文件
    val out = new PrintWriter("data/wc.txt")
    for (i <- 1 to 3) out.println(i)

    // 读文件
    val source = Source.fromFile("data/ip.txt")
    val iterator = source.getLines()
    for (line <- iterator) println(line)
  }

  /**
   * todo: 控制结构
   */
  def contral()= {
    var x = 6;
    //todo: if else
    if (x == 6){
      println("this is 'if'")
    }else{
      println("this is 'else'")
    }

    //todo: while
    while (x >= 0){
      x -= 1
      printf("this is %d\n", x)
    }

    //todo: do while
    do{
      x += 1
      printf("this do while %d\n", x)
    }while(x < 10)

    // todo: for for (变量<-表达式) 语句块 ; “变量<-表达式”被称为“生成器（generator）”
    for (i <- 1 to 5) println(i)
    for (i <- 1 to 5 by 2) println(i)

    // 但是，有时候，我们可能不希望打印出所有的结果，我们可能希望过滤出一些满足制定条件的结果，
    // 这个时候，就需要使用到称为“守卫(guard)”的表达式。比如，我们只希望输出1到5之中的所有偶数
    for (i <- 1 to 5 if i%2==0) println(i)

    // Scala也支持“多个生成器”的情形，可以用分号把它们隔开
    for (i <- 1 to 5; j <- 1 to 3) println(i*j)

    // 每个生成器都添加一个“守卫”
    for (i <- 1 to 5 if i%2==0; j <- 1 to 3 if j!=i) println(i*j)

    //todo: for推导式
    // 带有yield关键字的for循环，被称为“for推导式”。这个概念源自函数式编程，也就是说，
    // 通过for循环遍历一个或多个集合，对集合中的元素进行“推导”，从而计算得到新的集合，用于后续的其他处理
    for (i <- 1 to 20 by 2 if i%2 == 0) yield i
  }


  /**
   * todo: 高级数据结构
   */
  def array_structure() = {
    //todo: 定长数组，就是长度不变的数组，在Scala中使用Array进行声明
    val arr = new Array[Int](3)
    arr(0) = 1
    arr(1) = 2
    arr(2) = 3

    // 声明一个字符串数组
    val StrArr = new Array[String](3) //声明一个长度为3的字符串数组，每个数组元素初始化为null
    StrArr(0) = "BigData"
    StrArr(1) = "Hadoop"
    StrArr(2) = "Spark"
    for (i <- 0 to 2) println(StrArr(i))

    // Scala提供了更加简洁的数组声明和初始化方法
    val intValueArr = Array(12,45,33)
    val myStrArr = Array("BigData","Hadoop","Spark")
  }

  def list_structure() = {
    val intList = List(1, 2, 3)

    // 列表有头部和尾部的概念，可以使用intList.head来获取上面定义的列表的头部，值是1，
    // 使用intList.tail来获取上面定义的列表的尾部，值是List(2,3），可以看出，头部是一个元素，而尾部则仍然是一个列表
    // 列表的头部是一个元素，所以，我们可以使用::操作，在列表的头部增加新的元素，得到一个新的列表
    val intListOther = 0::intList

    // ::操作符是右结合的，因此，如果要构建一个列表List(1,2,3)，实际上也可以采用下面的方式
    val intList6 = 1::2::3::Nil // Nil表示空列表

    // 可以使用:::操作符对不同的列表进行连接得到新的列表
    val intList1 = List(1,2)
    val intList2 = List(3,4)
    val intList3 = intList1:::intList2

    intList.sum
  }

  /**
   *
   * todo: 元组
   * 元组是不同类型的值的聚集。元组和列表不同，列表中各个元素必须是相同类型，而元组可以包含不同类型的元素
   *
   * 当需要访问元组中的某个元素的值时，可以通过类似tuple._1、tuple._2、tuple._3这种方式就可以实现
   */
  def tuple_structure() = {
    val tuple = ("chendaye", 124)
    println(tuple._1)
    println(tuple._2)

  }

  /**
   *
   * todo: 集(set)是不重复元素的集合。
   *      列表中的元素是按照插入的先后顺序来组织的，
   *      但是，”集”中的元素并不会记录元素的插入顺序，而是以“哈希”方法对元素的值进行组织，
   *       所以，它允许你快速地找到某个元素
   *
   * 集包括可变集和不可变集，缺省情况下创建的是不可变集，通常我们使用不可变集
   *
   * 注意：虽然可变集和不可变集都有添加或删除元素的操作，但是，二者有很大的区别。
   * 对不可变集进行操作，会产生一个新的集，原来的集并不会发生变化。 而对可变集进行操作，改变的是该集本身，
   */
  def set_structure() = {
    var set = Set("Hadoop", "Spark")
    set += "Hive" // 添加元素
    println(set)
  }

  /**
   * 在Scala中，映射(Map)是一系列键值对的集合，也就是，建立了键和值之间的对应关系。在映射中，所有的值，都可以通过键来获取
   *
   * 映射包括可变和不可变两种，默认情况下创建的是不可变映射，如果需要创建可变映射，需要引入scala.collection.mutable.Map包
   *
   * 不可变映射，是无法更新映射中的元素的，也无法增加新的元素
   */
  def map_structure() = {
    var map = Map("chendaye" -> 666, "Whu" -> "hard")
    println(map("Whu"))

    val res = if (map.contains("chendaye")) map("chendaye") else 222
    println(res)

    map += ("aaa" -> 123) // 添加元素
    var map2 = scala.collection.mutable.Set("w" -> 13)
    map2("ww", 2)

    // todo: 遍历 for ((k,v) <- 映射) 语句块
    for ((k,v) <- map) printf("Code is : %s and name is: %s\n",k,v)
    // 只想把所有键打印出来：
    for (k<-map.keys) println(k)
    // 只想把所有值打印出来
    for (v<-map.values) println(v)
  }


  /**
   * todo: 迭代器
   * 在Scala中，迭代器（Iterator）不是一个集合，但是，提供了访问集合的一种方法。
   * 当构建一个集合需要很大的开销时（比如把一个文件的所有行都读取内存），迭代器就可以发挥很好的作用。
   * 迭代器包含两个基本操作：next和hasNext。next可以返回迭代器的下一个元素，hasNext用于检测是否还有下一个元素。
   */
  def iterator_method() = {
    val iterator = Iterator("Spark", "scala", "hive")
    while (iterator.hasNext){
      println(iterator.next())
    }
    for (item <- iterator){
      println(item)
    }
  }

  def main(args: Array[String]): Unit = {
    iterator_method()
  }
}
