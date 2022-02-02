package com.thoughtmechanix.authentication.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * At this point you’ve registered a single application, EagleEye, with your OAuth2 server. However, because you’re
 * using a password grant, you need to set up user accounts and passwords for those users before you start
 *
 * Spring can store and retrieve user information (the individual user’s credentials and the roles assigned to the user)
 * from an in-memory data store, a JDBC-backed relational database, or an LDAP server.
 *
 * You need to provide the OAuth2 server a mechanism to authenticate users and return the user information about the authenticating user
 * This is done by defining two beans in your Spring WebSecurityConfigurerAdapter implementation: authenticationManagerBean() and userDetailsServiceBean().
 * These two beans are exposed by using the default authentication authenticationManagerBean() and userDetailsServiceBean() methods from the parent WebSecurityConfigurerAdapter class
 */


@Configuration
public class WebSecurityConfigurer extends WebSecurityConfigurerAdapter { // Extends the core Spring Security WebSecurityConfigurerAdapter

    // The AuthenticationManagerBean is used by Spring Security to handle authentication.
    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    // The UserDetailsService is used by Spring Security to handle user information that will be returned the Spring Security
    @Override
    @Bean
    public UserDetailsService userDetailsServiceBean() throws Exception {
        return super.userDetailsServiceBean();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {

        auth.inMemoryAuthentication()
                .withUser("john.carnell")
                .password("password1")
                .roles("USER")
                .and()
                .withUser("william.woodward")
                .password("password1")
                .roles("ADMIN");
    }
}
