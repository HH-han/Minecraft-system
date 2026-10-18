package com.minecraft;

import com.minecraft.utils.ConsoleUtils;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@EnableAsync
@EnableScheduling
@MapperScan("com.minecraft.mapper")
public class MinecraftApplication {

    public static void main(String[] args) {
        SpringApplication.run(MinecraftApplication.class, args);
        ConsoleUtils.printStartupMessages();
    }

}
