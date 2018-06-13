package com.ch.dcs.node.client.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ClientProp {

    @Value("${web.socket.local.heartbeat.time:5}")
    public Integer heartbeatTime;

    @Value("${web.socket.server.uri}")
    public String serverUri;

}
