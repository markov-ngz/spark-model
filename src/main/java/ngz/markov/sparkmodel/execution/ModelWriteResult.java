package ngz.markov.sparkmodel.execution;

/** Write operation output result . */
public record ModelWriteResult(
        long recordsWritten, long filesWritten, long bytesWritten, boolean isMaterialized) {
    public static ModelWriteResult ephemeral() {
        return new ModelWriteResult(0, 0, 0, false);
    }
}
