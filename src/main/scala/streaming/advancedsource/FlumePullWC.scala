package streaming.advancedsource

import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.ReceiverInputDStream
import org.apache.spark.streaming.flume._
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
 * spark streaming 整合flume的第一种方式
 * Pull-based Approach using a Custom Sinbash
 *
 * http://spark.apache.org/docs/latest/streaming-flume-integration.html
 */
object FlumePullWC {
  def main(args: Array[String]): Unit = {
    val conf: SparkConf = new SparkConf().setMaster("local[2]").setAppName("FlumePushWC")
    val ssc: StreamingContext = new StreamingContext(conf, Seconds(2))

    //TODO:整合 Flume
    val flumeStream: ReceiverInputDStream[SparkFlumeEvent] = FlumeUtils.createPollingStream(ssc, "master", 41414)
    flumeStream.map(x => new String(x.event.getBody.array()).trim) //92.1
        .flatMap(_.split(" ")).map((_, 1))
        .reduceByKey(_+_)
        .print()
    ssc.start()
    ssc.awaitTermination()
  }
}
