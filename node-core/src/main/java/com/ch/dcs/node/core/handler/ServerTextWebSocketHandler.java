package com.ch.dcs.node.core.handler;

import com.ch.dcs.node.core.context.WebSocketContext;
import com.ch.dcs.node.core.message.MessageType;
import com.ch.dcs.node.core.utils.JsonUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class ServerTextWebSocketHandler extends TextWebSocketHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ServerTextWebSocketHandler.class);

    private ExecutorService executorService;

    public ServerTextWebSocketHandler() {
        this.executorService = Executors.newCachedThreadPool(new ThreadFactory(){
            private final AtomicInteger number = new AtomicInteger(0);
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, String.format("Handle-thread-%s", number.incrementAndGet()));
            }
        });
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        LOG.info(String.format("Socket session[%s] connection successful.", session.getId()));
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage textMessage) {
        // 多线程处理，避免阻塞
        final String message = textMessage.getPayload();
        LOG.info(String.format("收到消息：%s", message));
        executorService.submit(() -> {
            MessageType messageType = MessageType.valueOf(JsonUtil.getMember(message,"messageType"));
            ITextMessageHandle messageHandle = WebSocketContext.getMessageHandle(messageType);
            messageHandle.handleTextMessage(session, textMessage);
        });
    }


    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        String sessionId = session.getId();
        if(StringUtils.isNotBlank(sessionId)) {
            WebSocketContext.removeSession(sessionId);
        } else if(session.isOpen()) {
            session.close();
        }
        LOG.error(String.format("Session[%s] transport error: %s", session.getId(), exception.getMessage()));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String sessionId = session.getId();
        if(StringUtils.isNotBlank(sessionId)) {
            WebSocketContext.removeSession(sessionId);
        }
        LOG.info(String.format("Connection[%s] Closed: %s", session.getId(), status));
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

}
