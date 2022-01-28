package com.thoughtmechanix.licenses.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


/**
 * While Spring Data “auto-magically” injects the configuration data for the database
 * into a database connection object, all other properties must be injected using the
 * @Value annotation.
 */

@Component
public class ServiceConfig{

    @Value("${example.property}")
    private String exampleProperty;

    public String getExampleProperty(){
        return exampleProperty;
    }
}
