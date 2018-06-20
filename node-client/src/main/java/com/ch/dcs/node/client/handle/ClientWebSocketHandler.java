package com.ch.dcs.node.client.handle;

import com.ch.dcs.node.core.context.Constant;
import com.ch.dcs.node.core.context.MessageSender;
import com.ch.dcs.node.core.context.SocketSession;
import com.ch.dcs.node.core.context.WebSocketContext;
import com.ch.dcs.node.core.handler.ServerTextWebSocketHandler;
import com.ch.dcs.node.core.message.Message;
import com.ch.dcs.node.core.message.MessageType;
import com.ch.dcs.node.core.utils.JsonUtil;
import com.ch.dcs.node.core.utils.RequestIdUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

public class ClientWebSocketHandler extends ServerTextWebSocketHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ClientWebSocketHandler.class);

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {

    }
}
