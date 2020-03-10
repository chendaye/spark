package sparkoffline.test

import java.util.{Date, Locale}

import org.apache.commons.lang3.time.FastDateFormat
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.types.{StringType, StructField, StructType}
import org.apache.spark.sql.{DataFrame, Dataset, Row, SparkSession}
import streaming.project.utils.{IPParser, UserAgent, UserAgentParser}

import scala.util.matching.Regex

object TestApp {
  def main(args: Array[String]): Unit = {
    val spark: SparkSession = SparkSession
      .builder()
      .master("local[2]")
      .appName("testApp")
      .getOrCreate()

//    rddParser(spark)
//    testIterator(spark)
    testCustomDataSourceV2(spark)


    spark.stop()
  }

  /**
   * 测试自定义数据源
   * @param spark
   */
  def testCustomDataSourceV2(spark:SparkSession): Unit ={
    val frame: DataFrame = spark.read.format("sparkoffline.datasourcev2.log")
      .option("path", "data/test-access.log").load()

    // 自定义函数
    import org.apache.spark.sql.functions._
    def formatTime() = udf((time: String) => {
      FastDateFormat.getInstance("yyyyMMddHHmm").format(
        new Date(FastDateFormat.getInstance("dd/MMM/yyyy:HH:mm:ss Z", Locale.ENGLISH)
          .parse(time).getTime)
      )
    })
    // DF上添加修改字段
    val dataFrame: DataFrame = frame.withColumn("formattime", formatTime()(frame("time")))
    dataFrame.show()

  }

  /**
   * 测试iterator
   * @param spark
   */
  def testIterator(spark:SparkSession): Unit ={
    val iterator: Iterator[String] = spark.sparkContext.textFile("data/test-access.log").toLocalIterator
    for (elem <- iterator) {
      println(elem)
    }
  }

  /**
   * spark.sparkContext 读取 log为RDD
   * @param spark
   */
  def rddParser(spark:SparkSession): Unit ={
    // 隐式转换
    val logRDD: RDD[String] = spark.sparkContext.textFile("data/test-access.log")
//    logRDD.foreach(println)
    val value: RDD[Row] = logRDD.map(line => {

  /**
   * 结果0：113.77.139.245
   * 结果1：[30/Jan/2019:00:00:22 +0800]
   * 结果2："GET /static/img/common/new.png HTTP/1.1"
   * 结果3：200
   * 结果4：1020
   * 结果5："www.imooc.com"
   * 结果6："https://www.imooc.com/"
   * 结果7："Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3642.0 Safari/537.36"
   * 结果8："-"
   * 结果9：10.100.16.241
   * 结果10：200
   * 结果11：0.001
   * 结果12：0.001
   */
  val pattern = new Regex("(\"[^\"]+\")|(\\[.+\\])|([0-9][0-9\\.]*)")

  val iterator: Regex.MatchIterator = pattern.findAllIn(line)
  // 进一步去掉首位多余字符
  val list: List[String] = iterator.map(_.replaceAll("\"|\\[|\\]", "")).toList

      val info: IPParser.RegionInfo = IPParser.getInstance().analyseIp(list(0))
      val agent: UserAgent = UserAgentParser.getUserAgent(list(7))
      Row(
    list(0), // ip
    info.getCountry, // country
    info.getProvince, // province
    info.getCity, // city
    list(1), // time
    list(2).split(" ")(0), // method
    list(2).split(" ")(1), // url
    list(2).split(" ")(2), // protocal
    list(3), // status
    list(4), // bytessent
    list(6), // referer
    list(7), // ua
        agent.getBrowserName,
        agent.getBrowserVersion,
        agent.getOsName,
        agent.getOsVersion
  )
})
//      .foreach(println)

    val schema = StructType(Seq(
      StructField("ip", StringType, true),
      StructField("country", StringType, true),
      StructField("province", StringType, true),
      StructField("city", StringType, true),
      StructField("time", StringType, true),
      StructField("method", StringType, true),
      StructField("url", StringType, true),
      StructField("protocal", StringType, true),
      StructField("status", StringType, true),
      StructField("bytessent", StringType, true),
      StructField("referer", StringType, true),
      StructField("ua", StringType, true),
      StructField("browsername", StringType, true),
      StructField("browserversion", StringType, true),
      StructField("osname", StringType, true),
      StructField("osversion", StringType, true)
    ))

    val logDF: DataFrame = spark.createDataFrame(value, schema)

    logDF.show()
  }



  /**
   * spark 解析日志
   * @param spark
   */
  def scParserString(spark: SparkSession): Unit ={
    // 隐式转换
    val value: Dataset[String] = spark.read.textFile("data/test-access.log")
      value.toDF().show(false)
  }



  /**
   * 自定义外部数据源
   * @param spark
   */
  def customSource(spark: SparkSession): Unit ={
    //TODO:自定义外部数据源
    val logDF: DataFrame = spark.read.format("com.imooc.bigdata.spark.pk")
      .option("path", "data/test-access.log").load()

    logDF.show()
  }
}
