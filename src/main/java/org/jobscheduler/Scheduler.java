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

    PriorityQueue<ScheduledJob> jobQueue = new PriorityQueue<>();
    Map<String, ScheduledJob> jobMap = new HashMap<>();

    Scheduler() {
        state = SchedularState.RUNNING;
        scheduledExecutorService.execute(this::poll);
    }

    public void poll() {

        while (true) {
            lock.lock();
            try {

                while (state == SchedularState.RUNNING && jobQueue.isEmpty()) {
                    condition.await();
                }

                if(state == SchedularState.SHUTTING_DOWN && jobQueue.isEmpty()) {
                    state = SchedularState.TERMINATED;
                    workerPool.shutdown();
                    return;
                }

                ScheduledJob scheduledJob = jobQueue.peek();
                if(scheduledJob.isCancelled()) {
                    jobQueue.poll();
                    continue;
                }

                long rem = scheduledJob.getExecuteAt().toEpochMilli() - Instant.now().toEpochMilli();

                if(rem > 0) {
                    condition.await(rem, TimeUnit.MILLISECONDS);
                    continue;
                }

                ScheduledJob job = jobQueue.poll();
                job.setRunning(true);
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
            Instant earliestJob = jobQueue.isEmpty() ? null : jobQueue.peek().getExecuteAt();

            String jobId = job.getJobId();
            if(jobMap.containsKey(jobId)) {
                System.out.println("Job already submitted");
                return false;
            }
            ScheduledJob newJob = new ScheduledJob(job, executeAt);
            jobMap.put(jobId, newJob);
            jobQueue.offer(newJob);

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

    public boolean cancel(String jobId) {

        lock.lock();
        try {
            if(!jobMap.containsKey(jobId)) return false;
            ScheduledJob scheduledJob = jobMap.get(jobId);
            if(scheduledJob.isRunning()) {
                System.out.println("Job: :" + jobId + " is Running");
                return false;
            }

            scheduledJob.setCancelled(true);
            jobMap.remove(jobId);
            return true;
        } finally {
            lock.unlock();
        }
    }
}
