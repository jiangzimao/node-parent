package com.ch.dcs.node.core.handler;

import com.ch.dcs.node.core.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.WebSocketSession;

public class PrintMessageHandle extends AbstractMessageHandle {

    private static final Logger LOG = LoggerFactory.getLogger(PrintMessageHandle.class);

    @Override
    protected void handle(WebSocketSession session, Message message) {
        // TODO 调用本地打印服务
        LOG.info(String.format("调用本地服务打印 [%s]", message.getData()));
    }
}
