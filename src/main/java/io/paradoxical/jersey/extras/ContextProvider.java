package io.paradoxical.jersey.extras;

import io.paradoxical.jersey.extras.filters.CorrelationIdFilter;

import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import java.util.UUID;

public class ContextProvider {

    private ContainerRequestContext context;

    @Inject
    public ContextProvider(ContainerRequestContext context) {
        this.context = context;
    }

    public UUID getCorrelationId() {
        if (context == null) {
            return null;
        }

        return CorrelationIdFilter.lookupCorrelationId(context);
    }
}
