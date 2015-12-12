package io.paradoxical.common.web.web.filter;

import com.google.inject.Inject;
import io.paradoxical.common.interfaces.CorrelationIdGetter;
import io.paradoxical.common.web.web.WebRequestContext;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;


public class ContextProvider implements CorrelationIdGetter {

    private HttpServletRequest context;

    @Inject
    public ContextProvider(HttpServletRequest context) {
        this.context = context;
    }

    public WebRequestContext getContext() {
        return context == null || !(context.getAttribute(FilterAttributes.CONTEXT) instanceof WebRequestContext) ?
               null : (WebRequestContext) context.getAttribute(FilterAttributes.CONTEXT);
    }

    public UUID getCorrelationId() {
        if (context == null) {
            return null;
        }
        if (!(context.getAttribute(FilterAttributes.CONTEXT) instanceof WebRequestContext)) {
            return null;
        }

        return ((WebRequestContext) context.getAttribute(FilterAttributes.CONTEXT)).getCorrId();
    }

}
