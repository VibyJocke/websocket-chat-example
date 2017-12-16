package com.lahtinen.app.websocket.chat.port.rest.response;

import java.util.Collection;

public class JoinRoomResponse {
    private final Collection<String> users;

    public JoinRoomResponse(Collection<String> users) {
        this.users = users;
    }
}
