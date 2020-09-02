package sparkcore

import org.apache.spark.{SparkConf, SparkContext}

/**
 * 用 SparkCore 实现 topN
 */
object TopN {
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setMaster("local").setAppName("TopN")
    val sc = new SparkContext(sparkConf)
    val rdd = sc.textFile("data/txt/input.txt", 1)
    val N = 3
    //todo: 方法一
    rdd.map(_.split(" ")).filter(x => x.length == 2).map(x => (x(0), x(1).toInt)).groupByKey()
      .map({case(key, iterator) => { // 偏函数 case（模式匹配） 函数字面量
        (key, iterator.toList.sorted.takeRight(N))
//        (key,iterator.toList.sortWith((a,b)=>a>b).takeRight(N))
      } }).foreach(println(_))

    //todo: 方法二
    rdd.map(_.split(" ")).filter(x => x.length == 2).map(x => (x(0), x(1).toInt)).groupByKey()
      .flatMap({case (key, iterator) => {
        iterator.toList.sorted.takeRight(N).map(x => (key, x))
      }}).foreach(println(_))

    sc.stop()
  }
}
