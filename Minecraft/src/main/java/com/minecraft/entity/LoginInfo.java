package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 登录信息表
 */
@Data
@TableName("login_info")
public class LoginInfo {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private String username;
    
    private String ipaddr;
    
    private String loginLocation;
    
    private String browser;
    
    private String os;
    
    private String status;
    
    private String msg;
    
    private LocalDateTime loginTime;
}