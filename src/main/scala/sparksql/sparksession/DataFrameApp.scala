package sparksql.sparksession

import org.apache.spark.sql.{DataFrame, SparkSession}

object DataFrameApp {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().master("local").appName("DataFrameApp").getOrCreate()
    // 隐式转换
    import spark.implicits._

    val people: DataFrame = spark.read.json("data/people.json")

    people.printSchema() // 表结构
    people.show()

    // todo: 类比 select name from  table
    people.select("name").show()  // 只取一列
    // 两种写法等价 ，下面这种需要导入隐式转换
    people.select($"name").show()

    // todo: 类比 select * from table where age > 17
    people.filter($"age" > 17).show()
    people.filter("age > 17").show()

    // todo: select age,count(1) from table group by age
    people.groupBy("age").count().show()

    // todo: 注册成一张表，然后用SQL进行操作
    people.createOrReplaceTempView("people")

    spark.sql("select * from people where age > 17").show()

    people.select($"name", ($"age"+6).as("new_age")).show()


    val article: DataFrame = spark.read.json("data/article.json")
    article.printSchema()
    // 默认展示前20行，字符串超过20就截取
    article.show(10, false)
    // 前多少条
    article.head(100).foreach(println)
    article.first()
    article.take(20).foreach(println)

    val count : Long = article.count()
    println(s"总共有数据：$count 条")

    // 字段重新命名
    article.select($"title".as("tps"))
      .filter(article.col("id") > 359)
      .withColumnRenamed("cover", "pic")
      .show(10, false)

    // 排序
    import org.apache.spark.sql.functions._
    article.filter($"view" > 10).orderBy(desc("id")).show(false)

    // 注册成表，用sql操作
    article.createOrReplaceTempView("article")
    spark.sql("select id, view from article where view > 10 order by id desc").show()

    spark.stop()
  }
}
