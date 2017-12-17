package com.lahtinen.app.websocket.chat.domain;

import org.apache.commons.lang3.Validate;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.stream.Collectors.toList;

public class Room {

    private final Map<String/* session id*/, Client> clients = new ConcurrentHashMap<>();
    private final String name;
    private final String password;

    Room(String name, String password) {
        Validate.notEmpty(name, "Chat room must have a name");
        this.name = name;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public void addClient(Client client) {
        validateUserNameIsAvailable(client);

        clients.put(client.sessionId, client);
    }

    public void removeClient(String sessionId) {
        clients.remove(sessionId);
    }

    public List<String> getRoomSessionIds() {
        return clients.values().stream()
                .map(client -> client.sessionId).collect(toList());
    }

    private void validateUserNameIsAvailable(Client client) {
        clients.values().forEach(
                c -> Validate.isTrue(!c.username.equalsIgnoreCase(client.username))
        );
    }

    public boolean isUserConnected(String sessionId) {
        return clients.containsKey(sessionId);
    }

    public Collection<String> getUserNames() {
        return clients.values().stream()
                .map(client -> client.username)
                .collect(toList());
    }

}
