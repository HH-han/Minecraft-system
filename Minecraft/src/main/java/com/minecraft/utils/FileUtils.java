package com.minecraft.utils;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

    private final String windowsUploadDir;
    private final String unixUploadDir;
    private final String windowsBaseUrl;
    private final String unixBaseUrl;

    private Path uploadPath;
    private String baseUrl;

    public FileUtils(
            @Value("${image.upload.windows-dir}") String windowsUploadDir,
            @Value("${image.upload.unix-dir}") String unixUploadDir,
            @Value("${image.upload.windows-url}") String windowsBaseUrl,
            @Value("${image.upload.unix-url}") String unixBaseUrl) {
        this.windowsUploadDir = windowsUploadDir;
        this.unixUploadDir = unixUploadDir;
        this.windowsBaseUrl = windowsBaseUrl;
        this.unixBaseUrl = unixBaseUrl;
        this.uploadPath = getUploadPath();
        this.baseUrl = getBaseUrl();
        log.info("文件上传路径初始化: {}", uploadPath);
        log.info("文件访问URL初始化: {}", baseUrl);
    }

    private Path getUploadPath() {
        String os = System.getProperty("os.name").toLowerCase();
        Path imagePath = null;

        if (os.contains("win")) {
            File dDrive = new File("D:/");
            if (dDrive.exists()) {
                imagePath = Paths.get(windowsUploadDir);
                createDirectoryIfNotExists(imagePath);
                log.info("使用目录: {}", imagePath);
            } else {
                String userHome = System.getProperty("user.home");
                imagePath = Paths.get(userHome, "images");
                createDirectoryIfNotExists(imagePath);
                log.info("创建的目录: {}", imagePath);
            }
        } else {
            imagePath = Paths.get(unixUploadDir);
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

    private String getBaseUrl() {
        String os = System.getProperty("os.name").toLowerCase();
        return os.contains("win") ? windowsBaseUrl : unixBaseUrl;
    }

    public String uploadFile(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String fileName = UUID.randomUUID().toString() + extension;

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath);

        return baseUrl + fileName;
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
            return baseUrl + fileName;
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
            String fileName = filePath;
            if (filePath.contains("/upload/")) {
                fileName = filePath.substring(filePath.lastIndexOf("/upload/") + "/upload/".length());
            } else if (filePath.contains(baseUrl)) {
                fileName = filePath.substring(filePath.lastIndexOf(baseUrl) + baseUrl.length());
            }
            Path path = uploadPath.resolve(fileName);
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
