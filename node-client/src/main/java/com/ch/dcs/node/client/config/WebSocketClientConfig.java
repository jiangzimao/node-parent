package com.ch.dcs.node.client.config;

import com.ch.dcs.node.client.handle.ClientWebSocketHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.client.WebSocketConnectionManager;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

@Component
public class WebSocketClientConfig {

    @Value("${web.socket.server.uri}")
    private String uriTemplate;

    @Bean
    public WebSocketConnectionManager webSocketConnectionManager() {
        StandardWebSocketClient client = new StandardWebSocketClient();
        WebSocketConnectionManager manager = new WebSocketConnectionManager(client, new ClientWebSocketHandler(), uriTemplate);
        manager.setAutoStartup(true);
        return manager;
    }

}
