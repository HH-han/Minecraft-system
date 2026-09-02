package com.minecraft.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.minecraft.entity.PointsProducts;
import org.springframework.web.multipart.MultipartFile;

public interface PointsProductsService extends IService<PointsProducts> {
    // 扣减库存
    boolean deductStock(Long id, Integer quantity);

    /**
     * 上传积分商品封面图片（业务校验 + 调用 ImageUtils 落盘/返回访问路径）
     *
     * @param file 前端提交的 MultipartFile，字段名 file
     * @return 可存入 imageUrl 字段的相对路径/访问 URL 字符串
     * @throws Exception 类型/大小/IO 异常均向外抛出，由 Controller 统一包装错误信息
     */
    String uploadImage(MultipartFile file) throws Exception;
}