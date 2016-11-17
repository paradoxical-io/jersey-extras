package io.paradoxical.jersey.extras.filters;


import io.paradoxical.jersey.extras.WellKnownHeaders;
import org.apache.log4j.MDC;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;

public class CorrelationIdFilterTest extends JerseyTest {

    @Override
    protected Application configure() {
        ResourceConfig config = new ResourceConfig(CorrelationIdFilter.class, Resource.class);
        return config;
    }

    @Test
    public void test_no_context() {
        final Response response = target().path("test").request().get();

        assertNotNull(response.getHeaderString(WellKnownHeaders.CorrelationId.headerName()));
    }

    @Test
    public void test_mdc() {
        String corrId = target().path("test").request().get(String.class);
        assertNotNull(corrId);
    }

    @Test
    public void test_header_contains_corrid() {
        final String testCorrelationId = "8ef3a75e-cddb-48b6-83d6-d3377e068831";

        final Response response =
                target().path("test")
                        .request()
                        .header(WellKnownHeaders.CorrelationId.headerName(), testCorrelationId)
                        .get();

        final String value = response.getHeaderString(WellKnownHeaders.CorrelationId.headerName());

        assertThat(value).isEqualTo(testCorrelationId);
    }

    @Path("test")
    public static class Resource {
        @GET
        public String getTest() {
            return MDC.get(CorrelationIdFilter.LoggingProperties.CORR_ID)
                      .toString();
        }
    }

}
