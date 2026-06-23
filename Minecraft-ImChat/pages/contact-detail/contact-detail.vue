<template>
  <view class="contact-detail-page">
    <view class="nav-bar">
      <view class="nav-back" @click="goBack">
        <text class="back-icon">‹</text>
      </view>
      <view class="nav-title">资料</view>
      <view class="nav-more" @click="showMore">
        <text class="more-icon">···</text>
      </view>
    </view>

    <scroll-view class="detail-scroll" scroll-y>
      <view class="profile-card fade-in">
        <image
          class="profile-avatar"
          :src="contactInfo.avatar || defaultAvatar"
          mode="aspectFill"
          @click="previewAvatar"
        />
        <view class="profile-info">
          <view class="profile-name-row">
            <text class="profile-name">{{ contactInfo.name || '未知用户' }}</text>
            <text v-if="contactInfo.gender" class="gender-badge" :class="contactInfo.gender">
              {{ contactInfo.gender === 'male' ? '♂' : contactInfo.gender === 'female' ? '♀' : '' }}
            </text>
          </view>
          <text class="profile-line" v-if="contactInfo.username">
            <text class="line-label">昵称：</text>
            <text class="line-value">{{ contactInfo.username }}</text>
          </text>
          <text class="profile-line" v-if="contactInfo.account">
            <text class="line-label">微信号：</text>
            <text class="line-value">{{ contactInfo.account }}</text>
          </text>
          <text class="profile-line" v-if="contactInfo.region">
            <text class="line-label">地区：</text>
            <text class="line-value">{{ contactInfo.region }}</text>
          </text>
        </view>
        <view class="profile-actions">
          <view class="star-btn" :class="{ active: isStarred }" @click="toggleStar">
            <text class="star-icon">{{ isStarred ? '★' : '☆' }}</text>
          </view>
        </view>
      </view>

      <view class="section-card fade-in" style="animation-delay: 0.1s;">
        <view class="section-header" @click="showMoreInfo">
          <text class="section-title">朋友资料</text>
          <text class="section-arrow">›</text>
        </view>
        <view class="section-body">
          <view class="info-row" v-if="contactInfo.phone">
            <text class="info-label">电话</text>
            <text class="info-value">{{ contactInfo.phone }}</text>
          </view>
          <view class="info-row" v-if="tagsText">
            <text class="info-label">标签</text>
            <text class="info-value">{{ tagsText }}</text>
          </view>
          <view class="info-row" v-if="contactInfo.memo">
            <text class="info-label">备忘</text>
            <text class="info-value ellipsis">{{ contactInfo.memo }}</text>
          </view>
          <view class="info-row">
            <text class="info-label">照片</text>
            <text class="info-value">{{ photoCount }}张照片</text>
          </view>
        </view>
      </view>

      <view class="section-card fade-in" style="animation-delay: 0.2s;" @click="openMoments">
        <view class="section-row single">
          <text class="section-row-text">朋友圈</text>
          <text class="section-arrow">›</text>
        </view>
      </view>

      <view class="action-bar fade-in" style="animation-delay: 0.3s;">
        <view class="action-btn primary" @click="sendMessage">
          <view class="action-icon-wrap">
            <text class="action-icon-text">💬</text>
          </view>
          <text class="action-text">发消息</text>
        </view>
        <view class="action-btn secondary" @click="makeCall">
          <view class="action-icon-wrap">
            <text class="action-icon-text">📞</text>
          </view>
          <text class="action-text">音视频通话</text>
        </view>
      </view>

      <view class="footer-section fade-in" style="animation-delay: 0.4s;">
        <view
          v-if="contactType === 'single'"
          class="footer-btn danger"
          @click="deleteContact"
        >
          <text>删除好友</text>
        </view>
        <view
          v-if="contactType === 'group' && contactInfo.isCreator"
          class="footer-btn danger"
          @click="deleteGroup"
        >
          <text>解散群聊</text>
        </view>
        <view
          v-if="contactType === 'group' && !contactInfo.isCreator"
          class="footer-btn danger"
          @click="leaveGroup"
        >
          <text>退出群聊</text>
        </view>
      </view>
    </scroll-view>
  </view>
</template>

<script>
import {
  getGroupById,
  getGroupMembers,
  deleteFriend,
  deleteGroup,
  removeGroupMember,
  getUserByAccount
} from '../../utils/chat-api.js'
import { getUserInfo } from '../../utils/storage.js'

export default {
  data() {
    return {
      contactType: 'single',
      contactId: '',
      contactInfo: {
        id: '',
        name: '',
        avatar: '',
        phone: '',
        remark: '',
        description: '',
        username: '',
        account: '',
        region: '',
        gender: '',
        memo: '',
        tags: [],
        isStarred: false,
        isCreator: false
      },
      members: [],
      memberCount: 0,
      photoCount: 0,
      isStarred: false,
      defaultAvatar: '/static/default-avatar.png'
    }
  },

  computed: {
    tagsText() {
      if (Array.isArray(this.contactInfo.tags) && this.contactInfo.tags.length) {
        return this.contactInfo.tags.join('，')
      }
      if (typeof this.contactInfo.tags === 'string' && this.contactInfo.tags.trim()) {
        return this.contactInfo.tags
      }
      return ''
    }
  },

  onLoad(options) {
    this.contactType = options.type || 'single'
    this.contactId = options.id
    this.contactInfo = {
      id: options.id,
      name: decodeURIComponent(options.name || ''),
      avatar: decodeURIComponent(options.avatar || ''),
      username: decodeURIComponent(options.username || ''),
      account: decodeURIComponent(options.account || ''),
      region: decodeURIComponent(options.region || ''),
      phone: decodeURIComponent(options.phone || ''),
      gender: options.gender || '',
      memo: decodeURIComponent(options.memo || ''),
      remark: decodeURIComponent(options.remark || ''),
      isCreator: options.isCreator === 'true'
    }

    if (options.tags) {
      try {
        this.contactInfo.tags = JSON.parse(decodeURIComponent(options.tags))
      } catch (e) {
        this.contactInfo.tags = []
      }
    }

    this.isStarred = options.starred === 'true'

    if (this.contactType === 'group') {
      this.loadGroupInfo()
      this.loadGroupMembers()
    } else {
      this.loadFriendDetail()
    }
  },

  methods: {
    async loadFriendDetail() {
      if (!this.contactId) return
      const account = this.contactInfo.account
      if (!account) {
        return
      }
      try {
        uni.showLoading({ title: '加载中...', mask: true })
        const res = await getUserByAccount(account)
        uni.hideLoading()
        if (res && res.code === 200 && res.data) {
          this.contactInfo = {
            ...this.contactInfo,
            ...res.data,
            id: this.contactInfo.id || res.data.id
          }
        }
      } catch (e) {
        uni.hideLoading()
        console.error('加载好友详情失败:', e)
      }
    },

    async loadGroupInfo() {
      try {
        const res = await getGroupById(this.contactId)
        if (res.code === 200 && res.data) {
          this.contactInfo.description = res.data.description || ''
          this.photoCount = res.data.photoCount || 0
        }
      } catch (e) {
        console.error('加载群信息失败:', e)
      }
    },

    async loadGroupMembers() {
      try {
        const res = await getGroupMembers(this.contactId)
        if (res.code === 200 && res.data) {
          this.members = res.data
          this.memberCount = res.data.length
        }
      } catch (e) {
        console.error('加载群成员失败:', e)
      }
    },

    formatPhone(phone) {
      if (!phone) return ''
      return phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2')
    },

    goBack() {
      uni.navigateBack()
    },

    showMore() {
      uni.showActionSheet({
        itemList: ['投诉', '拉入黑名单', '添加到桌面'],
        success: (res) => {
          uni.showToast({ title: '功能开发中', icon: 'none' })
        }
      })
    },

    showMoreInfo() {
      uni.showToast({ title: '更多资料开发中', icon: 'none' })
    },

    previewAvatar() {
      if (!this.contactInfo.avatar) return
      uni.previewImage({
        urls: [this.contactInfo.avatar],
        current: this.contactInfo.avatar
      })
    },

    toggleStar() {
      this.isStarred = !this.isStarred
      uni.showToast({
        title: this.isStarred ? '已设为星标朋友' : '已取消星标',
        icon: 'none'
      })
    },

    openMoments() {
      uni.showToast({ title: '朋友圈功能开发中', icon: 'none' })
    },

    sendMessage() {
      const targetId = this.contactId
      const targetName = this.contactInfo.name || ''
      const targetAvatar = this.contactInfo.avatar || ''
      const pages = getCurrentPages()
      const chatPage = pages.find(p => p.route && p.route.includes('pages/chat/chat'))
      if (chatPage) {
        uni.navigateBack({ delta: pages.length - pages.indexOf(chatPage) - 1 })
        return
      }
      uni.navigateTo({
        url: `/pages/chat/chat?type=${this.contactType}&id=${targetId}&name=${encodeURIComponent(targetName)}&avatar=${encodeURIComponent(targetAvatar)}`
      })
    },

    makeCall() {
      uni.showToast({ title: '功能开发中', icon: 'none' })
    },

    async deleteContact() {
      const userInfo = getUserInfo()
      if (!userInfo) return

      uni.showModal({
        title: '提示',
        content: '确定要删除该好友吗？',
        success: async (res) => {
          if (res.confirm) {
            try {
              const result = await deleteFriend(userInfo.id, this.contactId)
              if (result.code === 200) {
                uni.showToast({ title: '已删除', icon: 'success' })
                setTimeout(() => {
                  uni.navigateBack()
                }, 1500)
              } else {
                uni.showToast({ title: result.message || '删除失败', icon: 'none' })
              }
            } catch (e) {
              console.error('删除好友失败:', e)
              uni.showToast({ title: '删除失败', icon: 'none' })
            }
          }
        }
      })
    },

    deleteGroup() {
      uni.showModal({
        title: '提示',
        content: '确定要解散该群聊吗？',
        success: async (res) => {
          if (res.confirm) {
            try {
              const result = await deleteGroup(this.contactId)
              if (result.code === 200) {
                uni.showToast({ title: '已解散', icon: 'success' })
                setTimeout(() => {
                  uni.switchTab({ url: '/pages/index/index' })
                }, 1500)
              } else {
                uni.showToast({ title: result.message || '解散失败', icon: 'none' })
              }
            } catch (e) {
              console.error('解散群聊失败:', e)
              uni.showToast({ title: '解散失败', icon: 'none' })
            }
          }
        }
      })
    },

    leaveGroup() {
      const userInfo = getUserInfo()
      if (!userInfo) return

      uni.showModal({
        title: '提示',
        content: '确定要退出该群聊吗？',
        success: async (res) => {
          if (res.confirm) {
            try {
              const result = await removeGroupMember(this.contactId, userInfo.id)
              if (result.code === 200) {
                uni.showToast({ title: '已退出', icon: 'success' })
                setTimeout(() => {
                  uni.switchTab({ url: '/pages/index/index' })
                }, 1500)
              } else {
                uni.showToast({ title: result.message || '退出失败', icon: 'none' })
              }
            } catch (e) {
              console.error('退出群聊失败:', e)
              uni.showToast({ title: '退出失败', icon: 'none' })
            }
          }
        }
      })
    }
  }
}
</script>

<style scoped>
.contact-detail-page {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background-color: #ededed;
  padding-bottom: calc(40rpx + env(safe-area-inset-bottom));
}

/* 顶部导航栏 */
.nav-bar {
  position: sticky;
  top: 0;
  z-index: 10;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20rpx 24rpx;
  padding-top: calc(20rpx + env(safe-area-inset-top));
  background: linear-gradient(180deg, #f8f8f8 0%, #f0f0f0 100%);
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.04);
}

.nav-back {
  width: 64rpx;
  height: 64rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  transition: background-color 0.2s ease;
}

.nav-back:active {
  background-color: rgba(0, 0, 0, 0.05);
}

.back-icon {
  font-size: 56rpx;
  color: #1d1d1f;
  font-weight: 300;
  line-height: 1;
}

.nav-more {
  width: 64rpx;
  height: 64rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  transition: background-color 0.2s ease;
}

.nav-more:active {
  background-color: rgba(0, 0, 0, 0.05);
}

.more-icon {
  font-size: 40rpx;
  color: #1d1d1f;
  font-weight: 600;
  letter-spacing: 2rpx;
}

.nav-title {
  font-size: 34rpx;
  color: #1d1d1f;
  font-weight: 600;
  letter-spacing: -0.3rpx;
}

/* 滚动容器 */
.detail-scroll {
  flex: 1;
  height: 0;
}

/* 用户信息卡片 */
.profile-card {
  display: flex;
  flex-direction: row;
  align-items: center;
  padding: 32rpx 28rpx;
  background-color: #ffffff;
  margin: 0 0 20rpx;
  border-radius: 20rpx;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.06);
  transition: all 0.3s ease;
}

.profile-avatar {
  width: 140rpx;
  height: 140rpx;
  border-radius: 16rpx;
  background-color: #f5f5f7;
  flex-shrink: 0;
  margin-right: 28rpx;
  transition: all 0.3s ease;
  border: 4rpx solid #f0f0f0;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.1);
}

.profile-avatar:active {
  opacity: 0.8;
  transform: scale(0.96);
}

.profile-info {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 4rpx;
}

.profile-name-row {
  display: flex;
  align-items: center;
  margin-bottom: 6rpx;
  gap: 8rpx;
}

.profile-name {
  font-size: 36rpx;
  color: #1d1d1f;
  font-weight: 700;
  letter-spacing: -0.4rpx;
  line-height: 1.2;
  max-width: 360rpx;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.gender-badge {
  font-size: 28rpx;
  font-weight: 600;
  line-height: 1;
}

.gender-badge.male {
  color: #4a90e2;
}

.gender-badge.female {
  color: #f55a8a;
}

.profile-line {
  font-size: 24rpx;
  color: #888888;
  line-height: 1.5;
  letter-spacing: -0.1rpx;
  display: block;
}

.line-label {
  color: #888888;
}

.line-value {
  color: #888888;
}

.profile-actions {
  display: flex;
  align-items: center;
  padding-left: 16rpx;
}

.star-btn {
  width: 72rpx;
  height: 72rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  transition: transform 0.2s ease, opacity 0.2s ease;
}

.star-btn:active {
  transform: scale(0.9);
  opacity: 0.6;
}

.star-icon {
  font-size: 48rpx;
  color: #cccccc;
  line-height: 1;
}

.star-btn.active .star-icon {
  color: #f5b800;
}

/* 分组卡片 */
.section-card {
  background-color: #ffffff;
  margin: 0 0 20rpx;
  overflow: hidden;
  border-radius: 16rpx;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.04);
  transition: all 0.3s ease;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24rpx 24rpx;
  border-bottom: 1rpx solid #ededed;
  transition: background-color 0.2s ease;
}

.section-header:active {
  background-color: #f7f7f7;
}

.section-title {
  font-size: 28rpx;
  color: #1d1d1f;
  font-weight: 600;
  letter-spacing: -0.2rpx;
}

.section-arrow {
  font-size: 36rpx;
  color: #c8c8c8;
  font-weight: 300;
  line-height: 1;
}

.section-body {
  display: flex;
  flex-direction: column;
}

.info-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24rpx;
  border-bottom: 1rpx solid #ededed;
  gap: 24rpx;
  min-height: 88rpx;
  transition: background-color 0.2s ease;
}

.info-row:last-child {
  border-bottom: none;
}

.info-row:active {
  background-color: #f7f7f7;
}

.info-label {
  font-size: 28rpx;
  color: #888888;
  flex-shrink: 0;
  font-weight: 400;
  min-width: 100rpx;
}

.info-value {
  font-size: 28rpx;
  color: #1d1d1f;
  font-weight: 500;
  text-align: right;
  flex: 1;
  min-width: 0;
  letter-spacing: -0.2rpx;
}

.info-value.ellipsis {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.section-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 28rpx 24rpx;
  transition: background-color 0.2s ease;
}

.section-card:active {
  background-color: #f7f7f7;
}

.section-row-text {
  font-size: 30rpx;
  color: #1d1d1f;
  font-weight: 500;
  letter-spacing: -0.2rpx;
}

/* 操作按钮区 */
.action-bar {
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: space-between;
  background-color: #ffffff;
  margin: 0;
  padding: 12rpx;
  border-radius: 20rpx;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.06);
  gap: 12rpx;
}

.action-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  flex: 1;
  padding: 32rpx 16rpx;
  border-radius: 16rpx;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
}

.action-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.2) 0%, transparent 50%);
  pointer-events: none;
}

.action-btn.primary {
  background: linear-gradient(135deg, #07c160 0%, #06ad56 100%);
  box-shadow: 0 6rpx 20rpx rgba(7, 193, 96, 0.4);
}

.action-btn.primary:active {
  background: linear-gradient(135deg, #06ad56 0%, #05994a 100%);
  box-shadow: 0 3rpx 10rpx rgba(7, 193, 96, 0.3);
}

.action-btn.primary .action-text {
  color: #ffffff;
}

.action-btn.primary .action-icon-wrap {
  background: rgba(255, 255, 255, 0.15);
}

.action-btn.secondary {
  background: linear-gradient(135deg, #f8f9fa 0%, #f0f1f2 100%);
  border: 1rpx solid #e8e8e8;
}

.action-btn.secondary:active {
  background: linear-gradient(135deg, #f0f1f2 0%, #e8e9ea 100%);
}

.action-btn:active {
  transform: scale(0.95);
}

.action-icon-wrap {
  width: 72rpx;
  height: 72rpx;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 12rpx;
  background: rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
}

.action-btn:active .action-icon-wrap {
  transform: scale(0.9);
}

.action-icon-text {
  font-size: 36rpx;
  line-height: 1;
}

.action-text {
  font-size: 26rpx;
  color: #1d1d1f;
  font-weight: 600;
  letter-spacing: -0.2rpx;
}

/* 底部危险按钮 */
.footer-section {
  margin-top: 40rpx;
  padding: 0 24rpx;
  padding-bottom: calc(24rpx + env(safe-area-inset-bottom));
  background-color: transparent;
}

.footer-btn {
  width: 100%;
  height: 88rpx;
  border-radius: 16rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  background-color: #ffffff;
}

.footer-btn text {
  font-size: 30rpx;
  font-weight: 500;
  letter-spacing: -0.2rpx;
}

.footer-btn.danger {
  border: none;
  background: linear-gradient(135deg, #ff4d4f 0%, #ff3b30 100%);
  box-shadow: 0 4rpx 16rpx rgba(255, 59, 48, 0.3);
}

.footer-btn.danger text {
  color: #ffffff;
}

.footer-btn.danger:active {
  background: linear-gradient(135deg, #ff3b30 0%, #ff2d20 100%);
  transform: scale(0.98);
  box-shadow: 0 2rpx 8rpx rgba(255, 59, 48, 0.2);
}

.fade-in {
  animation: fadeInUp 0.4s ease forwards;
  opacity: 0;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20rpx);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>