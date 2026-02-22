package com.minecraft.dto.request;

import lombok.Data;

import jakarta.validation.constraints.NotNull;

@Data
public class PageRequest {
    private Integer pageNum = 1;

    private Integer pageSize = 10;

    private String keyword;

    private String sortBy;

    private String sortOrder = "desc";
}
