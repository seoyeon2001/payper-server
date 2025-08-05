package com.payper.global.test;

import org.springframework.stereotype.Service;

@Service
public class TestService {

    public Integer sum(Integer a, Integer b) {
        return a + b;
    }
}
