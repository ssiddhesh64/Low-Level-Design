package org.customThreadPool;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;

public class CustomThreadPool {

    private final int nthreads;
    private final BlockingQueue<Runnable> jobs = new LinkedBlockingQueue<>();
    private final List<WorkerThread> workerThreads = new ArrayList<>();

    private final Runnable POISON_PILL = () -> {};

    private boolean isShutDown = false;

    public CustomThreadPool(int nthreads) {

        if(nthreads <= 0) {
            throw new IllegalArgumentException("Number of Threads must be positive");
        }

        this.nthreads = nthreads;

        for (int i = 0; i < nthreads; i++) {
            WorkerThread worker = new WorkerThread();
            workerThreads.add(worker);
            worker.start();
        }
    }

    public synchronized void submit(Runnable job) {

        Objects.requireNonNull(job, "Job cannot be null");

        if(isShutDown) {
            throw new RejectedExecutionException("Pool has been shut down");
        }

        jobs.add(job);
    }

    public synchronized void shutdown() {

        if(isShutDown) {
            return;
        }

        isShutDown = true;
        for (int i = 0; i < nthreads; i++) {
            jobs.add(POISON_PILL);
        }
    }

    public synchronized boolean isShutDown() {
        return isShutDown;
    }

    // True only when shutdown has been initiated
    // and all worker threads have terminated.
    public synchronized boolean isTerminated() {

        if (!isShutDown) {
            return false;
        }

        for (WorkerThread worker : workerThreads) {
            if (worker.isAlive()) {
                return false;
            }
        }

        return true;
    }

    public void awaitTermination() throws InterruptedException {
        for (WorkerThread workerThread : workerThreads) {
            workerThread.join();
        }
    }

    class WorkerThread implements Runnable{

        private final Thread thread;

        public WorkerThread() {
            this.thread = new Thread(this);
        }

        public void start() {
            thread.start();
        }

        public void join() throws InterruptedException {
            thread.join();
        }

        public boolean isAlive() {
            return thread.isAlive();
        }

        @Override
        public void run() {
            while(!Thread.currentThread().isInterrupted()) {
                try {
                    Runnable job = jobs.take();

                    if(job == POISON_PILL) {
                        break;
                    }

                    try {
                        job.run();
                    } catch (RuntimeException ex) {
                        System.err.println(
                                "Job failed: " + ex.getMessage()
                        );
                    }
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}
