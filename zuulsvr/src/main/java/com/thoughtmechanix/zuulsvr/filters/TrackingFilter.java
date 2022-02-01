package com.thoughtmechanix.zuulsvr.filters;

/**
 * Zuul allows you to build custom logic using a filter within the Zuul gateway. A filter allows you to implement a chain
 * of business logic that each service request passes through as it’s being implemented.
 * Zuul support Pre, Post and Route filters
 */

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * he TrackingFilter will be a pre-filter that will ensure that every request flowing from Zuul has a correlation ID
 * associated with it. A correlation ID is a unique ID that gets carried across all the microservices that are executed
 * when carrying out a customer request. A correlation ID allows you to trace the chain of events that occur as a call
 * goes through a series of microservice calls.
 */

// All Zuul filters must extend the ZuulFilter class and override four methods: filterType(), filterOrder(), shouldFilter(), and run()
@Component
public class TrackingFilter extends ZuulFilter {

    private static final int FILTER_ORDER = 1;
    private static final boolean SHOULD_FILTER = true;
    private static final Logger logger = LoggerFactory.getLogger(TrackingFilter.class);

    @Autowired
    FilterUtils filterUtils;

    // The filterType() method is used to tell Zuul whether the filter is a pre-, route, or post filter.
    @Override
    public String filterType() {
        return FilterUtils.PRE_FILTER_TYPE;
    }

    // The filterOrder() method returns an integer value indicating what order Zuul should send requests through the different filter types
    // what order it should be run in compared to the other filters of its type
    @Override
    public int filterOrder() {
        return FILTER_ORDER;
    }

    // The shouldFilter() method returns a Boolean indicating whether or not the filter should be active
    @Override
    public boolean shouldFilter() {
        return SHOULD_FILTER;
    }

    // The run() method is the code that is executed every time a service passes through the filter. In your run() function,
    // you check to see if the tmx- correlation-id is present and if it isn’t, you generate a correlation value and set
    // the tmx-correlation-id HTTP
    @Override
    public Object run() {
        if (isCorrelationIdPresent()) {
            logger.debug("tmx-correlation-id found in tracking filter: {}. ", filterUtils.getCorrelationId());
        } else {
            filterUtils.setCorrelationId(generateCorrelationId());
            logger.debug("tmx-correlation-id generated in tracking filter: {}.", filterUtils.getCorrelationId());
        }

        // The Request Context holds request, response, state information and data for ZuulFilters to access and share. The RequestContext lives for the duration of the request and is ThreadLocal
        RequestContext ctx = RequestContext.getCurrentContext();
        logger.debug("Processing incoming request for {}.", ctx.getRequest().getRequestURI());
        return ctx;
    }

    // The helper methods that actually check if the tmx-correlation-id is present and can also generate a correlation ID GUIID value
    private boolean isCorrelationIdPresent() {
        if (filterUtils.getCorrelationId() != null) {
            return true;
        }
        return false;
    }

    // generate a random correlation id
    private String generateCorrelationId() {
        return java.util.UUID.randomUUID().toString();
    }
}

