package org.ratelimiter.strategy;

import org.ratelimiter.states.SlidingWindowClientRateLimitState;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SlidingWindowRateLimiter implements RateLimiter {

    private final int limit;
    private final Duration window;

    private Map<String, SlidingWindowClientRateLimitState> hitMap;

    public SlidingWindowRateLimiter(int limit, Duration window) {
        this.limit = limit;
        this.window = window;
        hitMap = new ConcurrentHashMap<>();
    }

    @Override
    public boolean allow(String clientId) {
        SlidingWindowClientRateLimitState state =
                hitMap.computeIfAbsent(
                        clientId,
                        c -> new SlidingWindowClientRateLimitState()
                );
        return state.tryAcquire(
                Instant.now(),
                window,
                limit
        );
    }
}
