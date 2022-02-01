package com.thoughtmechanix.licenses.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * You’ve guaranteed that a correlation ID has been added to every microservice call flowing through Zuul,
 * how do you ensure that
 * 1.  The correlation-ID is readily accessible to the microservice that’s being invoked
 * 2. Any downstream service calls the microservice might make also propagate the correlation-ID on to the downstream call
 */

/**
 * This class is an HTTP servlet filter that will intercept all incoming HTTP requests coming into the service and
 * map the correlation ID (and a few other values) from the HTTP request to the UserContext class
 * The filter is registered and picked up by Spring through the use of the Spring @Component annotation and
 * by implementing a javax.servler.Filter interface
 */

// Ultimately, the UserContextFilter is used to map the HTTP header values you’re interested in into a Java class, UserContext

@Component
public class UserContextFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(UserContextFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;

        // Your filter retrieves the correlation ID from the header and sets the value on the UserContext class. The other values being scraped from the HTTP Headers will come into play if you use the authentication service
        UserContextHolder.getContext().setCorrelationId(httpServletRequest.getHeader(UserContext.CORRELATION_ID));
        UserContextHolder.getContext().setUserId(httpServletRequest.getHeader(UserContext.USER_ID));
        UserContextHolder.getContext().setAuthToken(httpServletRequest.getHeader(UserContext.AUTH_TOKEN));
        UserContextHolder.getContext().setOrgId(httpServletRequest.getHeader(UserContext.ORG_ID));

        logger.debug("License Service Incoming Correlation id: {}", UserContextHolder.getContext().getCorrelationId());

        filterChain.doFilter(httpServletRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }
}
