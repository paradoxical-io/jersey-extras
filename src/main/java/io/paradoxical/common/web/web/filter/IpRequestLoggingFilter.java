package io.paradoxical.common.web.web.filter;

import com.godaddy.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import java.io.IOException;
import java.util.function.Consumer;

import static com.godaddy.logging.LoggerFactory.getLogger;

public class IpRequestLoggingFilter implements ContainerRequestFilter {

    private static final Logger logger = getLogger(IpRequestLoggingFilter.class);
    private final Consumer<HttpServletRequest> requestProcessor;

    @Context private HttpServletRequest request;

    public IpRequestLoggingFilter() {
        this(r -> logger.with("remote-addr", r.getRemoteAddr())
                        .with("remote-host", r.getRemoteHost())
                        .info("Request context"));
    }

    public IpRequestLoggingFilter(Consumer<HttpServletRequest> requestProcessor) {
        this.requestProcessor = requestProcessor;
    }

    @Override public void filter(final ContainerRequestContext requestContext) throws IOException {
        if (request == null) {
            return;
        }

        if(requestProcessor == null){
            return;
        }

        requestProcessor.accept(request);
    }
}
