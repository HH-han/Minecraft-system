package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_announcement_target")
public class AnnouncementTarget {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long announcementId;
    private Long userId;
    private LocalDateTime createTime;
}
