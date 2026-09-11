package org.ratelimiter.strategy;

import org.ratelimiter.states.FixedWindowClientLimitState;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FixedWindowRateLimiter implements RateLimiter {

    private final int limit;
    private final Duration window;

    private final Map<String, FixedWindowClientLimitState> clientStates;

    public FixedWindowRateLimiter(int limit, Duration window) {
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
        clientStates = new ConcurrentHashMap<>();
    }

    @Override
    public boolean allow(String clientId) {
        Instant windowStart = getWindowStart(Instant.now(), window);
        return clientStates.computeIfAbsent(clientId, c -> new FixedWindowClientLimitState())
                .tryAcquire(windowStart, window, limit);
    }

    private Instant getWindowStart(Instant now, Duration window) {
        long windowSeconds = window.getSeconds();
        long epochSeconds = now.getEpochSecond();

        long windowStartSeconds =
                (epochSeconds / windowSeconds) * windowSeconds;

        return Instant.ofEpochSecond(windowStartSeconds);
    }
}
