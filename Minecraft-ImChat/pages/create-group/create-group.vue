<template>
  <view class="create-group-page">
    <view class="group-info-section">
      <view class="section-title">群信息</view>
      <view class="group-avatar-wrapper">
        <image
          class="group-avatar"
          :src="groupAvatar || defaultGroupAvatar"
          mode="aspectFill"
        />
        <view class="avatar-mask" @click="chooseAvatar">
          <text class="mask-icon">📷</text>
        </view>
      </view>
      <view class="input-item">
        <text class="input-label">群名称</text>
        <input
          class="input-field"
          v-model="groupName"
          placeholder="请输入群名称"
          placeholder-class="input-placeholder"
          maxlength="20"
        />
      </view>
      <view class="input-item">
        <text class="input-label">群描述</text>
        <textarea
          class="input-textarea"
          v-model="groupDescription"
          placeholder="请输入群描述（选填）"
          placeholder-class="input-placeholder"
          maxlength="100"
        />
      </view>
    </view>

    <view class="members-section">
      <view class="section-header">
        <text class="section-title">选择成员</text>
        <text class="member-count">已选 {{ selectedMembers.length }} 人</text>
      </view>

      <view class="search-bar">
        <view class="search-input-wrapper">
          <text class="search-icon">🔍</text>
          <input
            class="search-input"
            v-model="searchKeyword"
            placeholder="搜索好友"
            placeholder-class="search-placeholder"
            @input="onSearch"
          />
          <view v-if="searchKeyword" class="clear-btn" @click="clearSearch">
            <text>✕</text>
          </view>
        </view>
      </view>

      <scroll-view class="friend-list" scroll-y>
        <view
          v-for="(friend, index) in filteredFriends"
          :key="friend.id"
          :style="{ animationDelay: `${index * 0.05}s` }"
          :class="['friend-item fade-in', { selected: isSelected(friend.id) }]"
          @click="toggleMember(friend)"
        >
          <image
            class="friend-avatar"
            :src="friend.avatar || defaultAvatar"
            mode="aspectFill"
          />
          <view class="friend-info">
            <text class="friend-name">{{ friend.name }}</text>
          </view>
          <view :class="['checkbox', { checked: isSelected(friend.id) }]">
            <text v-if="isSelected(friend.id)" class="check-icon">✓</text>
          </view>
        </view>

        <view v-if="filteredFriends.length === 0" class="empty-state">
          <text class="empty-icon">👤</text>
          <text class="empty-text">暂无可添加的好友</text>
        </view>
      </scroll-view>
    </view>

    <view class="create-footer">
      <view
        :class="['create-btn', { active: canCreate }]"
        @click="createGroup"
      >
        <text>创建群聊</text>
      </view>
    </view>
  </view>
</template>

<script>
import { getFriendInfoList, createGroupWithMembers } from '../../utils/chat-api.js'
import { getUserInfo } from '../../utils/storage.js'

export default {
  data() {
    return {
      groupName: '',
      groupDescription: '',
      groupAvatar: '',
      friends: [],
      selectedMembers: [],
      searchKeyword: '',
      defaultAvatar: '/static/default-avatar.png',
      defaultGroupAvatar: '/static/default-group.png'
    }
  },

  computed: {
    filteredFriends() {
      if (!this.searchKeyword) return this.friends

      const keyword = this.searchKeyword.toLowerCase()
      return this.friends.filter(f =>
        f.name.toLowerCase().includes(keyword)
      )
    },

    canCreate() {
      return this.groupName.trim().length > 0
    }
  },

  onLoad() {
    this.loadFriends()
  },

  methods: {
    async loadFriends() {
      const userInfo = getUserInfo()
      if (!userInfo?.id) return

      try {
        const res = await getFriendInfoList(userInfo.id)
        if (res.code === 200 && res.data) {
          this.friends = res.data.map(f => ({
            id: f.friendId,
            name: f.remark || f.username || '未知用户',
            avatar: f.avatar || ''
          }))
        }
      } catch (e) {
        console.error('加载好友列表失败:', e)
      }
    },

    onSearch() {
    },

    clearSearch() {
      this.searchKeyword = ''
    },

    isSelected(id) {
      return this.selectedMembers.some(m => m.id === id)
    },

    toggleMember(friend) {
      const index = this.selectedMembers.findIndex(m => m.id === friend.id)
      if (index > -1) {
        this.selectedMembers.splice(index, 1)
      } else {
        this.selectedMembers.push(friend)
      }
    },

    chooseAvatar() {
      uni.chooseImage({
        count: 1,
        success: (res) => {
          this.tempAvatarPath = res.tempFilePaths[0]
          this.groupAvatar = res.tempFilePaths[0]
        }
      })
    },

    async createGroup() {
      if (!this.canCreate) {
        uni.showToast({ title: '请输入群名称', icon: 'none' })
        return
      }

      const userInfo = getUserInfo()
      if (!userInfo) {
        uni.showToast({ title: '请先登录', icon: 'none' })
        return
      }

      uni.showLoading({ title: '创建中...' })

      try {
        const res = await createGroupWithMembers({
          name: this.groupName.trim(),
          description: this.groupDescription.trim(),
          creatorId: userInfo.id,
          memberIds: this.selectedMembers.map(m => m.id)
        })

        if (res.code === 200) {
          uni.showToast({ title: '创建成功', icon: 'success' })

          setTimeout(() => {
            uni.navigateBack()
            uni.navigateTo({
              url: `/pages/chat/chat?type=group&id=${res.data.id}&name=${encodeURIComponent(this.groupName)}`
            })
          }, 1500)
        } else {
          uni.showToast({ title: res.message || '创建失败', icon: 'none' })
        }
      } catch (e) {
        console.error('创建群聊失败:', e)
        uni.showToast({ title: '创建失败', icon: 'none' })
      } finally {
        uni.hideLoading()
      }
    }
  }
}
</script>

<style scoped>
.create-group-page {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background-color: #f5f5f7;
  padding-bottom: calc(40rpx + env(safe-area-inset-bottom));
}

.group-info-section {
  margin: 32rpx 32rpx 24rpx;
  padding: 48rpx 32rpx 32rpx;
  background-color: #ffffff;
  border-radius: 24rpx;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.04);
}

.section-title {
  font-size: 28rpx;
  color: #1d1d1f;
  font-weight: 600;
  margin-bottom: 32rpx;
  letter-spacing: -0.2rpx;
}

.group-avatar-wrapper {
  position: relative;
  width: 144rpx;
  height: 144rpx;
  margin: 0 auto 40rpx;
}

.group-avatar {
  width: 144rpx;
  height: 144rpx;
  border-radius: 28rpx;
  background-color: #f5f5f7;
}

.avatar-mask {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.4);
  border-radius: 28rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background-color 0.2s ease;
}

.avatar-mask:active {
  background-color: rgba(0, 0, 0, 0.6);
}

.mask-icon {
  font-size: 40rpx;
}

.input-item {
  margin-bottom: 32rpx;
}

.input-item:last-child {
  margin-bottom: 0;
}

.input-label {
  display: block;
  font-size: 24rpx;
  color: #6e6e73;
  margin-bottom: 12rpx;
  font-weight: 500;
}

.input-field {
  width: 100%;
  height: 88rpx;
  padding: 0 32rpx;
  background-color: #f5f5f7;
  border-radius: 16rpx;
  font-size: 30rpx;
  color: #1d1d1f;
  box-sizing: border-box;
  border: none;
  outline: none;
  transition: all 0.25s ease;
}

.input-field:focus {
  background-color: #ffffff;
  box-shadow: inset 0 0 0 2rpx #2997ff;
}

.input-placeholder {
  color: #a1a1a6;
}

.input-textarea {
  width: 100%;
  height: 160rpx;
  padding: 24rpx 32rpx;
  background-color: #f5f5f7;
  border-radius: 16rpx;
  font-size: 30rpx;
  color: #1d1d1f;
  box-sizing: border-box;
  border: none;
  outline: none;
  line-height: 1.5;
  transition: all 0.25s ease;
}

.input-textarea:focus {
  background-color: #ffffff;
  box-shadow: inset 0 0 0 2rpx #2997ff;
}

.members-section {
  flex: 1;
  margin: 0 32rpx 32rpx;
  background-color: #ffffff;
  border-radius: 24rpx;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.04);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 32rpx;
  border-bottom: 1rpx solid #f5f5f7;
}

.member-count {
  font-size: 24rpx;
  color: #6e6e73;
  font-weight: 400;
}

.search-bar {
  padding: 16rpx 32rpx;
  border-bottom: 1rpx solid #f5f5f7;
}

.search-input-wrapper {
  display: flex;
  align-items: center;
  background-color: #f5f5f7;
  border-radius: 36rpx;
  padding: 16rpx 24rpx;
  transition: all 0.25s ease;
}

.search-input-wrapper:focus-within {
  background-color: #ffffff;
  box-shadow: 0 0 0 3rpx rgba(41, 151, 255, 0.1);
}

.search-icon {
  font-size: 28rpx;
  margin-right: 16rpx;
  opacity: 0.5;
}

.search-input {
  flex: 1;
  font-size: 28rpx;
  color: #1d1d1f;
  background: transparent;
  border: none;
  outline: none;
}

.search-placeholder {
  color: #a1a1a6;
}

.clear-btn {
  width: 40rpx;
  height: 40rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #d2d2d6;
  border-radius: 50%;
  transition: all 0.2s ease;
}

.clear-btn:active {
  background-color: #a1a1a6;
}

.clear-btn text {
  font-size: 24rpx;
  color: #ffffff;
  line-height: 1;
}

.friend-list {
  flex: 1;
}

.friend-item {
  display: flex;
  align-items: center;
  padding: 24rpx 32rpx;
  border-bottom: 1rpx solid #f5f5f7;
  transition: all 0.2s ease;
}

.friend-item:last-child {
  border-bottom: none;
}

.friend-item:active {
  background-color: #f5f5f7;
}

.friend-item.selected {
  background-color: rgba(41, 151, 255, 0.06);
}

.friend-avatar {
  width: 80rpx;
  height: 80rpx;
  border-radius: 50%;
  background-color: #f5f5f7;
  margin-right: 24rpx;
  flex-shrink: 0;
}

.friend-info {
  flex: 1;
  min-width: 0;
}

.friend-name {
  font-size: 30rpx;
  color: #1d1d1f;
  font-weight: 500;
  letter-spacing: -0.2rpx;
}

.checkbox {
  width: 44rpx;
  height: 44rpx;
  border: 2rpx solid #d2d2d6;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #ffffff;
  flex-shrink: 0;
  transition: all 0.2s ease;
}

.checkbox.checked {
  background-color: #2997ff;
  border-color: #2997ff;
}

.check-icon {
  font-size: 24rpx;
  color: #ffffff;
  font-weight: 600;
  line-height: 1;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80rpx 0;
}

.empty-icon {
  font-size: 120rpx;
  margin-bottom: 24rpx;
  opacity: 0.3;
}

.empty-text {
  font-size: 28rpx;
  color: #6e6e73;
}

.create-footer {
  padding: 24rpx 32rpx;
  padding-bottom: calc(24rpx + env(safe-area-inset-bottom));
  background-color: rgba(255, 255, 255, 0.96);
  backdrop-filter: saturate(180%) blur(20rpx);
  border-top: 1rpx solid #f5f5f7;
}

.create-btn {
  width: 100%;
  height: 88rpx;
  background-color: #e8e8ed;
  border-radius: 44rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
}

.create-btn text {
  font-size: 32rpx;
  color: #ffffff;
  font-weight: 500;
  letter-spacing: -0.2rpx;
}

.create-btn.active {
  background: linear-gradient(135deg, #2997ff 0%, #0066cc 100%);
}

.create-btn.active:active {
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