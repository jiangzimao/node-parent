package com.ch.dcs.node.core.config;

import com.ch.dcs.node.core.context.ServerType;

public interface IConfig {

    ServerType getServerType();

    Integer getId();
}
