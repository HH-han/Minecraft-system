package com.minecraft.service;

import java.util.Map;

/**
 * Token服务接口
 */
public interface TokenService {

    /**
     * 获取Token状态信息
     * @return Token状态信息
     */
    Map<String, Object> getTokenStatus();

    /**
     * 刷新Token
     * @return 新的Token信息
     */
    Map<String, Object> refreshToken();
}
