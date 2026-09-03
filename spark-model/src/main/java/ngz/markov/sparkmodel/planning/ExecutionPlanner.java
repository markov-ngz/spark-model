package ngz.markov.sparkmodel.planning;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import ngz.markov.sparkmodel.model.Materialization;
import ngz.markov.sparkmodel.model.SparkModel;
import ngz.markov.sparkmodel.registry.ModelRegistry;

public class ExecutionPlanner {

    /** Generate an execution plan to have an ordered list of models. */
    public static ExecutionPlan plan(ModelRegistry registry, Set<SparkModel> selectedModels) {
        List<SparkModel> orderedSequence = new ArrayList<>();
        Set<ExecutionPlan.Edge> edges = new LinkedHashSet<>();

        Set<Class<? extends SparkModel>> visited = new HashSet<>();
        Set<Class<? extends SparkModel>> inStack = new HashSet<>();

        for (SparkModel model : selectedModels) {
            visitNode(registry, model, selectedModels, visited, inStack, orderedSequence, edges);
        }

        return new ExecutionPlan(orderedSequence, edges);
    }

    private static void visitNode(
            ModelRegistry registry,
            SparkModel current,
            Set<SparkModel> selectedModels,
            Set<Class<? extends SparkModel>> visited,
            Set<Class<? extends SparkModel>> inStack,
            List<SparkModel> orderedSequence,
            Set<ExecutionPlan.Edge> edges) {

        Class<? extends SparkModel> clazz = current.getClass();

        // 1. Check circular dependencies
        if (inStack.contains(clazz)) {
            throw new InvalidModelExecutionOrderException(
                    "Circular dependency detected in model DAG at: " + clazz.getSimpleName());
        }

        // 2. If already visited skip
        if (visited.contains(clazz)) {
            return;
        }

        inStack.add(clazz);

        // 3. Should the model be recomputed from scratch ( cannot be read )
        boolean isUnselectedAndMaterialized =
                current.getMaterialization() != Materialization.EPHEMERAL
                        && !selectedModels.contains(current);

        if (!isUnselectedAndMaterialized) {
            for (Class<? extends SparkModel> depClass : current.getDeps()) {
                SparkModel depModel = registry.get(depClass);

                // Add the edge
                edges.add(new ExecutionPlan.Edge(depModel, current));

                // Recursive call
                visitNode(
                        registry,
                        depModel,
                        selectedModels,
                        visited,
                        inStack,
                        orderedSequence,
                        edges);
            }
        }

        inStack.remove(clazz);
        visited.add(clazz);

        // 4. Add node
        orderedSequence.add(current);
    }
}
