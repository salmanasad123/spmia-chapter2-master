package com.thoughtmechanix.licenses.clients;

import com.thoughtmechanix.licenses.model.Organization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class OrganizationRestTemplateClient {

    @Autowired
    RestTemplate restTemplate;

    public Organization getOrganization(String organizationId) {

        // The server name in the URL matches the application ID of the organizationservice key that you registered the organization service with in Eureka
        // http://{applicationid}/v1/organizations/{organizationId}
        // The Ribbon-enabled RestTemplate will parse the URL passed into it and use what- ever is passed in as the server name as the key to query Ribbon for an instance of a service.
        // By using the RestTemplate class, Ribbon will round-robin load balance all requests among all the service instances.
        ResponseEntity<Organization> restExchange =
                restTemplate.exchange("http://organizationservice/v1/organizations/{organizationId}",
                        HttpMethod.GET,
                        null, Organization.class, organizationId);

        return restExchange.getBody();
    }
}
