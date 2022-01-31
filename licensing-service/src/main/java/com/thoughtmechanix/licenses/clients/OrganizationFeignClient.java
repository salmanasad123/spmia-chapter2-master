package com.thoughtmechanix.licenses.clients;

import com.thoughtmechanix.licenses.model.Organization;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * The Feign library takes a different approach to calling a REST service by having the developer first define
 * a Java interface and then annotating that interface with Spring Cloud annotations to map what Eureka-based
 * service Ribbon will invoke. The Spring Cloud framework will dynamically generate a proxy class that will be used
 * to invoke the targeted REST service. There’s no code being written for calling the service other than an interface
 * definition
 */

@FeignClient("organizationservice")  // Identify your service to Feign using the FeignClient Annotation.
public interface OrganizationFeignClient {

    @RequestMapping(method = RequestMethod.GET,
            value = "/v1/organizations/{organizationId}",
            consumes = "application/json")
    public Organization getOrganization(@PathVariable(name = "organizationId") String organizationId);
}
