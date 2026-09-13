package org.jobscheduler;

import java.util.Objects;

public class Job {

    private final Runnable runnable;

    public Job(Runnable runnable) {
        this.runnable = Objects.requireNonNull(
                runnable,
                "Runnable must not be null"
        );
    }

    public void run() {
        runnable.run();
    }
}