package com.thoughtmechanix.zuulsvr.filters;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Zuul executes the actual HTTP call on behalf of the service client. Zuul has the opportunity to inspect the response
 * back from the target service call and then alter the response or decorate it with additional information.
 * When coupled with cap- turing data with the pre-filter, a Zuul post filter is an ideal location to collect metrics
 * and complete any logging associated with the user’s transaction. You’ll want to take advantage of this by injecting
 * the correlation ID that you’ve been passing around to your microservices back to the user.
 * You’re going to do this by using a Zuul post filter to inject the correlation ID back into the HTTP response headers
 * being passed back to the caller of the service. This way, you can pass the correlation ID back to the caller without
 * ever having to touch the message body
 */

@Component
public class ResponseFilter extends ZuulFilter {

    private static final int FILTER_ORDER = 1;
    private static final boolean SHOULD_FILTER = true;
    private static final Logger logger = LoggerFactory.getLogger(ResponseFilter.class);

    @Autowired
    FilterUtils filterUtils;

    // To build a post filter you need to set the filter type to be POST_FILTER_TYPE.
    @Override
    public String filterType() {
        return FilterUtils.POST_FILTER_TYPE;
    }

    @Override
    public int filterOrder() {
        return FILTER_ORDER;
    }

    @Override
    public boolean shouldFilter() {
        return SHOULD_FILTER;
    }

    @Override
    public Object run() {

        RequestContext ctx = RequestContext.getCurrentContext();

        logger.debug("Adding the correlation id to the outbound headers. {}", filterUtils.getCorrelationId());
        // Grab the correlation ID that was passed in on the original HTTP request and inject it into the response.
        ctx.getResponse().addHeader(FilterUtils.CORRELATION_ID, filterUtils.getCorrelationId());

        logger.debug("Completing outgoing request for {}.", ctx.getRequest().getRequestURI());

        return ctx;
    }
}
