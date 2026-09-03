package ngz.markov.sparkmodel.lineage;

import java.util.List;
import ngz.markov.sparkmodel.model.Layer;
import ngz.markov.sparkmodel.model.SparkModel;
import ngz.markov.sparkmodel.planning.ExecutionPlan;

/** Render lineage of the models as a mermaid chart. */
public class MermaidLineageRenderer {

    public static String render(ExecutionPlan plan) {
        // 1. Get the order of nodes
        List<SparkModel> nodes = plan.getExecutionSequence();
        List<ExecutionPlan.Edge> edges = plan.getEdges().stream().toList();

        // 2. Define the base graph structure
        StringBuilder mermaidGraph = new StringBuilder("flowchart LR;\n");

        // NodeType colors: Bronze -> Seed, Silver -> Staging/Intermediate, Gold -> Mart
        mermaidGraph.append(
                "classDef bronzeClass fill:#CD7F32,stroke:#8B4513,stroke-width:2px,rx:10,ry:10,color:#000000;\n");
        mermaidGraph.append(
                "classDef silverClass fill:#C0C0C0,stroke:#707070,stroke-width:2px,rx:10,ry:10,color:#000000;\n");
        mermaidGraph.append(
                "classDef goldClass fill:#FFD700,stroke:#B8860B,stroke-width:2px,rx:10,ry:10,color:#000000;\n");
        mermaidGraph.append(
                "classDef sourceClass fill:#87CEEB,stroke:#4682B4,stroke-width:2px,rx:10,ry:10,color:#000000;\n");
        mermaidGraph.append(
                "classDef seedClass fill:#D8E9E6,stroke:#4682B4,stroke-width:2px,rx:10,ry:10,color:#000000;\n");
        mermaidGraph.append("classDef edgeClass stroke:#333,stroke-width:2px;\n");

        // 3. Add nodes and edges

        for (SparkModel node : nodes) {
            mermaidGraph.append(formatNode(node)).append("\n");
        }

        for (ExecutionPlan.Edge edge : edges) {
            mermaidGraph.append(formatEdge(edge)).append("\n");
        }

        return mermaidGraph.toString();
    }

    private static String formatNode(SparkModel node) {
        String nodeId = node.getClass().getSimpleName().replaceAll("[^A-Za-z0-9_]", "");

        String label = escapeMermaid(node.getClass().getSimpleName());
        String location = escapeMermaid(node.getLocation());

        String materialization = node.getMaterialization().name().toLowerCase();

        String colorClass = getColorClass(node.getLayer());

        // GitHub/Mermaid supports a single tooltip argument for `click`.
        String tooltip =
                escapeMermaid(
                        "location: '" + location + "'<br> materialized: '" + materialization + "'");

        return String.format(
                "    %s[\"%s\"]:::%s;\n" + "    click %s \"#\" \"%s\";",
                nodeId, label, colorClass, nodeId, tooltip);
    }

    private static String escapeMermaid(Object value) {
        if (value == null) {
            return "null";
        }

        return String.valueOf(value)
                .replace("\\", "\\\\")
                .replace("\"", "&quot;")
                .replace("\n", " ")
                .replace("\r", " ");
    }

    private static String getColorClass(Layer layer) {
        if (layer == null) {
            throw new IllegalArgumentException("Invalid nodeType provided");
        }

        return switch (layer) {
            case SEED -> "seedClass";
            case SOURCE -> "sourceClass";
            case STAGING -> "bronzeClass";
            case INTERMEDIATE -> "silverClass";
            case MART -> "goldClass";
            default -> throw new IllegalArgumentException("Unsupported nodeType: " + layer);
        };
    }

    private static String formatEdge(ExecutionPlan.Edge edge) {
        String fromId = edge.from().getClass().getSimpleName().replaceAll("[^A-Za-z0-9_]", "");

        String toId = edge.to().getClass().getSimpleName().replaceAll("[^A-Za-z0-9_]", "");

        return String.format("    %s --> %s;", fromId, toId);
    }
}
