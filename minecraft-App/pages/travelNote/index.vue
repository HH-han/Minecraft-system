<template>
  <view class="note-page">
    <!-- 顶部 Tab -->
    <view class="tabs">
      <view
        v-for="tab in tabs"
        :key="tab.value"
        class="tab-item"
        :class="{ active: query.tab === tab.value }"
        @click="switchTab(tab.value)"
      >
        {{ tab.label }}
      </view>
    </view>

    <!-- 搜索 -->
    <view class="search-bar">
      <input
        v-model="keyword"
        class="search-input"
        type="text"
        placeholder="搜索目的地 / 标题"
        confirm-type="search"
        @confirm="handleSearch"
      />
      <button class="search-btn" @click="handleSearch">搜索</button>
    </view>

    <!-- 热门标签 -->
    <scroll-view v-if="hotTags.length" class="tag-scroll" scroll-x>
      <view class="tag-list">
        <view
          v-for="tag in hotTags"
          :key="tag.id"
          class="tag-item"
          :class="{ active: query.tagId === tag.id }"
          @click="toggleTag(tag)"
        >
          # {{ tag.name }}
        </view>
      </view>
    </scroll-view>

    <!-- 笔记列表 -->
    <view class="note-list">
      <view v-for="note in notes" :key="note.id" class="note-item" @click="goDetail(note.id)">
        <image v-if="note.coverUrl" class="note-cover" :src="note.coverUrl" mode="aspectFill" />
        <view v-else class="note-cover note-cover--empty">暂无封面</view>

        <view class="note-body">
          <text class="note-title">{{ note.title }}</text>
          <text class="note-summary">{{ note.summary || '暂无摘要' }}</text>
          <view class="note-meta">
            <text class="note-dest">{{ note.destination || '未标注目的地' }}</text>
            <text v-if="note.travelDays" class="note-days">{{ note.travelDays }}天</text>
          </view>
          <view class="note-tags">
            <text v-for="tag in (note.tags || []).slice(0, 3)" :key="tag" class="note-tag">#{{ tag }}</text>
          </view>
          <view class="note-footer">
            <text class="note-author">{{ note.username || '匿名用户' }}</text>
            <view class="note-stats">
              <text class="stat">👁 {{ note.viewCount || 0 }}</text>
              <text class="stat">❤ {{ note.likeCount || 0 }}</text>
            </view>
          </view>
        </view>
      </view>
    </view>

    <view v-if="!loading && notes.length === 0" class="empty-tip">暂无笔记，快来发布第一篇吧～</view>
    <view v-if="notes.length" class="load-more" @click="loadMore">
      {{ noMore ? '没有更多了' : '加载更多' }}
    </view>

    <!-- 悬浮操作 -->
    <view class="float-actions">
      <view class="float-btn float-btn--secondary" @click="goMine">我的</view>
      <view class="float-btn float-btn--primary" @click="goEditor">+</view>
    </view>
  </view>
</template>

<script>
import { getHotTags, getNotePage } from '@/api/travelNote'

export default {
  data() {
    return {
      tabs: [
        { label: '推荐', value: 'recommend' },
        { label: '最新', value: 'latest' },
        { label: '关注', value: 'follow' },
        { label: '同城', value: 'city' }
      ],
      keyword: '',
      loading: false,
      noMore: false,
      notes: [],
      hotTags: [],
      query: {
        pageNum: 1,
        pageSize: 10,
        tab: 'recommend',
        keyword: '',
        tagId: null
      }
    }
  },
  onLoad() {
    this.fetchNotes()
    this.fetchHotTags()
  },
  onPullDownRefresh() {
    this.query.pageNum = 1
    this.noMore = false
    this.fetchNotes().finally(() => uni.stopPullDownRefresh())
  },
  methods: {
    async fetchNotes() {
      this.loading = true
      try {
        const params = {}
        Object.keys(this.query).forEach((key) => {
          const value = this.query[key]
          if (value !== null && value !== undefined && value !== '') {
            params[key] = value
          }
        })
        const res = await getNotePage(params)
        if (res.code === 200) {
          const records = (res.data && res.data.records) || []
          this.notes = this.query.pageNum === 1 ? records : this.notes.concat(records)
          this.noMore = this.notes.length >= Number((res.data && res.data.total) || 0)
        }
      } catch (e) {
        console.error('获取笔记失败', e)
      } finally {
        this.loading = false
      }
    },
    async fetchHotTags() {
      try {
        const res = await getHotTags(12)
        if (res.code === 200) {
          this.hotTags = res.data || []
        }
      } catch (e) {
        console.error('获取标签失败', e)
      }
    },
    switchTab(tab) {
      this.query.tab = tab
      this.query.pageNum = 1
      this.noMore = false
      this.fetchNotes()
    },
    handleSearch() {
      this.query.keyword = this.keyword.trim()
      this.query.pageNum = 1
      this.noMore = false
      this.fetchNotes()
    },
    toggleTag(tag) {
      this.query.tagId = this.query.tagId === tag.id ? null : tag.id
      this.query.pageNum = 1
      this.noMore = false
      this.fetchNotes()
    },
    loadMore() {
      if (this.noMore) return
      this.query.pageNum += 1
      this.fetchNotes()
    },
    goDetail(id) {
      uni.navigateTo({ url: `/pages/travelNote/detail?id=${id}` })
    },
    goEditor() {
      uni.navigateTo({ url: '/pages/travelNote/editor' })
    },
    goMine() {
      uni.navigateTo({ url: '/pages/travelNote/mine' })
    }
  }
}
</script>

<style scoped>
.note-page {
  min-height: 100vh;
  background-color: var(--background-color, #f8faff);
  padding: 20rpx;
  position: relative;
}

.tabs {
  display: flex;
  gap: 40rpx;
  padding: 10rpx 6rpx 20rpx;
}

.tab-item {
  font-size: 30rpx;
  color: var(--text-secondary-color, #7d7e80);
}

.tab-item.active {
  color: var(--primary-color, #3a80d2);
  font-weight: 600;
  border-bottom: 4rpx solid var(--primary-color, #3a80d2);
  padding-bottom: 8rpx;
}

.search-bar {
  display: flex;
  gap: 16rpx;
  margin-bottom: 20rpx;
}

.search-input {
  flex: 1;
  height: 72rpx;
  padding: 0 24rpx;
  background-color: #fff;
  border-radius: 36rpx;
  font-size: 28rpx;
}

.search-btn {
  height: 72rpx;
  padding: 0 32rpx;
  background-color: var(--primary-color, #3a80d2);
  color: #fff;
  border: none;
  border-radius: 36rpx;
  font-size: 28rpx;
}

.tag-scroll {
  white-space: nowrap;
  margin-bottom: 20rpx;
}

.tag-list {
  display: inline-flex;
  gap: 16rpx;
}

.tag-item {
  padding: 8rpx 24rpx;
  background-color: #fff;
  border-radius: 30rpx;
  font-size: 24rpx;
  color: var(--text-secondary-color, #7d7e80);
}

.tag-item.active {
  background-color: var(--primary-color, #3a80d2);
  color: #fff;
}

.note-list {
  display: flex;
  flex-direction: column;
  gap: 20rpx;
  padding-bottom: 160rpx;
}

.note-item {
  display: flex;
  gap: 20rpx;
  background-color: #fff;
  border-radius: 16rpx;
  padding: 20rpx;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.06);
}

.note-cover {
  width: 220rpx;
  height: 180rpx;
  border-radius: 12rpx;
  flex-shrink: 0;
  background-color: #eef2f8;
}

.note-cover--empty {
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24rpx;
  color: #a0a4ab;
}

.note-body {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.note-title {
  font-size: 30rpx;
  font-weight: 600;
  color: var(--text-color, #1f2329);
}

.note-summary {
  margin-top: 8rpx;
  font-size: 24rpx;
  color: var(--text-secondary-color, #7d7e80);
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.note-meta {
  margin-top: 10rpx;
  display: flex;
  gap: 20rpx;
  font-size: 22rpx;
  color: var(--primary-color, #3a80d2);
}

.note-tags {
  margin-top: 8rpx;
  display: flex;
  gap: 12rpx;
}

.note-tag {
  font-size: 22rpx;
  color: var(--primary-color, #3a80d2);
}

.note-footer {
  margin-top: auto;
  padding-top: 10rpx;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.note-author {
  font-size: 22rpx;
  color: var(--text-secondary-color, #7d7e80);
}

.note-stats {
  display: flex;
  gap: 16rpx;
}

.stat {
  font-size: 22rpx;
  color: var(--text-secondary-color, #7d7e80);
}

.empty-tip {
  text-align: center;
  padding: 80rpx 0;
  color: var(--text-secondary-color, #7d7e80);
  font-size: 28rpx;
}

.load-more {
  text-align: center;
  padding: 24rpx 0 160rpx;
  color: var(--primary-color, #3a80d2);
  font-size: 26rpx;
}

.float-actions {
  position: fixed;
  right: 40rpx;
  bottom: 120rpx;
  display: flex;
  flex-direction: column;
  gap: 20rpx;
  align-items: center;
}

.float-btn {
  width: 96rpx;
  height: 96rpx;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 32rpx;
  box-shadow: 0 6rpx 20rpx rgba(58, 128, 210, 0.35);
}

.float-btn--primary {
  background-color: var(--primary-color, #3a80d2);
  font-size: 52rpx;
}

.float-btn--secondary {
  background-color: #6b7280;
  font-size: 26rpx;
  width: 88rpx;
  height: 88rpx;
}
</style>
