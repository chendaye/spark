package scala.OOP

/**
 * todo: 在Java中，我们经常需要用到同时包含实例方法和静态方法的类，
 *    在Scala中可以通过伴生对象来实现。
 *    当单例对象与某个类具有相同的名称时，它被称为这个类的“伴生对象”。
 *    类和它的伴生对象必须存在于同一个文件中，而且可以相互访问私有成员（字段和方法）
 *
 *
 * todo :从上面结果可以看出，伴生对象中定义的newPersonId()实际上就实现了Java中静态（static）方法的功能，
 *    所以，实例化对象person1调用newPersonId()返回的值是1，实例化对象person2调用newPersonId()返回的值是2。
 *
 *todo: Scala源代码编译后都会变成JVM字节码，实际上，在编译上面的源代码文件以后，
 *      在Scala里面的class和object在Java层面都会被合二为一，
 *      class里面的成员成了实例成员，object成员成了static成员
 */
class AssociatedObject {
  private val id = AssociatedObject.newPersonId() //调用了伴生对象中的方法
  private var name = ""
  def this(name: String) {
    this()
    this.name = name
  }
  def info() { printf("The id of %s is %d.\n",name,id)}
}
//todo: 编译之后成了静态成员
object AssociatedObject {
  private var lastId = 0  //一个人的身份编号
  private def newPersonId() = {
    lastId +=1
    lastId
  }


  def main(args: Array[String]){
    val person1 = new AssociatedObject("Ziyu")
    val person2 = new AssociatedObject("Minxing")
    person1.info()
    person2.info()
  }
}