package com.minecraft.controller;

import com.minecraft.dto.request.FavoriteSaveDTO;
import com.minecraft.dto.response.ApiResponse;
import com.minecraft.dto.response.FavoriteStatVO;
import com.minecraft.dto.response.FavoriteVO;
import com.minecraft.service.FavoriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 收藏（微信风格）
 */
@Tag(name = "收藏")
@RestController
@RequestMapping("/api/public/favorite")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    @Operation(summary = "新增收藏")
    @PostMapping("/add")
    public ApiResponse<FavoriteVO> add(@RequestBody FavoriteSaveDTO dto) {
        return ApiResponse.success("收藏成功", favoriteService.save(dto.getUserId(), dto));
    }

    @Operation(summary = "编辑收藏")
    @PutMapping("/{id}")
    public ApiResponse<FavoriteVO> update(@PathVariable Long id, @RequestBody FavoriteSaveDTO dto) {
        dto.setId(id);
        return ApiResponse.success("保存成功", favoriteService.save(dto.getUserId(), dto));
    }

    @Operation(summary = "分页查询收藏")
    @GetMapping("/list")
    public ApiResponse<List<FavoriteVO>> list(
            @RequestParam Long userId,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String tag,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        return ApiResponse.success("成功", favoriteService.list(userId, type, tag, keyword, page, size));
    }

    @Operation(summary = "最近使用的收藏")
    @GetMapping("/recent")
    public ApiResponse<List<FavoriteVO>> recent(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "20") Integer limit) {
        return ApiResponse.success("成功", favoriteService.recent(userId, limit));
    }

    @Operation(summary = "分类统计")
    @GetMapping("/stats")
    public ApiResponse<FavoriteStatVO> stats(@RequestParam Long userId) {
        return ApiResponse.success("成功", favoriteService.stats(userId));
    }

    @Operation(summary = "标签列表")
    @GetMapping("/tags")
    public ApiResponse<List<String>> tags(@RequestParam Long userId) {
        return ApiResponse.success("成功", favoriteService.tags(userId));
    }

    @Operation(summary = "收藏详情")
    @GetMapping("/{id}")
    public ApiResponse<FavoriteVO> detail(@PathVariable Long id, @RequestParam Long userId) {
        return ApiResponse.success("成功", favoriteService.detail(userId, id));
    }

    @Operation(summary = "更新最近使用时间")
    @PostMapping("/{id}/touch")
    public ApiResponse<Void> touch(@PathVariable Long id, @RequestParam Long userId) {
        favoriteService.touch(userId, id);
        return ApiResponse.success("成功", null);
    }

    @Operation(summary = "删除收藏")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id, @RequestParam Long userId) {
        favoriteService.delete(userId, id);
        return ApiResponse.success("删除成功", null);
    }

    @Operation(summary = "清空收藏")
    @DeleteMapping("/clear")
    public ApiResponse<Void> clear(
            @RequestParam Long userId,
            @RequestParam(required = false) String type) {
        favoriteService.clear(userId, type);
        return ApiResponse.success("已清空", null);
    }
}
