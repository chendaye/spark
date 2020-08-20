package scalalearn.ObjectOriented

object ConstruceClass {
  def main(args: Array[String]): Unit = {
    var person = new Person("wangcai", 18, "man")
    println("父类-----------" + person.name + "-------" + person.age + "------------" + person.gender)

    var studert = new Student("chendaye", 21, "zhu")
    println("子类-----------" + studert.name + "-------" + studert.age + "------------" + studert.teacher + "------------" + studert.school)

      println(studert)
  }
}

// 类名之后的是柱构造器
class Person(var name:String,var age:Int){
  val school:String = "whu"
  var gender:String = _

  // 附属构造器
  def this(name:String, age:Int, gender:String){
    this(name, age) // 附属构造器必须调动主构造器，或者其他附属构造器
    this.gender = "wuman"
  }

}

// name age 前面不用 val/var 因为是Person穿过来的
class Student(name:String, age:Int, var teacher:String) extends Person(name, age){
  // 继承重写
  override val school: String = "wuahandaxue"

  // 从写Object里面的 toString 方法
  override def toString: String = "lengo"
}


