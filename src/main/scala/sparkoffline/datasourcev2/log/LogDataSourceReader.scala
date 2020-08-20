package sparkoffline.datasourcev2.log

import java.util

import org.apache.spark.sql.catalyst.InternalRow
import org.apache.spark.sql.sources.v2.reader.{DataSourceReader, InputPartition}
import org.apache.spark.sql.types.{IntegerType, StringType, StructField, StructType}

/**
 * The InternalRowDataSourceReader uses a static object to define the schema. This is done for simplicity (to avoid the
 * need of moving it inwards to the partitions).
 */
class LogDataSourceReader(options: Map[String, String]) extends DataSourceReader {
  override def readSchema(): StructType = LogDataSourceReader.schema

  override def planInputPartitions(): util.List[InputPartition[InternalRow]] = {
    val factoryList = new java.util.ArrayList[InputPartition[InternalRow]]()
    factoryList.add(new LogDataSourcePartition(options: Map[String, String]))
    factoryList
  }
}

object LogDataSourceReader {
  /**
   * A sample schema which has a mix of types. Not all types are supported, this is an example. For more information
   * on the various types see
   * https://github.com/apache/spark/blob/64da2971a1f083926df35fe1366bcba84d97c7b7/sql/catalyst/src/main/scala/org/apache/spark/sql/catalyst/data/package.scala
   */
  val schema: StructType = {
    StructType(Array(
      StructField("ip", StringType, true),
      StructField("country", StringType, true),
      StructField("province", StringType, true),
      StructField("city", StringType, true),
      StructField("time", StringType, true),
      StructField("method", StringType, true),
      StructField("url", StringType, true),
      StructField("protocal", StringType, true),
      StructField("status", IntegerType, true),
      StructField("bytessent", IntegerType, true),
      StructField("referer", StringType, true),
      StructField("ua", StringType, true),
      StructField("browsername", StringType, true),
      StructField("browserversion", StringType, true),
      StructField("osname", StringType, true),
      StructField("osversion", StringType, true)
    ))
  }
}
