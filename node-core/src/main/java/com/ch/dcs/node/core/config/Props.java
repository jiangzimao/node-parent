package com.ch.dcs.node.core.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Props {

    @Value("${web.socket.server.id:0}")
    public Integer id;

    @Value("${web.socket.sync.timeout:30}")
    public Integer syncTimeout;

}
