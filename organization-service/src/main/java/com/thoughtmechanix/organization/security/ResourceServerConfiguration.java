package com.thoughtmechanix.organization.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;

/**
 * You’re now ready to begin defining the access control rules around the service. To define access control rules,
 * you need to extend a Spring ResourceServerConfigurerAdapter class and override the classes configure() method
 */

@Configuration
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

    // Access rules can range from extremely coarse-grained (any authenticated user can access the entire service) to
    // fine-grained (only the application with this role, accessing this URL through a DELETE is allowed)
    // All access rules are configured off the HttpSecurity object passed into the method
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers(HttpMethod.DELETE, "/v1/organizations/**") // The antMatchers() method allows you to restrict the URL and HTTP post that’s protected
                .hasRole("ADMIN")  // The hasRole() method is a comma-separated list of roles that can be accessed
                .anyRequest()
                .authenticated();
    }
}
