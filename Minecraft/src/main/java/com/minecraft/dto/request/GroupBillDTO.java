package com.minecraft.dto.request;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class GroupBillDTO {
    private Long groupId;
    private BigDecimal amount;
    private String category;
    private String description;
    private LocalDate billDate;
}