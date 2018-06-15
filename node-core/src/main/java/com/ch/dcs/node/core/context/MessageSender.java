package com.ch.dcs.node.core.context;

import com.ch.dcs.node.core.message.Message;
import com.ch.dcs.node.core.message.MessageType;
import com.ch.dcs.node.core.utils.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashMap;
import java.util.Map;

public class MessageSender {

    private static final Logger LOG = LoggerFactory.getLogger(MessageSender.class);

    public static <M, R> R syncSend(Integer targetId, M message, Class<R> resultClass, WebSocketSession... sessions) {
        send(targetId, message, sessions);
        return null;
    }

    public static <T> void send(Integer targetId, T message, WebSocketSession... sessions) {
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
                        data.put("errorMsg", errorMsg);
                        data.put("sourceMsg", message);
                        Message<Map<String, Object>> replyMessage = new Message<>(MessageType.REPLY);
                        replyMessage.setTargetId(sourceId);
                        replyMessage.setData(data);
                        send(null, replyMessage, threadLocalSession);
                    }
                }
            }  else {
                TextMessage textMessage = new TextMessage(message instanceof String ?
                        (String) message : JsonUtil.toString(message));
                socketSession.sendMessage(textMessage);
            }
        } else if(sessions != null && sessions.length > 0) {
            WebSocketSession session = sessions[0];
            if(session.isOpen()) {
                try {
                    String info = message instanceof String ? (String) message : JsonUtil.toString(message);
                    try {
                        session.sendMessage(new TextMessage(info));
                    } catch (Throwable e) {
                        LOG.error(String.format("Send message error. session[%s] message[%s]", session, info));
                    }
                } catch (Throwable e) {
                    LOG.error(String.format("Message[%s] conversion to Json failed", message));
                }
            }
        }
    }

}
