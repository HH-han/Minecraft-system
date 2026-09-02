package com.minecraft.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minecraft.entity.PointsProducts;
import com.minecraft.mapper.PointsProductsMapper;
import com.minecraft.service.PointsProductsService;
import com.minecraft.utils.ImageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@Service
public class PointsProductsServiceImpl extends ServiceImpl<PointsProductsMapper, PointsProducts> implements PointsProductsService {

    @Autowired
    private ImageUtils imageUtils;

    /** 积分商品封面允许的图片 Content-Type（业务侧白名单，ImageUtils 内部已做二次校验） */
    private static final Set<String> ALLOWED_IMAGE_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/gif",
            "image/webp"
    );

    /** 积分商品封面大小上限 2MB */
    private static final long MAX_IMAGE_SIZE = 2L * 1024 * 1024;

    @Override
    public boolean deductStock(Long id, Integer quantity) {
        int result = baseMapper.deductStock(id, quantity);
        return result > 0;
    }

    @Override
    public String uploadImage(MultipartFile file) throws Exception {
        // ========== 业务参数校验 ==========
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("上传文件不能为空");
        }
        final String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType.toLowerCase())) {
            throw new IllegalArgumentException("仅支持 JPG / PNG / GIF / WEBP 格式的图片");
        }
        if (file.getSize() > MAX_IMAGE_SIZE) {
            throw new IllegalArgumentException("图片大小不能超过 2MB");
        }
        // ========== 落盘 & 生成访问路径（复用项目通用 ImageUtils） ==========
        return imageUtils.processMultipartFile(file);
    }
}