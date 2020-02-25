package sparksql.sparksession

import org.apache.spark.sql.{DataFrame, Dataset, SparkSession}

object DataSetApp {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().master("local").appName("DataFrameApp").getOrCreate()
    // 隐式转换
    import spark.implicits._
    val ds: Dataset[Person] = Seq(Person("lengo", 17)).toDS()
    ds.show()

    val value: Dataset[Int] = Seq(2, 4, 6, 8).toDS()
    value.map(x => x+1).collect().foreach(println)

    // DF 转DS
    val peopleDF: DataFrame = spark.read.json("data/people.json")
    val peopleDS: Dataset[Person] = peopleDF.as[Person]
    peopleDS.show(false)

    // DF DS 区别
//    peopleDF.select("anme").show() 运行时报错
//    peopleDS.map(x => x.anme).show()  编译时就报错
    // 释放资源
    spark.stop()
  }

  case class Person(name: String, age: Long)
}
