package com.ch.dcs.node.core.handler;

import com.ch.dcs.node.core.utils.JsonUtil;
import com.ch.dcs.node.core.context.WebSocketContext;
import com.ch.dcs.node.core.message.RequestMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

public class InputMessageHandle extends AbstractMessageHandle {

    private static final Logger LOG = LoggerFactory.getLogger(InputMessageHandle.class);

    @Override
    protected void handle(WebSocketSession session, RequestMessage requestMessage) {
        // 推送消息到浏览器
        Integer clientId = requestMessage.getClientId();
        if(clientId != null) {
            WebSocketContext.getSession(clientId).sendMessage(new TextMessage(JsonUtil.toString(requestMessage)));
        } else {
            LOG.error(String.format("not found client[clientId=%s].", requestMessage.getClientId()));
        }
    }
}
