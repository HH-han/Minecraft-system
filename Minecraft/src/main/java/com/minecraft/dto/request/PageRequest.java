package com.minecraft.dto.request;

import lombok.Data;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@Data
public class PageRequest {

    @Min(value = 1, message = "页码不能小于1")
    private Integer pageNum = 1;

    @Min(value = 1, message = "每页数量不能小于1")
    @Max(value = 200, message = "每页数量不能超过200")
    private Integer pageSize = 10;

    private String keyword;

    private String sortBy;

    private String sortOrder = "desc";
}
