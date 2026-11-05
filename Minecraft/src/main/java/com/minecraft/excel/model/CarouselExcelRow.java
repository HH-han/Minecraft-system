package com.minecraft.excel.model;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.minecraft.entity.Carousels;
import com.minecraft.excel.core.ExcelImportRow;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@ColumnWidth(20)
public class CarouselExcelRow extends ExcelImportRow {

    @ExcelProperty("标题")
    private String title;

    @ExcelProperty("副标题")
    private String subtitle;

    @ExcelProperty("图片地址")
    private String imageUrl;

    @ExcelProperty("移动端图片地址")
    private String mobileImageUrl;

    @ExcelProperty("缩略图地址")
    private String thumbnailUrl;

    @ExcelProperty("链接类型")
    private String linkType;

    @ExcelProperty("链接地址")
    private String linkUrl;

    @ExcelProperty("链接打开方式")
    private String linkTarget;

    @ExcelProperty("目标ID")
    private Integer targetId;

    @ExcelProperty("按钮文字")
    private String buttonText;

    @ExcelProperty("按钮颜色")
    private String buttonColor;

    @ExcelProperty("文字颜色")
    private String textColor;

    @ExcelProperty("文字阴影")
    private Boolean textShadow;

    @ExcelProperty("遮罩颜色")
    private String overlayColor;

    @ExcelProperty("遮罩透明度")
    private Double overlayOpacity;

    @ExcelProperty("展示位置")
    private String position;

    @ExcelProperty("设备类型")
    private String deviceType;

    @ExcelProperty("用户类型")
    private String userType;

    @ExcelProperty("开始时间")
    private LocalDateTime startTime;

    @ExcelProperty("结束时间")
    private LocalDateTime endTime;

    @ExcelProperty("是否常显")
    private Boolean isAlwaysShow;

    @ExcelProperty("点击数")
    private Integer clickCount;

    @ExcelProperty("曝光数")
    private Integer impressionCount;

    @ExcelProperty("排序")
    private Integer sortOrder;

    @ExcelProperty("是否启用")
    private Boolean isActive;

    @ExcelProperty("备注")
    private String remark;

    @ExcelProperty("创建人")
    private String createdBy;

    @Override
    public List<String> validate() {
        List<String> errors = new ArrayList<>();
        if (title == null || title.isBlank()) {
            errors.add("标题不能为空");
        }
        if (imageUrl == null || imageUrl.isBlank()) {
            errors.add("图片地址不能为空");
        }
        return errors;
    }

    @Override
    public Object toEntity() {
        Carousels entity = new Carousels();
        entity.setTitle(title);
        entity.setSubtitle(subtitle);
        entity.setImageUrl(imageUrl);
        entity.setMobileImageUrl(mobileImageUrl);
        entity.setThumbnailUrl(thumbnailUrl);
        entity.setLinkType(linkType);
        entity.setLinkUrl(linkUrl);
        entity.setLinkTarget(linkTarget);
        entity.setTargetId(targetId);
        entity.setButtonText(buttonText);
        entity.setButtonColor(buttonColor);
        entity.setTextColor(textColor);
        entity.setTextShadow(textShadow);
        entity.setOverlayColor(overlayColor);
        entity.setOverlayOpacity(overlayOpacity);
        entity.setPosition(position);
        entity.setDeviceType(deviceType);
        entity.setUserType(userType);
        entity.setStartTime(startTime);
        entity.setEndTime(endTime);
        entity.setIsAlwaysShow(isAlwaysShow);
        entity.setClickCount(clickCount);
        entity.setImpressionCount(impressionCount);
        entity.setSortOrder(sortOrder);
        entity.setIsActive(isActive);
        entity.setRemark(remark);
        entity.setCreatedBy(createdBy);
        return entity;
    }

    public static CarouselExcelRow from(Carousels entity) {
        CarouselExcelRow row = new CarouselExcelRow();
        row.setTitle(entity.getTitle());
        row.setSubtitle(entity.getSubtitle());
        row.setImageUrl(entity.getImageUrl());
        row.setMobileImageUrl(entity.getMobileImageUrl());
        row.setThumbnailUrl(entity.getThumbnailUrl());
        row.setLinkType(entity.getLinkType());
        row.setLinkUrl(entity.getLinkUrl());
        row.setLinkTarget(entity.getLinkTarget());
        row.setTargetId(entity.getTargetId());
        row.setButtonText(entity.getButtonText());
        row.setButtonColor(entity.getButtonColor());
        row.setTextColor(entity.getTextColor());
        row.setTextShadow(entity.getTextShadow());
        row.setOverlayColor(entity.getOverlayColor());
        row.setOverlayOpacity(entity.getOverlayOpacity());
        row.setPosition(entity.getPosition());
        row.setDeviceType(entity.getDeviceType());
        row.setUserType(entity.getUserType());
        row.setStartTime(entity.getStartTime());
        row.setEndTime(entity.getEndTime());
        row.setIsAlwaysShow(entity.getIsAlwaysShow());
        row.setClickCount(entity.getClickCount());
        row.setImpressionCount(entity.getImpressionCount());
        row.setSortOrder(entity.getSortOrder());
        row.setIsActive(entity.getIsActive());
        row.setRemark(entity.getRemark());
        row.setCreatedBy(entity.getCreatedBy());
        return row;
    }
}
