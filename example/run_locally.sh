
source $HOME/Documents/setup/python_env/spark_env/bin/activate


APP_JAR=target/crops-0.1.0.jar

spark-submit \
  --master local[*] \
  --conf spark.app.name=$JOB_NAME \
  --conf spark.driver.host=127.0.0.1 \
  --conf spark.driver.bindAddress=127.0.0.1 \
  --conf spark.local.ip=127.0.0.1 \
  --conf spark.ui.host=127.0.0.1 \
  --conf spark.ui.bindAddress=127.0.0.1 \
  $APP_JAR