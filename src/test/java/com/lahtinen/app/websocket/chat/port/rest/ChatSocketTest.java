package com.lahtinen.app.websocket.chat.port.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lahtinen.app.websocket.chat.infrastructure.WebsocketApp;
import com.lahtinen.app.websocket.chat.infrastructure.WebsocketAppConfiguration;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;

public class ChatSocketTest {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @ClassRule
    public static final DropwizardAppRule<WebsocketAppConfiguration> RULE =
            new DropwizardAppRule<>(WebsocketApp.class, ResourceHelpers.resourceFilePath("config.yml"));

    private BlockingQueue<String> queue;
    private WebsocketClient client;

    @Before
    public void setUp() throws Exception {
        queue = new LinkedBlockingDeque<>();
        client = new WebsocketClient(new MessageHandler(), "ws://localhost:8080/chat");
    }

    @Test
    public void createRoom() throws Exception {
        String json = getJson(
                "CREATE-ROOM",
                new Object() {
                    @JsonProperty
                    String name = "room";
                    @JsonProperty
                    String password = "pwd";
                }
        );
        client.sendMessage(json);

        assertThat(queue.poll(1, TimeUnit.SECONDS), is(nullValue()));
    }

    @Test
    public void tryingToJoinARoomThatDoesNotExistReturnsErrorMessage() throws Exception {
        String json = getJson(
                "JOIN-ROOM",
                new Object() {
                    @JsonProperty
                    String roomName = "non-existing-room";
                    @JsonProperty
                    String userName = "joe";
                    @JsonProperty
                    String password = "pwd";
                }
        );
        client.sendMessage(json);

        assertThat(queue.poll(1, TimeUnit.SECONDS), is("{\"message\":\"Chat room not found\"}"));
    }

    @Test
    public void joiningAnExistingRoomReturnsListWithClientsCurrentlyInRoom() throws Exception {
        String json1 = getJson(
                "CREATE-ROOM",
                new Object() {
                    @JsonProperty
                    String name = "joinable-room";
                    @JsonProperty
                    String password = "pwd";
                }
        );
        String json2 = getJson(
                "JOIN-ROOM",
                new Object() {
                    @JsonProperty
                    String roomName = "joinable-room";
                    @JsonProperty
                    String userName = "joe";
                    @JsonProperty
                    String password = "pwd";
                }
        );
        client.sendMessage(json1);
        client.sendMessage(json2);

        assertThat(queue.poll(1, TimeUnit.SECONDS), is("{\"users\":[\"joe\"]}"));
    }

    private String getJson(String t, Object o) throws Exception {
        return MAPPER.writeValueAsString(
                new Object() {
                    @JsonProperty
                    String type = t;
                    @JsonProperty
                    Object content = o;
                }
        );
    }

    public class MessageHandler implements javax.websocket.MessageHandler.Whole<String> {
        @Override
        public void onMessage(String message) {
            try {
                queue.put(message);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}