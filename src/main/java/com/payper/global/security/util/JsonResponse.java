package com.payper.global.security.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.Writer;

@Component
@RequiredArgsConstructor
public class JsonResponse {
    private final ObjectMapper objectMapper;

    public <T> void send(HttpServletResponse response, T result) throws IOException {

        response.setContentType("application/json;charset=UTF-8");

        Writer out=response.getWriter();
        out.write(objectMapper.writeValueAsString(result));

        out.flush();
    }

    public static void sendError(HttpServletResponse response,
                                 HttpStatus status,
                                 String msg) throws IOException {
        response.setStatus(status.value());
        response.setContentType("application/json;charset=UTF-8");

        Writer out=response.getWriter();
        out.write(msg);

        out.flush();
    }
}
