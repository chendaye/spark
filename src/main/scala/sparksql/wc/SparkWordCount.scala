package sparksql.wc

import org.apache.spark.{SparkConf, SparkContext}

/**
 * spark 词频统计
 * 输入文件
 * 读取每一行，按分隔符把每一行拆分成词，并计数一
 * 按词分发，并统计数量
 * 输出文件
 */
object SparkWordCount {

  /**
   * master:开发运行模式： local
   *
   * local本地运行 IDEA中不需要安装spark
   * @param args
   */
  def main(args: Array[String]): Unit = {
    // 配置
    val sparkConf = new SparkConf().setMaster("local").setAppName("SparkWordCount")
    val sc = new SparkContext(sparkConf)

    // 读取文件的每一行
    var rdd = sc.textFile("data/wc.txt")
    // 处理每一行
    rdd.flatMap(_.split(",")).map(word => (word.toLowerCase(), 1)).reduceByKey(_+_).

      saveAsTextFile("output/wc_out")

//    rdd.collect().foreach(println)
    // 停止
    sc.stop()
  }
}
