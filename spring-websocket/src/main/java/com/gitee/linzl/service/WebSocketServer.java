package com.gitee.linzl.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * websocket是多例的
 */
@Slf4j
@ServerEndpoint(value = "/websocket/{userId}")
@Service
public class WebSocketServer {
    // 记录当前在线连接数
    private static AtomicLong onlineCount = new AtomicLong(0L);
    private static Map<String, WebSocketServer> sessionPool = new ConcurrentHashMap<>(1000);
    private Session session;
    private String userId;

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String userId) {
        this.session = session;
        this.userId = userId;
        sessionPool.put(userId, this);
        addOnlineCount(); // 在线数加1
        log.info("有新连接加入！当前在线人数为" + getOnlineCount());
        broadcast("连接成功", null);
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        sessionPool.remove(userId);
        subOnlineCount();
        log.info("有一连接关闭！当前在线人数为" + getOnlineCount());
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("来自客户端" + session + "的消息:" + message);

        // 群发消息
        broadcast(message, null);
    }

    /**
     * 群发自定义消息
     */
    public static void broadcast(String message, String userId) {
        log.info(message);
        sessionPool.forEach((key, item) -> {
            try {
                if (StringUtils.isEmpty(userId)) {// 空则群发
                    item.session.getBasicRemote().sendText(message);
                } else if (userId.equals(key)) {//指定发给谁
                    item.session.getBasicRemote().sendText(message);
                }
            } catch (IOException e) {
            }
        });
    }


    /**
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("发生错误");
        error.printStackTrace();
    }


    public static synchronized long getOnlineCount() {
        return onlineCount.get();
    }

    public static synchronized void addOnlineCount() {
        onlineCount.incrementAndGet();
    }

    public static synchronized void subOnlineCount() {
        onlineCount.decrementAndGet();
    }
}
