package com.minecraft.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserVO {
    private Long id;
    private String account;
    private String username;
    private String email;
    private String phone;
    private String avatar;
    private String gender;
    private Integer age;
    private String occupation;
    private String hobbies;
    private String permissions;
    private String signature;
    private String nickname;
    private String experience;
    private String bio;
    /** 在线状态：0-在线 1-离线 */
    private Integer online;
    /** 账号状态：0-禁用 1-正常 */
    private Integer status;
    /** 积分余额 */
    private Integer points;
    private LocalDateTime createTime;
}
