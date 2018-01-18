package com.rebuilding.day.testcase;

import org.junit.Test;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {
        "com.rebuilding.day.repository",
        "com.rebuilding.day.service"
})
public class IssueContext {
    @Test
    public void noTest(){

    }
}
