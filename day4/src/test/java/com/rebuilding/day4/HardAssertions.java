package com.rebuilding.day4;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class HardAssertions {
    @Test
    public void testHardAssertion() {

        int actual1 = 5;
        String actual2 = "10";

        assertThat(actual1).isLessThan(6);

        assertThat(actual2).isEqualTo("10");

    }
}
