package com.thoughtmechanix.licenses.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;

/**
 * You’re now ready to begin defining the access control rules around the service. To define access control rules,
 * you need to extend a Spring ResourceServerConfigurerAdapter class and override the classes configure() method
 */

@Configuration
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/v1/organizations/**")
                .hasRole("ADMIN")
                .anyRequest()
                .authenticated();
    }
}
