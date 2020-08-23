package sparkoffline.batch

import java.util.zip.CRC32
import java.util.{Date, Locale}

import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.time.FastDateFormat
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.hbase.{HColumnDescriptor, HTableDescriptor, TableName}
import org.apache.hadoop.hbase.client._
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import org.apache.hadoop.hbase.mapreduce.TableOutputFormat
import org.apache.hadoop.hbase.util.Bytes
import org.apache.spark.internal.Logging
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, SparkSession}

import scala.collection.mutable

/**
 * *
 * * 批处理：一天处理一次，今天凌晨来处理昨天的数据
 * * 需要传给我们的ImoocLogApp一个处理时间：yyyyMMdd
 * * HBase表：一天一个，logs_yyyyMMdd
 * *   创建表：表名和cf，不用关注具体有多少列，只要关注有多少个cf就行了
 * *   rowkey的设计
 * *     结合项目的业务需求来
 * *     通常是组合使用：时间作为rowkey的前缀_字段(MD5/CRC32编码)
 * *   cf：o
 * *   column：是把文件系统上解析出来的df的字段放到Map中，然后一个循环拼成这一个rowkey对应的cf下的所有列
 * * 后续进行业务统计分析时，也是一天一个批次，直接就从HBase表（logs_yyyyMMdd）里去读取数据，然后使用Spark进行业务统计即可
 * *
 */
object LogETLApp {
  def main(args: Array[String]): Unit = {
    // 设置hadoop用户
    System.setProperty("HADOOP_USER_NAME", "hadoop")
    val spark: SparkSession = SparkSession
      .builder()
      .config("spark.serializer","org.apache.spark.serializer.KryoSerializer") // 指定序列化方式
      .master("local[2]")
      .appName("ETL")
      .getOrCreate()

    // todo: 清洗数据
    var frame: DataFrame = spark.read.format("sparkoffline.datasourcev2.log")
      .option("path", "data/log/access.log").load()

    val day: String = "20190130" // 先写死后续通过脚本传给spark-submit
    val input: String = s"hdfs://master:9000/access/$day/*"

    // 自定义函数
    import org.apache.spark.sql.functions._
    def formatTime() = udf((time: String) => {
      FastDateFormat.getInstance("yyyyMMddHHmm").format(
        new Date(FastDateFormat.getInstance("dd/MMM/yyyy:HH:mm:ss Z", Locale.ENGLISH)
          .parse(time).getTime)
      )
    })
    // DF上添加修改字段
    frame = frame.withColumn("formattime", formatTime()(frame("time")))
//    frame.show()

    // todo: 数据落地到HBase
    val habaseRDD: RDD[(ImmutableBytesWritable, Put)] = frame.rdd.map(x => {
      val ip: String = x.getAs[String]("ip")
      val country: String = x.getAs[String]("country")
      val province: String = x.getAs[String]("province")
      val city: String = x.getAs[String]("city")
      val formattime: String = x.getAs[String]("formattime")
      val method: String = x.getAs[String]("method")
      val url: String = x.getAs[String]("url")
      val protocal: String = x.getAs[String]("protocal")
      val status: Integer = x.getAs[Integer]("status")
      val bytessent: Integer = x.getAs[Integer]("bytessent")
      val referer: String = x.getAs[String]("referer")
      val browsername: String = x.getAs[String]("browsername")
      val browserversion: String = x.getAs[String]("browserversion")
      val osname: String = x.getAs[String]("osname")
      val osversion: String = x.getAs[String]("osversion")

      val columns = scala.collection.mutable.HashMap[String,String]()
      columns.put("ip", ip)
      columns.put("country", country)
      columns.put("province", province)
      columns.put("city", city)
      columns.put("formattime", formattime)
      columns.put("method", method)
      columns.put("url", url)
      columns.put("protocal", protocal)
      columns.put("status", status.toString)
      columns.put("bytessent", bytessent.toString)
      columns.put("referer", referer)
      columns.put("browsername", browsername)
      columns.put("browserversion", browserversion)
      columns.put("osname", osname)
      columns.put("osversion", osversion)


      // HBase API Put
      val rowKey = getRowKey(day, referer+url+ip)  // HBase的RowKey
      val put: Put = new Put(Bytes.toBytes(rowKey)) // 要保存到HBase的Put对象
      // 每一个RowKey对应的cf中的所有column 字段
      for ((k, v) <- columns) {

        // 列族，列名，列值
        put.addColumn(Bytes.toBytes("o"), Bytes.toBytes(k.toString), Bytes.toBytes(if(v != null) v.toString else "unknown"))
      }
      //TODO: 禁用WAL,禁用预先写日志机制，手动刷新数据到磁盘
      put.setDurability(Durability.SKIP_WAL)
      // 每一行都转化成k=>v
      (new ImmutableBytesWritable(rowKey.getBytes()), put)
    })
    // 测试
//    habaseRDD.collect().foreach(println)
    // todo: 连接hbase
    val conf: Configuration = new Configuration()
    conf.set("hbase.rootdir", "hdfs://master:9000")
    conf.set("hbase.zookeeper.quorum", "master:2181")

    // todo: 建表
    val tabName: String = createTable(day, conf)
    // 设置数据写到哪个表中
    conf.set(TableOutputFormat.OUTPUT_TABLE, tabName)
    // todo: 保存数据
    habaseRDD.saveAsNewAPIHadoopFile(
      "hdfs://master:9000/etl/access/hbase",  // path
      classOf[ImmutableBytesWritable], // keyClass
      classOf[Put], // valueClass
      classOf[TableOutputFormat[ImmutableBytesWritable]], // outputFormatClass
      conf // Configuration
    )

    /**
     *WAL：写数据之前先写日志。作为数据恢复的依据
     * 但是WAL会降低写的性能
     * 所以禁用WAL，然后手动把数据刷到磁盘
     */
    flushTable(tabName, conf)
    // 数据写到HBase
    spark.stop()
  }

  /**
   * 刷写数据到磁盘  MemStore ==》 StoreFile
   * @param table
   * @param conf
   */
  def flushTable(table:String, conf:Configuration): Unit = {

    var connection:Connection = null
    var admin:Admin = null
    try {
      connection = ConnectionFactory.createConnection(conf)
      admin = connection.getAdmin

      admin.flush(TableName.valueOf(table)) // MemStore==>StoreFile
    } catch {
      case e:Exception => e.printStackTrace()
    } finally {
      if(null != admin) {
        admin.close()
      }

      if(null != connection) {
        connection.close()
      }
    }
  }

  /**
   * 字符串拼接尽量不要使用 “+”号
   * todo：原因：String s = "a"+"b"+"c"; 变成了 String s = "abc";
   * 如果连接字符串的都是字面量常量，那么编译器会做优化，将它变为连接后的字符串常量。
   *
   * s=s+"a"+"b"; 变成了 s = (new StringBuilder()).append(s).append("a").append("b").toString();
   * 如果拼接字符串中有任何一个是变量，那么就会创建StringBuilder,使用StringBuilder来append字符。
   * 可以看到如果在一个循环中，循环次数可观的情况下，会创建大量的StringBuilder，
   * 循环完毕就会回收，白白浪费内存。
   *
   * 加号连接变量会创建多个StringBuilder，
   *
   * todo：https://juejin.im/post/5dd3d8256fb9a01feb77ff8f
   *
   * @param time
   * @param info
   * @return
   */
  def getRowKey(time:String, info:String): String ={
    val builder: StringBuilder = new StringBuilder(time)
    builder.append("_")
    // 用CRC32编码
    val crc: CRC32 = new CRC32()
    crc.reset()
    if (StringUtils.isNoneEmpty(info)){
      crc.update(Bytes.toBytes(info))
    }

    builder.append(crc.getValue)
    builder.toString()
  }


  /**
   * 创建表
   * @param day
   * @param conf
   */
  def createTable(day:String, conf:Configuration) = {
    val table :String= "access_"+day
    var connect : Connection = null
    var admin: Admin = null
    try{
      connect = ConnectionFactory.createConnection(conf)
      admin = connect.getAdmin

      // 此spark任务是离线的，如果中途挂了，下一次跑的时候要清空表数据重新再跑
      val tableName: TableName = TableName.valueOf(table)
      if(admin.tableExists(tableName)){
        admin.disableTable(tableName)
        admin.deleteTable(tableName)
      }
      val tableDesc: HTableDescriptor = new HTableDescriptor(tableName)
      val columnDescriptor: HColumnDescriptor = new HColumnDescriptor("o")
      tableDesc.addFamily(columnDescriptor)
      admin.createTable(tableDesc)
    }catch {
      case e:Exception => e.printStackTrace()
    }finally {
      if(connect != null){
        connect.close()
      }
      if(admin != null){
        admin.close()
      }
    }
    table

  }
}
