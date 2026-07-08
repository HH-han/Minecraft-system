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
    private static final double GOLDEN_RATIO = (1 + Math.sqrt(5)) / 2;

    private static final List<String> BACKGROUND_COLORS = Arrays.asList(
            "#b0c4de", "#f7dc6f", "#a3d8a3", "#f5b7b1", "#d7bde2", "#aed6f1",
            "#87ceeb", "#98d8c8", "#f7dc6f", "#d4ac6e", "#9370db", "#87cefa"
    );

    private static final List<String> ACCENT_COLORS = Arrays.asList(
            "#4a6cf7", "#e74c3c", "#27ae60", "#f39c12", "#9b59b6", "#3498db"
    );

    private static final List<String> PATTERNS = Arrays.asList("circle", "rect", "triangle", "line");

    public static String generateSVG(int width, int height) {
        String bgColor = BACKGROUND_COLORS.get(ThreadLocalRandom.current().nextInt(BACKGROUND_COLORS.size()));
        String accentColor = ACCENT_COLORS.get(ThreadLocalRandom.current().nextInt(ACCENT_COLORS.size()));

        int cx = 60 + ThreadLocalRandom.current().nextInt(width - 120);
        int cy = 30 + ThreadLocalRandom.current().nextInt(height - 60);
        int r = 20 + ThreadLocalRandom.current().nextInt(40);

        int rectX = 40 + ThreadLocalRandom.current().nextInt(width - 120);
        int rectY = 20 + ThreadLocalRandom.current().nextInt(height - 80);
        int rectW = 60 + ThreadLocalRandom.current().nextInt(80);
        int rectH = 40 + ThreadLocalRandom.current().nextInt(60);

        double brightness = 0.85 + ThreadLocalRandom.current().nextDouble() * 0.3;
        
        StringBuilder noise = new StringBuilder();
        int noiseCount = Math.min((int) (width * height * 0.008), 50);
        for (int i = 0; i < noiseCount; i++) {
            int nx = ThreadLocalRandom.current().nextInt(width);
            int ny = ThreadLocalRandom.current().nextInt(height);
            noise.append(String.format("<rect x=\"%d\" y=\"%d\" width=\"1\" height=\"1\" fill=\"#000\" opacity=\"%.2f\"/>",
                    nx, ny, 0.08 + ThreadLocalRandom.current().nextDouble() * 0.12));
        }

        return String.format(
                "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"%d\" height=\"%d\">" +
                "<defs>" +
                "<filter id=\"b\"><feComponentTransfer><feFuncR type=\"linear\" slope=\"%.2f\"/><feFuncG type=\"linear\" slope=\"%.2f\"/><feFuncB type=\"linear\" slope=\"%.2f\"/></feComponentTransfer></filter>" +
                "</defs>" +
                "<rect width=\"%d\" height=\"%d\" fill=\"%s\"/>" +
                "<circle cx=\"%d\" cy=\"%d\" r=\"%d\" fill=\"%s\" opacity=\"0.6\" filter=\"url(#b)\"/>" +
                "<rect x=\"%d\" y=\"%d\" width=\"%d\" height=\"%d\" fill=\"%s\" opacity=\"0.5\" filter=\"url(#b)\"/>" +
                "%s" +
                "<rect x=\"60\" y=\"30\" width=\"200\" height=\"120\" fill=\"none\" stroke=\"#2c3e50\" stroke-width=\"2\" opacity=\"0.2\"/>" +
                "<circle cx=\"%d\" cy=\"%d\" r=\"%d\" fill=\"#4a6cf7\" opacity=\"0.15\"/>" +
                "</svg>",
                width, height,
                brightness, brightness, brightness,
                width, height, bgColor,
                cx, cy, r, accentColor,
                rectX, rectY, rectW, rectH, accentColor,
                noise,
                100 + ThreadLocalRandom.current().nextInt(100),
                40 + ThreadLocalRandom.current().nextInt(60),
                10 + ThreadLocalRandom.current().nextInt(20)
        );
    }

    public static PuzzleConfig generatePuzzleConfig(CaptchaImage image, int width) {
        PuzzleConfig config = new PuzzleConfig();
        config.setImageId(image.getId());
        config.setVersion(1);

        int pieceWidth = 44;
        int pieceHeight = 44;

        int pieceX = generateGoldenSectionGapPosition(width, pieceWidth, 60, width - pieceWidth - 80);
        int pieceY = generateGoldenSectionGapPosition(image.getHeight(), pieceHeight, 40, image.getHeight() - pieceHeight - 40);

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

    private static int generateGoldenSectionGapPosition(int maxRange, int pieceSize, int minOffset, int maxOffset) {
        int safeMin = minOffset + pieceSize;
        int safeMax = maxOffset - pieceSize;
        
        int center = maxRange / 2;
        int centerAvoidance = pieceSize * 3;
        
        int leftZoneMax = center - centerAvoidance;
        int rightZoneMin = center + centerAvoidance;
        
        int selectedZone;
        if (safeMin < leftZoneMax && rightZoneMin < safeMax) {
            selectedZone = ThreadLocalRandom.current().nextBoolean() ? 0 : 1;
        } else if (safeMin < leftZoneMax) {
            selectedZone = 0;
        } else {
            selectedZone = 1;
        }
        
        int zoneStart, zoneEnd;
        if (selectedZone == 0) {
            zoneStart = safeMin;
            zoneEnd = leftZoneMax;
        } else {
            zoneStart = rightZoneMin;
            zoneEnd = safeMax;
        }
        
        int zoneSize = zoneEnd - zoneStart + 1;
        double goldenPosition = zoneStart + (zoneSize / GOLDEN_RATIO);
        double jitter = (ThreadLocalRandom.current().nextDouble() - 0.5) * pieceSize;
        
        int result = (int) (goldenPosition + jitter);
        return Math.max(safeMin, Math.min(safeMax, result));
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

    public static String calculatePixelHash(String svgContent) {
        String normalized = svgContent.replaceAll("\\s+", "").toLowerCase();
        byte[] data = normalized.getBytes(StandardCharsets.UTF_8);
        
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(data);
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
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