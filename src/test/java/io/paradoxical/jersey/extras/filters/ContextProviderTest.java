package io.paradoxical.jersey.extras.filters;


import io.paradoxical.jersey.extras.ContextProperties;
import io.paradoxical.jersey.extras.ContextProvider;
import org.glassfish.jersey.internal.MapPropertiesDelegate;
import org.glassfish.jersey.server.ContainerRequest;
import org.junit.Test;

import java.net.URI;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNull;

public class ContextProviderTest {


    @Test
    public void when_no_request_is_provided_getting_correlation_should_not_error() {
        final ContextProvider provider = new ContextProvider(null);

        assertNull(provider.getCorrelationId());
    }

    @Test
    public void when_no_correlation_on_request_getting_correlation_should_not_error() {

        final MapPropertiesDelegate propertiesDelegate = new MapPropertiesDelegate();

        ContainerRequest containerRequest = new ContainerRequest(URI.create("http://test"),
                                                                 URI.create("http://test/path"),
                                                                 "POST",
                                                                 null,
                                                                 propertiesDelegate);

        final ContextProvider provider = new ContextProvider(containerRequest);

        assertNull(provider.getCorrelationId());
    }

    @Test
    public void test_get_domain_context() {
        final MapPropertiesDelegate propertiesDelegate = new MapPropertiesDelegate();

        ContainerRequest containerRequest = new ContainerRequest(URI.create("http://test"),
                                                  URI.create("http://test/path"),
                                                  "POST",
                                                  null,
                                                  propertiesDelegate);

        CorrelationIdFilter.CorrelationRequestContext context = new CorrelationIdFilter.CorrelationRequestContext();
        final UUID correlationId = UUID.randomUUID();

        context.setCorrelationId(correlationId);

        containerRequest.setProperty(ContextProperties.CorrelationId.key(), context);

        final ContextProvider provider = new ContextProvider(containerRequest);

        assertThat(provider.getCorrelationId()).isEqualTo(correlationId);
    }

    @Test
    public void test_get_domain_context_different_type() {

        final MapPropertiesDelegate propertiesDelegate = new MapPropertiesDelegate();

        ContainerRequest containerRequest = new ContainerRequest(URI.create("http://test"),
                                                                 URI.create("http://test/path"),
                                                                 "POST",
                                                                 null,
                                                                 propertiesDelegate);

        containerRequest.setProperty(ContextProperties.CorrelationId.key(), "not correlation id context");

        final ContextProvider provider = new ContextProvider(containerRequest);

        assertNull(provider.getCorrelationId());
    }
}
