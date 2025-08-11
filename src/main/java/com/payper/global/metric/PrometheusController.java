package com.payper.global.metric;

import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PrometheusController {

    private final PrometheusMeterRegistry prometheusMeterRegistry;

    @GetMapping(value = "/actuator/prometheus", produces = "text/plain")
    public String scrape() {
        return prometheusMeterRegistry.scrape();
    }
}
