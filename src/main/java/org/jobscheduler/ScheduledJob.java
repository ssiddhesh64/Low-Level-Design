package org.jobscheduler;

import java.time.Instant;
import java.util.Objects;

public class ScheduledJob implements Comparable<ScheduledJob> {

    private final Job job;
    private final Instant executeAt;
    private boolean isCancelled;
    private boolean isRunning;

    public ScheduledJob(Job job, Instant executeAt) {
        this.job = Objects.requireNonNull(job, "Job must not be null");
        this.isRunning = false;
        this.isCancelled = false;
        this.executeAt = Objects.requireNonNull(
                executeAt,
                "Execution time must not be null"
        );
    }

    public boolean isCancelled() {
        return isCancelled;
    }

    public void setCancelled(boolean cancelled) {
        isCancelled = cancelled;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public boolean run() {

        job.run();
        return true;
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