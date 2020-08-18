package scala

/**
 * todo:函数定义和高阶函数
 *  - Scala是一门多范式编程语言，混合了面向对象编程和函数式编程的风格。
 *  - 在过去很多年，面向对象编程一直是主流，但是，随着大数据时代的到来，函数式编程开始迅速崛起.
 *  - 因为，函数式编程可以较好满足分布式并行编程的需求（函数式编程一个重要特性就是值不可变性，
 *  - 这对于编写可扩展的并发程序而言可以带来巨大好处，因为它避免了对公共的可变状态进行同步访问控制的复杂问题）
 *
 * todo:scala 中函数就是一种变量
 *
 * 在非函数式编程语言里，函数的定义包含了“函数类型”和“值”两种层面的内容。
 * 但是，在函数式编程中，函数是“头等公民”，可以像任何其他数据类型一样被传递和操作，
 * 也就是说，函数的使用方式和其他数据类型的使用方式完全一致了。
 *
 * 这时，我们就可以像定义变量那样去定义一个函数，由此导致的结果是，函数也会和其他变量一样，开始有“值”。
 *
 * todo: 就像变量的“类型”和“值”是分开的两个概念一样，函数式编程中，函数的“类型”和“值”也成为两个分开的概念，函数的“值”，就是“函数字面量”
 */
object Function {
  def main(args: Array[String]): Unit = {

  }

  /**
   * todo: 函数字面量
   *  - 函数字面量可以体现函数式编程的核心理念。前面我们已经介绍过，
   *  - 字面量包括整数字面量、浮点数字面量、布尔型字面量、字符字面量、字符串字面量、符号字面量、函数字面量和元组字面量
   */
  def function_literal() = {
    // 字面量
    val a = 123  //123就是整数字面量
    val b = 3.14 //3.14就是浮点数字面量
    val c = true //true就是布尔型字面量
    val d = 'A' //'A'就是字符字面量
    val e = "Hello" //"Hello"就是字符串字面量
  }

  /**
   * todo:函数的类型和值
   */
  def function_value_type() ={
    // 函数的类型 (Int) => Int
    // 只有多个参数时（不同参数之间用逗号隔开），圆括号才是必须的，当参数只有一个时，圆括号可以省略，Int => Int
    def counter(value: Int): Int = { value + 1}

    // 函数的值
    // 采用“=>”而不是“=”，这是Scala的语法要求
    (value: Int) => {value + 1} //只有一条语句时，大括号可以省略


      // 函数字面量
      val counter: Int => Int = { (value: Int) =>  value+1 }
  }

  /**
   * todo: 匿名函数、Lambda表达式与闭包
   */
  def anonymous_lambda() ={
    // 匿名函数、Lambda表达式与闭包
    (num: Int) => num * 2
    // 匿名函数的定义形式，我们经常称为“Lambda表达式”。“Lambda表达式”的形式如下
    // (参数) => 表达式 //如果参数只有一个，参数的圆括号可以省略

      // 可以直接把匿名函数存放到变量中
      val myNumFunc: Int=>Int = (num: Int) => num * 2
      println(myNumFunc(3)) //myNumFunc函数调用的时候，需要给出参数的值，这里传入3，得到乘法结果是6

      // 去掉myNumFunc的类型声明，也就是去掉“Int=>Int”
      val myNumFunc2 = (num: Int) => num * 2
      println(myNumFunc2(3))

      /**
       * step是一个自由变量，它的值只有在运行的时候才能确定，num的类型是确定的，
       * num的值只有在调用的时候才被赋值。这样的函数，被称为“闭包”，它反映了一个从开放到封闭的过程
       *
       * @param step
       * @return
       */
      def plusStep(step: Int) = (num: Int) => num + step
      //给step赋值
      val myFunc3 = plusStep(3)
      //调用myFunc函数
      println(myFunc3(10))

      /**
       * more是一个自由变量，还没有绑定具体的值，因此，我们说这个时候这个函数是“开放的”。相对而言，这时的x就是一个绑定的变量，
       * 已经在函数中给出了明确的定义。因此，为了能够让addMore正常得到结果，不报错，必须在函数外部给出more的值
       *
       * 给more确定具体的值1以后，就让函数addMore中的more变量绑定了具体的值1，不再是“自由变量”，而是被绑定具体的值了，
       * 或者说“被关闭”了，这也是为什么我们说这样一个函数叫做“闭包”，它反映了一个从开放到封闭的过程
       */
      var more = 1
      val addMore = (x: Int) => x + more
      addMore(10)

      /**
       * 第1次调用addMore(10)的时候，捕获到的自由变量more的值是1，因此，addMore(10)的结果是11。
       * 第2次调用addMore(10)的时候，捕获到的自由变量more的值是9，因此，addMore(10)的结果是19。
       * 结论：每个闭包都会访问闭包创建时活跃的more变量
       */
      var more2 = 1
      val addMore2 = (x: Int) => x + more2
      addMore2(10)
      addMore2(10)
  }


  /**
   * todo:高阶函数
   * 一个简单的高阶函数实例。假设有一个函数对给定两个数区间中的所有整数求和
   */
  def advance_function() ={
    def sumInts(a: Int, b: Int): Int = {
      if(a > b) 0 else a + sumInts(a + 1, b)
    }

    // 重新设计函数sumInts的实现方式，让一个函数作为另一个函数的参数

    //定义了一个新的函数sum，以函数f为参数  sum是一个接受函数参数的函数，因此，是一个高阶函数 (Int=>Int, Int, Int) => Int
    def sum(f: Int => Int, a: Int, b: Int): Int ={
      if(a > b) 0 else f(a) + sum(f, a+1, b)
    }
    //定义了一个新的函数self，该函数的输入是一个整数x，然后直接输出x自身
    def self(x: Int): Int = x
    //重新定义sumInts函数
    def sumInts2(a: Int, b: Int): Int = sum(self, a, b)
  }

  def advance_function2() = {
    // 高阶函数的意义：代码重用，抽取公用部分代码
    def sum(f: Int => Int, a: Int, b: Int): Int = {
      if(a > b) 0 else f(a) + sum(f, a+1, b)
    }
    def self(x: Int): Int = x
    def square(x: Int): Int = x * x
    def powerOfTwo(x: Int): Int = if(x == 0) 1 else 2 * powerOfTwo(x-1)

    def sumInts(a: Int, b: Int): Int = sum(self, a, b)
    def sumSquared(a: Int, b: Int): Int = sum(square, a, b)
    def sumPowersOfTwo(a: Int, b: Int): Int = sum(powerOfTwo, a, b)
    println(sumInts(1,5))
    println(sumSquared(1,5))
    println(sumPowersOfTwo(1,5))
  }
}
