package com.minecraft.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.minecraft.entity.Recommend;

import java.util.List;

public interface RecommendService extends IService<Recommend> {
    List<Recommend> getRecommendList();
    Recommend getRecommendById(Integer id);
    void addRecommend(Recommend recommend);
    void updateRecommend(Recommend recommend);
    void deleteRecommend(Integer id);
}