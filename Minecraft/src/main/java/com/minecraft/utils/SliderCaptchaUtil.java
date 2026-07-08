package com.minecraft.utils;

import com.minecraft.entity.CaptchaImage;
import com.minecraft.entity.PuzzleConfig;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class SliderCaptchaUtil {

    private static final int TOLERANCE = 8;

    private static final List<String> COLORS = Arrays.asList(
            "#b0c4de", "#f7dc6f", "#a3d8a3", "#f5b7b1", "#d7bde2", "#aed6f1"
    );

    public static String generateSVG(int width, int height) {
        String bgColor = COLORS.get(ThreadLocalRandom.current().nextInt(COLORS.size()));
        String accentColor = COLORS.get(ThreadLocalRandom.current().nextInt(COLORS.size()));

        int cx = 80 + ThreadLocalRandom.current().nextInt(160);
        int cy = 40 + ThreadLocalRandom.current().nextInt(100);
        int r = 30 + ThreadLocalRandom.current().nextInt(50);

        return String.format(
                "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"%d\" height=\"%d\" viewBox=\"0 0 %d %d\">" +
                "<rect width=\"%d\" height=\"%d\" fill=\"%s\"/>" +
                "<circle cx=\"%d\" cy=\"%d\" r=\"%d\" fill=\"%s\" opacity=\"0.6\"/>" +
                "<rect x=\"60\" y=\"30\" width=\"200\" height=\"120\" fill=\"none\" stroke=\"#2c3e50\" stroke-width=\"2\" opacity=\"0.2\"/>" +
                "<circle cx=\"%d\" cy=\"%d\" r=\"%d\" fill=\"#4a6cf7\" opacity=\"0.15\"/>" +
                "</svg>",
                width, height, width, height,
                width, height, bgColor,
                cx, cy, r, accentColor,
                160 + ThreadLocalRandom.current().nextInt(40),
                80 + ThreadLocalRandom.current().nextInt(40),
                10 + ThreadLocalRandom.current().nextInt(20)
        );
    }

    public static PuzzleConfig generatePuzzleConfig(CaptchaImage image, int width) {
        PuzzleConfig config = new PuzzleConfig();
        config.setImageId(image.getId());
        config.setVersion(1);

        int pieceWidth = 44;
        int pieceHeight = 44;

        int minPieceX = 60;
        int maxPieceX = 105;
        int pieceX = minPieceX + ThreadLocalRandom.current().nextInt(maxPieceX - minPieceX + 1);

        int minPieceY = 50;
        int maxPieceY = 95;
        int pieceY = minPieceY + ThreadLocalRandom.current().nextInt(maxPieceY - minPieceY + 1);

        config.setPieceX(pieceX);
        config.setPieceY(pieceY);
        config.setPieceWidth(pieceWidth);
        config.setPieceHeight(pieceHeight);

        int targetX = width - pieceWidth - 20;
        config.setTargetX(targetX);
        config.setTargetY(pieceY);

        double sliderPercent = (double) (targetX - pieceX) / (width - pieceWidth - pieceX);
        config.setSliderPercent(java.math.BigDecimal.valueOf(Math.min(Math.max(sliderPercent, 0.3), 0.9)));

        return config;
    }

    public static CaptchaImage generateCaptchaImage(int width, int height) {
        String imageId = UUID.randomUUID().toString().replace("-", "");
        String svgContent = generateSVG(width, height);
        byte[] imageBytes = svgContent.getBytes(StandardCharsets.UTF_8);
        String md5 = calculateMD5(imageBytes);

        CaptchaImage image = new CaptchaImage();
        image.setId(imageId);
        image.setImageKey(imageId);
        image.setFileName(imageId + ".svg");
        image.setFileContent(svgContent);
        image.setFileSize(imageBytes.length);
        image.setWidth(width);
        image.setHeight(height);
        image.setMimeType("image/svg+xml");
        image.setMd5Hash(md5);
        image.setStatus(1);

        return image;
    }

    public static String calculateMD5(byte[] data) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(data);
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 algorithm not found", e);
        }
    }

    public static boolean verifySlider(int actualOffset, int targetOffset) {
        return Math.abs(actualOffset - targetOffset) <= TOLERANCE;
    }

    public static boolean verifySlider(int actualOffset, int targetOffset, int tolerance) {
        return Math.abs(actualOffset - targetOffset) <= tolerance;
    }
}