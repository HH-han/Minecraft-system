package com.minecraft.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class GroupUpdateDTO {

    private Long id;

    @Size(max = 50, message = "旅行团名称不能超过50个字符")
    private String name;

    @Size(max = 500, message = "封面地址不能超过500个字符")
    private String coverUrl;

    @Size(max = 100, message = "目的地不能超过100个字符")
    private String destination;

    private LocalDate startDate;

    private LocalDate endDate;

    @Size(max = 1000, message = "旅行团简介不能超过1000个字符")
    private String description;

    @Min(value = 2, message = "最大成员数不能小于2")
    private Integer maxMembers;

    private Boolean isPublic;
}
