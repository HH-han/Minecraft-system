package com.minecraft.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minecraft.entity.SafetyTips;
import com.minecraft.mapper.SafetyTipsMapper;
import com.minecraft.service.SafetyTipsService;
import com.minecraft.utils.ImageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SafetyTipsServiceImpl extends ServiceImpl<SafetyTipsMapper, SafetyTips> implements SafetyTipsService {
    @Autowired
    private ImageUtils imageUtils;

    @Override
    public List<SafetyTips> getActiveSafetyTips() {
        QueryWrapper<SafetyTips> wrapper = new QueryWrapper<>();
        wrapper.eq("status", 1)
                .orderByDesc("sort_weight")
                .orderByDesc("create_time");
        return baseMapper.selectList(wrapper);
    }

    @Override
    public List<SafetyTips> getSafetyTipsByCategory(Long categoryId) {
        QueryWrapper<SafetyTips> wrapper = new QueryWrapper<>();
        wrapper.eq("category_id", categoryId)
                .eq("status", 1)
                .orderByDesc("sort_weight")
                .orderByDesc("create_time");
        return baseMapper.selectList(wrapper);
    }

    @Override
    public boolean save(SafetyTips safetyTips) {
        try {
            // 处理图片上传
            if (safetyTips.getImageUrl() != null && safetyTips.getImageUrl().startsWith("data:image")) {
                String processedImageUrl = imageUtils.processBase64Image(safetyTips.getImageUrl());
                safetyTips.setImageUrl(processedImageUrl);
            }
            return super.save(safetyTips);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateById(SafetyTips safetyTips) {
        try {
            // 处理图片上传
            if (safetyTips.getImageUrl() != null && safetyTips.getImageUrl().startsWith("data:image")) {
                String processedImageUrl = imageUtils.processBase64Image(safetyTips.getImageUrl());
                safetyTips.setImageUrl(processedImageUrl);
            }
            return super.updateById(safetyTips);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
