package ngz.markov.crops.models;

import ngz.markov.crops.models.source.SourceCotationProteoOleagineux;
import ngz.markov.crops.models.staging.StgCotationProteoOleagineuxModel;
import ngz.markov.sparkmodel.model.SparkModel;

import java.util.List;

public class CropsModelCatalog {
    public static final List<Class<? extends SparkModel>> MODELZ =
            List.of(
                SourceCotationProteoOleagineux.class,
                StgCotationProteoOleagineuxModel.class
            );
}
