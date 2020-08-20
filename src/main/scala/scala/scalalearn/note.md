# lazy 在scala中的使用

```scala
// 在定义时并不会发生计算，只有真正使用的时候才会发生计算
lazy val a:Int = 6
```

# 函数

- 函数最后一行的结果就是返回值，不需要return
- 只有一行的函数可以不用写大括号
- 没有参数的函数，调用时可以省略圆括号

> 默认参数

```bash
# 配置文件
$SPARK_HOME/conf/spark-defaults.conf.template

# 进入bin目录
spark-shell --help

--properties-file FILE 
# 加载额外参数的路径，如果没有指定将从 conf/spark-defaults.conf 加载
Path to a file from which to load extra properties. If not specified, this will look for conf/spark-defaults.conf.
```
> 命名参数

指定参数名称后调用函数，参数的顺序可以和定义时不一致

> 可变参数

在类型后面加一个星号，表示多个类型一样的参数



# 条件表达式

# 循环

# 面向对象

- case class 不用实例化
- trait 用 extends A with B, C
- 封装
- 继承
- 多态
- 主构造器、附属构造器
- override 关键字进行属性方法重写
- 伴生对象、伴生类：如果有一个类（对象） 与另一个对象（类）同名，那么称彼此为对方的伴生类（伴生对象）。注意定义时相互的。伴生类和伴生对象是相互的概念。对象或类名() 调用的是 object.apply()，类的实例()   调用的是 class.apply()。最佳实践是在 object.apply()中实例化class
- case class,不需实例化，可直接使用，多用于模式匹配
- 可变数组&不可变数组
- Array & List 可重复+有序 & Set 不可重复+无序


# 模式匹配

```
变量 match{
    case value1 => code1
    case value2 => code2
    ......
    case _ => coden
}
```


