package ngz.markov.sparkmodel.planning;

public class InvalidModelExecutionOrderException extends RuntimeException {
    public InvalidModelExecutionOrderException(String message) {
        super(message);
    }

    public InvalidModelExecutionOrderException(String message, Throwable e) {
        super(message, e);
    }
}
