package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("excel_task")
public class ExcelTask {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String taskNo;

    private String bizType;

    private Integer taskType;

    private String fileName;

    private String filePath;

    private Long fileSize;

    private Integer totalRows;

    private Integer successRows;

    private Integer failRows;

    private Integer status;

    private String errorFile;

    private String errorMsg;

    private String params;

    private Long operatorId;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
