package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("captcha_image")
public class CaptchaImage {
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    
    @TableField("image_key")
    private String imageKey;
    
    @TableField("file_name")
    private String fileName;
    
    @TableField("file_path")
    private String filePath;
    
    @TableField("file_size")
    private Integer fileSize;
    
    private Integer width;
    
    private Integer height;
    
    @TableField("mime_type")
    private String mimeType;
    
    @TableField("md5_hash")
    private String md5Hash;
    
    private Integer status;
    
    @TableField("file_content")
    private String fileContent;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}