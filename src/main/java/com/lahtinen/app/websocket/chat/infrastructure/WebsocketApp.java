package com.lahtinen.app.websocket.chat.infrastructure;

import be.tomcools.dropwizard.websocket.WebsocketBundle;
import com.lahtinen.app.websocket.chat.port.rest.ChatResource;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class WebsocketApp extends Application<WebsocketAppConfiguration> {

    private WebsocketBundle<WebsocketAppConfiguration> websocketBundle = new WebsocketBundle<>();

    public static void main(String[] args) throws Exception {
        new WebsocketApp().run(args);
    }

    @Override
    public String getName() {
        return "websocket-chat";
    }

    @Override
    public void initialize(Bootstrap<WebsocketAppConfiguration> bootstrap) {
        bootstrap.addBundle(websocketBundle);
    }

    @Override
    public void run(WebsocketAppConfiguration configuration, Environment environment) {
        websocketBundle.addEndpoint(ChatResource.class);
    }
}
