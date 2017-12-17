package com.lahtinen.app.websocket.chat.infrastructure;

import com.lahtinen.app.websocket.chat.port.rest.ChatSocket;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.websockets.WebsocketBundle;

public class WebsocketApp extends Application<WebsocketAppConfiguration> {

    public static void main(String[] args) throws Exception {
        new WebsocketApp().run(args);
    }

    @Override
    public String getName() {
        return "websocket-chat";
    }

    @Override
    public void initialize(Bootstrap<WebsocketAppConfiguration> bootstrap) {
        bootstrap.addBundle(new WebsocketBundle(ChatSocket.class));
    }

    @Override
    public void run(WebsocketAppConfiguration configuration, Environment environment) {
    }
}
