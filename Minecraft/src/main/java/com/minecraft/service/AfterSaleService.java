package com.minecraft.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.minecraft.dto.request.AfterSaleApplyRequest;
import com.minecraft.dto.request.AfterSaleAuditRequest;
import com.minecraft.dto.request.AfterSaleMessageRequest;
import com.minecraft.dto.request.AfterSaleReturnRequest;
import com.minecraft.dto.request.PageRequest;
import com.minecraft.dto.response.AfterSaleVO;
import com.minecraft.dto.response.PageResponse;
import com.minecraft.entity.AfterSale;

/**
 * 售后服务
 */
public interface AfterSaleService extends IService<AfterSale> {

    /** 用户申请售后 */
    AfterSaleVO apply(Long userId, AfterSaleApplyRequest request);

    /** 我的售后列表 */
    PageResponse<?> pageByUser(Long userId, PageRequest request);

    /** 售后详情 */
    AfterSaleVO detail(Long id, Long userId);

    /** 买家填写退货物流 */
    void returnGoods(Long id, Long userId, AfterSaleReturnRequest request);

    /** 买家撤销申请 */
    void cancel(Long id, Long userId);

    /** 协商留言 */
    void message(Long id, Long userId, AfterSaleMessageRequest request);

    /** 管理端售后列表 */
    PageResponse<?> pageForAdmin(PageRequest request);

    /** 管理端审核 */
    void audit(Long id, AfterSaleAuditRequest request, Long adminId);

    /** 管理端确认收货 */
    void receive(Long id, Long adminId);

    /** 管理端关闭售后 */
    void close(Long id, String reason, Long adminId);
}
