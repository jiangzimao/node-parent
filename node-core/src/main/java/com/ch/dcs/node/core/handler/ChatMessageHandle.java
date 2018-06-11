package com.ch.dcs.node.core.handler;

import com.ch.dcs.node.core.context.MessageSender;
import com.ch.dcs.node.core.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.WebSocketSession;

public class ChatMessageHandle extends AbstractMessageHandle {

    private static final Logger LOG = LoggerFactory.getLogger(ChatMessageHandle.class);

    @Override
    protected void handle(WebSocketSession session, Message message) {
        // 推送消息到浏览器
        Integer targetId = message.getTargetId();
        if(targetId != null) {
            MessageSender.send(targetId, message);
        } else {
            LOG.error("the targetId of message is null.");
        }
    }
}
