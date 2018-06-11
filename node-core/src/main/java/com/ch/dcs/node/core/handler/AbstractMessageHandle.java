package com.ch.dcs.node.core.handler;

import com.ch.dcs.node.core.context.MessageSender;
import com.ch.dcs.node.core.context.ServerType;
import com.ch.dcs.node.core.context.WebSocketContext;
import com.ch.dcs.node.core.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.WebSocketSession;

public abstract class AbstractMessageHandle implements ITextMessageHandle {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractMessageHandle.class);

    @Override
    public final void handleTextMessage(WebSocketSession session, Message message) {
        Integer sourceId = message.getSourceId();
        try {
            if(sourceId != null) {
                WebSocketContext.setThreadLocalSession(session);
                WebSocketContext.setSourceIdThreadLocal(sourceId);
            }
            Integer targetId = message.getTargetId();
            if(targetId == null || targetId.equals(WebSocketContext.getServerId())) {
                handle(session, message);
            } else {
                if(!WebSocketContext.getServerType().equals(ServerType.SERVER)) {
                    // 如果为节点服务，则推送到服务端进行转发
                    targetId = 0;
                }
                // 推送到目标节点处理
                MessageSender.send(targetId, message);
            }
        } finally {
            if(sourceId != null) {
                WebSocketContext.clearThreadLocalSession();
                WebSocketContext.clearSourceIdThreadLocal();
            }
        }

    }

    protected abstract void handle(WebSocketSession session, Message message);
}
