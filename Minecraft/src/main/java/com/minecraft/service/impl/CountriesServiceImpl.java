package com.minecraft.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minecraft.entity.Countries;
import com.minecraft.mapper.CountriesMapper;
import com.minecraft.service.CountriesService;
import com.minecraft.utils.ImageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CountriesServiceImpl extends ServiceImpl<CountriesMapper, Countries> implements CountriesService {

    @Autowired
    private ImageUtils imageUtils;

    @Override
    public Countries getCountryById(Integer id) {
        return getById(id);
    }

    @Override
    public void addCountry(Countries country) {
        save(country);
    }

    @Override
    public void updateCountry(Countries country) {
        updateById(country);
    }

    @Override
    public void deleteCountry(Integer id) {
        removeById(id);
    }
    
    @Override
    public boolean save(Countries country) {
        try {
            // 处理图片上传
            if (country.getImage() != null && country.getImage().startsWith("data:image")) {
                String processedImage = imageUtils.processBase64Image(country.getImage());
                country.setImage(processedImage);
            }
            
            return super.save(country);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean updateById(Countries country) {
        try {
            // 处理图片上传
            if (country.getImage() != null && country.getImage().startsWith("data:image")) {
                String processedImage = imageUtils.processBase64Image(country.getImage());
                country.setImage(processedImage);
            }
            
            return super.updateById(country);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
