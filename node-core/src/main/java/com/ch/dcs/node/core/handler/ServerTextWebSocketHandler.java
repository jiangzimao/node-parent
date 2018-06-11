package com.ch.dcs.node.core.handler;

import com.ch.dcs.node.core.context.WebSocketContext;
import com.ch.dcs.node.core.message.MessageType;
import com.ch.dcs.node.core.message.RequestMessage;
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
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        RequestMessage requestMessage = JsonUtil.toObject(message.getPayload(), RequestMessage.class);
        MessageType type = requestMessage.getMessageType();
        ITextMessageHandle messageHandle = WebSocketContext.getMessageHandle(type);
        messageHandle.handleTextMessage(session, requestMessage);
    }

    /**
     * 发送信息给指定用户
     */
    public boolean sendMessageToUser(Integer clientId, TextMessage message) {
        // TODO
        return true;
    }


    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        // TODO
        if (session.isOpen()) {
            session.close();
        }
        System.out.println("连接出错");
    }



    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        // TODO
        System.out.println("连接已关闭：" + status);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

}
