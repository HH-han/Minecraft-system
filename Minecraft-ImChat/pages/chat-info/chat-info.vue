<template>
  <view class="ci-page">
    <CustomNavBar
      :show-back="true"
      :title="pageTitle"
      title-align="center"
      @back="goBack"
    />

    <scroll-view class="ci-scroll" scroll-y :show-scrollbar="false">
      <!-- 顶部资料 -->
      <view class="ci-profile">
        <UiAvatar shape="square" :size="96" :src="info.avatar" :name="info.name" />
        <view class="ci-profile__info">
          <text class="ci-profile__name">{{ info.name || (isGroup ? '群聊' : '聊天') }}</text>
          <text v-if="isGroup" class="ci-profile__sub">{{ memberCount }}位成员</text>
        </view>
      </view>

      <!-- 群成员网格 -->
      <view v-if="isGroup" class="ci-group ci-group--members">
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
              <UiIcon name="plus" :size="48" color="#B2B2B2" />
            </view>
            <text class="ci-member__name">邀请</text>
          </view>
        </view>
      </view>

      <!-- 分组列表 -->
      <view class="ci-group">
        <template v-if="!isGroup">
          <CellItem title="设置备注" :value="info.remark || '未设置'" :arrow="true" @click="editRemark" />
          <CellItem title="查找聊天记录" :arrow="true" @click="comingSoon('查找聊天记录')" />
          <CellItem title="清空聊天记录" :border="false" @click="clearHistory" />
        </template>
        <template v-else>
          <CellItem title="群聊名称" :value="info.name" :arrow="true" @click="openEdit" />
          <CellItem
            title="群公告"
            :value="info.description || '未设置'"
            :arrow="true"
            @click="openEdit"
          />
          <CellItem title="清空聊天记录" :border="false" @click="clearHistory" />
        </template>
      </view>

      <!-- 危险操作 -->
      <view class="ci-group">
        <template v-if="!isGroup">
          <view class="ci-danger" @click="removeFriend">
            <text>删除好友</text>
          </view>
        </template>
        <template v-else>
          <view v-if="isCreator" class="ci-danger" @click="removeGroup">
            <text>解散群聊</text>
          </view>
          <view v-else class="ci-danger" @click="leaveGroup">
            <text>退出群聊</text>
          </view>
        </template>
      </view>

      <view class="ci-safe"></view>
    </scroll-view>

    <!-- 群编辑弹层 -->
    <view v-if="showEditSheet" class="ci-mask" @click="showEditSheet = false">
      <view class="ci-sheet" @click.stop>
        <view class="ci-sheet__header">
          <text>编辑群资料</text>
        </view>
        <view class="ci-sheet__body">
          <view class="ci-edit-avatar" @click="chooseAvatar">
            <UiAvatar
              shape="square"
              :size="140"
              :src="avatarLocalPath || info.avatar"
              :name="info.name"
            />
            <view class="ci-edit-avatar__mask">
              <UiIcon name="camera" :size="40" color="#FFFFFF" />
            </view>
          </view>

          <view class="ci-field">
            <text class="ci-field__label">群名称</text>
            <input
              class="ci-field__input"
              v-model="editName"
              placeholder="请输入群名称"
              placeholder-class="ci-field__ph"
              maxlength="20"
            />
          </view>

          <view class="ci-field">
            <text class="ci-field__label">群公告</text>
            <textarea
              class="ci-field__textarea"
              v-model="editDescription"
              placeholder="请输入群公告"
              placeholder-class="ci-field__ph"
              maxlength="200"
            />
          </view>
        </view>
        <view class="ci-sheet__footer">
          <view class="ci-sheet__btn ci-sheet__btn--primary" @click="saveGroup">
            <text>保存</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 邀请弹层 -->
    <view v-if="showInviteSheet" class="ci-mask" @click="showInviteSheet = false">
      <view class="ci-sheet ci-sheet--tall" @click.stop>
        <view class="ci-sheet__header">
          <text>邀请好友</text>
          <view class="ci-sheet__close" @click="showInviteSheet = false">
            <UiIcon name="close" :size="36" color="#888888" />
          </view>
        </view>
        <FriendPicker v-model="selected" :exclude-ids="memberIds" />
        <view class="ci-sheet__footer">
          <view class="ci-sheet__btn ci-sheet__btn--primary" @click="confirmInvite">
            <text>确定</text>
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
    pageTitle() {
      return '聊天信息'
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
        name: m.remark || m.nickname || m.username || m.name || '成员',
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
      uni.showToast({ title: `${name}功能开发中`, icon: 'none' })
    },
    editRemark() {
      uni.showModal({
        title: '设置备注',
        editable: true,
        placeholderText: '请输入备注',
        content: this.info.remark || '',
        success: async (res) => {
          if (!res.confirm) return
          const remark = (res.content || '').trim()
          try {
            const result = await updateFriendRemark(getUserId(), this.id, remark)
            if (result && result.code === 200) {
              this.info.remark = remark
              uni.showToast({ title: '已保存', icon: 'success' })
            } else {
              uni.showToast({ title: (result && result.message) || '保存失败', icon: 'none' })
            }
          } catch (e) {
            console.error('修改备注失败:', e)
            uni.showToast({ title: '保存失败', icon: 'none' })
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
        uni.showToast({ title: '请输入群名称', icon: 'none' })
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
          uni.showToast({ title: '已保存', icon: 'success' })
        } else {
          uni.showToast({ title: (result && result.message) || '保存失败', icon: 'none' })
        }
      } catch (e) {
        console.error('保存群资料失败:', e)
        uni.showToast({ title: '保存失败', icon: 'none' })
      }
    },
    openInvite() {
      this.selected = []
      this.showInviteSheet = true
    },
    async confirmInvite() {
      if (!this.selected.length) {
        uni.showToast({ title: '请选择好友', icon: 'none' })
        return
      }
      try {
        const result = await inviteFriendsToGroup(
          this.id,
          this.selected.map((f) => f.id)
        )
        if (result && result.code === 200) {
          this.showInviteSheet = false
          uni.showToast({ title: '已发送邀请', icon: 'success' })
          this.loadMembers()
        } else {
          uni.showToast({ title: (result && result.message) || '邀请失败', icon: 'none' })
        }
      } catch (e) {
        console.error('邀请好友失败:', e)
        uni.showToast({ title: '邀请失败', icon: 'none' })
      }
    },
    clearHistory() {
      uni.showModal({
        title: '提示',
        content: '确定清空聊天记录吗？',
        success: (res) => {
          if (!res.confirm) return
          removeConversation(this.type, this.id)
          uni.showToast({ title: '已清空', icon: 'success' })
        }
      })
    },
    removeFriend() {
      uni.showModal({
        title: '提示',
        content: '确定要删除该好友吗？',
        success: async (res) => {
          if (!res.confirm) return
          try {
            const result = await deleteFriend(getUserId(), this.id)
            if (result && result.code === 200) {
              uni.showToast({ title: '已删除', icon: 'success' })
              setTimeout(() => uni.switchTab({ url: '/pages/index/index' }), 800)
            } else {
              uni.showToast({ title: (result && result.message) || '删除失败', icon: 'none' })
            }
          } catch (e) {
            console.error('删除好友失败:', e)
            uni.showToast({ title: '删除失败', icon: 'none' })
          }
        }
      })
    },
    removeGroup() {
      uni.showModal({
        title: '提示',
        content: '确定要解散该群聊吗？',
        success: async (res) => {
          if (!res.confirm) return
          try {
            const result = await deleteGroup(this.id)
            if (result && result.code === 200) {
              uni.showToast({ title: '已解散', icon: 'success' })
              setTimeout(() => uni.switchTab({ url: '/pages/index/index' }), 800)
            } else {
              uni.showToast({ title: (result && result.message) || '解散失败', icon: 'none' })
            }
          } catch (e) {
            console.error('解散群聊失败:', e)
            uni.showToast({ title: '解散失败', icon: 'none' })
          }
        }
      })
    },
    leaveGroup() {
      uni.showModal({
        title: '提示',
        content: '确定要退出该群聊吗？',
        success: async (res) => {
          if (!res.confirm) return
          try {
            const result = await removeGroupMember(this.id, getUserId())
            if (result && result.code === 200) {
              uni.showToast({ title: '已退出', icon: 'success' })
              setTimeout(() => uni.switchTab({ url: '/pages/index/index' }), 800)
            } else {
              uni.showToast({ title: (result && result.message) || '退出失败', icon: 'none' })
            }
          } catch (e) {
            console.error('退出群聊失败:', e)
            uni.showToast({ title: '退出失败', icon: 'none' })
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
  background-color: #ededed;
}

.ci-scroll {
  flex: 1;
  height: 0;
}

/* 顶部资料 */
.ci-profile {
  display: flex;
  align-items: center;
  padding: 32rpx;
  background-color: #ffffff;
  margin-bottom: 16rpx;
}

.ci-profile__info {
  flex: 1;
  min-width: 0;
  margin-left: 24rpx;
}

.ci-profile__name {
  display: block;
  font-size: 36rpx;
  font-weight: 600;
  color: #191919;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.ci-profile__sub {
  display: block;
  font-size: 26rpx;
  color: #888888;
  margin-top: 8rpx;
}

/* 分组 */
.ci-group {
  background-color: #ffffff;
  margin-bottom: 16rpx;
}

.ci-group--members {
  padding: 24rpx 0 8rpx;
}

/* 群成员网格 */
.ci-members {
  display: flex;
  flex-wrap: wrap;
  padding: 0 12rpx;
}

.ci-member {
  width: 20%;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 12rpx 0 20rpx;
}

.ci-member:active {
  opacity: 0.7;
}

.ci-member__name {
  max-width: 120rpx;
  margin-top: 10rpx;
  font-size: 22rpx;
  color: #888888;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.ci-member__plus {
  width: 96rpx;
  height: 96rpx;
  border-radius: 16rpx;
  border: 2rpx dashed #c8c8cd;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 危险操作 */
.ci-danger {
  height: 96rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.ci-danger:active {
  background-color: #f7f7f7;
}

.ci-danger text {
  font-size: 32rpx;
  color: #fa5151;
}

.ci-safe {
  height: env(safe-area-inset-bottom);
}

/* 弹层 */
.ci-mask {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  z-index: 200;
  display: flex;
  align-items: flex-end;
}

.ci-sheet {
  width: 100%;
  background-color: #ffffff;
  border-radius: 24rpx 24rpx 0 0;
  padding-bottom: env(safe-area-inset-bottom);
  animation: ciSlideUp 0.24s ease;
  display: flex;
  flex-direction: column;
  max-height: 80vh;
}

.ci-sheet--tall {
  height: 72vh;
}

.ci-sheet__header {
  position: relative;
  height: 96rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32rpx;
  font-weight: 600;
  color: #191919;
  border-bottom: 1rpx solid #ededed;
}

.ci-sheet__close {
  position: absolute;
  right: 24rpx;
  top: 50%;
  transform: translateY(-50%);
  padding: 12rpx;
}

.ci-sheet__body {
  padding: 32rpx;
  flex: 1;
  overflow-y: auto;
}

.ci-sheet__footer {
  padding: 20rpx 32rpx;
  border-top: 1rpx solid #ededed;
}

.ci-sheet__btn {
  height: 88rpx;
  border-radius: 12rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.ci-sheet__btn--primary {
  background-color: #07c160;
}

.ci-sheet__btn--primary:active {
  background-color: #06ad56;
}

.ci-sheet__btn--primary text {
  color: #ffffff;
  font-size: 32rpx;
  font-weight: 500;
}

/* 编辑表单 */
.ci-edit-avatar {
  position: relative;
  width: 140rpx;
  margin: 0 auto 32rpx;
}

.ci-edit-avatar__mask {
  position: absolute;
  right: -8rpx;
  bottom: -8rpx;
  width: 52rpx;
  height: 52rpx;
  border-radius: 50%;
  background-color: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
}

.ci-field {
  margin-bottom: 28rpx;
}

.ci-field__label {
  display: block;
  font-size: 26rpx;
  color: #888888;
  margin-bottom: 12rpx;
}

.ci-field__input,
.ci-field__textarea {
  width: 100%;
  box-sizing: border-box;
  background-color: #f7f7f7;
  border-radius: 12rpx;
  font-size: 30rpx;
  color: #191919;
}

.ci-field__input {
  height: 88rpx;
  padding: 0 28rpx;
}

.ci-field__textarea {
  height: 180rpx;
  padding: 20rpx 28rpx;
  line-height: 1.5;
}

.ci-field__ph {
  color: #b2b2b2;
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
