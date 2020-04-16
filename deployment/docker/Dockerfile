FROM krailis/spark-docker:spark-2.3.0-scala-2.11.8-sbt-1.1.5

# Set work directory
WORKDIR /opt/spark-apps/predictor

# Copy code
COPY .. /opt/spark-apps/predictor

# Compile code
RUN sbt clean assembly && chmod +x deployment/docker/run.sh

# Expose ports
EXPOSE 4040

ENTRYPOINT ["/bin/sh", "-c"]
CMD ["/opt/spark-apps/predictor/deployment/docker/run.sh"]