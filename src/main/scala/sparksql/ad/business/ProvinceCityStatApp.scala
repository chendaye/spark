package sparksql.ad.business

import sparksql.ad.utils.{KuduSaveUtil, SchemaUtil, SqlUtil}
import org.apache.spark.sql.{DataFrame, SparkSession}

/**
 * 基于kudu中的 ODS表 统计省市省份分布信息
 */
object ProvinceCityStatApp {
  def main(args: Array[String]): Unit = {
    val spark: SparkSession = SparkSession.builder().master("local[2]").appName("ProvinceCity")
      .getOrCreate()

    // 从kudu中的ods表读取数据，按城市 省份进行统计分析
    val tabName = "ods"
    val master = "master"

    // 和从 txt json 读取数据类似
    val odsDF: DataFrame = spark.read.format("org.apache.kudu.spark.kudu")
      .option("kudu.table", tabName)
      .option("kudu.master", master)
      .load()
    odsDF.createOrReplaceTempView("ods")
    // 查询ods表
    val result: DataFrame = spark.sql(SqlUtil.PROVINCE_CITY_SQL)

    // 统计结果落地到 kudu
    val sinkName :String = "ods"  // 原始数据大表
    val partitionId: String = "provincename"
    KuduSaveUtil.sink(result, sinkName, master, SchemaUtil.ProvinceCitySchema, partitionId)

    spark.stop()
  }
}
