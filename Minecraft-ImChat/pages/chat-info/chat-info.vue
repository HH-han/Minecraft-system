<template>
  <view class="ci-page" :class="themeClass">
    <CustomNavBar
      :show-back="true"
      :title="$t('chatInfo.title')"
      title-align="center"
      @back="goBack"
    />

    <scroll-view class="ci-scroll" scroll-y :show-scrollbar="false">
      <!-- 资料头部 -->
      <view class="ci-hero surface-card grid-overlay">
        <view class="ci-hero__glow"></view>
        <view class="ci-hero__avatar">
          <UiAvatar
            shape="square"
            :size="140"
            :src="info.avatar"
            :name="displayName"
            :ring="isGroup"
          />
        </view>
        <text class="ci-hero__name">{{ displayName }}</text>
        <view class="ci-hero__chips">
          <view v-if="isGroup" class="chip chip--accent">{{ $t('common.groupChat') }}</view>
          <view v-else class="chip chip--accent">{{ $t('chatInfo.friendChip') }}</view>
          <view v-if="isGroup && isCreator" class="chip">{{ $t('chatInfo.ownerChip') }}</view>
          <view v-if="!isGroup && info.remark" class="chip">{{ info.remark }}</view>
        </view>
      </view>

      <!-- 群成员网格 -->
      <view v-if="isGroup" class="group-block members-block">
        <view class="members-block__head">
          <text class="block-label">{{ $t('chatInfo.membersTitle') }}</text>
          <text class="members-block__count">{{ $t('common.members', { count: memberCount }) }}</text>
        </view>
        <view class="ci-members">
          <view
            v-for="member in members"
            :key="member.id"
            class="ci-member"
            @click="openMember(member)"
          >
            <UiAvatar shape="square" :size="96" :src="member.avatar" :name="member.name" />
            <text class="ci-member__name">{{ member.name }}</text>
          </view>
          <view class="ci-member" @click="openInvite">
            <view class="ci-member__plus">
              <UiIcon name="plus" :size="40" color="var(--accent-color)" />
            </view>
            <text class="ci-member__name ci-member__name--accent">{{ $t('chatInfo.invite') }}</text>
          </view>
        </view>
      </view>

      <!-- 设置项分组卡 -->
      <text class="block-label ci-label">{{ $t('chatInfo.settingsTitle') }}</text>
      <view class="group-block ci-block">
        <template v-if="!isGroup">
          <CellItem
            :title="$t('chatInfo.remark')"
            :value="info.remark || $t('common.notSet')"
            :arrow="true"
            @click="editRemark"
          >
            <template #icon><UiIcon name="tag" :size="36" color="#FFFFFF" /></template>
          </CellItem>
          <CellItem
            :title="$t('chatInfo.searchHistory')"
            :arrow="true"
            @click="comingSoon($t('chatInfo.searchHistory'))"
          >
            <template #icon><UiIcon name="search" :size="36" color="#FFFFFF" /></template>
          </CellItem>
          <CellItem
            :title="$t('chatInfo.clearHistory')"
            :border="false"
            @click="clearHistory"
          >
            <template #icon><UiIcon name="clock" :size="36" color="#FFFFFF" /></template>
          </CellItem>
        </template>
        <template v-else>
          <CellItem
            :title="$t('chatInfo.groupName')"
            :value="info.name"
            :arrow="true"
            @click="openEdit"
          >
            <template #icon><UiIcon name="edit" :size="36" color="#FFFFFF" /></template>
          </CellItem>
          <CellItem
            :title="$t('chatInfo.groupNotice')"
            :value="info.description || $t('common.notSet')"
            :arrow="true"
            @click="openEdit"
          >
            <template #icon><UiIcon name="flag" :size="36" color="#FFFFFF" /></template>
          </CellItem>
          <CellItem
            :title="$t('chatInfo.clearHistory')"
            :border="false"
            @click="clearHistory"
          >
            <template #icon><UiIcon name="clock" :size="36" color="#FFFFFF" /></template>
          </CellItem>
        </template>
      </view>

      <!-- 危险操作 -->
      <view class="ci-danger-wrap">
        <view
          v-if="!isGroup"
          class="ci-danger"
          hover-class="ci-danger--hover"
          hover-stay-time="60"
          @click="removeFriend"
        >
          <UiIcon name="trash" :size="32" color="var(--danger-color)" />
          <text>{{ $t('chatInfo.deleteFriend') }}</text>
        </view>
        <template v-else>
          <view
            v-if="isCreator"
            class="ci-danger"
            hover-class="ci-danger--hover"
            hover-stay-time="60"
            @click="removeGroup"
          >
            <UiIcon name="trash" :size="32" color="var(--danger-color)" />
            <text>{{ $t('chatInfo.dismissGroup') }}</text>
          </view>
          <view
            v-else
            class="ci-danger"
            hover-class="ci-danger--hover"
            hover-stay-time="60"
            @click="leaveGroup"
          >
            <UiIcon name="logout" :size="32" color="var(--danger-color)" />
            <text>{{ $t('chatInfo.leaveGroup') }}</text>
          </view>
        </template>
      </view>

      <view class="safe-bottom-space--sm"></view>
    </scroll-view>

    <!-- 群编辑弹层 -->
    <view v-if="showEditSheet" class="ci-mask" @click="showEditSheet = false">
      <view class="ci-sheet" @click.stop>
        <view class="ci-sheet__handle"></view>
        <view class="ci-sheet__header">
          <text>{{ $t('chatInfo.editTitle') }}</text>
          <view class="ci-sheet__close" @click="showEditSheet = false">
            <UiIcon name="close" :size="32" color="var(--text-tertiary)" />
          </view>
        </view>
        <view class="ci-sheet__body">
          <view class="ci-edit-avatar" @click="chooseAvatar">
            <UiAvatar
              shape="square"
              :size="132"
              :src="avatarLocalPath || info.avatar"
              :name="displayName"
            />
            <view class="ci-edit-avatar__badge">
              <UiIcon name="camera" :size="26" color="var(--accent-contrast)" />
            </view>
          </view>

          <view class="ci-field">
            <text class="ci-field__label">{{ $t('chatInfo.groupName') }}</text>
            <input
              class="ci-field__input"
              v-model="editName"
              :placeholder="$t('chatInfo.groupNamePlaceholder')"
              placeholder-class="ci-field__ph"
              maxlength="20"
            />
          </view>

          <view class="ci-field">
            <text class="ci-field__label">{{ $t('chatInfo.groupNotice') }}</text>
            <textarea
              class="ci-field__textarea"
              v-model="editDescription"
              :placeholder="$t('chatInfo.groupNoticePlaceholder')"
              placeholder-class="ci-field__ph"
              maxlength="200"
            />
          </view>
        </view>
        <view class="ci-sheet__footer">
          <view
            class="ci-sheet__btn"
            hover-class="ci-sheet__btn--hover"
            hover-stay-time="60"
            @click="saveGroup"
          >
            <text>{{ $t('common.save') }}</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 邀请弹层 -->
    <view v-if="showInviteSheet" class="ci-mask" @click="showInviteSheet = false">
      <view class="ci-sheet ci-sheet--tall" @click.stop>
        <view class="ci-sheet__handle"></view>
        <view class="ci-sheet__header">
          <text>{{ $t('chatInfo.inviteTitle') }}</text>
          <view class="ci-sheet__close" @click="showInviteSheet = false">
            <UiIcon name="close" :size="32" color="var(--text-tertiary)" />
          </view>
        </view>
        <FriendPicker v-model="selected" :exclude-ids="memberIds" />
        <view class="ci-sheet__footer">
          <view
            class="ci-sheet__btn"
            hover-class="ci-sheet__btn--hover"
            hover-stay-time="60"
            @click="confirmInvite"
          >
            <text>{{ $t('chatInfo.confirm') }}</text>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import CustomNavBar from '../../components/CustomNavBar.vue'
import UiIcon from '../../components/Icon.vue'
import UiAvatar from '../../components/Avatar.vue'
import CellItem from '../../components/CellItem.vue'
import FriendPicker from '../../components/FriendPicker.vue'
import {
  getGroupById,
  getGroupMembers,
  deleteFriend,
  deleteGroup,
  removeGroupMember,
  updateGroup,
  updateFriendRemark,
  inviteFriendsToGroup
} from '../../utils/chat-api.js'
import { getUserId } from '../../utils/session.js'
import { removeConversation } from '../../store/index.js'

export default {
  components: { CustomNavBar, UiIcon, UiAvatar, CellItem, FriendPicker },
  data() {
    return {
      type: 'group',
      id: '',
      info: {
        id: '',
        name: '',
        avatar: '',
        remark: '',
        description: '',
        creatorId: '',
        memberCount: 0
      },
      members: [],
      selected: [],
      showEditSheet: false,
      showInviteSheet: false,
      editName: '',
      editDescription: '',
      avatarLocalPath: ''
    }
  },
  computed: {
    isGroup() {
      return this.type === 'group'
    },
    displayName() {
      if (this.info.name) return this.info.name
      return this.isGroup ? this.$t('common.groupChat') : this.$t('common.chat')
    },
    memberCount() {
      return this.members.length || this.info.memberCount || 0
    },
    isCreator() {
      return String(this.info.creatorId) === String(getUserId())
    },
    memberIds() {
      return this.members.map((m) => m.id)
    }
  },
  onLoad(options) {
    this.type = options.type || 'single'
    this.id = options.id || ''
    this.info = {
      ...this.info,
      id: this.id,
      name: decodeURIComponent(options.name || ''),
      avatar: decodeURIComponent(options.avatar || '')
    }

    if (this.isGroup) {
      this.loadGroupInfo()
      this.loadMembers()
    }
  },
  methods: {
    normalizeMember(m) {
      return {
        id: m.userId || m.id || m.friendId,
        name: m.remark || m.nickname || m.username || m.name || this.$t('chatInfo.memberFallback'),
        avatar: m.avatar || ''
      }
    },
    async loadGroupInfo() {
      try {
        const res = await getGroupById(this.id)
        if (res && res.code === 200 && res.data) {
          const data = res.data
          this.info = {
            ...this.info,
            name: data.name || this.info.name,
            description: data.description || '',
            creatorId: data.creatorId || '',
            memberCount: data.memberCount || 0
          }
        }
      } catch (e) {
        console.error('加载群信息失败:', e)
      }
    },
    async loadMembers() {
      try {
        const res = await getGroupMembers(this.id)
        if (res && res.code === 200 && Array.isArray(res.data)) {
          this.members = res.data.map((m) => this.normalizeMember(m))
        }
      } catch (e) {
        console.error('加载群成员失败:', e)
      }
    },
    goBack() {
      uni.navigateBack()
    },
    comingSoon(name) {
      uni.showToast({ title: this.$t('chatInfo.comingSoon', { name }), icon: 'none' })
    },
    editRemark() {
      uni.showModal({
        title: this.$t('chatInfo.remark'),
        editable: true,
        placeholderText: this.$t('chatInfo.remarkPlaceholder'),
        content: this.info.remark || '',
        success: async (res) => {
          if (!res.confirm) return
          const remark = (res.content || '').trim()
          try {
            const result = await updateFriendRemark(getUserId(), this.id, remark)
            if (result && result.code === 200) {
              this.info.remark = remark
              uni.showToast({ title: this.$t('chatInfo.saved'), icon: 'success' })
            } else {
              uni.showToast({
                title: (result && result.message) || this.$t('chatInfo.saveFailed'),
                icon: 'none'
              })
            }
          } catch (e) {
            console.error('修改备注失败:', e)
            uni.showToast({ title: this.$t('chatInfo.saveFailed'), icon: 'none' })
          }
        }
      })
    },
    openMember(member) {
      if (!member.id || String(member.id) === String(getUserId())) return
      uni.navigateTo({
        url: `/pages/contact-detail/contact-detail?type=single&id=${member.id}&name=${encodeURIComponent(
          member.name
        )}&avatar=${encodeURIComponent(member.avatar || '')}`
      })
    },
    openEdit() {
      this.editName = this.info.name || ''
      this.editDescription = this.info.description || ''
      this.avatarLocalPath = ''
      this.showEditSheet = true
    },
    chooseAvatar() {
      uni.chooseImage({
        count: 1,
        success: (res) => {
          this.avatarLocalPath = res.tempFilePaths[0]
        }
      })
    },
    async saveGroup() {
      const name = (this.editName || '').trim()
      const description = (this.editDescription || '').trim()
      if (!name) {
        uni.showToast({ title: this.$t('chatInfo.nameRequired'), icon: 'none' })
        return
      }
      try {
        const result = await updateGroup(
          { id: this.id, name, description },
          this.avatarLocalPath || ''
        )
        if (result && result.code === 200) {
          this.info.name = name
          this.info.description = description
          if (this.avatarLocalPath) this.info.avatar = this.avatarLocalPath
          this.showEditSheet = false
          uni.showToast({ title: this.$t('chatInfo.saved'), icon: 'success' })
        } else {
          uni.showToast({
            title: (result && result.message) || this.$t('chatInfo.saveFailed'),
            icon: 'none'
          })
        }
      } catch (e) {
        console.error('保存群资料失败:', e)
        uni.showToast({ title: this.$t('chatInfo.saveFailed'), icon: 'none' })
      }
    },
    openInvite() {
      this.selected = []
      this.showInviteSheet = true
    },
    async confirmInvite() {
      if (!this.selected.length) {
        uni.showToast({ title: this.$t('chatInfo.selectFriend'), icon: 'none' })
        return
      }
      try {
        const result = await inviteFriendsToGroup(
          this.id,
          this.selected.map((f) => f.id)
        )
        if (result && result.code === 200) {
          this.showInviteSheet = false
          uni.showToast({ title: this.$t('chatInfo.inviteSent'), icon: 'success' })
          this.loadMembers()
        } else {
          uni.showToast({
            title: (result && result.message) || this.$t('chatInfo.inviteFailed'),
            icon: 'none'
          })
        }
      } catch (e) {
        console.error('邀请好友失败:', e)
        uni.showToast({ title: this.$t('chatInfo.inviteFailed'), icon: 'none' })
      }
    },
    clearHistory() {
      uni.showModal({
        title: this.$t('common.tip'),
        content: this.$t('chatInfo.clearConfirm'),
        success: (res) => {
          if (!res.confirm) return
          removeConversation(this.type, this.id)
          uni.showToast({ title: this.$t('chatInfo.cleared'), icon: 'success' })
        }
      })
    },
    removeFriend() {
      uni.showModal({
        title: this.$t('common.tip'),
        content: this.$t('chatInfo.deleteConfirm'),
        success: async (res) => {
          if (!res.confirm) return
          try {
            const result = await deleteFriend(getUserId(), this.id)
            if (result && result.code === 200) {
              uni.showToast({ title: this.$t('chatInfo.deleted'), icon: 'success' })
              setTimeout(() => uni.switchTab({ url: '/pages/index/index' }), 800)
            } else {
              uni.showToast({
                title: (result && result.message) || this.$t('chatInfo.deleteFailed'),
                icon: 'none'
              })
            }
          } catch (e) {
            console.error('删除好友失败:', e)
            uni.showToast({ title: this.$t('chatInfo.deleteFailed'), icon: 'none' })
          }
        }
      })
    },
    removeGroup() {
      uni.showModal({
        title: this.$t('common.tip'),
        content: this.$t('chatInfo.dismissConfirm'),
        success: async (res) => {
          if (!res.confirm) return
          try {
            const result = await deleteGroup(this.id)
            if (result && result.code === 200) {
              uni.showToast({ title: this.$t('chatInfo.dismissed'), icon: 'success' })
              setTimeout(() => uni.switchTab({ url: '/pages/index/index' }), 800)
            } else {
              uni.showToast({
                title: (result && result.message) || this.$t('chatInfo.dismissFailed'),
                icon: 'none'
              })
            }
          } catch (e) {
            console.error('解散群聊失败:', e)
            uni.showToast({ title: this.$t('chatInfo.dismissFailed'), icon: 'none' })
          }
        }
      })
    },
    leaveGroup() {
      uni.showModal({
        title: this.$t('common.tip'),
        content: this.$t('chatInfo.leaveConfirm'),
        success: async (res) => {
          if (!res.confirm) return
          try {
            const result = await removeGroupMember(this.id, getUserId())
            if (result && result.code === 200) {
              uni.showToast({ title: this.$t('chatInfo.left'), icon: 'success' })
              setTimeout(() => uni.switchTab({ url: '/pages/index/index' }), 800)
            } else {
              uni.showToast({
                title: (result && result.message) || this.$t('chatInfo.leaveFailed'),
                icon: 'none'
              })
            }
          } catch (e) {
            console.error('退出群聊失败:', e)
            uni.showToast({ title: this.$t('chatInfo.leaveFailed'), icon: 'none' })
          }
        }
      })
    }
  }
}
</script>

<style scoped>
.ci-page {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background-color: var(--bg-secondary);
  background-image: radial-gradient(120% 46% at 50% 0%, var(--accent-light) 0%, transparent 60%);
  color: var(--text-primary);
}

.ci-scroll {
  flex: 1;
  height: 0;
}

.block-label {
  display: block;
  font-size: var(--font-xs);
  font-weight: 600;
  letter-spacing: 2rpx;
  text-transform: uppercase;
  color: var(--text-tertiary);
}

/* 资料头部 */
.ci-hero {
  position: relative;
  margin: 24rpx 24rpx 0;
  padding: 44rpx 32rpx 32rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.ci-hero__glow {
  position: absolute;
  top: -140rpx;
  left: 50%;
  transform: translateX(-50%);
  width: 460rpx;
  height: 320rpx;
  border-radius: 50%;
  background-color: var(--accent-light);
  opacity: 0.8;
  pointer-events: none;
}

.ci-hero__avatar {
  position: relative;
  z-index: 1;
}

.ci-hero__name {
  position: relative;
  z-index: 1;
  margin-top: 24rpx;
  max-width: 100%;
  font-size: var(--font-xxl);
  font-weight: 700;
  letter-spacing: -0.4rpx;
  color: var(--text-primary);
  text-align: center;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.ci-hero__chips {
  position: relative;
  z-index: 1;
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 12rpx;
  margin-top: 16rpx;
  max-width: 100%;
}

.ci-hero__chips .chip {
  max-width: 420rpx;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* 群成员网格 */
.members-block {
  padding-bottom: 10rpx;
}

.members-block__head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 26rpx 32rpx 4rpx;
}

.members-block__count {
  font-size: var(--font-xs);
  color: var(--text-tertiary);
  font-family: var(--font-mono);
}

.ci-members {
  display: flex;
  flex-wrap: wrap;
  padding: 8rpx 16rpx 8rpx;
}

.ci-member {
  width: 20%;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 14rpx 0 18rpx;
}

.ci-member:active {
  opacity: 0.7;
}

.ci-member__name {
  max-width: 120rpx;
  margin-top: 10rpx;
  font-size: var(--font-xs);
  color: var(--text-secondary);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.ci-member__name--accent {
  color: var(--accent-color);
  font-weight: 500;
}

.ci-member__plus {
  width: 96rpx;
  height: 96rpx;
  border-radius: var(--radius-lg);
  border: 2rpx dashed var(--border-color);
  background-color: var(--bg-chip);
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 设置项 */
.ci-label {
  padding: 32rpx 40rpx 12rpx;
}

.ci-block {
  margin: 0 24rpx;
}

/* 危险操作 */
.ci-danger-wrap {
  margin: 24rpx 24rpx 0;
}

.ci-danger {
  height: 92rpx;
  border-radius: var(--radius-lg);
  background-color: var(--danger-light);
  border: 1rpx solid var(--danger-color);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10rpx;
  transition: opacity var(--transition-fast), transform var(--transition-fast);
}

.ci-danger text {
  font-size: var(--font-md);
  font-weight: 600;
  color: var(--danger-color);
}

.ci-danger--hover {
  opacity: 0.85;
  transform: scale(0.99);
}

/* 弹层 */
.ci-mask {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: var(--bg-mask);
  z-index: 200;
  display: flex;
  align-items: flex-end;
}

.ci-sheet {
  width: 100%;
  background-color: var(--bg-primary);
  border-radius: var(--radius-xl) var(--radius-xl) 0 0;
  border-top: 1rpx solid var(--glass-border);
  box-shadow: var(--shadow-xl);
  padding-bottom: env(safe-area-inset-bottom);
  animation: ciSlideUp 0.24s ease;
  display: flex;
  flex-direction: column;
  max-height: 88vh;
}

.ci-sheet--tall {
  height: 72vh;
}

.ci-sheet__handle {
  width: 76rpx;
  height: 8rpx;
  margin: 16rpx auto 0;
  border-radius: 999rpx;
  background-color: var(--border-color);
}

.ci-sheet__header {
  position: relative;
  height: 84rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: var(--font-md);
  font-weight: 600;
  color: var(--text-primary);
}

.ci-sheet__close {
  position: absolute;
  right: 20rpx;
  top: 50%;
  transform: translateY(-50%);
  padding: 10rpx;
}

.ci-sheet__body {
  padding: 16rpx 32rpx 32rpx;
  flex: 1;
  overflow-y: auto;
}

.ci-sheet__footer {
  padding: 16rpx 32rpx 20rpx;
  border-top: 1rpx solid var(--divider);
}

.ci-sheet__btn {
  height: 88rpx;
  border-radius: var(--radius-md);
  background-image: var(--accent-gradient);
  box-shadow: var(--accent-glow);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: opacity var(--transition-fast), transform var(--transition-fast);
}

.ci-sheet__btn text {
  color: var(--accent-contrast);
  font-size: var(--font-md);
  font-weight: 600;
}

.ci-sheet__btn--hover {
  opacity: 0.9;
  transform: scale(0.99);
}

/* 编辑表单 */
.ci-edit-avatar {
  position: relative;
  width: 132rpx;
  margin: 8rpx auto 32rpx;
}

.ci-edit-avatar__badge {
  position: absolute;
  right: -10rpx;
  bottom: -10rpx;
  width: 48rpx;
  height: 48rpx;
  border-radius: 50%;
  background-image: var(--accent-gradient);
  box-shadow: var(--shadow-sm);
  display: flex;
  align-items: center;
  justify-content: center;
}

.ci-field {
  margin-bottom: 24rpx;
}

.ci-field__label {
  display: block;
  margin-bottom: 10rpx;
  font-size: var(--font-xs);
  font-weight: 600;
  letter-spacing: 1rpx;
  color: var(--text-tertiary);
}

.ci-field__input,
.ci-field__textarea {
  width: 100%;
  box-sizing: border-box;
  background-color: var(--bg-input);
  border: 1rpx solid var(--border-light);
  border-radius: var(--radius-md);
  font-size: var(--font-base);
  color: var(--text-primary);
}

.ci-field__input {
  height: 84rpx;
  padding: 0 24rpx;
}

.ci-field__textarea {
  height: 168rpx;
  padding: 18rpx 24rpx;
  line-height: 1.5;
}

.ci-field__ph {
  color: var(--text-placeholder);
}

@keyframes ciSlideUp {
  from {
    transform: translateY(100%);
  }
  to {
    transform: translateY(0);
  }
}
</style>
