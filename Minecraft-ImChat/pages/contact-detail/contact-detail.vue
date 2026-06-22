<template>
  <view class="contact-detail-page">
    <!-- 基本信息 -->
    <view class="profile-section">
      <image
        class="profile-avatar"
        :src="contactInfo.avatar || defaultAvatar"
        mode="aspectFill"
      />
      <text class="profile-name">{{ contactInfo.name }}</text>
      <text class="profile-phone">{{ formatPhone(contactInfo.phone) }}</text>
    </view>

    <!-- 操作按钮 -->
    <view class="actions-section">
      <view class="action-item" @click="sendMessage">
        <view class="action-icon">
          <text>💬</text>
        </view>
        <text class="action-text">发消息</text>
      </view>
      <view v-if="contactType === 'single'" class="action-item" @click="makeCall">
        <view class="action-icon">
          <text>📞</text>
        </view>
        <text class="action-text">音视频通话</text>
      </view>
    </view>

    <!-- 详细信息 -->
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

    <!-- 群成员列表 -->
    <view v-if="contactType === 'group'" class="members-section">
      <view class="section-header">
        <text class="section-title">群成员</text>
        <text class="member-count">{{ memberCount }} 人</text>
      </view>
      <scroll-view class="member-list" scroll-x>
        <view
          v-for="member in members"
          :key="member.userId"
          class="member-item"
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

    <!-- 底部操作 -->
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
  background-color: #f5f5f5;
}

/* 基本信息 */
.profile-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 60rpx 30rpx;
  background-color: #fff;
}

.profile-avatar {
  width: 160rpx;
  height: 160rpx;
  border-radius: 50%;
  background-color: #f5f5f5;
  margin-bottom: 30rpx;
}

.profile-name {
  font-size: 40rpx;
  color: #1d1d1f;
  font-weight: 600;
  margin-bottom: 12rpx;
}

.profile-phone {
  font-size: 28rpx;
  color: #6e6e73;
}

/* 操作按钮 */
.actions-section {
  display: flex;
  justify-content: center;
  gap: 80rpx;
  padding: 40rpx 30rpx;
  background-color: #fff;
  margin-bottom: 20rpx;
}

.action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.action-icon {
  width: 100rpx;
  height: 100rpx;
  background-color: #f5f5f5;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 16rpx;
}

.action-icon text {
  font-size: 48rpx;
}

.action-text {
  font-size: 26rpx;
  color: #6e6e73;
}

/* 详细信息 */
.detail-section {
  background-color: #fff;
  padding: 0 30rpx;
  margin-bottom: 20rpx;
}

.detail-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 30rpx 0;
  border-bottom: 1rpx solid #f0f0f0;
}

.detail-item:last-child {
  border-bottom: none;
}

.detail-label {
  font-size: 30rpx;
  color: #6e6e73;
}

.detail-value {
  font-size: 30rpx;
  color: #1d1d1f;
}

/* 群成员 */
.members-section {
  background-color: #fff;
  margin-bottom: 20rpx;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 30rpx;
  border-bottom: 1rpx solid #f0f0f0;
}

.section-title {
  font-size: 30rpx;
  color: #1d1d1f;
  font-weight: 500;
}

.member-count {
  font-size: 26rpx;
  color: #6e6e73;
}

.member-list {
  padding: 30rpx;
  white-space: nowrap;
}

.member-item {
  display: inline-flex;
  flex-direction: column;
  align-items: center;
  margin-right: 40rpx;
}

.member-avatar {
  width: 100rpx;
  height: 100rpx;
  border-radius: 50%;
  background-color: #f5f5f5;
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

/* 底部操作 */
.footer-section {
  margin-top: auto;
  padding: 20rpx 30rpx;
  padding-bottom: calc(20rpx + env(safe-area-inset-bottom));
  background-color: #fff;
}

.footer-btn {
  width: 100%;
  height: 88rpx;
  border-radius: 44rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.footer-btn text {
  font-size: 32rpx;
}

.footer-btn.danger {
  background-color: #fff;
  border: 2rpx solid #ff3b30;
}

.footer-btn.danger text {
  color: #ff3b30;
}
</style>
