package ngz.markov.sparkmodel.pipeline;

import java.util.List;
import ngz.markov.sparkmodel.execution.ModelExecutionResult;

// Pipeline result
public record PipelineResult(List<ModelExecutionResult> success, boolean isSuccess) {}
