#!/bin/bash

# Change dir
cd /opt/spark-apps/predictor/target/scala-2.11

# Submit application to spark worker
/usr/local/spark/bin/spark-submit \
  --class "com.fivegmedia.predictor.DLClassifier" \
  --master spark://localhost:7077 \
  --supervise \
  Predictor-assembly-1.0-SNAPSHOT.jar ${CNO_OUTPUT_OPERATION} ${CNO_KAFKA_HOST} > /opt/spark-apps/predictor/logs/predictor.log

# Keep container running
tail -f /dev/null