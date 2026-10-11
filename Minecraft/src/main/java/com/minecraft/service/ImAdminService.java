package com.minecraft.service;

import com.minecraft.dto.response.FavoriteVO;
import com.minecraft.dto.response.ImCallRecordVO;
import com.minecraft.dto.response.ImFriendVO;
import com.minecraft.dto.response.ImGroupMemberVO;
import com.minecraft.dto.response.ImGroupMessageVO;
import com.minecraft.dto.response.ImGroupVO;
import com.minecraft.dto.response.ImSingleMessageVO;
import com.minecraft.dto.response.MomentCommentVO;
import com.minecraft.dto.response.MomentUserVO;
import com.minecraft.dto.response.MomentVO;
import com.minecraft.dto.response.PageResponse;
import com.minecraft.entity.ChatGroup;
import com.minecraft.entity.Emoji;

import java.util.List;

/**
 * 即时通讯管理端服务（好友/群组/消息/通话/表情）
 */
public interface ImAdminService {

    // ---------------- 好友关系 ----------------

    PageResponse<ImFriendVO> pageFriends(Integer page, Integer size, String keyword, Integer status);

    void deleteFriendRelation(Long id);

    void approveFriendRequest(Long id);

    void rejectFriendRequest(Long id);

    // ---------------- 聊天群组 ----------------

    PageResponse<ImGroupVO> pageGroups(Integer page, Integer size, String keyword);

    List<ImGroupMemberVO> listGroupMembers(Long groupId);

    void updateGroup(Long id, ChatGroup group);

    void deleteGroup(Long id);

    void removeGroupMember(Long groupId, Long userId);

    // ---------------- 聊天消息 ----------------

    PageResponse<ImSingleMessageVO> pageSingleMessages(Integer page, Integer size, Long userId, String keyword, String messageType);

    PageResponse<ImGroupMessageVO> pageGroupMessages(Integer page, Integer size, Long groupId, String keyword);

    // ---------------- 通话记录 ----------------

    PageResponse<ImCallRecordVO> pageCallRecords(Integer page, Integer size, String keyword, String callType);

    void deleteCallRecord(Long id);

    // ---------------- 系统表情 ----------------

    PageResponse<Emoji> pageEmojis(Integer page, Integer size, String keyword, String category, Integer isActive);

    Long createEmoji(Emoji emoji);

    void updateEmoji(Long id, Emoji emoji);

    void deleteEmoji(Long id);

    // ---------------- 用户所在群 ----------------

    List<ImGroupVO> listUserGroups(Long userId);

    // ---------------- 朋友圈 ----------------

    PageResponse<MomentVO> pageMoments(Integer page, Integer size, Long userId, String keyword, Integer visibility);

    List<MomentCommentVO> listMomentComments(Long momentId);

    List<MomentUserVO> listMomentLikes(Long momentId);

    void deleteMoment(Long momentId);

    // ---------------- 收藏 ----------------

    PageResponse<FavoriteVO> pageFavorites(Integer page, Integer size, Long userId, String type, String tag, String keyword);

    void deleteFavorite(Long id);
}
