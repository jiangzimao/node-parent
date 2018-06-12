package com.ch.dcs.node.core.handler;

import com.ch.dcs.node.core.context.MessageSender;
import com.ch.dcs.node.core.context.WebSocketContext;
import com.ch.dcs.node.core.message.Message;
import com.ch.dcs.node.core.message.MessageType;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.WebSocketSession;

public class HeartbeatMessageHandle extends AbstractMessageHandle {

    private static final Logger LOG = LoggerFactory.getLogger(HeartbeatMessageHandle.class);

    @Override
    protected void handle(WebSocketSession session, Message message) {
        Integer sourceId = message.getSourceId();
        if(sourceId != null) {
            Message<Object> resMessage = new Message<>(MessageType.HEARTBEAT);
            resMessage.setTargetId(sourceId);
            resMessage.setSourceId(WebSocketContext.getId());
            resMessage.setData(message.getData());
            MessageSender.send(sourceId, resMessage);
        } else {
            LOG.error("the sourceId of message is null.");
        }
    }
}
