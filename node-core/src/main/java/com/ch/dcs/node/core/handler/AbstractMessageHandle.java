package com.ch.dcs.node.core.handler;

import com.ch.dcs.node.core.context.Constant;
import com.ch.dcs.node.core.context.MessageSender;
import com.ch.dcs.node.core.context.ServerType;
import com.ch.dcs.node.core.context.WebSocketContext;
import com.ch.dcs.node.core.message.Message;
import com.ch.dcs.node.core.message.MessageType;
import org.springframework.web.socket.WebSocketSession;

public abstract class AbstractMessageHandle implements ITextMessageHandle {

    @Override
    public final void handleTextMessage(WebSocketSession session, Message message) {
        Integer sourceId = message.getSourceId();
        try {
            if(sourceId != null) {
                // 更新 socket 活跃时间
                WebSocketContext.refreshActiveSocket(sourceId);
                // 保存 sourceId 和 session 到线程变量中
                WebSocketContext.setThreadLocalSession(session);
                WebSocketContext.setSourceIdThreadLocal(sourceId);
            }
            Integer targetId = message.getTargetId();
            if(targetId == null || targetId.equals(WebSocketContext.getId())) {
                handle(session, message);
            } else {
                if(!WebSocketContext.getServerType().equals(ServerType.SERVER)) {
                    // 如果为节点服务，则推送到服务端进行转发
                    targetId = Constant.CENTER_SOCKET_ID;
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
