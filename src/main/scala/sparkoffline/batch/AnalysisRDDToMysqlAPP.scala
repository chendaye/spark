package sparkoffline.batch


import org.apache.spark.rdd.RDD
import java.sql.{Connection, DriverManager, PreparedStatement}

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.hbase.client.{Result, Scan}
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import org.apache.hadoop.hbase.mapreduce.TableInputFormat
import org.apache.hadoop.hbase.protobuf.ProtobufUtil
import org.apache.hadoop.hbase.util.{Base64, Bytes}
import org.apache.spark.sql.SparkSession

import scala.util.{Failure, Success, Try}
object AnalysisRDDToMysqlAPP {
  /**
   * 1）统计每个国家 每个省份访问量
   * 2）浏览器的访问量
   * @param args
   */
  def main(args: Array[String]): Unit = {
    // 设置hadoop用户
    System.setProperty("HADOOP_USER_NAME", "hadoop")
    val spark: SparkSession = SparkSession
      .builder()
      .config("spark.serializer","org.apache.spark.serializer.KryoSerializer") // 指定序列化方式
      .master("local[2]")
      .appName("Analysis")
      .getOrCreate()

    val day: String = "20190130"
    //Todo: 连接hbase
    val conf: Configuration = new Configuration()
    conf.set("hbase.rootdir", "hdfs://master:8020")
    conf.set("hbase.zookeeper.quorum", "master:2181")
    // 设置表
    val tabName:String = s"access_$day"
    conf.set(TableInputFormat.INPUT_TABLE, tabName)

    val scan: Scan = new Scan()
    // 设置要查询得cf
    scan.addFamily(Bytes.toBytes("o"))
    // 设置要查询得列
    scan.addColumn(Bytes.toBytes("o"), Bytes.toBytes("country"))
    scan.addColumn(Bytes.toBytes("o"), Bytes.toBytes("province"))
    scan.addColumn(Bytes.toBytes("o"), Bytes.toBytes("browsername"))
    // 设置scan,固定写法
    conf.set(TableInputFormat.SCAN, Base64.encodeBytes(ProtobufUtil.toScan(scan).toByteArray))

    // 读取
    val hbaseRDD: RDD[(ImmutableBytesWritable, Result)] = spark.sparkContext.newAPIHadoopRDD(
      conf,
      classOf[TableInputFormat],
      classOf[ImmutableBytesWritable],
      classOf[Result]
    )


    //todo: 统计每个国家省份访问量 WordCount  ===> Top2
    hbaseRDD.cache() // 多个地方使用，缓存起来
    val mysqlRDD: RDD[((String, String), Int)] = hbaseRDD.map(x => {
      val country: String = Bytes.toString(x._2.getValue("o".getBytes, "country".getBytes))
      val province: String = Bytes.toString(x._2.getValue("o".getBytes, "province".getBytes))
      ((country, province), 1)
    }).reduceByKey(_ + _)


//    mysqlRDD.collect().foreach(println)

    mysqlRDD.coalesce(1).foreachPartition(part => {
      Try{
        val connection: Connection = {
          Class.forName("com.mysql.jdbc.Driver")
          val url = "jdbc:mysql://master:3306/spark?characterEncoding=UTF-8"
          val user = "hadoop"
          val password = "hadoop"
          DriverManager.getConnection(url, user, password)
        }
        // TODO... 将统计结果写入到MySQL

        val preAutoCommit = connection.getAutoCommit  // 保存默认的提交设置，以便恢复
        connection.setAutoCommit(false) // 采用自己的提交

        val sql:String = "insert into log_etl (province,country,day,count) values(?,?,?,?)"
        val pstmt: PreparedStatement = connection.prepareStatement(sql)
        pstmt.addBatch(s"delete from log_etl where day=$day")

        part.foreach(x => {
          println(x._1._1+":"+x._1._2+":"+x._2)
          pstmt.setString(1, x._1._2)
          pstmt.setString(2, x._1._1)
          pstmt.setString(3, day)
          pstmt.setInt(4,  x._2)

          pstmt.addBatch()
        })

        pstmt.executeBatch()
        connection.commit()

        (connection, preAutoCommit)
      } match {
        case Success((connection, preAutoCommit)) => {
          connection.setAutoCommit(preAutoCommit) // 恢复更改的设置
          if(null != connection) connection.close()
        }
        case Failure(e) => throw e
      }
    })


    hbaseRDD.unpersist(true) // 关闭缓存，也可以不写，spark会自动处理


    spark.stop()
  }
}
