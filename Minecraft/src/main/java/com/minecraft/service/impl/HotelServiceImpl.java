package com.minecraft.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minecraft.dto.request.PageRequest;
import com.minecraft.dto.response.PageResponse;
import com.minecraft.entity.Hotel;
import com.minecraft.mapper.HotelMapper;
import com.minecraft.service.HotelService;
import com.minecraft.utils.ImageUtils;
import com.minecraft.vo.AttractionVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HotelServiceImpl extends ServiceImpl<HotelMapper, Hotel> implements HotelService {
    @Autowired
    private ImageUtils imageUtils;

    @Override
    public PageResponse<AttractionVO> getHotelList(PageRequest request) {
        Page<Hotel> page = new Page<>(request.getPageNum(), request.getPageSize());
        LambdaQueryWrapper<Hotel> wrapper = new LambdaQueryWrapper<>();

        if (request.getKeyword() != null) {
            wrapper.like(Hotel::getName, request.getKeyword())
                    .or()
                    .like(Hotel::getCity, request.getKeyword());
        }

        if (request.getSortBy() != null) {
            if ("rating".equals(request.getSortBy())) {
                wrapper.orderByDesc(Hotel::getRating);
            } else if ("price".equals(request.getSortBy())) {
                wrapper.orderByAsc(Hotel::getPrice);
            }
        } else {
            wrapper.orderByDesc(Hotel::getCreateTime);
        }

        Page<Hotel> result = page(page, wrapper);

        List<AttractionVO> voList = result.getRecords().stream().map(item -> {
            AttractionVO vo = new AttractionVO();
            BeanUtils.copyProperties(item, vo);
            return vo;
        }).collect(Collectors.toList());

        return new PageResponse<>(voList, result.getTotal(), request.getPageNum(), request.getPageSize());
    }

    @Override
    public Hotel getHotelDetail(Long id) {
        return getById(id);
    }

    @Override
    public List<Hotel> getRecommendHotels(String city, Integer limit) {
        LambdaQueryWrapper<Hotel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Hotel::getStatus, 1);

        if (city != null) {
            wrapper.eq(Hotel::getCity, city);
        }

        wrapper.orderByDesc(Hotel::getRating)
                .last("LIMIT " + (limit != null ? limit : 10));

        return list(wrapper);
    }

    @Override
    public void addHotel(Hotel hotel) {
        save(hotel);
    }

    @Override
    public void updateHotel(Hotel hotel) {
        updateById(hotel);
    }

    @Override
    public void deleteHotel(Long id) {
        removeById(id);
    }
    
    @Override
    public boolean save(Hotel hotel) {
        try {
            // 处理封面图片上传
            if (hotel.getCoverImage() != null && hotel.getCoverImage().startsWith("data:image")) {
                String processedCoverImage = imageUtils.processBase64Image(hotel.getCoverImage());
                hotel.setCoverImage(processedCoverImage);
            }
            
            // 处理多图片上传（如果是Base64数组）
            if (hotel.getImages() != null && hotel.getImages().startsWith("[")) {
                try {
                    // 解析图片数组
                    String[] imageArray = hotel.getImages().replace("[", "").replace("]", "").replaceAll("\\\"", "").split(",");
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
                    
                    hotel.setImages(processedImages.toString());
                } catch (Exception e) {
                    // 如果解析失败，保持原数据
                    e.printStackTrace();
                }
            }
            
            return super.save(hotel);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean updateById(Hotel hotel) {
        try {
            // 处理封面图片上传
            if (hotel.getCoverImage() != null && hotel.getCoverImage().startsWith("data:image")) {
                String processedCoverImage = imageUtils.processBase64Image(hotel.getCoverImage());
                hotel.setCoverImage(processedCoverImage);
            }
            
            // 处理多图片上传（如果是Base64数组）
            if (hotel.getImages() != null && hotel.getImages().startsWith("[")) {
                try {
                    // 解析图片数组
                    String[] imageArray = hotel.getImages().replace("[", "").replace("]", "").replaceAll("\\\"", "").split(",");
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
                    
                    hotel.setImages(processedImages.toString());
                } catch (Exception e) {
                    // 如果解析失败，保持原数据
                    e.printStackTrace();
                }
            }
            
            return super.updateById(hotel);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
