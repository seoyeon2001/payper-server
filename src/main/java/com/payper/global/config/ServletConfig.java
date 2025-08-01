package com.payper.global.config;

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
public class ServletConfig implements WebMvcConfigurer {
}
