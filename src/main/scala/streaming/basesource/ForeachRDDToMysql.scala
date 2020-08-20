package streaming.basesource

import java.sql.DriverManager

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
 * 词频统计并且结果写入mysql
 */
object ForeachRDDToMysql {
  def main(args: Array[String]): Unit = {


    val sparkConf = new SparkConf().setAppName("ForeachRDDToMysql").setMaster("local[2]")
    val ssc = new StreamingContext(sparkConf, Seconds(2))

    // 如果使用了stateful的算子，必须要设置checkpoint
    // 在生产环境中，建议大家把checkpoint设置到HDFS的某个文件夹中
    ssc.checkpoint("output/checkpoint")

    val lines = ssc.socketTextStream("master", 9999)

    val result = lines.flatMap(_.split(" ")).map((_,1)).reduceByKey(_+_)

    result.print()

    //TODO: 结果写入Mysql,注意获取连接的方式 按下面的方式会有比较好的性能
    /**
     *todo： 还可以尝试 写入 HBase Redis等
     * create table wc(word varchar(20) default null,count int default null)ENGINE=InnoDB DEFAULT CHARSET=utf8;ET=utf8;
     */
    result.foreachRDD { rdd =>
      rdd.foreachPartition { partitionOfRecords =>
        val connection = createConnection() // 每一个分区一个connection
        // 再遍历分区里面的记录
        partitionOfRecords.foreach(record => {
          val sql:String = "insert into wc (word,count) values ('"+record._1+"', "+record._2+")"
          connection.createStatement().execute(sql)
        })
        connection.close()
      }
    }

    ssc.start()
    ssc.awaitTermination()
  }

  def createConnection() = {
    Class.forName("com.mysql.jdbc.Driver")
    DriverManager.getConnection("jdbc:mysql://master:3306/spark", "hadoop", "hadoop")
  }
}
