package com.lahtinen.cloud.service.frontend.port.rest.request;

public class Request {

    private final String type;
    private final Object content;

    public Request(String type, Object content) {
        this.type = type;
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public Object getContent() {
        return content;
    }
}
