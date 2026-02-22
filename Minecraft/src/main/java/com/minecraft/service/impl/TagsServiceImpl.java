package com.minecraft.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minecraft.entity.Tags;
import com.minecraft.mapper.TagsMapper;
import com.minecraft.service.TagsService;
import org.springframework.stereotype.Service;

@Service
public class TagsServiceImpl extends ServiceImpl<TagsMapper, Tags> implements TagsService {

    @Override
    public Tags getTagById(Integer id) {
        return getById(id);
    }

    @Override
    public void addTag(Tags tag) {
        save(tag);
    }

    @Override
    public void updateTag(Tags tag) {
        updateById(tag);
    }

    @Override
    public void deleteTag(Integer id) {
        removeById(id);
    }
}
