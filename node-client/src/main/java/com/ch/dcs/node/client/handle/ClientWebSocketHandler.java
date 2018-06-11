package com.ch.dcs.node.client.handle;

import com.ch.dcs.node.core.context.SocketSession;
import com.ch.dcs.node.core.context.WebSocketContext;
import com.ch.dcs.node.core.handler.ServerTextWebSocketHandler;
import com.ch.dcs.node.core.message.Message;
import com.ch.dcs.node.core.message.MessageType;
import com.ch.dcs.node.core.utils.JsonUtil;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

public class ClientWebSocketHandler extends ServerTextWebSocketHandler {

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Message message = new Message(MessageType.REGISTER);
        message.setSourceId(WebSocketContext.getServerId());
        session.sendMessage(new TextMessage(JsonUtil.toString(message)));
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage textMessage) {
        Message message = JsonUtil.toObject(textMessage.getPayload(), Message.class);
        if(MessageType.REGISTER.equals(message.getMessageType())) {
            // 注册响应消息, 服务端 socketId 默认为 0
            SocketSession serverSocketSession = WebSocketContext.getSession(0);
            if(serverSocketSession != null) {
                serverSocketSession.close();
            }
            WebSocketContext.putSession(0, new SocketSession(0, session));
            return;
        }
        super.handleTextMessage(session, textMessage);
    }
}
