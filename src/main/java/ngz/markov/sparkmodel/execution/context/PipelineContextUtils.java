package ngz.markov.sparkmodel.execution.context;

import static org.apache.spark.sql.functions.col;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

public class PipelineContextUtils {

    public static Dataset<Row> filterByLookbackWindow(
            Dataset<Row> dataset, String datetimeColumn, LookBackWindow lookBackWindow) {
        return dataset.where(
                col(datetimeColumn).between(lookBackWindow.startTime(), lookBackWindow.endTime()));
    }
}
