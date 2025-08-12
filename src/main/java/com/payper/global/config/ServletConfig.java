package com.payper.global.config;

import com.payper.global.metric.MetricsInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.*;

@EnableWebMvc
@ComponentScan(
        basePackages = "com.payper",
        includeFilters = @ComponentScan.Filter(
                type = FilterType.ANNOTATION,
                classes = RestController.class
        ),
        useDefaultFilters = false
)
@RequiredArgsConstructor
public class ServletConfig implements WebMvcConfigurer {

    private final MetricsInterceptor metricsInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(metricsInterceptor);
    }
}
