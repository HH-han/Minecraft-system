package com.minecraft.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class HotelRoomVO {
    private Long id;
    private String name;
    private String description;
    private List<String> facilities;
    private BigDecimal price;
}
