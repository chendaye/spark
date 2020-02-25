package sparksql.sparkonhive

import com.typesafe.config.{Config, ConfigFactory}
import org.apache.spark.sql.{SaveMode, SparkSession}

object SparkHiveApp {
  def main(args: Array[String]): Unit = {
    // 要操作Hive，要开启HIve支持
    val spark: SparkSession = SparkSession.builder().master("local").appName("sparkOnHive")
      .enableHiveSupport()
      .getOrCreate()

    // hive 上的表
//    spark.table("default.lengo").show()

    // 把数据存到hive
    sparkToHive(spark)

    spark.stop()
  }

  /**
   * Spark 从数据库查出数据
   * 再把查出的数据存到Hive中
   * @param spark
   */
  def sparkToHive(spark:SparkSession): Unit ={
    // 配置
    val config: Config = ConfigFactory.load()
    val driver: String = config.getString("db.default.driver") //提交到yarn 或者 standlone上需要设置
    val url: String = config.getString("db.default.url")
    val user: String = config.getString("db.default.user")
    val password: String = config.getString("db.default.password")
    val database: String = config.getString("db.default.database")
    val table: String = config.getString("db.default.table")

    // todo: 第一种连接方式
    val jdbcDF = spark.read
      .format("jdbc")
      .option("url", url)
      .option("dbtable", s"$database.$table")
      .option("user", user)
      .option("password", password)
      .load()
//    jdbcDF.filter("age > 1").show()

    // 存到hive
    jdbcDF.write.mode(SaveMode.Overwrite).saveAsTable("sparkstorehive_test")

    // saveAsTable 和 insertInto区别
    /**
     * Inserts the content of the `DataFrame` to the specified table. It requires that
     * the schema of the `DataFrame` is the same as the schema of the table.
     *
     * @note Unlike `saveAsTable`, `insertInto` ignores the column names and just uses position-based
     *  resolution.
     *
     *  it inserts data to an existing table, format or options will be ignored.
     */
//    jdbcDF.write.insertInto("sparkstorehive")
  }
}
