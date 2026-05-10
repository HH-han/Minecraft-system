package com.minecraft.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.minecraft.entity.Emoji;

import java.util.List;
import java.util.Map;

public interface EmojiService extends IService<Emoji> {
    List<Emoji> getEmojisByCategory(String category);
    List<Emoji> getAllEmojis();
    List<String> getAllCategories();
    Map<String, List<Emoji>> getEmojisGroupedByCategory();
    Emoji getEmojiByCode(String emojiCode);
}