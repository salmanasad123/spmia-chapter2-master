package com.thoughtmechanix.organization.security;

import com.thoughtmechanix.organization.config.ServiceConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

/**
 * you need to first tell your authentication service how it’s going to generate and translate JWT tokens.
 * To do this, you’re going to set up in the authentication service a new configuration class
 *
 * The JWTTokenStoreConfig class is used to define how Spring will manage the creation, signing, and translation of a JWT token
 */

@Configuration
public class JwtTokenStoreConfig {

    @Autowired
    ServiceConfig serviceConfig;

    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(jwtAccessTokenConverter());
    }

    // The tokenServices() method is going to use Spring security’s default token services implementation, so the work here is rote
    // The @Primary annotation is used to tell Spring that if there is more than one bean of specific type (in this case DefaultTokenService), use the bean type marked as @Primary for auto-injection
    @Bean
    @Primary
    public DefaultTokenServices tokenServices() {
        DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        defaultTokenServices.setTokenStore(tokenStore());
        defaultTokenServices.setSupportRefreshToken(true);
        return defaultTokenServices;
    }

    // It defines how the token is going to be translated. The most important thing to note about this method is that you’re setting the signing key that will be used to sign your token
    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter() {

        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setSigningKey(serviceConfig.getJwtSigningKey());
        return converter;
    }

}
