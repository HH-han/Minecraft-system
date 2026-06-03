package com.minecraft.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.minecraft.dto.request.PageRequest;
import com.minecraft.dto.response.PageResponse;
import com.minecraft.entity.CommunityPost;
import com.minecraft.mapper.CommunityPostMapper;
import com.minecraft.service.CollectionService;
import com.minecraft.service.CommunityService;
import com.minecraft.service.LikeService;
import com.minecraft.utils.ImageUtils;
import com.minecraft.vo.CommunityPostVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommunityServiceImpl extends ServiceImpl<CommunityPostMapper, CommunityPost> implements CommunityService {

    @Autowired
    private LikeService likeService;

    @Autowired
    private CollectionService collectionService;

    @Autowired
    private ImageUtils imageUtils;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public PageResponse<CommunityPostVO> getPostList(PageRequest request) {
        Page<CommunityPost> page = new Page<>(request.getPageNum(), request.getPageSize());
        LambdaQueryWrapper<CommunityPost> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CommunityPost::getStatus, 1)
                .orderByDesc(CommunityPost::getCreateTime);

        Page<CommunityPost> result = page(page, wrapper);

        List<CommunityPostVO> voList = result.getRecords().stream().map(item -> {
            CommunityPostVO vo = new CommunityPostVO();
            BeanUtils.copyProperties(item, vo);
            return vo;
        }).collect(Collectors.toList());

        return new PageResponse<>(voList, result.getTotal(), request.getPageNum(), request.getPageSize());
    }

    @Override
    public CommunityPostVO getPostDetail(Long id, Long userId) {
        CommunityPost post = getById(id);
        CommunityPostVO vo = new CommunityPostVO();
        BeanUtils.copyProperties(post, vo);

        if (userId != null) {
            vo.setIsLiked(likeService.isLiked("post", id, userId));
            vo.setIsCollected(collectionService.isCollected("post", id, userId));
        }

        return vo;
    }

    @Override
    public void createPost(CommunityPost post) {
        save(post);
    }

    @Override
    public void updatePost(CommunityPost post) {
        updateById(post);
    }

    @Override
    public void deletePost(Long id) {
        removeById(id);
    }

    @Override
    public boolean save(CommunityPost post) {
        try {
            processImages(post);
            return super.save(post);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateById(CommunityPost post) {
        try {
            processImages(post);
            return super.updateById(post);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 统一处理图片数据：
     * 1. 支持 JSON 数组格式 ["http://...", "data:image/png;base64,..."]
     * 2. 支持逗号分隔格式 http://...,http://...
     * 3. 将 Base64 图片转换为文件路径
     * 4. 最终以逗号分隔的 URL 字符串形式存储
     */
    private void processImages(CommunityPost post) {
        if (post.getImages() == null || post.getImages().trim().isEmpty()) {
            post.setImages(null);
            return;
        }

        String imagesStr = post.getImages().trim();
        List<String> imageList = new ArrayList<>();

        // 情况 1: JSON 数组格式
        if (imagesStr.startsWith("[")) {
            try {
                List<String> parsedList = objectMapper.readValue(imagesStr, new TypeReference<List<String>>() {});
                for (String img : parsedList) {
                    if (img != null && !img.trim().isEmpty()) {
                        imageList.add(img.trim());
                    }
                }
            } catch (Exception e) {
                // JSON 解析失败，回退到简单解析
                imageList.addAll(fallbackParse(imagesStr));
            }
        }
        // 情况 2: 逗号分隔格式（已存储的数据）
        else if (imagesStr.contains(",") || imagesStr.startsWith("http") || imagesStr.startsWith("data:image")) {
            String[] parts = imagesStr.split(",");
            for (String part : parts) {
                String trimmed = part.trim().replace("`", "").replace("\"", "");
                if (!trimmed.isEmpty()) {
                    imageList.add(trimmed);
                }
            }
        }

        // 处理每张图片（Base64 → 文件路径）
        StringBuilder result = new StringBuilder();
        for (String image : imageList) {
            String cleanImage = image.replace("`", "").replace("\"", "").trim();
            if (cleanImage.startsWith("data:image")) {
                // Base64 图片 → 转换为文件 URL
                String processedImage = imageUtils.processBase64ImageSafe(cleanImage);
                if (processedImage != null && !processedImage.isEmpty()) {
                    if (result.length() > 0) result.append(",");
                    result.append(processedImage);
                }
            } else if (!cleanImage.isEmpty()) {
                // 已有 URL，直接保留
                if (result.length() > 0) result.append(",");
                result.append(cleanImage);
            }
        }

        post.setImages(result.length() > 0 ? result.toString() : null);
    }

    /**
     * 回退解析：当 JSON 解析失败时，手动解析字符串数组
     */
    private List<String> fallbackParse(String imagesStr) {
        List<String> result = new ArrayList<>();
        String content = imagesStr;

        // 去掉外层的 [ 和 ]
        if (content.startsWith("[")) content = content.substring(1);
        if (content.endsWith("]")) content = content.substring(0, content.length() - 1);

        if (content.trim().isEmpty()) return result;

        // 按 "," 或 ", " 分割
        String[] parts;
        if (content.contains("\",\"")) {
            parts = content.split("\",\"");
        } else if (content.contains("\", \"")) {
            parts = content.split("\", \"");
        } else {
            parts = new String[]{content};
        }

        for (String part : parts) {
            String cleaned = part.trim().replace("\"", "").replace("`", "");
            if (!cleaned.isEmpty()) {
                result.add(cleaned);
            }
        }
        return result;
    }
}
