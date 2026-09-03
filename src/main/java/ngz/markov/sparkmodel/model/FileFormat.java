package ngz.markov.sparkmodel.model;

public enum FileFormat {
    PARQUET("parquet"),
    DELTA("delta"),
    CSV("csv"),
    JSON("json"),
    ORC("orc");

    private final String sparkFormat;

    FileFormat(String sparkFormat) {
        this.sparkFormat = sparkFormat;
    }

    public String sparkFormat() {
        return sparkFormat;
    }
}
