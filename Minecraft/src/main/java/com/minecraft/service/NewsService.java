package com.minecraft.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.minecraft.dto.request.PageRequest;
import com.minecraft.dto.response.PageResponse;
import com.minecraft.entity.News;

import java.util.List;

public interface NewsService extends IService<News> {
    PageResponse<?> getNewsList(PageRequest request);
    News getNewsDetail(Long id);
    List<News> getHotNews(Integer limit);
    void addNews(News news);
    void updateNews(News news);
    void deleteNews(Long id);
}
