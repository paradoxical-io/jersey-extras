package io.paradoxical.jersey.extras.filters;

import com.godaddy.logging.Logger;
import com.google.common.base.Strings;
import io.paradoxical.jersey.extras.ContextProperties;
import io.paradoxical.jersey.extras.NamedRequestHeader;
import io.paradoxical.jersey.extras.WellKnownHeaders;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.slf4j.MDC;

import javax.annotation.Priority;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.container.PreMatching;
import java.io.IOException;
import java.util.UUID;

import static com.godaddy.logging.LoggerFactory.getLogger;

@PreMatching
@Priority(Integer.MIN_VALUE)
public class CorrelationIdFilter implements ContainerRequestFilter, ContainerResponseFilter {

    private static final Logger logger = getLogger(CorrelationIdFilter.class);

    private final NamedRequestHeader correlationIdHeaderName;
    private final String correlationIdLoggingKey;

    @SuppressWarnings("unused")
    public CorrelationIdFilter() {
        this(WellKnownHeaders.CorrelationId, LoggingProperties.DEFAULT_CORRELATION_ID_KEY);
    }

    public CorrelationIdFilter(String correlationIdLoggingKey) {
        this(WellKnownHeaders.CorrelationId, correlationIdLoggingKey);
    }

    public CorrelationIdFilter(NamedRequestHeader correlationIdHeaderName) {
        this(correlationIdHeaderName, LoggingProperties.DEFAULT_CORRELATION_ID_KEY);
    }

    public CorrelationIdFilter(@NonNull NamedRequestHeader correlationIdHeaderName, @NonNull String correlationIdLoggingKey) {
        this.correlationIdHeaderName = correlationIdHeaderName;
        this.correlationIdLoggingKey = correlationIdLoggingKey;
    }

    public static UUID lookupCorrelationId(ContainerRequestContext context) {

        final Object contextAttribute = context.getProperty(ContextProperties.CorrelationId.key());

        if (contextAttribute == null ||
            !(contextAttribute instanceof CorrelationRequestContext)) {
            return null;
        }

        return ((CorrelationRequestContext) contextAttribute).getCorrelationId();
    }

    @Override
    public void filter(ContainerRequestContext context) {

        if (context == null) {
            return;
        }

        final Object requestAttribute = context.getProperty(ContextProperties.CorrelationId.key());

        if (requestAttribute == null) {
            setCorrelationId(new CorrelationRequestContext(), context);
        }
        else if (requestAttribute instanceof CorrelationRequestContext) {
            setCorrelationId((CorrelationRequestContext) requestAttribute, context);
        }
    }

    @Override
    public void filter(final ContainerRequestContext requestContext, final ContainerResponseContext responseContext) throws IOException {
        /**
         * Intentionally don't remove the correlation id since async responses can potentially run on the same thread
         * So if you complete an async response from the current thread, you will lose the correlation id that was set
         *
         * This is safe because every new request gets a correlation id from the header, there's no way to "leave" a correlation id
         * in a thread since all new threads will get new correlatoin id
         */

        final UUID correlationId = lookupCorrelationId(requestContext);

        if (correlationId == null) {
            return;
        }

        responseContext.getHeaders()
                       .add(correlationIdHeaderName.headerName(),
                            correlationId.toString());
    }

    private void setCorrelationId(
            final CorrelationRequestContext correlationContext,
            final ContainerRequestContext requestContext) {

        final String requestHeader = requestContext.getHeaderString(correlationIdHeaderName.headerName());

        UUID corrId = Strings.isNullOrEmpty(requestHeader) ? UUID.randomUUID() : tryParseCorrelationId(requestHeader);

        correlationContext.setCorrelationId(corrId);

        requestContext.setProperty(ContextProperties.CorrelationId.key(), correlationContext);

        MDC.put(correlationIdLoggingKey, correlationContext.getCorrelationId().toString());
    }

    private UUID tryParseCorrelationId(final String requestHeader) {
        try {
            return UUID.fromString(requestHeader);
        }
        catch (IllegalArgumentException e) {
            final UUID generatedCorrelationId = UUID.randomUUID();

            logger.with("headerCorrelationId", requestHeader)
                  .with("generatedCorrelationId", generatedCorrelationId)
                  .error(e, "Error parsing correlation id from header string");

            return generatedCorrelationId;
        }
    }

    static final class CorrelationRequestContext {

        @Getter
        @Setter
        private UUID correlationId;

    }

    public static final class LoggingProperties {
        private LoggingProperties() {
        }

        public static final String DEFAULT_CORRELATION_ID_KEY = "corrId";
    }
}




