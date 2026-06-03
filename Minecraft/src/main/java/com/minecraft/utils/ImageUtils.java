package com.minecraft.utils;

import com.minecraft.controller.UserController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.bind.DatatypeConverter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.UUID;

@Component
public class ImageUtils {
    //记录器
    static Logger logger = LoggerFactory.getLogger(UserController.class);

    private final String windowsUploadDir;
    private final String unixUploadDir;
    private final String windowsBaseUrl;
    private final String unixBaseUrl;

    public ImageUtils(
            @Value("${image.upload.windows-dir}") String windowsUploadDir,
            @Value("${image.upload.unix-dir}") String unixUploadDir,
            @Value("${image.upload.windows-url}") String windowsBaseUrl,
            @Value("${image.upload.unix-url}") String unixBaseUrl) {
        this.windowsUploadDir = windowsUploadDir;
        this.unixUploadDir = unixUploadDir;
        this.windowsBaseUrl = windowsBaseUrl;
        this.unixBaseUrl = unixBaseUrl;
    }

    private String getUploadDir() {
        return System.getProperty("os.name").toLowerCase().contains("win") ? windowsUploadDir : unixUploadDir;
    }

    private String getBaseUrl() {
        return System.getProperty("os.name").toLowerCase().contains("win") ? windowsBaseUrl : unixBaseUrl;
    }

    public void deleteImage(String imageUrl) throws Exception {
        String baseUrl = getBaseUrl();
        if (imageUrl != null && imageUrl.startsWith(baseUrl)) {
            String fileName = imageUrl.substring(baseUrl.length());
            Path imagePath = Paths.get(getUploadDir(), fileName);

            if (java.nio.file.Files.exists(imagePath)) {
                java.nio.file.Files.delete(imagePath);
            }
        }
    }
    
    /**
     * 处理 Base64 图片并返回 URL。遇到错误时抛出异常。
     */
    public String processBase64Image(String base64Image) throws Exception {
        if (base64Image == null || !base64Image.startsWith("data:image")) {
            return null;
        }

        // 解码base64图片 - 只分割第一个逗号，避免base64内容中包含逗号的问题
        int commaIndex = base64Image.indexOf(",");
        if (commaIndex < 0 || commaIndex >= base64Image.length() - 1) {
            logger.error("Base64图片格式不正确，缺少数据部分");
            throw new Exception("Base64图片格式不正确");
        }
        String dataPart = base64Image.substring(commaIndex + 1);
        byte[] imageBytes = DatatypeConverter.parseBase64Binary(dataPart);

        // 生成唯一文件名
        String fileName = UUID.randomUUID().toString() + ".png";
        String uploadDir = getUploadDir();
        Path uploadPath = Paths.get(uploadDir);

        // 确保目录存在
        if (!java.nio.file.Files.exists(uploadPath)) {
            try {
                java.nio.file.Files.createDirectories(uploadPath);
            } catch (Exception e) {
                // 如果创建目录失败，尝试使用unix目录
                if (!uploadDir.equals(unixUploadDir)) {
                    uploadPath = Paths.get(unixUploadDir);
                    java.nio.file.Files.createDirectories(uploadPath);
                } else {
                    logger.error("文件保存失败: {}", fileName, e);
                    throw e;
                }
            }
        }

        // 保存文件
        Path filePath = uploadPath.resolve(fileName);
        java.nio.file.Files.write(filePath, imageBytes);
        String baseUrl = getBaseUrl();
        logger.info("文件保存成功: {}", fileName + baseUrl);

        // 返回图片URL
        return baseUrl + fileName;
    }

    /**
     * 安全版本的 Base64 图片处理。遇到任何错误时返回 null 而不是抛出异常。
     */
    public String processBase64ImageSafe(String base64Image) {
        try {
            return processBase64Image(base64Image);
        } catch (Exception e) {
            logger.error("处理Base64图片失败: {}", e.getMessage());
            return null;
        }
    }
    
    public String processMultipartFile(MultipartFile file) throws Exception {
        // 验证文件类型
        String[] allowedTypes = {"image/jpeg", "image/png", "image/gif", "image/webp"};
        if (!Arrays.asList(allowedTypes).contains(file.getContentType())) {
            logger.error("仅支持JPEG、PNG、GIF或WEBP格式的图片");
            throw new Exception("仅支持JPEG、PNG、GIF或WEBP格式的图片");
        }

        // 验证文件大小 (限制2MB)
        long maxSize = 2 * 1024 * 1024;
        if (file.getSize() > maxSize) {
            logger.error("图片大小不能超过2MB");
            throw new Exception("图片大小不能超过2MB");
        }

        // 生成唯一文件名
        String fileName = UUID.randomUUID().toString() + "." + file.getContentType().split("/")[1];
        String uploadDir = getUploadDir();
        Path uploadPath = Paths.get(uploadDir);

        // 确保目录存在
        if (!java.nio.file.Files.exists(uploadPath)) {
            java.nio.file.Files.createDirectories(uploadPath);
        }

        // 保存文件
        Path filePath = uploadPath.resolve(fileName);
        file.transferTo(filePath.toFile());
        String baseUrl = getBaseUrl();

        // 返回图片URL
        return baseUrl + fileName;
    }
}