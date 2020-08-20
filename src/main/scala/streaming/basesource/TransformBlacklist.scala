package streaming.basesource

import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.streaming.dstream.{DStream, ReceiverInputDStream}
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
 * transform 操作实现黑名单过滤
 */
object TransformBlacklist {
  def main(args: Array[String]): Unit = {
//    val sparkConf: SparkConf = new SparkConf().setMaster("local[2]").setAppName("NetWorkWorkCount")
//    // 第一个参数是config 第二个参数是，每隔几秒一个批次 batch interval
//    val context: StreamingContext = new StreamingContext(sparkConf, Seconds(2))
//
//    // todo: 构建黑名单
//    val blacklist = List("xw", "xl")
//    // 列表转化为 RDD, xw ===> (xw,true)
//    val blacklistRDD: RDD[(String, Boolean)] = context.sparkContext.parallelize(blacklist).map(x => (x, true))
//
//    // 获取数据
//    val batch: ReceiverInputDStream[String] = context.socketTextStream("master", 9999)
//    // 2134,xw ===> (xw, (2134,xw))
//    val list: DStream[(String, (String, String))] = batch.map(x => (x.split(",")(1), (x.split(",")(0), x.split(",")(1))))
//    //todo: transform + leftjon ===> 过滤
//    list.transform(rdd => {
//      // (xw,((2134,xw), true)) join两个RDD
//      rdd.leftOuterJoin(blacklistRDD).filter(
//        x => {
//          x._2._2.getOrElse(false) != true
//        }
//      ).map(x => x._2._1)
//    })
//
//    list.print()
//    context.start()
//    context.awaitTermination()
  }
}
