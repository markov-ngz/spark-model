package ngz.markov.sparkmodel.registry;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import ngz.markov.sparkmodel.model.Materialization;
import ngz.markov.sparkmodel.model.SparkModel;

public class ModelRegistry {

    final Map<Class<? extends SparkModel>, SparkModel> models = new LinkedHashMap<>();

    public ModelRegistry() {}

    public void register(SparkModel model) {
        models.put(model.getClass(), model);
    }

    public SparkModel get(Class<? extends SparkModel> modelClazz) {
        return models.get(modelClazz);
    }

    public Collection<SparkModel> getModels() {
        return models.values();
    }

    public boolean contains(Class<? extends SparkModel> modelClazz) {
        return models.containsKey(modelClazz);
    }

    public boolean containsValue(SparkModel model) {
        return models.containsValue(model);
    }

    public void validateAll() {
        for (SparkModel model : models.values()) {

            // 1. Check if a model dependency is properly registered
            for (Class<? extends SparkModel> dep : model.getDeps()) {
                if (!models.containsKey(dep)) {
                    throw new InvalidRegistryException(
                            "Model '%s' depends on '%s' which is not registered."
                                    .formatted(
                                            model.getClass().getSimpleName(), dep.getSimpleName()));
                }
            }

            // 2. Check if a model materialized as table overrides write method
            validateWriteCapabilities(model);
        }
    }

    private void validateWriteCapabilities(SparkModel model) {
        Materialization materialization = model.getMaterialization();

        if (materialization == Materialization.TABLE) {
            boolean overridesWriteToTable = isMethodOverridden(model.getClass(), "writeToTable");
            boolean overridesWrite = isMethodOverridden(model.getClass(), "write");

            if (!overridesWriteToTable && !overridesWrite) {
                throw new InvalidRegistryException(
                        "Validation Error: Model '%s' specifies Materialization.TABLE but does not override writeToTable() or write()."
                                .formatted(model.getClass().getCanonicalName()));
            }
        } else if (materialization == Materialization.FILE) {
            if (model.getLocation() == null || model.getLocation().isBlank()) {
                throw new InvalidRegistryException(
                        "Validation Error: Model '%s' specifies Materialization.FILE but location is null or empty."
                                .formatted(model.getClass().getCanonicalName()));
            }
        }
    }

    private boolean isMethodOverridden(Class<?> clazz, String methodName) {
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.getName().equals(methodName)
                    && !method.getDeclaringClass().equals(SparkModel.class)) {
                return true;
            }
        }
        return false;
    }
}
