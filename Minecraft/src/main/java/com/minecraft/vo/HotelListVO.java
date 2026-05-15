package com.minecraft.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class HotelListVO {
    private Long id;
    private String name;
    private String city;
    private String province;
    private String address;
    private String description;
    private String coverImage;
    private List<String> images;
    private BigDecimal price;
    private Integer rating;
    private Integer commentCount;
    private LocalDateTime createTime;
    
    private List<HotelRoomVO> rooms;
    private List<String> facilities;
}
