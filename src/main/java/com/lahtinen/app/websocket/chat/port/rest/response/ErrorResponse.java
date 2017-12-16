package com.lahtinen.app.websocket.chat.port.rest.response;

public class ErrorResponse {
    private final String message;

    public ErrorResponse(String message) {
        this.message = message;
    }
}
