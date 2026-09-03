package ngz.markov.sparkmodel.selection;

public class SelectorQueryFactory {

    private SelectorQueryFactory() {}

    public static SelectorQuery createFromString(String selector, String delimiter) {
        if (selector == null || !selector.contains(delimiter)) {
            throw new InvalidModelSelectorException(
                    "Invalid selector format: '%s'. Expected format 'TYPE%sVALUE'"
                            .formatted(selector, delimiter));
        }

        String[] selectorSplit =
                selector.split(
                        delimiter,
                        2); // 2 pour ne pas splitter la valeur si elle contient le délimiteur

        try {
            ModelSelector.SelectorType type =
                    ModelSelector.SelectorType.valueOf(selectorSplit[0].toUpperCase());
            String value = selectorSplit[1];
            return new SelectorQuery(type, value);
        } catch (IllegalArgumentException e) {
            throw new InvalidModelSelectorException(
                    "Unknown selector type: " + selectorSplit[0], e);
        }
    }

    public static SelectorQuery loadSelector(String selector, String delimiter) {
        if (selector == null || !selector.contains(delimiter)) {
            throw new InvalidModelSelectorException(
                    "Invalid selector format: '%s'. Expected format 'TYPE%sVALUE'"
                            .formatted(selector, delimiter));
        }

        String[] selectorSplit =
                selector.split(
                        delimiter,
                        2); // 2 pour ne pas splitter la valeur si elle contient le délimiteur

        try {
            ModelSelector.SelectorType type =
                    ModelSelector.SelectorType.valueOf(selectorSplit[0].toUpperCase());
            String value = selectorSplit[1];
            return new SelectorQuery(type, value);
        } catch (IllegalArgumentException e) {
            throw new InvalidModelSelectorException(
                    "Unknown selector type: " + selectorSplit[0], e);
        }
    }
}
