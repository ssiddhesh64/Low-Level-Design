package org.jobscheduler;

import java.time.Instant;
import java.util.Objects;

public class ScheduledJob implements Comparable<ScheduledJob> {

    private final Job job;
    private final Instant executeAt;

    public ScheduledJob(Job job, Instant executeAt) {
        this.job = Objects.requireNonNull(job, "Job must not be null");
        this.executeAt = Objects.requireNonNull(
                executeAt,
                "Execution time must not be null"
        );
    }

    public Job getJob() {
        return job;
    }

    public Instant getExecuteAt() {
        return executeAt;
    }

    @Override
    public int compareTo(ScheduledJob other) {
        return executeAt.compareTo(other.executeAt);
    }
}