package com.anr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
// Removed: Hystrix is not compatible with Spring Boot 3.x
// import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
@EnableAsync
@EnableAspectJAutoProxy
// Removed: @EnableCircuitBreaker - Hystrix not compatible with Spring Boot 3.x
@SpringBootApplication(exclude = MongoAutoConfiguration.class)
public class Bootstrap extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(Bootstrap.class, args);
        // SpringApplication app = new SpringApplication(DemoApplication.class);
        // app.run(args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Bootstrap.class);
    }

}
