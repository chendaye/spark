package sparksql.function

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, SparkSession}

object UDFFunctionApp {
  /**
   * lover.txt
   *
   * 统计每一个人爱人的个数
   * @param args
   */
  def main(args: Array[String]): Unit = {
    val spark: SparkSession = SparkSession.builder().master("local").getOrCreate()

    // 文件 => RDD
    val loverRDD: RDD[String] = spark.sparkContext.textFile("data/lover.txt")
    // RDD => DF
    import spark.implicits._
    val loverDF: DataFrame = loverRDD.map(_.split("##")).map(x => {
      Lover(x(0), x(1))
    }).toDF()
    loverDF.show()

    // 定义函数 使用函数
    spark.udf.register("lover_num", (s:String) => {
      s.split(",").size
    })

    // 创建表，并且使用自定义函数
    loverDF.createOrReplaceTempView("loverTable")
    spark.sql("select name, lover_num(lover) from loverTable").show()
    spark.stop()
  }

  /**
   * schema
   * @param name
   * @param lover
   */
  case class Lover(name:String, lover:String)
}
