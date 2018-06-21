package com.ch.dcs.node.core.context;

import com.ch.dcs.node.core.config.Props;
import com.ch.dcs.node.core.message.Message;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ReplyFuture {

    private Long requestId;

    private final CountDownLatch latch = new CountDownLatch(1);

    private Message reply;

    private ReplyFuture() {
    }

    public ReplyFuture(Long requestId) {
        this();
        this.requestId = requestId;
    }

    public void setReply(Message reply) {
        this.reply = reply;
    }

    public void countDown() {
        this.latch.countDown();;
    }

    public Message getReply() {
        try {
            latch.await(WebSocketContext.getContext().getBean(Props.class).syncTimeout, TimeUnit.SECONDS);
        } catch (Throwable e) {
            throw new RuntimeException(String.format("The request[%s] timeout", requestId));
        }
        return reply;
    }
}
