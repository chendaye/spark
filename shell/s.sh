spark-submit --master local[3] \
--name ImoocStatStreamingApp \
--packages org.apache.spark:spark-streaming-kafka-0-8_2.11:2.2.0 \
--class com.imooc.spark.project.spark.ImoocStatStreamingApp \
--jars $(echo /home/hadoop/app/hbase-1.2.0-cdh5.7.0/lib/*.jar | tr ' ' ',') \
/home/hadoop/lib/sparktrain-1.0.jar \
hadoop000:2181 test streamingtopic 1
