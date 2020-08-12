package scalalearn.ModeMatch

/**
 * 异常处理
 * */
object ExceptionApp extends App {
  try{

    // 打开文件
    // 使用文件
    val num = 10/0
    println(num)
  }catch {
    // 模式匹配处理异常
    case e:ArithmeticException => println("除数不能为0"+e.getMessage)
    case e:Exception => println(e.getMessage)
  } finally {
    println("释放资源")
    // 关闭文件
  }
}
