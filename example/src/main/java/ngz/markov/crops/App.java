package ngz.markov.crops;

import ngz.markov.crops.models.CropsModelCatalog;
import ngz.markov.sparkmodel.execution.ModelExecutionResult;
import ngz.markov.sparkmodel.execution.ModelExecutor;
import ngz.markov.sparkmodel.execution.context.PipelineContext;
import ngz.markov.sparkmodel.lineage.MermaidLineageRenderer;
import ngz.markov.sparkmodel.model.SparkModel;
import ngz.markov.sparkmodel.pipeline.Pipeline;
import ngz.markov.sparkmodel.pipeline.PipelineExecutionException;
import ngz.markov.sparkmodel.planning.ExecutionPlan;
import ngz.markov.sparkmodel.planning.ExecutionPlanner;
import ngz.markov.sparkmodel.registry.ModelRegistry;
import ngz.markov.sparkmodel.registry.ModelRegistryFactory;
import ngz.markov.sparkmodel.selection.ModelSelector;
import ngz.markov.sparkmodel.selection.SelectorQuery;
import ngz.markov.sparkmodel.selection.SelectorQueryFactory;
import org.apache.spark.sql.SparkSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.reflect.internal.Mode;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class App {

    private static final Logger LOG = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {

        try (SparkSession spark = SparkSession.builder().getOrCreate()) {

            // 1. Setup of the core objects needed : registry, model selection, executor

            // registry
            ModelRegistry modelRegistry =
                    ModelRegistryFactory.createDefault(
                            "./model_registry.yaml",
                            CropsModelCatalog.MODELZ);
            // selected models
            SelectorQuery selectorQuery =
                    SelectorQueryFactory.createFromString(
                            "model:StgCotationProteoOleagineuxModel",
                            ":");
            Set<SparkModel> selectedModels = ModelSelector.select(modelRegistry.getModels(),selectorQuery);

            // executor to process models
            PipelineContext pipelineContext = new PipelineContext(spark,null, Map.of(),false);
            ModelExecutor executor = new ModelExecutor(pipelineContext,List.of());


            // 2. Plan the execution
            ExecutionPlan plan = ExecutionPlanner.plan(modelRegistry,selectedModels);

            LOG.info(plan.toString());

            // (Optional) display the plan as a mermaid chart
            String mermaidChart = MermaidLineageRenderer.render(plan);
            System.out.println(mermaidChart);

            // 3. Execute
            List<ModelExecutionResult> results = executor.execute(plan.getExecutionSequence(),selectedModels);

            LOG.info(results.toString());


        } catch (IOException e) {
            throw new PipelineExecutionException(
                    "Failed to execute pipeline due to IO error: " + e.getMessage(), e);
        } catch (Exception e) {
            LOG.error("Unexpected error during pipeline execution", e);
            throw new PipelineExecutionException(
                    "Unexpected error during pipeline execution: " + e.getMessage(), e);
        }

    }
}
