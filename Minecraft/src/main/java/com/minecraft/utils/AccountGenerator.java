package com.minecraft.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AccountGenerator {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    public static String generateAccount() {
        String datePrefix = LocalDateTime.now().format(FORMATTER);
        String randomSuffix = String.format("%06d", (int)(Math.random() * 1000000));
        return datePrefix + randomSuffix;
    }
}
