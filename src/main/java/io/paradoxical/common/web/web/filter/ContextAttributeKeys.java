package io.paradoxical.common.web.web.filter;

public enum ContextAttributeKeys {
    CorrelationId;

    public String key() {
        return ContextAttributeKeys.class.getPackage() + "-" + name();
    }
}
