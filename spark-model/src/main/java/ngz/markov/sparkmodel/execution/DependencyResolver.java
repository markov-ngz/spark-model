package ngz.markov.sparkmodel.execution;

import ngz.markov.sparkmodel.model.SparkModel;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

public interface DependencyResolver {
    Dataset<Row> get(Class<? extends SparkModel> name);
}
