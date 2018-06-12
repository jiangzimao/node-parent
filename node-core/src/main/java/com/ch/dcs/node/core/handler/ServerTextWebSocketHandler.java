package com.ch.dcs.node.core.handler;

import com.ch.dcs.node.core.context.WebSocketContext;
import com.ch.dcs.node.core.message.Message;
import com.ch.dcs.node.core.message.MessageType;
import com.ch.dcs.node.core.utils.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class ServerTextWebSocketHandler extends TextWebSocketHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ServerTextWebSocketHandler.class);

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // TODO
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage textMessage) {
        Message message = JsonUtil.toObject(textMessage.getPayload(), Message.class);
        MessageType type = message.getMessageType();
        ITextMessageHandle messageHandle = WebSocketContext.getMessageHandle(type);
        messageHandle.handleTextMessage(session, message);
    }


    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        // TODO
        if (session.isOpen()) {
            session.close();
        }
        LOG.error("连接出错");
    }



    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        // TODO

        LOG.info("连接已关闭：" + status);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

}
