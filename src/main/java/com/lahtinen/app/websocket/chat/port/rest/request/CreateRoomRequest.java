package com.lahtinen.app.websocket.chat.port.rest.request;

public class CreateRoomRequest {
    private String roomName;
    private String roomPassword;

    public String getRoomName() {
        return roomName;
    }

    public String getRoomPassword() {
        return roomPassword;
    }
}
