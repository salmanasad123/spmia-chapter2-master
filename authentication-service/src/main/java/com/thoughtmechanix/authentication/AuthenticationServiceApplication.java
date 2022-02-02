package com.thoughtmechanix.authentication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * OAuth2 is a token-based security authentication and authorization framework that breaks security down into
 * four components
 * 1. A protected resource
 * 2. A resource owner
 * 3. An application
 * 4. OAuth2 authentication server
 */

@SpringBootApplication
@RestController
@EnableResourceServer  // Convenient annotation for OAuth2 Resource Servers, enabling a Spring Security filter that authenticates requests via an incoming OAuth2 token. Users should add this annotation
@EnableAuthorizationServer      // This annotation tells Spring Cloud that this service will be used as an OAuth2 ser- vice and to add several REST-based endpoints that will be used in the OAuth2 authentication and authorization processes
public class AuthenticationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthenticationServiceApplication.class, args);
    }


    // Used to retrieve information about the user
    // This endpoint is called by the protected service to validate the OAuth2 access token and retrieve the assigned roles of the user accessing the protected service
    @RequestMapping(value = {"/user"}, produces = "application/json")
    public Map<String, Object> user(OAuth2Authentication user) {
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("user", user.getUserAuthentication().getPrincipal());
        userInfo.put("authorities", AuthorityUtils.authorityListToSet(user.getUserAuthentication().getAuthorities()));
        return userInfo;
    }

}
