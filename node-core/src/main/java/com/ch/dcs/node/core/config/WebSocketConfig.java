package com.ch.dcs.node.core.config;

import com.ch.dcs.node.core.context.WebSocketContext;
import com.ch.dcs.node.core.handler.InputMessageHandle;
import com.ch.dcs.node.core.handler.PrintMessageHandle;
import com.ch.dcs.node.core.handler.RegisterMessageHandle;
import com.ch.dcs.node.core.handler.ServerTextWebSocketHandler;
import com.ch.dcs.node.core.message.MessageType;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import javax.annotation.PostConstruct;

@Component
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @PostConstruct
    public void init() {
        WebSocketContext.registerHandler(MessageType.REGISTER, new RegisterMessageHandle());
        WebSocketContext.registerHandler(MessageType.PRINT, new PrintMessageHandle());
        WebSocketContext.registerHandler(MessageType.INPUT, new InputMessageHandle());
    }

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
        return new ServerTextWebSocketHandler();
    }
}
