package com.ch.dcs.node.core.handler;

import com.ch.dcs.node.core.message.Message;
import org.springframework.web.socket.WebSocketSession;

public interface ITextMessageHandle {

    void handleTextMessage(WebSocketSession session, Message message);

}
