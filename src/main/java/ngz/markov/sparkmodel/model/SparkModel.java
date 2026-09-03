package ngz.markov.sparkmodel.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import ngz.markov.sparkmodel.execution.DependencyResolver;
import ngz.markov.sparkmodel.execution.ModelWriteResult;
import ngz.markov.sparkmodel.execution.context.PipelineContext;
import org.apache.spark.sql.DataFrameReader;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

public abstract class SparkModel {

    @Getter protected final List<String> primaryKey;
    @Getter protected final Layer layer;
    @Getter protected final Materialization materialization;
    @Getter protected final List<Class<? extends SparkModel>> deps;
    @Getter protected final String location;
    @Getter protected final FileFormat fileFormat;
    @Getter protected final List<String> tags;
    @Getter protected final Map<String, String> readOptions;
    @Getter protected final Map<String, String> writeOptions;

    protected abstract List<String> defaultPrimaryKey();

    protected abstract Layer defaultNodeType();

    protected abstract Materialization defaultMaterialization();

    protected List<Class<? extends SparkModel>> defaultDeps() {
        return List.of();
    }

    protected List<String> defaultTags() {
        return List.of();
    }

    protected Map<String, String> defaultReadOptions() {
        return Map.of();
    }

    protected Map<String, String> defaultWriteOptions() {
        return Map.of();
    }

    protected SparkModel(
            Layer layer,
            List<String> primaryKey,
            List<Class<? extends SparkModel>> deps,
            Materialization materialization,
            List<String> tags,
            String location,
            FileFormat fileFormat,
            Map<String, String> readOptions,
            Map<String, String> writeOptions) {
        this.layer = layer;
        this.primaryKey = primaryKey;
        this.materialization = materialization;
        this.deps = List.copyOf(deps);
        // Optional fields
        this.tags = List.copyOf(tags);
        // File format and location if Materialization.FILE
        this.location = location;
        this.fileFormat = fileFormat;
        this.readOptions = readOptions;
        this.writeOptions = writeOptions;
    }

    /**
     * The bean class describing this model's schema. Must be annotated with @SparkColumn. Override
     * in every concrete model — no default.
     */
    public abstract Class<?> schemaClass();

    public abstract Dataset<Row> process(
            PipelineContext pipelineContext, DependencyResolver upstream);

    public Dataset<Row> read(PipelineContext pipelineContext) {
        return switch (materialization) {
            case TABLE, VIEW -> pipelineContext.spark().read().table(location);

            case FILE -> {
                if (location == null || fileFormat == null) {
                    throw new IllegalStateException(
                            "Model '"
                                    + location
                                    + "' is FILE-materialized but location or format is null.");
                }

                DataFrameReader reader =
                        pipelineContext
                                .spark()
                                .read()
                                .options(readOptions)
                                .schema(SparkSchema.of(schemaClass()));

                Dataset<Row> raw = reader.format(fileFormat.sparkFormat()).load(location);

                yield raw.withColumnsRenamed(SparkSchema.getSourceRenameMapping(schemaClass()));
            }

            case EPHEMERAL ->
                    throw new UnsupportedOperationException(
                            "Ephemeral model '"
                                    + this.getClass().getSimpleName()
                                    + "' cannot be read from storage.");
        };
    }

    public ModelWriteResult write(Dataset<Row> dataset, PipelineContext context) {
        if (this.getLayer().equals(Layer.SEED) || this.getLayer().equals(Layer.SOURCE)) {
            return ModelWriteResult.ephemeral();
        }
        return switch (materialization) {
            case EPHEMERAL, VIEW -> ModelWriteResult.ephemeral();
            case FILE -> writeToFile(dataset, context);
            case TABLE -> writeToTable(dataset, context);
        };
    }

    protected ModelWriteResult writeToFile(Dataset<Row> dataset, PipelineContext context) {
        dataset.write()
                .format(getFileFormat().sparkFormat())
                .options(getWriteOptions())
                .save(getLocation());

        return new ModelWriteResult(dataset.count(), 1, 0, true);
    }

    protected ModelWriteResult writeToTable(Dataset<Row> dataset, PipelineContext context) {
        throw new InvalidSparkModelConfigurationException(
                "Model '%s' is declared as TABLE materialization but has not overridden 'writeToTable' or 'write'."
                        .formatted(getClass().getName()));
    }

    // === Builder ===

    protected SparkModel(Builder<?> builder) {

        this.layer = builder.layer != null ? builder.layer : defaultNodeType();
        this.primaryKey =
                builder.primaryKey != null ? List.copyOf(builder.primaryKey) : defaultPrimaryKey();
        this.deps = builder.deps != null ? List.copyOf(builder.deps) : defaultDeps();

        this.materialization =
                builder.materialization != null
                        ? builder.materialization
                        : defaultMaterialization();
        this.tags = builder.tags != null ? List.copyOf(builder.tags) : defaultTags();
        this.location = builder.location;
        this.fileFormat = builder.fileFormat;
        this.readOptions = builder.readOptions;
        this.writeOptions = builder.writeOptions;
    }

    public abstract static class Builder<T extends SparkModel> {
        protected Layer layer;
        protected List<String> primaryKey;
        protected List<Class<? extends SparkModel>> deps;
        protected Materialization materialization;
        protected List<String> tags;
        protected String location;
        protected FileFormat fileFormat;
        protected Map<String, String> readOptions = new HashMap<>();
        protected Map<String, String> writeOptions = new HashMap<>();

        public Builder<T> nodeType(Layer layer) {
            this.layer = layer;
            return this;
        }

        public Builder<T> primaryKey(List<String> primaryKey) {
            this.primaryKey = primaryKey;
            return this;
        }

        public Builder<T> deps(List<Class<? extends SparkModel>> deps) {
            this.deps = deps;
            return this;
        }

        public Builder<T> materialization(Materialization materialization) {
            this.materialization = materialization;
            return this;
        }

        public Builder<T> tags(List<String> tags) {
            this.tags = tags;
            return this;
        }

        public Builder<T> tag(String tag) {
            this.tags.add(tag);
            return this;
        }

        public Builder<T> location(String location) {
            this.location = location;
            return this;
        }

        public Builder<T> fileFormat(FileFormat fileFormat) {
            this.fileFormat = fileFormat;
            return this;
        }

        public Builder<T> readOptions(Map<String, String> readOptions) {
            this.readOptions = readOptions;
            return this;
        }

        public Builder<T> readOption(String key, String value) {
            this.readOptions.put(key, value);
            return this;
        }

        public Builder<T> writeOptions(Map<String, String> writeOptions) {
            this.writeOptions = writeOptions;
            return this;
        }

        public Builder<T> writeOption(String key, String value) {
            this.writeOptions.put(key, value);
            return this;
        }

        // --- Abstract build() to be implemented by concrete models ---
        public abstract T build();
    }

    // === String / Debug methods ===

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

    public String describe() {
        StringBuilder sb = new StringBuilder();

        // Class Header
        sb.append(getClass().getSimpleName()).append(" {\n");

        // Core Properties
        sb.append("  primaryKey = ").append(primaryKey).append("\n");
        sb.append("  layer = ").append(layer).append("\n");
        sb.append("  materialization = ").append(materialization).append("\n");
        if (location != null) {
            sb.append("  location        = '").append(location).append("'\n");
        }
        if (fileFormat != null) {
            sb.append("  fileFormat      = ").append(fileFormat).append("\n");
        }

        // Dependencies (mapped to short class names for readability)
        if (deps != null && !deps.isEmpty()) {
            List<String> depNames = deps.stream().map(Class::getSimpleName).toList();
            sb.append("  deps            = ").append(depNames).append("\n");
        }

        // Tags
        if (tags != null && !tags.isEmpty()) {
            sb.append("  tags            = ").append(tags).append("\n");
        }

        // Options
        if (readOptions != null && !readOptions.isEmpty()) {
            sb.append("  readOptions     = ").append(readOptions).append("\n");
        }
        if (writeOptions != null && !writeOptions.isEmpty()) {
            sb.append("  writeOptions    = ").append(writeOptions).append("\n");
        }

        sb.append("}");
        return sb.toString();
    }
}
