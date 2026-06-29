package com.minecraft.controller;

import com.minecraft.dto.response.ApiResponse;
import com.minecraft.entity.GroupAlbum;
import com.minecraft.service.GroupAlbumService;
import com.minecraft.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "群组相册")
@RestController
@RequestMapping("/api/group/{groupId}/album")
public class GroupAlbumController {

    @Autowired
    private GroupAlbumService groupAlbumService;

    @Operation(summary = "批量上传照片")
    @PostMapping("/upload")
    public ApiResponse<List<GroupAlbum>> uploadPhotos(@PathVariable Long groupId, @RequestParam List<MultipartFile> files) {
        Long userId = SecurityUtils.getCurrentUserId();
        List<GroupAlbum> albums = groupAlbumService.uploadPhotos(groupId, files, userId);
        return ApiResponse.success("上传成功", albums);
    }

    @Operation(summary = "删除照片")
    @DeleteMapping("/{photoId}")
    public ApiResponse<Void> deletePhoto(@PathVariable Long photoId) {
        Long userId = SecurityUtils.getCurrentUserId();
        groupAlbumService.deletePhoto(photoId, userId);
        return ApiResponse.success("删除成功", null);
    }

    @Operation(summary = "获取相册列表")
    @GetMapping("/list")
    public ApiResponse<List<GroupAlbum>> getAlbumList(@PathVariable Long groupId) {
        List<GroupAlbum> albums = groupAlbumService.getAlbumList(groupId);
        return ApiResponse.success(albums);
    }
}