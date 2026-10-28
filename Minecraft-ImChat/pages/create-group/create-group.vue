<template>
  <view class="create-group" :class="themeClass">
    <CustomNavBar
      :show-back="true"
      :title="$t('createGroup.title')"
      title-align="center"
      @back="goBack"
    />

    <view class="group-card surface-card grid-overlay">
      <view class="group-card__avatar" @click="chooseAvatar">
        <UiAvatar
          shape="square"
          :size="128"
          :src="avatarLocalPath"
          :name="groupName || $t('createGroup.groupFallback')"
        />
        <view class="group-card__badge">
          <UiIcon name="camera" :size="26" color="var(--accent-contrast)" />
        </view>
      </view>

      <view class="group-card__fields">
        <view class="field">
          <text class="field__label">{{ $t('createGroup.groupName') }}</text>
          <input
            class="field__input"
            v-model="groupName"
            :placeholder="$t('createGroup.groupNamePlaceholder')"
            placeholder-class="field__ph"
            maxlength="20"
          />
        </view>

        <view class="field field--area">
          <text class="field__label">{{ $t('createGroup.groupDescription') }}</text>
          <textarea
            class="field__area"
            v-model="groupDescription"
            :placeholder="$t('createGroup.descriptionPlaceholder')"
            placeholder-class="field__ph"
            maxlength="100"
          />
        </view>
      </view>
    </view>

    <view class="members-bar">
      <text class="members-bar__title">{{ $t('createGroup.membersTitle') }}</text>
      <view class="chip chip--accent">
        {{ $t('createGroup.selectedCount', { count: selectedFriends.length }) }}
      </view>
    </view>

    <scroll-view
      v-if="selectedFriends.length"
      class="selected-strip"
      scroll-x
      :show-scrollbar="false"
    >
      <view class="selected-strip__inner">
        <view
          v-for="friend in selectedFriends"
          :key="friend.id"
          class="selected-strip__item"
        >
          <UiAvatar shape="square" :size="84" :src="friend.avatar" :name="friend.name" />
          <text class="selected-strip__name">{{ friend.name }}</text>
        </view>
      </view>
    </scroll-view>

    <FriendPicker v-model="selectedFriends" />

    <view class="footer">
      <view
        class="create-btn"
        :class="{ 'is-disabled': createDisabled }"
        hover-class="create-btn--hover"
        hover-stay-time="60"
        @click="createGroup"
      >
        <UiIcon
          name="users"
          :size="30"
          :color="createDisabled ? 'var(--text-tertiary)' : 'var(--accent-contrast)'"
        />
        <text>{{ $t('createGroup.createBtn') }}</text>
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
    },
    createDisabled() {
      return !this.canCreate || this.submitting
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
        uni.showToast({ title: this.$t('createGroup.nameRequired'), icon: 'none' })
        return
      }
      const userId = getUserId()
      if (!userId) {
        uni.showToast({ title: this.$t('createGroup.loginRequired'), icon: 'none' })
        return
      }

      this.submitting = true
      uni.showLoading({ title: this.$t('createGroup.creating'), mask: true })
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
          uni.showToast({ title: this.$t('createGroup.createSuccess'), icon: 'success' })
          const groupId = res.data.id
          const name = encodeURIComponent(this.groupName.trim())
          const avatar = encodeURIComponent(avatarUrl || '')
          setTimeout(() => {
            uni.redirectTo({
              url: `/pages/chat/chat?type=group&id=${groupId}&name=${name}&avatar=${avatar}`
            })
          }, 800)
        } else {
          uni.showToast({
            title: (res && res.message) || this.$t('createGroup.createFailed'),
            icon: 'none'
          })
        }
      } catch (e) {
        console.error('创建群聊失败:', e)
        uni.hideLoading()
        uni.showToast({ title: this.$t('createGroup.createFailed'), icon: 'none' })
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
  background-color: var(--bg-secondary);
  background-image: radial-gradient(120% 52% at 50% 0%, var(--accent-light) 0%, transparent 62%);
  color: var(--text-primary);
}

.group-card {
  flex-shrink: 0;
  display: flex;
  align-items: flex-start;
  margin: 20rpx 24rpx 0;
  padding: 24rpx;
}

.group-card__avatar {
  position: relative;
  flex-shrink: 0;
  margin-right: 24rpx;
}

.group-card__avatar:active {
  opacity: 0.85;
}

.group-card__badge {
  position: absolute;
  right: -8rpx;
  bottom: -8rpx;
  width: 48rpx;
  height: 48rpx;
  border-radius: 50%;
  background-image: var(--accent-gradient);
  box-shadow: var(--shadow-sm);
  display: flex;
  align-items: center;
  justify-content: center;
}

.group-card__fields {
  position: relative;
  z-index: 1;
  flex: 1;
  min-width: 0;
}

.field {
  padding: 8rpx 0 12rpx;
  border-bottom: 1rpx solid var(--divider);
}

.field--area {
  padding-top: 12rpx;
  border-bottom: none;
}

.field__label {
  display: block;
  font-size: var(--font-xs);
  font-weight: 600;
  letter-spacing: 1rpx;
  color: var(--text-tertiary);
}

.field__input {
  width: 100%;
  height: 60rpx;
  margin-top: 4rpx;
  font-size: var(--font-base);
  color: var(--text-primary);
}

.field__area {
  width: 100%;
  height: 96rpx;
  margin-top: 6rpx;
  font-size: var(--font-sm);
  color: var(--text-primary);
  line-height: 1.5;
}

.field__ph {
  color: var(--text-placeholder);
}

.members-bar {
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 24rpx 40rpx 10rpx;
}

.members-bar__title {
  font-size: var(--font-xs);
  font-weight: 600;
  letter-spacing: 2rpx;
  text-transform: uppercase;
  color: var(--text-tertiary);
}

.selected-strip {
  flex-shrink: 0;
  white-space: nowrap;
}

.selected-strip__inner {
  display: inline-flex;
  gap: 18rpx;
  padding: 8rpx 24rpx 12rpx;
}

.selected-strip__item {
  width: 96rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.selected-strip__name {
  max-width: 96rpx;
  margin-top: 6rpx;
  font-size: var(--font-xs);
  color: var(--text-secondary);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.footer {
  flex-shrink: 0;
  padding: 16rpx 24rpx;
  padding-bottom: calc(16rpx + env(safe-area-inset-bottom));
  background-color: var(--bg-secondary);
  border-top: 1rpx solid var(--divider);
}

.create-btn {
  height: 88rpx;
  border-radius: var(--radius-md);
  background-image: var(--accent-gradient);
  box-shadow: var(--accent-glow);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10rpx;
  transition: transform var(--transition-fast), opacity var(--transition-fast);
}

.create-btn text {
  font-size: var(--font-md);
  font-weight: 600;
  color: var(--accent-contrast);
}

.create-btn--hover {
  transform: scale(0.99);
  opacity: 0.9;
}

.create-btn.is-disabled {
  background-image: none;
  background-color: var(--bg-chip);
  border: 1rpx solid var(--border-light);
  box-shadow: none;
}

.create-btn.is-disabled text {
  color: var(--text-tertiary);
}
</style>
