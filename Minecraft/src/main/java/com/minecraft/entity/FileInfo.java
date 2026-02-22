package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("file_info")
public class FileInfo {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String fileName;
    
    private String originalName;
    
    private String filePath;
    
    private String fileType;
    
    private Long fileSize;
    
    private Integer chunkIndex;
    
    private Integer totalChunks;
    
    private String fileMd5;
    
    private Integer status;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
