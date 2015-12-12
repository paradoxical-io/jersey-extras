package io.paradoxical.common.web;


import io.paradoxical.common.web.web.WebRequestContext;
import io.paradoxical.common.web.web.filter.CorrelationIdFilter;
import io.paradoxical.common.web.web.filter.FilterAttributes;
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
        assertNull(request.getAttribute(FilterAttributes.CONTEXT));
        target().path("test").request().get();
        assertNotNull(request.getAttribute(FilterAttributes.CONTEXT));
    }

    @Test
    public void test_with_domain_context() {
        WebRequestContext context = new WebRequestContext();
        request.setAttribute(FilterAttributes.CONTEXT, context);
        target().path("test").request().get();
        assertNotNull(request.getAttribute(FilterAttributes.CONTEXT));
        //When domain context has more fields we can verify that those fields stayed in tack.
    }

    @Test
    public void test_mdc() {
        String corrId = target().path("test").request().get(String.class);
        assertNotNull(corrId);
    }

    @Test
    public void test_header_contains_corrid() {
        request.addHeader(FilterAttributes.CORRELATION_ID_HEADER, "8ef3a75e-cddb-48b6-83d6-d3377e068831");
        target().path("test").request().get();
        assertThat(request.getAttribute(FilterAttributes.CONTEXT)).isInstanceOf(WebRequestContext.class);

        WebRequestContext context = (WebRequestContext) request.getAttribute(FilterAttributes.CONTEXT);

        assertThat(context.getCorrId().toString()).isEqualTo("8ef3a75e-cddb-48b6-83d6-d3377e068831");
    }

    @Path("test")
    public static class Resource {
        @GET
        public String getTest() {
            return MDC.get(FilterAttributes.CORR_ID).toString();
        }
    }

}
