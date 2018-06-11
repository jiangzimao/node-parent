package com.ch.dcs.node.core.context;

import com.ch.dcs.node.core.handler.ITextMessageHandle;
import com.ch.dcs.node.core.message.MessageType;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketContext implements ApplicationContextAware {

    private static final Map<Integer, Session> CLIENT_SOCKET_SESSIONS = new ConcurrentHashMap<>();
    private static final Map<MessageType, ITextMessageHandle> MESSAGE_HANDLES = new ConcurrentHashMap<>();
    private static ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        WebSocketContext.context = applicationContext;
    }

    public static void putSession(Integer id, Session session) {
        CLIENT_SOCKET_SESSIONS.put(id, session);
    }

    public static Session getSession(Integer id) {
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
}
