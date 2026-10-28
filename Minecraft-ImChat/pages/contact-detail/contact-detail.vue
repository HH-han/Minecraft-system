<template>
  <view class="cd-page" :class="themeClass">
    <CustomNavBar
      :show-back="true"
      :title="$t('contactDetail.title')"
      title-align="center"
      :show-add="true"
      :menu-items="menuItems"
      @back="goBack"
      @menuItemClick="onMenu"
    />

    <scroll-view class="cd-scroll" scroll-y :show-scrollbar="false">
      <!-- ===== 单聊视图 ===== -->
      <block v-if="!isGroup">
        <view class="cd-hero grid-overlay">
          <view class="cd-hero__top">
            <UiAvatar
              shape="square"
              :size="160"
              :src="info.avatar"
              :name="info.name"
              :ring="starred"
            />
            <view class="cd-hero__head">
              <view class="cd-hero__name-row">
                <text class="cd-hero__name">{{ info.name || $t('common.unknownUser') }}</text>
                <text v-if="genderText" class="cd-gender" :class="info.gender">{{ genderText }}</text>
              </view>
              <view class="cd-hero__chips">
                <view v-if="starred" class="chip chip--accent">
                  <text class="cd-chip__text">{{ $t('contactDetail.starredTag') }}</text>
                </view>
                <view v-if="info.region" class="chip">
                  <text class="cd-chip__text">{{ info.region }}</text>
                </view>
              </view>
            </view>
            <view class="cd-star" hover-class="cd-star--hover" @click="onToggleStar">
              <UiIcon
                :name="starred ? 'star-filled' : 'star'"
                :size="40"
                :color="starred ? 'var(--warning-color)' : 'var(--text-tertiary)'"
              />
            </view>
          </view>

          <view v-if="metaRows.length" class="cd-meta">
            <view v-for="(row, index) in metaRows" :key="index" class="cd-meta__row">
              <text class="cd-meta__label">{{ row.label }}</text>
              <text class="cd-meta__value">{{ row.value }}</text>
            </view>
          </view>
        </view>

        <view class="cd-actions">
          <view class="cd-action cd-action--primary" hover-class="cd-action--hover" @click="sendMessage">
            <UiIcon name="message" :size="36" color="var(--accent-contrast)" />
            <text>{{ $t('contactDetail.sendMessage') }}</text>
          </view>
          <view
            class="cd-action cd-action--ghost"
            hover-class="cd-action--hover"
            @click="comingSoon($t('contactDetail.call'))"
          >
            <UiIcon name="video" :size="36" color="var(--text-secondary)" />
            <text>{{ $t('contactDetail.call') }}</text>
          </view>
        </view>

        <view class="section-label">{{ $t('contactDetail.infoSection') }}</view>

        <view class="group-block">
          <CellItem
            :title="$t('contactDetail.remark')"
            :value="info.remark || $t('common.notSet')"
            :arrow="true"
            @click="editRemark"
          />
          <CellItem
            :title="$t('contactDetail.moments')"
            :arrow="true"
            :border="!!(info.phone || tagsText)"
            @click="openMoments"
          />
          <CellItem
            v-if="info.phone"
            :title="$t('contactDetail.phone')"
            :value="phoneText"
            :arrow="true"
            :border="!!tagsText"
            @click="callPhone"
          />
          <CellItem
            v-if="tagsText"
            :title="$t('contactDetail.tags')"
            :value="tagsText"
            :border="false"
            :clickable="false"
          />
        </view>

        <view class="group-block cd-danger-block">
          <view class="cd-danger" hover-class="cd-danger--hover" @click="removeFriend">
            <UiIcon name="trash" :size="30" color="var(--danger-color)" />
            <text>{{ $t('contactDetail.removeFriend') }}</text>
          </view>
        </view>
      </block>

      <!-- ===== 群聊视图 ===== -->
      <block v-else>
        <view class="cd-hero grid-overlay">
          <view class="cd-hero__top">
            <UiAvatar shape="square" :size="160" :src="info.avatar" :name="info.name" />
            <view class="cd-hero__head">
              <text class="cd-hero__name">{{ info.name || $t('common.groupChat') }}</text>
              <view class="cd-hero__chips">
                <view class="chip chip--accent">
                  <text class="cd-chip__text">{{ $t('common.members', { count: memberCount }) }}</text>
                </view>
                <view v-if="info.isCreator" class="chip">
                  <text class="cd-chip__text">{{ $t('contactDetail.creatorTag') }}</text>
                </view>
              </view>
            </view>
          </view>
          <text v-if="info.description" class="cd-hero__desc">{{ info.description }}</text>
        </view>

        <view class="cd-actions">
          <view class="cd-action cd-action--primary" hover-class="cd-action--hover" @click="sendMessage">
            <UiIcon name="message" :size="36" color="var(--accent-contrast)" />
            <text>{{ $t('contactDetail.sendMessage') }}</text>
          </view>
        </view>

        <view class="section-label">{{ $t('contactDetail.membersSection') }}</view>

        <view class="group-block cd-members-card">
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
                <UiIcon name="plus" :size="44" color="var(--text-tertiary)" />
              </view>
              <text class="cd-member__name">{{ $t('contactDetail.invite') }}</text>
            </view>
          </view>
        </view>

        <view class="section-label">{{ $t('contactDetail.groupSection') }}</view>

        <view class="group-block">
          <CellItem
            :title="$t('contactDetail.groupName')"
            :value="info.name"
            :arrow="true"
            @click="comingSoon($t('contactDetail.editGroupName'))"
          />
          <CellItem
            :title="$t('contactDetail.groupNotice')"
            :value="info.description || $t('common.notSet')"
            :arrow="true"
            @click="comingSoon($t('contactDetail.groupNotice'))"
          />
          <CellItem
            :title="$t('contactDetail.searchHistory')"
            :arrow="true"
            :border="false"
            @click="comingSoon($t('contactDetail.searchHistory'))"
          />
        </view>

        <view class="group-block cd-danger-block">
          <view v-if="info.isCreator" class="cd-danger" hover-class="cd-danger--hover" @click="removeGroup">
            <UiIcon name="trash" :size="30" color="var(--danger-color)" />
            <text>{{ $t('contactDetail.dissolveGroup') }}</text>
          </view>
          <view v-else class="cd-danger" hover-class="cd-danger--hover" @click="leaveGroup">
            <UiIcon name="logout" :size="30" color="var(--danger-color)" />
            <text>{{ $t('contactDetail.leaveGroup') }}</text>
          </view>
        </view>
      </block>

      <view class="safe-bottom-space--sm"></view>
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
      starred: false
    }
  },
  computed: {
    menuItems() {
      return [
        { text: this.$t('contactDetail.complain'), action: 'complain' },
        { text: this.$t('contactDetail.block'), action: 'block' }
      ]
    },
    isGroup() {
      return this.type === 'group'
    },
    genderText() {
      if (this.info.gender === 'male' || this.info.gender === 1 || this.info.gender === '1') {
        return this.$t('contactDetail.genderMale')
      }
      if (this.info.gender === 'female' || this.info.gender === 2 || this.info.gender === '2') {
        return this.$t('contactDetail.genderFemale')
      }
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
    },
    // 资料卡中的键值行：仅在有值时展示
    metaRows() {
      const rows = []
      if (this.info.account) {
        rows.push({
          key: 'account',
          label: this.$t('contactDetail.accountLabel'),
          value: this.info.account
        })
      }
      if (this.info.username && this.info.username !== this.info.name) {
        rows.push({
          key: 'nickname',
          label: this.$t('contactDetail.nicknameLabel'),
          value: this.info.username
        })
      }
      return rows
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
        name: m.remark || m.nickname || m.username || m.name || this.$t('contactDetail.memberFallback'),
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
      uni.showToast({ title: this.$t('contactDetail.devToast', { name: item.text }), icon: 'none' })
    },
    onToggleStar() {
      this.starred = toggleStar(this.id)
      uni.showToast({
        title: this.starred ? this.$t('contactDetail.starredOn') : this.$t('contactDetail.starredOff'),
        icon: 'none'
      })
    },
    editRemark() {
      uni.showModal({
        title: this.$t('contactDetail.remark'),
        editable: true,
        placeholderText: this.$t('contactDetail.remarkPlaceholder'),
        content: this.info.remark || '',
        success: async (res) => {
          if (!res.confirm) return
          const remark = (res.content || '').trim()
          try {
            const result = await updateFriendRemark(getUserId(), this.id, remark)
            if (result && result.code === 200) {
              this.info.remark = remark
              uni.showToast({ title: this.$t('contactDetail.saved'), icon: 'success' })
            } else {
              uni.showToast({
                title: (result && result.message) || this.$t('common.saveFailed'),
                icon: 'none'
              })
            }
          } catch (e) {
            console.error('修改备注失败:', e)
            uni.showToast({ title: this.$t('common.saveFailed'), icon: 'none' })
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
      uni.showToast({ title: this.$t('contactDetail.devToast', { name: this.$t('contactDetail.phone') }), icon: 'none' })
    },
    sendMessage() {
      if (this.isGroup) {
        uni.navigateTo({
          url: `/pages/chat/chat?type=group&id=${this.id}&name=${encodeURIComponent(
            this.info.name || this.$t('common.groupChat')
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
          this.info.name || this.$t('common.groupChat')
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
      uni.showToast({ title: this.$t('contactDetail.devToast', { name }), icon: 'none' })
    },
    removeFriend() {
      uni.showModal({
        title: this.$t('common.tip'),
        content: this.$t('contactDetail.removeFriendConfirm'),
        success: async (res) => {
          if (!res.confirm) return
          try {
            const result = await deleteFriend(getUserId(), this.id)
            if (result && result.code === 200) {
              uni.showToast({ title: this.$t('contactDetail.deleted'), icon: 'success' })
              setTimeout(() => uni.navigateBack(), 800)
            } else {
              uni.showToast({
                title: (result && result.message) || this.$t('contactDetail.deleteFailed'),
                icon: 'none'
              })
            }
          } catch (e) {
            console.error('删除好友失败:', e)
            uni.showToast({ title: this.$t('contactDetail.deleteFailed'), icon: 'none' })
          }
        }
      })
    },
    removeGroup() {
      uni.showModal({
        title: this.$t('common.tip'),
        content: this.$t('contactDetail.dissolveConfirm'),
        success: async (res) => {
          if (!res.confirm) return
          try {
            const result = await deleteGroup(this.id)
            if (result && result.code === 200) {
              uni.showToast({ title: this.$t('contactDetail.dissolved'), icon: 'success' })
              setTimeout(() => uni.switchTab({ url: '/pages/index/index' }), 800)
            } else {
              uni.showToast({
                title: (result && result.message) || this.$t('contactDetail.dissolveFailed'),
                icon: 'none'
              })
            }
          } catch (e) {
            console.error('解散群聊失败:', e)
            uni.showToast({ title: this.$t('contactDetail.dissolveFailed'), icon: 'none' })
          }
        }
      })
    },
    leaveGroup() {
      uni.showModal({
        title: this.$t('common.tip'),
        content: this.$t('contactDetail.leaveConfirm'),
        success: async (res) => {
          if (!res.confirm) return
          try {
            const result = await removeGroupMember(this.id, getUserId())
            if (result && result.code === 200) {
              uni.showToast({ title: this.$t('contactDetail.left'), icon: 'success' })
              setTimeout(() => uni.switchTab({ url: '/pages/index/index' }), 800)
            } else {
              uni.showToast({
                title: (result && result.message) || this.$t('contactDetail.leaveFailed'),
                icon: 'none'
              })
            }
          } catch (e) {
            console.error('退出群聊失败:', e)
            uni.showToast({ title: this.$t('contactDetail.leaveFailed'), icon: 'none' })
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
  background-color: var(--bg-secondary);
  color: var(--text-primary);
}

.cd-scroll {
  flex: 1;
  height: 0;
}

/* 资料头部卡 */
.cd-hero {
  margin: 20rpx 24rpx 0;
  padding: 32rpx 32rpx 26rpx;
  border-radius: var(--radius-lg);
  background-color: var(--bg-primary);
  background-image: linear-gradient(155deg, var(--accent-light) 0%, transparent 62%);
  border: 1rpx solid var(--border-light);
  box-shadow: var(--shadow-sm);
}

.cd-hero__top {
  display: flex;
  align-items: center;
  position: relative;
  z-index: 1;
}

.cd-hero__head {
  flex: 1;
  min-width: 0;
  margin-left: 28rpx;
}

.cd-hero__name-row {
  display: flex;
  align-items: center;
  gap: 12rpx;
}

.cd-hero__name {
  display: block;
  font-size: var(--font-xl);
  font-weight: 700;
  letter-spacing: -0.3rpx;
  color: var(--text-primary);
  line-height: 1.3;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.cd-gender {
  flex-shrink: 0;
  height: 36rpx;
  padding: 0 14rpx;
  border-radius: 999rpx;
  font-size: var(--font-xs);
  line-height: 36rpx;
  font-weight: 600;
  color: var(--text-secondary);
  background-color: var(--bg-chip);
}

.cd-gender.male,
.cd-gender[class~='1'] {
  color: var(--info-color);
  background-color: var(--info-light);
}

.cd-gender.female,
.cd-gender[class~='2'] {
  color: var(--danger-color);
  background-color: var(--danger-light);
}

.cd-hero__chips {
  display: flex;
  flex-wrap: wrap;
  gap: 10rpx;
  margin-top: 14rpx;
}

.cd-chip__text {
  font-size: var(--font-xs);
  color: inherit;
  line-height: 1;
}

.cd-hero__desc {
  display: block;
  position: relative;
  z-index: 1;
  margin-top: 24rpx;
  font-size: var(--font-sm);
  color: var(--text-secondary);
  line-height: 1.5;
}

.cd-star {
  width: 68rpx;
  height: 68rpx;
  margin-left: 12rpx;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: var(--radius-full);
  background-color: var(--bg-chip);
  transition: background-color var(--transition-fast), transform var(--transition-fast);
}

.cd-star--hover {
  background-color: var(--bg-active);
  transform: scale(0.94);
}

/* 资料键值行 */
.cd-meta {
  position: relative;
  z-index: 1;
  margin-top: 26rpx;
  padding-top: 20rpx;
  border-top: 1rpx solid var(--divider);
}

.cd-meta__row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10rpx 0;
}

.cd-meta__label {
  flex-shrink: 0;
  font-size: var(--font-sm);
  color: var(--text-tertiary);
  letter-spacing: 0.6rpx;
}

.cd-meta__value {
  flex: 1;
  min-width: 0;
  margin-left: 24rpx;
  text-align: right;
  font-size: var(--font-base);
  color: var(--text-primary);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* 操作按钮组 */
.cd-actions {
  margin: 20rpx 24rpx 0;
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

.cd-action {
  height: 92rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12rpx;
  border-radius: var(--radius-md);
  transition: transform var(--transition-fast), opacity var(--transition-fast);
}

.cd-action text {
  font-size: var(--font-md);
  font-weight: 600;
}

.cd-action--primary {
  background-image: var(--accent-gradient);
  box-shadow: var(--accent-glow);
}

.cd-action--primary text {
  color: var(--accent-contrast);
}

.cd-action--ghost {
  background-color: var(--bg-chip);
  border: 1rpx solid var(--border-light);
}

.cd-action--ghost text {
  color: var(--text-primary);
}

.cd-action--hover {
  transform: scale(0.98);
  opacity: 0.92;
}

/* 群成员网格 */
.cd-members-card {
  padding: 20rpx 12rpx 8rpx;
}

.cd-members {
  display: flex;
  flex-wrap: wrap;
}

.cd-member {
  width: 20%;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 10rpx 0 16rpx;
}

.cd-member__name {
  max-width: 120rpx;
  margin-top: 10rpx;
  font-size: var(--font-xs);
  color: var(--text-tertiary);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.cd-member__plus {
  width: 96rpx;
  height: 96rpx;
  border-radius: 22rpx;
  border: 2rpx dashed var(--border-color);
  background-color: var(--bg-chip);
  display: flex;
  align-items: center;
  justify-content: center;
}

.cd-member:active .cd-member__plus {
  background-color: var(--bg-active);
}

/* 危险操作 */
.cd-danger-block {
  padding: 0;
}

.cd-danger {
  height: 96rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10rpx;
  transition: background-color var(--transition-fast);
}

.cd-danger text {
  font-size: var(--font-md);
  font-weight: 600;
  color: var(--danger-color);
}

.cd-danger--hover {
  background-color: var(--danger-light);
}
</style>
