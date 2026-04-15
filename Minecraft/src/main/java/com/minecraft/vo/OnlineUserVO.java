package com.minecraft.vo;

import lombok.Data;

import java.util.Date;

/**
 * 在线用户VO
 */
@Data
public class OnlineUserVO {
    /**
     * 用户ID
     */
    private Long id;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 昵称
     */
    private String nickname;
    
    /**
     * 角色
     */
    private String role;
    
    /**
     * 登录IP
     */
    private String loginIp;
    
    /**
     * 登录时间
     */
    private Date loginTime;
    
    /**
     * 最后活动时间
     */
    private Date lastActivityTime;
    
    /**
     * 浏览器
     */
    private String browser;
    
    /**
     * 操作系统
     */
    private String os;
    
    /**
     * 在线状态
     */
    private String status;
}
