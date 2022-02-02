package com.thoughtmechanix.organization;

import com.thoughtmechanix.organization.utils.UserContextFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

import javax.servlet.Filter;

/**
 * When a service registers with Eureka, Eureka will wait for three successive health checks over the course of 30
 * seconds before the service becomes available via a Eureka.
 */

@SpringBootApplication
@EnableEurekaClient
// The @EnableResourceServer annotation tells Spring Cloud and Spring Security that the service is a protected resource
// The @EnableResourceServer enforces a filter that intercepts all incoming calls to the service, checks to see if there’s an OAuth2
// access token present in the incoming call’s HTTP header, and then calls back to the call- back URL defined in the security.oauth2.resource.userInfoUri to see if the token is valid
@EnableResourceServer
public class OrganizationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrganizationServiceApplication.class, args);
    }

    @Bean
    public Filter userContextFilter() {
        UserContextFilter userContextFilter = new UserContextFilter();
        return userContextFilter;
    }


}
