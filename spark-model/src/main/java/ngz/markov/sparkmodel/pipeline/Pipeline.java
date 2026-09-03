package ngz.markov.sparkmodel.pipeline;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import ngz.markov.sparkmodel.execution.ExecutionObserver;
import ngz.markov.sparkmodel.execution.ModelExecutionResult;
import ngz.markov.sparkmodel.execution.ModelExecutor;
import ngz.markov.sparkmodel.execution.context.PipelineContext;
import ngz.markov.sparkmodel.execution.context.PipelineContextFactory;
import ngz.markov.sparkmodel.model.SparkModel;
import ngz.markov.sparkmodel.planning.ExecutionPlan;
import ngz.markov.sparkmodel.planning.ExecutionPlanner;
import ngz.markov.sparkmodel.registry.ModelRegistry;
import ngz.markov.sparkmodel.selection.ModelSelector;
import ngz.markov.sparkmodel.selection.SelectorQuery;
import org.apache.spark.sql.SparkSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Pipeline {

    private static final Logger LOG = LoggerFactory.getLogger(Pipeline.class);
    private final ModelRegistry modelRegistry;
    private final PipelineContextFactory pipelineContextFactory;
    private final SelectorQuery selectorQuery;
    private final List<ExecutionObserver> observers;

    public Pipeline(
            ModelRegistry modelRegistry,
            PipelineContextFactory pipelineContextFactory,
            SelectorQuery selectorQuery,
            List<ExecutionObserver> observers) {
        this.modelRegistry = modelRegistry;
        this.pipelineContextFactory = pipelineContextFactory;
        this.selectorQuery = selectorQuery;
        this.observers = List.copyOf(observers);
    }

    public PipelineResult run() {

        try (SparkSession spark = SparkSession.builder().getOrCreate()) {

            // 0. provide context along with spark's
            PipelineContext pipelineContext = this.pipelineContextFactory.create(spark);
            LOG.info("Context {}", pipelineContext);

            // 0. Instantiate executor
            ModelExecutor executor = new ModelExecutor(pipelineContext, observers);

            // 1. Get selected models
            Set<SparkModel> selectedModels =
                    ModelSelector.select(modelRegistry.getModels(), selectorQuery);

            // 2. Build execution plan
            ExecutionPlan plan = ExecutionPlanner.plan(modelRegistry, selectedModels);
            LOG.info("Execution Plan : {}", plan);

            // 3. Execute selected models
            List<ModelExecutionResult> results =
                    executor.execute(plan.getExecutionSequence(), selectedModels);

            LOG.info("Successfully carried out plan");

            return buildPipelineResult(results, selectedModels);

        } catch (IOException e) {
            throw new PipelineExecutionException(
                    "Failed to execute pipeline due to IO error: " + e.getMessage(), e);
        } catch (Exception e) {
            LOG.error("Unexpected error during pipeline execution", e);
            throw new PipelineExecutionException(
                    "Unexpected error during pipeline execution: " + e.getMessage(), e);
        }
    }

    public static PipelineResult buildPipelineResult(
            List<ModelExecutionResult> results, Set<SparkModel> selectedModels) {

        Set<String> selectedModelSimpleNames =
                selectedModels.stream()
                        .map(model -> model.getClass().getSimpleName())
                        .collect(Collectors.toSet());

        List<ModelExecutionResult> filteredResults =
                results.stream()
                        .filter(
                                r ->
                                        selectedModelSimpleNames.contains(
                                                r.model().getClass().getSimpleName()))
                        .toList();

        boolean allSuccess = filteredResults.stream().allMatch(ModelExecutionResult::success);
        return new PipelineResult(List.copyOf(filteredResults), allSuccess);
    }
}
