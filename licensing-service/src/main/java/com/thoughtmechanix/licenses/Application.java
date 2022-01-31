package com.thoughtmechanix.licenses;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableDiscoveryClient // Activates the Spring DiscoveryClient for use. The @EnableDiscoveryClient annotation is the trigger for Spring Cloud to enable the application to use the DiscoveryClient and Ribbon libraries
@EnableFeignClients // The @EnableFeignClients annotation is needed to use the FeignClient in your code.
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
