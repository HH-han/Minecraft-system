package com.minecraft.handler;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.minecraft.constant.CacheConstants;
import com.minecraft.service.UserService;
import com.minecraft.utils.JwtUtil;
import com.minecraft.utils.RedisUtil;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WebSocketHandler extends TextWebSocketHandler {

    private static final Map<Long, WebSocketSession> SESSIONS = new ConcurrentHashMap<>();
    private static final Map<String, Long> SESSION_USER = new ConcurrentHashMap<>();

    private final RedisUtil redisUtil;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    public WebSocketHandler(RedisUtil redisUtil, UserService userService, JwtUtil jwtUtil) {
        this.redisUtil = redisUtil;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String token = getTokenFromSession(session);
        if (token == null) {
            session.close(CloseStatus.NOT_ACCEPTABLE);
            return;
        }

        Long userId = jwtUtil.getUserId(token);
        if (userId == null) {
            session.close(CloseStatus.NOT_ACCEPTABLE);
            return;
        }

        if (!jwtUtil.validateToken(token)) {
            session.close(CloseStatus.NOT_ACCEPTABLE);
            return;
        }

        SESSIONS.put(userId, session);
        SESSION_USER.put(session.getId(), userId);

        redisUtil.set(CacheConstants.ONLINE_STATUS_KEY + userId, "1");
        redisUtil.hset(CacheConstants.USER_SESSION_KEY, String.valueOf(userId), session.getId());

        userService.updateOnlineStatus(userId, true);

        JSONObject response = new JSONObject();
        response.put("type", "CONNECT");
        response.put("status", "success");
        response.put("userId", userId);
        session.sendMessage(new TextMessage(response.toJSONString()));
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        try {
            JSONObject json = JSON.parseObject(message.getPayload());
            String type = json.getString("type");

            switch (type) {
                case "HEARTBEAT":
                    handleHeartbeat(session);
                    break;
                case "PRIVATE_MESSAGE":
                    handlePrivateMessage(json);
                    break;
                case "GROUP_MESSAGE":
                    handleGroupMessage(json);
                    break;
                case "READ_RECEIPT":
                    handleReadReceipt(json);
                    break;
                case "TYPING":
                    handleTyping(json);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            System.out.println("Error handling WebSocket message: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Long userId = SESSION_USER.remove(session.getId());
        if (userId != null) {
            SESSIONS.remove(userId);
            redisUtil.delete(CacheConstants.ONLINE_STATUS_KEY + userId);
            redisUtil.hdel(CacheConstants.USER_SESSION_KEY, String.valueOf(userId));
            userService.updateOnlineStatus(userId, false);
        }
    }

    private void handleHeartbeat(WebSocketSession session) throws IOException {
        JSONObject response = new JSONObject();
        response.put("type", "HEARTBEAT");
        response.put("status", "success");
        session.sendMessage(new TextMessage(response.toJSONString()));
    }

    private void handlePrivateMessage(JSONObject json) {
        Long receiverId = json.getLong("receiverId");
        WebSocketSession receiverSession = SESSIONS.get(receiverId);

        if (receiverSession != null && receiverSession.isOpen()) {
            try {
                JSONObject message = new JSONObject();
                message.put("type", "PRIVATE_MESSAGE");
                message.put("senderId", json.getLong("senderId"));
                message.put("receiverId", receiverId);
                message.put("content", json.getString("content"));
                message.put("messageType", json.getString("messageType"));
                message.put("timestamp", System.currentTimeMillis());
                receiverSession.sendMessage(new TextMessage(message.toJSONString()));
            } catch (IOException e) {
                System.out.println("Error sending private message: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void handleGroupMessage(JSONObject json) {
        Long groupId = json.getLong("groupId");
        String content = json.getString("content");
        Long senderId = json.getLong("senderId");
        String messageType = json.getString("messageType");

        SESSIONS.forEach((userId, session) -> {
            if (session.isOpen() && !userId.equals(senderId)) {
                try {
                    JSONObject message = new JSONObject();
                    message.put("type", "GROUP_MESSAGE");
                    message.put("groupId", groupId);
                    message.put("senderId", senderId);
                    message.put("content", content);
                    message.put("messageType", messageType);
                    message.put("timestamp", System.currentTimeMillis());
                    session.sendMessage(new TextMessage(message.toJSONString()));
                } catch (IOException e) {
                }
            }
        });
    }

    private void handleReadReceipt(JSONObject json) {
        Long senderId = json.getLong("senderId");
        Long messageId = json.getLong("messageId");
        WebSocketSession senderSession = SESSIONS.get(senderId);

        if (senderSession != null && senderSession.isOpen()) {
            try {
                JSONObject receipt = new JSONObject();
                receipt.put("type", "READ_RECEIPT");
                receipt.put("readerId", json.getLong("readerId"));
                receipt.put("messageId", messageId);
                senderSession.sendMessage(new TextMessage(receipt.toJSONString()));
            } catch (IOException e) {
            }
        }
    }

    private void handleTyping(JSONObject json) {
        Long receiverId = json.getLong("receiverId");
        Long senderId = json.getLong("senderId");
        WebSocketSession receiverSession = SESSIONS.get(receiverId);

        if (receiverSession != null && receiverSession.isOpen()) {
            try {
                JSONObject typing = new JSONObject();
                typing.put("type", "TYPING");
                typing.put("senderId", senderId);
                typing.put("chatType", json.getString("chatType"));
                receiverSession.sendMessage(new TextMessage(typing.toJSONString()));
            } catch (IOException e) {
            }
        }
    }

    private String getTokenFromSession(WebSocketSession session) {
        String query = session.getUri().getQuery();
        if (query != null && query.startsWith("token=")) {
            try {
                return java.net.URLDecoder.decode(query.substring(6), "UTF-8");
            } catch (Exception e) {
                System.out.println("Error decoding token: " + e.getMessage());
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    public static void sendMessage(Long userId, JSONObject message) {
        WebSocketSession session = SESSIONS.get(userId);
        if (session != null && session.isOpen()) {
            try {
                session.sendMessage(new TextMessage(message.toJSONString()));
            } catch (IOException e) {
                System.out.println("Error sending WebSocket message: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("User not online or session closed: " + userId);
        }
    }

    public static boolean isOnline(Long userId) {
        return SESSIONS.containsKey(userId);
    }

    public static void sendGroupMessage(Long groupId, JSONObject message) {
        SESSIONS.forEach((userId, session) -> {
            if (session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(message.toJSONString()));
                } catch (IOException e) {
                    System.out.println("Error sending group message: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }
}
