package com.ch.dcs.node.core.context;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Props {

    @Value("${web.socket.server.id:0}")
    public Integer serverId;

    @Value("${web.socket.server.type:SERVER}")
    public ServerType serverType;
}
