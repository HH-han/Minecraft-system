package com.minecraft.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minecraft.entity.UserEmoji;
import com.minecraft.mapper.UserEmojiMapper;
import com.minecraft.service.UserEmojiService;
import com.minecraft.utils.ImageUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class UserEmojiServiceImpl extends ServiceImpl<UserEmojiMapper, UserEmoji> implements UserEmojiService {

    private static final Logger logger = LoggerFactory.getLogger(UserEmojiServiceImpl.class);
    
    @Autowired
    private ImageUtils imageUtils;

    private static final int MAX_EMOJI_COUNT = 100;

    @Override
    @Transactional
    public UserEmoji uploadEmoji(Long userId, MultipartFile file, String emojiName) {
        Integer currentCount = getEmojiCount(userId);
        if (currentCount >= MAX_EMOJI_COUNT) {
            throw new RuntimeException("表情包数量已达上限");
        }

        String emojiUrl;
        try {
            emojiUrl = imageUtils.processMultipartFile(file);
        } catch (Exception e) {
            logger.error("图片上传失败", e);
            throw new RuntimeException("图片上传失败: " + e.getMessage(), e);
        }

        UserEmoji userEmoji = new UserEmoji();
        userEmoji.setUserId(userId);
        userEmoji.setEmojiUrl(emojiUrl);
        userEmoji.setEmojiName(emojiName);
        userEmoji.setSortOrder(currentCount);

        save(userEmoji);
        return userEmoji;
    }

    @Override
    @Transactional
    public UserEmoji uploadEmojiBase64(Long userId, String base64Image, String emojiName) {
        Integer currentCount = getEmojiCount(userId);
        if (currentCount >= MAX_EMOJI_COUNT) {
            throw new RuntimeException("表情包数量已达上限");
        }

        String emojiUrl;
        try {
            emojiUrl = imageUtils.processBase64Image(base64Image);
            if (emojiUrl == null) {
                throw new RuntimeException("无效的Base64图片数据");
            }
        } catch (Exception e) {
            logger.error("Base64图片处理失败", e);
            throw new RuntimeException("图片上传失败: " + e.getMessage(), e);
        }

        UserEmoji userEmoji = new UserEmoji();
        userEmoji.setUserId(userId);
        userEmoji.setEmojiUrl(emojiUrl);
        userEmoji.setEmojiName(emojiName);
        userEmoji.setSortOrder(currentCount);

        save(userEmoji);
        return userEmoji;
    }

    @Override
    public List<UserEmoji> getEmojisByUserId(Long userId) {
        return baseMapper.selectByUserId(userId);
    }

    @Override
    @Transactional
    public boolean deleteEmoji(Long userId, Long emojiId) {
        LambdaQueryWrapper<UserEmoji> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserEmoji::getId, emojiId)
                .eq(UserEmoji::getUserId, userId);
        
        UserEmoji emoji = getOne(wrapper);
        if (emoji == null) {
            return false;
        }

        try {
            imageUtils.deleteImage(emoji.getEmojiUrl());
        } catch (Exception e) {
            logger.warn("删除图片文件失败: {}", emoji.getEmojiUrl(), e);
        }
        
        removeById(emojiId);

        LambdaQueryWrapper<UserEmoji> updateWrapper = new LambdaQueryWrapper<>();
        updateWrapper.eq(UserEmoji::getUserId, userId)
                     .gt(UserEmoji::getSortOrder, emoji.getSortOrder());
        
        List<UserEmoji> emojis = list(updateWrapper);
        emojis.forEach(e -> {
            e.setSortOrder(e.getSortOrder() - 1);
            updateById(e);
        });

        return true;
    }

    @Override
    @Transactional
    public boolean updateEmojiName(Long userId, Long emojiId, String emojiName) {
        LambdaQueryWrapper<UserEmoji> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserEmoji::getId, emojiId)
                .eq(UserEmoji::getUserId, userId);
        
        UserEmoji emoji = getOne(wrapper);
        if (emoji == null) {
            return false;
        }

        emoji.setEmojiName(emojiName);
        updateById(emoji);
        return true;
    }

    @Override
    public Integer getEmojiCount(Long userId) {
        Integer count = baseMapper.countByUserId(userId);
        return count != null ? count : 0;
    }
}