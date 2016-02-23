package io.paradoxical.common.web.web.filter;

import io.paradoxical.common.web.web.CorrelationRequestContext;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.util.UUID;


public class ContextProvider {

    private HttpServletRequest context;

    @Inject
    public ContextProvider(HttpServletRequest context) {
        this.context = context;
    }

    public UUID getCorrelationId() {
        if (context == null) {
            return null;
        }

        final Object contextAttribute = context.getAttribute(ContextAttributeKeys.CorrelationId.key());

        if (!(contextAttribute instanceof CorrelationRequestContext)) {
            return null;
        }

        return ((CorrelationRequestContext) contextAttribute).getCorrelationId();
    }
}
