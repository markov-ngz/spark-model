package ngz.markov.sparkmodel.planning;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import ngz.markov.sparkmodel.model.SparkModel;

public final class ExecutionPlan {

    private final List<SparkModel> executionSequence;
    private final Set<Edge> edges;

    public record Edge(SparkModel from, SparkModel to) {}

    public ExecutionPlan(List<SparkModel> executionSequence, Set<Edge> edges) {
        this.executionSequence = List.copyOf(executionSequence);
        this.edges = Collections.unmodifiableSet(new LinkedHashSet<>(edges));
    }

    /** Ordered sequence to be executed. */
    public List<SparkModel> getExecutionSequence() {
        return executionSequence;
    }

    public Set<Edge> getEdges() {
        return edges;
    }

    @Override
    public String toString() {
        return "ExecutionPlan{Sequence=%s, EdgesCount=%d}"
                .formatted(
                        executionSequence.stream().map(m -> m.getClass().getSimpleName()).toList(),
                        edges.size());
    }
}
