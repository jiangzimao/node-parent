package com.ch.dcs.node.core.message;

import java.io.Serializable;

public class Message<T> implements Serializable {

    private Boolean sync = false;

    private Long requestId;

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

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
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

    public Boolean getSync() {
        return sync;
    }

    public void setSync(Boolean sync) {
        this.sync = sync;
    }
}
