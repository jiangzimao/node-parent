package com.ch.dcs.node.core.handler;

import com.ch.dcs.node.core.context.MessageSender;
import com.ch.dcs.node.core.context.WebSocketContext;
import com.ch.dcs.node.core.device.IPrintService;
import com.ch.dcs.node.core.device.ITermDeviceService;
import com.ch.dcs.node.core.message.Message;
import com.ch.dcs.node.core.message.MessageType;
import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashMap;
import java.util.Map;

public class PrintMessageHandle extends AbstractMessageHandle<Map<String, String>> {

    private static final Logger LOG = LoggerFactory.getLogger(PrintMessageHandle.class);

    private IPrintService<Map<String, String>> printService;
    private ITermDeviceService termDeviceService;

    public PrintMessageHandle() {
        super(new TypeToken<Message<Map<String, String>>>() {}.getType());
    }

    @Override
    protected void handle(WebSocketSession session, Message<Map<String, String>> message) {
        if(message.getTargetId() == null) {
            message.setSourceId(WebSocketContext.getId());
            if(termDeviceService == null) {
                termDeviceService = WebSocketContext.getContext().getBean(ITermDeviceService.class);
            }
            Integer targetTermNum = termDeviceService.findTermNumOfDefaultDev(WebSocketContext.getId());
            if(!WebSocketContext.getId().equals(targetTermNum)) {
                super.handleMessage(session, message);
                return;
            }
            message.setTargetId(targetTermNum);
        }
        if(printService == null) {
            printService = WebSocketContext.getContext().getBean(IPrintService.class);
        }
        boolean result = false;
        String errorMsg = "";
        try {
            // 调用本地打印服务
            result = printService.print(session, message);
        } catch (Throwable e) {
            errorMsg = e.getMessage();
            LOG.error("print error. %s", errorMsg, e);
        }

        Map<String, String> data = new HashMap<>();
        data.put("result", String.valueOf(result));
        data.put("errorMsg", errorMsg);

        Message<Map<String, String>> response = new Message<>(MessageType.REPLY);
        response.setSourceId(WebSocketContext.getId());
        response.setTargetId(WebSocketContext.getId());
        response.setRequestId(message.getRequestId());
        response.setData(data);
        MessageSender.sendMessage(message.getSourceId(), response);
    }
}
