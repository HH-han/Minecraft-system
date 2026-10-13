package com.minecraft.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.minecraft.dto.request.PageRequest;
import com.minecraft.dto.request.TravelNoteQueryDTO;
import com.minecraft.dto.request.TravelNoteSaveDTO;
import com.minecraft.dto.response.PageResponse;
import com.minecraft.entity.TravelNote;
import com.minecraft.entity.TravelNoteTag;
import com.minecraft.vo.TravelNoteDetailVO;
import com.minecraft.vo.TravelNoteReportVO;
import com.minecraft.vo.TravelNoteStatVO;
import com.minecraft.vo.TravelNoteVO;

import java.util.List;

/**
 * 旅游笔记服务
 */
public interface TravelNoteService extends IService<TravelNote> {

    /** 创建/保存草稿/提交审核（编辑时 dto.id 不为空） */
    Long saveNote(Long userId, TravelNoteSaveDTO dto);

    /** 删除笔记（作者本人） */
    void deleteNote(Long id, Long userId);

    /** 笔记详情（含结构化数据与互动状态），同时记录浏览 */
    TravelNoteDetailVO getDetail(Long id, Long viewerUserId, String ip, boolean ignoreStatus);

    /** 广场分页：tab=recommend/latest/follow/city */
    PageResponse<TravelNoteVO> pageNotes(TravelNoteQueryDTO query, Long userId);

    /** 我的笔记（可按状态筛选，null=全部） */
    PageResponse<TravelNoteVO> myNotes(Long userId, Integer status, PageRequest request);

    /** 一键复制为出行计划，返回 planId */
    Long copyToPlan(Long noteId, Long userId);

    /** 我的举报记录分页 */
    PageResponse<TravelNoteReportVO> myReports(Long userId, PageRequest request);

    // ==================== 管理端 ====================

    PageResponse<TravelNoteVO> adminPage(Integer status, String keyword, PageRequest request);

    /** 审核：通过→已发布，驳回→已驳回 */
    void audit(Long id, Boolean approved, String reason);

    void toggleTop(Long id);

    void toggleFeatured(Long id);

    void offline(Long id);

    TravelNoteStatVO statOverview();

    PageResponse<TravelNoteReportVO> reportPage(Integer status, PageRequest request);

    /** 处理举报：1-处理(下架笔记) 2-驳回举报 */
    void handleReport(Long reportId, Integer status, Long adminId);

    // ==================== 标签 ====================

    PageResponse<TravelNoteTag> tagPage(String keyword, PageRequest request);

    void saveTag(TravelNoteTag tag);

    void deleteTag(Long id);

    List<TravelNoteTag> hotTags(Integer limit);
}
