package com.ch.dcs.node.client.handle;

import com.ch.dcs.node.core.context.Session;
import com.ch.dcs.node.core.context.WebSocketContext;
import com.ch.dcs.node.core.handler.ServerTextWebSocketHandler;
import com.ch.dcs.node.core.message.MessageType;
import com.ch.dcs.node.core.message.RequestMessage;
import com.ch.dcs.node.core.utils.JsonUtil;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

public class ClientWebSocketHandler extends ServerTextWebSocketHandler {

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        RequestMessage requestMessage = new RequestMessage(MessageType.REGISTER);
        requestMessage.setClientId(WebSocketContext.getServerId());
        session.sendMessage(new TextMessage(JsonUtil.toString(requestMessage)));
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        RequestMessage requestMessage = JsonUtil.toObject(message.getPayload(), RequestMessage.class);
        if(MessageType.REGISTER.equals(requestMessage.getMessageType())) {
            // 注册响应消息, 服务端 socketId 默认为 0
            Session serverSession = WebSocketContext.getSession(0);
            if(serverSession != null) {
                serverSession.close();
            }
            WebSocketContext.putSession(0, new Session(0, session));
            return;
        }
        super.handleTextMessage(session, message);
    }
}
