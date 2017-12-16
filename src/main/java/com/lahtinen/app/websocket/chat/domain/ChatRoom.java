package com.lahtinen.app.websocket.chat.domain;

import java.util.HashMap;
import java.util.Map;

public class ChatRoom {

    public final String name;
    public final String password;
    public final Map<String/* session id*/, Client> clients = new HashMap<>();

    public ChatRoom(String name, String password) {
        this.name = name;
        this.password = password;
    }
}
