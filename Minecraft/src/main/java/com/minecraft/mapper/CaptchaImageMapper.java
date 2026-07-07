package com.minecraft.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minecraft.entity.CaptchaImage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Optional;

@Mapper
public interface CaptchaImageMapper extends BaseMapper<CaptchaImage> {
    
    @Select("SELECT * FROM captcha_image WHERE md5_hash = #{md5Hash} AND status = 1 LIMIT 1")
    Optional<CaptchaImage> findByMd5Hash(@Param("md5Hash") String md5Hash);
}