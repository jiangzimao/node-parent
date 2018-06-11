package com.ch.dcs.node.core.handler;

import com.ch.dcs.node.core.context.ServerType;
import com.ch.dcs.node.core.context.Session;
import com.ch.dcs.node.core.utils.JsonUtil;
import com.ch.dcs.node.core.context.WebSocketContext;
import com.ch.dcs.node.core.message.RequestMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

public abstract class AbstractMessageHandle implements ITextMessageHandle {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractMessageHandle.class);

    @Override
    public void handleTextMessage(WebSocketSession session, RequestMessage requestMessage) {
        Integer targetServerId = requestMessage.getTargetServerId();
        if(targetServerId == null || targetServerId.equals(WebSocketContext.getServerId())) {
            handle(session, requestMessage);
        } else {
            Session targetSession;
            if(WebSocketContext.getServerType().equals(ServerType.SERVER)) {
                // 如果为服务端，则直接找到目标节点推送请求
                targetSession = WebSocketContext.getSession(targetServerId);
            } else {
                // 如果为节点服务，则推送到服务端进行转发
                targetSession = WebSocketContext.getSession(0);
            }
            // 推送到目标节点处理
            targetSession.sendMessage(new TextMessage(JsonUtil.toString(requestMessage)));
        }
    }

    protected abstract void handle(WebSocketSession session, RequestMessage requestMessage);
}
