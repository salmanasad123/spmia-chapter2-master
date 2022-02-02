package com.thoughtmechanix.authentication.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.stereotype.Component;

/**
 * At this point you have an authentication service, but haven’t defined any applications, users, or roles
 * within the authentication server. You can begin by registering the application with your authentication service
 * This class will define what applications are registered with your OAuth2 authentication service
 * <p>
 * The OAuth2Config class defines what applications and the user credentials the OAuth2 service knows about
 */

// The AuthenticationServerConfigurer class is a core piece of Spring Security. It provides the basic mechanisms for
// carrying out key authentication and authorization functions
@Configuration
public class OAuth2Config extends AuthorizationServerConfigurerAdapter {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    // configure method is used to define what client applications are registered with your authentication server.
    // The ClientDetailsServiceConfigurer class supports two different types of stores for application information: an in-memory store and a JDBC store. For the purposes of this example, you’re going to use the clients.inMemory() store
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {

        // The first thing you do in this method is register which client applications are allowed to access services protected by the OAuth2 service
        // The two method calls withClient() and secret() provide the name of the application (eagleeye) that you’re registering along with a secret (a password, thisissecret) that will be presented when the EagleEye application calls your OAuth2 server to receive an OAuth2 access token
        // The next method, authorizedGrantTypes(), is passed a comma-separated list of the authorization grant types that will be supported by your OAuth2 service. In your service, you’ll support the password and client credential grants
        // The scopes() method is used to define the boundaries that the calling application can operate in when they’re asking your OAuth2 server for an access token
        clients.inMemory()
                .withClient("eagleeye")
                .secret("thisissecret")
                .authorizedGrantTypes("password", "client_credentials")
                .scopes("webclient", "mobileclient");
    }

    // This method defines the different components used within the Authentication- ServerConfigurer.
    // This code is telling Spring to use the default authentication manager and user details service that comes up with Spring
    // The beans defined in WebSecurityConfigurer class are injected into the configure- (AuthorizationServerEndpointsConfigurer endpoints) method
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints
                .authenticationManager(authenticationManager)
                .userDetailsService(userDetailsService);
    }
}
