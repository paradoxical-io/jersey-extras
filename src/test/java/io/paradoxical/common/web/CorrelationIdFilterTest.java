package io.paradoxical.common.web;


import io.paradoxical.common.web.web.CorrelationRequestContext;
import io.paradoxical.common.web.web.filter.ContextAttributeKeys;
import io.paradoxical.common.web.web.filter.CorrelationIdFilter;
import io.paradoxical.common.web.web.filter.LoggingProperties;
import io.paradoxical.common.web.web.filter.WellKnownHeaders;
import org.apache.log4j.MDC;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class CorrelationIdFilterTest extends JerseyTest {

    private MockHttpServletRequest request = new MockHttpServletRequest();
    private final static ContextAttributeKeys correlationIdContextKey = ContextAttributeKeys.CorrelationId;

    @Override
    protected Application configure() {
        ResourceConfig config = new ResourceConfig(CorrelationIdFilter.class, Resource.class);
        config.register(new AbstractBinder() {
            @Override
            protected void configure() {
                bind(request).to(HttpServletRequest.class);
            }
        });
        return config;
    }

    @Test
    public void test_no_domain_context() {
        assertNull(request.getAttribute(correlationIdContextKey.key()));
        target().path("test").request().get();
        assertNotNull(request.getAttribute(correlationIdContextKey.key()));
    }

    @Test
    public void test_with_domain_context() {
        CorrelationRequestContext context = new CorrelationRequestContext();
        request.setAttribute(correlationIdContextKey.key(), context);
        target().path("test").request().get();
        assertNotNull(request.getAttribute(correlationIdContextKey.key()));
        //When domain context has more fields we can verify that those fields stayed in tack.
    }

    @Test
    public void test_mdc() {
        String corrId = target().path("test").request().get(String.class);
        assertNotNull(corrId);
    }

    @Test
    public void test_header_contains_corrid() {
        final String testCorrelationId = "8ef3a75e-cddb-48b6-83d6-d3377e068831";

        request.addHeader(WellKnownHeaders.CorrelationId.headerName(), testCorrelationId);
        target().path("test").request().get();
        assertThat(request.getAttribute(correlationIdContextKey.key())).isInstanceOf(CorrelationRequestContext.class);

        CorrelationRequestContext context = (CorrelationRequestContext) request.getAttribute(correlationIdContextKey.key());

        assertThat(context.getCorrelationId().toString()).isEqualTo(testCorrelationId);
    }

    @Path("test")
    public static class Resource {
        @GET
        public String getTest() {
            return MDC.get(LoggingProperties.CORR_ID).toString();
        }
    }

}
