package com.lahtinen.cloud.service.frontend.domain;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.Session;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

// TODO: websocket.Session should not exist here as it is not a domain entity.
public class ChatService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChatService.class);
    private static final Gson GSON = new Gson();

    private final Map<String /* session id */, Session> userSessions = new HashMap<>();
    private final Map<ChatRoomId, ChatRoom> chatRooms = new HashMap<>();

    public static ChatService getInstance() {
        return Holder.INSTANCE;
    }

    public void connected(Session session) {
        userSessions.put(session.getId(), session);
    }

    public void disconnected(Session session) {
        userSessions.remove(session.getId());
    }

    public void createRoom(String name, String password) {
        ChatRoom chatRoom = new ChatRoom(name, password);
        chatRooms.put(chatRoom.getId(), chatRoom);
    }

    public void connectToRoom(Session session, String roomName) {
        // TODO Implement
    }

    public void sendMessage(String message) {
        // TODO Implement
    }

    private void sendMessage(Session session, Object message) {
        try {
            session.getBasicRemote().sendText(GSON.toJson(message));
        } catch (IOException e) {
            LOGGER.warn("Failed to send message", e);
        }
    }

    private static class Holder {
        static final ChatService INSTANCE = new ChatService();
    }
}
