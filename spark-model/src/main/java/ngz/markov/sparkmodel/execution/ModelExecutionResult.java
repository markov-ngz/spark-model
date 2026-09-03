package ngz.markov.sparkmodel.execution;

import ngz.markov.sparkmodel.model.SparkModel;

/** Model processing execution result ( transfo + write ). */
public record ModelExecutionResult(
        String model, ModelWriteResult metrics, long durationMs, boolean success, Throwable error) {
    public static ModelExecutionResult success(
            SparkModel model, ModelWriteResult metrics, long durationMs) {
        return new ModelExecutionResult(
                model.getClass().getSimpleName(), metrics, durationMs, true, null);
    }

    public static ModelExecutionResult failure(SparkModel model, Throwable error, long durationMs) {
        return new ModelExecutionResult(
                model.getClass().getSimpleName(), null, durationMs, false, error);
    }
}
