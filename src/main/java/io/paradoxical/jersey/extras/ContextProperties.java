package io.paradoxical.jersey.extras;

public enum ContextProperties {
    CorrelationId;

    public String key() {
        return ContextProperties.class.getPackage() + "-" + name();
    }
}
