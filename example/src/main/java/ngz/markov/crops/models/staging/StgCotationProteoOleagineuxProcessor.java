package ngz.markov.crops.models.staging;

import ngz.markov.crops.models.source.SourceCotationProteoOleagineux;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;

public class StgCotationProteoOleagineuxProcessor {
    public static Dataset<StgCotationProteoOleagineuxSchema> process(
            Dataset<SourceCotationProteoOleagineux.Schema> sourceCotationProteoOleagineux
    ){
        // Drop column cotation
        sourceCotationProteoOleagineux.na().drop("any", new String[]{"cotation"});
        Dataset<Row> result = sourceCotationProteoOleagineux.na().drop("any", new String[]{"cotation"});
        return result.as(Encoders.bean(StgCotationProteoOleagineuxSchema.class));
    }

}
