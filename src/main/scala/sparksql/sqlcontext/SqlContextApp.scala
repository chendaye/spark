package sparksql.sqlcontext

import org.apache.spark.sql.{DataFrame, SQLContext}
import org.apache.spark.{SparkConf, SparkContext}

object SqlContextApp {
  def main(args: Array[String]): Unit = {
    val conf: SparkConf = new SparkConf().setAppName("sparksql").setMaster("local")
    val sparkContext: SparkContext = new SparkContext(conf)
    val context: SQLContext = new SQLContext(sparkContext)

    val frame: DataFrame = context.read.text("D:\\WHU\\sparksql\\data\\wc.txt")

    frame.show()

    sparkContext.stop()
  }
}
