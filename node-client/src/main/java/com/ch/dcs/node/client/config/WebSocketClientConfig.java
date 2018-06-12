package com.ch.dcs.node.client.config;

import com.ch.dcs.node.client.SocketConnectionManager;
import com.ch.dcs.node.client.handle.ClientWebSocketHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

@Component
public class WebSocketClientConfig {

    @Value("${web.socket.server.uri}")
    private String uriTemplate;

    @Bean
    public SocketConnectionManager socketManager() {
        StandardWebSocketClient client = new StandardWebSocketClient();
        WebSocketHandler webSocketHandler = new ClientWebSocketHandler();
        SocketConnectionManager manager = new SocketConnectionManager(client, webSocketHandler, uriTemplate);
        manager.startHeartbeat();
        return manager;
    }

}
