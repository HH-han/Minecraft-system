package com.minecraft.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class AttractionTicketVO {
    private Long id;
    private String name;
    private String description;
    private List<String> rules;
    private BigDecimal price;
}
