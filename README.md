# 离线处理

- 统计国家省份的访问量

> 清洗数据（自定义数据源）， 清洗成规定字段的数据
>
> 将 数据 放入Hbase Put对象中 RDD 转化为 （RowKey， Put）， 创建HBase表 然后保存
> RowKey 当前天 拼接特定的字段
>  清洗数据存入HBase

> 从Hbase里面取出Etl数据 转化为RDD进行分析



# 流式处理

