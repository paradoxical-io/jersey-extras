package io.paradoxical.common.web.web.filter;

import io.paradoxical.common.web.web.CorrelationRequestContext;
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

    private final NamedRequestHeader correlationIdHeaderName;
    @Context private HttpServletRequest request;

    @SuppressWarnings("unused")
    public CorrelationIdFilter() {
        this(WellKnownHeaders.CorrelationId);
    }

    public CorrelationIdFilter(NamedRequestHeader correlationIdHeaderName) {
        this.correlationIdHeaderName = correlationIdHeaderName;
    }


    @Override
    public void filter(ContainerRequestContext context) {

        if (context == null || request == null) {
            return;
        }

        final Object requestAttribute = request.getAttribute(ContextAttributeKeys.CorrelationId.key());

        if (requestAttribute == null) {
            setCorrelationId(new CorrelationRequestContext());
        }
        else if (requestAttribute instanceof CorrelationRequestContext) {
            setCorrelationId((CorrelationRequestContext) requestAttribute);
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

    private void setCorrelationId(CorrelationRequestContext domainContext) {
        final String requestHeader = request.getHeader(correlationIdHeaderName.headerName());

        UUID corrId = requestHeader == null ? UUID.randomUUID() : UUID.fromString(requestHeader);

        domainContext.setCorrelationId(corrId);

        request.setAttribute(ContextAttributeKeys.CorrelationId.key(), domainContext);

        MDC.put(LoggingProperties.CORR_ID, domainContext.getCorrelationId().toString());
    }
}




