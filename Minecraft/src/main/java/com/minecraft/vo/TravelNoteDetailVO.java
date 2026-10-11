package com.minecraft.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 旅游笔记详情 VO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TravelNoteDetailVO extends TravelNoteVO {
    private String content;
    private Integer contentType;
    private Long continentId;
    private Long countryId;
    private Long cityId;
    private String cityName;
    private LocalDate travelDate;
    private List<TravelNoteMediaVO> mediaList;
    private List<TravelNoteRelationVO> relations;
    private List<TravelNoteRouteVO> routes;

    @Data
    public static class TravelNoteMediaVO {
        private Long id;
        private Integer mediaType;
        private String url;
        private String thumbUrl;
        private Integer sort;
    }

    @Data
    public static class TravelNoteRelationVO {
        private Long id;
        private Integer bizType;
        private Long bizId;
        private String bizName;
        private String bizCover;
        private Integer sort;
    }

    @Data
    public static class TravelNoteRouteVO {
        private Long id;
        private Integer dayIndex;
        private String poiName;
        private BigDecimal longitude;
        private BigDecimal latitude;
        private String arriveTime;
        private String remark;
        private Integer sort;
    }
}
