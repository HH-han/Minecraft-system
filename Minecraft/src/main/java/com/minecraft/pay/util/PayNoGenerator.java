package com.minecraft.pay.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 支付相关单号生成器
 */
public final class PayNoGenerator {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

    private PayNoGenerator() {
    }

    public static String payNo() {
        return "PAY" + LocalDateTime.now().format(FORMATTER) + random(4);
    }

    public static String flowNo() {
        return "FLOW" + LocalDateTime.now().format(FORMATTER) + random(4);
    }

    public static String refundNo() {
        return "RF" + LocalDateTime.now().format(FORMATTER) + random(4);
    }

    private static String random(int digits) {
        int bound = (int) Math.pow(10, digits);
        return String.format("%0" + digits + "d", ThreadLocalRandom.current().nextInt(bound));
    }
}
