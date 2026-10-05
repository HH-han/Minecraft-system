package com.minecraft.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * 景点门票预订请求
 */
@Data
public class TicketBookingRequest {

    @NotBlank(message = "联系人姓名不能为空")
    private String contactName;

    @NotBlank(message = "联系电话不能为空")
    private String contactPhone;

    private String contactEmail;

    private String specialRequest;

    @NotNull(message = "游玩日期不能为空")
    private LocalDate visitDate;

    @Valid
    @NotEmpty(message = "请至少选择一种门票")
    private List<TicketItem> items;

    /**
     * 门票明细项
     */
    @Data
    public static class TicketItem {

        @NotNull(message = "门票ID不能为空")
        private Long ticketId;

        @NotNull(message = "购买数量不能为空")
        @Min(value = 1, message = "购买数量至少为1")
        private Integer quantity;
    }
}
