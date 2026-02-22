package com.minecraft.controller;

import com.minecraft.dto.response.ApiResponse;
import com.minecraft.utils.FileUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "文件上传管理")
@RestController
@RequestMapping("/api/upload")
public class UploadController {

    @Autowired
    private FileUtils fileUtils;

    @Operation(summary ="上传文件")
    @PostMapping("/file")
    public ApiResponse<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String filePath = fileUtils.uploadFile(file);
            return ApiResponse.success("上传成功", filePath);
        } catch (Exception e) {
            return ApiResponse.error("上传失败" + e.getMessage());
        }
    }

    @Operation(summary ="上传分片")
    @PostMapping("/chunk")
    public ApiResponse<String> uploadChunk(
            @RequestParam("file") MultipartFile file,
            @RequestParam("fileMd5") String fileMd5,
            @RequestParam("chunkIndex") Integer chunkIndex,
            @RequestParam("totalChunks") Integer totalChunks) {
        try {
            String chunkPath = fileUtils.uploadChunk(file, fileMd5, chunkIndex, totalChunks);
            return ApiResponse.success("分片上传成功", chunkPath);
        } catch (Exception e) {
            return ApiResponse.error("分片上传失败" + e.getMessage());
        }
    }

    @Operation(summary ="合并分片")
    @PostMapping("/merge")
    public ApiResponse<String> mergeChunks(
            @RequestParam("fileMd5") String fileMd5,
            @RequestParam("originalName") String originalName) {
        try {
            String filePath = fileUtils.mergeChunks(fileMd5, originalName);
            return ApiResponse.success("合并成功", filePath);
        } catch (Exception e) {
            return ApiResponse.error("合并失败" + e.getMessage());
        }
    }
}
