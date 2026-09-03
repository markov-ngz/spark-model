package ngz.markov.sparkmodel.pipeline;

public class PipelineExecutionException extends RuntimeException {
    public PipelineExecutionException(String message) {
        super(message);
    }

    public PipelineExecutionException(String message, Throwable e) {
        super(message, e);
    }
}
