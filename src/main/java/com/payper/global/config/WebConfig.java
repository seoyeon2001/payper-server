package com.payper.global.config;

import com.payper.global.security.config.SecurityConfig;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletRegistration;

public class WebConfig extends AbstractAnnotationConfigDispatcherServletInitializer {
    final String LOCATION = "업로드 절대 경로";
    final long MAX_FILE_SIZE = 10 * 1024 * 1024L;
    final long MAX_REQUEST_SIZE = 10 * 1024 * 1024L;
    final int FILE_SIZE_THRESHOLD = 1024 * 1024 * 5;

    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class[] {
                RootConfig.class,
                SecurityConfig.class
        };
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class[] {
                ServletConfig.class
        };
    }

    @Override
    protected String[] getServletMappings() {
        return new String[] {"/"};
    }


    @Override
    protected void customizeRegistration(ServletRegistration.Dynamic registration) {
        registration.setInitParameter("throwExceptionIfNoHandlerFound", "true");

        MultipartConfigElement multipartConfig =
                new MultipartConfigElement(
                        LOCATION, MAX_REQUEST_SIZE, MAX_FILE_SIZE, FILE_SIZE_THRESHOLD
                );
        registration.setMultipartConfig(multipartConfig);
    }
}
