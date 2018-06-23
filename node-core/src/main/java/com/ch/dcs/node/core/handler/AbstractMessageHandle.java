package com.ch.dcs.node.core.handler;

import com.ch.dcs.node.core.context.*;
import com.ch.dcs.node.core.message.Message;
import com.ch.dcs.node.core.message.MessageType;
import com.ch.dcs.node.core.utils.JsonUtil;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.lang.reflect.Type;

public abstract class AbstractMessageHandle<T> implements ITextMessageHandle<T> {

    private Type defaultType;

    private AbstractMessageHandle() {
    }

    AbstractMessageHandle(Type type) {
        this();
        this.defaultType = type;
    }

    @Override
    public final void handleTextMessage(WebSocketSession session, TextMessage textMessage) {
        Message<T> message = JsonUtil.toObject(textMessage.getPayload(), defaultType);
        handleMessage(session, message);
    }

    @Override
    public final void handleMessage(WebSocketSession session, Message<T> message) {
        Integer sourceId = message.getSourceId();
        MessageType messageType = message.getMessageType();
        try {
            if(messageType == MessageType.REPLY) {
                Long requestId = message.getRequestId();
                if(requestId == null) {
                    return;
                }
                ReplyContext.updateReplyFuture(requestId, message);
                return;
            }
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
                Class<T> cla = null;
                // 推送到目标节点处理
                if(message.getSync()) {
                    Message response = MessageSender.sendSyncMessage(targetId, message, message.getReplyClass());
                    MessageSender.sendMessage(response.getTargetId(), response);
                } else {
                    MessageSender.sendMessage(targetId, message);
                }
            }
        } finally {
            if(sourceId != null) {
                WebSocketContext.clearThreadLocalSession();
                WebSocketContext.clearSourceIdThreadLocal();
            }
        }
    }

    protected abstract void handle(WebSocketSession session, Message<T> message);
}
