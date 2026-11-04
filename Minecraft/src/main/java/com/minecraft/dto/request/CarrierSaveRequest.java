package com.minecraft.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 承运商新增/编辑请求
 */
@Data
public class CarrierSaveRequest {

    /** 主键，为空表示新增 */
    private Long id;

    /** 承运商编码 */
    @NotBlank(message = "承运商编码不能为空")
    @Size(max = 32, message = "承运商编码不能超过32个字符")
    private String code;

    /** 承运商名称 */
    @NotBlank(message = "承运商名称不能为空")
    @Size(max = 64, message = "承运商名称不能超过64个字符")
    private String name;

    @Size(max = 500, message = "Logo地址不能超过500个字符")
    private String logoUrl;

    /** kuaidi100/kuaidiniao/aliyun */
    @Size(max = 32, message = "接口类型不能超过32个字符")
    private String apiType;

    @Size(max = 500, message = "接口密钥不能超过500个字符")
    private String apiKey;

    @Size(max = 500, message = "接口秘钥不能超过500个字符")
    private String apiSecret;

    @Size(max = 200, message = "客户编码不能超过200个字符")
    private String apiCustomer;

    @Size(max = 20, message = "联系电话不能超过20个字符")
    private String phone;

    @Size(max = 200, message = "官网地址不能超过200个字符")
    private String website;

    @Min(value = 0, message = "排序值不能小于0")
    private Integer sort;

    /** 状态：0-禁用 1-启用 */
    @Min(value = 0, message = "状态值不合法")
    @Max(value = 1, message = "状态值不合法")
    private Integer status;
}
