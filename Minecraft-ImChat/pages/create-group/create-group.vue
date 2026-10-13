<template>
  <view class="create-group">
    <CustomNavBar
      :show-back="true"
      title="发起群聊"
      title-align="center"
      @back="goBack"
    />

    <view class="group-card">
      <view class="avatar-wrap" @click="chooseAvatar">
        <UiAvatar shape="square" :size="140" :src="avatarLocalPath" :name="groupName || '群聊'" />
        <view class="avatar-badge">
          <UiIcon name="camera" :size="34" color="#FFFFFF" />
        </view>
      </view>

      <view class="field">
        <text class="field__label">群名称</text>
        <input
          class="field__input"
          v-model="groupName"
          placeholder="请输入群名称"
          placeholder-class="field__ph"
          maxlength="20"
        />
      </view>

      <view class="field field--textarea">
        <text class="field__label">群描述</text>
        <textarea
          class="field__textarea"
          v-model="groupDescription"
          placeholder="请输入群描述（选填）"
          placeholder-class="field__ph"
          maxlength="100"
        />
      </view>
    </view>

    <view class="members-bar">
      <text class="members-bar__title">选择成员</text>
      <text class="members-bar__count">已选 {{ selectedFriends.length }} 人</text>
    </view>

    <FriendPicker v-model="selectedFriends" />

    <view class="footer">
      <view
        class="create-btn"
        :class="{ 'is-disabled': !canCreate || submitting }"
        hover-class="create-btn--hover"
        hover-stay-time="60"
        @click="createGroup"
      >
        <text>创建群聊</text>
      </view>
    </view>
  </view>
</template>

<script>
import CustomNavBar from '../../components/CustomNavBar.vue'
import UiIcon from '../../components/Icon.vue'
import UiAvatar from '../../components/Avatar.vue'
import FriendPicker from '../../components/FriendPicker.vue'
import { createGroupWithMembers, uploadFile } from '../../utils/chat-api.js'
import { getUserId } from '../../utils/session.js'

export default {
  components: { CustomNavBar, UiIcon, UiAvatar, FriendPicker },
  data() {
    return {
      groupName: '',
      groupDescription: '',
      avatarLocalPath: '',
      selectedFriends: [],
      submitting: false
    }
  },
  computed: {
    canCreate() {
      return this.groupName.trim().length > 0
    }
  },
  methods: {
    goBack() {
      uni.navigateBack()
    },
    chooseAvatar() {
      uni.chooseImage({
        count: 1,
        success: (res) => {
          const filePath = res.tempFilePaths && res.tempFilePaths[0]
          if (filePath) this.avatarLocalPath = filePath
        }
      })
    },
    async createGroup() {
      if (this.submitting) return
      if (!this.canCreate) {
        uni.showToast({ title: '请输入群名称', icon: 'none' })
        return
      }
      const userId = getUserId()
      if (!userId) {
        uni.showToast({ title: '请先登录', icon: 'none' })
        return
      }

      this.submitting = true
      uni.showLoading({ title: '创建中…', mask: true })
      try {
        // 选择了群头像时，先上传拿到 URL 再提交
        let avatarUrl = ''
        if (this.avatarLocalPath) {
          const up = await uploadFile(this.avatarLocalPath)
          if (up && up.code === 200 && up.data) {
            avatarUrl = typeof up.data === 'string' ? up.data : (up.data.url || up.data.path || '')
          }
        }

        const res = await createGroupWithMembers({
          name: this.groupName.trim(),
          description: this.groupDescription || '',
          creatorId: userId,
          memberIds: this.selectedFriends.map((f) => f.id),
          avatar: avatarUrl || undefined
        })

        uni.hideLoading()
        if (res && res.code === 200 && res.data) {
          uni.showToast({ title: '创建成功', icon: 'success' })
          const groupId = res.data.id
          const name = encodeURIComponent(this.groupName.trim())
          const avatar = encodeURIComponent(avatarUrl || '')
          setTimeout(() => {
            uni.redirectTo({
              url: `/pages/chat/chat?type=group&id=${groupId}&name=${name}&avatar=${avatar}`
            })
          }, 800)
        } else {
          uni.showToast({ title: (res && res.message) || '创建失败', icon: 'none' })
        }
      } catch (e) {
        console.error('创建群聊失败:', e)
        uni.hideLoading()
        uni.showToast({ title: '创建失败', icon: 'none' })
      } finally {
        this.submitting = false
      }
    }
  }
}
</script>

<style scoped>
.create-group {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background-color: #ededed;
}

.group-card {
  flex-shrink: 0;
  background-color: #ffffff;
  margin-top: 20rpx;
  padding: 32rpx 0 8rpx;
}

.avatar-wrap {
  position: relative;
  width: 140rpx;
  height: 140rpx;
  margin: 8rpx auto 28rpx;
}

.avatar-wrap:active {
  opacity: 0.85;
}

.avatar-badge {
  position: absolute;
  right: -12rpx;
  bottom: -12rpx;
  width: 52rpx;
  height: 52rpx;
  border-radius: 50%;
  background-color: rgba(0, 0, 0, 0.55);
  display: flex;
  align-items: center;
  justify-content: center;
}

.field {
  display: flex;
  align-items: center;
  min-height: 88rpx;
  padding: 0 32rpx;
  position: relative;
}

.field::after {
  content: '';
  position: absolute;
  left: 32rpx;
  right: 0;
  bottom: 0;
  height: 1rpx;
  background-color: #ededed;
  transform: scaleY(0.5);
}

.field:last-child::after {
  display: none;
}

.field__label {
  flex-shrink: 0;
  min-width: 140rpx;
  font-size: 30rpx;
  color: #191919;
}

.field__input {
  flex: 1;
  margin-left: 24rpx;
  font-size: 30rpx;
  color: #191919;
}

.field__ph {
  color: #b2b2b2;
}

.field--textarea {
  align-items: flex-start;
  padding-top: 24rpx;
  padding-bottom: 24rpx;
}

.field--textarea .field__label {
  padding-top: 6rpx;
}

.field__textarea {
  flex: 1;
  margin-left: 24rpx;
  height: 120rpx;
  font-size: 30rpx;
  color: #191919;
  line-height: 1.5;
}

.members-bar {
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 24rpx 32rpx 12rpx;
}

.members-bar__title {
  font-size: 26rpx;
  color: #888888;
}

.members-bar__count {
  font-size: 26rpx;
  color: #888888;
}

.footer {
  flex-shrink: 0;
  padding: 20rpx 32rpx;
  padding-bottom: calc(20rpx + env(safe-area-inset-bottom));
  background-color: #ededed;
}

.create-btn {
  height: 88rpx;
  background-color: #07c160;
  border-radius: 10rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.create-btn--hover {
  background-color: #06ad56;
}

.create-btn.is-disabled {
  background-color: #c8e9d4;
}

.create-btn text {
  font-size: 32rpx;
  color: #ffffff;
  font-weight: 500;
}
</style>
