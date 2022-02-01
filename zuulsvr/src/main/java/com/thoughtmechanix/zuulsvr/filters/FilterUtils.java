package com.thoughtmechanix.zuulsvr.filters;

import com.netflix.zuul.context.RequestContext;
import org.springframework.stereotype.Component;

/**
 * Commonly used functions that are used across all your filters have been encapsulated in the FilterUtils class
 */

@Component
public class FilterUtils {

    public static final String CORRELATION_ID = "tmx-correlation-id";
    public static final String AUTH_TOKEN = "tmx-auth-token";
    public static final String USER_ID = "tmx-user-id";
    public static final String ORG_ID = "tmx-org-id";
    public static final String PRE_FILTER_TYPE = "pre";
    public static final String POST_FILTER_TYPE = "post";
    public static final String ROUTE_FILTER_TYPE = "route";

    public String getCorrelationId() {
        // The Request Context holds request, response, state information and data for ZuulFilters to access and share. The RequestContext lives for the duration of the request and is ThreadLocal
        RequestContext ctx = RequestContext.getCurrentContext();

        if (ctx.getRequest().getHeader(CORRELATION_ID) != null) {
            return ctx.getRequest().getHeader(CORRELATION_ID);
        } else {
            return ctx.getZuulRequestHeaders().get(CORRELATION_ID);
        }
    }

    // In the FilterUtils setCorrelationId() method, when you want to add a value to the HTTP request headers,
    // you use the RequestContext’s addZuulRequestHeader() method. This method will maintain a separate map of HTTP
    // headers that were added while a request was flowing through the filters with your Zuul server.
    // The data contained within the ZuulRequestHeader map will be merged when the target service is invoked by your
    // Zuul server
    public void setCorrelationId(String correlationId) {
        // The Request Context holds request, response, state information and data for ZuulFilters to access and share. The RequestContext lives for the duration of the request and is ThreadLocal
        RequestContext ctx = RequestContext.getCurrentContext();
        ctx.addZuulRequestHeader(CORRELATION_ID, correlationId);
    }

    public final String getOrgId() {
        RequestContext ctx = RequestContext.getCurrentContext();
        if (ctx.getRequest().getHeader(ORG_ID) != null) {
            return ctx.getRequest().getHeader(ORG_ID);
        } else {
            return ctx.getZuulRequestHeaders().get(ORG_ID);
        }
    }

    public void setOrgId(String orgId) {
        RequestContext ctx = RequestContext.getCurrentContext();
        ctx.addZuulRequestHeader(ORG_ID, orgId);
    }

    public final String getUserId() {
        RequestContext ctx = RequestContext.getCurrentContext();
        if (ctx.getRequest().getHeader(USER_ID) != null) {
            return ctx.getRequest().getHeader(USER_ID);
        } else {
            return ctx.getZuulRequestHeaders().get(USER_ID);
        }
    }

    public void setUserId(String userId) {
        RequestContext ctx = RequestContext.getCurrentContext();
        ctx.addZuulRequestHeader(USER_ID, userId);
    }

    public final String getAuthToken() {
        RequestContext ctx = RequestContext.getCurrentContext();
        return ctx.getRequest().getHeader(AUTH_TOKEN);
    }

    public String getServiceId() {
        RequestContext ctx = RequestContext.getCurrentContext();

        //We might not have a service id if we are using a static, non-eureka route.
        if (ctx.get("serviceId") == null) return "";
        return ctx.get("serviceId").toString();
    }

}
