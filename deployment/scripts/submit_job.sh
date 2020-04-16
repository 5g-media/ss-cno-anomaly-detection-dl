#!/bin/bash

cd /opt/predictor
/usr/local/spark-2.3.0-bin-hadoop2.7/bin/spark-submit \
 --class "com.fivegmedia.predictor.DLClassifier" \
 --master spark://localhost:7077 \
 target/scala-2.11/Predictor-assembly-1.0-SNAPSHOT.jar