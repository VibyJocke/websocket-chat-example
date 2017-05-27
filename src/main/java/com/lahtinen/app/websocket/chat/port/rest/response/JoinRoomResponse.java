package com.lahtinen.app.websocket.chat.port.rest.response;

import java.util.List;

public class JoinRoomResponse {
    private final List<String> users;

    public JoinRoomResponse(List<String> users) {
        this.users = users;
    }
}
