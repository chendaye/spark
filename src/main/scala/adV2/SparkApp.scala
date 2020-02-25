package adV2

import adV2.business.{AppStatProcess, AreaStatProcess, ProvinceCityStatProcess}
import adV2.etl.ETLJsonProcess
import org.apache.kudu.WireProtocol.AppStatusPB
import org.apache.spark.sql.SparkSession

/**
 * 整个spark作业的入口点
 *
 * 离线处理 一天一批次
 */
object SparkApp {
  def main(args: Array[String]): Unit = {
    val spark: SparkSession = SparkSession.builder().master("local[2]").appName("SparkKudu").getOrCreate()

    // ETL
    ETLJsonProcess.process(spark)

    // 统计地市信息
    ProvinceCityStatProcess.process(spark)

    // 按地域分布情况统计
    AreaStatProcess.process(spark)

    // 按APP分布情况统计
    AppStatProcess.process(spark)

    spark.stop()
  }
}
