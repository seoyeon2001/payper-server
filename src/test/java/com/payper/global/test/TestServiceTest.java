package com.payper.global.test;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TestServiceTest {

    private TestService testService = new TestService();

    @Test
    void sum() {
        // given
        int a = 1;
        int b = 2;

        // when
        int sum = testService.sum(a, b);

        // then
        assertThat(sum).isEqualTo(3);
    }
}