package sparkoffline.datasourcev2


import org.apache.spark.sql.Row
import org.scalatest.DiagrammedAssertions
import org.scalatest.FunSuite
import streaming.project.utils.{IPParser, UserAgent, UserAgentParser}

import scala.io.Source
import scala.util.matching.Regex



/**
 * Usage example for using the trivial reader
 */
class TestFile extends FunSuite with DiagrammedAssertions  {

  test("test scala read file") {
    //导入Scala的IO包
    //以指定的UTF-8字符集读取文件，第一个参数可以是字符串或者是java.io.File
    val source = Source.fromFile("data/test-access.log", "UTF-8")
    //或取文件中所有行
    val lineIterator = source.getLines()

//    println(lineIterator.next())

    lineIterator.map(line => {
      val pattern = new Regex("(\"[^\"]+\")|(\\[.+\\])|([0-9][0-9\\.]*)")

      val iterator: Regex.MatchIterator = pattern.findAllIn(line)
      val list: List[String] = iterator.map(_.replaceAll("\"|\\[|\\]", "")).toList

      val info: IPParser.RegionInfo = IPParser.getInstance().analyseIp(list(0))
      val agent: UserAgent = UserAgentParser.getUserAgent(list(7))
      List(
        list(0), // ip
        info.getCountry, // country
        info.getProvince, // province
        info.getCity, // city
        list(1), // time
        list(2).split(" ")(0), // method
        list(2).split(" ")(1), // url
        list(2).split(" ")(2), // protocal
        list(3), // status
        list(4), // bytessent
        list(6), // referer
        list(7), // ua
        agent.getBrowserName,
        agent.getBrowserVersion,
        agent.getOsName,
        agent.getOsVersion
      )
    }).foreach(println)



    //迭代打印所有行
//    lineIterator.foreach(println)
    //将所有行放到数组中
//    val lines = source.getLines().toArray
    source.close()
    //println(lines.size)
//    lines
  }

}
