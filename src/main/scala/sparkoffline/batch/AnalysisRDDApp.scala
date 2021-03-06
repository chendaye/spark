package sparkoffline.batch

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.hbase.CellUtil
import org.apache.hadoop.hbase.client.{Result, Scan}
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import org.apache.hadoop.hbase.mapreduce.TableInputFormat
import org.apache.hadoop.hbase.protobuf.ProtobufUtil
import org.apache.hadoop.hbase.util.{Base64, Bytes}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession

object AnalysisRDDApp {
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
    conf.set("hbase.rootdir", "hdfs://master:9000")
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

//    hbaseRDD.collect().foreach(x => {
//      val rowkey: String = Bytes.toString(x._1.get())
//      for(cell <- x._2.rawCells()){
//        val cf: String = Bytes.toString(CellUtil.cloneFamily(cell))
//        val qualifier: String = Bytes.toString(CellUtil.cloneQualifier(cell))
//        val value: String = Bytes.toString(CellUtil.cloneValue(cell))
//        println(s"$cf:$qualifier:$value")
//      }
//      rowkey
//    })

    //todo: 统计每个国家省份访问量 WordCount  ===> Top2
    hbaseRDD.cache() // 多个地方使用，缓存起来
    hbaseRDD.map(x => {
      val country: String = Bytes.toString(x._2.getValue("o".getBytes, "country".getBytes))
      val province: String = Bytes.toString(x._2.getValue("o".getBytes, "province".getBytes))
      ((country, province), 1)
    }).reduceByKey(_+_)
      .map(x => (x._2,x._1)).sortByKey(false) // 交换key value，然后排序
      .map(x => (x._2,x._1)) // 再把 key value换回来
      .take(2) // 获取前2个
//      .collect() // 获取全部
      .foreach(println)

    // todo:每个浏览器的访问量
    hbaseRDD.map(x => {
      val browsername: String = Bytes.toString(x._2.getValue("o".getBytes, "browsername".getBytes))
      (browsername, 1)
    }).reduceByKey(_+_)
      .map(x => (x._2,x._1)).sortByKey(false) // 交换key value，然后排序
      .map(x => (x._2,x._1)) // 再把 key value换回来
      .collect() // 获取全部
      .foreach(println)

    hbaseRDD.unpersist(true) // 关闭缓存，也可以不写，spark会自动处理
    spark.stop()
  }
}
