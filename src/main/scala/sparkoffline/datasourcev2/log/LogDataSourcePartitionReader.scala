package sparkoffline.datasourcev2.log

import org.apache.spark.sql.Row
import org.apache.spark.sql.catalyst.InternalRow
import org.apache.spark.sql.catalyst.encoders.{ExpressionEncoder, RowEncoder}
import org.apache.spark.sql.sources.v2.reader.InputPartitionReader
import utils.{IPParser, UserAgent, UserAgentParser}

import scala.io.{BufferedSource, Source}
import scala.util.matching.Regex


/**
 * 就是一个迭代器:
 * 读取文件数据
 * 用get next 处理每一行
 * 最后close关闭资源
 *
 * @param options
 */
class LogDataSourcePartitionReader(options: Map[String, String]) extends InputPartitionReader[InternalRow] {

  lazy val encoder: ExpressionEncoder[Row] = RowEncoder.apply(LogDataSourceReader.schema).resolveAndBind()

  private val source: BufferedSource = getLogSource()

  //以指定的UTF-8字符集读取文件，第一个参数可以是字符串或者是java.io.File,取文件中所有行
  private val iterator: Iterator[String] = source.getLines()

  def getLogSource(): BufferedSource ={
    val source = Source.fromFile(options("path"), "UTF-8")
    source
  }

  /**
   * RDD 中的下一个元素
   * @return
   */
  override def next(): Boolean = {
    iterator.hasNext
  }

  override def get(): InternalRow = {
    val line: String = iterator.next()
//    val line:String = "110.85.18.234 - - [30/Jan/2019:00:00:22 +0800] \"GET /activity/newcomer HTTP/1.1\" 200 444 \"www.imooc.com\" \"https://www.imooc.com/course/list?c=cb\" - \"Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36 SE 2.X MetaSr 1.0\" \"-\" 10.100.136.64:80 200 0.103 0.103"
    val pattern = new Regex("(\"[^\"]+\")|(\\[.+\\])|([0-9][0-9\\.]*)")
    val matchIterator: Regex.MatchIterator = pattern.findAllIn(line)
    val list: List[String] = matchIterator.map(_.replaceAll("\"|\\[|\\]", "")).toList
    val info: IPParser.RegionInfo = IPParser.getInstance().analyseIp(list(0))
    val agent: UserAgent = UserAgentParser.getUserAgent(list(7))


    val row = encoder.toRow(Row(
      list(0).trim, // ip
      info.getCountry, // country
      info.getProvince, // province
      info.getCity, // city
      list(1).trim, // time
      list(2).split(" ")(0).trim, // method
      list(2).split(" ")(1).trim, // url
      list(2).split(" ")(2).trim, // protocal
      list(3).trim.toInt, // status
      list(4).trim.toInt, // bytessent
      list(6).trim, // referer
      list(7).trim, // ua
      agent.getBrowserName,
      agent.getBrowserVersion,
      agent.getOsName,
      agent.getOsVersion
    ))
    row
  }

  override def close(): Unit = {
    source.close()
  }

}
