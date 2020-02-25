package adV2.business

import adV2.`trait`.DataProcess
import org.apache.spark.sql.{DataFrame, SparkSession}
import ad.utils.{DateUtil, KuduSaveUtil, SchemaUtil, SqlUtil}

/**
 * 统计区域信息
 */
object AreaStatProcess extends DataProcess{
  override def process(spark: SparkSession): Unit = {
    // kudu里面的表按天分表
    val sourceTableName = DateUtil.getTableName("ods", spark)
    val masterAddress = "master"
    val odsDF: DataFrame = spark.read.format("org.apache.kudu.spark.kudu")
      .option("kudu.master", masterAddress)
      .option("kudu.table", sourceTableName)
      .load()
    odsDF.createOrReplaceTempView("ods")
    val resultTmp: DataFrame = spark.sql(SqlUtil.AREA_SQL_STEP1)
    resultTmp.createOrReplaceTempView("area_tmp")
    val result: DataFrame = spark.sql(SqlUtil.AREA_SQL_STEP2)
    //    result.show(false)
    val sinkTableName = DateUtil.getTableName("area_stat", spark)
    val partitionId = "provincename"
    KuduSaveUtil.sink(result, sinkTableName, masterAddress, SchemaUtil.AREASchema, partitionId)
  }
}
