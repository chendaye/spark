package sparkoffline.datasourcev2.log

import org.apache.spark.sql.catalyst.InternalRow
import org.apache.spark.sql.sources.v2.reader.{InputPartition, InputPartitionReader}

/**
 * Defines a single partition in the dataframe's underlying RDD. This object is generated in the driver and then
 * serialized to the executors where it is responsible for creating the actual the ([[InputPartitionReader]]) which
 * does the actual reading.
 */
class LogDataSourcePartition(options: Map[String, String]) extends InputPartition[InternalRow]  {
  override def createPartitionReader(): InputPartitionReader[InternalRow]  = new LogDataSourcePartitionReader(options: Map[String, String])
}
