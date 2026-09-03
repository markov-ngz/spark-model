package ngz.markov.sparkmodel.execution.context;

import java.util.Map;
import org.apache.spark.sql.SparkSession;

public record PipelineContext(
        SparkSession spark,
        LookBackWindow lookBackWindow, // range of historical data to reprocess
        Map<String, String> customConfig, // For any additional dynamic parameters
        boolean dryRun) {}
