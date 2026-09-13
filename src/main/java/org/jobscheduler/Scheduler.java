package org.jobscheduler;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Scheduler {

    private volatile SchedularState state;
    ExecutorService scheduledExecutorService = Executors.newSingleThreadExecutor();

    ExecutorService workerPool = Executors.newFixedThreadPool(4);

    ReentrantLock lock = new ReentrantLock();
    Condition condition = lock.newCondition();

    PriorityQueue<ScheduledJob> jobs = new PriorityQueue<>();

    Scheduler() {
        state = SchedularState.RUNNING;
        scheduledExecutorService.execute(this::poll);
    }

    public void poll() {

        while (true) {
            lock.lock();
            try {

                while (state == SchedularState.RUNNING && jobs.isEmpty()) {
                    condition.await();
                }

                if(state == SchedularState.SHUTTING_DOWN && jobs.isEmpty()) {
                    state = SchedularState.TERMINATED;
                    workerPool.shutdown();
                    return;
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

    public boolean schedule(Job job, Instant executeAt) {
        lock.lock();
        try {
            if(state != SchedularState.RUNNING) {
                return false;
            }
            Instant earliestJob = jobs.isEmpty() ? null : jobs.peek().getExecuteAt();

            jobs.offer(new ScheduledJob(job, executeAt));

            if (earliestJob == null || executeAt.isBefore(earliestJob)) {
                condition.signal();
            }

            return true;
        } finally {
            lock.unlock();
        }
    }

    public void shutdown() {

        if(state != SchedularState.RUNNING) return;
        lock.lock();
        try {
            state = SchedularState.SHUTTING_DOWN;
            condition.signalAll();
        } finally {
            lock.unlock();
        }

        scheduledExecutorService.shutdown();
    }
}
