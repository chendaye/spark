package streaming.project.dao

import streaming.project.domain.CourseClickCount
import streaming.project.utils.HBaseUtils
import org.apache.hadoop.hbase.client.Get
import org.apache.hadoop.hbase.util.Bytes

import scala.collection.mutable.ListBuffer

/**
 *
 * HBasen 建表： create 'imooc_course_clickcount','info'
 * rowkey:day_courseid
  * todo：实战课程点击数-数据访问层
  */
object CourseClickCountDAO {

  val tableName = "imooc_course_clickcount"  // 表名
  val cf = "info" // 列族
  val qualifer = "click_count"  // 列名


  /**
    * 保存数据到HBase
    * @param list  CourseClickCount集合
    */
  def save(list: ListBuffer[CourseClickCount]): Unit = {

    val table = HBaseUtils.getInstance().getTable(tableName)

    for(ele <- list) {
      // incrementColumnValue 这个api 可以直接把 结果加到现有的值上，不需要取出来更新放回去
      table.incrementColumnValue(Bytes.toBytes(ele.day_course),
        Bytes.toBytes(cf),
        Bytes.toBytes(qualifer),
        ele.click_count)
    }

  }


  /**
    * 根据rowkey查询值
    */
  def count(day_course: String):Long = {
    val table = HBaseUtils.getInstance().getTable(tableName)

    val get = new Get(Bytes.toBytes(day_course))
    val value = table.get(get).getValue(cf.getBytes, qualifer.getBytes)

    if(value == null) {
      0L
    }else{
      Bytes.toLong(value)
    }
  }

  def main(args: Array[String]): Unit = {


    val list = new ListBuffer[CourseClickCount]
    list.append(CourseClickCount("20171111_8",8))
    list.append(CourseClickCount("20171111_9",9))
    list.append(CourseClickCount("20171111_1",100))

    save(list)

    println(count("20171111_8") + " : " + count("20171111_9")+ " : " + count("20171111_1"))
  }

}
