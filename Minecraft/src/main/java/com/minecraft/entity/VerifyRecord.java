package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("captcha_verify_record")
public class VerifyRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("trace_id")
    private String traceId;
    
    @TableField("image_id")
    private String imageId;
    
    @TableField("client_ip")
    private String clientIp;
    
    @TableField("user_agent")
    private String userAgent;
    
    @TableField("slider_offset")
    private Integer sliderOffset;
    
    @TableField("target_offset")
    private Integer targetOffset;
    
    @TableField("is_passed")
    private Integer isPassed;
    
    @TableField("cost_time")
    private Integer costTime;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}