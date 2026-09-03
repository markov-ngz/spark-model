package ngz.markov.sparkmodel.registry;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import lombok.Data;
import ngz.markov.sparkmodel.model.FileFormat;
import ngz.markov.sparkmodel.model.Materialization;
import ngz.markov.sparkmodel.model.SparkModel;
import tools.jackson.core.TokenStreamFactory;
import tools.jackson.core.json.JsonFactory;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.dataformat.yaml.YAMLFactory;

public class SparkModelSpecReader {

    public static SparkModelRegistrySpec readModelRegistry(String filePath) {

        // 1. Choose factory with a switch if file name is either .json or .yaml else thrown unknown
        // or not implemented
        TokenStreamFactory factory;

        if (filePath.toLowerCase().endsWith(".json")) {
            factory = new JsonFactory();
        } else if (filePath.toLowerCase().endsWith(".yaml")
                || filePath.toLowerCase().endsWith(".yml")) {
            factory = new YAMLFactory();
        } else {
            throw new IllegalArgumentException(
                    "Unsupported file extension. Only .json, .yaml, or .yml are supported: "
                            + filePath);
        }

        ObjectMapper mapper = new ObjectMapper(factory);
        return mapper.readValue(new File(filePath), SparkModelRegistrySpec.class);
    }

    @Data
    public static class SparkModelRegistrySpec {
        private List<SparkModelSpec> models;

        public List<SparkModelSpec> getModels() {
            return models;
        }

        public void setModels(List<SparkModelSpec> models) {
            this.models = models;
        }
    }

    @Data
    public static class SparkModelSpec {
        private String name;
        private String materialized;
        private String location;

        @JsonProperty("file_format")
        private String fileFormat;

        @JsonProperty("read_options")
        private Map<String, String> readOptions;

        @JsonProperty("write_options")
        private Map<String, String> writeOptions;
    }

    public static SparkModel instantiateModel(
            SparkModelSpec sparkModelSpec, List<Class<? extends SparkModel>> modelsClass) {
        try {
            // A. Resolve Fully Qualified Class Name (FQCN)
            String className = sparkModelSpec.getName();

            Class<? extends SparkModel> specModelClass = null;
            for (Class<? extends SparkModel> modelClass : modelsClass) {
                if (className.equals(modelClass.getSimpleName())) {
                    specModelClass = modelClass;
                }
            }

            if (specModelClass == null) {
                throw new InvalidRegistryException(
                        "Specified configuration model '"
                                + className
                                + "' do not match any model class simpleName registered ");
            }

            // C. Invoke static .builder() method on the target class
            Method builderMethod = specModelClass.getMethod("builder");
            Object builderInstance = builderMethod.invoke(null); // static method, so target is null

            // D. Map fields from SparkModelSpec using Reflection on the Builder methods

            // Map 'materialization' (assuming it expects an Enum or String)
            if (sparkModelSpec.getMaterialized() != null) {
                invokeBuilderMethod(
                        builderInstance,
                        "materialization",
                        Materialization.valueOf(sparkModelSpec.getMaterialized().toUpperCase()));
            }

            // Map 'location'
            invokeBuilderMethod(builderInstance, "location", sparkModelSpec.getLocation());

            // Map 'readOptions'
            invokeBuilderMethod(builderInstance, "readOptions", sparkModelSpec.getReadOptions());

            // Map 'writeOptions'
            invokeBuilderMethod(builderInstance, "writeOptions", sparkModelSpec.getWriteOptions());

            // Map 'fileFormat' (assuming it expects an Enum or String)
            if (sparkModelSpec.getFileFormat() != null) {
                invokeBuilderMethod(
                        builderInstance,
                        "fileFormat",
                        FileFormat.valueOf(sparkModelSpec.getFileFormat().toUpperCase()));
            }

            // E. Invoke .build() on the populated builder
            Method buildMethod = builderInstance.getClass().getMethod("build");
            Object builtModel = buildMethod.invoke(builderInstance);

            return (SparkModel) builtModel;

        } catch (Exception e) {
            throw new InvalidSparkModelSpecException(
                    "Failed to instantiate SparkModel for spec: " + sparkModelSpec.getName(), e);
        }
    }

    /** Helper method to dynamically invoke setter methods on a builder instance. */
    private static void invokeBuilderMethod(Object builder, String methodName, Object value) {
        if (value == null) {
            return;
        }

        // Find matching method on the builder class based on argument type
        Method[] methods = builder.getClass().getMethods();
        for (Method method : methods) {
            if (method.getName().equals(methodName) && method.getParameterCount() == 1) {
                Class<?> paramType = method.getParameterTypes()[0];
                if (paramType.isAssignableFrom(value.getClass())) {
                    try {
                        method.invoke(builder, value);
                        return;

                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new InvalidSparkModelSpecException(
                                "Failed to invoke builder method :"
                                        + methodName
                                        + " for ClassModel {}"
                                        + value.getClass().getSimpleName(),
                                e);
                    }
                }
            }
        }
        throw new InvalidSparkModelSpecException(
                "No suitable builder method found: "
                        + methodName
                        + "("
                        + value.getClass().getSimpleName()
                        + ")");
    }
}
