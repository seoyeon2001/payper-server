package com.payper.global.metric;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.web.exchanges.InMemoryHttpExchangeRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HttpTraceMetrics {

    private final InMemoryHttpExchangeRepository repository;
    private final PrometheusMeterRegistry registry;

    @PostConstruct
    public void init() {
        Gauge.builder("httptrace_recent_count", () -> repository.findAll().size())
                .description("Number of recent HTTP exchanges")
                .register(registry);
    }
}
