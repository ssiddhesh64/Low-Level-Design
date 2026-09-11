package org.ratelimiter.states;

import java.time.Duration;
import java.time.Instant;
import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.locks.ReentrantLock;

public class SlidingWindowClientRateLimitState {

    private final Deque<Instant> timestamps;
    private final ReentrantLock lock = new ReentrantLock();

    public SlidingWindowClientRateLimitState() {
        this.timestamps = new LinkedList<>();
    }

    public boolean tryAcquire(Instant now, Duration window, int limit) {
        lock.lock();
        try {
            Instant cutoff = now.minus(window);
            removeExpired(cutoff);
            if(!canHit(limit)) return false;

            addHit(now);
            return true;
        } finally {
            lock.unlock();
        }
    }

    private void addHit(Instant timestamp) {
        timestamps.offer(timestamp);
    }

    private boolean canHit(int limit) {
        return timestamps.size() < limit;
    }

    private void removeExpired(Instant cutoff) {
        while(!timestamps.isEmpty() && timestamps.peekFirst().isBefore(cutoff)) {
            timestamps.pollFirst();
        }
    }

    public boolean shouldRemove(Duration window) {
        lock.lock();
        try {
            if (timestamps.isEmpty()) {
                return true;
            }

            Instant expirationTime = Instant.now().minus(window);

            return timestamps.peekLast().isBefore(expirationTime);
        } finally {
            lock.unlock();
        }
    }
}
