package com.thoughtmechanix.licenses.services;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.thoughtmechanix.licenses.clients.OrganizationDiscoveryClient;
import com.thoughtmechanix.licenses.clients.OrganizationFeignClient;
import com.thoughtmechanix.licenses.clients.OrganizationRestTemplateClient;
import com.thoughtmechanix.licenses.config.ServiceConfig;
import com.thoughtmechanix.licenses.model.License;
import com.thoughtmechanix.licenses.model.Organization;
import com.thoughtmechanix.licenses.repository.LicenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * Hystrix and Spring Cloud use the @HystrixCommand annotation to mark Java class methods as being managed by a
 * Hystrix circuit breaker. When the Spring framework sees the @HystrixCommand, it will dynamically generate a
 * proxy that will wrapper the method and manage all calls to that method through a thread pool of threads specifically
 * set aside to handle remote calls
 * Hystrix will also monitor the number of times a call fails and if enough calls fail, Hystrix will automatically
 * prevent future calls from reaching the service by failing the call before the requests ever hit the remote resource
 */

/**
 * Without using a bulkhead pattern, the default behavior for microservice calls is that the calls are executed using the same
 * threads that are reserved for handling requests for the entire Java container. In high volumes,
 * performance problems with one service out of many can result in all of the threads for the Java container
 * being maxed out and waiting to process work, while new requests for work back up. The Java container will eventually crash
 * The problem is if you have services that have far higher volumes or longer completion times then other services,
 * you can end up introducing thread exhaustion into your Hystrix thread pools because one service ends up dominating
 * all of the threads in the default thread pool
 */

@Service
public class LicenseService {

    @Autowired
    private LicenseRepository licenseRepository;

    @Autowired
    ServiceConfig config;

    @Autowired
    OrganizationFeignClient organizationFeignClient;

    @Autowired
    OrganizationRestTemplateClient organizationRestClient;

    @Autowired
    OrganizationDiscoveryClient organizationDiscoveryClient;


    // I’ve added a simple method called retrieveOrgInfo() that will resolve based on the clientType
    // passed into the route the type of client that will be used to look up an organization service instance
    private Organization retrieveOrgInfo(String organizationId, String clientType) {

        Organization organization = null;

        switch (clientType) {
            case "feign":
                System.out.println("I am using the feign client");
                organization = organizationFeignClient.getOrganization(organizationId);
                break;
            case "rest":
                System.out.println("I am using the rest client");
                organization = organizationRestClient.getOrganization(organizationId);
                break;
            case "discovery":
                System.out.println("I am using the discovery client");
                organization = organizationDiscoveryClient.getOrganization(organizationId);
                break;
            default:
                organization = organizationRestClient.getOrganization(organizationId);
        }

        return organization;
    }

    @HystrixCommand
    private Organization getOrganization(String organizationId) {
        return organizationRestClient.getOrganization(organizationId);
    }


    public License getLicense(String organizationId, String licenseId, String clientType) {

        License license = licenseRepository.findByOrganizationIdAndLicenseId(organizationId, licenseId);

        // Organization org = retrieveOrgInfo(organizationId, clientType);  // commented for now, this was used to show different rest clients a microservice can use
        Organization org = getOrganization(organizationId);

        return license
                .withOrganizationName(org.getName())
                .withContactName(org.getContactName())
                .withContactEmail(org.getContactEmail())
                .withContactPhone(org.getContactPhone())
                .withComment(config.getExampleProperty());
    }

    // @HystrixCommand annotation is used to wrapper the getLicenseByOrg() method with a Hystrix circuit breaker
    // The circuit breaker will interrupt any call to the getLicensesByOrg() method any time the call takes longer than 1,000 milliseconds
    // The commandProperties attribute lets you provide additional properties to customize Hystrix
    // The execution.isolation.thread.timeoutInMilliseconds is used to set the length of the timeout (in milliseconds) of the circuit breaker
    // The theadPoolKey defines the unique name of the thread pool
    // The coreSize attribute lets you define the maximum number of threads in the thread pool.
    // The maxQueueSize lets you define a queue that sits in front of your thread pool and that can queue incoming requests, that will control how many requests will be allowed to back up when the threads in the thread pool are busy
    // circuitBreaker.requestVolumeThreshold, controls the amount of consecutive calls that must occur within a 10-second window before Hystrix will consider tripping the circuit breaker for the call
    // The second property, circuitBreaker.errorThresholdPercentage, is the percentage of calls that must fail (due to timeouts, an exception being thrown, or a HTTP 500 being returned) after the circuitBreaker.requestVolumeThreshold value has been passed before the circuit breaker it tripped
    // circuitBreaker.sleepWindowInMilliseconds, is the amount of time Hystrix will sleep once the circuit breaker is tripped before Hystrix will allow another call through to see if the service is healthy again
    // metrics.rollingStats.timeInMilliseconds, is used to control the size of the window that will be used by Hystrix to monitor for problems with a ser- vice call. The default value for this is 10,000 milliseconds
    @HystrixCommand(fallbackMethod = "buildFallbackLicenseList",
            threadPoolKey = "licenseByOrgThreadPool",
            threadPoolProperties =
                    {
                            @HystrixProperty(name = "coreSize", value = "30"),
                            @HystrixProperty(name = "maxQueueSize", value = "10")
                    },
            commandProperties =
                    {
                            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "1000"),
                            @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "10"),
                            @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "75"),
                            @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = "7000"),
                            @HystrixProperty(name = "metrics.rollingStats.timeInMilliseconds", value = "15000")
                    })
    public List<License> getLicensesByOrg(String organizationId) {

        randomlyRunLong();

        return licenseRepository.findByOrganizationId(organizationId);
    }

    public void saveLicense(License license) {
        license.withId(UUID.randomUUID().toString());

        licenseRepository.save(license);

    }

    public void updateLicense(License license) {
        licenseRepository.save(license);
    }

    public void deleteLicense(License license) {
        licenseRepository.delete(license.getLicenseId());
    }


    // using this helper method we will simulate a long-running call that takes more than 1000 milliseconds
    private void randomlyRunLong() {
        Random rand = new Random();

        int randomNum = rand.nextInt((3 - 1) + 1) + 1;

        if (randomNum == 3) sleep();
    }

    private void sleep() {
        try {
            Thread.sleep(11000);  // You sleep for 11,000 millisecond (11 seconds). Default Hystrix behavior is to time a call out after 1 second
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Fallback method to be executed. This fallback method must reside in the same class as the original method
    // that was protected by the @HystrixCommand. The fallback method must have the exact same method signature
    // as the originating function as all of the parameters passed into the original method protected by
    // the @HystrixCommand will be passed to the fallback
    private List<License> buildFallbackLicenseList(String organizationId) {
        List<License> fallbackList = new ArrayList<>();
        License license = new License()
                .withId("0000000-00-00000")
                .withOrganizationId(organizationId)
                .withProductName("Sorry no licensing information currently available");

        fallbackList.add(license);
        return fallbackList;

    }

}
