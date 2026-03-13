package com.minecraft.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minecraft.dto.request.PageRequest;
import com.minecraft.dto.response.PageResponse;
import com.minecraft.entity.GroupTravel;
import com.minecraft.mapper.GroupTravelMapper;
import com.minecraft.service.GroupTravelService;
import com.minecraft.utils.ImageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GroupTravelServiceImpl extends ServiceImpl<GroupTravelMapper, GroupTravel> implements GroupTravelService {

    @Autowired
    private ImageUtils imageUtils;

    @Override
    public PageResponse<?> getGroupList(PageRequest request) {
        Page<GroupTravel> page = new Page<>(request.getPageNum(), request.getPageSize());
        LambdaQueryWrapper<GroupTravel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GroupTravel::getStatus, 1)
                .orderByDesc(GroupTravel::getCreateTime);

        Page<GroupTravel> result = page(page, wrapper);
        return new PageResponse<>(result.getRecords(), result.getTotal(), request.getPageNum(), request.getPageSize());
    }

    @Override
    public GroupTravel getGroupDetail(Long id) {
        return getById(id);
    }

    @Override
    public void createGroup(GroupTravel group) {
        save(group);
    }

    @Override
    public void joinGroup(Long groupId, Long userId) {
        GroupTravel group = getById(groupId);
        if (group.getCurrentMembers() >= group.getMaxMembers()) {
            throw new RuntimeException("团队已满员");
        }
        group.setCurrentMembers(group.getCurrentMembers() + 1);
        updateById(group);
    }

    @Override
    public void leaveGroup(Long groupId, Long userId) {
        GroupTravel group = getById(groupId);
        group.setCurrentMembers(group.getCurrentMembers() - 1);
        updateById(group);
    }

    @Override
    public void updateGroup(GroupTravel group) {
        updateById(group);
    }

    @Override
    public void deleteGroup(Long id) {
        removeById(id);
    }

    @Override
    public boolean save(GroupTravel group) {
        try {
            // 处理多图片上传（如果是Base64数组）
            if (group.getImages() != null && group.getImages().startsWith("[")) {
                try {
                    // 解析图片数组
                    String[] imageArray = group.getImages().replace("[", "").replace("]", "").replaceAll("\\"", "").split(",");
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
                    
                    group.setImages(processedImages.toString());
                } catch (Exception e) {
                    // 如果解析失败，保持原数据
                    e.printStackTrace();
                }
            }
            
            return super.save(group);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean updateById(GroupTravel group) {
        try {
            // 处理多图片上传（如果是Base64数组）
            if (group.getImages() != null && group.getImages().startsWith("[")) {
                try {
                    // 解析图片数组
                    String[] imageArray = group.getImages().replace("[", "").replace("]", "").replaceAll("\\"", "").split(",");
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
                    
                    group.setImages(processedImages.toString());
                } catch (Exception e) {
                    // 如果解析失败，保持原数据
                    e.printStackTrace();
                }
            }
            
            return super.updateById(group);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
