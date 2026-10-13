<template>
  <view class="fav-page">
    <CustomNavBar
      :show-back="true"
      title="收藏"
      title-align="center"
      :show-add="true"
      :menu-items="menuItems"
      @back="goBack"
      @menuItemClick="onMenuItem"
    />

    <!-- 搜索 -->
    <view class="fav-search">
      <view class="fav-search__box">
        <UiIcon name="search" :size="32" color="#B2B2B2" />
        <input
          class="fav-search__input"
          v-model="keyword"
          placeholder="搜索收藏"
          placeholder-class="fav-search__ph"
          confirm-type="search"
          @input="onKeywordInput"
          @confirm="onKeywordInput"
        />
        <view v-if="keyword" class="fav-search__clear" @click="clearKeyword">
          <UiIcon name="close" :size="22" color="#FFFFFF" />
        </view>
      </view>
    </view>

    <!-- 分类 tab -->
    <scroll-view class="fav-tabs" scroll-x :show-scrollbar="false">
      <view class="fav-tabs__inner">
        <view
          v-for="t in tabsWithCount"
          :key="t.key"
          class="fav-tab"
          :class="{ 'is-active': activeTab === t.key }"
          @click="switchTab(t.key)"
        >
          <view class="fav-tab__row">
            <text class="fav-tab__label">{{ t.label }}</text>
            <text v-if="t.count" class="fav-tab__badge">{{ t.count > 99 ? '99+' : t.count }}</text>
          </view>
          <view v-if="activeTab === t.key" class="fav-tab__line"></view>
        </view>
      </view>
    </scroll-view>

    <!-- 标签云 -->
    <scroll-view v-if="tags.length" class="fav-tagbar" scroll-x :show-scrollbar="false">
      <view class="fav-tagbar__inner">
        <view
          v-for="(tag, i) in tags"
          :key="i"
          class="fav-tag"
          :class="{ 'is-active': activeTag === tag }"
          @click="toggleTag(tag)"
        >
          <text>{{ tag }}</text>
        </view>
      </view>
    </scroll-view>

    <!-- 列表 -->
    <scroll-view class="fav-list" scroll-y :show-scrollbar="false" @scrolltolower="onReachLower">
      <view
        v-for="item in list"
        :key="item.id"
        class="fav-card"
        hover-class="fav-card--hover"
        hover-stay-time="60"
        @click="openDetail(item)"
        @longpress="onCardLongPress(item)"
      >
        <view class="fav-card__icon">
          <UiIcon :name="iconOf(item.type)" :size="44" color="#576B95" />
        </view>
        <view class="fav-card__body">
          <text class="fav-card__title">{{ itemTitle(item) }}</text>
          <text class="fav-card__sub">{{ subText(item) }}</text>
        </view>
        <image v-if="item.cover" class="fav-card__thumb" :src="item.cover" mode="aspectFill" />
      </view>

      <view v-if="canClear" class="fav-clear" @click="clearCategory">
        <text>清空当前分类</text>
      </view>

      <view v-if="loading && list.length" class="fav-footer">
        <text>加载中…</text>
      </view>

      <UiEmpty v-if="!list.length && !loading" icon="⭐" text="还没有收藏内容" />
      <view class="fav-list__safe"></view>
    </scroll-view>

    <!-- 详情弹层 -->
    <view v-if="detailVisible" class="sheet-mask" @click="closeDetail"></view>
    <view v-if="detailVisible" class="fav-detail" @click.stop>
      <view class="fav-detail__head">
        <text class="fav-detail__title">{{ detailTitle }}</text>
        <view class="fav-detail__close" @click="closeDetail">
          <UiIcon name="close" :size="40" color="#888888" />
        </view>
      </view>
      <scroll-view scroll-y class="fav-detail__body">
        <image
          v-if="detail.cover"
          class="fav-detail__cover"
          :src="detail.cover"
          mode="widthFix"
          @click="previewCover"
        />
        <text v-if="detail.content" class="fav-detail__content">{{ detail.content }}</text>

        <view v-if="detail.url" class="fav-detail__row">
          <text class="fav-detail__label">链接</text>
          <text class="fav-detail__value is-link">{{ detail.url }}</text>
        </view>
        <view v-if="detail.fileName" class="fav-detail__row">
          <text class="fav-detail__label">文件名</text>
          <text class="fav-detail__value">{{ detail.fileName }}</text>
        </view>
        <view v-if="detail.fileSize" class="fav-detail__row">
          <text class="fav-detail__label">大小</text>
          <text class="fav-detail__value">{{ formatSize(detail.fileSize) }}</text>
        </view>
        <view v-if="detailTags.length" class="fav-detail__tags">
          <view v-for="(tag, i) in detailTags" :key="i" class="fav-tag is-static">
            <text>{{ tag }}</text>
          </view>
        </view>
        <view class="fav-detail__row">
          <text class="fav-detail__label">来源</text>
          <text class="fav-detail__value">{{ detail.source || '—' }}</text>
        </view>
        <view class="fav-detail__row">
          <text class="fav-detail__label">收藏时间</text>
          <text class="fav-detail__value">{{ formatRelativeTime(detail.createTime) }}</text>
        </view>
      </scroll-view>
      <view class="fav-detail__actions">
        <view class="fav-detail__btn" @click="copyItem">
          <text>复制</text>
        </view>
        <view class="fav-detail__btn is-danger" @click="deleteCurrent">
          <text>删除</text>
        </view>
      </view>
      <view class="fav-detail__safe"></view>
    </view>

    <!-- 新建弹层 -->
    <view v-if="composerVisible" class="sheet-mask" @click="closeComposer"></view>
    <view v-if="composerVisible" class="composer" @click.stop>
      <view class="composer__head">
        <text class="composer__cancel" @click="closeComposer">取消</text>
        <text class="composer__title">{{ composerTitle }}</text>
        <view
          class="composer__save"
          :class="{ 'is-disabled': saving }"
          @click="submitComposer"
        >
          <text>{{ saving ? '保存中…' : '保存' }}</text>
        </view>
      </view>
      <scroll-view scroll-y class="composer__body">
        <template v-if="composerType === 'link'">
          <view class="composer__field">
            <text class="composer__label">链接地址</text>
            <input
              class="composer__input"
              v-model="cUrl"
              placeholder="https://"
              placeholder-class="composer__ph"
            />
          </view>
        </template>

        <view class="composer__field">
          <text class="composer__label">标题</text>
          <input
            class="composer__input"
            v-model="cTitle"
            :maxlength="100"
            placeholder="标题（选填）"
            placeholder-class="composer__ph"
          />
        </view>

        <view class="composer__field composer__field--col">
          <text class="composer__label">{{ composerType === 'link' ? '备注' : '内容' }}</text>
          <textarea
            class="composer__textarea"
            v-model="cContent"
            :maxlength="2000"
            placeholder="写点什么…"
            placeholder-class="composer__ph"
            auto-height
          />
        </view>

        <view class="composer__field">
          <text class="composer__label">标签</text>
          <input
            class="composer__input"
            v-model="cTags"
            placeholder="标签，用逗号或空格分隔（选填）"
            placeholder-class="composer__ph"
          />
        </view>

        <view class="composer__field">
          <text class="composer__label">封面</text>
          <view class="composer__cover" @click="pickCover">
            <image v-if="cCover" class="composer__cover-img" :src="cCover" mode="aspectFill" />
            <view v-else class="composer__cover-add">
              <UiIcon name="image" :size="48" color="#B2B2B2" />
            </view>
          </view>
        </view>
      </scroll-view>
      <view class="composer__safe"></view>
    </view>
  </view>
</template>

<script>
import CustomNavBar from '../../components/CustomNavBar.vue'
import UiIcon from '../../components/Icon.vue'
import UiEmpty from '../../components/EmptyState.vue'
import {
  getFavoriteList,
  getRecentFavorites,
  getFavoriteStats,
  getFavoriteTags,
  addFavorite,
  touchFavorite,
  deleteFavorite,
  clearFavorites
} from '../../utils/favorite-api.js'
import { uploadFile } from '../../utils/chat-api.js'
import { getUserId, getCurrentProfile } from '../../utils/session.js'
import { formatRelativeTime } from '../../utils/format.js'

const TYPE_LABELS = {
  note: '笔记',
  link: '链接',
  image: '图片',
  video: '视频',
  file: '文件',
  chat: '聊天记录',
  voice: '语音',
  location: '位置',
  miniapp: '小程序'
}

const TYPE_ICONS = {
  note: 'note',
  link: 'link',
  image: 'image',
  video: 'video',
  file: 'file',
  chat: 'message',
  voice: 'mic',
  location: 'map-pin',
  miniapp: 'compass'
}

export default {
  components: { CustomNavBar, UiIcon, UiEmpty },
  data() {
    return {
      userId: null,
      profile: { name: '', avatar: '' },
      tabs: [
        { key: 'all', label: '全部' },
        { key: 'recent', label: '最近' },
        { key: 'note', label: '笔记' },
        { key: 'link', label: '链接' },
        { key: 'media', label: '图片与视频' },
        { key: 'file', label: '文件' },
        { key: 'chat', label: '聊天记录' },
        { key: 'voice', label: '语音' },
        { key: 'location', label: '位置' },
        { key: 'miniapp', label: '小程序' }
      ],
      activeTab: 'all',
      activeTag: '',
      keyword: '',
      searchTimer: null,

      stats: { total: 0, types: {} },
      tags: [],

      list: [],
      page: 1,
      size: 20,
      hasMore: true,
      loading: false,

      detailVisible: false,
      detail: {},

      composerVisible: false,
      composerType: 'note',
      cTitle: '',
      cContent: '',
      cUrl: '',
      cCover: '',
      cTags: '',
      saving: false,

      menuItems: [
        { text: '写笔记', iconName: 'note', action: 'note' },
        { text: '添加链接', iconName: 'link', action: 'link' }
      ]
    }
  },
  computed: {
    tabsWithCount() {
      return this.tabs.map((t) => ({ ...t, count: this.tabCount(t.key) }))
    },
    typeParam() {
      if (this.activeTab === 'all' || this.activeTab === 'recent') return ''
      if (this.activeTab === 'media') return 'image,video'
      return this.activeTab
    },
    canClear() {
      return !this.activeTag && this.activeTab !== 'all' && this.activeTab !== 'recent'
    },
    detailTitle() {
      return this.itemTitle(this.detail)
    },
    detailTags() {
      const t = this.detail && this.detail.tags
      if (Array.isArray(t)) return t
      if (typeof t === 'string' && t.trim()) {
        return t.split(/[,，\s]+/).filter(Boolean)
      }
      return []
    },
    composerTitle() {
      return this.composerType === 'link' ? '添加链接' : '写笔记'
    }
  },
  onLoad() {
    this.userId = getUserId()
    this.profile = getCurrentProfile()
    this.load(true)
    this.loadStats()
    this.loadTags()
  },
  methods: {
    formatRelativeTime,
    goBack() {
      uni.navigateBack()
    },
    tabCount(key) {
      if (key === 'recent') return null
      const stats = this.stats || {}
      const types = stats.types || {}
      if (key === 'all') return Number(stats.total || 0)
      if (key === 'media') return Number(types.image || 0) + Number(types.video || 0)
      return Number(types[key] || 0)
    },
    iconOf(type) {
      return TYPE_ICONS[type] || 'star'
    },
    typeLabel(type) {
      return TYPE_LABELS[type] || '收藏'
    },
    itemTitle(item) {
      if (!item) return ''
      if (item.title) return item.title
      if ((item.type === 'link' || item.type === 'miniapp') && item.url) return item.url
      if (item.type === 'file' && item.fileName) return item.fileName
      const content = (item.content || '').trim()
      if (content) return content.length > 40 ? `${content.slice(0, 40)}…` : content
      return this.typeLabel(item.type)
    },
    subText(item) {
      const parts = []
      if (item.source) parts.push(item.source)
      const time = formatRelativeTime(item.updateTime || item.createTime)
      if (time) parts.push(time)
      return parts.join(' · ')
    },
    formatSize(bytes) {
      const size = Number(bytes)
      if (!size || size <= 0) return ''
      if (size < 1024) return `${size}B`
      if (size < 1024 * 1024) return `${(size / 1024).toFixed(1)}KB`
      if (size < 1024 * 1024 * 1024) return `${(size / 1024 / 1024).toFixed(1)}MB`
      return `${(size / 1024 / 1024 / 1024).toFixed(2)}GB`
    },
    async load(reset = false) {
      if (!this.userId || this.loading) return
      if (reset) {
        this.page = 1
        this.hasMore = true
      }
      this.loading = true
      try {
        if (this.activeTab === 'recent') {
          const res = await getRecentFavorites(this.userId, 30)
          if (res && res.code === 200 && Array.isArray(res.data)) {
            this.list = res.data
            this.hasMore = false
          }
          return
        }
        const res = await getFavoriteList({
          userId: this.userId,
          type: this.typeParam,
          tag: this.activeTag || '',
          keyword: this.keyword || '',
          page: this.page,
          size: this.size
        })
        if (res && res.code === 200 && Array.isArray(res.data)) {
          this.list = reset ? res.data : this.list.concat(res.data)
          this.hasMore = res.data.length >= this.size
        }
      } catch (e) {
        console.error('加载收藏失败:', e)
      } finally {
        this.loading = false
      }
    },
    onReachLower() {
      if (!this.hasMore || this.loading || this.activeTab === 'recent') return
      this.page += 1
      this.load(false)
    },
    async loadStats() {
      if (!this.userId) return
      try {
        const res = await getFavoriteStats(this.userId)
        if (res && res.code === 200 && res.data) {
          this.stats = res.data
        }
      } catch (e) {
        // ignore
      }
    },
    async loadTags() {
      if (!this.userId) return
      try {
        const res = await getFavoriteTags(this.userId)
        if (res && res.code === 200 && Array.isArray(res.data)) {
          this.tags = res.data
        }
      } catch (e) {
        // ignore
      }
    },
    switchTab(key) {
      if (this.activeTab === key) return
      this.activeTab = key
      this.load(true)
    },
    toggleTag(tag) {
      this.activeTag = this.activeTag === tag ? '' : tag
      this.load(true)
    },
    onKeywordInput() {
      if (this.searchTimer) clearTimeout(this.searchTimer)
      this.searchTimer = setTimeout(() => {
        this.load(true)
      }, 300)
    },
    clearKeyword() {
      this.keyword = ''
      this.load(true)
    },
    openDetail(item) {
      touchFavorite(item.id, this.userId).catch(() => {})
      this.detail = { ...item }
      this.detailVisible = true
    },
    closeDetail() {
      this.detailVisible = false
      this.detail = {}
    },
    previewCover() {
      if (this.detail.cover) {
        uni.previewImage({ urls: [this.detail.cover], current: this.detail.cover })
      }
    },
    copyItem() {
      const value = this.detail.url || this.detail.content || this.detail.title
      if (!value) {
        uni.showToast({ title: '暂无可复制内容', icon: 'none' })
        return
      }
      uni.setClipboardData({
        data: value,
        success: () => {
          uni.showToast({ title: '已复制', icon: 'success' })
        }
      })
    },
    deleteCurrent() {
      const item = this.detail
      uni.showModal({
        title: '提示',
        content: '确定删除该收藏吗？',
        success: async (r) => {
          if (!r.confirm) return
          try {
            const res = await deleteFavorite(item.id, this.userId)
            if (res && res.code === 200) {
              this.closeDetail()
              this.load(true)
              this.loadStats()
              this.loadTags()
              uni.showToast({ title: '已删除', icon: 'success' })
            }
          } catch (e) {
            uni.showToast({ title: '删除失败', icon: 'none' })
          }
        }
      })
    },
    onCardLongPress(item) {
      uni.showModal({
        title: '提示',
        content: '确定删除该收藏吗？',
        success: async (r) => {
          if (!r.confirm) return
          try {
            const res = await deleteFavorite(item.id, this.userId)
            if (res && res.code === 200) {
              this.list = this.list.filter((it) => it.id !== item.id)
              this.loadStats()
              this.loadTags()
              uni.showToast({ title: '已删除', icon: 'success' })
            }
          } catch (e) {
            uni.showToast({ title: '删除失败', icon: 'none' })
          }
        }
      })
    },
    clearCategory() {
      uni.showModal({
        title: '提示',
        content: '确定清空当前分类的收藏吗？',
        success: async (r) => {
          if (!r.confirm) return
          try {
            const res = await clearFavorites(this.userId, this.typeParam || undefined)
            if (res && res.code === 200) {
              this.load(true)
              this.loadStats()
              this.loadTags()
              uni.showToast({ title: '已清空', icon: 'success' })
            }
          } catch (e) {
            uni.showToast({ title: '清空失败', icon: 'none' })
          }
        }
      })
    },
    onMenuItem(item) {
      this.openComposer(item.action)
    },
    openComposer(type) {
      this.composerType = type
      this.cTitle = ''
      this.cContent = ''
      this.cUrl = ''
      this.cCover = ''
      this.cTags = ''
      this.composerVisible = true
    },
    closeComposer() {
      this.composerVisible = false
    },
    pickCover() {
      uni.chooseImage({
        count: 1,
        success: async (res) => {
          const path = res.tempFilePaths[0]
          if (!path) return
          uni.showLoading({ title: '上传中…', mask: true })
          try {
            const uploadRes = await uploadFile(path)
            if (uploadRes && uploadRes.code === 200 && uploadRes.data) {
              this.cCover = uploadRes.data
            } else {
              uni.showToast({ title: '上传失败', icon: 'none' })
            }
          } catch (e) {
            uni.showToast({ title: '上传失败', icon: 'none' })
          } finally {
            uni.hideLoading()
          }
        }
      })
    },
    async submitComposer() {
      if (this.saving) return
      if (this.composerType === 'link' && !this.cUrl.trim()) {
        uni.showToast({ title: '请输入链接地址', icon: 'none' })
        return
      }
      if (
        this.composerType === 'note' &&
        !this.cTitle.trim() &&
        !this.cContent.trim() &&
        !this.cCover
      ) {
        uni.showToast({ title: '请至少填写标题、内容或封面', icon: 'none' })
        return
      }
      const tags = this.cTags
        .split(/[,，\s]+/)
        .map((t) => t.trim())
        .filter(Boolean)
      const payload = {
        userId: this.userId,
        type: this.composerType,
        title: this.cTitle.trim() || null,
        content: this.cContent.trim() || null,
        url: this.composerType === 'link' ? this.cUrl.trim() || null : null,
        cover: this.cCover || null,
        tags,
        source: this.profile.name || null
      }
      this.saving = true
      try {
        const res = await addFavorite(payload)
        if (res && res.code === 200) {
          this.composerVisible = false
          this.activeTab = 'all'
          this.activeTag = ''
          this.keyword = ''
          this.load(true)
          this.loadStats()
          this.loadTags()
          uni.showToast({ title: '已收藏', icon: 'success' })
        } else {
          uni.showToast({ title: (res && res.message) || '保存失败', icon: 'none' })
        }
      } catch (e) {
        uni.showToast({ title: '保存失败', icon: 'none' })
      } finally {
        this.saving = false
      }
    }
  }
}
</script>

<style scoped>
.fav-page {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background-color: #ededed;
}

/* 搜索 */
.fav-search {
  flex-shrink: 0;
  padding: 0 24rpx 16rpx;
}

.fav-search__box {
  display: flex;
  align-items: center;
  height: 64rpx;
  background-color: #ffffff;
  border-radius: 10rpx;
  padding: 0 20rpx;
}

.fav-search__input {
  flex: 1;
  font-size: 28rpx;
  color: #191919;
  margin-left: 12rpx;
}

.fav-search__ph {
  color: #b2b2b2;
}

.fav-search__clear {
  width: 32rpx;
  height: 32rpx;
  border-radius: 50%;
  background-color: #c8c8cd;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* tab */
.fav-tabs {
  flex-shrink: 0;
  background-color: #ffffff;
  white-space: nowrap;
}

.fav-tabs__inner {
  display: inline-flex;
  align-items: flex-end;
  padding: 0 12rpx;
}

.fav-tab {
  position: relative;
  flex-shrink: 0;
  padding: 20rpx 20rpx 16rpx;
}

.fav-tab__row {
  display: flex;
  align-items: center;
}

.fav-tab__label {
  font-size: 28rpx;
  color: #191919;
}

.fav-tab.is-active .fav-tab__label {
  color: #07c160;
  font-weight: 500;
}

.fav-tab__badge {
  min-width: 32rpx;
  height: 32rpx;
  padding: 0 8rpx;
  margin-left: 8rpx;
  background-color: #f2f2f2;
  border-radius: 16rpx;
  font-size: 20rpx;
  color: #999999;
  line-height: 32rpx;
  text-align: center;
}

.fav-tab.is-active .fav-tab__badge {
  background-color: #e6f7ee;
  color: #07c160;
}

.fav-tab__line {
  position: absolute;
  left: 20rpx;
  right: 20rpx;
  bottom: 4rpx;
  height: 4rpx;
  background-color: #07c160;
  border-radius: 2rpx;
}

/* 标签云 */
.fav-tagbar {
  flex-shrink: 0;
  background-color: #ffffff;
  border-top: 1rpx solid #ededed;
  white-space: nowrap;
}

.fav-tagbar__inner {
  display: inline-flex;
  align-items: center;
  padding: 16rpx 24rpx;
  gap: 16rpx;
}

.fav-tag {
  flex-shrink: 0;
  padding: 8rpx 24rpx;
  background-color: #f2f2f2;
  border-radius: 26rpx;
}

.fav-tag:active {
  opacity: 0.7;
}

.fav-tag text {
  font-size: 24rpx;
  color: #576b95;
}

.fav-tag.is-active {
  background-color: #e6f7ee;
}

.fav-tag.is-active text {
  color: #07c160;
}

.fav-tag.is-static {
  flex-shrink: 1;
}

/* 列表 */
.fav-list {
  flex: 1;
  height: 0;
  padding: 16rpx 0;
}

.fav-card {
  display: flex;
  align-items: center;
  margin: 0 24rpx 16rpx;
  padding: 24rpx;
  background-color: #ffffff;
  border-radius: 16rpx;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.04);
}

.fav-card--hover {
  background-color: #f7f7f7;
}

.fav-card__icon {
  width: 88rpx;
  height: 88rpx;
  border-radius: 16rpx;
  background-color: #f2f6ff;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.fav-card__body {
  flex: 1;
  min-width: 0;
  margin-left: 20rpx;
}

.fav-card__title {
  display: block;
  font-size: 30rpx;
  color: #191919;
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.fav-card__sub {
  display: block;
  font-size: 24rpx;
  color: #999999;
  margin-top: 10rpx;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.fav-card__thumb {
  width: 80rpx;
  height: 80rpx;
  border-radius: 10rpx;
  margin-left: 20rpx;
  flex-shrink: 0;
  background-color: #f2f2f2;
}

.fav-clear {
  display: flex;
  justify-content: center;
  padding: 32rpx 0;
}

.fav-clear:active {
  opacity: 0.6;
}

.fav-clear text {
  font-size: 28rpx;
  color: #fa5151;
}

.fav-footer {
  display: flex;
  justify-content: center;
  padding: 24rpx 0;
}

.fav-footer text {
  font-size: 24rpx;
  color: #b2b2b2;
}

.fav-list__safe {
  height: env(safe-area-inset-bottom);
}

/* 遮罩 */
.sheet-mask {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.45);
  z-index: 900;
}

/* 详情 */
.fav-detail {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 901;
  background-color: #ffffff;
  border-radius: 24rpx 24rpx 0 0;
  display: flex;
  flex-direction: column;
  max-height: 78vh;
  animation: sheetUp 0.22s ease;
}

.fav-detail__head {
  flex-shrink: 0;
  height: 96rpx;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 32rpx;
  border-bottom: 1rpx solid #ededed;
}

.fav-detail__title {
  flex: 1;
  font-size: 32rpx;
  font-weight: 600;
  color: #191919;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.fav-detail__close {
  width: 56rpx;
  height: 56rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-left: 16rpx;
}

.fav-detail__close:active {
  opacity: 0.5;
}

.fav-detail__body {
  flex: 1;
  height: 0;
  padding: 24rpx 32rpx;
}

.fav-detail__cover {
  width: 100%;
  border-radius: 12rpx;
  margin-bottom: 20rpx;
  display: block;
}

.fav-detail__content {
  display: block;
  font-size: 30rpx;
  color: #191919;
  line-height: 1.6;
  word-break: break-word;
  white-space: pre-wrap;
  margin-bottom: 12rpx;
}

.fav-detail__row {
  display: flex;
  align-items: flex-start;
  padding: 16rpx 0;
  border-top: 1rpx solid #f0f0f0;
}

.fav-detail__label {
  width: 130rpx;
  flex-shrink: 0;
  font-size: 28rpx;
  color: #999999;
}

.fav-detail__value {
  flex: 1;
  font-size: 28rpx;
  color: #191919;
  line-height: 1.5;
  word-break: break-all;
}

.fav-detail__value.is-link {
  color: #576b95;
}

.fav-detail__tags {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
  padding: 16rpx 0;
  border-top: 1rpx solid #f0f0f0;
}

.fav-detail__actions {
  flex-shrink: 0;
  display: flex;
  padding: 20rpx 32rpx;
  border-top: 1rpx solid #ededed;
}

.fav-detail__btn {
  flex: 1;
  height: 80rpx;
  margin: 0 10rpx;
  border-radius: 12rpx;
  background-color: #f2f2f2;
  display: flex;
  align-items: center;
  justify-content: center;
}

.fav-detail__btn:active {
  opacity: 0.7;
}

.fav-detail__btn text {
  font-size: 30rpx;
  color: #191919;
}

.fav-detail__btn.is-danger text {
  color: #fa5151;
}

.fav-detail__safe {
  height: env(safe-area-inset-bottom);
}

/* 新建弹层 */
.composer {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 901;
  background-color: #ffffff;
  border-radius: 24rpx 24rpx 0 0;
  display: flex;
  flex-direction: column;
  max-height: 80vh;
  animation: sheetUp 0.22s ease;
}

.composer__head {
  flex-shrink: 0;
  height: 96rpx;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 32rpx;
  border-bottom: 1rpx solid #ededed;
}

.composer__cancel {
  font-size: 30rpx;
  color: #888888;
  min-width: 120rpx;
}

.composer__title {
  font-size: 32rpx;
  font-weight: 600;
  color: #191919;
}

.composer__save {
  min-width: 120rpx;
  height: 64rpx;
  padding: 0 28rpx;
  background-color: #07c160;
  border-radius: 10rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.composer__save:active {
  background-color: #06ad56;
}

.composer__save.is-disabled {
  background-color: #b7ebcb;
}

.composer__save text {
  font-size: 28rpx;
  color: #ffffff;
  font-weight: 500;
}

.composer__body {
  flex: 1;
  height: 0;
  padding: 8rpx 32rpx;
}

.composer__field {
  display: flex;
  align-items: center;
  height: 96rpx;
  border-bottom: 1rpx solid #f0f0f0;
}

.composer__field--col {
  flex-direction: column;
  align-items: flex-start;
  height: auto;
  padding: 20rpx 0;
}

.composer__label {
  width: 130rpx;
  flex-shrink: 0;
  font-size: 28rpx;
  color: #888888;
}

.composer__input {
  flex: 1;
  font-size: 30rpx;
  color: #191919;
}

.composer__textarea {
  width: 100%;
  min-height: 180rpx;
  margin-top: 16rpx;
  font-size: 30rpx;
  color: #191919;
  line-height: 1.5;
}

.composer__ph {
  color: #b2b2b2;
}

.composer__cover {
  width: 160rpx;
  height: 160rpx;
  border-radius: 12rpx;
  overflow: hidden;
  background-color: #f7f7f7;
}

.composer__cover-img {
  width: 100%;
  height: 100%;
  display: block;
}

.composer__cover-add {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1rpx solid #e2e2e2;
  border-radius: 12rpx;
}

.composer__cover:active {
  opacity: 0.7;
}

.composer__safe {
  height: env(safe-area-inset-bottom);
}

@keyframes sheetUp {
  from {
    transform: translateY(100%);
  }
  to {
    transform: translateY(0);
  }
}
</style>
