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
//    Map<String, ScheduledJob> jobMap = new HashMap<>();

    DependencyGraph graph;

    Scheduler() {
        state = SchedularState.RUNNING;
        scheduledExecutorService.execute(this::poll);
        graph = new DependencyGraph();
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
                if(scheduledJob.getJobStatus().equals(JobStatus.CANCELLED)) {
                    jobQueue.poll();
                    continue;
                }

                long rem = scheduledJob.getExecuteAt().toEpochMilli() - Instant.now().toEpochMilli();

                if(rem > 0) {
                    condition.await(rem, TimeUnit.MILLISECONDS);
                    continue;
                }

                ScheduledJob job = jobQueue.poll();
                job.setJobStatus(JobStatus.RUNNING);
                workerPool.submit(() -> {
                    try {
                        job.run();
                        onJobSuccess(job);
                    } catch (Exception e) {
                        onJobFailure(job);
                    }
                });
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } finally {
                lock.unlock();
            }
        }
    }

    private void onJobSuccess(ScheduledJob job) {

        job.setJobStatus(JobStatus.SUCCESS);
        for(String child : job.getDependents()) {
            ScheduledJob childJob = graph.getJob(child);
            childJob.getDependsOn().remove(job.getJob().getJobId());

            if(childJob.getDependsOn().isEmpty() && !childJob.getJobStatus().equals(JobStatus.CANCELLED)) {
                childJob.setJobStatus(JobStatus.READY);
                lock.lock();
                try {
                    jobQueue.offer(childJob);
                    condition.signal();
                } finally {
                    lock.unlock();
                }
             }
        }
    }

    private void onJobFailure(ScheduledJob job) {
        job.setJobStatus(JobStatus.FAILED);
        for(String child : job.getDependents()) {
            graph.getJob(child).setJobStatus(JobStatus.CANCELLED);
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
            if(graph.getJob(jobId) != null) {
                System.out.println("Job already submitted");
                return false;
            }
            ScheduledJob newJob = new ScheduledJob(job, executeAt);
            graph.addJob(newJob);
            jobQueue.offer(newJob);

            if (earliestJob == null || executeAt.isBefore(earliestJob)) {
                condition.signal();
            }

            return true;
        } finally {
            lock.unlock();
        }
    }

    public boolean schedule(Job job, Instant executeAt, List<String> dependsOn) {
        lock.lock();
        try {
            if(state != SchedularState.RUNNING) {
                return false;
            }
            Instant earliestJob = jobQueue.isEmpty() ? null : jobQueue.peek().getExecuteAt();

            String jobId = job.getJobId();
            ScheduledJob newJob = new ScheduledJob(job, executeAt);

            newJob.dependsOn.addAll(new HashSet<>(dependsOn));
            for(String par : dependsOn) {
                ScheduledJob child = graph.getJob(par);
                newJob.addParent(par);
                if(Objects.nonNull(child)) {
                    child.addDependent(jobId);
                }
            }

            if(graph.getJob(jobId) != null) {
                System.out.println("Job already submitted");
                return false;
            }
            graph.addJob(newJob);
            if(newJob.getDependsOn().isEmpty()) {
                jobQueue.offer(newJob);
            }

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
            ScheduledJob job = graph.getJob(jobId);
            if(job == null) return false;
            if(job.getJobStatus().equals(JobStatus.RUNNING)) {
                System.out.println("Job: :" + jobId + " is Running");
                return false;
            }

            job.setJobStatus(JobStatus.CANCELLED);
            return true;
        } finally {
            lock.unlock();
        }
    }
}
