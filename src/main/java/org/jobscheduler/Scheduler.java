package org.jobscheduler;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Scheduler {

    ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(4);

    ExecutorService workerPool = Executors.newFixedThreadPool(4);

    PriorityQueue<ScheduledJob> jobs = new PriorityQueue<>();

    Scheduler() {
        scheduledExecutorService
                .scheduleAtFixedRate(this::poll, 0, 1, TimeUnit.SECONDS);
    }

    public void poll() {
        while (!jobs.isEmpty() && jobs.peek().getExecuteAt().isBefore(Instant.now())) {
            ScheduledJob scheduledJob = jobs.poll();
            workerPool.submit(scheduledJob.getJob()::run);
        }
    }

    public void schedule(Job job, Instant executeAt) {
        jobs.offer(new ScheduledJob(job, executeAt));
    }

    public void shutdown() {
        scheduledExecutorService.shutdown();
        workerPool.shutdown();
    }
}
