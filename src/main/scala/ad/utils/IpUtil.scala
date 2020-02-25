package ad.utils

object IpUtil {

  def main(args: Array[String]): Unit = {
    println(ip2Long("182.91.190.221"))
  }
  /**
   * 将字符串转成十进制
   * @param ip
   */
  def ip2Long(ip: String): Long = {
    val splits: Array[String] = ip.split("[.]")
    var ipNum = 0l
    // 位运算符用来对二进制位进行操作，按位操作，~,&,|,^ ,;分别为:取反，与，或，异或(不一样=1)
    // << 左移动运算符  >> 右移动运算符
    for (i <- 0 until(splits.length)) {
      ipNum = splits(i).toLong | ipNum << 8L
    }
    ipNum
  }

}
