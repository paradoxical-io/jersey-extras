package io.paradoxical.common.web;


import io.paradoxical.common.web.web.WebRequestContext;
import io.paradoxical.common.web.web.filter.ContextProvider;
import io.paradoxical.common.web.web.filter.FilterAttributes;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNull;

public class ContextProviderTest {

    private ContextProvider provider;

    @Test
    public void test_get_domain_context_empty_request() {
        provider = new ContextProvider(null);

        assertNull(provider.getContext());
    }

    @Test
    public void test_get_domain_context_null_attribute() {
        provider = new ContextProvider(new MockHttpServletRequest());

        assertNull(provider.getContext());
    }

    @Test
    public void test_get_domain_context() {
        MockHttpServletRequest request = new MockHttpServletRequest();

        WebRequestContext context = new WebRequestContext();
        context.setCorrId(UUID.randomUUID());

        request.setAttribute(FilterAttributes.CONTEXT, context);

        provider = new ContextProvider(request);

        assertThat(provider.getContext()).isEqualTo(context);
    }

    @Test
    public void test_get_domain_context_different_type() {
        MockHttpServletRequest request = new MockHttpServletRequest();

        request.setAttribute(FilterAttributes.CONTEXT, "not domain context");

        provider = new ContextProvider(request);

        assertNull(provider.getContext());
    }

    @Test
    public void test_get_correlation_id_null_context() {
        provider = new ContextProvider(null);

        assertNull(provider.getCorrelationId());
    }

    @Test
    public void test_get_correlation_id_null_attribute() {
        provider = new ContextProvider(new MockHttpServletRequest());

        assertNull(provider.getCorrelationId());
    }

    @Test
    public void test_get_correlation_id() {
        MockHttpServletRequest request = new MockHttpServletRequest();

        WebRequestContext context = new WebRequestContext();
        UUID randomUUID = UUID.randomUUID();
        context.setCorrId(randomUUID);

        request.setAttribute(FilterAttributes.CONTEXT, context);

        provider = new ContextProvider(request);

        assertThat(randomUUID).isEqualTo(provider.getCorrelationId());
    }

    @Test
    public void test_get_correlation_id_context_different_type() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute(FilterAttributes.CONTEXT, "not domain context");

        provider = new ContextProvider(request);

        assertNull(provider.getCorrelationId());
    }

}
