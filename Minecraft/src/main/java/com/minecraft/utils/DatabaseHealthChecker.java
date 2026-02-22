package com.minecraft.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class DatabaseHealthChecker implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseHealthChecker.class);

    @Autowired(required = false)
    private DataSource dataSource;

    @Autowired(required = false)
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) {
        checkDatabaseConnection();
    }

    private void checkDatabaseConnection() {
        try {
            if (jdbcTemplate != null) {
                jdbcTemplate.queryForObject("SELECT 1", Integer.class);
                logger.info("✅ MySQL 数据库连接成功");
            } else {
                logger.warn("⚠️  数据库配置未找到，请检查 application.yml 中的 datasource 配置");
            }
        } catch (Exception e) {
            logger.error("❌ MySQL 数据库连接失败: {}", e.getMessage());
            logger.warn("⚠️  请确保 MySQL 服务已启动，并检查以下配置：");
            logger.warn("   - MySQL 地址: localhost:3306");
            logger.warn("   - 数据库名称: db_minecraft");
            logger.warn("   - 用户名: root");
            logger.warn("   - 密码: 123456");
            logger.warn("   - 启动 MySQL: net start mysql");
            logger.warn("   - 检查 MySQL 状态: sc query mysql");
            logger.warn("   - 测试连接: mysql -uroot -p123456 -e \"SELECT 1;\"");
            logger.warn("   - 查看日志: src/main/resources/static/bug");
        }
    }
}