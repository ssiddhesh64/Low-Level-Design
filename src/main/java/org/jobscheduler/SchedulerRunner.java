package org.jobscheduler;

import java.time.Duration;
import java.time.Instant;

public class SchedulerRunner {

    public static void main(String[] args) {

        Scheduler scheduler = new Scheduler();

        Instant now = Instant.now();

        scheduler.schedule(
                new Job("Job1", () -> System.out.println("Job A")),
                now.plus(Duration.ofSeconds(5))
        );

        scheduler.schedule(
                new Job("Job2", () -> System.out.println("Job B")),
                now.plus(Duration.ofSeconds(2))
        );

        scheduler.schedule(
                new Job("Job3", () -> System.out.println("Job C")),
                now.plus(Duration.ofSeconds(8))
        );
    }
}
