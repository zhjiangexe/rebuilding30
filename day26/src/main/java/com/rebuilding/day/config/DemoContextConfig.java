package com.rebuilding.day.config;

import org.springframework.context.annotation.*;

@Configuration
@Import(PersistenceContext.class)
public class DemoContextConfig {
    @Profile("prod")
    @Configuration
    @PropertySource("classpath:application-prod.properties")
    static class ApplicationProperties {
    }

    @Profile("integrationTest")
    @Configuration
    @PropertySource("classpath:integration-test.properties")
    static class IntegrationTestProperties {
    }

    @Profile("prod")
    @Bean
    String prod() {
        return "prod";
    }

    @Profile("integrationTest")
    @Bean
    String test() {
        return "test";
    }
}
