package com.minecraft.dto.request;

import lombok.Data;

/**
 * 承运商新增/编辑请求
 */
@Data
public class CarrierSaveRequest {

    private Long id;

    private String code;

    private String name;

    private String logoUrl;

    /** kuaidi100/kuaidiniao/aliyun */
    private String apiType;

    private String apiKey;

    private String apiSecret;

    private String apiCustomer;

    private String phone;

    private String website;

    private Integer sort;

    private Integer status;
}
