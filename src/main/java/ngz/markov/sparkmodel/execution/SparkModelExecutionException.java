package ngz.markov.sparkmodel.execution;

public class SparkModelExecutionException extends RuntimeException {
    public SparkModelExecutionException(String message) {
        super(message);
    }

    public SparkModelExecutionException(String message, Throwable e) {
        super(message, e);
    }
}
