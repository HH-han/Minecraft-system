package com.minecraft.config;
import cloud.tianai.captcha.common.constant.CaptchaTypeConstant;
import cloud.tianai.captcha.resource.ResourceStore;
import cloud.tianai.captcha.resource.common.model.dto.Resource;
import cloud.tianai.captcha.resource.impl.LocalMemoryResourceStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class CaptchaResourceConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(CaptchaResourceConfiguration.class);

    @Value("${captcha.image-dir.windows}")
    private String windowsImageDir;

    @Value("${captcha.image-dir.unix}")
    private String unixImageDir;

    /** Linux 候选回退目录（逗号分隔，来自 application.yml） */
    @Value("#{'${captcha.image-dir.fallbacks:}'.split(',')}")
    private List<String> fallbackDirs;

    @Bean
    public ResourceStore resourceStore() {
        LocalMemoryResourceStore resourceStore = new LocalMemoryResourceStore();

        String configuredDir = resolveImageDir();
        List<String> candidateDirs = buildCandidateDirs(configuredDir);

        String usedDir = null;
        List<String> imageFiles = new ArrayList<>();

        for (String dir : candidateDirs) {
            List<String> files = getImageFiles(dir);
            if (!files.isEmpty()) {
                usedDir = dir;
                imageFiles = files;
                break;
            }
            logger.debug("No captcha images in candidate directory: {}", dir);
        }

        if (usedDir == null) {
            logger.error("No captcha images found in any candidate directory: {}. " +
                    "Slider captcha will fail! Please upload jpg/png images to the configured directory.", candidateDirs);
        } else {
            logger.info("Loaded {} captcha image(s) from {}", imageFiles.size(), usedDir);
            for (String imageFile : imageFiles) {
                resourceStore.addResource(CaptchaTypeConstant.SLIDER, new Resource("file", imageFile, "default"));
            }
        }

        return resourceStore;
    }

    /**
     * 根据操作系统选择验证码图片目录
     */
    private String resolveImageDir() {
        String osName = System.getProperty("os.name", "").toLowerCase();
        if (osName.contains("win")) {
            return windowsImageDir;
        }
        return unixImageDir;
    }

    /**
     * 构建候选目录列表：优先使用配置目录，其次使用配置的回退目录
     */
    private List<String> buildCandidateDirs(String configuredDir) {
        List<String> dirs = new ArrayList<>();
        dirs.add(configuredDir);
        String osName = System.getProperty("os.name", "").toLowerCase();
        if (!osName.contains("win") && fallbackDirs != null) {
            fallbackDirs.stream()
                    .map(String::trim)
                    .filter(dir -> !dir.isEmpty())
                    .forEach(dirs::add);
        }
        return dirs;
    }

    private List<String> getImageFiles(String dirPath) {
        File dir = new File(dirPath);
        if (!dir.exists() || !dir.isDirectory()) {
            return Arrays.asList();
        }

        File[] files = dir.listFiles();
        if (files == null) {
            return Arrays.asList();
        }

        return Arrays.stream(files)
                .filter(file -> file.isFile() && isImageFile(file.getName()))
                .map(File::getAbsolutePath)
                .limit(20)
                .collect(Collectors.toList());
    }

    private boolean isImageFile(String fileName) {
        String lowerName = fileName.toLowerCase();
        return lowerName.endsWith(".jpg") || lowerName.endsWith(".jpeg") ||
               lowerName.endsWith(".png") || lowerName.endsWith(".webp");
    }
}
