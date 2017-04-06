#!/bin/bash
# run ADAMpro

#export SPARK_SUBMIT_OPTS=-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005
#$SPARK_HOME/bin/spark-submit --master "$ADAMPRO_MASTER" --driver-memory "$ADAMPRO_MEMORY" --executor-memory "$ADAMPRO_MEMORY" --deploy-mode client --driver-java-options "-Dlog4j.configuration=file:$ADAM_HOME/log4j.xml" --conf "spark.executor.extraJavaOptions=-Dlog4j.configuration=file:$ADAM_HOME/log4j.xml" --driver-java-options "-XX:+UnlockCommercialFeatures -XX:+FlightRecorder" --conf "spark.executor.extraJavaOptions=-XX:+UnlockCommercialFeatures -XX:+FlightRecorder" --class org.vitrivr.adampro.main.Startup $ADAM_HOME/ADAMpro-assembly-0.1.0.jar &

cd $SPARK_HOME
#Launch master
./sbin/start-master.sh
#Launch worker
./sbin/start-slave.sh spark://parquet-demo:7077


# startup
if [[ $1 == "-bash" ]]; then
  /bin/bash
fi

while true; do sleep 60 ; done