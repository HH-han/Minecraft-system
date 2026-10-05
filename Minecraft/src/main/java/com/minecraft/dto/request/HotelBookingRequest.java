package com.minecraft.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

/**
 * 酒店预订请求
 */
@Data
public class HotelBookingRequest {

    @NotBlank(message = "联系人姓名不能为空")
    private String contactName;

    @NotBlank(message = "联系电话不能为空")
    private String contactPhone;

    private String contactEmail;

    private String specialRequest;

    @NotNull(message = "入住日期不能为空")
    private LocalDate checkInDate;

    @NotNull(message = "退房日期不能为空")
    private LocalDate checkOutDate;

    @NotNull(message = "入住人数不能为空")
    @Min(value = 1, message = "入住人数至少为1")
    @Max(value = 10, message = "入住人数最多为10")
    private Integer guests;

    @NotNull(message = "房间ID不能为空")
    private Long roomId;

    /** 房间数量，不传默认1间 */
    @Min(value = 1, message = "房间数量至少为1")
    private Integer quantity;
}
