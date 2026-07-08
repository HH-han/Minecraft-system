package com.minecraft.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minecraft.entity.CaptchaImage;
import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;

@Mapper
public interface CaptchaImageMapper extends BaseMapper<CaptchaImage> {
    
    Optional<CaptchaImage> findByMd5Hash(String md5Hash);
    
    void deleteOldRecords(int limit);
}