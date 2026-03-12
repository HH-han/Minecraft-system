package com.minecraft.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.minecraft.entity.SafetyTips;

import java.util.List;

public interface SafetyTipsService extends IService<SafetyTips> {
    List<SafetyTips> getActiveSafetyTips();
    List<SafetyTips> getSafetyTipsByCategory(Long categoryId);
}
