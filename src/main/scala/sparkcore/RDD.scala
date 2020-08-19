package sparkcore

import org.apache.spark.{SparkConf, SparkContext}

/**
 * https://zhuanlan.zhihu.com/p/95746381
 */
object RDD {
  private val sparkConf = new SparkConf().setMaster("local").setAppName("learn")
  private val sc = new SparkContext(sparkConf)

  /**
   * todo: 创建RDD
   */
  def createRDD() = {
    // 使用paralleize（）从集合中创建 （内部使用makeRDD创建）
    val rdd = sc.parallelize(Array(1, 2, 3, 4, 5, 6))
    // 使用makeRDD（）从集合中创建
    val rdd2 = sc.makeRDD(Array(2, 3, 4, 5, 6))
    // 从文件获取
    var rdd3 = sc.textFile("data/wc.txt")
    // 从其他RDD转换而来
  }

  /**
   * todo: map 算子
   */
  def map_operator() ={
    val rdd = sc.parallelize(Array(1 to 100 by 2))
    rdd.collect()
  }

  def main(args: Array[String]): Unit = {
    map_operator()
  }


}


