package com.ch.dcs.node.client;

import com.ch.dcs.node.client.config.ClientProp;
import com.ch.dcs.node.core.context.Constant;
import com.ch.dcs.node.core.context.MessageSender;
import com.ch.dcs.node.core.context.SocketSession;
import com.ch.dcs.node.core.context.WebSocketContext;
import com.ch.dcs.node.core.message.Message;
import com.ch.dcs.node.core.message.MessageType;
import com.ch.dcs.node.core.utils.RequestIdUtil;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.context.Lifecycle;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.Nullable;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.ConnectionManagerSupport;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.handler.LoggingWebSocketHandlerDecorator;

import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class SocketConnectionManager extends ConnectionManagerSupport {
    private final WebSocketClient client;
    private final WebSocketHandler webSocketHandler;
    @Nullable
    private WebSocketSession webSocketSession;
    private WebSocketHttpHeaders headers = new WebSocketHttpHeaders();

    public SocketConnectionManager(WebSocketClient client, WebSocketHandler webSocketHandler, String uriTemplate, Object... uriVariables) {
        super(uriTemplate, uriVariables);
        this.client = client;
        this.webSocketHandler = this.decorateWebSocketHandler(webSocketHandler);
    }

    protected WebSocketHandler decorateWebSocketHandler(WebSocketHandler handler) {
        return new LoggingWebSocketHandlerDecorator(handler);
    }

    public void setSubProtocols(List<String> protocols) {
        this.headers.setSecWebSocketProtocol(protocols);
    }

    public List<String> getSubProtocols() {
        return this.headers.getSecWebSocketProtocol();
    }

    public void setOrigin(@Nullable String origin) {
        this.headers.setOrigin(origin);
    }

    @Nullable
    public String getOrigin() {
        return this.headers.getOrigin();
    }

    public void setHeaders(HttpHeaders headers) {
        this.headers.clear();
        this.headers.putAll(headers);
    }

    public HttpHeaders getHeaders() {
        return this.headers;
    }

    public void startInternal() {
        if (this.client instanceof Lifecycle && !((Lifecycle) this.client).isRunning()) {
            ((Lifecycle) this.client).start();
        }
        super.startInternal();
        startHeartbeat();
    }

    public void stopInternal() throws Exception {
        if (this.client instanceof Lifecycle && ((Lifecycle) this.client).isRunning()) {
            ((Lifecycle) this.client).stop();
        }

        super.stopInternal();
    }

    protected void openConnection() {
        if (this.logger.isInfoEnabled()) {
            this.logger.info("Connecting to WebSocket at " + this.getUri());
        }

        ListenableFuture<WebSocketSession> future = this.client.doHandshake(this.webSocketHandler, this.headers, this.getUri());
        future.addCallback(new ListenableFutureCallback<WebSocketSession>() {
            public void onSuccess(@Nullable WebSocketSession result) {
                SocketConnectionManager.this.webSocketSession = result;
                SocketConnectionManager.this.logger.info("Successfully connected");
                register(result);
            }

            public void onFailure(Throwable ex) {
                SocketConnectionManager.this.logger.error(String.format("Failed to connect. %s %s", ex.getMessage(), ex.getCause().getMessage()));
            }
        });
    }

    protected void closeConnection() throws Exception {
        if (this.webSocketSession != null) {
            this.webSocketSession.close();
        }
    }

    protected boolean isConnected() {
        return this.webSocketSession != null && this.webSocketSession.isOpen();
    }

    private void register(WebSocketSession session) {
        Message<String> message = new Message<>(MessageType.REGISTER);
        message.setSourceId(WebSocketContext.getId());
        message.setTargetId(Constant.CENTER_SOCKET_ID);
        message.setSync(Boolean.TRUE);
        message.setData(String.format("client[%s] register.", WebSocketContext.getId()));
        Message<Map<String, Object>> registerRes = MessageSender.sendSyncMessage(null, message, session);
        if (registerRes != null && registerRes.getData() != null && Boolean.valueOf(registerRes.getData().get("status").toString())) {
            // 注册响应消息, 服务端 socketId 默认为 0
            SocketSession serverSocketSession = WebSocketContext.getSession(Constant.CENTER_SOCKET_ID);
            if (serverSocketSession != null) {
                serverSocketSession.close();
            }
            WebSocketContext.putSession(Constant.CENTER_SOCKET_ID, new SocketSession(Constant.CENTER_SOCKET_ID, session));
            SocketConnectionManager.this.logger.info(String.format("Socket client[%s] registered successfully.", session.getId()));
        }
    }

    public void startHeartbeat() {
        Integer heartbeatTime = WebSocketContext.getContext().getBean(ClientProp.class).heartbeatTime;
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (webSocketSession == null) {
                openConnection();
            } else if (!webSocketSession.isOpen()) {
                try {
                    closeConnection();
                } catch (Throwable e) {
                    SocketConnectionManager.this.logger.error("Close connection error.", e);
                }
                openConnection();
            } else if (WebSocketContext.hasSession(Constant.CENTER_SOCKET_ID)) {
                //Long lastActiveTime = WebSocketContext.getActiveSockets().getIfPresent(Constant.CENTER_SOCKET_ID);
                //if(lastActiveTime == null || System.currentTimeMillis() - lastActiveTime >= heartbeatTime * 1000) {
                // 超出指定时间未有信息交互，则发送心跳请求
                Message<String> reqMessage = new Message<>(MessageType.HEARTBEAT);
                reqMessage.setSourceId(WebSocketContext.getId());
                reqMessage.setTargetId(Constant.CENTER_SOCKET_ID);
                reqMessage.setData(DateFormatUtils.format(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss:SSS"));
                Message<String> heartbeatRes = MessageSender.sendSyncMessage(Constant.CENTER_SOCKET_ID, reqMessage);
                WebSocketContext.refreshActiveSocket(heartbeatRes.getSourceId());
                //}
            }
        }, 0, heartbeatTime, TimeUnit.SECONDS);
    }
}