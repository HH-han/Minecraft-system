package com.minecraft.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minecraft.entity.WorldCharacteristics;
import com.minecraft.mapper.WorldCharacteristicsMapper;
import com.minecraft.service.WorldCharacteristicsService;
import com.minecraft.utils.ImageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WorldCharacteristicsServiceImpl extends ServiceImpl<WorldCharacteristicsMapper, WorldCharacteristics> implements WorldCharacteristicsService {
    @Autowired
    private ImageUtils imageUtils;

    @Override
    public boolean save(WorldCharacteristics worldCharacteristics) {
        try {
            // 处理图片上传
            if (worldCharacteristics.getImage() != null && worldCharacteristics.getImage().startsWith("data:image")) {
                String processedImage = imageUtils.processBase64Image(worldCharacteristics.getImage());
                worldCharacteristics.setImage(processedImage);
            }
            return super.save(worldCharacteristics);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateById(WorldCharacteristics worldCharacteristics) {
        try {
            // 处理图片上传
            if (worldCharacteristics.getImage() != null && worldCharacteristics.getImage().startsWith("data:image")) {
                String processedImage = imageUtils.processBase64Image(worldCharacteristics.getImage());
                worldCharacteristics.setImage(processedImage);
            }
            return super.updateById(worldCharacteristics);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}