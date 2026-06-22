<template>
  <view class="contact-detail-page">
    <view class="profile-section">
      <image
        class="profile-avatar"
        :src="contactInfo.avatar || defaultAvatar"
        mode="aspectFill"
      />
      <text class="profile-name">{{ contactInfo.name }}</text>
      <text class="profile-phone">{{ formatPhone(contactInfo.phone) }}</text>
    </view>

    <view class="actions-section">
      <view class="action-item" @click="sendMessage">
        <view class="action-icon chat-icon">
          <text>💬</text>
        </view>
        <text class="action-text">发消息</text>
      </view>
      <view v-if="contactType === 'single'" class="action-item" @click="makeCall">
        <view class="action-icon call-icon">
          <text>📞</text>
        </view>
        <text class="action-text">音视频通话</text>
      </view>
    </view>

    <view class="detail-section">
      <view v-if="contactType === 'single'" class="detail-item">
        <text class="detail-label">手机号</text>
        <text class="detail-value">{{ contactInfo.phone || '未填写' }}</text>
      </view>
      <view v-if="contactType === 'single'" class="detail-item">
        <text class="detail-label">备注</text>
        <text class="detail-value">{{ contactInfo.remark || '未设置' }}</text>
      </view>

      <view v-if="contactType === 'group'" class="detail-item">
        <text class="detail-label">群聊ID</text>
        <text class="detail-value">{{ contactInfo.id }}</text>
      </view>
      <view v-if="contactType === 'group'" class="detail-item">
        <text class="detail-label">群成员</text>
        <text class="detail-value">{{ memberCount }} 人</text>
      </view>
      <view v-if="contactType === 'group'" class="detail-item">
        <text class="detail-label">群描述</text>
        <text class="detail-value">{{ contactInfo.description || '暂无描述' }}</text>
      </view>
    </view>

    <view v-if="contactType === 'group'" class="members-section">
      <view class="section-header">
        <text class="section-title">群成员</text>
        <text class="member-count">{{ memberCount }} 人</text>
      </view>
      <scroll-view class="member-list" scroll-x>
        <view
          v-for="(member, index) in members"
          :key="member.userId"
          :style="{ animationDelay: `${index * 0.05}s` }"
          class="member-item fade-in"
        >
          <image
            class="member-avatar"
            :src="member.avatar || defaultAvatar"
            mode="aspectFill"
          />
          <text class="member-name">{{ member.username }}</text>
        </view>
      </scroll-view>
    </view>

    <view class="footer-section">
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
  </view>
</template>

<script>
import {
  getGroupById,
  getGroupMembers,
  deleteFriend,
  deleteGroup,
  removeGroupMember
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
        description: ''
      },
      members: [],
      memberCount: 0,
      defaultAvatar: '/static/default-avatar.png'
    }
  },

  onLoad(options) {
    this.contactType = options.type || 'single'
    this.contactId = options.id
    this.contactInfo = {
      id: options.id,
      name: decodeURIComponent(options.name || ''),
      avatar: decodeURIComponent(options.avatar || ''),
      isCreator: options.isCreator === 'true'
    }

    if (this.contactType === 'group') {
      this.loadGroupInfo()
      this.loadGroupMembers()
    }
  },

  methods: {
    async loadGroupInfo() {
      try {
        const res = await getGroupById(this.contactId)
        if (res.code === 200 && res.data) {
          this.contactInfo.description = res.data.description || ''
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

    sendMessage() {
      uni.navigateBack()
      setTimeout(() => {
        uni.navigateTo({
          url: `/pages/chat/chat?type=${this.contactType}&id=${this.contactId}&name=${encodeURIComponent(this.contactInfo.name)}&avatar=${encodeURIComponent(this.contactInfo.avatar)}`
        })
      }, 100)
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
  background-color: #f5f5f7;
  padding-bottom: calc(40rpx + env(safe-area-inset-bottom));
}

.profile-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 64rpx 32rpx 56rpx;
  background: linear-gradient(135deg, #ffffff 0%, #f5f5f7 100%);
  margin: 32rpx 32rpx 24rpx;
  border-radius: 24rpx;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.04);
}

.profile-avatar {
  width: 160rpx;
  height: 160rpx;
  border-radius: 50%;
  background-color: #f5f5f7;
  margin-bottom: 32rpx;
  border: 4rpx solid #ffffff;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.1);
}

.profile-name {
  font-size: 40rpx;
  color: #1d1d1f;
  font-weight: 700;
  margin-bottom: 8rpx;
  letter-spacing: -0.4rpx;
}

.profile-phone {
  font-size: 26rpx;
  color: #6e6e73;
  font-weight: 400;
}

.actions-section {
  display: flex;
  justify-content: center;
  gap: 96rpx;
  padding: 40rpx 32rpx;
  background-color: #ffffff;
  margin: 0 32rpx 24rpx;
  border-radius: 24rpx;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.04);
}

.action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  transition: opacity 0.2s ease;
}

.action-item:active {
  opacity: 0.6;
}

.action-icon {
  width: 96rpx;
  height: 96rpx;
  border-radius: 24rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 16rpx;
  transition: all 0.2s ease;
}

.chat-icon {
  background: linear-gradient(135deg, #2997ff 0%, #0066cc 100%);
}

.call-icon {
  background: linear-gradient(135deg, #34c759 0%, #30d158 100%);
}

.action-item:active .action-icon {
  transform: scale(0.97);
}

.action-icon text {
  font-size: 44rpx;
  line-height: 1;
}

.action-text {
  font-size: 24rpx;
  color: #6e6e73;
  font-weight: 500;
}

.detail-section {
  background-color: #ffffff;
  margin: 0 32rpx 24rpx;
  border-radius: 24rpx;
  padding: 0 32rpx;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.04);
  overflow: hidden;
}

.detail-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 32rpx 0;
  border-bottom: 1rpx solid #f5f5f7;
  gap: 24rpx;
}

.detail-item:last-child {
  border-bottom: none;
}

.detail-label {
  font-size: 28rpx;
  color: #6e6e73;
  flex-shrink: 0;
  font-weight: 500;
}

.detail-value {
  font-size: 28rpx;
  color: #1d1d1f;
  font-weight: 500;
  text-align: right;
  flex: 1;
  min-width: 0;
  letter-spacing: -0.2rpx;
}

.members-section {
  background-color: #ffffff;
  margin: 0 32rpx 24rpx;
  border-radius: 24rpx;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.04);
  overflow: hidden;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 32rpx;
  border-bottom: 1rpx solid #f5f5f7;
}

.section-title {
  font-size: 28rpx;
  color: #1d1d1f;
  font-weight: 600;
  letter-spacing: -0.2rpx;
}

.member-count {
  font-size: 24rpx;
  color: #6e6e73;
  font-weight: 400;
}

.member-list {
  padding: 32rpx;
  white-space: nowrap;
}

.member-item {
  display: inline-flex;
  flex-direction: column;
  align-items: center;
  margin-right: 40rpx;
  transition: opacity 0.2s ease;
}

.member-item:active {
  opacity: 0.6;
}

.member-avatar {
  width: 96rpx;
  height: 96rpx;
  border-radius: 50%;
  background-color: #f5f5f7;
  margin-bottom: 12rpx;
}

.member-name {
  font-size: 24rpx;
  color: #6e6e73;
  max-width: 120rpx;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.footer-section {
  margin-top: auto;
  padding: 24rpx 32rpx;
  padding-bottom: calc(24rpx + env(safe-area-inset-bottom));
  background-color: rgba(255, 255, 255, 0.96);
  backdrop-filter: saturate(180%) blur(20rpx);
  border-top: 1rpx solid #f5f5f7;
}

.footer-btn {
  width: 100%;
  height: 88rpx;
  border-radius: 44rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
}

.footer-btn text {
  font-size: 32rpx;
  font-weight: 500;
  letter-spacing: -0.2rpx;
}

.footer-btn.danger {
  background-color: #ffffff;
  border: 2rpx solid #ff3b30;
}

.footer-btn.danger text {
  color: #ff3b30;
}

.footer-btn.danger:active {
  background-color: rgba(255, 59, 48, 0.06);
  transform: scale(0.99);
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