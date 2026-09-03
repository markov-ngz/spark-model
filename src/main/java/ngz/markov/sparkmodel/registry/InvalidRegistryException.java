package ngz.markov.sparkmodel.registry;

public class InvalidRegistryException extends RuntimeException {
    public InvalidRegistryException(String message) {
        super(message);
    }

    public InvalidRegistryException(String message, Throwable e) {
        super(message, e);
    }
}
