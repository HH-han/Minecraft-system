<template>
  <view class="note-mine">
    <view class="mine-header">
      <text class="page-title">我的笔记</text>
      <button class="create-btn" @click="goEditor">写笔记</button>
    </view>

    <!-- 状态筛选 -->
    <scroll-view class="status-scroll" scroll-x>
      <view class="status-list">
        <view
          v-for="item in statusTabs"
          :key="item.value"
          class="status-item"
          :class="{ active: activeStatus === item.value }"
          @click="switchStatus(item.value)"
        >
          {{ item.label }}
        </view>
      </view>
    </scroll-view>

    <view class="note-list">
      <view v-for="note in notes" :key="note.id" class="note-item" @click="goDetail(note)">
        <image v-if="note.coverUrl" class="note-cover" :src="note.coverUrl" mode="aspectFill" />
        <view v-else class="note-cover note-cover--empty">暂无封面</view>

        <view class="note-body">
          <view class="note-title-row">
            <text class="note-title">{{ note.title }}</text>
            <text class="note-status" :class="statusClass(note.status)">{{ statusLabel(note.status) }}</text>
          </view>
          <text class="note-summary">{{ note.summary || '暂无摘要' }}</text>
          <view v-if="note.status === 3 && note.rejectReason" class="reject-tip">
            驳回原因：{{ note.rejectReason }}
          </view>
          <view class="note-stats">
            <text class="stat">👁 {{ note.viewCount || 0 }}</text>
            <text class="stat">❤ {{ note.likeCount || 0 }}</text>
            <text class="stat">★ {{ note.collectCount || 0 }}</text>
            <text class="stat">💬 {{ note.commentCount || 0 }}</text>
          </view>
          <view class="note-ops" @click.stop>
            <text class="op" @click="goEditor(note)">编辑</text>
            <text class="op op--danger" @click="removeNote(note)">删除</text>
          </view>
        </view>
      </view>
    </view>

    <view v-if="!loading && notes.length === 0" class="empty-tip">这里还什么都没有</view>
    <view v-if="notes.length" class="load-more" @click="loadMore">
      {{ noMore ? '没有更多了' : '加载更多' }}
    </view>
  </view>
</template>

<script>
import { deleteNote, getMyNotes } from '@/api/travelNote'

const STATUS_MAP = {
  0: { label: '草稿', cls: 'status--draft' },
  1: { label: '待审核', cls: 'status--pending' },
  2: { label: '已发布', cls: 'status--published' },
  3: { label: '已驳回', cls: 'status--rejected' },
  4: { label: '已下架', cls: 'status--offline' },
  5: { label: '私密', cls: 'status--private' }
}

export default {
  data() {
    return {
      statusTabs: [
        { label: '全部', value: 'all' },
        { label: '草稿箱', value: 0 },
        { label: '待审核', value: 1 },
        { label: '已发布', value: 2 },
        { label: '已驳回', value: 3 },
        { label: '私密', value: 5 }
      ],
      activeStatus: 'all',
      loading: false,
      notes: [],
      pageNum: 1,
      pageSize: 10,
      noMore: false
    }
  },
  onLoad() {
    if (!uni.getStorageSync('token')) {
      uni.showToast({ title: '请先登录', icon: 'none' })
      setTimeout(() => uni.navigateBack(), 800)
      return
    }
    this.fetchNotes()
  },
  onShow() {
    if (uni.getStorageSync('token')) {
      this.reset()
    }
  },
  onPullDownRefresh() {
    this.reset()
    setTimeout(() => uni.stopPullDownRefresh(), 600)
  },
  methods: {
    statusLabel(status) {
      return (STATUS_MAP[status] && STATUS_MAP[status].label) || '未知'
    },
    statusClass(status) {
      return (STATUS_MAP[status] && STATUS_MAP[status].cls) || ''
    },
    reset() {
      this.pageNum = 1
      this.noMore = false
      this.notes = []
      this.fetchNotes()
    },
    switchStatus(value) {
      this.activeStatus = value
      this.reset()
    },
    async fetchNotes() {
      this.loading = true
      try {
        const params = { pageNum: this.pageNum, pageSize: this.pageSize }
        if (this.activeStatus !== 'all') {
          params.status = this.activeStatus
        }
        const res = await getMyNotes(params)
        if (res.code === 200) {
          const records = (res.data && res.data.records) || []
          this.notes = this.pageNum === 1 ? records : this.notes.concat(records)
          this.noMore = this.notes.length >= Number((res.data && res.data.total) || 0)
        }
      } catch (e) {
        console.error('获取我的笔记失败', e)
      } finally {
        this.loading = false
      }
    },
    loadMore() {
      if (this.noMore) return
      this.pageNum += 1
      this.fetchNotes()
    },
    goDetail(note) {
      if (note.status !== 2) {
        uni.showToast({ title: '仅已发布笔记可查看详情', icon: 'none' })
        return
      }
      uni.navigateTo({ url: `/pages/travelNote/detail?id=${note.id}` })
    },
    goEditor(note) {
      uni.navigateTo({ url: `/pages/travelNote/editor?id=${note.id}` })
    },
    removeNote(note) {
      uni.showModal({
        title: '提示',
        content: '确定删除这篇笔记吗？',
        success: async (r) => {
          if (!r.confirm) return
          const res = await deleteNote(note.id)
          if (res.code === 200) {
            uni.showToast({ title: '删除成功', icon: 'none' })
            this.reset()
          } else {
            uni.showToast({ title: res.message || '删除失败', icon: 'none' })
          }
        }
      })
    }
  }
}
</script>

<style scoped>
.note-mine {
  min-height: 100vh;
  background-color: var(--background-color, #f8faff);
  padding: 24rpx;
}

.mine-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20rpx;
}

.page-title {
  font-size: 36rpx;
  font-weight: 700;
  color: var(--text-color, #1f2329);
}

.create-btn {
  height: 68rpx;
  padding: 0 32rpx;
  line-height: 68rpx;
  background-color: var(--primary-color, #3a80d2);
  color: #fff;
  border: none;
  border-radius: 34rpx;
  font-size: 26rpx;
}

.status-scroll {
  white-space: nowrap;
  margin-bottom: 20rpx;
}

.status-list {
  display: inline-flex;
  gap: 16rpx;
}

.status-item {
  padding: 10rpx 26rpx;
  background-color: #fff;
  border-radius: 30rpx;
  font-size: 26rpx;
  color: var(--text-secondary-color, #7d7e80);
}

.status-item.active {
  background-color: var(--primary-color, #3a80d2);
  color: #fff;
}

.note-list {
  display: flex;
  flex-direction: column;
  gap: 20rpx;
  padding-bottom: 60rpx;
}

.note-item {
  display: flex;
  gap: 20rpx;
  background-color: #fff;
  border-radius: 16rpx;
  padding: 20rpx;
}

.note-cover {
  width: 200rpx;
  height: 160rpx;
  border-radius: 12rpx;
  background-color: #eef2f8;
  flex-shrink: 0;
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
  min-width: 0;
}

.note-title-row {
  display: flex;
  align-items: center;
  gap: 12rpx;
}

.note-title {
  font-size: 30rpx;
  font-weight: 600;
  color: var(--text-color, #1f2329);
  flex: 1;
}

.note-status {
  font-size: 22rpx;
  padding: 4rpx 14rpx;
  border-radius: 8rpx;
  background-color: #eef2f8;
  color: #6b7280;
}

.status--published {
  background-color: #e6f7ed;
  color: #18a058;
}

.status--pending {
  background-color: #fff4e5;
  color: #f0a020;
}

.status--rejected {
  background-color: #ffe9e9;
  color: #e64340;
}

.note-summary {
  display: block;
  margin-top: 8rpx;
  font-size: 24rpx;
  color: var(--text-secondary-color, #7d7e80);
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.reject-tip {
  margin-top: 8rpx;
  font-size: 22rpx;
  color: #e64340;
}

.note-stats {
  margin-top: 10rpx;
  display: flex;
  flex-wrap: wrap;
  gap: 20rpx;
}

.stat {
  font-size: 22rpx;
  color: var(--text-secondary-color, #7d7e80);
}

.note-ops {
  margin-top: 12rpx;
  display: flex;
  gap: 28rpx;
}

.op {
  font-size: 24rpx;
  color: var(--primary-color, #3a80d2);
}

.op--danger {
  color: #e64340;
}

.empty-tip {
  text-align: center;
  padding: 100rpx 0;
  color: var(--text-secondary-color, #7d7e80);
  font-size: 28rpx;
}

.load-more {
  text-align: center;
  padding: 24rpx 0 60rpx;
  color: var(--primary-color, #3a80d2);
  font-size: 26rpx;
}
</style>
