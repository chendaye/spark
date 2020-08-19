package sparkcore

import org.apache.spark.{SparkConf, SparkContext}

/**
 * https://zhuanlan.zhihu.com/p/95746381
 */
object RDDLearn {


  /**
   * todo: 创建RDD
   */
  def createRDD(sc: SparkContext) = {
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
  def map_operator(sc: SparkContext) ={
    val rdd = sc.parallelize(Array(1 to 100))
    val source = rdd.collect()
    source.map(_.splitAt(10)).foreach(println)
  }

  /**
   * todo:mapPartitions
   *
   * todo: 对每一个分区 map
   *
   * 1. 作用：类似于map，但独立地在RDD的每一个分片（分区）上运行，因此在类型为T的RDD上运行时，
   * func的函数类型必须是Iterator[T] => Iterator[U]。假设有N个元素，
   * 有M个分区，那么map的函数的将被调用N次,而mapPartitions被调用M次,一个函数一次处理所有分区。
   *
   * 2. 需求：创建一个RDD，使每个元素*2组成新的RDD
   */
  def mapPartitions_opreator(sc: SparkContext) ={
    val rdd = sc.parallelize(Array(1,2,3,4))
    rdd.mapPartitions(x => x.map(_*2)).collect().foreach(println)
  }

  /**
   * todo: mapPartitionsWithIndex
   *
   * 作用：类似于mapPartitions，但func带有一个整数参数表示分片的索引值，因此在类型为T的RDD上运行时，
   * func的函数类型必须是(Int, Interator[T]) => Iterator[U]；
   *
   * 2. 需求：创建一个RDD，使每个元素跟所在分区形成一个元组组成一个新的RDD
   */
  def mapPartitionsWithIndex_opreator(sc: SparkContext) = {
    val rdd = sc.parallelize(Array(1,2,3,4))
    val indexRdd = rdd.mapPartitionsWithIndex((index,items)=>(items.map((index,_)))).collect().foreach(println)
  }


  /**
   *
   * todo: flatMap
   * 作用：类似于map，但是每一个输入元素可以被映射为0或多个输出元素（所以func应该返回一个序列，而不是单一元素）
   *
   * 2. 需求：创建一个元素为1-5的RDD，运用flatMap创建一个新的RDD，新的RDD为原RDD的每个元素的2倍（2，4，6，8，10）
   */
  def flatMap_oprator(sc: SparkContext) = {
   sc.parallelize(1 to 5).flatMap(x => x*3).collect().foreach(println)
  }



  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setMaster("local").setAppName("learn")
    val sc = new SparkContext(sparkConf)
    flatMap_oprator(sc)
    sc.stop()
  }


}


