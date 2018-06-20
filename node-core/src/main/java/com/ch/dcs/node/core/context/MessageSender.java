package com.ch.dcs.node.core.context;

import com.ch.dcs.node.core.message.Message;
import com.ch.dcs.node.core.message.MessageType;
import com.ch.dcs.node.core.utils.JsonUtil;
import com.ch.dcs.node.core.utils.RequestIdUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashMap;
import java.util.Map;

public class MessageSender {

    private static final Logger LOG = LoggerFactory.getLogger(MessageSender.class);

    public static <E, T> Message<T> sendSyncMessage(Integer targetId, Message<E> message, WebSocketSession... sessions) {
        return sendMsg(targetId, message, true, sessions);
    }

    public static <E> void sendMessage(Integer targetId, Message<E> message, WebSocketSession... sessions) {
        sendMsg(targetId, message, false, sessions);
    }

    private static <E, T> Message<T> sendMsg(Integer targetId, Message<E> message, Boolean sync, WebSocketSession... sessions) {
        if(targetId != null) {
            SocketSession socketSession = WebSocketContext.getSession(targetId);
            if(socketSession == null || !socketSession.isOpen()) {
                String errorMsg;
                if(socketSession == null) {
                    errorMsg = String.format("No available socketSession with targetId[%s] was found.", targetId);
                } else {
                    errorMsg = String.format("The socketSession[targetId=%s] not open.", targetId);
                }
                LOG.error(errorMsg);
                WebSocketSession threadLocalSession = WebSocketContext.getThreadLocalSession();
                if(threadLocalSession != null && threadLocalSession.isOpen()) {
                    Integer sourceId = WebSocketContext.getSourceIdThreadLocal();
                    if(sourceId != null) {
                        Map<String, Object> data = new HashMap<>();
                        data.put("status", Boolean.FALSE);
                        data.put("errorMsg", errorMsg);
                        data.put("sourceMsg", message);
                        Message<Map<String, Object>> replyMessage = new Message<>(MessageType.REPLY);
                        replyMessage.setRequestId(message.getRequestId());
                        replyMessage.setTargetId(sourceId);
                        // replyMessage.setSourceId(WebSocketContext.getId());
                        replyMessage.setData(data);
                        if(sync) {
                            return (Message<T>) replyMessage;
                        }
                        return sendMsg(null, replyMessage, sync, threadLocalSession);
                    }
                }
            } else {
                if(sync) {
                    if(message.getRequestId() == null || message.getRequestId() <= 0) {
                        message.setRequestId(RequestIdUtil.getRequestId());
                    }
                    TextMessage textMessage = new TextMessage(JsonUtil.toString(message));
                    ReplyFuture replyFuture = ReplyContext.newReplyFuture(message.getRequestId());
                    socketSession.sendMessage(textMessage);
                    return (Message<T>) replyFuture.getReply();
                } else {
                    TextMessage textMessage = new TextMessage(JsonUtil.toString(message));
                    socketSession.sendMessage(textMessage);
                }
            }
        } else if(sessions != null && sessions.length > 0) {
            WebSocketSession session = sessions[0];
            if(session.isOpen()) {
                try {
                    if(sync) {
                        if(message.getRequestId() == null || message.getRequestId() <= 0) {
                            message.setRequestId(RequestIdUtil.getRequestId());
                        }
                        ReplyFuture replyFuture = ReplyContext.newReplyFuture(message.getRequestId());
                        session.sendMessage(new TextMessage(JsonUtil.toString(message)));
                        return (Message<T>) replyFuture.getReply();
                    } else {
                        TextMessage textMessage = new TextMessage(JsonUtil.toString(message));
                        session.sendMessage(textMessage);
                    }
                } catch (Throwable e) {
                    LOG.error(String.format("Send message error. session[%s] message[%s]", session, message));
                }
            }
        }
        return null;
    }

}
