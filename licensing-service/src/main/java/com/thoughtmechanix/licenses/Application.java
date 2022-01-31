package com.thoughtmechanix.licenses;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * We’re going to look at implementing Hystrix in two broad categories. In the first category,
 * you’re going to wrap all calls to your database in the licensing and organization service with a Hystrix circuit breaker.
 * You’re then going to wrap the inter-service calls between the licensing service and the organization service using Hystrix
 */

@SpringBootApplication
@EnableEurekaClient
@EnableDiscoveryClient   // Activates the Spring DiscoveryClient for use. The @EnableDiscoveryClient annotation is the trigger for Spring Cloud to enable the application to use the DiscoveryClient and Ribbon libraries
@EnableFeignClients      // The @EnableFeignClients annotation is needed to use the FeignClient in your code.
@EnableCircuitBreaker    // Tells Spring Cloud you’re going to use Hystrix for your service
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    // To use a Ribbon-aware RestTemplate class, you need to define a RestTemplate bean construction method
    // with a Spring Cloud annotation called @LoadBalanced
    // The @LoadBalanced annotation tells Spring Cloud to create a Ribbon backed RestTemplate class without this the rest template client will not be backed by ribbon
    // which we can use for client side load balancing.
    @LoadBalanced
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
