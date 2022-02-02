package com.thoughtmechanix.organization.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


/**
 * While Spring Data “auto-magically” injects the configuration data for the database
 * into a database connection object, all other properties must be injected using the
 * @Value annotation.
 */

@Component
public class ServiceConfig {

    @Value("${example.property}")
    private String exampleProperty;

    @Value("${signing.key}")
    private String jwtSigningKey = "";


    public String getJwtSigningKey() {
        return jwtSigningKey;
    }

    public String getExampleProperty(){
        return exampleProperty;
    }
}
