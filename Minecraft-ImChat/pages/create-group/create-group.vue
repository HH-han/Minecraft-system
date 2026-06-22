<template>
  <view class="create-group-page">
    <!-- 群信息 -->
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
          maxlength="20"
        />
      </view>
      <view class="input-item">
        <text class="input-label">群描述</text>
        <textarea
          class="input-textarea"
          v-model="groupDescription"
          placeholder="请输入群描述（选填）"
          maxlength="100"
        />
      </view>
    </view>

    <!-- 选择成员 -->
    <view class="members-section">
      <view class="section-header">
        <text class="section-title">选择成员</text>
        <text class="member-count">已选 {{ selectedMembers.length }} 人</text>
      </view>

      <view class="search-bar">
        <input
          class="search-input"
          v-model="searchKeyword"
          placeholder="搜索好友"
          @input="onSearch"
        />
      </view>

      <scroll-view class="friend-list" scroll-y>
        <view
          v-for="friend in filteredFriends"
          :key="friend.id"
          :class="['friend-item', { selected: isSelected(friend.id) }]"
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
          <text class="empty-text">暂无可添加的好友</text>
        </view>
      </scroll-view>
    </view>

    <!-- 创建按钮 -->
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
      // 搜索逻辑已在 computed 中处理
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

          // 跳转到聊天页面
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
  background-color: #f5f5f5;
}

/* 群信息区域 */
.group-info-section {
  padding: 30rpx;
  background-color: #fff;
  margin-bottom: 20rpx;
}

.section-title {
  font-size: 30rpx;
  color: #1d1d1f;
  font-weight: 500;
  margin-bottom: 30rpx;
}

.group-avatar-wrapper {
  position: relative;
  width: 160rpx;
  height: 160rpx;
  margin: 0 auto 40rpx;
}

.group-avatar {
  width: 160rpx;
  height: 160rpx;
  border-radius: 24rpx;
  background-color: #f5f5f5;
}

.avatar-mask {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.4);
  border-radius: 24rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.mask-icon {
  font-size: 48rpx;
}

.input-item {
  margin-bottom: 30rpx;
}

.input-label {
  display: block;
  font-size: 28rpx;
  color: #6e6e73;
  margin-bottom: 12rpx;
}

.input-field {
  width: 100%;
  height: 80rpx;
  padding: 0 24rpx;
  background-color: #f5f5f5;
  border-radius: 16rpx;
  font-size: 30rpx;
  box-sizing: border-box;
}

.input-textarea {
  width: 100%;
  height: 160rpx;
  padding: 20rpx 24rpx;
  background-color: #f5f5f5;
  border-radius: 16rpx;
  font-size: 30rpx;
  box-sizing: border-box;
}

/* 成员区域 */
.members-section {
  flex: 1;
  background-color: #fff;
  display: flex;
  flex-direction: column;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 30rpx;
  border-bottom: 1rpx solid #f0f0f0;
}

.member-count {
  font-size: 26rpx;
  color: #6e6e73;
}

.search-bar {
  padding: 20rpx 30rpx;
}

.search-input {
  width: 100%;
  height: 72rpx;
  padding: 0 24rpx;
  background-color: #f5f5f5;
  border-radius: 36rpx;
  font-size: 28rpx;
  box-sizing: border-box;
}

.friend-list {
  flex: 1;
}

.friend-item {
  display: flex;
  align-items: center;
  padding: 24rpx 30rpx;
  border-bottom: 1rpx solid #f0f0f0;
}

.friend-item:active {
  background-color: #f5f5f5;
}

.friend-item.selected {
  background-color: #e8f4ff;
}

.friend-avatar {
  width: 80rpx;
  height: 80rpx;
  border-radius: 50%;
  background-color: #f5f5f5;
  margin-right: 20rpx;
}

.friend-info {
  flex: 1;
}

.friend-name {
  font-size: 30rpx;
  color: #1d1d1f;
}

.checkbox {
  width: 44rpx;
  height: 44rpx;
  border: 2rpx solid #d2d2d6;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.checkbox.checked {
  background-color: #2997ff;
  border-color: #2997ff;
}

.check-icon {
  font-size: 24rpx;
  color: #fff;
}

/* 空状态 */
.empty-state {
  display: flex;
  justify-content: center;
  padding: 60rpx 0;
}

.empty-text {
  font-size: 28rpx;
  color: #a1a1a6;
}

/* 底部按钮 */
.create-footer {
  padding: 20rpx 30rpx;
  padding-bottom: calc(20rpx + env(safe-area-inset-bottom));
  background-color: #fff;
  border-top: 1rpx solid #e5e5e5;
}

.create-btn {
  width: 100%;
  height: 88rpx;
  background-color: #e5e5e5;
  border-radius: 44rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.create-btn text {
  font-size: 32rpx;
  color: #fff;
}

.create-btn.active {
  background-color: #2997ff;
}
</style>
