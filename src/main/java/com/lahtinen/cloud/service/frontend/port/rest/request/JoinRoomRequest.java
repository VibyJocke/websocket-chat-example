package com.lahtinen.cloud.service.frontend.port.rest.request;

public class JoinRoomRequest {
    private String userName;
    private String roomName;
    private String roomPassword;

    public String getUserName() {
        return userName;
    }

    public String getRoomName() {
        return roomName;
    }

    public String getRoomPassword() {
        return roomPassword;
    }
}
