package com.ch.dcs.node.core.message;

import java.io.Serializable;

public class Message<T> implements Serializable {

    private MessageType messageType;

    private Integer targetId;

    private Integer sourceId;

    private T data;

    private Message() {
    }

    public Message(MessageType messageType) {
        this();
        this.messageType = messageType;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public Integer getTargetId() {
        return targetId;
    }

    public void setTargetId(Integer targetId) {
        this.targetId = targetId;
    }

    public Integer getSourceId() {
        return sourceId;
    }

    public void setSourceId(Integer sourceId) {
        this.sourceId = sourceId;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Message{" +
                "messageType=" + messageType +
                ", targetId=" + targetId +
                ", sourceId=" + sourceId +
                ", data='" + data + '\'' +
                '}';
    }
}
