package sparksql.adV2.optimize

import org.apache.spark.broadcast.Broadcast
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession

/**
 * 广播变量 优化后
 */
object BroadcastVariableOptimize {
  def main(args: Array[String]): Unit = {
    val spark: SparkSession = SparkSession.builder().master("local").appName("broadcast").getOrCreate()

    // TODO：广播变量 优化； 通过sc把小表的数据广播出去
    val peopleInfo: collection.Map[String, String] = spark.sparkContext
      .parallelize(Array(("100", "pk"), ("101", "jepson")))
      .collectAsMap()

    // 数据广播出去
    val peopleBroadcast: Broadcast[collection.Map[String, String]] = spark.sparkContext.broadcast(peopleInfo)

    val peopleDetail: RDD[(String, (String, String, String))] = spark.sparkContext
      .parallelize(Array(("100", "ustc", "beijing"), ("103", "xxx", "shanghai")))
      .map(x => (x._1, x))


    //TODO: 遍历大表的数据，和广播变量的数据对比，事项join的功能。整个过程不需要 shuffle
    peopleDetail.mapPartitions(x => {
      val broadcast: collection.Map[String, String] = peopleBroadcast.value

      /**
       * yield 关键字的简短总结:
       *
       * 针对每一次 for 循环的迭代, yield 会产生一个值，被循环记录下来 (内部实现上，像是一个缓冲区).
       * 当循环结束后, 会返回所有 yield 的值组成的集合.
       * 返回集合的类型与被遍历的集合类型是一致的.
       *
       * 但是for/yield所做的事情可以有更简单的实现方式，那就是map()方法。
       */
      for((k,v) <- x if broadcast.contains(k))
        yield (k, broadcast.get(k).getOrElse(""), v._2)
    }).foreach(println)

    // todo:扩展 ETL中 logs join Ip ； 可以用广播变量代替 join， 避免shuffle


    Thread.sleep(20000)
    spark.stop()
  }
}
