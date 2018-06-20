package com.ch.dcs.node.core.context;

import com.ch.dcs.node.core.message.Message;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ReplyContext {

    private static final Map<Long, ReplyFuture> syncReplyMap = new ConcurrentHashMap<>();

    public static ReplyFuture newReplyFuture(Long requestId) {
        ReplyFuture replyFuture = new ReplyFuture(requestId);
        syncReplyMap.put(requestId, replyFuture);
        return replyFuture;
    }

    public static void updateReplyFuture(Long requestId, Message message) {
        ReplyFuture replyFuture = syncReplyMap.get(requestId);
        if(replyFuture != null) {
            try {
                replyFuture.setReply(message);
                replyFuture.countDown();
            } finally {
                syncReplyMap.remove(requestId);
            }
        }
    }

}
