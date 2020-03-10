package streaming.basesource

import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.{DStream, ReceiverInputDStream}
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
 * 监听网络端口流数据，实现词频统计
 * 测试： nc -lk 9999
 */
object SocketWordCount {
  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf().setMaster("local[2]").setAppName("NetWorkWorkCount")
    // 第一个参数是config 第二个参数是，每隔几秒一个批次 batch interval
    val context: StreamingContext = new StreamingContext(sparkConf, Seconds(2))

    // 获取数据
    val batch: ReceiverInputDStream[String] = context.socketTextStream("master", 9999)
    // 用空格分隔然后 reducer
    val result: DStream[(String, Int)] = batch.flatMap(_.split(" "))
      .map((_, 1)).reduceByKey(_ + _)

    result.print()
    context.start()
    context.awaitTermination()
  }
}
