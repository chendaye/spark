package scala

/**
 * todo: 模式匹配
 *    - Java中有switch-case语句，但是，只能按顺序匹配简单的数据类型和表达式。
 *    - 相对而言，Scala中的模式匹配的功能则要强大得多，可以应用到switch语句、类型检查、“解构”等多种场合
 */
object PatternMatching {
  def main(args: Array[String]): Unit = {
    type_match()
  }

  // todo: 简单模式匹配
  def sample_match() = {
    val colorNum = 1
    val colorStr = colorNum match {
      case 1 => "red"
      case 2 => "green"
      case 3 => "yellow"
      case _ => "Not Allowed"
    }
    println(colorStr)

    // 模式匹配的case语句中，还可以使用变量
    val colorNum2 = 4
    val colorStr2 = colorNum2 match {
      case 1 => "red"
      case 2 => "green"
      case 3 => "yellow"
      case unexpected => unexpected + " is Not Allowed"
    }
    println(colorStr2)
  }
  //todo: 类型模式匹配
  def type_match() ={
    for (elem <- List("spark", 123, 12.3)){
      val str = elem match {
        case int: Int => int + "is a Int value"
        case double: Double => double+ "is a Double value"
        case "Spark"=> "Spark is found."
        case s: String => s + " is a string value."
        case _ => "This is an unexpected value."
      }
      println(str)
    }
  }

  //todo: “守卫(guard)”语句 可以在模式匹配中添加一些必要的处理逻辑
  def gurd_match() ={
    for (elem <- List(1,2,3,4)){
      elem match {
        case _ if (elem %2 == 0) => println(elem + " is even.")
        case _ => println(elem + " is odd.")
      }
    }
  }

  //todo:for表达式中的模式
  def for_match() ={
    val university = Map("XMU" -> "Xiamen University", "THU" -> "Tsinghua University","PKU"->"Peking University")

    for ((k,v) <- university) printf("Code is : %s and name is: %s\n",k,v)
  }

  //todo: case类的匹配  case类是一种特殊的类，它们经过优化以被用于模式匹配
  def case_match() ={
    case class Car(brand: String, price: Int)
    val myBYDCar = new Car("BYD", 89000)
    val myBMWCar = new Car("BMW", 1200000)
    val myBenzCar = new Car("Benz", 1500000)
    for (car <- List(myBYDCar, myBMWCar, myBenzCar)) {
      car match{
        case Car("BYD", 89000) => println("Hello, BYD!")
        case Car("BMW", 1200000) => println("Hello, BMW!")
        case Car(brand, price) => println("Brand:"+ brand +", Price:"+price+", do you want it?")
      }
    }
  }
}
