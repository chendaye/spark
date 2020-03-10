package streaming.basesource

import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
 * 使用文件系统进行 wc， local/hdfs
 *
 * 这里 可以设置 local[1], 因为本地文件系统不需要 receiver
 */
object FileWordCount {
  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf().setMaster("local").setAppName("FileWordCount")
    val context: StreamingContext = new StreamingContext(sparkConf, Seconds(2))

    // 读取文件 会持续处理文件夹里面新增 的文件，已经处理过的文件不会再处理，所有文件格式要统一
    val batch: DStream[String] = context.textFileStream("data/stream")

    val result: DStream[(String, Int)] = batch.flatMap(_.split(","))
      .map((_, 1)).reduceByKey(_ + _)

    result.print()

    context.start()
    context.awaitTermination()
  }
}
