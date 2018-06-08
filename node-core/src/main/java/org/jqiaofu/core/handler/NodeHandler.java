package org.jqiaofu.core.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NodeHandler extends TextWebSocketHandler {

    private static final Logger LOG = LoggerFactory.getLogger(NodeHandler.class);

    // nodeId
    private static final String NODE_ID = "nodeId";

    // node web socket map
    private static final Map<Integer, WebSocketSession> NODE_WEB_SOCKETS = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Integer nodeId = getClientId(session);
        LOG.info(String.format("客户端[%s]成功建立连接", nodeId));
        NODE_WEB_SOCKETS.put(nodeId, session);
        session.sendMessage(new TextMessage("成功建立socket连接"));
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        // ...
        System.out.println(message.getPayload());

        WebSocketMessage message1 = new TextMessage("server:"+message);
        try {
            session.sendMessage(message1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送信息给指定用户
     */
    public boolean sendMessageToUser(Integer clientId, TextMessage message) {
        if (NODE_WEB_SOCKETS.get(clientId) == null) return false;
        WebSocketSession session = NODE_WEB_SOCKETS.get(clientId);
        System.out.println("sendMessage:" + session);
        if (!session.isOpen()) return false;
        try {
            session.sendMessage(message);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        if (session.isOpen()) {
            session.close();
        }
        System.out.println("连接出错");
        NODE_WEB_SOCKETS.remove(getClientId(session));
    }



    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        System.out.println("连接已关闭：" + status);
        NODE_WEB_SOCKETS.remove(getClientId(session));
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    private Integer getClientId(WebSocketSession session) {
        Object nodeId = session.getAttributes().get(NODE_ID);
        if(nodeId != null) {
            return (Integer) nodeId;
        }
        // TODO
        return null;
    }
}
