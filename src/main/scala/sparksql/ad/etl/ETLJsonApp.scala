package sparksql.ad.etl

import sparksql.ad.utils.{IpUtil, KuduSaveUtil, SchemaUtil, SqlUtil}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, SparkSession}

/**
 * 日志ETL操作
 * ETL是将业务系统的数据经过抽取、清洗转换之后加载到数据仓库的过程，
 * 目的是将企业中的分散、零乱、标准不统一的数据整合到一起，为企业的决策提供分析依据
 */
object ETLJsonApp {
  def main(args: Array[String]): Unit = {
    val spark: SparkSession = SparkSession.builder()
      .master("local[2]").appName("LogETL")
      .getOrCreate()

    // 使用 Data Source 读取json数据， DF
    var jsonDF: DataFrame = spark.read.json("data/data-test.json")

    // 读取IP解析规则
    val ipRowRDD: RDD[String] = spark.sparkContext.textFile("data/ip.txt")

    // TODO: 将RDD转换为DF ====> DF的相关操作，或者DF注册成表用sql操作。 方便
    import spark.implicits._
    val ipRuleDF: DataFrame = ipRowRDD.map(x => {
      // 处理每一行
      val splits: Array[String] = x.split("\\|")
      val startIP: Long = splits(2).toLong
      val endIP: Long = splits(3).toLong
      val province: String = splits(6)
      val city: String = splits(7)
      val isp: String = splits(9) // 运营商
      // 返回一个列表
      (startIP, endIP, province, city, isp)
    }).toDF()

    // TODO:将每条log信息读出来，对应IP规则，获得 省、市、运营商
    import org.apache.spark.sql.functions._
    // 自定义spark.sql 函数
    def getLongIp = udf((ip: String) => {
      IpUtil.ip2Long(ip)
    })
    // Returns a new Dataset by adding a column or replacing the existing column
    // that has the same name
    jsonDF = jsonDF.withColumn("ip_long", getLongIp($"ip"))

    // DF方式 ipRuleDF 和 jsonDF 进行连接操作
    jsonDF.join(ipRuleDF, jsonDF("ip_long").between(ipRuleDF("start_ip"), ipRuleDF("end_ip")))
        .show()

    // sql方式
    jsonDF.createOrReplaceTempView("logs")
    ipRuleDF.createOrReplaceTempView("ips")
    var sql = SqlUtil.SQL
    val result: DataFrame = spark.sql(sql)
    val tabName :String = "ods"  // 原始数据大表
    val masterAddress: String = "master"
    val partitionId: String = "ip"


    // 把用DF方式 或者SQL 方式获取的 ODS数据存放在 Kudu中
    KuduSaveUtil.sink(result, tabName, masterAddress, SchemaUtil.ODSSchema, partitionId)
    spark.stop()
  }
}
