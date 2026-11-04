package com.minecraft.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("excel_task_detail")
public class ExcelTaskDetail {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String taskNo;

    private Integer rowIndex;

    private String rowData;

    private String errorField;

    private String errorMsg;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
