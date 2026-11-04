package com.minecraft.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class AttractionTicketVO {
    private Long id;
    /** 所属景点ID */
    private Long attractionId;
    private String name;
    private String description;
    private List<String> rules;
    private BigDecimal price;
    /** 状态：0-停售 1-在售 */
    private Integer status;
}
