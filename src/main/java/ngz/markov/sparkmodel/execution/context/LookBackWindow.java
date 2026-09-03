package ngz.markov.sparkmodel.execution.context;

import java.sql.Timestamp;

public record LookBackWindow(Timestamp startTime, Timestamp endTime) {}
