package com.rebuilding.day.config;

import org.springframework.context.annotation.*;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@Import(PersistenceContext.class)
@EnableWebMvc
@ComponentScan(basePackages = {"com.rebuilding.day.service", "com.rebuilding.day.web"})
public class DemoContextConfig {
    @Profile("prod")
    @Configuration
    @PropertySource("classpath:application.properties")
    static class ApplicationProperties {
    }

    @Profile("integrationTest")
    @Configuration
    @PropertySource("classpath:integration-test.properties")
    static class IntegrationTestProperties {
    }

}
