package com.thoughtmechanix.licenses;

import com.thoughtmechanix.licenses.utils.UserContextInterceptor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

/**
 * We’re going to look at implementing Hystrix in two broad categories. In the first category,
 * you’re going to wrap all calls to your database in the licensing and organization service with a Hystrix circuit breaker.
 * You’re then going to wrap the inter-service calls between the licensing service and the organization service using Hystrix
 */

@SpringBootApplication
@EnableEurekaClient
@EnableDiscoveryClient    // Activates the Spring DiscoveryClient for use. The @EnableDiscoveryClient annotation is the trigger for Spring Cloud to enable the application to use the DiscoveryClient and Ribbon libraries
@EnableFeignClients      // The @EnableFeignClients annotation is needed to use the FeignClient in your code.
@EnableCircuitBreaker    // Tells Spring Cloud you’re going to use Hystrix for your service
@EnableResourceServer   // This annotation indicated that this service is an OAuth2 resource service. The @EnableResourceServer enforces a filter that intercepts all incoming calls to the service, checks to see if there’s an OAuth2 access token present in the incoming call’s HTTP header, and then calls back to the call- back URL defined in the security.oauth2.resource.userInfoUri to see if the token is valid
public class LicensingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(LicensingServiceApplication.class, args);
    }

    // To use a Ribbon-aware RestTemplate class, you need to define a RestTemplate bean construction method
    // with a Spring Cloud annotation called @LoadBalanced
    // The @LoadBalanced annotation tells Spring Cloud to create a Ribbon backed RestTemplate class without this the rest template client will not be backed by ribbon
    // which we can use for client side load balancing.
    // To use the UserContextInterceptor you need to define a RestTemplate bean and then add the UserContextInterceptor to it
    @LoadBalanced
    @Bean
    public RestTemplate restTemplate() {
        RestTemplate template = new RestTemplate();
        List<ClientHttpRequestInterceptor> interceptors = template.getInterceptors();
        if (interceptors == null) {
            template.setInterceptors(Collections.singletonList(new UserContextInterceptor()));
        } else {
            interceptors.add(new UserContextInterceptor());
            template.setInterceptors(interceptors);
        }
        return template;
    }

    /**
     * all you need to do is modify how the code in the licensing service calls the organization service.
     * You need to ensure that the “Authorization” HTTP header is injected into the application call out to the
     * Organization service. Without Spring Security, you’d have to write a servlet filter to grab the HTTP header off
     * the incoming licensing service call and then manually add it to every outbound service call in the licensing service.
     * Spring OAuth2 provides a new Rest Template class that supports OAuth2 calls. The class is called OAuth2RestTemplate.
     * To use the OAuth2RestTemplate class you first need to expose it as a bean that can be auto-wired into a service
     * calling another OAuth2 protected services
     */

    @Bean
    public OAuth2RestTemplate oauth2RestTemplate(OAuth2ClientContext oauth2ClientContext,
                                                 OAuth2ProtectedResourceDetails details) {
        return new OAuth2RestTemplate(details, oauth2ClientContext);
    }


    // Because the licensing service calls the organization service, you need to ensure that the OAuth2 token is propagated.
    // This is normally done via the OAuth2RestTemplate class; however, the OAuth2RestTemplate class doesn’t
    // propagate JWT-based tokens. To make sure that your licensing service does this, you need to add a custom
    // RestTemplate bean that will perform this injection for you
    @Primary
    @Bean
    public RestTemplate getCustomRestTemplate() {
        RestTemplate template = new RestTemplate();
        List interceptors = template.getInterceptors();
        if (interceptors == null) {
            template.setInterceptors(Collections.singletonList(new UserContextInterceptor()));  // The UserContextInterceptor will inject the Authorization header into every Rest call
        } else {
            interceptors.add(new UserContextInterceptor());  // The UserContextInterceptor will inject the Authorization header into every Rest call
            template.setInterceptors(interceptors);
        }
        return template;
    }

}
