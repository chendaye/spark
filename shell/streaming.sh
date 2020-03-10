export HADOOP_CONF_DIR=/home/hadoop/app/hadoop-2.6.0-cdh5.7.0/etc/hadoop/
spark-submit --master yarn \
--name ImoocStatStreamingApp \
--packages org.apache.spark:spark-streaming-kafka-0-8_2.11:2.2.0 \
--class com.imooc.spark.project.spark.ImoocStatStreamingApp \
--num-executors 1 --executor-cores 2 --executor-memory 512M \
/home/hadoop/lib/sparktrain-1.0.jar \
hadoop000:2181 test streamingtopic 1
