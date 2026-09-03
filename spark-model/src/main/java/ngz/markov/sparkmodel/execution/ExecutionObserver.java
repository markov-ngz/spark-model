package ngz.markov.sparkmodel.execution;

import ngz.markov.sparkmodel.model.SparkModel;

public interface ExecutionObserver {
    default void onModelStart(SparkModel model) {}

    default void onModelSuccess(ModelExecutionResult result) {}

    default void onModelFailure(SparkModel model, Throwable throwable) {}
}
