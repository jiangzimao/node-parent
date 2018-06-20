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

public class RegisterMessageHandle implements ITextMessageHandle<String> {

    private static final Logger LOG = LoggerFactory.getLogger(RegisterMessageHandle.class);

    @Override
    public void handleTextMessage(WebSocketSession webSocketSession, Message<String> message) {
        Integer sourceId = message.getSourceId();
        if(sourceId == null) {
            sourceId = WebSocketContext.getId();
        }
        if(WebSocketContext.hasSession(sourceId)) {
            SocketSession oldSocketSession = WebSocketContext.getSession(sourceId);
            if(oldSocketSession != null) {
                oldSocketSession.close();
            }
        }
        SocketSession socketSession = new SocketSession(sourceId, webSocketSession);
        WebSocketContext.putSession(sourceId, socketSession);
        Message<Map<String, Object>> req = new Message<>(MessageType.REPLY);
        req.setRequestId(message.getRequestId());
        req.setTargetId(message.getSourceId());
        req.setSourceId(WebSocketContext.getId());
        Map<String, Object> result = new HashMap<>();
        result.put("status", Boolean.TRUE);
        result.put("id", WebSocketContext.getId());
        result.put("sourceId", sourceId);
        req.setData(result);
        MessageSender.sendMessage(sourceId, req);
        LOG.info(String.format("Socket client [id=%s, nodeId=%s] registration successful.",
                webSocketSession.getId(), sourceId));
    }
}
