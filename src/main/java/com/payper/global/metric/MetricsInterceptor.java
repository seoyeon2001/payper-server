package com.payper.global.metric;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class MetricsInterceptor implements HandlerInterceptor {

    private final MeterRegistry meterRegistry;

    private static final String TIMER_METRIC_NAME = "http_server_requests";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        request.setAttribute("startTime", System.nanoTime());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        Long startTime = (Long) request.getAttribute("startTime");
        if (startTime != null) {
            long durationNs = System.nanoTime() - startTime;

            Timer timer = meterRegistry.timer(TIMER_METRIC_NAME,
                    "method", request.getMethod(),
                    "uri", request.getRequestURI(),
                    "status", Integer.toString(response.getStatus()));
            timer.record(durationNs, TimeUnit.NANOSECONDS);
        }
    }
}
