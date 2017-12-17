package com.lahtinen.app.websocket.chat.port.rest;

import com.google.gson.Gson;
import com.lahtinen.app.websocket.chat.domain.ChatService;
import com.lahtinen.app.websocket.chat.port.rest.response.ErrorResponse;
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
public class ChatSocket {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChatSocket.class);
    private static final Gson GSON = new Gson();
    private static final ChatService CHAT_SERVICE = ChatService.getInstance();

    private Session session;

    @OnOpen
    public void createSession(Session session) {
        LOGGER.info("Session established: " + session.getId());
        CHAT_SERVICE.connected(session);
        this.session = session;
    }

    @OnMessage
    public void onMessage(String message) {
        LOGGER.info("Message: " + message);
        MessageHandler.handleRequest(session.getId(), message);
    }

    @OnClose
    public void closeSession(Session session, CloseReason cr) {
        LOGGER.info("Session [{}] closed, with reason [{}]", session.getId(), cr.getReasonPhrase());
        CHAT_SERVICE.disconnected(session.getId());
    }

    @OnError
    public void onError(Throwable error) {
        if (error instanceof IllegalArgumentException) {
            try {
                session.getAsyncRemote().sendObject(GSON.toJson(new ErrorResponse(error.getMessage())));
            } catch (Exception e) {
                LOGGER.error("Failed to reply error", e);
            }
        } else {
            LOGGER.error("Unexpected error", error);
        }
    }
}