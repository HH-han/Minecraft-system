package com.minecraft.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class MemberLocationVO {
    private Long userId;
    private String username;
    private String avatar;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Integer accuracy;
    private LocalDateTime updatedAt;
}