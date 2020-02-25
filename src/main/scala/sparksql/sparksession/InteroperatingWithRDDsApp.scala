package sparksql.sparksession

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.types._
import org.apache.spark.sql.{DataFrame, Row, SparkSession}

object InteroperatingWithRDDsApp {
  def main(args: Array[String]): Unit = {
    val spark: SparkSession = SparkSession.builder().master("local").getOrCreate()

    runRDDSwitchDFV2(spark)
    // 停掉spark
    spark.stop()
  }

  case class Person(name:String,age: Int)

  /**
   * RDD 转 DF
   * 第一种方式：通过case class反射
   * 1）定义 case class
   * 2）RDD map, 每一行数据转化成 case class
   * @param spark
   */
  def runRDDSwitchDFV1(spark:SparkSession): Unit ={
    import spark.implicits._

    // 读进来的RDD
    val peopleRDD: RDD[String] = spark.sparkContext.textFile("data/people.txt")
    // 每一行RDD 用逗号拆分，然后用拆分的结果实例Person. 注意，每一个点号"."都是一个管道
    // 通过管道之前整个数据集都会被处理，然后传输到下一个步骤；下一个步骤对整个数据集进行处理
    val peopleDF: DataFrame = peopleRDD.map(_.split(",")).map(x => Person(x(0), x(1).trim.toInt))
      .toDF()
    peopleDF.show(false)

    // DF创建表
    peopleDF.createOrReplaceTempView("people")
    val queryDF: DataFrame = spark.sql("select * from people where age > 17")
    queryDF.map(x => "NAME:"+x(0)).show() // 通过索引取字段
    queryDF.map(x => "NAME:"+x.getAs[String]("name")).show() //通过字段名取字段

  }

  /**
   * RDD 转 DF
   * 第二种方式：通过编程的方式，case没法事先定义
   * 1）Create an RDD of Rows from the original RDD;
   * 2）Create the schema represented by a StructType matching the structure of Rows in the RDD created in Step 1.
   * 3）Apply the schema to the RDD of Rows via createDataFrame method provided by SparkSession.
   *
   * @param spark
   */
  def runRDDSwitchDFV2(spark:SparkSession): Unit ={
    import spark.implicits._

    // todo: Step1
    // 读进来的RDD
    val peopleRDD: RDD[String] = spark.sparkContext.textFile("data/people.txt")

    // todo: Step2
//    // The schema is encoded in a string
//    val schemaString = "name age"
//
//    // Generate the schema based on the string of schema
//    val fields = schemaString.split(" ")
//      .map(fieldName => StructField(fieldName, StringType, nullable = true))
//
//    fields.foreach(println)

//    val schema = StructType(fields)

    val innerStruct =
      StructType(
        StructField("name", StringType, true) ::
          StructField("age", IntegerType, false) :: Nil)

    // Convert records of the RDD (people) to Rows
    val rowRDD = peopleRDD
      .map(_.split(","))
      .map(attributes => Row(attributes(0), attributes(1).trim.toInt))

    // todo: Step3
    // Apply the schema to the RDD
    val peopleDF = spark.createDataFrame(rowRDD, innerStruct)

    peopleDF.show()

  }
}
