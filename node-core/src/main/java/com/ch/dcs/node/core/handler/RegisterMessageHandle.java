package com.ch.dcs.node.core.handler;

import com.ch.dcs.node.core.context.MessageSender;
import com.ch.dcs.node.core.context.SocketSession;
import com.ch.dcs.node.core.message.Message;
import com.ch.dcs.node.core.context.WebSocketContext;
import com.ch.dcs.node.core.message.MessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashMap;
import java.util.Map;

public class RegisterMessageHandle implements ITextMessageHandle {

    private static final Logger LOG = LoggerFactory.getLogger(RegisterMessageHandle.class);

    @Override
    public void handleTextMessage(WebSocketSession webSocketSession, Message message) {
        Integer sourceId = message.getSourceId();
        if(sourceId == null) {
            sourceId = WebSocketContext.getServerId();
        }
        if(WebSocketContext.hasSession(sourceId)) {
            SocketSession oldSocketSession = WebSocketContext.getSession(sourceId);
            if(oldSocketSession != null) {
                oldSocketSession.close();
            }
        }
        SocketSession socketSession = new SocketSession(sourceId, webSocketSession);
        WebSocketContext.putSession(sourceId, socketSession);
        Map<String, Object> result = new HashMap<>();
        result.put("status", Boolean.TRUE);
        result.put("serverId", WebSocketContext.getServerId());
        result.put("sourceId", sourceId);
        Message<Map<String, Object>> req = new Message<>(MessageType.REGISTER);
        req.setData(result);
        MessageSender.send(sourceId, req);
        LOG.info(String.format("Client[%s] registration successful.", sourceId));
    }
}
