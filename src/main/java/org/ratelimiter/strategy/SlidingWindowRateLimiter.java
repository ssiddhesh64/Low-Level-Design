package org.ratelimiter.strategy;

import org.ratelimiter.states.SlidingWindowClientRateLimitState;

import java.time.Clock;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;

public class SlidingWindowRateLimiter implements RateLimiter {

    private final int limit;
    private final Duration window;
    private final Clock clock;

    private final ScheduledExecutorService cleanUp = Executors.newSingleThreadScheduledExecutor();

    private final Map<String, SlidingWindowClientRateLimitState> clientStates;

    public SlidingWindowRateLimiter(int limit, Duration window, Clock clock) {

        Objects.requireNonNull(clock, "clock should not be null");
        if (limit <= 0) {
            throw new IllegalArgumentException("Limit must be greater than 0");
        }

        if (window.isZero() || window.isNegative()) {
            throw new IllegalArgumentException("Window must be positive");
        }

        if (window.getNano() != 0) {
            throw new IllegalArgumentException("Window must use whole seconds");
        }

        this.limit = limit;
        this.window = window;
        this.clock = clock;
        clientStates = new ConcurrentHashMap<>();

        cleanUp.scheduleAtFixedRate(this::cleanStates, 0, 1, TimeUnit.DAYS);
    }

    private void cleanStates() {
        for(String clientId : clientStates.keySet()) {
            SlidingWindowClientRateLimitState state = clientStates.get(clientId);
            state.tryExpire(clock.instant(), window, () -> clientStates.remove(clientId, state));
        }
    }

    @Override
    public boolean allow(String clientId) {
        SlidingWindowClientRateLimitState state =
                clientStates.computeIfAbsent(
                        clientId,
                        c -> new SlidingWindowClientRateLimitState()
                );

        return state.tryAcquire(
                clock.instant(),
                window,
                limit
        );
    }

    public void shutdown() {
        cleanUp.shutdown();
    }
}
