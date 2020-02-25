package ad.utils

import java.util

import org.apache.kudu.Schema
import org.apache.kudu.client.{CreateTableOptions, KuduClient}
import org.apache.kudu.client.KuduClient.KuduClientBuilder
import org.apache.spark.sql.{DataFrame, SaveMode}

/**
 * Kudu 操作
 * 单独抽取成一个工具类
 */
object KuduSaveUtil {
  /**
   * 将DF落地到Kudu
   * @param data DataFrame结果集
   * @param tableName kudu目标表
   * @param master kudu的master地址
   * @param schema kudu表的schema信息
   * @param partitionTd kudu表的分区字段
   */
  def sink(data:DataFrame, tableName:String, master:String, schema:Schema, partitionTd:String): Unit ={
    val client: KuduClient = new KuduClientBuilder(master).build()
    val options: CreateTableOptions = new CreateTableOptions

    options.setNumReplicas(1)

    val parcols: util.LinkedList[String] = new util.LinkedList[String]()
    parcols.add(partitionTd)
    options.addHashPartitions(parcols, 3)

    // 创建表
    if(client.tableExists(tableName)){
      client.deleteTable(tableName)
    }
    client.createTable(tableName, schema, options)

    // 写入数据
    data.write.mode(SaveMode.Append)
      .format("org.apache.kudu.spark.kudu")
      .option("kudu.table", tableName)
      .option("kudu.master", master)
      .save()
  }
}
