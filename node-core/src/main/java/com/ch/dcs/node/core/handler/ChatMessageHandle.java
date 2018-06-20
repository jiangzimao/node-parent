package com.ch.dcs.node.core.handler;

import com.ch.dcs.node.core.context.Constant;
import com.ch.dcs.node.core.context.MessageSender;
import com.ch.dcs.node.core.context.ServerType;
import com.ch.dcs.node.core.context.WebSocketContext;
import com.ch.dcs.node.core.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.WebSocketSession;

public class ChatMessageHandle extends AbstractMessageHandle<String> {

    private static final Logger LOG = LoggerFactory.getLogger(ChatMessageHandle.class);

    @Override
    protected void handle(WebSocketSession session, Message<String> message) {
        // 同步推送消息到浏览器
        Integer targetId = message.getTargetId();
        if(targetId != null) {
            Message<String> response = MessageSender.sendSyncMessage(targetId, message);
            if(response != null) {
                // 同步返回
                if(!WebSocketContext.getId().equals(response.getTargetId())) {
                    // 如果为节点服务，则推送到服务端进行转发
                    targetId = Constant.CENTER_SOCKET_ID;
                }
                MessageSender.sendMessage(targetId, response);
            }
        } else {
            LOG.error("the targetId of message is null.");
        }
    }
}
