package com.ch.dcs.node.core.handler;

import com.ch.dcs.node.core.context.MessageSender;
import com.ch.dcs.node.core.context.WebSocketContext;
import com.ch.dcs.node.core.message.Message;
import com.ch.dcs.node.core.message.MessageType;
import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.WebSocketSession;

public class HeartbeatMessageHandle extends AbstractMessageHandle<String> {

    private static final Logger LOG = LoggerFactory.getLogger(HeartbeatMessageHandle.class);

    public HeartbeatMessageHandle() {
        super(new TypeToken<Message<String>>() {}.getType());
    }

    @Override
    protected void handle(WebSocketSession session, Message<String> message) {
        Integer sourceId = message.getSourceId();
        if(sourceId != null) {
            Message<String> resMessage = new Message<>(MessageType.REPLY);
            resMessage.setRequestId(message.getRequestId());
            resMessage.setTargetId(sourceId);
            resMessage.setSourceId(WebSocketContext.getId());
            resMessage.setData(message.getData());
            MessageSender.sendMessage(sourceId, resMessage);
        } else {
            LOG.error("the sourceId of message is null.");
        }
    }
}
