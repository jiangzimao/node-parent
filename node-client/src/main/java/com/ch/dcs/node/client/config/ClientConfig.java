package com.ch.dcs.node.client.config;

import com.ch.dcs.node.client.SocketConnectionManager;
import com.ch.dcs.node.client.handle.ClientWebSocketHandler;
import com.ch.dcs.node.core.config.IConfig;
import com.ch.dcs.node.core.config.Props;
import com.ch.dcs.node.core.context.ServerType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

@Component
public class ClientConfig implements IConfig {

    private final Props props;
    private final ClientProp clientProp;

    @Autowired
    public ClientConfig(Props props, ClientProp clientProp) {
        this.props = props;
        this.clientProp = clientProp;
    }

    @Bean
    public SocketConnectionManager socketManager() {
        StandardWebSocketClient client = new StandardWebSocketClient();
        WebSocketHandler webSocketHandler = new ClientWebSocketHandler();
        SocketConnectionManager manager = new SocketConnectionManager(client, webSocketHandler, clientProp.serverUri);
        manager.startHeartbeat();
        return manager;
    }

    @Override
    public ServerType getServerType() {
        return ServerType.NODE;
    }

    @Override
    public Integer getId() {
        return props.id;
    }
}
