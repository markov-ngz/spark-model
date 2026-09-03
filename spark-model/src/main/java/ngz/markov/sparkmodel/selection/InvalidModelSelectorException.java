package ngz.markov.sparkmodel.selection;

public class InvalidModelSelectorException extends RuntimeException {
    public InvalidModelSelectorException(String message) {
        super(message);
    }

    public InvalidModelSelectorException(String message, Throwable e) {
        super(message, e);
    }
}
