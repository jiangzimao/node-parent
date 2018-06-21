package com.ch.dcs.node.core.context;

import com.ch.dcs.node.core.config.Props;
import com.ch.dcs.node.core.message.Message;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.concurrent.TimeUnit;

public class ReplyContext {

    private static final Cache<Long, ReplyFuture> SYNC_REPLY_CACHE
            = CacheBuilder.newBuilder()
            .expireAfterWrite(WebSocketContext.getContext().getBean(Props.class).syncTimeout, TimeUnit.SECONDS)
            .build();

    public static ReplyFuture newReplyFuture(Long requestId) {
        ReplyFuture replyFuture = new ReplyFuture(requestId);
        SYNC_REPLY_CACHE.put(requestId, replyFuture);
        return replyFuture;
    }

    public static void updateReplyFuture(Long requestId, Message message) {
        ReplyFuture replyFuture = SYNC_REPLY_CACHE.getIfPresent(requestId);
        if(replyFuture != null) {
            try {
                replyFuture.setReply(message);
                replyFuture.countDown();
            } finally {
                SYNC_REPLY_CACHE.invalidate(requestId);
                SYNC_REPLY_CACHE.cleanUp();
            }
        }
    }

}
