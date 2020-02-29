package sparkoffline.datasourcev2.log

import org.apache.spark.sql.sources.v2.{DataSourceOptions, DataSourceV2, ReadSupport}
import org.apache.spark.sql.sources.v2.reader.DataSourceReader

import scala.collection.JavaConverters._

/**
 * The internal row reader aims to expand the concepts in [[com.example.sources.readers.trivial.DefaultSource]] by
 * expanding on the generation of [[org.apache.spark.sql.catalyst.InternalRow]]
 *
 * The overall behavior is almost identical to the trivial data source (com.example.sources.trivial.reader package) with
 * the difference of having a more complex schema with different rows generating it in different ways
 *
 */
class DefaultSource extends DataSourceV2 with ReadSupport {
  override def createReader(options: DataSourceOptions): DataSourceReader = new LogDataSourceReader(options.asMap().asScala.toMap)
}
