package com.minecraft.controller;

import com.minecraft.dto.request.FriendRequestWithPhoneDTO;
import com.minecraft.dto.request.GroupChatMessage;
import com.minecraft.dto.request.SingleChatMessage;
import com.minecraft.dto.response.ApiResponse;
import com.minecraft.dto.response.FriendInfoDTO;
import com.minecraft.dto.response.FriendRequestDTO;
import com.minecraft.entity.ChatMessage;
import com.minecraft.entity.Friend;
import com.minecraft.entity.User;
import com.minecraft.service.ChatService;
import com.minecraft.service.FriendService;
import com.minecraft.service.GroupChatService;
import com.minecraft.service.UserService;
import com.minecraft.utils.FileUtils;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Tag(name = "即时通讯")
@RestController
@RequestMapping("/api/public/im")
public class ImController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private FriendService friendService;

    @Autowired
    private UserService userService;

    @Autowired
    private GroupChatService groupChatService;

    @Autowired
    private FileUtils fileUtils;

    @Operation(summary = "上传文件")
    @PostMapping("/upload")
    public ApiResponse<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String filePath = fileUtils.uploadFile(file);
            return ApiResponse.success("上传成功", filePath);
        } catch (Exception e) {
            return ApiResponse.error("上传失败" + e.getMessage());
        }
    }

    @Operation(summary = "发送单聊消息（参数方式）")
    @PostMapping("/single/send/param")
    public ApiResponse<Void> sendSingleMessageParam(
            @RequestParam Long senderId,
            @RequestParam Long receiverId,
            @RequestParam String content,
            @RequestParam(defaultValue = "text") String messageType) {
        
        ChatMessage message = new ChatMessage();
        message.setSenderId(senderId);
        message.setReceiverId(receiverId);
        message.setContent(content);
        message.setMessageType(messageType);
        message.setIsRead(0);
        
        chatService.sendMessage(message);
        return ApiResponse.success("消息发送成功", null);
    }

    @Operation(summary = "发送单聊消息（实体方式）")
    @PostMapping("/single/send")
    public ApiResponse<ChatMessage> sendSingleMessage(@RequestBody SingleChatMessage dto) {
        ChatMessage message = new ChatMessage();
        message.setSenderId(dto.getSenderId());
        message.setReceiverId(dto.getReceiverId());
        message.setContent(dto.getContent());
        message.setMessageType(dto.getMessageType() != null ? dto.getMessageType() : "text");
        message.setIsRead(0);
        
        chatService.sendMessage(message);
        return ApiResponse.success("消息发送成功", message);
    }

    @Operation(summary = "发送群聊消息（参数方式）")
    @PostMapping("/group/send/param")
    public ApiResponse<Void> sendGroupMessageParam(
            @RequestParam Long groupId,
            @RequestParam Long senderId,
            @RequestParam String content,
            @RequestParam(defaultValue = "text") String messageType) {
        
        groupChatService.sendMessage(groupId, senderId, content, messageType);
        return ApiResponse.success("消息发送成功", null);
    }

    @Operation(summary = "发送群聊消息（实体方式）")
    @PostMapping("/group/send")
    public ApiResponse<Void> sendGroupMessage(@RequestBody GroupChatMessage dto) {
        groupChatService.sendMessage(dto.getGroupId(), dto.getSenderId(), dto.getContent(), dto.getMessageType());
        return ApiResponse.success("消息发送成功", null);
    }

    @Operation(summary = "获取单聊历史消息")
    @GetMapping("/single/history")
    public ApiResponse<List<ChatMessage>> getSingleChatHistory(
            @RequestParam Long userId1,
            @RequestParam Long userId2,
            @RequestParam(defaultValue = "50") Integer limit) {
        
        List<ChatMessage> history = chatService.getChatHistory(userId1, userId2, limit);
        return ApiResponse.success("成功", history);
    }

    @Operation(summary = "获取群聊历史消息")
    @GetMapping("/group/history")
    public ApiResponse<List<com.minecraft.entity.GroupChatMessage>> getGroupChatHistory(
            @RequestParam Long groupId,
            @RequestParam(defaultValue = "50") Integer limit) {
        
        List<com.minecraft.entity.GroupChatMessage> history = groupChatService.getChatHistory(groupId, limit);
        return ApiResponse.success("成功", history);
    }

    @Operation(summary = "发送好友申请（参数方式）")
    @PostMapping("/friend/request/param")
    public ApiResponse<Void> sendFriendRequestParam(
            @RequestParam Long senderId,
            @RequestParam(required = false) Long receiverId,
            @RequestParam(required = false) String receiverPhone,
            @RequestParam(required = false) String message) {
        
        if (receiverId == null && receiverPhone == null) {
            return ApiResponse.error(400, "receiverId或receiverPhone必须提供一个");
        }
        
        if (receiverId == null && receiverPhone != null) {
            User user = userService.getUserByPhone(receiverPhone);
            if (user != null) {
                receiverId = user.getId();
            } else {
                return ApiResponse.error(404, "未找到该手机号对应的用户");
            }
        }
        
        friendService.addFriend(senderId, receiverId);
        return ApiResponse.success("好友申请发送成功", null);
    }

    @Operation(summary = "发送好友申请（实体方式）")
    @PostMapping("/friend/request")
    public ApiResponse<Void> sendFriendRequest(@RequestBody FriendRequestWithPhoneDTO dto) {
        if (dto.getReceiverId() == null && dto.getReceiverPhone() == null) {
            return ApiResponse.error(400, "receiverId或receiverPhone必须提供一个");
        }
        
        Long receiverId = dto.getReceiverId();
        if (receiverId == null && dto.getReceiverPhone() != null) {
            User user = userService.getUserByPhone(dto.getReceiverPhone());
            if (user != null) {
                receiverId = user.getId();
            } else {
                return ApiResponse.error(404, "未找到该手机号对应的用户");
            }
        }
        
        friendService.addFriend(dto.getSenderId(), receiverId);
        return ApiResponse.success("好友申请发送成功", null);
    }

    @Operation(summary = "处理好友申请")
    @PutMapping("/friend/request/handle")
    public ApiResponse<Void> handleFriendRequest(
            @RequestParam Long requestId,
            @RequestParam Integer status) {
        
        Friend friend = friendService.getById(requestId);
        if (friend == null) {
            return ApiResponse.error(404, "好友申请不存在");
        }
        
        if (status == 1) {
            friendService.acceptFriend(friend.getFriendId(), friend.getUserId());
        } else if (status == 2) {
            friendService.deleteFriend(friend.getUserId(), friend.getFriendId());
        } else {
            return ApiResponse.error(400, "无效的状态值");
        }
        
        return ApiResponse.success("处理成功", null);
    }

    @Operation(summary = "接受好友申请")
    @PutMapping("/friend/request/accept")
    public ApiResponse<Void> acceptFriendRequest(@RequestParam Long requestId) {
        Friend friend = friendService.getById(requestId);
        if (friend == null) {
            return ApiResponse.error(404, "好友申请不存在");
        }
        
        friendService.acceptFriend(friend.getFriendId(), friend.getUserId());
        return ApiResponse.success("好友申请已接受", null);
    }

    @Operation(summary = "拒绝好友申请")
    @PutMapping("/friend/request/reject")
    public ApiResponse<Void> rejectFriendRequest(@RequestParam Long requestId) {
        Friend friend = friendService.getById(requestId);
        if (friend == null) {
            return ApiResponse.error(404, "好友申请不存在");
        }
        
        friendService.deleteFriend(friend.getUserId(), friend.getFriendId());
        return ApiResponse.success("好友申请已拒绝", null);
    }

    @Operation(summary = "获取好友列表")
    @GetMapping("/friend/list")
    public ApiResponse<List<Friend>> getFriendList(@RequestParam Long userId) {
        List<Friend> friends = friendService.getFriendList(userId);
        return ApiResponse.success("成功", friends);
    }

    @Operation(summary = "获取好友信息列表")
    @GetMapping("/friend/info/list")
    public ApiResponse<List<FriendInfoDTO>> getFriendInfoList(@RequestParam Long userId) {
        List<FriendInfoDTO> friends = friendService.getFriendInfoList(userId);
        return ApiResponse.success("成功", friends);
    }

    @Operation(summary = "删除好友")
    @DeleteMapping("/friend/delete")
    public ApiResponse<Void> deleteFriend(
            @RequestParam Long userId,
            @RequestParam Long friendId) {
        
        friendService.deleteFriend(userId, friendId);
        return ApiResponse.success("好友删除成功", null);
    }

    @Operation(summary = "获取待处理好友申请")
    @GetMapping("/friend/request/pending")
    public ApiResponse<List<Friend>> getPendingFriendRequests(@RequestParam Long userId) {
        List<Friend> requests = friendService.getFriendRequestList(userId);
        return ApiResponse.success("成功", requests);
    }

    @Operation(summary = "获取待处理好友申请（含用户信息）")
    @GetMapping("/friend/request/info/pending")
    public ApiResponse<List<FriendRequestDTO>> getPendingFriendRequestsWithInfo(@RequestParam Long userId) {
        List<FriendRequestDTO> requests = friendService.getFriendRequestInfoList(userId);
        return ApiResponse.success("成功", requests);
    }

    @Operation(summary = "标记消息为已读")
    @PostMapping("/message/read")
    public ApiResponse<Void> markMessageAsRead(
            @RequestParam Long messageId,
            @RequestParam Long userId,
            @RequestParam(required = false) Long groupId) {
        
        if (groupId != null) {
            groupChatService.markAsRead(userId, groupId);
        } else {
            chatService.markMessageAsRead(messageId, userId);
        }
        
        return ApiResponse.success("消息已读", null);
    }

    @Operation(summary = "获取未读消息数量")
    @GetMapping("/message/unread/count")
    public ApiResponse<Long> getUnreadMessageCount(
            @RequestParam Long userId,
            @RequestParam(required = false) Long groupId) {
        
        Long count = 0L;
        if (groupId != null) {
            count = groupChatService.getUnreadCount(userId, groupId);
        } else {
            count = 0L;
        }
        
        return ApiResponse.success("成功", count);
    }
}
