package sparksql.sparksession

import com.typesafe.config.{Config, ConfigFactory}

/**
 * 读取配置文件参数
 */
object ParamApp {
  def main(args: Array[String]): Unit = {
    val config: Config = ConfigFactory.load()
    val config1: String = config.getString("db.default.driver")

    println(config1)
  }
}
