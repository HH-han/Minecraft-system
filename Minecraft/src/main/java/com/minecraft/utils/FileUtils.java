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
    /** 返回URL的服务器前缀（可为空，空则返回相对路径） */
    private final String urlBase;

    private Path uploadPath;
    private String baseUrl;

    public FileUtils(
            @Value("${image.upload.windows-dir}") String windowsUploadDir,
            @Value("${image.upload.unix-dir}") String unixUploadDir,
            @Value("${image.upload.windows-url}") String windowsBaseUrl,
            @Value("${image.upload.unix-url}") String unixBaseUrl,
            @Value("${image.upload.url-base:}") String urlBase) {
        this.windowsUploadDir = windowsUploadDir;
        this.unixUploadDir = unixUploadDir;
        this.windowsBaseUrl = windowsBaseUrl;
        this.unixBaseUrl = unixBaseUrl;
        // 去掉末尾斜杠，避免与路径前缀拼接时出现双斜杠
        this.urlBase = urlBase != null && urlBase.endsWith("/")
                ? urlBase.substring(0, urlBase.length() - 1) : urlBase;
        this.uploadPath = getUploadPath();
        this.baseUrl = getBaseUrl();
        log.info("文件上传路径初始化: {}", uploadPath);
        log.info("文件访问URL初始化: {}", baseUrl);
    }

    private Path getUploadPath() {
        String os = System.getProperty("os.name").toLowerCase();
        String dir = os.contains("win") ? windowsUploadDir : unixUploadDir;
        Path imagePath = Paths.get(dir);
        createDirectoryIfNotExists(imagePath);
        log.info("使用目录: {}", imagePath);
        return imagePath;
    }

    private String getBaseUrl() {
        String os = System.getProperty("os.name").toLowerCase();
        String path = os.contains("win") ? windowsBaseUrl : unixBaseUrl;
        return (urlBase == null ? "" : urlBase) + path;
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
            } else if (filePath.contains("/tmp/images/")) {
                // 兼容历史数据中的旧路径前缀
                fileName = filePath.substring(filePath.lastIndexOf("/tmp/images/") + "/tmp/images/".length());
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
