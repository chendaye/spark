package sparksql.function

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, SparkSession}

object BuildInFunction {
  def main(args: Array[String]): Unit = {
    val spark: SparkSession = SparkSession.builder().master("local").getOrCreate()

    val userLog = Array(
      "2020-01-12,1111", // day userid
      "2020-02-12,1112",
      "2020-02-12,1112",
      "2020-03-12,1112",
      "2020-04-12,1112",
      "2020-05-12,1113",
      "2020-06-12,1114",
      "2020-07-12,1113",
      "2020-08-12,1112",
      "2020-09-12,1115",
      "2020-09-12,1115",
      "2020-10-12,1115",
      "2020-11-12,1112"
    )

    // 隐式转换
    import spark.implicits._

    // Array => RDD
    val logRDD: RDD[String] = spark.sparkContext.parallelize(userLog)

    // RDD + schema => DF
    val logDF: DataFrame = logRDD.map(x => {
      val items: Array[String] = x.split(",")
      User(items(0), items(1).toInt) // 返回
    }).toDF()

    // select time, count(user_id) from xxx group by time
    import org.apache.spark.sql.functions._
    logDF.groupBy("time").agg(count("user_id").as("pv")).show()
    // 去重
    logDF.groupBy("time").agg(countDistinct("user_id").as("uv")).show()

    logDF.show()

    // stop
    spark.stop()
  }

  case class User(time: String, user_id: Int)
}
