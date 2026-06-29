package com.minecraft.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.minecraft.entity.GroupAlbum;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface GroupAlbumService extends IService<GroupAlbum> {
    List<GroupAlbum> uploadPhotos(Long groupId, List<MultipartFile> files, Long userId);
    void deletePhoto(Long photoId, Long userId);
    List<GroupAlbum> getAlbumList(Long groupId);
}