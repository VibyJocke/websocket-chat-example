package com.lahtinen.cloud.service.frontend.port.rest;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.lahtinen.cloud.service.frontend.domain.ChatService;
import com.lahtinen.cloud.service.frontend.port.rest.request.CreateRoomRequest;
import com.lahtinen.cloud.service.frontend.port.rest.request.JoinRoomRequest;
import com.lahtinen.cloud.service.frontend.port.rest.request.Request;
import com.lahtinen.cloud.service.frontend.port.rest.request.SendMessageRequest;
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
            parseRequest(parsedRequest, parsedRequest.getContent());
        } catch (JsonSyntaxException e) {
            LOGGER.warn("Failed to parse message");
        }
    }

    // TODO: replace uglyness with an event-bus or something
    private void parseRequest(Request parsedRequest, Object messageContent) {
        switch (parsedRequest.getType()) {
            case "CREATE-ROOM":
                CreateRoomRequest request = (CreateRoomRequest) messageContent;
                chatService.createRoom(request.getRoomName(), request.getRoomPassword());
                LOGGER.info("User session [{}] created room [{}]", session.getId(), request.getRoomName());
                break;
            case "JOIN-ROOM":
                JoinRoomRequest joinRequest = (JoinRoomRequest) messageContent;
                chatService.connectToRoom(session, joinRequest.getRoomName());
                LOGGER.info("User session [{}] joined room [{}] with username [{}]", session.getId(), joinRequest.getRoomName(), joinRequest.getUserName());
                break;
            case "MESSAGE":
                SendMessageRequest messageRequest = (SendMessageRequest) messageContent;
                chatService.sendMessage(messageRequest.getMessage());
                break;
            default:
                LOGGER.info("Unsupported message type: " + parsedRequest.getType());
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