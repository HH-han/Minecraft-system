package com.minecraft.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class HotelRoomVO {
    private Long id;
    /** 所属酒店ID */
    private Long hotelId;
    private String name;
    private String description;
    private List<String> facilities;
    private BigDecimal price;
    /** 状态：0-停售 1-在售 */
    private Integer status;
}
