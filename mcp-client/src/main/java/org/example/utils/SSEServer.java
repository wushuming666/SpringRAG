package org.example.utils;

import cn.hutool.core.collection.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.example.enums.SSEMsgType;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Slf4j
public class SSEServer {
    // 存放用户
    private static final Map<String, SseEmitter> sseClients = new ConcurrentHashMap<>();

    /**
     * 创建sse连接
     *
     * @param userId 用户ID
     * @return SseEmitter
     */
    public static SseEmitter connect(String userId) {

        // 设置超时时间，0L表示不超时（永不过期）；默认是30秒，超时未完成任务则会抛出异常
        SseEmitter sseEmitter = new SseEmitter(0L);

        // 注册回调方法
        sseEmitter.onTimeout(timeoutCallback(userId));
        sseEmitter.onCompletion(completionCallback(userId));
        sseEmitter.onError(errorCallback(userId));

        sseClients.put(userId, sseEmitter);
        log.info("SSE连接创建成功，连接的用户ID为：{}", userId);

        return sseEmitter;
    }

    public static Consumer<Throwable> errorCallback(String userId) {
        return Throwable -> {
            log.error("SSE异常...");
            // 移除用户连接
            remove(userId);
        };
    }

    public static Runnable timeoutCallback(String userId) {
        return () -> {
            log.info("SSE超时");
            // 移除用户连接
            remove(userId);
        };
    }

    public static Runnable completionCallback(String userId) {
        return () -> {
            log.info("SSE完成...");
            // 移除用户连接
            remove(userId);
        };
    }

    public static void remove(String userId) {
        // 删除用户
        sseClients.remove(userId);
        log.info("SSE连接被移除，移除的用户ID为：{}", userId);
    }

    public static void sendMsg(String userId, String message, SSEMsgType msgType) {
//        log.info("Sending message to user: {}, message: {}, type: {}", userId, message, msgType);
        if (CollectionUtils.isEmpty(sseClients)){
            return;
        }
        if(sseClients.containsKey(userId)) {
            SseEmitter sseEmitter = sseClients.get(userId);
            sendEmitterMessage(sseEmitter, userId, message, msgType);
        }
    }

    public static void sendMsgToAll(String message) {
        if (CollectionUtils.isEmpty(sseClients)){
            return;
        }

        sseClients.forEach((userId, sseEmitter) -> {
            sendEmitterMessage(sseEmitter, userId, message, SSEMsgType.MESSAGE);
        });
    }

    private static void sendEmitterMessage(SseEmitter sseEmitter, String userId, String message, SSEMsgType msgType)
    {
        try {
            SseEmitter.SseEventBuilder msgEvent = SseEmitter.event()
                    .id(userId)
                    .data(message)
                    .name(msgType.type)
                    .reconnectTime(5000);
            sseEmitter.send(msgEvent);
        } catch (IOException e) {
            remove(userId);
            log.error("SSE异常...{}", e.getMessage());
        }
    }
}
