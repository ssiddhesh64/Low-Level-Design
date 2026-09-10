package org.ratelimiter;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SlidingWindowRateLimiter {

    private final int limit;
    private final Duration window;

    private Map<String, ClientRateLimitState> hitMap;

    public SlidingWindowRateLimiter(int limit, Duration window) {
        this.limit = limit;
        this.window = window;
        hitMap = new ConcurrentHashMap<>();
    }

    public boolean allow(String clientId) {
        ClientRateLimitState state =
                hitMap.computeIfAbsent(
                        clientId,
                        c -> new ClientRateLimitState()
                );
        return state.tryAcquire(
                Instant.now(),
                window,
                limit
        );
    }
}
