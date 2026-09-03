package ngz.markov.sparkmodel.execution.context;

import org.apache.spark.sql.SparkSession;

public interface PipelineContextFactory {
    public PipelineContext create(SparkSession spark);
}
