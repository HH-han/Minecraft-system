package com.minecraft.controller;

import com.minecraft.dto.request.PageRequest;
import com.minecraft.dto.response.ApiResponse;
import com.minecraft.dto.response.PageResponse;
import com.minecraft.entity.News;
import com.minecraft.service.NewsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "新闻管理")
@RestController
@RequestMapping("/api/news")
public class NewsController {

    @Autowired
    private NewsService newsService;

    @Operation(summary ="获取新闻列表")
    @GetMapping("/list")
    public ApiResponse<PageResponse<?>> getNewsList(PageRequest request) {
        return ApiResponse.success(newsService.getNewsList(request));
    }

    @Operation(summary ="获取新闻详情")
    @GetMapping("/{id}")
    public ApiResponse<News> getNewsDetail(@PathVariable Long id) {
        return ApiResponse.success(newsService.getNewsDetail(id));
    }

    @Operation(summary ="获取热门新闻")
    @GetMapping("/hot")
    public ApiResponse<List<News>> getHotNews(@RequestParam(defaultValue = "10") Integer limit) {
        return ApiResponse.success(newsService.getHotNews(limit));
    }

    @Operation(summary ="添加新闻")
    @PostMapping("/add")
    public ApiResponse<Void> addNews(@RequestBody News news) {
        newsService.addNews(news);
        return ApiResponse.success("添加成功", null);
    }

    @Operation(summary ="更新新闻")
    @PutMapping("/update")
    public ApiResponse<Void> updateNews(@RequestBody News news) {
        newsService.updateNews(news);
        return ApiResponse.success("更新成功", null);
    }

    @Operation(summary ="删除新闻")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteNews(@PathVariable Long id) {
        newsService.deleteNews(id);
        return ApiResponse.success("删除成功", null);
    }
}
