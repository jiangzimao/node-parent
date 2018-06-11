package com.ch.dcs.node.core.context;

import com.ch.dcs.node.core.handler.ITextMessageHandle;
import com.ch.dcs.node.core.message.MessageType;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketContext implements ApplicationContextAware {

    private static final Map<Integer, SocketSession> CLIENT_SOCKET_SESSIONS = new ConcurrentHashMap<>();
    private static final Map<MessageType, ITextMessageHandle> MESSAGE_HANDLES = new ConcurrentHashMap<>();
    private static final ThreadLocal<WebSocketSession> SESSION_THREAD_LOCAL = new ThreadLocal<>();
    private static final ThreadLocal<Integer> SOURCE_ID_THREAD_LOCAL = new ThreadLocal<>();
    private static ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        WebSocketContext.context = applicationContext;
    }

    public static void putSession(Integer id, SocketSession socketSession) {
        CLIENT_SOCKET_SESSIONS.put(id, socketSession);
    }

    public static SocketSession getSession(Integer id) {
        return CLIENT_SOCKET_SESSIONS.get(id);
    }

    public static boolean hasSession(Integer id) {
        return CLIENT_SOCKET_SESSIONS.containsKey(id);
    }

    public static Integer getServerId() {
        return context.getBean(Props.class).serverId;
    }

    public static ServerType getServerType() {
        return context.getBean(Props.class).serverType;
    }

    public static void registerHandler(MessageType messageType, ITextMessageHandle messageHandle) {
        MESSAGE_HANDLES.put(messageType, messageHandle);
    }

    public static ITextMessageHandle getMessageHandle(MessageType messageType) {
        return MESSAGE_HANDLES.get(messageType);
    }

    public static void setThreadLocalSession(WebSocketSession session) {
        SESSION_THREAD_LOCAL.set(session);
    }

    public static WebSocketSession getThreadLocalSession() {
        return SESSION_THREAD_LOCAL.get();
    }

    public static void setSourceIdThreadLocal(Integer sourceId) {
        SOURCE_ID_THREAD_LOCAL.set(sourceId);
    }

    public static Integer getSourceIdThreadLocal() {
        return SOURCE_ID_THREAD_LOCAL.get();
    }

    public static void clearThreadLocalSession() {
        SESSION_THREAD_LOCAL.remove();
    }

    public static void clearSourceIdThreadLocal() {
        SOURCE_ID_THREAD_LOCAL.remove();
    }
}
