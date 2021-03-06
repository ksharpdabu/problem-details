package com.github.t1.problemdetail.ri;

import com.github.t1.problemdetail.ri.lib.ProblemDetailBuilder;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.StatusType;
import java.net.URI;

class JaxRsProblemDetailBuilder extends ProblemDetailBuilder {
    private final HttpHeaders requestHeaders;
    private final Response response;

    JaxRsProblemDetailBuilder(Throwable exception, HttpHeaders requestHeaders) {
        this(exception, requestHeaders, null);
    }

    JaxRsProblemDetailBuilder(Throwable exception, HttpHeaders requestHeaders, Response response) {
        super(exception);
        this.requestHeaders = requestHeaders;
        this.response = response;
    }

    @Override protected StatusType fallbackStatus() {
        return (response != null) ? response.getStatusInfo() : super.fallbackStatus();
    }

    @Override protected URI buildTypeUri() {
        if (response != null)
            return problemTypeUrn(response.getStatusInfo().getReasonPhrase());
        return super.buildTypeUri();
    }

    @Override protected String fallbackTitle() {
        if (response != null)
            return response.getStatusInfo().getReasonPhrase();
        return super.fallbackTitle();
    }

    @Override protected boolean hasDefaultMessage() {
        return exception.getMessage() != null
            && exception.getMessage().equals("HTTP " + getStatus().getStatusCode()
            + " " + getStatus().getReasonPhrase());
    }

    @Override protected String findMediaTypeSubtype() {
        for (MediaType accept : requestHeaders.getAcceptableMediaTypes()) {
            if ("application".equals(accept.getType())) {
                return accept.getSubtype();
            }
        }
        return "json";
    }

    @Override public JaxRsProblemDetailBuilder log() {
        super.log();
        return this;
    }

    public Response toResponse() {
        return Response
            .status(getStatus())
            .entity(getBody())
            .header("Content-Type", getMediaType())
            .build();
    }
}
