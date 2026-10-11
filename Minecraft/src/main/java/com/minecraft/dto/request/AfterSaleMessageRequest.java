package com.minecraft.dto.request;

import lombok.Data;

import java.util.List;

/**
 * 售后协商留言请求
 */
@Data
public class AfterSaleMessageRequest {

    private String content;

    /** 附件URL列表 */
    private List<String> evidenceUrls;
}
