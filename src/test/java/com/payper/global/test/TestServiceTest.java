package com.payper.global.test;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TestServiceTest {

    private TestService testService = new TestService();

    @Test
    void sum() {
        // given
        Integer a = 1;
        Integer b = 2;

        // when
        Integer sum = testService.sum(a, b);

        // then
        assertThat(sum).isEqualTo(3);
    }
}