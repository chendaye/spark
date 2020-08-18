package scala

/**
 * todo: Option类型
 *  - 标准类库中的Option类型用case类来表示那种可能存在、也可能不存在的值。
 *
 * 一般而言，对于每种语言来说，都会有一个关键字来表示一个对象引用的是“无”，在Java中使用的是null。
 * Scala融合了函数式编程风格，因此，当预计到变量或者函数返回值可能不会引用任何值的时候，建议你使用Option类型。
 * Option类包含一个子类Some，当存在可以被引用的值的时候，就可以使用Some来包含这个值，
 * 例如Some(“Hadoop”)。而None则被声明为一个对象，而不是一个类，表示没有值。
 */
object Option {
  def main(args: Array[String]): Unit = {
    //首先我们创建一个映射
    val books=Map("hadoop"->5,"spark"->10,"hbase"->7)
    //下面我们从映射中取出键为"hadoop"对应的值，这个键是存在的，可以取到值，并且取到的值会被包含在Some中返回
    books.get("hadoop")
    //下面我们从映射中取出键为"hive"对应的值，这个键是不存在的，所以取到的值是None对象
    books.get("hive")

    //Option类型还提供了getOrElse方法，这个方法在这个Option是Some的实例时返回对应的值，而在是None的实例时返回传入的参数
    val sales=books.get("hive")
    sales.getOrElse("No Such Book")
    println(sales.getOrElse("No Such Book"))


    // 在Scala中，使用Option的情形是非常频繁的。在Scala里，经常会用到Option[T]类型，
    // 其中的T可以是Sting或Int或其他各种数据类型。Option[T]实际上就是一个容器，
    // 我们可以把它看做是一个集合，只不过这个集合中要么只包含一个元素（被包装在Some中返回），
    // 要么就不存在元素（返回None）。既然是一个集合，我们当然可以对它使用map、foreach或者filter等方法。比如
    books.get("hive").foreach(println)
  }
}
