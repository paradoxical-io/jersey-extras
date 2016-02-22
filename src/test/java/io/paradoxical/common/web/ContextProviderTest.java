package io.paradoxical.common.web;


import io.paradoxical.common.web.web.CorrelationRequestContext;
import io.paradoxical.common.web.web.filter.ContextAttributeKeys;
import io.paradoxical.common.web.web.filter.ContextProvider;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNull;

public class ContextProviderTest {

    private ContextProvider provider;

    @Test
    public void when_no_request_is_provided_getting_correlation_should_not_error() {
        provider = new ContextProvider(null);

        assertNull(provider.getCorrelationId());
    }

    @Test
    public void when_no_correlation_on_request_getting_correlation_should_not_error() {
        provider = new ContextProvider(new MockHttpServletRequest());

        assertNull(provider.getCorrelationId());
    }

    @Test
    public void test_get_domain_context() {
        MockHttpServletRequest request = new MockHttpServletRequest();

        CorrelationRequestContext context = new CorrelationRequestContext();
        final UUID correlationId = UUID.randomUUID();

        context.setCorrelationId(correlationId);

        request.setAttribute(ContextAttributeKeys.CorrelationId.key(), context);

        provider = new ContextProvider(request);

        assertThat(provider.getCorrelationId()).isEqualTo(correlationId);
    }

    @Test
    public void test_get_domain_context_different_type() {
        MockHttpServletRequest request = new MockHttpServletRequest();

        request.setAttribute(ContextAttributeKeys.CorrelationId.key(), "not domain context");

        provider = new ContextProvider(request);

        assertNull(provider.getCorrelationId());
    }
}
