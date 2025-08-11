package com.payper.global.metric;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class HttpMetricsFilter implements Filter {
    private final Counter requestCounter;
    private final Timer requestTimer;

    public HttpMetricsFilter(MeterRegistry registry) {
        this.requestCounter = registry.counter("http_requests_total");
        this.requestTimer = registry.timer("http_request_duration_seconds");
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException { }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        if (req instanceof HttpServletRequest) {
            HttpServletRequest request = (HttpServletRequest) req;
            long start = System.nanoTime();

            try {
                chain.doFilter(req, res);
            } finally {
                long duration = System.nanoTime() - start;
                requestCounter.increment();
                requestTimer.record(duration, TimeUnit.NANOSECONDS);
            }
        } else {
            chain.doFilter(req, res);
        }
    }

    @Override
    public void destroy() { }
}
