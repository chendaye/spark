package sparksql.datasource

import java.util.Properties

import com.typesafe.config.{Config, ConfigFactory}
import org.apache.spark.sql.{DataFrame, Dataset, SaveMode, SparkSession}

object DataSourceApp {
  def main(args: Array[String]): Unit = {
    // 获取SparkSession，DF/DS编程的入口
    val spark: SparkSession = SparkSession.builder().master("local").getOrCreate()

//    textSource(spark)
//    jsonSource(spark)
//    common(spark)
//      parquetSource(spark)
//    convertResource(spark)
//    jdbcResource(spark)
    jdbcParamResource(spark)
    // 关闭资源
    spark.stop()
  }


  /**
   * 处理文本格式内容
   * @param spark
   */
  def textSource(spark:SparkSession): Unit ={
    // 隐式转换
    import spark.implicits._
    val textDF: DataFrame = spark.read.text("data/people.txt")
    textDF.show()

    val textDS: Dataset[(String)] = textDF.map(x => {
      // 每一行用逗号拆分
      val line: Array[String] = x.getString(0).split(",")
      (line(0).trim+" "+line(1).trim)
    })

    // 文本类型不支持 Int类型，并且只能输出一列 SaveMode可以设置
    textDS.write.mode(SaveMode.Append)text("output/text")
  }

  /**
   * json 数据源
   * @param spark
   */
  def jsonSource(spark: SparkSession): Unit ={
    // 隐式转换
    import spark.implicits._
    val jsonDF: DataFrame = spark.read.json("data/people.json")
//    jsonDF.show()
    // write
    jsonDF.filter("age > 17").select("name").write.mode("overwrite").json("output/json")


    val jsonDF2: DataFrame = spark.read.json("data/complict.json")

    jsonDF2.select($"age", $"name", $"info.address".as("address"), $"info.money".as("money"))
      .show()

  }

  /**
   * 对text json 等格式，统一访问
   * 多看源码
   * @param spark
   */
  def common(spark: SparkSession): Unit ={
    // 隐式转换
    import spark.implicits._

    //输入
    val textDF: DataFrame = spark.read.format("text").load("data/people.txt")
    val jsonDF: DataFrame = spark.read.format("json").load("data/people.json")
    textDF.show()
    jsonDF.show()

    // 输出
    textDF.write.format("text").mode("overwrite").save("output/common")
//    textDF.write.format("json").mode("overwrite").save("output/common")
    jsonDF.write.format("json").mode("overwrite").save("output/common")
//    jsonDF.write.format("text").mode("overwrite").save("output/common")
  }

  /**
   * Parquet 数据源
   * @param spark
   */
  def parquetSource(spark: SparkSession): Unit ={
    // 隐式转换
    import spark.implicits._
    val parquetDF: DataFrame = spark.read.parquet("data/users.parquet")
    parquetDF.show()
    parquetDF.printSchema()
    parquetDF.select("name", "favorite_color")
      .write.mode("overwrite")
      .option("compression", "none") // 是否压缩
      .parquet("output/parquet")
  }

  /**
   * 各种数据源格式的转换
   * @param spark
   */
  def convertResource(spark:SparkSession): Unit ={
    // 隐式转换
    import spark.implicits._
    val jsonDF: DataFrame = spark.read.json("data/people.json")
    jsonDF.filter("age > 20").write.format("parquet")
      .mode(SaveMode.Overwrite).save("output/jsontoparquet")
  }

  /**
   * JDBC 数据源
   * 1）有些数据存在Mysql里面，要用spark读取出来，进行分析
   * 2）从text json等数据源读取数据、分析；分析的结果要存放到Mysql里面
   *
   * CREATE USER 'hadoop'@'%' IDENTIFIED BY 'hadoop';
   * GRANT all ON databasename.* TO 'username'@'%';
   *
   *  create database if not exists spark default charset utf8 collate utf8_general_ci;
   *
   *  create table if not exists `jdbc`(`id` int unsigned auto_increment,`name` varchar(100) not null, `age` int not null, primary key(`id`))ENGINE=InnoDB DEFAULT CHARSET=utf8;
   * @param spark
   */
  def jdbcResource(spark: SparkSession): Unit ={
    // 隐式转换
    import spark.implicits._

    // todo: 第一种连接方式
    val jdbcDF = spark.read
      .format("jdbc")
      .option("url", "jdbc:mysql://master:3306")
      .option("dbtable", "spark.jdbc")
      .option("user", "hadoop")
      .option("password", "hadoop")
      .load()
    jdbcDF.filter("age > 18").show()

    // todo:第二种连接方式
    val connectionProperties = new Properties()
      connectionProperties.put("user", "hadoop")
      connectionProperties.put("password", "hadoop")
      val jdbcDF2 = spark.read
        .jdbc("jdbc:mysql://master:3306", "spark.jdbc", connectionProperties)
    jdbcDF2.filter("age > 18").show()

    // todo: 自定义schema
    // Specifying the custom data types of the read schema
    connectionProperties.put("customSchema", "id DECIMAL(38, 0), name STRING")
    val jdbcDF3 = spark.read
      .jdbc("jdbc:mysql://master:3306", "spark.jdbc", connectionProperties)
    jdbcDF3.show()


    // todo: Saving data to a JDBC source
    jdbcDF.write
      .format("jdbc")
      .option("url", "jdbc:mysql://master:3306")
      .option("dbtable", "spark.jdbc_save")
      .option("user", "hadoop")
      .option("password", "hadoop")
      .save()

    val connectionProperties_2 = new Properties()
    connectionProperties_2.put("user", "hadoop")
    connectionProperties_2.put("password", "hadoop")

    jdbcDF2.write
      .jdbc("jdbc:mysql://master:3306", "spark.jdbc_save3", connectionProperties_2)

    // Specifying create table column data types on write
    jdbcDF.write
      .option("createTableColumnTypes", "name CHAR(64), comments VARCHAR(1024)")
      .jdbc("jdbc:mysql://master:3306", "spark.jdbc_save3", connectionProperties_2)

  }

  /**
   * 把变量配置到配置文件中
   * @param spark
   */
  def jdbcParamResource(spark: SparkSession): Unit ={
    // 隐式转换
    import spark.implicits._

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
    jdbcDF.filter("age > 18").show()
  }
}
