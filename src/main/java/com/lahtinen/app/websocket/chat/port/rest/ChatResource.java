package com.lahtinen.app.websocket.chat.port.rest;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.internal.LinkedTreeMap;
import com.lahtinen.app.websocket.chat.domain.ChatService;
import com.lahtinen.app.websocket.chat.port.rest.request.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/chat")
public class ChatResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChatResource.class);
    private static final Gson GSON = new Gson();

    private final ChatService chatService = ChatService.getInstance();

    private Session session;

    @OnOpen
    public void createSession(Session session) {
        LOGGER.info("Session established: " + session.getId());
        chatService.connected(session);
        this.session = session;
    }

    @OnMessage
    public void onMessage(String message) {
        LOGGER.info("Message: " + message);
        try {
            Request parsedRequest = GSON.fromJson(message, Request.class);
            parseRequest(parsedRequest, parsedRequest.content);
        } catch (JsonSyntaxException e) {
            LOGGER.warn("Failed to parse message");
        }
    }

    // TODO: replace uglyness with an event-bus or something
    private void parseRequest(Request parsedRequest, LinkedTreeMap content) {
        switch (parsedRequest.type) {
            case "CREATE-ROOM":
                chatService.createRoom(
                        (String) content.get("roomName"),
                        (String) content.get("roomPassword")
                );
                LOGGER.info("User session [{}] created room [{}]", session.getId(), content.get("roomName"));
                break;
            case "JOIN-ROOM":
                chatService.connectToRoom(
                        session.getId(),
                        (String) content.get("roomName"),
                        (String) content.get("userName"),
                        (String) content.get("password")
                );
                LOGGER.info("User session [{}] joined room [{}] with username [{}]", session.getId(), content.get("roomName"), content.get("userName"));
                break;
            case "MESSAGE":
                chatService.sendMessage(
                        (String) content.get("roomName"),
                        (String) content.get("message")
                );
                LOGGER.info("User session [{}] sent message [{}]", session.getId(), content.get("message"));
                break;
            default:
                LOGGER.info("Unsupported message type: " + parsedRequest.type);
                break;
        }
    }

    @OnClose
    public void closeSession(final Session session, CloseReason cr) {
        LOGGER.info("Session [{}] closed, with reason [{}]", session.getId(), cr.getReasonPhrase());
        chatService.disconnected(session);
    }

    @OnError
    public void onError(Throwable error) {
        LOGGER.error("Error", error);
    }
}