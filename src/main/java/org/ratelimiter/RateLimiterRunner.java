package org.ratelimiter;

import org.ratelimiter.strategy.FixedWindowRateLimiter;
import org.ratelimiter.strategy.RateLimiter;
import org.ratelimiter.strategy.SlidingWindowRateLimiter;
import org.util.PrintUtil;

import java.time.Clock;
import java.time.Duration;

public class RateLimiterRunner {

    public static void main(String[] args) {

        System.out.println("========Sliding Window========");
        RateLimiter slidingWindowRateLimiter = new SlidingWindowRateLimiter(2, Duration.ofMinutes(1), Clock.systemUTC());

        boolean shouldAllow1  = slidingWindowRateLimiter.allow("c1");
        boolean shouldAllow2  = slidingWindowRateLimiter.allow("c1");
        boolean shouldAllow3  = slidingWindowRateLimiter.allow("c1");

        PrintUtil.print(shouldAllow1, shouldAllow2, shouldAllow3);

        System.out.println("========Fixed Window========");
        RateLimiter fixedWindowRateLimiter = new FixedWindowRateLimiter(2, Duration.ofMinutes(1), Clock.systemUTC());

        boolean shouldAllow4  = fixedWindowRateLimiter.allow("c1");
        boolean shouldAllow5  = fixedWindowRateLimiter.allow("c1");
        boolean shouldAllow6  = fixedWindowRateLimiter.allow("c1");

        PrintUtil.print(shouldAllow4, shouldAllow5, shouldAllow6);

    }
}
