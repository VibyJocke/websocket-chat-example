package com.lahtinen.cloud.service.frontend.port.rest.response;

import java.util.List;

public class JoinRoomResponse {
    private final List<String> users;

    public JoinRoomResponse(List<String> users) {
        this.users = users;
    }
}
