package com.lahtinen.app.websocket.chat.port.rest;

import org.glassfish.tyrus.client.ClientManager;
import org.glassfish.tyrus.ext.client.java8.SessionBuilder;

import javax.websocket.MessageHandler;
import javax.websocket.Session;
import java.net.URI;

public class WebsocketClient {
    private Session session;

    public WebsocketClient(MessageHandler messageHandler, String endpointURI) throws Exception {
        session = new SessionBuilder(ClientManager.createClient())
                .uri(new URI(endpointURI))
                .connect();
        session.addMessageHandler(messageHandler);
    }

    public void sendMessage(String message) {
        session.getAsyncRemote().sendText(message);
    }
}