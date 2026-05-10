package com.minecraft.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.minecraft.entity.UserEmoji;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserEmojiService extends IService<UserEmoji> {
    UserEmoji uploadEmoji(Long userId, MultipartFile file, String emojiName);
    UserEmoji uploadEmojiBase64(Long userId, String base64Image, String emojiName);
    List<UserEmoji> getEmojisByUserId(Long userId);
    boolean deleteEmoji(Long userId, Long emojiId);
    boolean updateEmojiName(Long userId, Long emojiId, String emojiName);
    Integer getEmojiCount(Long userId);
}