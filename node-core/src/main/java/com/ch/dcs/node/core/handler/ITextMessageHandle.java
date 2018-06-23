package com.ch.dcs.node.core.handler;

import com.ch.dcs.node.core.message.Message;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

public interface ITextMessageHandle<T> {

    void handleTextMessage(WebSocketSession session, TextMessage textMessage);

    void handleMessage(WebSocketSession session, Message<T> message);
}
