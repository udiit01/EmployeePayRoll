package com.example.payroll.filters;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private final ConcurrentHashMap<String, Bucket> cache = new ConcurrentHashMap<>();

    private static final String IP_LIMIT_PREFIX = "rate-limit-ip:";
    private static final String GLOBAL_LIMIT_KEY = "rate-limit-global";

    public RateLimitingFilter() {
        
    }

    private Bucket getIpBucket(String ipAddress) {
        return cache.computeIfAbsent(IP_LIMIT_PREFIX + ipAddress, key ->
            Bucket.builder()
                .addLimit(Bandwidth.builder()
                    .capacity(100)
                    .refillIntervally(100, Duration.ofMinutes(1))
                    .build())
                .build()
        );
    }

    private Bucket getGlobalBucket() {
        return cache.computeIfAbsent(GLOBAL_LIMIT_KEY, key ->
            Bucket.builder()
                .addLimit(Bandwidth.builder()
                    .capacity(1000) 
                    .refillIntervally(1000, Duration.ofMinutes(1))
                    .build())
                .build()
        );
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String ipAddress = request.getRemoteAddr();
        Bucket ipBucket = getIpBucket(ipAddress);
        Bucket globalBucket = getGlobalBucket();

        if (!ipBucket.tryConsume(1)) {
            response.sendError(HttpStatus.TOO_MANY_REQUESTS.value(), "Too many requests from this IP.");
            return;
        }

        if (!globalBucket.tryConsume(1)) {
            response.sendError(HttpStatus.TOO_MANY_REQUESTS.value(), "Server-wide rate limit exceeded.");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
