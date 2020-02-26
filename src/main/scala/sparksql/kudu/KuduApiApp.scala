package sparksql.kudu

import java.util

import org.apache.kudu.{ColumnSchema, Schema, Type}
import org.apache.kudu.client.{AlterTableOptions, CreateTableOptions, Insert, KuduClient, KuduScanner, KuduSession, KuduTable, PartialRow, RowResult, RowResultIterator, Update}

object KuduApiApp {
  def main(args: Array[String]): Unit = {
    val KUDU_MASTERS = "master:7051,master:7151,master:7251"
    import scala.collection.JavaConverters._
    // schema   把scala list转化java
//    val KUDU_MASTERS = List(
//      "master:7051",
//      "master:7151",
//      "master:7251"
//    ).asJava

    val kuduClient: KuduClient = new KuduClient.KuduClientBuilder(KUDU_MASTERS)
      .build()

    val tabName = "KUDU_Claster"
    // 建表
    createTable(kuduClient, tabName)

    kuduClient.close()
  }

  /**
   * 建表
   * @param client
   * @param tabName
   */
  def createTable(client:KuduClient, tabName:String): Unit = {
    import scala.collection.JavaConverters._
    // schema   把scala list转化java
    val builders = List(
      new ColumnSchema.ColumnSchemaBuilder("word", Type.STRING).key(true)build(),
      new ColumnSchema.ColumnSchemaBuilder("count", Type.INT32).build()
    ).asJava
    // 表的schema信息
    val schema = new Schema(builders)
    val options: CreateTableOptions = new CreateTableOptions()

    options.setNumReplicas(1)

    val parcols: util.LinkedList[String] = new util.LinkedList[String]()
    parcols.add("word")
    options.addHashPartitions(parcols,3)

    client.createTable(tabName, schema, options)
  }

  /**
   * 删除表
   * @param client
   * @param tabName
   */
  def DeleteTable(client:KuduClient, tabName:String): Unit ={
    client.deleteTable(tabName)
  }

  /**
   * 插入数据
   * @param client
   * @param tabName
   */
  def InsertRows(client:KuduClient, tabName:String): Unit ={
    val table: KuduTable = client.openTable(tabName)
    val kuduSession: KuduSession = client.newSession()

    for(i <- 1 to 10){
      val insert: Insert = table.newInsert()
      val row: PartialRow = insert.getRow
      row.addString("word", s"lengp-$i")
      row.addInt("count", 100+1)
      // 应用insert
      kuduSession.apply(insert)
    }
  }

  /**
   * 查询数据
   * @param client
   * @param tabName
   */
  def QueryData(client:KuduClient, tabName:String): Unit ={
    val table: KuduTable = client.openTable(tabName)
    val scanner: KuduScanner = client.newScannerBuilder(table).build()

    while (scanner.hasMoreRows){
      val iterator: RowResultIterator = scanner.nextRows()

      while (iterator.hasNext){
        val result: RowResult = iterator.next()
        println(result.getString("word"))
        println(result.getInt("count"))
      }
    }
  }

  /**
   * 修改数据
   * @param client
   * @param tabName
   */
  def AlertRow(client:KuduClient, tabName:String): Unit ={
    val table: KuduTable = client.openTable(tabName)
    val kuduSession: KuduSession = client.newSession()

    val update: Update = table.newUpdate()
    val row: PartialRow = update.getRow
    row.addString("word", "love")
    row.addInt("count", 666)

    kuduSession.apply(update)
  }

  /**
   * 修改table name
   * @param client
   * @param tabName
   * @param newName
   */
  def RenameTable(client:KuduClient, tabName:String, newName:String): Unit ={
    val options: AlterTableOptions = new AlterTableOptions
    options.renameTable(newName)
    client.alterTable(tabName, options)
  }
}
