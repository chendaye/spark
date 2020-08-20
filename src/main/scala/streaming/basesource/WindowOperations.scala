package streaming.basesource

import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.{DStream, ReceiverInputDStream}
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
 * todo:定时进行一个时间段内的处理
 * window length - The duration of the window
 * sliding interval - The interval at which the window operation is performed
 *
 * 两个参数和 batch size 之间是倍数关系
 *
 * 每隔 10s 计算前 10min 的wc
 * sliding interval = 10s
 * window length = 10min
 */

object WindowOperations {
  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf().setMaster("local[2]").setAppName("NetWorkWorkCount")
    // 第一个参数是config 第二个参数是，每隔几秒一个批次 batch interval
    val context: StreamingContext = new StreamingContext(sparkConf, Seconds(2))

    // 获取数据
    val batch: ReceiverInputDStream[String] = context.socketTextStream("master", 9999)
    // 用空格分隔然后 reduceByKeyAndWindow
    // reduceByKeyAndWindow(func, invFunc, windowLength, slideInterval, [numTasks])
    val result: DStream[(String, Int)] = batch.flatMap(_.split(" "))
      .map((_, 1)).reduceByKeyAndWindow((a:Int,b:Int) => (a + b), Seconds(30), Seconds(10))

    result.print()
    context.start()
    context.awaitTermination()
  }
}
