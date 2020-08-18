package scala

class Collect {
  def main(args: Array[String]): Unit = {
    //todo: 列表遍历
    val list = List(1, 2, 3, 4, 5)
    for (elem <- list) println(elem)
    val list2 = List(1, 2, 3, 4, 5)
    list2.foreach(elem => println(elem)) //本行语句甚至可以简写为list.foreach(println)，或者写成：list foreach println


    // todo:映射遍历  格式：for ((k,v) <- 映射) 语句块
    // 不可变映射
    val university = Map("XMU" -> "Xiamen University", "THU" -> "Tsinghua University","PKU"->"Peking University")
    for ((k,v) <- university) printf("Code is : %s and name is: %s\n",k,v)
    // 键值
    for (k<-university.keys) println(k)
    // 值
    for (v<-university.values) println(v)
    // forearch
    university foreach {case(k,v) => println(k+":"+v)} //或者写成：university.foreach
    university foreach {kv => println(kv._1+":"+kv._2)}


    /**
     * todo: Map
     *
     * map操作是针对集合的典型变换操作，它将某个函数应用到集合中的每个元素，并产生一个结果集合
     *
     * Lambda表达式 (参数) => 表达式 //如果参数只有一个，参数的圆括号可以省略 s => s.toUpperCase
     *
     * Lambda表达式实际上是一种匿名函数，大大简化代码编写工作。s => s.toUpperCase，
     * 它的含义是，对于输入s，都都执行s.toUpperCase操作
     */
    val books = List("Hadoop", "Hive", "HDFS")
    books.map(s => s.toUpperCase)

    /**
     * todo: flatMap
     * flatMap是map的一种扩展。在flatMap中，我们会传入一个函数，该函数对每个输入都会返回一个集合（而不是一个元素），
     * 然后，flatMap把生成的多个集合“拍扁”成为一个集合。
     *
     * 这条语句的含义就是：对于列表books中的每个元素，都执行Lambda表达式定义的匿名函数“s => s.toList”，
     * 把一个books元素转换成一个字符集合，
     * 比如说，把“Hadoop”这个字符串转换成字符集合List(‘H’,’a’,’d’,’o’,’o’,’p’)，
     * 把“Hive”字符串转换成字符集合List(‘H’,’i’,’v’,’e’)。
     * 最后，flatMap把这些集合中的元素“拍扁”得到一个集合List(‘H’, ‘a’,’d’, ‘o’, ‘o’, ‘p’, ‘H’, ‘i’, ‘v’, ‘e’, ‘H’, ‘D’, ‘F’, ‘S’)
     *
     * 注：
     *
     * a 方法 b 与 a.方法(b) 是等价的
     */
    val books2 = List("Hadoop","Hive","HDFS")
    books2 flatMap (s => s.toList)  // books中的每个元素都调用toList，生成List[Char]，最终，多个Char的集合被“拍扁”成一个集合


    /**
     * todo:filter
     * 遍历一个集合并从中获取满足指定条件的元素组成一个新的集合。Scala中可以通过filter操作来实现
     */
    val university2 = Map("XMU" -> "Xiamen University", "THU" -> "Tsinghua University","PKU"->"Peking University","XMUT"->"Xiamen University of Technology")
    // 用filter操作过滤得到那些学校名称中包含“Xiamen”的元素
    val universityOfXiamen = university2 filter {kv => kv._2 contains "Xiamen"}

    /**
     * todo: reduce
     *
     * 使用reduce这种二元操作对集合中的元素进行归约
     *
     * reduce包含reduceLeft和reduceRight两种操作，前者从集合的头部开始操作，后者从集合的尾部开始操作
     *
     * reduceLeft和reduceRight都是针对两两元素进行操作，在上面代码中，reduceLeft(_ + _)表示从列表头部开始，对两两元素进行求和操作，
     * 下划线是占位符，用来表示当前获取的两个元素，两个下划线之间的是操作符，表示对两个元素进行的操作，
     * 这里是加法操作（你也可以使用乘法*或者减法-等其他操作）
     */
    val list2 = List(1,2,3,4,5)
    list.reduceLeft(_ + _)
    list.reduceRight(_ + _)
    // 可以直接使用reduce，而不用reduceLeft和reduceRight，这时，默认采用的是reduceLeft
    val list3 = List(1,2,3,4,5)
    list.reduce(_ - _)

    /**
     * todo: fold
     *
     * 折叠(fold)操作和reduce（归约）操作比较类似。fold操作需要从一个初始的“种子”值开始，
     * 并以该值作为上下文，处理集合中的每个元素
     *
     * fold有两个变体：foldLeft()和foldRight()，其中，foldLeft()，第一个参数为累计值，集合遍历的方向是从左到右。
     * foldRight()，第二个参数为累计值，集合遍历的方向是从右到左。对于fold()自身而言，遍历的顺序是未定义的，
     * 不过，一般都是从左到右遍历
     */
    val list6 = List(1,2,3,4,5)
    list6.fold(10)(_*_)
  }
}
