package com.thoughtmechanix.licenses.clients;

import com.thoughtmechanix.licenses.model.Organization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * Using the DiscoveryClient, you can query for all the services registered with the ribbon client and their
 * corresponding URLs.
 */
@Component
public class OrganizationDiscoveryClient {

    // Using DiscoveryClient we are querying ribbon to know what services instances are registered with it
    @Autowired
    private DiscoveryClient discoveryClient;  // class we used to interact with ribbon

    public Organization getOrganization(String organizationId) {

        RestTemplate restTemplate = new RestTemplate();

        // get a list of all the instances of organization service registered with eureka passing in the ID of service
        // registered with Eureka, which is the application.name property of organization service
        // The <ServiceInstance> class is used to hold information about a specific instance of a service including its hostname, port and URI.
        List<ServiceInstance> instances = discoveryClient.getInstances("organizationservice");

        if (instances.size() == 0) return null;

        // we take the first ServiceInstance class in our list to build a target URL that can then be used to call your service
        // retrieve the service end point we are going to call
        String serviceUri = String.format("%s/v1/organizations/%s", instances.get(0).getUri().toString(), organizationId);

        // uses a standard spring rest template class to call the service
        ResponseEntity<Organization> restExchange =
                restTemplate.exchange(
                        serviceUri,
                        HttpMethod.GET,
                        null, Organization.class, organizationId);

        return restExchange.getBody();
    }
}
