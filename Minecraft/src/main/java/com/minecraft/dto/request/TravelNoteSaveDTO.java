package com.minecraft.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 旅游笔记保存（创建/编辑）请求
 */
@Data
public class TravelNoteSaveDTO {

    /** 编辑时传 id，创建时不传 */
    private Long id;

    @NotBlank(message = "标题不能为空")
    @Size(max = 120, message = "标题最长120字")
    private String title;

    @Size(max = 300, message = "摘要最长300字")
    private String summary;

    private String coverUrl;

    private String content;

    /** 1-HTML 2-Markdown，默认1 */
    private Integer contentType = 1;

    private String destination;

    private Long continentId;

    private Long countryId;

    private Long cityId;

    private Integer travelDays;

    private LocalDate travelDate;

    private BigDecimal cost;

    /** 提交状态：0-存草稿 1-提交审核 5-私密，默认0 */
    private Integer status = 0;

    /** 标签名列表 */
    private List<String> tags;

    private List<MediaItem> mediaList;

    private List<RelationItem> relations;

    private List<RouteItem> routes;

    @Data
    public static class MediaItem {
        /** 1-图片 2-视频 */
        private Integer mediaType = 1;
        private String url;
        private String thumbUrl;
        private Integer sort = 0;
    }

    @Data
    public static class RelationItem {
        /** 1-景点 2-酒店 3-美食 4-商品 5-行程 */
        private Integer bizType;
        private Long bizId;
        private Integer sort = 0;
    }

    @Data
    public static class RouteItem {
        private Integer dayIndex;
        private String poiName;
        private BigDecimal longitude;
        private BigDecimal latitude;
        private LocalDateTime arriveTime;
        private String remark;
        private Integer sort = 0;
    }
}
