package org.customThreadPool;

public class Main {

    public static void main(String[] args)
            throws InterruptedException {

        CustomThreadPool pool = new CustomThreadPool(2);

        System.out.println("Initially:");
        System.out.println("Shutdown: " + pool.isShutDown());
        System.out.println("Terminated: " + pool.isTerminated());

        pool.submit(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            System.out.println("Task completed");
        });

        pool.shutdown();

        System.out.println("\nAfter shutdown():");
        System.out.println("Shutdown: " + pool.isShutDown());
        System.out.println("Terminated: " + pool.isTerminated());

        pool.awaitTermination();

        System.out.println("\nAfter awaitTermination():");
        System.out.println("Shutdown: " + pool.isShutDown());
        System.out.println("Terminated: " + pool.isTerminated());
    }
}