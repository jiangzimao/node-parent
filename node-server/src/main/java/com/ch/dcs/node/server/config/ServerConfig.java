package com.ch.dcs.node.server.config;

import com.ch.dcs.node.core.config.IConfig;
import com.ch.dcs.node.core.config.Props;
import com.ch.dcs.node.core.context.ServerType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ServerConfig implements IConfig {

    private final Props props;

    @Autowired
    public ServerConfig(Props props) {
        this.props = props;
    }

    @Override
    public ServerType getServerType() {
        return ServerType.SERVER;
    }

    @Override
    public Integer getId() {
        return props.id;
    }

}
