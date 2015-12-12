package io.paradoxical.common.web.web.filter;

import io.paradoxical.common.web.web.WebRequestContext;
import org.slf4j.MDC;

import javax.annotation.Priority;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Context;
import java.io.IOException;
import java.util.UUID;

@PreMatching
@Priority(Integer.MIN_VALUE)
public class CorrelationIdFilter implements ContainerRequestFilter, ContainerResponseFilter {

    @Context private HttpServletRequest request;

    @Override
    public void filter(ContainerRequestContext context) {

        if (context == null || request == null) {
            return;
        }

        if (request.getAttribute(FilterAttributes.CONTEXT) == null) {
            setCorrelationId(new WebRequestContext());
        }
        else if (request.getAttribute(FilterAttributes.CONTEXT) instanceof WebRequestContext) {
            setCorrelationId((WebRequestContext) request.getAttribute(FilterAttributes.CONTEXT));
        }
    }

    @Override public void filter(final ContainerRequestContext requestContext, final ContainerResponseContext responseContext) throws IOException {
        /**
         * Intentionally don't remove the correlation id since async responses can potentially run on the same thread
         * So if you complete an async response from the current thread, you will lose the correlation id that was set
         *
         * This is safe because every new request gets a correlation id from the header, there's no way to "leave" a correlation id
         * in a thread since all new threads will get new correlatoin id
         */
    }

    private void setCorrelationId(WebRequestContext domainContext) {
        UUID corrId = request.getHeader(FilterAttributes.CORRELATION_ID_HEADER) ==
                      null ? UUID.randomUUID() : UUID.fromString(request.getHeader(FilterAttributes.CORRELATION_ID_HEADER));
        domainContext.setCorrId(corrId);

        request.setAttribute(FilterAttributes.CONTEXT, domainContext);

        MDC.put(FilterAttributes.CORR_ID, domainContext.getCorrId().toString());
    }
}




