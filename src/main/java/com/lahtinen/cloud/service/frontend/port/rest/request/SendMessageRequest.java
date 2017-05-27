package com.lahtinen.cloud.service.frontend.port.rest.request;

public class SendMessageRequest {
    private final String message;

    public SendMessageRequest(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
