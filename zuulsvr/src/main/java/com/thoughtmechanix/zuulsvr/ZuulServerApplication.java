package com.thoughtmechanix.zuulsvr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

/**
 * Zuul at its heart is a reverse proxy. A reverse proxy is an intermediate server that sits between the client
 * trying to reach a resource and the resource itself. The client has no idea it’s even communicating to a server other
 * than a proxy. The reverse proxy takes care of capturing the client’s request and then calls the remote resource on
 * the client’s behalf.
 * In the case of a microservices architecture, Zuul (your reverse proxy) takes a microservice call from a client and
 * forwards it onto the downstream service
 * The service client thinks it’s only communicating with Zuul. For Zuul to communicate with the downstream clients,
 * Zuul has to know how to map the incoming call to a downstream route. Zuul has several mechanisms to do this, including
 * 1. Automated mapping of routes via service discovery
 * 2. Manual mapping of routes using service discovery
 * 3. Manual mapping of routes using static URLs
 */

/**
 * @EnableZuulServer is used when you want to build your own routing service and not use any Zuul pre-built capabilities.
 * An example of this would be if you wanted to use Zuul to integrate with a service discovery engine other than Eureka (for example, Consul
 * The Zuul proxy server is designed by default to work on the Spring products. As such, Zuul will automatically use
 * Eureka to look up services by their service IDs and then use Netflix Ribbon to do client-side load balancing of requests
 * from within Zuul.
 */

@SpringBootApplication
@EnableEurekaClient
@EnableDiscoveryClient
@EnableZuulProxy      // Enables the service to be a Zuul server
public class ZuulServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZuulServerApplication.class, args);
    }
}
