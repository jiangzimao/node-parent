package com.ch.dcs.node.core.config;

import com.ch.dcs.node.core.context.ServerType;
import com.ch.dcs.node.core.context.WebSocketContext;
import com.ch.dcs.node.core.handler.*;
import com.ch.dcs.node.core.message.MessageType;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.HandshakeInterceptor;

import javax.annotation.PostConstruct;

@Component
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @PostConstruct
    public void init() {
        WebSocketContext.registerHandler(MessageType.REPLY, new ReplyMessageHandle());
        WebSocketContext.registerHandler(MessageType.HEARTBEAT, new HeartbeatMessageHandle());
        WebSocketContext.registerHandler(MessageType.REGISTER, new RegisterMessageHandle());
        WebSocketContext.registerHandler(MessageType.PRINT, new PrintMessageHandle());
        WebSocketContext.registerHandler(MessageType.INPUT, new InputMessageHandle());

        WebSocketContext.registerHandler(MessageType.CHAT, new ChatMessageHandle());
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
        webSocketHandlerRegistry.addHandler(nodeHandler(), "/webSocket")
                .setAllowedOrigins("*")
                .addInterceptors(handshakeInterceptor())
                .withSockJS();
        webSocketHandlerRegistry.addHandler(nodeHandler(), "/webSocket")
                .setAllowedOrigins("*")
                .addInterceptors(handshakeInterceptor());
    }

    @Bean
    public WebSocketHandler nodeHandler() {
        return new ServerTextWebSocketHandler();
    }

    @Bean
    public HandshakeInterceptor handshakeInterceptor() {
        return new ServerHandshakeInterceptor();
    }
}
