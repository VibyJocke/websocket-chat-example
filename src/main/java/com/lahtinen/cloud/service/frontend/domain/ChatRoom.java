package com.lahtinen.cloud.service.frontend.domain;

import java.util.HashMap;
import java.util.Map;

public class ChatRoom {

    private final ChatRoomId id;
    private final String name;
    private final String password;
    private final Map<String/* session id*/, Client> clients = new HashMap<>();

    public ChatRoom(String name, String password) {
        this.id = new ChatRoomId();
        this.name = name;
        this.password = password;
    }

    public ChatRoomId getId() {
        return id;
    }
}
