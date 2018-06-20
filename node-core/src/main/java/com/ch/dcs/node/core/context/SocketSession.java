package com.ch.dcs.node.core.context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

public class SocketSession {

    private static final Logger LOG = LoggerFactory.getLogger(SocketSession.class);

    private Integer id;

    private WebSocketSession webSocketSession;

    private SocketSession() {

    }

    public SocketSession(Integer id, WebSocketSession webSocketSession) {
        this();
        this.id = id;
        this.webSocketSession = webSocketSession;
    }

    public void close() {
        if(webSocketSession != null && webSocketSession.isOpen()) {
            try {
                webSocketSession.close();
            } catch (Throwable e) {
                LOG.error(String.format("close session[%s] error.", e.getMessage()), e);
            }
        }
    }

    public void sendMessage(WebSocketMessage<?> message) {
        if(webSocketSession.isOpen()) {
            try {
                webSocketSession.sendMessage(message);
            } catch (Throwable e) {
                LOG.error(String.format("sendMessage message[%s] error.", message.getPayload()), e);
            }
        }
    }

    public Integer getId() {
        return this.id;
    }

    public boolean isOpen() {
        return webSocketSession.isOpen();
    }

    public String getSessionId() {
        return this.webSocketSession.getId();
    }
}
