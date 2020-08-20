package sparkcore

import org.apache.spark.{SparkConf, SparkContext}

/**
 * https://zhuanlan.zhihu.com/p/95746381
 */
object RDDLearn {

  /*********************************************************VALUE类型****************************************************/

  /**
   * todo: 创建RDD
   */
  def createRDD(sc: SparkContext) = {
    // 使用paralleize（）从集合中创建 （内部使用makeRDD创建）
    val rdd = sc.parallelize(Array(1, 2, 3, 4, 5, 6))
    // 使用makeRDD（）从集合中创建
    val rdd2 = sc.makeRDD(Array(2, 3, 4, 5, 6))
    val rdd4 = sc.makeRDD(Array((2, 3), (4, 5), (9,2)), 2)
    // 从文件获取
    var rdd3 = sc.textFile("data/wc.txt")
    // 从其他RDD转换而来
  }

  /**
   * todo: map 算子
   */
  def map_operator(sc: SparkContext) ={
    val rdd = sc.parallelize(Array(1 to 100))
    val source = rdd.collect()
    source.map(_.splitAt(10)).foreach(println)
  }

  /**
   * todo:mapPartitions
   *
   * todo: 对每一个分区 map
   *    - map()：每次处理一条数据
   *    - mapPartition()：每次处理一个分区的数据，这个分区的数据处理完后，原RDD中分区的数据才能释放，可能导致OOM
   *
   * 1. 作用：类似于map，但独立地在RDD的每一个分片（分区）上运行，因此在类型为T的RDD上运行时，
   * func的函数类型必须是Iterator[T] => Iterator[U]。假设有N个元素，
   * 有M个分区，那么map的函数的将被调用N次,而mapPartitions被调用M次,一个函数一次处理所有分区。
   *
   * 2. 需求：创建一个RDD，使每个元素*2组成新的RDD
   */
  def mapPartitions_opreator(sc: SparkContext) ={
    val rdd = sc.parallelize(Array(1,2,3,4))
    rdd.mapPartitions(x => x.map(_*2)).collect().foreach(println)
  }

  /**
   * todo: mapPartitionsWithIndex
   *
   * 作用：类似于mapPartitions，但func带有一个整数参数表示分片的索引值，因此在类型为T的RDD上运行时，
   * func的函数类型必须是(Int, Interator[T]) => Iterator[U]；
   *
   * 2. 需求：创建一个RDD，使每个元素跟所在分区形成一个元组组成一个新的RDD
   */
  def mapPartitionsWithIndex_opreator(sc: SparkContext) = {
    val rdd = sc.parallelize(Array(1,2,3,4))
    val indexRdd = rdd.mapPartitionsWithIndex((index,items)=>(items.map((index,_)))).collect().foreach(println)
  }


  /**
   *
   * todo: flatMap
   * 作用：类似于map，但是每一个输入元素可以被映射为0或多个输出元素（所以func应该返回一个序列，而不是单一元素）
   *
   * 2. 需求：创建一个元素为1-5的RDD，运用flatMap创建一个新的RDD，新的RDD为原RDD的每个元素的2倍（2，4，6，8，10）
   */
  def flatMap_oprator(sc: SparkContext) = {
    sc.parallelize(1 to 10).flatMap(_ to 5).collect().foreach(println)
  }

  /**
   * todo: glom
   *    - 作用：将每一个分区形成一个数组，形成新的RDD类型时RDD[Array[T]]
   *
   *    创建一个4个分区的RDD，并将每个分区的数据放到一个数组
   * @param sc
   */
  def glom_oprator(sc: SparkContext) ={
    val rdd = sc.parallelize(1 to 16, 4) // 4 个分区的rdd
    rdd.glom().collect().map(_.foreach(println)) // 将每个分区的数据放到一个数组
  }

  /**
   * todo: groupBy
   *    - 分组，按照传入函数的返回值进行分组。将相同的key对应的值放入一个迭代器。分组后的数据形成了对偶元组（k，v），K表示分组的key，v表示分组的数据集合
   *
   *    创建一个RDD，按照元素模以2的值进行分组。
   */
  def groupBy_oprator(sc: SparkContext)={
    val rdd = sc.makeRDD(1 to 10)
    val rdd2 = rdd.groupBy(_ % 2) // 按 对2 取模分组
    rdd2.collect().foreach(println)
  }

  /**
   * todo: filter
   *    - 过滤。返回一个新的RDD，该RDD由经过func函数计算后返回值为true的输入元素组成。
   *
   *    创建一个RDD（由字符串组成），过滤出一个新RDD（包含”xiao”子串）
   * @param sc
   */
  def filter_oprator(sc: SparkContext) = {
    var rdd = sc.parallelize(Array("xiaoming","xiaojiang","xiaohe","dazhi"))
    rdd.filter(_.contains("xiao")).collect().foreach(println)
  }

  /**
   * todo:sample(withReplacement, fraction, seed)
   *    - 以指定的随机种子随机抽样出数量为fraction的数据，withReplacement表示是抽出的数据是否放回，
   *    - true为有放回的抽样，false为无放回的抽样，seed用于指定随机数生成器种子
   *    - 种子相同时，指定相同的fraction每次抽样的数据会相同，用于测试
   *
   *    创建一个RDD（1-10），从中选择放回和不放回抽样
   */
  def sample_oprator(sc: SparkContext) = {
    val rdd = sc.parallelize(1 to 9)

    // 抽样放回
    rdd.sample(true, 0.4, 2).collect().foreach(println)

    // 抽样不放回
    rdd.sample(false, 0.4, 2).collect().foreach(println)
  }

  /**
   * todo:distinct([numTasks]))
   *    - 源RDD进行去重后返回一个新的RDD。默认情况下，只有8个并行任务来操作，但是可以传入一个可选的numTasks参数改变它。
   *    -（有shuff过程，同一个分区的数据被打乱重组到不同的分区）
   *
   *  创建一个RDD，使用distinct()对其去重
   */
  def distinct_oprator(sc: SparkContext) ={
    val rdd = sc.parallelize(List(1,2,3,4,5,6,7,2,4,3))
    rdd.distinct().collect().foreach(println)

    val unionRDD = rdd.distinct(2) // 并行度 2
  }

  /**
   * todo: coalesce(numPartitions)
   *    - 缩减分区数，用于大数据集过滤后，提高小数据集的执行效率。（可以简单地理解为合并分区，默认并没有shuff过程，可能导致数据倾斜）
   *
   *    创建一个4个分区的RDD，对其缩减分区（不会shuffle）
   * @param sc
   */
  def coalesce_oprator(sc: SparkContext) ={
    val rdd = sc.parallelize(1 to 16,4) // 4个分区
    println(rdd.partitions.size)
    val size = rdd.coalesce(3).partitions.size  // 压缩分区
    println(size)
  }

  /**
   * todo: repartition 重新 shuffle 分区
   *    - 根据分区数，重新通过网络随机洗牌所有数据
   *    - coalesce重新分区，可以选择是否进行shuffle过程。由参数shuffle: Boolean = false/true决定。
   *    - repartition实际上是调用的coalesce，默认是进行shuffle的
   *
   *    创建一个4个分区的RDD，对其重新分区
   */
  def repartition_oprator(sc: SparkContext) ={
    val rdd = sc.parallelize(1 to 16,4)
    val rerdd = rdd.repartition(2)
    println(rerdd.partitions.size)
  }

  /**
   * todo:sortBy(func,[ascending], [numTasks])
   *    - 使用func先对数据进行处理，按照处理后的数据比较结果排序，默认为正序
   *
   *    创建一个RDD，按照不同的规则进行排序
   */
  def sortBy_oprator(sc: SparkContext) = {
    val rdd = sc.parallelize(List(2,1,3,4))
    rdd.sortBy(x => x).collect().foreach(print)
    rdd.sortBy(x => x%2).collect().foreach(print)
  }

  /**
   * todo: pipe(command, [envVars])
   *      - 管道，针对每个分区，都执行一个shell脚本，返回输出的RDD
   *      - 脚本需要放在Worker节点可以访问到的位置
   *
   *      编写一个脚本，使用管道将脚本作用于RDD上。
   * @param sc
   */
  def pipe_oprator(sc: SparkContext) = {
    val rdd = sc.parallelize(List("hi","Hello","how","are","you"),1)
    rdd.pipe("/opt/module/spark/pipe.sh").collect()
  }

  /**************************************************双Value类型交互****************************************************/

  /**
   * todo: union
   *    - 对源RDD和参数RDD求并集后返回一个新的RDD
   *
   *    创建两个RDD，求并集
   * @param sc
   */
  def union_oprator(sc: SparkContext) = {
    val rdd1 = sc.parallelize(1 to 5)

    val rdd2 = sc.parallelize(5 to 10)

    val rdd3 = rdd1.union(rdd2)
    rdd3.collect().foreach(println)
  }

  /**
   * todo: subtract
   *    - 计算差的一种函数，去除两个RDD中相同的元素，不同的RDD将保留下来
   *
   *    创建两个RDD，求第一个RDD与第二个RDD的差集
   * @param sc
   */
  def subtract_oprator(sc: SparkContext) = {
    val rdd = sc.parallelize(3 to 8)
    val rdd1 = sc.parallelize(1 to 5)
    rdd.subtract(rdd1).collect().foreach(print)
  }

  /**
   * todo: intersection
   *    - 对源RDD和参数RDD求交集后返回一个新的RDD
   *
   *    创建两个RDD，求两个RDD的交集
   * @param sc
   */
  def intersection_oprator(sc: SparkContext) = {
    val rdd = sc.parallelize(3 to 8)
    val rdd1 = sc.parallelize(1 to 5)
    rdd.intersection(rdd1).collect().foreach(print)
  }


  /**
   * todo:cartesian
   *    - 笛卡尔积（尽量避免使用）
   *    - 假设集合A={a, b}，集合B={0, 1, 2}，则两个集合的笛卡尔积为{(a, 0), (a, 1), (a, 2), (b, 0), (b, 1), (b, 2)}
   *
   *    创建两个RDD，计算两个RDD的笛卡尔积
   */
    def cartesian_oprator(sc: SparkContext) = {
      val rdd = sc.parallelize(3 to 8)
      val rdd1 = sc.parallelize(1 to 5)
      rdd.cartesian(rdd1).collect().foreach(print)
    }


  /**
   * todo: zip
   *      - 将两个RDD组合成Key/Value形式的RDD,这里默认两个RDD的partition数量以及元素数量都相同，否则会抛出异常。
   *
   *      创建两个RDD，并将两个RDD组合到一起形成一个(k,v)RDD
   * @param sc
   */
  def zip_oprator(sc: SparkContext) = {
    val rdd1 = sc.parallelize(Array(1,2,3),3)
    val rdd2 = sc.parallelize(Array("a","b","c"),3)
    rdd1.zip(rdd2).collect.foreach(print)
  }

  /******************************************Key-Value类型**********************************************************/

  /**
   *todo:partitionBy
   *    - 对pairRDD进行分区操作，如果原有的partionRDD和现有的partionRDD是一致的话就不进行分区，
   *    - 否则会生成ShuffleRDD，即会产生shuffle过程。（可自定义分区）
   *
   *    创建一个4个分区的RDD，对其重新分区
   */
  def partitionBy_oprator(sc: SparkContext) = {
    val rdd = sc.parallelize(Array((1,"aaa"),(2,"bbb"),(3,"ccc"),(4,"ddd")),4)
    println(rdd.partitions.size)
    var rdd2 = rdd.partitionBy(new org.apache.spark.HashPartitioner(2))
    println(rdd2.partitions.size)
  }


  /**
   * todo: groupByKey
   *    - groupByKey也是对每个key进行操作，但只生成一个sequence。
   *
   *    创建一个pairRDD，将相同key对应值聚合到一个sequence中，并计算相同key对应值的相加结果。
   */
  def groupByKey_oprator(sc: SparkContext) = {
    val words = Array("one", "two", "two", "three", "three", "three")
    val group = sc.parallelize(words).map(word => (word, 1)).groupByKey()
    group.collect().foreach(println)
    group.map(x => (x._1, x._2.sum)).collect().foreach(println)
  }


  /**
   * todo: reduceByKey(func, [numTasks])
   *    - 在一个(K,V)的RDD上调用，返回一个(K,V)的RDD，使用指定的reduce函数，将相同key的值聚合到一起，reduce任务的个数可以通过第二个可选的参数来设置
   *    - reduceByKey：按照key进行聚合，在shuffle之前有combine（预聚合）操作，返回结果是RDD[k,v].
   *    - groupByKey：按照key进行分组，直接进行shuffle
   *    - 开发指导：reduceByKey比groupByKey，建议使用
   *    - reduceByKey = combine -> shuffle
   *    - groupByKey = key 分组 -> shuffle
   *
   *    创建一个pairRDD，计算相同key对应值的相加结果
   *
   */
  def reduceByKey_oprator(sc: SparkContext): Unit ={
    val rdd = sc.parallelize(List(("female",1),("male",5),("female",5),("male",2)))
    rdd.reduceByKey((x,y) => x+y).collect().foreach(println)
  }


  /**
   * todo: aggregateByKey
   *    - 作用：在kv对的RDD中，，按key将value进行分组合并，合并时，将每个value和初始值作为seq函数的参数，进行计算，返回的结果作为一个新的kv对，
   *    - 然后再将结果按照key进行合并，最后将每个分组的value传递给combine函数进行计算
   *    （先将前两个value进行计算，将返回结果和下一个value传给combine函数，以此类推），将key与计算结果作为一个新的kv对输出。
   *
   * todo: 参数描述
   *    - zeroValue：给每一个分区中的每一个key一个初始值； 相当于求数组最大值 max的初始值，用来迭代比较
   *    - seqOp：函数用于在每一个分区中用初始值逐步迭代value； 迭代分区中的每一个值
   *    - combOp：函数用于合并每个分区中的结果。 综合每一个分区中 key 相同的 值
   *    - seqOp  combOp 都是迭代 处理同样的key
   *
   *
   * todo: aggregateByKey()()使用了函数柯里化
   *   存在两个参数列表
   *  1）第一个参数列表表示分区内计算时的初始值（零值）
   *  2）第二个参数列表需要传两个参数：
   *     1.第一个参数表示分区内计算规则
   *    2.第二个参数表示分区间计算规则
   *
   *
   *    创建一个pairRDD，取出每个分区相同key对应值的最大值，然后相加
   */
  def aggregateByKey_oprator(sc: SparkContext): Unit ={
    val rdd = sc.parallelize(List(("a",3),("a",2),("c",4),("b",3),("c",6),("c",8)),2)
    // zeroValue 每个分区起始迭代值； 分区内 math.max(_,_) 迭代; 分区间 _+_ 综合
    val agg = rdd.aggregateByKey(0)(math.max(_,_),_+_) // 取出每个分区相同key对应值的最大值，然后相加
  }

  /**
   * todo: foldByKey
   *    - (zeroValue: V)(func: (V, V) => V): RDD[(K, V)]
   *    -  作用：aggregateByKey的简化操作，seqop和combop即分区内和分区间的处理相同
   *
   *    创建一个pairRDD，计算相同key对应值的相加结果
   * @param sc
   */
  def foldByKey_oprator(sc: SparkContext): Unit ={
    // 3 个分区， 先分区内 累加； 再分区间 累加
    val rdd = sc.parallelize(List((1,3),(1,2),(1,4),(2,3),(3,6),(3,8)),3)
    val agg = rdd.foldByKey(0)(_+_).collect().foreach(println)
  }

  /**
   * todo: combineByKey
   *    - (createCombiner: V => C, mergeValue: (C, V) => C, mergeCombiners: (C, C) => C)
   *    - 对相同K，把V合并成一个集合
   *
   * todo: 参数描述：
   *    - createCombiner: combineByKey() 、（创建初始规则）会遍历分区中的所有元素，因此每个元素的键要么还没有遇到过，
   *      要么就和之前的某个元素的键相同。如果这是一个新的元素,
   *      combineByKey()会使用一个叫作createCombiner()的函数来创建那个键对应的累加器的初始值
   *    - mergeValue: 如果这是一个在处理当前分区之前已经遇到的键，它会使用mergeValue()方法将该键的累加器对应的当前值与这个新的值进行合并
   *    - mergeCombiners: 由于每个分区都是独立处理的， 因此对于同一个键可以有多个累加器。如果有两个或者更多的分区都有对应同一个键的累加器，
   *      就需要使用用户提供的 mergeCombiners() 方法将各个分区的结果进行合并。
   *
   *  创建一个pairRDD，根据key计算每种key的均值。（先计算每个key出现的次数以及可以对应值的总和，再相除得到结果）
   * @param sc
   */
  def combineByKey_oprator(sc: SparkContext): Unit ={
    // 2个分区
    val input = sc.parallelize(Array(("a", 88), ("b", 95), ("a", 91), ("b", 93), ("a", 95), ("b", 98)),2)
    // 将相同key对应的值相加，同时记录该key出现的次数，放入一个二元组
    //mergeValue (acc:(Int,Int),v)=>(acc._1+v,acc._2+1)
    //mergeCombiners (acc1:(Int,Int),acc2:(Int,Int))=>(acc1._1+acc2._1,acc1._2+acc2._2)
    val combine = input.combineByKey((_,1),(acc:(Int,Int),v)=>(acc._1+v,acc._2+1),(acc1:(Int,Int),acc2:(Int,Int))=>(acc1._1+acc2._1,acc1._2+acc2._2))
    // 计算平均值
    val result = combine.map{case (key,value) => (key,value._1/value._2.toDouble)}
    result.collect()
  }

  /**
   * todo: sortByKey
   *      - 在一个(K,V)的RDD上调用，K必须实现Ordered接口，返回一个按照key进行排序的(K,V)的RDD
   *
   *      创建一个pairRDD，按照key的正序和倒序进行排序
   */
  def sortByKey_oprator(sc: SparkContext): Unit = {
    val rdd = sc.parallelize(Array((3,"aa"),(6,"cc"),(2,"bb"),(1,"dd")))
    // 按照key的正序
    rdd.sortByKey(true).collect()
  }

  /**
   * todo: mapValues
   *    - 针对于(K,V)形式的类型只对V进行操作
   *
   *    创建一个pairRDD，并将value添加字符串"|||"
   * @param sc
   */
  def mapValues_oprator(sc: SparkContext): Unit ={
    val rdd3 = sc.parallelize(Array((1,"a"),(1,"d"),(2,"b"),(3,"c")))
    // 对value添加字符串"|||"
    rdd3.mapValues(_+"|||").collect().foreach(println)
  }

  /**
   * todo: join
   *    - join(otherDataset, [numTasks])
   *    - 作用：在类型为(K,V)和(K,W)的RDD上调用，返回一个相同key对应的所有元素对在一起的(K,(V,W))的RDD
   *
   *    创建两个pairRDD，并将key相同的数据聚合到一个元组。
   * @param sc
   */
  def join_oprator(sc: SparkContext): Unit ={
    val rdd = sc.parallelize(Array((1,"a"),(2,"b"),(3,"c")))
    val rdd1 = sc.parallelize(Array((1,4),(2,5),(3,6)))
    rdd.join(rdd1).collect().foreach(println)
  }

  /**
   * todo: cogroup
   *    -  cogroup(otherDataset, [numTasks])
   *    - 在类型为(K,V)和(K,W)的RDD上调用，返回一个(K,(Iterable<V>,Iterable<W>))类型的RDD
   *
   *    创建两个pairRDD，并将key相同的数据聚合到一个迭代器。
   * @param sc
   */
  def cogroup_oprator(sc: SparkContext): Unit ={
    val rdd = sc.parallelize(Array((1,"a"),(2,"b"),(3,"c")))
    val rdd1 = sc.parallelize(Array((1,4),(2,5),(3,6)))
    rdd.cogroup(rdd1).collect().foreach(println)
  }


  /*******************************************************************Action 算子*****************************************************************/

  /**
   * todo: reduce
   *    - 通过func函数聚集RDD中的所有元素，先聚合分区内数据，再聚合分区间数据。
   *
   *    创建一个RDD，将所有元素聚合得到结果。
   * @param sc
   */
  def reduce_oprator(sc: SparkContext): Unit ={
    val rdd1 = sc.makeRDD(1 to 10,2)
    rdd1.reduce(_+_)

    val rdd2 = sc.makeRDD(Array(("a",1),("a",3),("c",3),("d",5)))
    rdd2.reduce((x,y)=>(x._1 + y._1,x._2 + y._2))
  }

  /**
   * todo: collect
   *    - 在驱动程序中，以数组的形式返回数据集的所有元素。
   *
   *    创建一个RDD，并将RDD内容收集到Driver端打印
   * @param sc
   */
  def collect_oprator(sc: SparkContext): Unit ={
    val rdd = sc.parallelize(1 to 10)
    rdd.collect.foreach(println)
  }

  /**
   * todo: count
   *    - 返回RDD中元素的个数
   *
   *    创建一个RDD，统计该RDD的条数
   * @param sc
   */
  def count_oprator(sc: SparkContext): Unit ={
    val rdd = sc.parallelize(1 to 10)
    rdd.count
  }

  /**
   * todo: first
   *    - 返回RDD中的第一个元素
   *
   *    创建一个RDD，返回该RDD中的第一个元素
   * @param sc
   */
  def first_oprator(sc: SparkContext): Unit ={
    val rdd = sc.parallelize(1 to 10).first()
  }

  /**
   * todo: take
   *      - 返回一个由RDD的前n个元素组成的数组
   *
   *      创建一个RDD，统计该RDD的条数
   * @param sc
   */
  def take_oprator(sc: SparkContext): Unit ={
    val rdd = sc.parallelize(Array(2,5,4,6,8,3)).take(3)
  }

  /**
   * todo: takeOrdered
   *    - 返回该RDD排序后的前n个元素组成的数组
   *
   *    创建一个RDD，统计该RDD的条数
   * @param sc
   */
  def takeOrdered_oprator(sc: SparkContext): Unit ={
    val rdd = sc.parallelize(Array(2,5,4,6,8,3)).takeOrdered(3)
  }

  /**
   * todo: aggregate
   *    -  参数：(zeroValue: U)(seqOp: (U, T) ⇒ U, combOp: (U, U) ⇒ U)
   *    - aggregate函数将每个分区里面的元素通过seqOp和初始值进行聚合，
   *    然后用combine函数将每个分区的结果和初始值(zeroValue)进行combine操作。
   *    这个函数最终返回的类型不需要和RDD中元素类型一致。
   *
   *    创建一个RDD，将所有元素相加得到结果
   * @param sc
   */
  def aggregate_oprator(sc: SparkContext): Unit ={
    var rdd1 = sc.makeRDD(1 to 10,2)
    rdd1.aggregate(0)(_+_,_+_) // aggregateBykey 一样， 只是这个是 action 算子
  }

  /**
   * todo: fold
   *    - 折叠操作，aggregate的简化操作，seqop和combop一样。
   *
   *    创建一个RDD，将所有元素相加得到结果
   * @param sc
   */
  def fold_oprator(sc: SparkContext): Unit ={
    var rdd1 = sc.makeRDD(1 to 10,2).fold(0)(_+_)
  }

  /**
   * todo: saveAsTextFile 将数据集的元素以textfile的形式保存到HDFS文件系统或者其他支持的文件系统，对于每个元素，Spark将会调用toString方法，将它装换为文件中的文本
   *      - saveAsSequenceFile 将数据集中的元素以Hadoop sequencefile的格式保存到指定的目录下，可以使HDFS或者其他Hadoop支持的文件系统。
   *      - saveAsObjectFile 用于将RDD中的元素序列化成对象，存储到文件中
   * @param sc
   */
  def saveAsTextFile_oprator(sc: SparkContext): Unit ={
    sc.makeRDD(1 to 10,2).saveAsTextFile("data/rdd.txt")
  }

  /**
   * todo:countByKey
   *    - 针对(K,V)类型的RDD，返回一个(K,Int)的map，表示每一个key对应的元素个数
   *
   *    创建一个PairRDD，统计每种key的个数
   * @param sc
   */
  def countByKey_oprator(sc: SparkContext): Unit ={
    val rdd = sc.parallelize(List((1,3),(1,2),(1,4),(2,3),(3,6),(3,8)),3).countByKey()
  }

  /**
   * todo: foreach
   *    - 在数据集的每一个元素上，运行函数func进行更新。
   *
   *    创建一个RDD，对每个元素进行打印
   * @param sc
   */
  def foreach_oprator(sc: SparkContext): Unit ={
    var rdd = sc.makeRDD(1 to 5,2).foreach(println(_))
  }


  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setMaster("local").setAppName("learn")
    val sc = new SparkContext(sparkConf)
    cogroup_oprator(sc)
    sc.stop()

  }


}


