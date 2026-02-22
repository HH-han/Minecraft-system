package com.minecraft.utils;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.stereotype.Component;


@Component
public class RedisHealthChecker {

    private static final Logger logger = LoggerFactory.getLogger(RedisHealthChecker.class);

    @Autowired(required = false)
    private RedisConnectionFactory redisConnectionFactory;

    @PostConstruct
    public void checkRedisConnection() {
        try {
            if (redisConnectionFactory != null) {
                redisConnectionFactory.getConnection().close();
                logger.info("✅ Redis 连接成功");
            } else {
                logger.warn("⚠️  Redis 配置未找到，请检查 application.yml 中的 Redis 配置");
            }
        } catch (Exception e) {
            logger.error("❌ Redis 连接失败: {}", e.getMessage());
            logger.warn("⚠️  请确保 Redis 服务已启动，并检查以下配置：");
            logger.warn("   - Redis 地址: localhost:6379");
            logger.warn("   - Redis 密码: (如果配置了密码)");
            logger.warn("   - 启动 Redis: redis-server 或 redis-cli ping");
            logger.warn("   - Windows 启动 Redis: redis-server.exe");
        }
    }
}