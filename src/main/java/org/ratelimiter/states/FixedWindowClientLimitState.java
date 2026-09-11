package org.ratelimiter.states;

import java.time.Instant;
import java.util.concurrent.locks.ReentrantLock;

public class FixedWindowClientLimitState {

    private Instant windowStart;
    private int hitCount;
    private final ReentrantLock lock = new ReentrantLock();

    public FixedWindowClientLimitState() {
        hitCount = 0;
        windowStart = null;
    }

    public boolean tryAcquire(Instant curstart, int limit) {
        lock.lock();
        try {
            if(!curstart.equals(windowStart)) {
                windowStart = curstart;
                hitCount = 0;
            }

            if(hitCount < limit) {
                hitCount++;
                return true;
            }

            return false;
        } finally {
            lock.unlock();
        }
    }


}
