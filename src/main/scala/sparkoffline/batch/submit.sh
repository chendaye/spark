#!/bin/bash
spark-submit \
--class sparkoffline.batch.LogETLSubmit\
--master yarn \
--name LOGETL \
--conf "spark.serializer=org.apache.spark.serializer.KryoSerializer"
--packages cz.mallat.uasparser:uasparser:0.6.2 \
--jar $(echo $HBASE_HOME/lib/*.jar | tr ' ' ',') \
**.jar   20190130

# --packages groupId:artifactId:version  从pom仓库中下载依赖包
# --jar $(echo $HBASE_HOME/lib/*.jar | tr ' ' ',')  导入HBase相关的包

# 缺什么，补什么