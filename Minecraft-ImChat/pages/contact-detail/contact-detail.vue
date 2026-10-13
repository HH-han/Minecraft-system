<template>
  <view class="cd-page">
    <CustomNavBar
      :show-back="true"
      title="资料"
      title-align="center"
      :show-add="true"
      :menu-items="menuItems"
      @back="goBack"
      @menuItemClick="onMenu"
    />

    <scroll-view class="cd-scroll" scroll-y :show-scrollbar="false">
      <!-- ===== 单聊视图 ===== -->
      <block v-if="!isGroup">
        <view class="cd-profile">
          <UiAvatar shape="square" :size="160" :src="info.avatar" :name="info.name" />
          <view class="cd-profile__info">
            <view class="cd-profile__name-row">
              <text class="cd-profile__name">{{ info.name || '未知用户' }}</text>
              <text v-if="genderText" class="cd-gender" :class="info.gender">{{ genderText }}</text>
            </view>
            <text v-if="info.account" class="cd-profile__line">微信号：{{ info.account }}</text>
            <text v-if="info.username && info.username !== info.name" class="cd-profile__line">
              昵称：{{ info.username }}
            </text>
            <text v-if="info.region" class="cd-profile__line">地区：{{ info.region }}</text>
          </view>
          <view class="cd-star" @click="onToggleStar">
            <UiIcon
              :name="starred ? 'star-filled' : 'star'"
              :size="48"
              :color="starred ? '#F5B800' : '#CCCCCC'"
            />
          </view>
        </view>

        <view class="cd-group">
          <CellItem
            title="设置备注"
            :value="info.remark || '未设置'"
            :arrow="true"
            @click="editRemark"
          />
          <CellItem title="朋友圈" :arrow="true" @click="openMoments" />
          <CellItem
            v-if="info.phone"
            title="电话"
            :value="phoneText"
            :arrow="true"
            @click="callPhone"
          />
          <CellItem
            v-if="tagsText"
            title="标签"
            :value="tagsText"
            :border="false"
            :clickable="false"
          />
        </view>

        <view class="cd-group">
          <view class="cd-action cd-action--primary" @click="sendMessage">
            <text>发消息</text>
          </view>
          <view class="cd-action cd-action--ghost" @click="comingSoon('音视频通话')">
            <text>音视频通话</text>
          </view>
        </view>

        <view class="cd-group">
          <view class="cd-danger" @click="removeFriend">
            <text>删除好友</text>
          </view>
        </view>
      </block>

      <!-- ===== 群聊视图 ===== -->
      <block v-else>
        <view class="cd-profile">
          <UiAvatar shape="square" :size="160" :src="info.avatar" :name="info.name" />
          <view class="cd-profile__info">
            <text class="cd-profile__name">{{ info.name || '群聊' }}</text>
            <text class="cd-profile__line">{{ memberCount }}位成员</text>
          </view>
        </view>

        <view class="cd-group cd-group--members">
          <view class="cd-members">
            <view
              v-for="member in members"
              :key="member.id"
              class="cd-member"
              @click="openMember(member)"
            >
              <UiAvatar shape="square" :size="96" :src="member.avatar" :name="member.name" />
              <text class="cd-member__name">{{ member.name }}</text>
            </view>
            <view class="cd-member" @click="inviteMembers">
              <view class="cd-member__plus">
                <UiIcon name="plus" :size="48" color="#B2B2B2" />
              </view>
              <text class="cd-member__name">邀请</text>
            </view>
          </view>
        </view>

        <view class="cd-group">
          <CellItem title="群聊名称" :value="info.name" :arrow="true" @click="comingSoon('修改群聊名称')" />
          <CellItem
            title="群公告"
            :value="info.description || '未设置'"
            :arrow="true"
            @click="comingSoon('群公告')"
          />
          <CellItem
            title="查找聊天记录"
            :arrow="true"
            :border="false"
            @click="comingSoon('查找聊天记录')"
          />
        </view>

        <view class="cd-group">
          <view class="cd-action cd-action--primary" @click="sendMessage">
            <text>发消息</text>
          </view>
        </view>

        <view class="cd-group">
          <view v-if="info.isCreator" class="cd-danger" @click="removeGroup">
            <text>解散群聊</text>
          </view>
          <view v-else class="cd-danger" @click="leaveGroup">
            <text>退出群聊</text>
          </view>
        </view>
      </block>

      <view class="cd-safe"></view>
    </scroll-view>
  </view>
</template>

<script>
import CustomNavBar from '../../components/CustomNavBar.vue'
import UiIcon from '../../components/Icon.vue'
import UiAvatar from '../../components/Avatar.vue'
import CellItem from '../../components/CellItem.vue'
import {
  getGroupById,
  getGroupMembers,
  getUserByAccount,
  deleteFriend,
  deleteGroup,
  removeGroupMember,
  updateFriendRemark
} from '../../utils/chat-api.js'
import { getUserId } from '../../utils/session.js'
import { maskPhone } from '../../utils/format.js'
import { isStarred, toggleStar } from '../../store/index.js'

export default {
  components: { CustomNavBar, UiIcon, UiAvatar, CellItem },
  data() {
    return {
      type: 'single',
      id: '',
      info: {
        id: '',
        name: '',
        avatar: '',
        account: '',
        username: '',
        region: '',
        phone: '',
        gender: '',
        memo: '',
        remark: '',
        description: '',
        isCreator: false,
        tags: []
      },
      members: [],
      memberCount: 0,
      starred: false,
      menuItems: [
        { text: '投诉', action: 'complain' },
        { text: '加入黑名单', action: 'block' }
      ]
    }
  },
  computed: {
    isGroup() {
      return this.type === 'group'
    },
    genderText() {
      if (this.info.gender === 'male' || this.info.gender === 1 || this.info.gender === '1') return '男'
      if (this.info.gender === 'female' || this.info.gender === 2 || this.info.gender === '2') return '女'
      return ''
    },
    phoneText() {
      return maskPhone(this.info.phone)
    },
    tagsText() {
      const tags = this.info.tags
      if (Array.isArray(tags) && tags.length) return tags.join('，')
      if (typeof tags === 'string' && tags.trim()) return tags
      return ''
    }
  },
  onLoad(options) {
    this.type = options.type || 'single'
    this.id = options.id || ''
    this.info = {
      id: this.id,
      name: decodeURIComponent(options.name || ''),
      avatar: decodeURIComponent(options.avatar || ''),
      account: decodeURIComponent(options.account || ''),
      username: decodeURIComponent(options.username || ''),
      region: decodeURIComponent(options.region || ''),
      phone: decodeURIComponent(options.phone || ''),
      gender: options.gender || '',
      memo: decodeURIComponent(options.memo || ''),
      remark: decodeURIComponent(options.remark || ''),
      description: '',
      isCreator: options.isCreator === 'true',
      tags: []
    }
    if (options.tags) {
      try {
        this.info.tags = JSON.parse(decodeURIComponent(options.tags))
      } catch (e) {
        this.info.tags = []
      }
    }

    this.starred = isStarred(this.id)

    if (this.isGroup) {
      this.loadGroupInfo()
      this.loadMembers()
    } else {
      this.loadFriendDetail()
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
    async loadFriendDetail() {
      if (!this.info.account) return
      try {
        const res = await getUserByAccount(this.info.account)
        if (res && res.code === 200 && res.data) {
          this.info = { ...this.info, ...res.data, id: this.id }
        }
      } catch (e) {
        console.error('加载好友详情失败:', e)
      }
    },
    async loadGroupInfo() {
      try {
        const res = await getGroupById(this.id)
        if (res && res.code === 200 && res.data) {
          const data = res.data
          const isCreator = this.info.isCreator || String(data.creatorId) === String(getUserId())
          this.info = {
            ...this.info,
            name: data.name || this.info.name,
            avatar: data.avatar || this.info.avatar,
            description: data.description || '',
            isCreator
          }
          this.memberCount = data.memberCount || this.memberCount
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
          this.memberCount = this.members.length
        }
      } catch (e) {
        console.error('加载群成员失败:', e)
      }
    },
    goBack() {
      uni.navigateBack()
    },
    onMenu(item) {
      uni.showToast({ title: `${item.text}功能开发中`, icon: 'none' })
    },
    onToggleStar() {
      this.starred = toggleStar(this.id)
      uni.showToast({ title: this.starred ? '已设为星标朋友' : '已取消星标', icon: 'none' })
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
    openMoments() {
      uni.navigateTo({
        url: `/pages/moments/moments?userId=${this.id}&name=${encodeURIComponent(this.info.name || '')}`
      })
    },
    callPhone() {
      uni.showToast({ title: '功能开发中', icon: 'none' })
    },
    sendMessage() {
      if (this.isGroup) {
        uni.navigateTo({
          url: `/pages/chat/chat?type=group&id=${this.id}&name=${encodeURIComponent(
            this.info.name || '群聊'
          )}&avatar=${encodeURIComponent(this.info.avatar || '')}`
        })
        return
      }
      const pages = getCurrentPages()
      const index = pages.findIndex((p) => p.route && p.route.includes('pages/chat/chat'))
      if (index > -1) {
        uni.navigateBack({ delta: pages.length - index - 1 })
        return
      }
      uni.navigateTo({
        url: `/pages/chat/chat?type=single&id=${this.id}&name=${encodeURIComponent(
          this.info.name || ''
        )}&avatar=${encodeURIComponent(this.info.avatar || '')}`
      })
    },
    inviteMembers() {
      uni.navigateTo({
        url: `/pages/chat-info/chat-info?type=group&id=${this.id}&name=${encodeURIComponent(
          this.info.name || '群聊'
        )}&avatar=${encodeURIComponent(this.info.avatar || '')}`
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
    comingSoon(name) {
      uni.showToast({ title: `${name}功能开发中`, icon: 'none' })
    },
    removeFriend() {
      uni.showModal({
        title: '提示',
        content: '删除后将同时删除与该好友的聊天记录',
        success: async (res) => {
          if (!res.confirm) return
          try {
            const result = await deleteFriend(getUserId(), this.id)
            if (result && result.code === 200) {
              uni.showToast({ title: '已删除', icon: 'success' })
              setTimeout(() => uni.navigateBack(), 800)
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
.cd-page {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background-color: #ededed;
}

.cd-scroll {
  flex: 1;
  height: 0;
}

/* 资料卡 */
.cd-profile {
  display: flex;
  align-items: center;
  padding: 40rpx 32rpx;
  background-color: #ffffff;
  margin-bottom: 16rpx;
}

.cd-profile__info {
  flex: 1;
  min-width: 0;
  margin-left: 28rpx;
}

.cd-profile__name-row {
  display: flex;
  align-items: center;
  gap: 12rpx;
}

.cd-profile__name {
  font-size: 40rpx;
  font-weight: 600;
  color: #191919;
  line-height: 1.3;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.cd-gender {
  flex-shrink: 0;
  height: 32rpx;
  padding: 0 12rpx;
  border-radius: 16rpx;
  font-size: 20rpx;
  line-height: 32rpx;
  color: #ffffff;
  background-color: #b2b2b2;
}

.cd-gender.male,
.cd-gender[class~='1'] {
  background-color: #4a90e2;
}

.cd-gender.female,
.cd-gender[class~='2'] {
  background-color: #f55a8a;
}

.cd-profile__line {
  display: block;
  font-size: 26rpx;
  color: #888888;
  margin-top: 8rpx;
}

.cd-star {
  width: 72rpx;
  height: 72rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-left: 16rpx;
}

.cd-star:active {
  opacity: 0.6;
}

/* 分组 */
.cd-group {
  background-color: #ffffff;
  margin-bottom: 16rpx;
}

.cd-group--members {
  padding: 24rpx 0 8rpx;
}

/* 操作按钮 */
.cd-action {
  height: 96rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 24rpx 32rpx;
  border-radius: 12rpx;
}

.cd-action text {
  font-size: 32rpx;
}

.cd-action--primary {
  background-color: #07c160;
}

.cd-action--primary:active {
  background-color: #06ad56;
}

.cd-action--primary text {
  color: #ffffff;
  font-weight: 500;
}

.cd-action--ghost {
  margin-top: 0;
  background-color: #f7f7f7;
}

.cd-action--ghost:active {
  background-color: #ececec;
}

.cd-action--ghost text {
  color: #191919;
}

/* 危险操作 */
.cd-danger {
  height: 96rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.cd-danger:active {
  background-color: #f7f7f7;
}

.cd-danger text {
  font-size: 32rpx;
  color: #fa5151;
}

/* 群成员网格 */
.cd-members {
  display: flex;
  flex-wrap: wrap;
  padding: 0 12rpx;
}

.cd-member {
  width: 20%;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 12rpx 0 20rpx;
}

.cd-member__name {
  max-width: 120rpx;
  margin-top: 10rpx;
  font-size: 22rpx;
  color: #888888;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.cd-member__plus {
  width: 96rpx;
  height: 96rpx;
  border-radius: 16rpx;
  border: 2rpx dashed #c8c8cd;
  display: flex;
  align-items: center;
  justify-content: center;
}

.cd-member:active .cd-member__plus {
  background-color: #f7f7f7;
}

.cd-safe {
  height: env(safe-area-inset-bottom);
}
</style>
