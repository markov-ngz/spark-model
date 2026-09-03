package ngz.markov.sparkmodel.execution.context;

public class InvalidPipelineContextException extends RuntimeException {
    public InvalidPipelineContextException(String message) {
        super(message);
    }

    public InvalidPipelineContextException(String message, Throwable e) {
        super(message, e);
    }
}
