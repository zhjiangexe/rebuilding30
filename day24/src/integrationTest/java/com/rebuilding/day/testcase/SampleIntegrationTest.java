package com.rebuilding.day.testcase;

import com.rebuilding.day.integrationTest.IntegrationTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class SampleIntegrationTest {
    @Test
    public void test() {
        System.out.println("rock");
    }
}
