package com.lahtinen.app.websocket.chat.domain;

public class Client {
    public final String username;
    public final String sessionId;

    Client(String username, String sessionId) {
        this.username = username;
        this.sessionId = sessionId;
    }
}
