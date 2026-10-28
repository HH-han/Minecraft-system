<template>
  <view class="fav-page" :class="themeClass">
    <CustomNavBar
      :show-back="true"
      :title="$t('favorites.title')"
      title-align="center"
      :show-add="true"
      :menu-items="menuItems"
      @back="goBack"
      @menuItemClick="onMenuItem"
    />

    <!-- 搜索 -->
    <view class="fav-search">
      <view class="fav-search__box">
        <UiIcon name="search" :size="30" color="var(--text-tertiary)" />
        <input
          class="fav-search__input"
          v-model="keyword"
          :placeholder="$t('favorites.searchPlaceholder')"
          placeholder-class="fav-search__ph"
          confirm-type="search"
          @input="onKeywordInput"
          @confirm="onKeywordInput"
        />
        <view v-if="keyword" class="fav-search__clear" @click="clearKeyword">
          <UiIcon name="close" :size="20" color="var(--text-secondary)" />
        </view>
      </view>
    </view>

    <!-- 分类筛选 chips -->
    <scroll-view class="fav-filters" scroll-x :show-scrollbar="false">
      <view class="fav-filters__inner">
        <view
          v-for="t in tabsWithCount"
          :key="t.key"
          class="fav-chip"
          :class="{ 'is-active': activeTab === t.key }"
          @click="switchTab(t.key)"
        >
          <text>{{ t.label }}</text>
          <view v-if="t.count" class="fav-chip__badge">
            <text>{{ t.count > 99 ? '99+' : t.count }}</text>
          </view>
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
        class="fav-card surface-card"
        hover-class="fav-card--hover"
        hover-stay-time="60"
        @click="openDetail(item)"
        @longpress="onCardLongPress(item)"
      >
        <view class="fav-card__icon" :style="iconBgOf(item.type)">
          <UiIcon :name="iconOf(item.type)" :size="40" :color="iconColorOf(item.type)" />
        </view>
        <view class="fav-card__body">
          <text class="fav-card__title">{{ itemTitle(item) }}</text>
          <view class="fav-card__meta">
            <view class="chip fav-card__chip">
              <text>{{ typeLabel(item.type) }}</text>
            </view>
            <text class="fav-card__sub">{{ subText(item) }}</text>
          </view>
        </view>
        <image v-if="item.cover" class="fav-card__thumb" :src="item.cover" mode="aspectFill" />
      </view>

      <view v-if="canClear" class="fav-clear" @click="clearCategory">
        <text>{{ $t('favorites.clearCategory') }}</text>
      </view>

      <view v-if="loading && list.length" class="fav-footer">
        <text>{{ $t('favorites.loadingMore') }}</text>
      </view>

      <UiEmpty v-if="!list.length && !loading" icon-name="star" :text="$t('favorites.empty')" />
      <view class="fav-list__safe"></view>
    </scroll-view>

    <!-- 详情弹层 -->
    <view v-if="detailVisible" class="sheet-mask" @click="closeDetail"></view>
    <view v-if="detailVisible" class="fav-detail" @click.stop>
      <view class="fav-detail__head">
        <text class="fav-detail__title">{{ detailTitle }}</text>
        <view class="fav-detail__close" @click="closeDetail">
          <UiIcon name="close" :size="36" color="var(--text-tertiary)" />
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
          <text class="fav-detail__label">{{ $t('favorites.linkLabel') }}</text>
          <text class="fav-detail__value is-link">{{ detail.url }}</text>
        </view>
        <view v-if="detail.fileName" class="fav-detail__row">
          <text class="fav-detail__label">{{ $t('favorites.fileName') }}</text>
          <text class="fav-detail__value">{{ detail.fileName }}</text>
        </view>
        <view v-if="detail.fileSize" class="fav-detail__row">
          <text class="fav-detail__label">{{ $t('favorites.fileSize') }}</text>
          <text class="fav-detail__value">{{ formatSize(detail.fileSize) }}</text>
        </view>
        <view v-if="detailTags.length" class="fav-detail__tags">
          <view v-for="(tag, i) in detailTags" :key="i" class="chip fav-detail__tag">
            <text>{{ tag }}</text>
          </view>
        </view>
        <view class="fav-detail__row">
          <text class="fav-detail__label">{{ $t('favorites.source') }}</text>
          <text class="fav-detail__value">{{ detail.source || '—' }}</text>
        </view>
        <view class="fav-detail__row">
          <text class="fav-detail__label">{{ $t('favorites.favoriteTime') }}</text>
          <text class="fav-detail__value">{{ formatRelativeTime(detail.createTime) }}</text>
        </view>
      </scroll-view>
      <view class="fav-detail__actions">
        <view class="fav-detail__btn" @click="copyItem">
          <text>{{ $t('favorites.copy') }}</text>
        </view>
        <view class="fav-detail__btn is-danger" @click="deleteCurrent">
          <text>{{ $t('favorites.deleteLabel') }}</text>
        </view>
      </view>
      <view class="fav-detail__safe"></view>
    </view>

    <!-- 新建弹层 -->
    <view v-if="composerVisible" class="sheet-mask" @click="closeComposer"></view>
    <view v-if="composerVisible" class="composer" @click.stop>
      <view class="composer__head">
        <text class="composer__cancel" @click="closeComposer">{{ $t('favorites.cancel') }}</text>
        <text class="composer__title">{{ composerTitle }}</text>
        <view
          class="composer__save"
          :class="{ 'is-disabled': saving }"
          @click="submitComposer"
        >
          <text>{{ saving ? $t('favorites.saving') : $t('favorites.save') }}</text>
        </view>
      </view>
      <scroll-view scroll-y class="composer__body">
        <template v-if="composerType === 'link'">
          <view class="composer__field">
            <text class="composer__label">{{ $t('favorites.urlLabel') }}</text>
            <input
              class="composer__input"
              v-model="cUrl"
              :placeholder="$t('favorites.urlPlaceholder')"
              placeholder-class="composer__ph"
            />
          </view>
        </template>

        <view class="composer__field">
          <text class="composer__label">{{ $t('favorites.titleLabel') }}</text>
          <input
            class="composer__input"
            v-model="cTitle"
            :maxlength="100"
            :placeholder="$t('favorites.titlePlaceholder')"
            placeholder-class="composer__ph"
          />
        </view>

        <view class="composer__field composer__field--col">
          <text class="composer__label">
            {{ composerType === 'link' ? $t('favorites.remarkLabel') : $t('favorites.contentLabel') }}
          </text>
          <textarea
            class="composer__textarea"
            v-model="cContent"
            :maxlength="2000"
            :placeholder="$t('favorites.contentPlaceholder')"
            placeholder-class="composer__ph"
            auto-height
          />
        </view>

        <view class="composer__field">
          <text class="composer__label">{{ $t('favorites.tagsLabel') }}</text>
          <input
            class="composer__input"
            v-model="cTags"
            :placeholder="$t('favorites.tagsPlaceholder')"
            placeholder-class="composer__ph"
          />
        </view>

        <view class="composer__field">
          <text class="composer__label">{{ $t('favorites.coverLabel') }}</text>
          <view class="composer__cover" @click="pickCover">
            <image v-if="cCover" class="composer__cover-img" :src="cCover" mode="aspectFill" />
            <view v-else class="composer__cover-add">
              <UiIcon name="image" :size="44" color="var(--text-tertiary)" />
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

const TYPE_TONES = {
  note: 'accent',
  link: 'info',
  image: 'info',
  video: 'info',
  file: 'warning',
  chat: 'accent',
  voice: 'warning',
  location: 'danger',
  miniapp: 'accent'
}

const TYPE_LABEL_KEYS = {
  note: 'tabNote',
  link: 'tabLink',
  image: 'typeImage',
  video: 'typeVideo',
  file: 'tabFile',
  chat: 'tabChat',
  voice: 'tabVoice',
  location: 'tabLocation',
  miniapp: 'tabMiniapp'
}

export default {
  components: { CustomNavBar, UiIcon, UiEmpty },
  data() {
    return {
      userId: null,
      profile: { name: '', avatar: '' },
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
      saving: false
    }
  },
  computed: {
    tabs() {
      return [
        { key: 'all', label: this.$t('favorites.tabAll') },
        { key: 'recent', label: this.$t('favorites.tabRecent') },
        { key: 'note', label: this.$t('favorites.tabNote') },
        { key: 'link', label: this.$t('favorites.tabLink') },
        { key: 'media', label: this.$t('favorites.tabMedia') },
        { key: 'file', label: this.$t('favorites.tabFile') },
        { key: 'chat', label: this.$t('favorites.tabChat') },
        { key: 'voice', label: this.$t('favorites.tabVoice') },
        { key: 'location', label: this.$t('favorites.tabLocation') },
        { key: 'miniapp', label: this.$t('favorites.tabMiniapp') }
      ]
    },
    menuItems() {
      return [
        { text: this.$t('favorites.writeNote'), iconName: 'note', action: 'note' },
        { text: this.$t('favorites.addLink'), iconName: 'link', action: 'link' }
      ]
    },
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
      return this.composerType === 'link'
        ? this.$t('favorites.addLink')
        : this.$t('favorites.writeNote')
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
    iconTone(type) {
      return TYPE_TONES[type] || 'accent'
    },
    iconColorOf(type) {
      return `var(--${this.iconTone(type)}-color)`
    },
    iconBgOf(type) {
      return { backgroundColor: `var(--${this.iconTone(type)}-light)` }
    },
    typeLabel(type) {
      const key = TYPE_LABEL_KEYS[type]
      return key ? this.$t(`favorites.${key}`) : this.$t('favorites.typeFallback')
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
        uni.showToast({ title: this.$t('favorites.nothingToCopy'), icon: 'none' })
        return
      }
      uni.setClipboardData({
        data: value,
        success: () => {
          uni.showToast({ title: this.$t('favorites.copied'), icon: 'success' })
        }
      })
    },
    deleteCurrent() {
      const item = this.detail
      uni.showModal({
        title: this.$t('common.tip'),
        content: this.$t('favorites.deleteConfirm'),
        success: async (r) => {
          if (!r.confirm) return
          try {
            const res = await deleteFavorite(item.id, this.userId)
            if (res && res.code === 200) {
              this.closeDetail()
              this.load(true)
              this.loadStats()
              this.loadTags()
              uni.showToast({ title: this.$t('favorites.deleted'), icon: 'success' })
            }
          } catch (e) {
            uni.showToast({ title: this.$t('favorites.deleteFailed'), icon: 'none' })
          }
        }
      })
    },
    onCardLongPress(item) {
      uni.showModal({
        title: this.$t('common.tip'),
        content: this.$t('favorites.deleteConfirm'),
        success: async (r) => {
          if (!r.confirm) return
          try {
            const res = await deleteFavorite(item.id, this.userId)
            if (res && res.code === 200) {
              this.list = this.list.filter((it) => it.id !== item.id)
              this.loadStats()
              this.loadTags()
              uni.showToast({ title: this.$t('favorites.deleted'), icon: 'success' })
            }
          } catch (e) {
            uni.showToast({ title: this.$t('favorites.deleteFailed'), icon: 'none' })
          }
        }
      })
    },
    clearCategory() {
      uni.showModal({
        title: this.$t('common.tip'),
        content: this.$t('favorites.clearConfirm'),
        success: async (r) => {
          if (!r.confirm) return
          try {
            const res = await clearFavorites(this.userId, this.typeParam || undefined)
            if (res && res.code === 200) {
              this.load(true)
              this.loadStats()
              this.loadTags()
              uni.showToast({ title: this.$t('favorites.cleared'), icon: 'success' })
            }
          } catch (e) {
            uni.showToast({ title: this.$t('favorites.clearFailed'), icon: 'none' })
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
          uni.showLoading({ title: this.$t('favorites.uploading'), mask: true })
          try {
            const uploadRes = await uploadFile(path)
            if (uploadRes && uploadRes.code === 200 && uploadRes.data) {
              this.cCover = uploadRes.data
            } else {
              uni.showToast({ title: this.$t('favorites.uploadFailed'), icon: 'none' })
            }
          } catch (e) {
            uni.showToast({ title: this.$t('favorites.uploadFailed'), icon: 'none' })
          } finally {
            uni.hideLoading()
          }
        }
      })
    },
    async submitComposer() {
      if (this.saving) return
      if (this.composerType === 'link' && !this.cUrl.trim()) {
        uni.showToast({ title: this.$t('favorites.inputUrl'), icon: 'none' })
        return
      }
      if (
        this.composerType === 'note' &&
        !this.cTitle.trim() &&
        !this.cContent.trim() &&
        !this.cCover
      ) {
        uni.showToast({ title: this.$t('favorites.inputAtLeastOne'), icon: 'none' })
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
          uni.showToast({ title: this.$t('favorites.added'), icon: 'success' })
        } else {
          uni.showToast({ title: (res && res.message) || this.$t('favorites.saveFailed'), icon: 'none' })
        }
      } catch (e) {
        uni.showToast({ title: this.$t('favorites.saveFailed'), icon: 'none' })
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
  background-color: var(--bg-secondary);
  color: var(--text-primary);
}

/* 搜索 */
.fav-search {
  flex-shrink: 0;
  padding: 8rpx 24rpx 14rpx;
}

.fav-search__box {
  display: flex;
  align-items: center;
  height: 76rpx;
  padding: 0 24rpx;
  gap: 14rpx;
  background-color: var(--bg-input);
  border: 1rpx solid var(--border-light);
  border-radius: 999rpx;
}

.fav-search__input {
  flex: 1;
  min-width: 0;
  font-size: var(--font-base);
  color: var(--text-primary);
}

.fav-search__ph {
  color: var(--text-placeholder);
}

.fav-search__clear {
  width: 36rpx;
  height: 36rpx;
  border-radius: 50%;
  background-color: var(--bg-active);
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 分类 chips */
.fav-filters {
  flex-shrink: 0;
  white-space: nowrap;
}

.fav-filters__inner {
  display: inline-flex;
  align-items: center;
  gap: 14rpx;
  padding: 0 24rpx 14rpx;
}

.fav-chip {
  flex-shrink: 0;
  display: inline-flex;
  align-items: center;
  height: 60rpx;
  padding: 0 26rpx;
  border-radius: 999rpx;
  background-color: var(--bg-chip);
  border: 1rpx solid var(--border-light);
  transition: all var(--transition-fast);
}

.fav-chip:active {
  opacity: 0.8;
}

.fav-chip text {
  font-size: var(--font-sm);
  color: var(--text-secondary);
}

.fav-chip.is-active {
  background-color: var(--accent-light);
  border-color: var(--accent-color);
}

.fav-chip.is-active text {
  color: var(--accent-color);
  font-weight: 600;
}

.fav-chip__badge {
  min-width: 32rpx;
  height: 32rpx;
  padding: 0 8rpx;
  margin-left: 10rpx;
  border-radius: 16rpx;
  background-color: var(--bg-active);
  display: flex;
  align-items: center;
  justify-content: center;
}

.fav-chip__badge text {
  font-size: var(--font-xs);
  color: var(--text-tertiary);
}

.fav-chip.is-active .fav-chip__badge {
  background-color: var(--accent-color);
}

.fav-chip.is-active .fav-chip__badge text {
  color: var(--accent-contrast);
  font-weight: 600;
}

/* 标签云 */
.fav-tagbar {
  flex-shrink: 0;
  white-space: nowrap;
}

.fav-tagbar__inner {
  display: inline-flex;
  align-items: center;
  gap: 14rpx;
  padding: 0 24rpx 14rpx;
}

.fav-tag {
  flex-shrink: 0;
  display: inline-flex;
  align-items: center;
  height: 52rpx;
  padding: 0 22rpx;
  border-radius: 999rpx;
  background-color: var(--bg-chip);
  border: 1rpx solid var(--border-light);
}

.fav-tag:active {
  opacity: 0.7;
}

.fav-tag text {
  font-size: var(--font-xs);
  color: var(--text-secondary);
}

.fav-tag.is-active {
  background-color: var(--accent-light);
  border-color: var(--accent-color);
}

.fav-tag.is-active text {
  color: var(--accent-color);
  font-weight: 600;
}

/* 列表 */
.fav-list {
  flex: 1;
  height: 0;
  padding-top: 8rpx;
}

.fav-card {
  display: flex;
  align-items: center;
  margin: 0 24rpx 18rpx;
  padding: 24rpx;
}

.fav-card.fav-card--hover {
  background-color: var(--bg-hover);
}

.fav-card__icon {
  width: 84rpx;
  height: 84rpx;
  border-radius: var(--radius-md);
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
  font-size: var(--font-md);
  font-weight: 500;
  color: var(--text-primary);
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.fav-card__meta {
  display: flex;
  align-items: center;
  gap: 12rpx;
  margin-top: 10rpx;
}

.fav-card__chip {
  flex-shrink: 0;
  height: 34rpx;
  padding: 0 12rpx;
}

.fav-card__chip text {
  font-size: var(--font-xs);
  color: var(--text-secondary);
}

.fav-card__sub {
  flex: 1;
  min-width: 0;
  font-size: var(--font-sm);
  color: var(--text-tertiary);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.fav-card__thumb {
  width: 84rpx;
  height: 84rpx;
  border-radius: var(--radius-sm);
  margin-left: 18rpx;
  flex-shrink: 0;
  background-color: var(--bg-tertiary);
}

.fav-clear {
  display: flex;
  justify-content: center;
  padding: 12rpx 0 32rpx;
}

.fav-clear:active {
  opacity: 0.6;
}

.fav-clear text {
  font-size: var(--font-sm);
  color: var(--danger-color);
}

.fav-footer {
  display: flex;
  justify-content: center;
  padding: 24rpx 0;
}

.fav-footer text {
  font-size: var(--font-xs);
  color: var(--text-tertiary);
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
  background-color: var(--bg-mask);
  z-index: 900;
}

/* 详情 */
.fav-detail {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 901;
  background-color: var(--bg-elevated);
  border-radius: var(--radius-xl) var(--radius-xl) 0 0;
  display: flex;
  flex-direction: column;
  max-height: 78vh;
  animation: sheetUp 0.22s ease;
}

.fav-detail__head {
  flex-shrink: 0;
  height: 100rpx;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 32rpx;
  border-bottom: 1rpx solid var(--divider);
}

.fav-detail__title {
  flex: 1;
  font-size: var(--font-lg);
  font-weight: 600;
  color: var(--text-primary);
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
  border-radius: 50%;
}

.fav-detail__close:active {
  background-color: var(--bg-hover);
}

.fav-detail__body {
  flex: 1;
  height: 0;
  padding: 24rpx 32rpx;
}

.fav-detail__cover {
  width: 100%;
  border-radius: var(--radius-md);
  margin-bottom: 20rpx;
  display: block;
}

.fav-detail__content {
  display: block;
  font-size: var(--font-md);
  color: var(--text-primary);
  line-height: 1.6;
  word-break: break-word;
  white-space: pre-wrap;
  margin-bottom: 12rpx;
}

.fav-detail__row {
  display: flex;
  align-items: flex-start;
  padding: 18rpx 0;
  border-top: 1rpx solid var(--divider);
}

.fav-detail__label {
  width: 130rpx;
  flex-shrink: 0;
  font-size: var(--font-sm);
  color: var(--text-tertiary);
}

.fav-detail__value {
  flex: 1;
  font-size: var(--font-sm);
  color: var(--text-primary);
  line-height: 1.5;
  word-break: break-all;
}

.fav-detail__value.is-link {
  color: var(--text-link);
}

.fav-detail__tags {
  display: flex;
  flex-wrap: wrap;
  gap: 14rpx;
  padding: 18rpx 0;
  border-top: 1rpx solid var(--divider);
}

.fav-detail__tag {
  height: 44rpx;
  padding: 0 18rpx;
}

.fav-detail__actions {
  flex-shrink: 0;
  display: flex;
  padding: 20rpx 32rpx;
  border-top: 1rpx solid var(--divider);
}

.fav-detail__btn {
  flex: 1;
  height: 84rpx;
  margin: 0 10rpx;
  border-radius: 999rpx;
  background-color: var(--bg-chip);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background-color var(--transition-fast);
}

.fav-detail__btn:active {
  background-color: var(--bg-active);
}

.fav-detail__btn text {
  font-size: var(--font-md);
  color: var(--text-primary);
}

.fav-detail__btn.is-danger {
  background-color: var(--danger-light);
}

.fav-detail__btn.is-danger text {
  color: var(--danger-color);
  font-weight: 500;
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
  background-color: var(--bg-elevated);
  border-radius: var(--radius-xl) var(--radius-xl) 0 0;
  display: flex;
  flex-direction: column;
  max-height: 80vh;
  animation: sheetUp 0.22s ease;
}

.composer__head {
  flex-shrink: 0;
  height: 100rpx;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 32rpx;
  border-bottom: 1rpx solid var(--divider);
}

.composer__cancel {
  font-size: var(--font-base);
  color: var(--text-secondary);
  min-width: 120rpx;
}

.composer__title {
  font-size: var(--font-lg);
  font-weight: 600;
  color: var(--text-primary);
}

.composer__save {
  min-width: 120rpx;
  height: 64rpx;
  padding: 0 28rpx;
  border-radius: 999rpx;
  background-image: var(--accent-gradient);
  box-shadow: var(--accent-glow);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: transform var(--transition-fast), opacity var(--transition-fast);
}

.composer__save:active {
  transform: scale(0.97);
  opacity: 0.9;
}

.composer__save.is-disabled {
  background-image: none;
  background-color: var(--bg-tertiary);
  box-shadow: none;
}

.composer__save text {
  font-size: var(--font-sm);
  color: var(--accent-contrast);
  font-weight: 600;
}

.composer__save.is-disabled text {
  color: var(--text-disabled);
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
  border-bottom: 1rpx solid var(--divider);
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
  font-size: var(--font-sm);
  color: var(--text-tertiary);
}

.composer__input {
  flex: 1;
  min-width: 0;
  font-size: var(--font-md);
  color: var(--text-primary);
}

.composer__textarea {
  width: 100%;
  min-height: 180rpx;
  margin-top: 16rpx;
  padding: 20rpx;
  box-sizing: border-box;
  background-color: var(--bg-input);
  border-radius: var(--radius-md);
  font-size: var(--font-md);
  color: var(--text-primary);
  line-height: 1.5;
}

.composer__ph {
  color: var(--text-placeholder);
}

.composer__cover {
  width: 152rpx;
  height: 152rpx;
  border-radius: var(--radius-md);
  overflow: hidden;
  background-color: var(--bg-tertiary);
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
  border: 1rpx solid var(--border-color);
  border-radius: var(--radius-md);
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
