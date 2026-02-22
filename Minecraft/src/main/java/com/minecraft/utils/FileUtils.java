package com.minecraft.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Component
public class FileUtils {

    @Value("${file.upload.path:uploads}")
    private String uploadPath;

    public String uploadFile(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String fileName = UUID.randomUUID().toString() + extension;

        Path path = Paths.get(uploadPath);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }

        Path filePath = path.resolve(fileName);
        Files.copy(file.getInputStream(), filePath);

        return "/uploads/" + fileName;
    }

    public String uploadChunk(MultipartFile file, String fileMd5, Integer chunkIndex, Integer totalChunks) throws IOException {
        String chunkDirPath = uploadPath + "/chunks/" + fileMd5;
        Path chunkDir = Paths.get(chunkDirPath);
        if (!Files.exists(chunkDir)) {
            Files.createDirectories(chunkDir);
        }

        String chunkFileName = chunkIndex + ".part";
        Path chunkPath = chunkDir.resolve(chunkFileName);
        Files.copy(file.getInputStream(), chunkPath);

        return chunkDirPath;
    }

    public String mergeChunks(String fileMd5, String originalFilename) throws IOException {
        String chunkDirPath = uploadPath + "/chunks/" + fileMd5;
        Path chunkDir = Paths.get(chunkDirPath);

        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String fileName = UUID.randomUUID().toString() + extension;
        Path targetPath = Paths.get(uploadPath, fileName);

        try {
            Files.createFile(targetPath);
            for (int i = 0; i < chunkDir.toFile().listFiles().length; i++) {
                Path chunkPath = chunkDir.resolve(i + ".part");
                Files.write(targetPath, Files.readAllBytes(chunkPath), java.nio.file.StandardOpenOption.APPEND);
            }

            deleteDirectory(chunkDir.toFile());
            return "/uploads/" + fileName;
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
            Path path = Paths.get(uploadPath, filePath.replace("/uploads/", ""));
            return Files.deleteIfExists(path);
        } catch (IOException e) {
            return false;
        }
    }
}
