package com.lahtinen.app.websocket.chat.domain;

import com.google.gson.Gson;
import com.lahtinen.app.websocket.chat.port.rest.response.MessageResponse;
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
    private final Map<String /* chat room name*/, ChatRoom> chatRooms = new HashMap<>();

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
        if (chatRooms.containsKey(name)) {
            throw new IllegalArgumentException("Chat room names must be unique");
        }
        chatRooms.put(name, new ChatRoom(name, password));
    }

    public void connectToRoom(String sessionId, String roomName, String userName, String password) {
        validateRoomExists(roomName);

        if (!chatRooms.get(roomName).password.equals(password)) {
            throw new IllegalArgumentException("Access denied: invalid password");
        }

        chatRooms.get(roomName).clients.put(sessionId, new Client(userName, userSessions.get(sessionId)));
    }

    public void sendMessage(String roomName, String message) {
        validateRoomExists(roomName);

        final MessageResponse messageResponse = new MessageResponse(message);
        chatRooms.get(roomName).clients.values().forEach(
                client -> broadcastMessage(client.session, messageResponse)
        );
    }

    private void broadcastMessage(Session session, Object message) {
        try {
            session.getBasicRemote().sendText(GSON.toJson(message));
        } catch (IOException e) {
            LOGGER.warn("Failed to send message", e);
        }
    }

    private void validateRoomExists(String roomName) {
        if (!chatRooms.containsKey(roomName)) {
            throw new IllegalArgumentException("Chat room not found");
        }
    }

    private static class Holder {
        static final ChatService INSTANCE = new ChatService();
    }
}
