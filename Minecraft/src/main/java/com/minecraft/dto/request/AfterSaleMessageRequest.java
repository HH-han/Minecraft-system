package com.minecraft.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * 售后协商留言请求
 */
@Data
public class AfterSaleMessageRequest {

    /** 留言内容 */
    @NotBlank(message = "留言内容不能为空")
    @Size(max = 500, message = "留言内容不能超过500个字符")
    private String content;

    /** 附件URL列表 */
    @Size(max = 9, message = "附件数量不能超过9个")
    private List<String> evidenceUrls;
}
