package org.jobscheduler;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class SchedulerRunner {

    public static void main(String[] args) {

        Scheduler scheduler = new Scheduler();

        Instant now = Instant.now();

        Job A = new Job("JobA", () -> System.out.println("Job A"));
        Job B = new Job("JobB", () -> System.out.println("Job B"));
        Job C = new Job("JobC", () -> System.out.println("Job C"));
        Job D = new Job("JobD", () -> System.out.println("Job D"));

        scheduler.schedule(
                A,
                now.plus(Duration.ofSeconds(5)),
                List.of()
        );

        scheduler.schedule(
                B,
                now.plus(Duration.ofSeconds(2)),
                List.of("JobA")
        );

        scheduler.schedule(
                C,
                now.plus(Duration.ofSeconds(8)),
                List.of()
        );

        scheduler.schedule(
                D,
                now.plus(Duration.ofSeconds(8)),
                List.of("JobB", "JobC")
        );
    }

}
