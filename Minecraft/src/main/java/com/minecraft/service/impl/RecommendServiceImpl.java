package com.minecraft.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minecraft.entity.Recommend;
import com.minecraft.mapper.RecommendMapper;
import com.minecraft.service.RecommendService;
import com.minecraft.utils.ImageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecommendServiceImpl extends ServiceImpl<RecommendMapper, Recommend> implements RecommendService {

    @Autowired
    private ImageUtils imageUtils;

    @Override
    public List<Recommend> getRecommendList() {
        return getRecommendListByState("0");
    }

    @Override
    public List<Recommend> getRecommendListByState(String state) {
        if (state == null) {
            return list();
        }
        return list(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Recommend>().eq("state", state));
    }

    @Override
    public Recommend getRecommendById(Integer id) {
        return getById(id);
    }

    @Override
    public void addRecommend(Recommend recommend) {
        save(recommend);
    }

    @Override
    public void updateRecommend(Recommend recommend) {
        updateById(recommend);
    }

    @Override
    public void deleteRecommend(Integer id) {
        removeById(id);
    }
    
    @Override
    public boolean save(Recommend recommend) {
        try {
            // 处理图片上传
            if (recommend.getImage() != null && recommend.getImage().startsWith("data:image")) {
                String processedImage = imageUtils.processBase64Image(recommend.getImage());
                recommend.setImage(processedImage);
            }
            
            return super.save(recommend);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean updateById(Recommend recommend) {
        try {
            // 处理图片上传
            if (recommend.getImage() != null && recommend.getImage().startsWith("data:image")) {
                String processedImage = imageUtils.processBase64Image(recommend.getImage());
                recommend.setImage(processedImage);
            }
            
            return super.updateById(recommend);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}