package com.minecraft.utils;

import com.minecraft.entity.User;
import com.minecraft.mapper.OnlineUserMapper;
import com.minecraft.vo.OnlineUserVO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 在线用户服务工具类
 */
@Component
public class OnlineUserServiceUtil {
    
    @Autowired
    private OnlineUserMapper onlineUserMapper;
    
    /**
     * 添加在线用户
     * @param user 用户实体
     * @param request HttpServletRequest
     */
    public void addOnlineUser(User user, HttpServletRequest request) {
        OnlineUserVO onlineUser = OnlineUserUtil.createOnlineUserVO(user, request);
        // 添加在线用户到数据库
        onlineUserMapper.addOnlineUser(onlineUser);
    }
    
    /**
     * 移除在线用户
     * @param userId 用户ID
     */
    public void removeOnlineUser(Long userId) {
        onlineUserMapper.deleteOnlineUser(userId);
    }
    
    /**
     * 获取在线用户列表
     * @param page 页码
     * @param size 每页大小
     * @return 在线用户列表
     */
    public List<OnlineUserVO> getOnlineUsers(int page, int size) {
        int offset = (page - 1) * size;
        return onlineUserMapper.getOnlineUsers(offset, size);
    }
    
    /**
     * 更新用户活动时间
     * @param userId 用户ID
     */
    public void updateUserActivity(Long userId) {
        // 这里需要实现更新用户活动时间的逻辑
    }
    
    /**
     * 检查用户是否在线
     * @param userId 用户ID
     * @return 是否在线
     */
    public boolean isUserOnline(Long userId) {
        OnlineUserVO onlineUser = onlineUserMapper.getOnlineUserById(userId);
        return onlineUser != null;
    }
}
