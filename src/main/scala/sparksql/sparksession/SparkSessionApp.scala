package sparksql.sparksession

import org.apache.spark.sql.{DataFrame, SparkSession}


/**
 * 了解SparkSession API
 */
object SparkSessionApp {
  def main(args: Array[String]): Unit = {
    // 获取SparkSession，DF/DS编程的入口
    val spark: SparkSession = SparkSession.builder().master("local").getOrCreate()

    val df: DataFrame = spark.read.text("D:\\WHU\\sparksql\\data\\wc.txt")
    df.show() // 展示出来一个字段，string类型的value

    // 关闭资源
    spark.stop()
  }
}
