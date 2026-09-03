package ngz.markov.crops.models.source;

import lombok.Data;
import ngz.markov.sparkmodel.execution.DependencyResolver;
import ngz.markov.sparkmodel.execution.context.PipelineContext;
import ngz.markov.sparkmodel.model.Layer;
import ngz.markov.sparkmodel.model.Materialization;
import ngz.markov.sparkmodel.model.SparkColumn;
import ngz.markov.sparkmodel.model.SparkModel;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import java.io.Serializable;
import java.util.List;

public class SourceCotationProteoOleagineux extends SparkModel {
    private SourceCotationProteoOleagineux(SourceCotationProteoOleagineux.Builder builder) {

        super(builder);
    }

    public static SourceCotationProteoOleagineux.Builder builder() {
        return new SourceCotationProteoOleagineux.Builder();
    }

    public static class Builder extends SparkModel.Builder<SourceCotationProteoOleagineux> {
        @Override
        public SourceCotationProteoOleagineux build() {
            return new SourceCotationProteoOleagineux(this);
        }
    }

    @Override
    protected Layer defaultNodeType() {
        return Layer.SOURCE;
    }

    @Override
    protected List<String> defaultPrimaryKey() {
        return List.of();
    }

    @Override
    protected Materialization defaultMaterialization() {
        return null;
    }

    @Override
    public Class<?> schemaClass() {
        return Schema.class; // direct, no wrapping
    }

    @Override
    public Dataset<Row> process(PipelineContext pipelineContext, DependencyResolver upstream) {
        return read(pipelineContext);
    }

    @Data
    public static class Schema implements Serializable {
        @SparkColumn(name="ANNEE")
        Integer year;

        @SparkColumn(name="CAMPAGNE")
        String campaign;

        @SparkColumn(name="MOIS")
        String month;

        @SparkColumn(name="SEMAINE")
        Integer weekNumber;

        @SparkColumn(name="ESPECES")
        String species;

        @SparkColumn(name="DETAIL")
        String detail;

        @SparkColumn(name="COTATION (€/t)")
        Integer cotation;
    }
}
