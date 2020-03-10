package sparkoffline.streaming

import com.alibaba.fastjson.{JSON, JSONObject}
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.streaming.dstream.InputDStream
import org.apache.spark.streaming.kafka010.{ConsumerStrategies, KafkaUtils, LocationStrategies}
import org.apache.spark.streaming.{Seconds, StreamingContext}
import sparkoffline.streaming.kafka.ParamsConf
import sparkoffline.streaming.redis.RedisPool


object StreamingApp {
  def main(args: Array[String]): Unit = {
    val conf: SparkConf = new SparkConf().setMaster("local[2]").setAppName("Streaming")
    val ssc: StreamingContext = new StreamingContext(conf, Seconds(5))

    val stream: InputDStream[ConsumerRecord[String, String]] = KafkaUtils.createDirectStream(
      ssc,
      LocationStrategies.PreferConsistent,
      ConsumerStrategies.Subscribe[String, String](ParamsConf.topic, ParamsConf.kafkaParams)
    )

    //todo: 打印  跑起来之后要，运行ProducerApp发送数据
    stream.map(x => x.value()).print()

    /**
     * 统计每天付费成功的总订单数
     * 统计每天付费成功的总订单金额
     */
    stream.foreachRDD(rdd => {
       //flag  fee time
      val data: RDD[JSONObject] = rdd.map(x => JSON.parseObject(x.value()))
      //todo: 后面多次用到 缓存起来
      data.cache()
      // todo: 统计每天付费成功的总订单数, 注意服务器上的 Redis服务要启动
      data.map(x => {
        val time: String = x.getString("time")
        val day: String = time.substring(0, 8)
        val flag: String = x.getString("flag")
        val flagResult: Int = if (flag == "1") 1 else 0
        (day, flagResult)
      }).reduceByKey(_ + _)
        .foreachPartition(part => {
          val jedis = RedisPool.getJedis()

          part.foreach(x => {
            jedis.incrBy("count" + x._1, x._2)
          })
        })


      // 统计每天付费成功的总订单金额
      data.map(x => {
        val time: String = x.getString("time")
        val day: String = time.substring(0, 8)
        val flag: String = x.getString("flag")
        val fee = if (flag == "1") x.getString("fee").toLong else 0
        (day, fee)
      }).reduceByKey(_ + _)
        .foreachPartition(part => {
          val jedis = RedisPool.getJedis()

          part.foreach(x => {
            jedis.incrBy("money" + x._1, x._2)
          })
        })

      //todo： 去掉缓存 对应前面的缓存
      data.unpersist(true)
    })

    ssc.start()
    ssc.awaitTermination()
  }
}
