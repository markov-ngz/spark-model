package ngz.markov.crops.models.staging;

import ngz.markov.crops.models.source.SourceCotationProteoOleagineux;
import ngz.markov.sparkmodel.execution.DependencyResolver;
import ngz.markov.sparkmodel.execution.context.PipelineContext;
import ngz.markov.sparkmodel.model.Layer;
import ngz.markov.sparkmodel.model.Materialization;
import ngz.markov.sparkmodel.model.SparkModel;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;

import java.util.List;

public class StgCotationProteoOleagineuxModel extends SparkModel {
    public static final List<Class<? extends SparkModel>> DEPENDENCIES =
            List.of(
                    SourceCotationProteoOleagineux.class);

    private StgCotationProteoOleagineuxModel(Builder builder) {

        super(builder);
    }

    @Override
    protected Layer defaultNodeType() {
        return Layer.STAGING;
    }

    @Override
    protected List<Class<? extends SparkModel>> defaultDeps() {
        return DEPENDENCIES;
    }

    @Override
    protected List<String> defaultPrimaryKey() {
        return List.of(
                "meteofranceModel",
                "meteofranceGrid",
                "meteofrancePackage",
                "meteofranceReferenceTime",
                "meteofranceTime",
                "latitude",
                "longitude",
                "variable");
    }

    @Override
    protected Materialization defaultMaterialization() {
        return Materialization.FILE;
    }

    @Override
    public Class<?> schemaClass() {
        return StgCotationProteoOleagineuxSchema.class;
    }

    // --- Static factory method ---
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends SparkModel.Builder<StgCotationProteoOleagineuxModel> {
        @Override
        public StgCotationProteoOleagineuxModel build() {
            return new StgCotationProteoOleagineuxModel(this);
        }
    }

    @Override
    public Dataset<Row> process(PipelineContext pipelineContext, DependencyResolver upstream) {
        Dataset<SourceCotationProteoOleagineux.Schema> source =
                upstream.get(SourceCotationProteoOleagineux.class)
                        .as(Encoders.bean(SourceCotationProteoOleagineux.Schema.class));

        Dataset<StgCotationProteoOleagineuxSchema> result = StgCotationProteoOleagineuxProcessor.process(source);
        return result.toDF();
    }

}
