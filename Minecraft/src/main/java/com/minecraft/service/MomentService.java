package com.minecraft.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.minecraft.dto.request.MomentCommentDTO;
import com.minecraft.dto.request.MomentPublishDTO;
import com.minecraft.dto.response.MomentCommentVO;
import com.minecraft.dto.response.MomentUserVO;
import com.minecraft.dto.response.MomentVO;
import com.minecraft.entity.Moment;

import java.util.List;
import java.util.Map;

/**
 * 朋友圈服务
 */
public interface MomentService extends IService<Moment> {

    /** 发布动态 */
    MomentVO publishMoment(Long userId, MomentPublishDTO dto);

    /** 获取朋友圈信息流（自己 + 好友） */
    List<MomentVO> getFeed(Long userId, Integer page, Integer size);

    /** 获取指定用户的动态列表 */
    List<MomentVO> getUserMoments(Long currentUserId, Long targetUserId, Integer page, Integer size);

    /** 删除动态（仅本人可删） */
    void deleteMoment(Long userId, Long momentId);

    /** 点赞/取消点赞，返回 liked 与 likeCount */
    Map<String, Object> toggleLike(Long userId, Long momentId);

    /** 获取动态的点赞用户列表 */
    List<MomentUserVO> getLikes(Long momentId);

    /** 添加评论 */
    MomentCommentVO addComment(Long userId, Long momentId, MomentCommentDTO dto);

    /** 获取动态的评论列表 */
    List<MomentCommentVO> getComments(Long momentId);

    /** 删除评论（仅本人可删） */
    void deleteComment(Long userId, Long commentId);
}
