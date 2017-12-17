package com.lahtinen.app.websocket.chat.domain;

import com.google.gson.Gson;
import com.lahtinen.app.websocket.chat.port.rest.response.JoinRoomResponse;
import com.lahtinen.app.websocket.chat.port.rest.response.MessageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.Session;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChatService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChatService.class);
    private static final Gson GSON = new Gson();

    private final Map<String /* session id */, Session> userSessions = new ConcurrentHashMap<>();
    private final Map<String /* chat room name*/, Room> rooms = new ConcurrentHashMap<>();

    public static ChatService getInstance() {
        return Holder.INSTANCE;
    }

    public void connected(Session session) {
        userSessions.put(session.getId(), session);
    }

    public void disconnected(String sessionId) {
        userSessions.remove(sessionId);
        rooms.values().forEach(room -> room.removeClient(sessionId));
    }

    public void createRoom(String name, String password) {
        if (rooms.containsKey(name)) {
            throw new IllegalArgumentException("Chat room names must be unique");
        }
        rooms.put(name, new Room(name, password));
    }

    public void connectToRoom(String sessionId, String roomName, String userName, String password) {
        validateRoomExists(roomName);

        final Room room = rooms.get(roomName);
        if (room.getPassword() != null && !room.getPassword().equals(password)) {
            throw new IllegalArgumentException("Access denied: invalid password");
        }

        room.addClient(new Client(userName, sessionId));

        broadcastMessage(
                userSessions.get(sessionId),
                new JoinRoomResponse(rooms.get(roomName).getUserNames())
        );
    }

    public void sendMessage(String sessionId, String roomName, String message) {
        validateClientConnectedToRoom(sessionId, roomName);

        final MessageResponse messageResponse = new MessageResponse(message);
        rooms.get(roomName).getRoomSessionIds().forEach(
                id -> broadcastMessage(userSessions.get(id), messageResponse)
        );
    }

    private void broadcastMessage(Session session, Object message) {
        try {
            session.getAsyncRemote().sendText(GSON.toJson(message));
        } catch (Exception e) {
            LOGGER.warn("Failed to send message", e);
        }
    }

    private void validateRoomExists(String roomName) {
        if (!rooms.containsKey(roomName)) {
            throw new IllegalArgumentException("Chat room not found");
        }
    }

    private void validateClientConnectedToRoom(String sessionId, String roomName) {
        validateRoomExists(roomName);
        if (!rooms.get(roomName).isUserConnected(sessionId)) {
            throw new IllegalArgumentException("Access denied: not connected to room");
        }
    }

    private static class Holder {
        static final ChatService INSTANCE = new ChatService();
    }
}
