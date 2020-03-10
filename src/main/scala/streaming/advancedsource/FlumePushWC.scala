package streaming.advancedsource

import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.ReceiverInputDStream
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.flume._

/**
 * spark streaming 整合flume的第一种方式
 * Flume-style Push-based Approach
 *
 * http://spark.apache.org/docs/latest/streaming-flume-integration.html
 */
object FlumePushWC {
  def main(args: Array[String]): Unit = {
    val conf: SparkConf = new SparkConf().setMaster("local[2]").setAppName("FlumePushWC")
    val ssc: StreamingContext = new StreamingContext(conf, Seconds(2))

    //TODO:整合 Flume
    val flumeStream: ReceiverInputDStream[SparkFlumeEvent] = FlumeUtils.createStream(ssc, "master", 9999)
    flumeStream.map(x => new String(x.event.getBody.array()).trim) //92.1
        .flatMap(_.split(" ")).map((_, 1))
        .reduceByKey(_+_)
        .print()
    ssc.start()
    ssc.awaitTermination()
  }
}
