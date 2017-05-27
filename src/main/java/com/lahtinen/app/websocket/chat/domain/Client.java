package com.lahtinen.app.websocket.chat.domain;

import javax.websocket.Session;

public class Client {

    private final String username;
    private final Session session;

    public Client(String username, Session session) {
        this.username = username;
        this.session = session;
    }
}