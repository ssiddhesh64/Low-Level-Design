package org.jobscheduler;

import java.util.Objects;

public class Job {

    private final String jobId;
    private final Runnable runnable;

    public Job(String jobId, Runnable runnable) {
        this.jobId = jobId;
        this.runnable = Objects.requireNonNull(
                runnable,
                "Runnable must not be null"
        );
    }

    public String getJobId() {
        return jobId;
    }

    public void run() {
        runnable.run();
    }
}