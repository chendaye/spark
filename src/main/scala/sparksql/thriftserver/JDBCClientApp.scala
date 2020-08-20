package sparksql.thriftserver

import java.sql.{Connection, DriverManager, PreparedStatement, ResultSet}

object JDBCClientApp {
  def main(args: Array[String]): Unit = {
    // 加载驱动
    Class.forName("org.apache.hive.jdbc.HiveDriver")
    val connection: Connection = DriverManager.getConnection("jdbc:hive2://master:10000")
    val statement: PreparedStatement = connection.prepareStatement("select * from lengo")
    val set: ResultSet = statement.executeQuery()
    while (set.next()){
      println(set.getObject(1)+":"+set.getObject(2))
    }
  }
}
