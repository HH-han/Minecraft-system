package com;

import com.minecraft.MinecraftApplication;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;



@SpringBootTest(classes = MinecraftApplication.class)
class MinecraftApplicationTests {

    @Resource
    private ApplicationContext applicationContext;

    @Test
    void contextLoads() {
        System.out.println("Spring Boot 应用启动成功！");
        System.out.println("Bean数量: " + applicationContext.getBeanDefinitionCount());
    }

    @Test
    void testApplication() {
        System.out.println("Minecraft 旅游系统测试环境加载完成");
    }

}