package ngz.markov.crops.models.staging;

import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
public class StgCotationProteoOleagineuxSchema implements Serializable {

    Integer year;

    String campaign;

    String month;

    Integer weekNumber;

    String species;

    String detail;

    Integer cotation;
}
