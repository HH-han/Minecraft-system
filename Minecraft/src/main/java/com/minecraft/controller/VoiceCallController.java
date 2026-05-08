package com.minecraft.controller;

import com.minecraft.dto.response.ApiResponse;
import com.minecraft.utils.RedisUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Tag(name = "语音通话")
@RestController
@RequestMapping("/api/public/voice")
public class VoiceCallController {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    private static final String CALL_PREFIX = "voice:call:";
    private static final long CALL_EXPIRE_MINUTES = 5;

    @Operation(summary = "发起语音通话")
    @PostMapping("/call/initiate")
    public ApiResponse<Map<String, Object>> initiateCall(
            @RequestParam Long callerId,
            @RequestParam Long receiverId) {
        
        String callId = UUID.randomUUID().toString();
        
        Map<String, Object> callData = new HashMap<>();
        callData.put("callId", callId);
        callData.put("callerId", callerId);
        callData.put("receiverId", receiverId);
        callData.put("status", "initiated");
        
        redisUtil.set(CALL_PREFIX + callId, callData, CALL_EXPIRE_MINUTES, TimeUnit.MINUTES);
        
        messagingTemplate.convertAndSendToUser(receiverId.toString(), "/queue/call", callData);
        
        Map<String, Object> response = new HashMap<>();
        response.put("callId", callId);
        response.put("status", "initiated");
        
        return ApiResponse.success("通话请求发送成功", response);
    }

    @Operation(summary = "接受语音通话")
    @PostMapping("/call/accept")
    @ResponseBody
    public ApiResponse<Map<String, Object>> acceptCall(
            @RequestParam String callId,
            @RequestParam Long userId) {
        
        @SuppressWarnings("unchecked")
        Map<String, Object> callData = (Map<String, Object>) redisUtil.get(CALL_PREFIX + callId);
        
        if (callData == null) {
            return ApiResponse.error(404, "通话不存在或已过期");
        }
        
        Long receiverId = ((Number) callData.get("receiverId")).longValue();
        if (!receiverId.equals(userId)) {
            return ApiResponse.error(403, "无权操作此通话");
        }
        
        callData.put("status", "accepted");
        redisUtil.set(CALL_PREFIX + callId, callData, CALL_EXPIRE_MINUTES, TimeUnit.MINUTES);
        
        Long callerId = ((Number) callData.get("callerId")).longValue();
        messagingTemplate.convertAndSendToUser(callerId.toString(), "/queue/call", callData);
        
        Map<String, Object> response = new HashMap<>();
        response.put("callId", callId);
        response.put("status", "accepted");
        
        return ApiResponse.success("通话已接受", response);
    }

    @Operation(summary = "拒绝语音通话")
    @PostMapping("/call/reject")
    public ApiResponse<Map<String, Object>> rejectCall(
            @RequestParam String callId,
            @RequestParam Long userId) {
        
        @SuppressWarnings("unchecked")
        Map<String, Object> callData = (Map<String, Object>) redisUtil.get(CALL_PREFIX + callId);
        
        if (callData == null) {
            return ApiResponse.error(404, "通话不存在或已过期");
        }
        
        Long receiverId = ((Number) callData.get("receiverId")).longValue();
        if (!receiverId.equals(userId)) {
            return ApiResponse.error(403, "无权操作此通话");
        }
        
        callData.put("status", "rejected");
        redisUtil.set(CALL_PREFIX + callId, callData, CALL_EXPIRE_MINUTES, TimeUnit.MINUTES);
        
        Long callerId = ((Number) callData.get("callerId")).longValue();
        messagingTemplate.convertAndSendToUser(callerId.toString(), "/queue/call", callData);
        
        Map<String, Object> response = new HashMap<>();
        response.put("callId", callId);
        response.put("status", "rejected");
        
        return ApiResponse.success("通话已拒绝", response);
    }

    @Operation(summary = "结束语音通话")
    @PostMapping("/call/end")
    public ApiResponse<Map<String, Object>> endCall(
            @RequestParam String callId,
            @RequestParam Long userId) {
        
        @SuppressWarnings("unchecked")
        Map<String, Object> callData = (Map<String, Object>) redisUtil.get(CALL_PREFIX + callId);
        
        if (callData == null) {
            return ApiResponse.error(404, "通话不存在或已过期");
        }
        
        Long callerId = ((Number) callData.get("callerId")).longValue();
        Long receiverId = ((Number) callData.get("receiverId")).longValue();
        
        if (!callerId.equals(userId) && !receiverId.equals(userId)) {
            return ApiResponse.error(403, "无权操作此通话");
        }
        
        callData.put("status", "ended");
        redisUtil.set(CALL_PREFIX + callId, callData, CALL_EXPIRE_MINUTES, TimeUnit.MINUTES);
        
        if (callerId.equals(userId)) {
            messagingTemplate.convertAndSendToUser(receiverId.toString(), "/queue/call", callData);
        } else {
            messagingTemplate.convertAndSendToUser(callerId.toString(), "/queue/call", callData);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("callId", callId);
        response.put("status", "ended");
        
        return ApiResponse.success("通话已结束", response);
    }

    @Operation(summary = "发送SDP Offer")
    @PostMapping("/call/sdp-offer")
    public ApiResponse<Void> sendSdpOffer(
            @RequestParam String callId,
            @RequestParam String sdp) {
        
        @SuppressWarnings("unchecked")
        Map<String, Object> callData = (Map<String, Object>) redisUtil.get(CALL_PREFIX + callId);
        
        if (callData == null) {
            return ApiResponse.error(404, "通话不存在或已过期");
        }
        
        callData.put("sdpOffer", sdp);
        redisUtil.set(CALL_PREFIX + callId, callData, CALL_EXPIRE_MINUTES, TimeUnit.MINUTES);
        
        Long receiverId = ((Number) callData.get("receiverId")).longValue();
        messagingTemplate.convertAndSendToUser(receiverId.toString(), "/queue/sdp", callData);
        
        return ApiResponse.success("SDP Offer发送成功", null);
    }

    @Operation(summary = "发送SDP Answer")
    @PostMapping("/call/sdp-answer")
    public ApiResponse<Void> sendSdpAnswer(
            @RequestParam String callId,
            @RequestParam String sdp) {
        
        @SuppressWarnings("unchecked")
        Map<String, Object> callData = (Map<String, Object>) redisUtil.get(CALL_PREFIX + callId);
        
        if (callData == null) {
            return ApiResponse.error(404, "通话不存在或已过期");
        }
        
        callData.put("sdpAnswer", sdp);
        redisUtil.set(CALL_PREFIX + callId, callData, CALL_EXPIRE_MINUTES, TimeUnit.MINUTES);
        
        Long callerId = ((Number) callData.get("callerId")).longValue();
        messagingTemplate.convertAndSendToUser(callerId.toString(), "/queue/sdp", callData);
        
        return ApiResponse.success("SDP Answer发送成功", null);
    }

    @Operation(summary = "发送ICE Candidate")
    @PostMapping("/call/ice-candidate")
    public ApiResponse<Void> sendIceCandidate(
            @RequestParam String callId,
            @RequestParam String candidate) {
        
        @SuppressWarnings("unchecked")
        Map<String, Object> callData = (Map<String, Object>) redisUtil.get(CALL_PREFIX + callId);
        
        if (callData == null) {
            return ApiResponse.error(404, "通话不存在或已过期");
        }
        
        Long callerId = ((Number) callData.get("callerId")).longValue();
        Long receiverId = ((Number) callData.get("receiverId")).longValue();
        
        Map<String, Object> iceData = new HashMap<>();
        iceData.put("callId", callId);
        iceData.put("candidate", candidate);
        
        if (callerId.equals(Long.parseLong(candidate.split(" ")[1]))) {
            messagingTemplate.convertAndSendToUser(receiverId.toString(), "/queue/ice", iceData);
        } else {
            messagingTemplate.convertAndSendToUser(callerId.toString(), "/queue/ice", iceData);
        }
        
        return ApiResponse.success("ICE Candidate发送成功", null);
    }

    @Operation(summary = "获取通话状态")
    @GetMapping("/call/status")
    public ApiResponse<Map<String, Object>> getCallStatus(@RequestParam String callId) {
        
        @SuppressWarnings("unchecked")
        Map<String, Object> callData = (Map<String, Object>) redisUtil.get(CALL_PREFIX + callId);
        
        if (callData == null) {
            return ApiResponse.error(404, "通话不存在或已过期");
        }
        
        return ApiResponse.success("成功", callData);
    }
}