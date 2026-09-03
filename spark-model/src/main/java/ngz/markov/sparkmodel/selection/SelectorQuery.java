package ngz.markov.sparkmodel.selection;

public record SelectorQuery(ModelSelector.SelectorType type, String value) {
    public SelectorQuery {
        if (type == null || value == null || value.isBlank()) {
            throw new InvalidModelSelectorException("Type and value must not be null or blank");
        }
    }
}
