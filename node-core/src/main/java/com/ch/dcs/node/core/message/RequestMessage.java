package com.ch.dcs.node.core.message;

import java.io.Serializable;

public class RequestMessage implements Serializable {

    private MessageType messageType;

    private Integer targetServerId;

    private Integer clientId;

    private String data;

    private RequestMessage() {
    }

    public RequestMessage(MessageType messageType) {
        this();
        this.messageType = messageType;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public Integer getTargetServerId() {
        return targetServerId;
    }

    public void setTargetServerId(Integer targetServerId) {
        this.targetServerId = targetServerId;
    }

    public Integer getClientId() {
        return clientId;
    }

    public void setClientId(Integer clientId) {
        this.clientId = clientId;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "RequestMessage{" +
                "messageType=" + messageType +
                ", targetServerId=" + targetServerId +
                ", clientId=" + clientId +
                ", data='" + data + '\'' +
                '}';
    }
}
