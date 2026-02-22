package com.minecraft.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.minecraft.entity.Tags;

public interface TagsService extends IService<Tags> {
    Tags getTagById(Integer id);
    void addTag(Tags tag);
    void updateTag(Tags tag);
    void deleteTag(Integer id);
}
