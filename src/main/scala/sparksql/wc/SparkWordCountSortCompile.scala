package sparksql.wc

import org.apache.spark.{SparkConf, SparkContext}

/**
 * spark 词频统计
 * 输入文件
 * 读取每一行，按分隔符把每一行拆分成词，并计数一
 * 按词分发，并统计数量
 * 输出文件
 *
 * 打包提交到spark上运行
 */
object SparkWordCountSortCompile {

  /**
   * master:开发运行模式： local
   *
   * local本地运行 IDEA中不需要安装spark
   * @param args  当程序打包之后，在服务器上运行时，命令行中指定的参数，通过args传进来
   */
  def main(args: Array[String]): Unit = {
    // 配置,此时不能设置成local模式， AppName 以参数形式传递
    val sparkConf = new SparkConf()
    val sc = new SparkContext(sparkConf)

    // 读取文件的每一行，输入文件地址以参数传递,第0个参数
    var rdd = sc.textFile(args(0))

    // todo:要求按单词数量降序排列
    rdd.flatMap(_.split(",")).map(word => (word.toLowerCase(), 1)).reduceByKey(_+_)
      .map(x => (x._2, x._1)).sortByKey(false).map(x => (x._2, x._1)).saveAsTextFile(args(1))

    // 停止
    sc.stop()
  }
}
