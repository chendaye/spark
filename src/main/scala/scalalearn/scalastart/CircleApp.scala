package scalalearn.scalastart

object CircleApp {
  def main(args: Array[String]): Unit = {
    1.to(10)
    Range(1, 12, 2)
    circleTest()
  }

  def circleTest(): Unit ={
    for(elem <- 1 to 12 if elem % 2 == 0){
      println(elem)
    }

    val bigdata = Array("scala", "hadoop", "Hive", "spark")

    for (e <- bigdata){
      println(e)
    }
    bigdata.foreach(tmp => println(tmp))

    var (num, sum) = (1, 100)
    while (num < 100){
      sum += num
      num += 1
    }
    println(sum)
  }
}
