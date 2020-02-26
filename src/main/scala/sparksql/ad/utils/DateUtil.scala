package sparksql.ad.utils

import org.apache.spark.sql.SparkSession

object DateUtil {
  def getTableName(tableName: String, spark: SparkSession): String = {
    val time = spark.sparkContext.getConf.get("spark.time")
    tableName + "_" + time
  }
}
