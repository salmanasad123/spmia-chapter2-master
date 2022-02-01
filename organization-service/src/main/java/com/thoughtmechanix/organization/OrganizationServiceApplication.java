package com.thoughtmechanix.organization;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * When a service registers with Eureka, Eureka will wait for three successive health checks over the course of 30
 * seconds before the service becomes available via a Eureka.
 */

@SpringBootApplication
@EnableEurekaClient
public class OrganizationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrganizationServiceApplication.class, args);
    }

}
