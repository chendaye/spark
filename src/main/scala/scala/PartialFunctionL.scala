package scala

/**
 * todo: 偏函数
 *
 */
object PartialFunctionL {
  def main(args: Array[String]): Unit = {
    //todo: 在Scala中，被“{}”包含的一系列case语句可以被看成是一个函数字面量，
    // todo:它可以被用在任何普通的函数字面量适用的地方，例如被当做参数传递
    val defaultValue:Option[Int] => Int = {
      case  Some(x) => x
      case None => 0
      case _ => 5
    }
    defaultValue(Some(5))

    /**
     * todo: 在Scala中，偏函数是具有类型PartialFunction[-A,+B]的一种函数。
     *  A是其接受的函数类型，B是其返回的结果类型。 - ：表示 A或A的父类  + 表示B或者B的子类
     *  偏函数最大的特点就是它只接受和处理其参数定义域的一个子集，而对于这个子集之外的参数则抛出运行时异常。
     *  这与Case语句的特性非常契合，因为我们在使用case语句是，常常是匹配一组具体的模式，最后用“_”来代表剩余的模式。
     *  如果一一组case语句没有涵盖所有的情况，那么这组case语句就可以被看做是一个偏函数
     *
     *todo：http://www.360doc.com/content/19/0827/11/14808334_857322042.shtml
     *  在Scala中的偏函数是通过特质PartialFunction[-A, +B]来定义的，查看PatialFunction特质的API，可看到PatialFunction定义如下：
     *  1）是一个将类型A转为类型B的特质。
     *  2）接受A类型的输入参数，返回值为B类型。
     *  3）是一个一元函数，“-”符号作用于类型表示逆变，-A表明输入参数为A类型或A类型的父类，也就是说输入的参数应为A的子集，具有“部分”的含义。
     *  4）函数有可能“偏”离了A定义域(Type)类型，而成为值域B, +表明可以是B或B的子类，具有“全部”的含义。
     */

    //todo: case语句作为偏函数字面量;
  //todo: case语句作为偏函数字面量; second函数的功能是返回一个List[Int]中的第二个值。
    // todo:case函数体只涵盖了当一个List的长度大于2的情况，而忽略Nil和长度为1的列表
    val second:PartialFunction[List[Int],Int] = {
      case List(x, y) => y
    }
    // second函数的功能是返回一个List[Int]中的第二个值。case函数体只涵盖了当一个List的长度不等于于2的情况，而忽略Nil和长度为1的列表。
    second(1::2::Nil)
    // 当我们试图传入一个不在偏函数的定义域范围内的参数时，抛出了异常。如果我们想在调用函数前先检查一个参数是否在定义域范围以避免抛出异常，
    // 那么可以使用偏函数的isDefinedAt方法
    val bool = second.isDefinedAt(List(2, 3, 4))

    /**
     * Tips:一组case语句要成为一个偏函数，那么它被赋予的变量必须被声明为PartionFunction[-A,+B]
     *
     * todo：那么我们什么时候该使用偏函数？或者说偏函数给我们带来了什么好处？当我们确定我们的程序不会被传入不可处理的值时，
     * 我们就可以使用偏函数。这样万一程序被传入了不应该被传入的值，程序自动抛出异常，而不需要我们手工编写代码去抛出异常，减少了我们的代码量。
     */
  }
}
