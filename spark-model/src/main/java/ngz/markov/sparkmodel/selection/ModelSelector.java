package ngz.markov.sparkmodel.selection;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import ngz.markov.sparkmodel.model.SparkModel;

public final class ModelSelector {

    public enum SelectorType {
        MODEL,
        MATERIALIZATION,
        TAG
    }

    private ModelSelector() {}

    public static Set<SparkModel> select(Collection<SparkModel> models, SelectorQuery query) {
        return ModelSelector.filter(query.type(), query.value(), models);
    }

    public static Set<SparkModel> select(
            Collection<SparkModel> models, Map<ModelSelector.SelectorType, String> selectors) {
        return ModelSelector.filterAny(models, selectors);
    }

    /**
     * Filter a collection of models by a selector type and value. Returns a LinkedHashSet to
     * preserve registration order and guarantee uniqueness.
     */
    public static Set<SparkModel> filter(
            SelectorType type, String value, Collection<SparkModel> models) {

        return models.stream()
                .filter(m -> matches(type, value, m))
                .collect(Collectors.toUnmodifiableSet());
    }

    /** Combine multiple selectors — union of all matches. */
    public static Set<SparkModel> filterAny(
            Collection<SparkModel> models, Map<SelectorType, String> selectors) {

        return selectors.entrySet().stream()
                .flatMap(e -> filter(e.getKey(), e.getValue(), models).stream())
                .collect(Collectors.toUnmodifiableSet());
    }

    private static boolean matches(SelectorType type, String value, SparkModel m) {
        return switch (type) {
            case MODEL -> m.getClass().getSimpleName().equalsIgnoreCase(value);
            case MATERIALIZATION -> m.getMaterialization().name().equalsIgnoreCase(value);
            case TAG -> m.getTags().stream().anyMatch(t -> t.equalsIgnoreCase(value));
        };
    }
}
