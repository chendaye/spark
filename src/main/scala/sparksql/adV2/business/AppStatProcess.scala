package sparksql.adV2.business

import sparksql.ad.utils.{DateUtil, KuduSaveUtil, SchemaUtil, SqlUtil}
import sparksql.adV2.`trait`.DataProcess
import org.apache.spark.sql.{DataFrame, SparkSession}
import sparksql.adV2.`trait`.DataProcess

object AppStatProcess extends DataProcess{
  /**
   * 统计APP的信息
   * @param spark
   */
  override def process(spark: SparkSession): Unit = {
    // kudu里面的表按天分表
    val sourceTableName = "ods"
    val masterAddress = "master"
    val odsDF: DataFrame = spark.read.format("org.apache.kudu.spark.kudu")
      .option("kudu.master", masterAddress)
      .option("kudu.table", sourceTableName)
      .load()
    odsDF.createOrReplaceTempView("ods")
    val resultTmp: DataFrame = spark.sql(SqlUtil.APP_SQL_STEP1)
    resultTmp.createOrReplaceTempView("app_tmp")
    val result: DataFrame = spark.sql(SqlUtil.APP_SQL_STEP2)
    //    result.show(false)
    val sinkTableName = DateUtil.getTableName("app_stat", spark)
    val partitionId = "appId"
    KuduSaveUtil.sink(result, sinkTableName, masterAddress, SchemaUtil.APPSchema, partitionId)
  }
}
