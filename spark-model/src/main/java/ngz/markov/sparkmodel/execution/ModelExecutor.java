package ngz.markov.sparkmodel.execution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import ngz.markov.sparkmodel.execution.context.PipelineContext;
import ngz.markov.sparkmodel.model.Materialization;
import ngz.markov.sparkmodel.model.SparkModel;
import ngz.markov.sparkmodel.planning.InvalidModelExecutionOrderException;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

public class ModelExecutor {

    private final PipelineContext pipelineContext;
    private final List<ExecutionObserver> observers;

    public ModelExecutor(PipelineContext pipelineContext, List<ExecutionObserver> observers) {
        this.pipelineContext = pipelineContext;
        this.observers = observers != null ? observers : List.of();
    }

    public List<ModelExecutionResult> execute(
            List<SparkModel> sequencedModels, Set<SparkModel> selectedModels) {

        Map<Class<? extends SparkModel>, Dataset<Row>> datasetCache = new HashMap<>();

        List<ModelExecutionResult> modelExecutionResults = new ArrayList<>();

        for (SparkModel model : sequencedModels) {
            Class<? extends SparkModel> modelClass = model.getClass();
            notifyStart(model);
            long startTime = System.currentTimeMillis();

            try {
                Dataset<Row> resolvedDataset;

                // 2. Read if not selected and not ephemeral
                if (model.getMaterialization() != Materialization.EPHEMERAL
                        && !selectedModels.contains(model)) {
                    resolvedDataset = model.read(pipelineContext);
                    datasetCache.put(modelClass, resolvedDataset);
                    continue;
                }

                // 3.  "Closure" to resolve dependencies from cache
                DependencyResolver upstream =
                        depClass -> {
                            Dataset<Row> depDs = datasetCache.get(depClass);
                            if (depDs == null) {
                                throw new InvalidModelExecutionOrderException(
                                        "Model dependency do not exists in cache "
                                                + depClass.getSimpleName());
                            }
                            return depDs;
                        };

                // 4. Process
                resolvedDataset = model.process(pipelineContext, upstream);

                // 5. Write
                ModelExecutionResult modelExecutionResult;
                if (!pipelineContext.dryRun()) {
                    ModelWriteResult modelWriteResult =
                            model.write(resolvedDataset, pipelineContext);
                    long duration = System.currentTimeMillis() - startTime;
                    modelExecutionResult =
                            new ModelExecutionResult(
                                    model.getClass().getSimpleName(),
                                    modelWriteResult,
                                    duration,
                                    true,
                                    null);
                } else {
                    long duration = System.currentTimeMillis() - startTime;
                    modelExecutionResult =
                            new ModelExecutionResult(
                                    model.getClass().getSimpleName(),
                                    new ModelWriteResult(0, 0, 0, false),
                                    duration,
                                    true,
                                    null);
                }
                // 6. Put dataset to cache
                datasetCache.put(modelClass, resolvedDataset);

                modelExecutionResults.add(modelExecutionResult);

                notifySuccess(modelExecutionResult);

            } catch (Exception e) {
                long duration = System.currentTimeMillis() - startTime;
                notifyFailure(model, e);
                ModelExecutionResult modelExecutionResult =
                        new ModelExecutionResult(
                                model.getClass().getSimpleName(), null, duration, false, e);
                modelExecutionResults.add(modelExecutionResult);
            }
        }
        return modelExecutionResults;
    }

    private void notifyStart(SparkModel model) {
        observers.forEach(o -> o.onModelStart(model));
    }

    private void notifySuccess(ModelExecutionResult result) {
        observers.forEach(o -> o.onModelSuccess(result));
    }

    private void notifyFailure(SparkModel model, Throwable t) {
        observers.forEach(o -> o.onModelFailure(model, t));
    }
}
