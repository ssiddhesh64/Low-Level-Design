package org.ratelimiter;

import org.util.PrintUtil;

import java.time.Duration;

public class RateLimiter {

    public static void main(String[] args) {

        SlidingWindowRateLimiter window = new SlidingWindowRateLimiter(2, Duration.ofMinutes(1));

        boolean shouldAllow1  = window.allow("c1");
        boolean shouldAllow2  = window.allow("c1");
        boolean shouldAllow3  = window.allow("c1");

        PrintUtil.print(shouldAllow1, shouldAllow2, shouldAllow3);
    }
}
