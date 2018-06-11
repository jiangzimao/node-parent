package com.ch.dcs.node.core.handler;

import com.ch.dcs.node.core.context.Session;
import com.ch.dcs.node.core.utils.JsonUtil;
import com.ch.dcs.node.core.context.WebSocketContext;
import com.ch.dcs.node.core.message.MessageType;
import com.ch.dcs.node.core.message.RequestMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashMap;
import java.util.Map;

public class RegisterMessageHandle implements ITextMessageHandle {

    private static final Logger LOG = LoggerFactory.getLogger(RegisterMessageHandle.class);

    @Override
    public void handleTextMessage(WebSocketSession webSocketSession, RequestMessage requestMessage) {
        Integer clientId = requestMessage.getClientId();
        if(clientId == null) {
            clientId = WebSocketContext.getServerId();
        }
        if(WebSocketContext.hasSession(clientId)) {
            Session oldSession = WebSocketContext.getSession(clientId);
            if(oldSession != null) {
                oldSession.close();
            }
        }
        Session session = new Session(clientId, webSocketSession);
        WebSocketContext.putSession(clientId, session);
        Map<String, Object> result = new HashMap<>();
        result.put("status", Boolean.TRUE);
        result.put("serverId", WebSocketContext.getServerId());
        result.put("clientId", clientId);
        RequestMessage req = new RequestMessage(MessageType.REGISTER);
        req.setData(JsonUtil.toString(result));
        session.sendMessage(new TextMessage(JsonUtil.toString(req)));
        LOG.info(String.format("Client[clientId=%s] registration successful.", clientId));
    }
}
