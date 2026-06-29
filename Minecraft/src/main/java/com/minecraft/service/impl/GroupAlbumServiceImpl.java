package com.minecraft.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minecraft.entity.GroupAlbum;
import com.minecraft.exception.BusinessException;
import com.minecraft.mapper.GroupAlbumMapper;
import com.minecraft.service.GroupAlbumService;
import com.minecraft.service.GroupMemberService;
import com.minecraft.utils.ImageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class GroupAlbumServiceImpl extends ServiceImpl<GroupAlbumMapper, GroupAlbum> implements GroupAlbumService {

    @Autowired
    private GroupMemberService groupMemberService;

    @Autowired
    private ImageUtils imageUtils;

    @Override
    @Transactional
    public List<GroupAlbum> uploadPhotos(Long groupId, List<MultipartFile> files, Long userId) {
        if (!groupMemberService.isMember(groupId, userId)) {
            throw new BusinessException(403, "不是群成员");
        }

        List<GroupAlbum> albums = new ArrayList<>();
        for (MultipartFile file : files) {
            String imageUrl;
            try {
                imageUrl = imageUtils.processMultipartFile(file);
            } catch (Exception e) {
                throw new BusinessException(500, "图片上传失败: " + e.getMessage());
            }
            String thumbnailUrl = imageUrl;

            GroupAlbum album = new GroupAlbum();
            album.setGroupId(groupId);
            album.setUserId(userId);
            album.setImageUrl(imageUrl);
            album.setThumbnailUrl(thumbnailUrl);
            album.setDeleted(0);
            save(album);
            albums.add(album);
        }

        return albums;
    }

    @Override
    @Transactional
    public void deletePhoto(Long photoId, Long userId) {
        GroupAlbum album = getById(photoId);
        if (album == null) {
            throw new BusinessException(404, "照片不存在");
        }
        if (!album.getUserId().equals(userId) && !groupMemberService.isAdmin(album.getGroupId(), userId)) {
            throw new BusinessException(403, "没有权限删除");
        }

        album.setDeleted(1);
        updateById(album);
    }

    @Override
    public List<GroupAlbum> getAlbumList(Long groupId) {
        return baseMapper.selectByGroupIdOrdered(groupId);
    }
}