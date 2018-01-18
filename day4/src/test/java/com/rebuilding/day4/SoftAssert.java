package com.rebuilding.day4;

import org.assertj.core.api.SoftAssertions;
import org.junit.Test;

public class SoftAssert {
    @Test
    public void testHardAssertion() {

        int actual1 = 5;
        String actual2 = "10";

        SoftAssertions softAssertions = new SoftAssertions();

        softAssertions.assertThat(actual1).isLessThan(6);

        softAssertions.assertThat(actual2).isEqualTo("10");

        softAssertions.assertAll();
    }
}
