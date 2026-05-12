package com.minecraft.entity;

import com.minecraft.enums.CallStatus;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("call_records")
public class CallRecord {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String callId;
    
    private Long callerId;
    
    private Long receiverId;
    
    private String callType;
    
    private String status;
    
    private Integer duration;
    
    private LocalDateTime startTime;
    
    private LocalDateTime endTime;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}