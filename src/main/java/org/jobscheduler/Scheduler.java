package org.jobscheduler;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Scheduler {

    private volatile boolean running;
    ExecutorService scheduledExecutorService = Executors.newSingleThreadExecutor();

    ExecutorService workerPool = Executors.newFixedThreadPool(4);

    ReentrantLock lock = new ReentrantLock();
    Condition condition = lock.newCondition();

    PriorityQueue<ScheduledJob> jobs = new PriorityQueue<>();

    Scheduler() {
        running = true;
        scheduledExecutorService.execute(this::poll);
    }

    public void poll() {

        while (true) {
            lock.lock();
            try {

                while (running && jobs.isEmpty()) {
                    condition.await();
                }

                ScheduledJob scheduledJob = jobs.peek();

                long rem = scheduledJob.getExecuteAt().toEpochMilli() - Instant.now().toEpochMilli();

                if(rem > 0) {
                    condition.await(rem, TimeUnit.MILLISECONDS);
                    continue;
                }

                ScheduledJob job = jobs.poll();
                workerPool.submit(job::run);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } finally {
                lock.unlock();
            }
        }
    }

    public void schedule(Job job, Instant executeAt) {
        lock.lock();
        try {
            Instant earliestJob;
            if (jobs.isEmpty()) {
                earliestJob = executeAt;
            } else {
                earliestJob = jobs.peek().getExecuteAt();
            }
            jobs.offer(new ScheduledJob(job, executeAt));
            if (executeAt.isBefore(earliestJob)) {
                condition.signal();
            }
        } finally {
            lock.unlock();
        }
    }

    public void shutdown() {
        lock.lock();
        try {
            running = false;
            condition.signalAll();
        } finally {
            lock.unlock();
        }

        scheduledExecutorService.shutdown();
        workerPool.shutdown();
    }
}
