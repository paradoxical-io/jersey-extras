package io.paradoxical.jersey.extras.filters;

import org.glassfish.jersey.filter.LoggingFilter;

import javax.annotation.Priority;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Logs all incoming and outgoing requests with their full payloads
 * except for those paths that match the exclude list
 */
@PreMatching
@Priority(Integer.MIN_VALUE + 1)
public class JerseyRequestLoggingFilter implements ContainerRequestFilter, ClientRequestFilter, ContainerResponseFilter,
                                                   ClientResponseFilter, WriterInterceptor {
    private final List<String> pathExcludes;

    private LoggingFilter nativeFilter;

    public JerseyRequestLoggingFilter(String... pathExcludes) {
        this(Arrays.asList(pathExcludes));
    }

    public JerseyRequestLoggingFilter(List<String> pathExcludes) {
        nativeFilter = new LoggingFilter(java.util.logging.Logger.getLogger(LoggingFilter.class.getName()), true);
        this.pathExcludes = pathExcludes;
    }

    @Override
    public void filter(final ClientRequestContext context) throws IOException {
        if (!isEmpty(pathExcludes) && pathExcludes.stream().anyMatch(i -> i.startsWith(context.getUri().getPath()))) {
            return;
        }

        nativeFilter.filter(context);
    }

    @Override
    public void filter(final ContainerRequestContext context) throws IOException {
        if (!isEmpty(pathExcludes) && pathExcludes.stream().anyMatch(i -> i.startsWith(context.getUriInfo().getPath()))) {
            return;
        }

        nativeFilter.filter(context);
    }

    @Override
    public void filter(final ContainerRequestContext requestContext, final ContainerResponseContext responseContext) throws IOException {
        if (!isEmpty(pathExcludes) && pathExcludes.stream().anyMatch(i -> i.startsWith(requestContext.getUriInfo().getPath()))) {
            return;
        }

        nativeFilter.filter(requestContext, responseContext);
    }

    @Override
    public void filter(final ClientRequestContext requestContext, final ClientResponseContext responseContext) throws IOException {
        if (!isEmpty(pathExcludes) && pathExcludes.stream().anyMatch(i -> i.startsWith(requestContext.getUri().getPath()))) {
            return;
        }

        nativeFilter.filter(requestContext, responseContext);
    }

    @Override
    public void aroundWriteTo(final WriterInterceptorContext context) throws IOException, WebApplicationException {
        nativeFilter.aroundWriteTo(context);
    }

    private <T> Boolean isEmpty(List<T> list) {
        return list == null || list.size() == 0;
    }
}
