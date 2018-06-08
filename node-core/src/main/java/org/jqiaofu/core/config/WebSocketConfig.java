package org.jqiaofu.core.config;

import org.jqiaofu.core.handler.NodeHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
        webSocketHandlerRegistry.addHandler(nodeHandler(), "/webSocket")
                .setAllowedOrigins("*")
                .withSockJS();
        webSocketHandlerRegistry.addHandler(nodeHandler(), "/webSocket")
                .setAllowedOrigins("*");
    }

    @Bean
    public WebSocketHandler nodeHandler() {
        return new NodeHandler();
    }
}
