package com.ch.dcs.node.core.handler;

import com.ch.dcs.node.core.context.MessageSender;
import com.ch.dcs.node.core.message.Message;
import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.WebSocketSession;

public class ReplyMessageHandle extends AbstractMessageHandle<String> {

    private static final Logger LOG = LoggerFactory.getLogger(ReplyMessageHandle.class);

    public ReplyMessageHandle() {
        super(new TypeToken<Message<String>>() {}.getType());
    }

    @Override
    protected void handle(WebSocketSession session, Message<String> message) {
        Integer targetId = message.getTargetId();
        if(targetId != null) {
            MessageSender.sendMessage(targetId, message);
        } else {
            LOG.error(String.format("the targetId of message is null. message[%s]", message));
        }
    }
}
