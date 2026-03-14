package com.minecraft.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minecraft.dto.request.PageRequest;
import com.minecraft.dto.response.PageResponse;
import com.minecraft.entity.Product;
import com.minecraft.mapper.ProductMapper;
import com.minecraft.service.ProductService;
import com.minecraft.utils.ImageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    @Autowired
    private ImageUtils imageUtils;

    @Override
    public PageResponse<?> getProductList(PageRequest request) {
        Page<Product> page = new Page<>(request.getPageNum(), request.getPageSize());
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();

        if (request.getKeyword() != null) {
            wrapper.like(Product::getName, request.getKeyword())
                    .or()
                    .like(Product::getCity, request.getKeyword());
        }

        wrapper.orderByDesc(Product::getCreateTime);

        Page<Product> result = page(page, wrapper);
        return new PageResponse<>(result.getRecords(), result.getTotal(), request.getPageNum(), request.getPageSize());
    }

    @Override
    public Product getProductDetail(Long id) {
        return getById(id);
    }

    @Override
    public List<Product> getRecommendProducts(String city, Integer limit) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getStatus, 1);

        if (city != null) {
            wrapper.eq(Product::getCity, city);
        }

        wrapper.orderByDesc(Product::getLikeCount)
                .last("LIMIT " + (limit != null ? limit : 10));

        return list(wrapper);
    }

    @Override
    public void addProduct(Product product) {
        save(product);
    }

    @Override
    public void updateProduct(Product product) {
        updateById(product);
    }

    @Override
    public void deleteProduct(Long id) {
        removeById(id);
    }
    
    @Override
    public boolean save(Product product) {
        try {
            // 处理封面图片上传
            if (product.getCoverImage() != null && product.getCoverImage().startsWith("data:image")) {
                String processedCoverImage = imageUtils.processBase64Image(product.getCoverImage());
                product.setCoverImage(processedCoverImage);
            }
            
            // 处理多图片上传（如果是Base64数组）
            if (product.getImages() != null && product.getImages().startsWith("[")) {
                try {
                    // 解析图片数组
                    String[] imageArray = product.getImages().replace("[", "").replace("]", "").replaceAll("\\\"", "").split(",");
                    StringBuilder processedImages = new StringBuilder();
                    
                    for (String image : imageArray) {
                        if (image.trim().startsWith("data:image")) {
                            String processedImage = imageUtils.processBase64Image(image.trim());
                            processedImages.append(processedImage).append(",");
                        } else {
                            processedImages.append(image.trim()).append(",");
                        }
                    }
                    
                    if (processedImages.length() > 0) {
                        processedImages.setLength(processedImages.length() - 1);
                    }
                    
                    product.setImages(processedImages.toString());
                } catch (Exception e) {
                    // 如果解析失败，保持原数据
                    e.printStackTrace();
                }
            }
            
            return super.save(product);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean updateById(Product product) {
        try {
            // 处理封面图片上传
            if (product.getCoverImage() != null && product.getCoverImage().startsWith("data:image")) {
                String processedCoverImage = imageUtils.processBase64Image(product.getCoverImage());
                product.setCoverImage(processedCoverImage);
            }
            
            // 处理多图片上传（如果是Base64数组）
            if (product.getImages() != null && product.getImages().startsWith("[")) {
                try {
                    // 解析图片数组
                    String[] imageArray = product.getImages().replace("[", "").replace("]", "").replaceAll("\\\"", "").split(",");
                    StringBuilder processedImages = new StringBuilder();
                    
                    for (String image : imageArray) {
                        if (image.trim().startsWith("data:image")) {
                            String processedImage = imageUtils.processBase64Image(image.trim());
                            processedImages.append(processedImage).append(",");
                        } else {
                            processedImages.append(image.trim()).append(",");
                        }
                    }
                    
                    if (processedImages.length() > 0) {
                        processedImages.setLength(processedImages.length() - 1);
                    }
                    
                    product.setImages(processedImages.toString());
                } catch (Exception e) {
                    // 如果解析失败，保持原数据
                    e.printStackTrace();
                }
            }
            
            return super.updateById(product);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
