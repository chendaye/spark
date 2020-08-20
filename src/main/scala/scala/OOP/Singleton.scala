package scala.OOP

/**
 * 单例对象的定义和类的定义很相似，明显的区分是，用object关键字，而不是用class关键字
 *
 * 对于一个Scala应用程序而言，必须包含main方法，由于上面代码中没有包含main方法，
 * 因此，不能使用scalac命令进行编译，而是直接使用scala命令运行代码，就可以得到结果；
 * 如果使用scalac命令去编译test.scala文件，就会报错。
 */
object Singleton {
  private var lastId = 0  //一个人的身份编号
  def newPersonId() = {
    lastId +=1
    lastId
  }

  def main(args: Array[String]): Unit = {
    printf("The first person id is %d.\n",Singleton.newPersonId())
    printf("The second person id is %d.\n",Singleton.newPersonId())
    printf("The third person id is %d.\n",Singleton.newPersonId())
  }
}
