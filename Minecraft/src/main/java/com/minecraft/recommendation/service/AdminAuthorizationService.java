package com.minecraft.recommendation.service;

import com.minecraft.entity.User;
import com.minecraft.exception.BusinessException;
import com.minecraft.service.UserService;
import com.minecraft.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 推荐后台统一鉴权入口：SecurityConfig 对接口全放行，
 * 因此管理员身份在代码中强制校验（permissions='0' 为管理员）。
 */
@Service
@RequiredArgsConstructor
public class AdminAuthorizationService {

    /** 管理员权限标识 */
    public static final String ADMIN_PERMISSION = "0";

    private final UserService userService;

    /**
     * 要求当前请求具备管理员身份，返回管理员用户 ID。
     *
     * @throws BusinessException 401 未登录/用户不存在，403 非管理员
     */
    public Long requireAdmin() {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }
        User user = userService.getById(userId);
        if (user == null) {
            throw new BusinessException(401, "用户不存在，请重新登录");
        }
        if (!ADMIN_PERMISSION.equals(user.getPermissions())) {
            throw new BusinessException(403, "无推荐后台访问权限");
        }
        return userId;
    }
}
