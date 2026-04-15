package com.minecraft.utils;

import com.minecraft.entity.User;
import com.minecraft.vo.OnlineUserVO;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 在线用户工具类
 */
public class OnlineUserUtil {
    
    private static final DeviceInfoUtils deviceInfoUtils = new DeviceInfoUtils();
    private static final IpLocationUtils ipLocationUtils = new IpLocationUtils();
    
    /**
     * 从User实体创建OnlineUserVO
     * @param user 用户实体
     * @param request HttpServletRequest
     * @return OnlineUserVO
     */
    public static OnlineUserVO createOnlineUserVO(User user, HttpServletRequest request) {
        OnlineUserVO onlineUser = new OnlineUserVO();
        
        // 获取用户信息
        onlineUser.setId(user.getId());
        onlineUser.setUsername(user.getUsername());
        // 确保nickname不为空
        String nickname = user.getNickname();
        if (nickname == null || nickname.trim().isEmpty()) {
            nickname = user.getUsername();
            if (nickname == null || nickname.trim().isEmpty()) {
                nickname = user.getAccount();
            }
        }
        onlineUser.setNickname(nickname);
        onlineUser.setRole(getUserRole(user));
        
        // 获取登录信息
        String ip = deviceInfoUtils.getClientIP(request);
        onlineUser.setLoginIp(ip);
        
        // 获取时间信息
        onlineUser.setLoginTime(new Date());
        onlineUser.setLastActivityTime(new Date());
        
        // 获取设备信息
        String userAgent = deviceInfoUtils.getUserAgent(request);
        onlineUser.setBrowser(deviceInfoUtils.getBrowser(userAgent));
        onlineUser.setOs(deviceInfoUtils.getOS(userAgent));
        
        // 设置在线状态
        onlineUser.setStatus("online");
        
        return onlineUser;
    }
    
    /**
     * 获取用户角色
     * @param user 用户实体
     * @return 角色名称
     */
    private static String getUserRole(User user) {
        if (user == null) {
            return "user";
        }
        
        // 根据权限判断角色
        String permissions = user.getPermissions();
        if ("0".equals(permissions)) {
            return "admin";
        } else if ("1".equals(permissions)) {
            return "vip";
        } else {
            return "user";
        }
    }
    
    /**
     * 更新用户最后活动时间
     * @param onlineUser 在线用户
     */
    public static void updateLastActivityTime(OnlineUserVO onlineUser) {
        if (onlineUser != null) {
            onlineUser.setLastActivityTime(new Date());
        }
    }
    
    /**
     * 更新用户状态
     * @param onlineUser 在线用户
     * @param status 状态
     */
    public static void updateUserStatus(OnlineUserVO onlineUser, String status) {
        if (onlineUser != null) {
            onlineUser.setStatus(status);
            onlineUser.setLastActivityTime(new Date());
        }
    }
}
