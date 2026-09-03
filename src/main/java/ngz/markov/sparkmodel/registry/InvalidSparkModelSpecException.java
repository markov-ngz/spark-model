package ngz.markov.sparkmodel.registry;

public class InvalidSparkModelSpecException extends RuntimeException {
    public InvalidSparkModelSpecException(String message) {
        super(message);
    }

    public InvalidSparkModelSpecException(String message, Throwable e) {
        super(message, e);
    }
}
