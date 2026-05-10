package com.minecraft.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minecraft.entity.Emoji;
import com.minecraft.mapper.EmojiMapper;
import com.minecraft.service.EmojiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EmojiServiceImpl extends ServiceImpl<EmojiMapper, Emoji> implements EmojiService {

    @Autowired
    private EmojiMapper emojiMapper;

    @Override
    public List<Emoji> getEmojisByCategory(String category) {
        return emojiMapper.selectByCategory(category);
    }

    @Override
    public List<Emoji> getAllEmojis() {
        return emojiMapper.selectAllActive();
    }

    @Override
    public List<String> getAllCategories() {
        return emojiMapper.selectAllCategories();
    }

    @Override
    public Map<String, List<Emoji>> getEmojisGroupedByCategory() {
        List<Emoji> emojis = getAllEmojis();
        return emojis.stream()
                .collect(Collectors.groupingBy(Emoji::getCategory));
    }

    @Override
    public Emoji getEmojiByCode(String emojiCode) {
        LambdaQueryWrapper<Emoji> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Emoji::getEmojiCode, emojiCode)
                .eq(Emoji::getIsActive, 1);
        return getOne(wrapper);
    }
}