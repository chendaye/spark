# 手写 WordCount

```bash
conf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("wc")
sc = new SparkContext(conf)
sc.textFile("/input").flatMap(_.split(" ")).map((_,1)).reduceByKey(_+_).saveAsTextFile("/output")
sc.stop()
```

# 手写 TopN

```bash
conf: SparkConf = new SparkConf().setMatser("local[*]").setAppName()

sc = new SparkContext(conf)

sc.textFile("/input).map(_.split(" ")).map(x => (x(0), x(1).toInt)).groupByKey().map({
    case (key, iterator) => {
        iterator.toList.sorted.takeRight(10).map(x => (key, _))
    }
})

sc.stop()
```

# 手写HQl

## 