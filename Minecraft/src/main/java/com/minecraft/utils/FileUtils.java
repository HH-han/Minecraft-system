package com.minecraft.utils;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@Component
public class FileUtils {

    // 暂时使用硬编码路径来测试
    private String windowsDir = "D:/Image/";
    private String unixDir = "/tmp/images/";

    private Path uploadPath;

    // 初始化上传路径
    public FileUtils() {
        this.uploadPath = getUploadPath();
        log.info("文件上传路径初始化: {}", uploadPath);
    }

    // 获取上传路径，与 WebConfig 保持一致
    private Path getUploadPath() {
        // 自动检测操作系统
        String os = System.getProperty("os.name").toLowerCase();
        Path imagePath = null;

        // Windows 系统配置
        if (os.contains("win")) {
            // 检查D盘是否存在
            File dDrive = new File("D:/");
            if (dDrive.exists()) {
                imagePath = Paths.get(windowsDir);
                createDirectoryIfNotExists(imagePath);
                log.info("使用目录: {}", imagePath);
            } else {
                // 如果D盘不存在，使用用户主目录下的images文件夹
                String userHome = System.getProperty("user.home");
                imagePath = Paths.get(userHome, "images");
                createDirectoryIfNotExists(imagePath);
                log.info("创建的目录: {}", imagePath);
            }
        }
        // Linux/Mac 系统配置
        else {
            // 使用配置文件中的Unix目录
            imagePath = Paths.get(unixDir);
            try {
                createDirectoryIfNotExists(imagePath);
                log.info("使用目录: {}", imagePath);
            } catch (Exception e) {
                log.error("无法创建目录: {}", imagePath, e);
                throw new RuntimeException("无法创建图片存储目录: " + imagePath, e);
            }
        }
        return imagePath;
    }

    public String uploadFile(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String fileName = UUID.randomUUID().toString() + extension;

        // 确保上传路径存在
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath);

        return "/upload/" + fileName;
    }

    public String uploadChunk(MultipartFile file, String fileMd5, Integer chunkIndex, Integer totalChunks) throws IOException {
        Path chunkDir = uploadPath.resolve("chunks").resolve(fileMd5);
        if (!Files.exists(chunkDir)) {
            Files.createDirectories(chunkDir);
        }

        String chunkFileName = chunkIndex + ".part";
        Path chunkPath = chunkDir.resolve(chunkFileName);
        Files.copy(file.getInputStream(), chunkPath);

        return chunkDir.toString();
    }

    public String mergeChunks(String fileMd5, String originalFilename) throws IOException {
        Path chunkDir = uploadPath.resolve("chunks").resolve(fileMd5);

        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String fileName = UUID.randomUUID().toString() + extension;
        Path targetPath = uploadPath.resolve(fileName);

        try {
            Files.createFile(targetPath);
            for (int i = 0; i < chunkDir.toFile().listFiles().length; i++) {
                Path chunkPath = chunkDir.resolve(i + ".part");
                Files.write(targetPath, Files.readAllBytes(chunkPath), java.nio.file.StandardOpenOption.APPEND);
            }

            deleteDirectory(chunkDir.toFile());
            return "/upload/" + fileName;
        } catch (IOException e) {
            throw e;
        }
    }

    private void deleteDirectory(File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    file.delete();
                }
            }
        }
        directory.delete();
    }

    public boolean deleteFile(String filePath) {
        try {
            Path path = uploadPath.resolve(filePath.replace("/upload/", ""));
            return Files.deleteIfExists(path);
        } catch (IOException e) {
            return false;
        }
    }

    private void createDirectoryIfNotExists(Path path) {
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                log.error("创建目录失败: {}", path, e);
                throw new RuntimeException("创建目录失败: " + path, e);
            }
        }
    }
}
