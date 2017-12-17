package com.lahtinen.app.websocket.chat.port.rest;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.internal.LinkedTreeMap;
import com.lahtinen.app.websocket.chat.domain.ChatService;
import com.lahtinen.app.websocket.chat.port.rest.request.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//TODO: replace this with a neater way to parse and handle messages
class MessageHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageHandler.class);
    private static final Gson GSON = new Gson();
    private static final ChatService CHAT_SERVICE = ChatService.getInstance();

    static void handleRequest(String sessionId, String message) {
        try {
            final Request parsedRequest = GSON.fromJson(message, Request.class);
            final LinkedTreeMap content = parsedRequest.content;

            switch (parsedRequest.type) {
                case "CREATE-ROOM":
                    CHAT_SERVICE.createRoom(
                            (String) content.get("name"),
                            (String) content.get("password")
                    );
                    LOGGER.info("User session [{}] created room [{}]", sessionId, content.get("name"));
                    break;
                case "JOIN-ROOM":
                    CHAT_SERVICE.connectToRoom(
                            sessionId,
                            (String) content.get("roomName"),
                            (String) content.get("userName"),
                            (String) content.get("password")
                    );
                    LOGGER.info("User session [{}] joined room [{}] with username [{}]", sessionId, content.get("roomName"), content.get("userName"));
                    break;
                case "MESSAGE":
                    CHAT_SERVICE.sendMessage(
                            sessionId,
                            (String) content.get("roomName"),
                            (String) content.get("message")
                    );
                    LOGGER.info("User session [{}] sent message [{}]", sessionId, content.get("message"));
                    break;
                default:
                    LOGGER.warn("Unsupported message type: " + parsedRequest.type);
                    break;
            }
        } catch (JsonSyntaxException e) {
            LOGGER.warn("Failed to parse message");
        }
    }
}
