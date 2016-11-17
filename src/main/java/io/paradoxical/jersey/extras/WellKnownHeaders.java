package io.paradoxical.jersey.extras;

public enum WellKnownHeaders implements NamedRequestHeader {
    CorrelationId("X-CorrelationId");


    private final String headerName;

    WellKnownHeaders(final String headerName) {
        this.headerName = headerName;
    }

    @Override
    public String headerName() {
        return headerName;
    }

}
