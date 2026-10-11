<template>
  <view class="note-detail">
    <template v-if="note">
      <!-- 标题区 -->
      <view class="header">
        <text class="title">{{ note.title }}</text>
        <view class="author-row">
          <image v-if="note.avatar" class="avatar" :src="note.avatar" mode="aspectFill" />
          <view class="author-info">
            <text class="author-name">{{ note.username || '匿名用户' }}</text>
            <text class="publish-time">{{ formatTime(note.publishTime || note.createTime) }}</text>
          </view>
          <text v-if="note.destination" class="dest">{{ note.destination }}</text>
        </view>

        <view class="trip-info">
          <text v-if="note.travelDays" class="trip-item">行程 {{ note.travelDays }} 天</text>
          <text v-if="note.cost" class="trip-item">人均 ¥{{ note.cost }}</text>
          <text v-if="note.travelDate" class="trip-item">出行 {{ note.travelDate }}</text>
        </view>

        <view v-if="note.tags && note.tags.length" class="tags">
          <text v-for="tag in note.tags" :key="tag" class="tag"># {{ tag }}</text>
        </view>
      </view>

      <!-- 图集 -->
      <view v-if="note.mediaList && note.mediaList.length" class="gallery">
        <image
          v-for="media in note.mediaList"
          :key="media.id"
          class="gallery-item"
          :src="media.url"
          mode="aspectFill"
          @click="previewImage(media.url)"
        />
      </view>

      <!-- 正文 -->
      <view class="content">
        <rich-text v-if="note.contentType === 1" :nodes="note.content || ''" />
        <text v-else class="content-plain">{{ note.content }}</text>
      </view>

      <!-- 关联 -->
      <view v-if="note.relations && note.relations.length" class="section">
        <text class="section-title">相关推荐</text>
        <view v-for="rel in note.relations" :key="rel.id" class="relation-item">
          <image v-if="rel.bizCover" class="relation-cover" :src="rel.bizCover" mode="aspectFill" />
          <view v-else class="relation-cover relation-cover--empty" />
          <view class="relation-info">
            <text class="relation-type">{{ bizTypeLabel(rel.bizType) }}</text>
            <text class="relation-name">{{ rel.bizName }}</text>
          </view>
        </view>
      </view>

      <!-- 行程轨迹 -->
      <view v-if="note.routes && note.routes.length" class="section">
        <text class="section-title">行程轨迹</text>
        <view v-for="route in note.routes" :key="route.id" class="route-item">
          <text class="route-day">Day {{ route.dayIndex || '-' }}</text>
          <text class="route-poi">{{ route.poiName }}</text>
          <text v-if="route.remark" class="route-remark">{{ route.remark }}</text>
        </view>
      </view>

      <!-- 互动 -->
      <view class="actions">
        <view class="action-btn" :class="{ active: note.isLiked }" @click="toggleLike">
          ❤ {{ note.likeCount || 0 }}
        </view>
        <view class="action-btn" :class="{ active: note.isCollected }" @click="toggleCollect">
          ★ {{ note.collectCount || 0 }}
        </view>
        <view class="action-btn" @click="copyToPlan">复制为行程</view>
        <view class="action-btn" @click="handleReport">举报</view>
      </view>

      <!-- 评论 -->
      <view class="section">
        <text class="section-title">评论 ({{ note.commentCount || 0 }})</text>

        <view class="comment-input-row">
          <input v-model="commentText" class="comment-input" placeholder="写下你的想法..." />
          <button class="comment-submit" :disabled="commentSubmitting" @click="submitComment">发送</button>
        </view>

        <view v-for="comment in comments" :key="comment.id" class="comment-item">
          <image v-if="comment.avatar" class="comment-avatar" :src="comment.avatar" mode="aspectFill" />
          <view class="comment-body">
            <view class="comment-head">
              <text class="comment-user">{{ comment.username }}</text>
              <text class="comment-time">{{ formatTime(comment.createTime) }}</text>
            </view>
            <text class="comment-text">{{ comment.content }}</text>
            <view class="comment-ops">
              <text class="comment-op" @click="replyTo(comment, null)">回复</text>
              <text v-if="comment.userId === currentUserId" class="comment-op comment-op--danger" @click="removeComment(comment.id)">
                删除
              </text>
            </view>

            <view v-for="reply in comment.replies || []" :key="reply.id" class="reply-item">
              <text class="comment-user">{{ reply.username }}</text>
              <text v-if="reply.replyUsername" class="comment-user"> 回复 @{{ reply.replyUsername }}</text>
              <text class="comment-text">{{ reply.content }}</text>
              <view class="comment-ops">
                <text class="comment-op" @click="replyTo(comment, reply)">回复</text>
                <text
                  v-if="reply.userId === currentUserId"
                  class="comment-op comment-op--danger"
                  @click="removeComment(reply.id)"
                >
                  删除
                </text>
              </view>
            </view>
          </view>
        </view>

        <view v-if="comments.length" class="load-more" @click="loadMoreComments">
          {{ commentsNoMore ? '没有更多评论' : '加载更多评论' }}
        </view>
      </view>
    </template>

    <view v-else-if="!loading" class="empty-tip">笔记不存在或未发布</view>
  </view>
</template>

<script>
import {
  addNoteComment,
  collectNote,
  copyNoteToPlan,
  deleteNoteComment,
  getNoteComments,
  getNoteDetail,
  likeNote,
  reportNote,
  uncollectNote,
  unlikeNote
} from '@/api/travelNote'

const BIZ_TYPES = { 1: '景点', 2: '酒店', 3: '美食', 4: '商品', 5: '行程' }

export default {
  data() {
    return {
      noteId: null,
      loading: true,
      note: null,
      currentUserId: null,
      commentText: '',
      commentSubmitting: false,
      comments: [],
      commentsPage: 1,
      commentsNoMore: false
    }
  },
  onLoad(options) {
    this.noteId = options.id
    const userInfo = uni.getStorageSync('userInfo')
    this.currentUserId = userInfo ? userInfo.id : null
    this.fetchDetail()
  },
  onPullDownRefresh() {
    this.fetchDetail().finally(() => uni.stopPullDownRefresh())
  },
  methods: {
    bizTypeLabel(type) {
      return BIZ_TYPES[type] || '相关'
    },
    async fetchDetail() {
      this.loading = true
      try {
        const res = await getNoteDetail(this.noteId)
        if (res.code === 200) {
          this.note = res.data
          this.fetchComments()
        } else {
          uni.showToast({ title: res.message || '加载失败', icon: 'none' })
        }
      } catch (e) {
        console.error('获取笔记详情失败', e)
      } finally {
        this.loading = false
      }
    },
    async fetchComments() {
      try {
        const res = await getNoteComments(this.noteId, {
          pageNum: this.commentsPage,
          pageSize: 10
        })
        if (res.code === 200) {
          const records = (res.data && res.data.records) || []
          this.comments = this.commentsPage === 1 ? records : this.comments.concat(records)
          this.commentsNoMore = this.comments.length >= Number((res.data && res.data.total) || 0)
        }
      } catch (e) {
        console.error('获取评论失败', e)
      }
    },
    loadMoreComments() {
      if (this.commentsNoMore) return
      this.commentsPage += 1
      this.fetchComments()
    },
    previewImage(url) {
      uni.previewImage({ urls: [url] })
    },
    ensureLogin() {
      if (!uni.getStorageSync('token')) {
        uni.showToast({ title: '请先登录', icon: 'none' })
        setTimeout(() => uni.navigateTo({ url: '/pages/login/login' }), 800)
        return false
      }
      return true
    },
    async toggleLike() {
      if (!this.ensureLogin()) return
      const res = this.note.isLiked ? await unlikeNote(this.noteId) : await likeNote(this.noteId)
      if (res.code === 200) {
        this.note.isLiked = !this.note.isLiked
        this.note.likeCount = Math.max((this.note.likeCount || 0) + (this.note.isLiked ? 1 : -1), 0)
      } else {
        uni.showToast({ title: res.message, icon: 'none' })
      }
    },
    async toggleCollect() {
      if (!this.ensureLogin()) return
      const res = this.note.isCollected ? await uncollectNote(this.noteId) : await collectNote(this.noteId)
      if (res.code === 200) {
        this.note.isCollected = !this.note.isCollected
        this.note.collectCount = Math.max((this.note.collectCount || 0) + (this.note.isCollected ? 1 : -1), 0)
        uni.showToast({ title: this.note.isCollected ? '已收藏' : '已取消收藏', icon: 'none' })
      } else {
        uni.showToast({ title: res.message, icon: 'none' })
      }
    },
    copyToPlan() {
      if (!this.ensureLogin()) return
      uni.showModal({
        title: '复制为行程',
        content: '将根据本笔记生成一份出行计划，是否继续？',
        success: async (r) => {
          if (!r.confirm) return
          const res = await copyNoteToPlan(this.noteId)
          uni.showToast({ title: res.message || '已复制', icon: 'none' })
        }
      })
    },
    handleReport() {
      if (!this.ensureLogin()) return
      uni.showModal({
        title: '举报笔记',
        editable: true,
        placeholderText: '请描述举报原因',
        success: async (r) => {
          if (!r.confirm) return
          const res = await reportNote(this.noteId, r.content || '违规内容')
          uni.showToast({ title: res.message || '举报成功', icon: 'none' })
        }
      })
    },
    async submitComment() {
      if (!this.ensureLogin()) return
      if (!this.commentText.trim()) {
        uni.showToast({ title: '评论内容不能为空', icon: 'none' })
        return
      }
      this.commentSubmitting = true
      try {
        const res = await addNoteComment(this.noteId, { content: this.commentText.trim() })
        if (res.code === 200) {
          this.commentText = ''
          this.commentsPage = 1
          this.comments = []
          if (this.note) this.note.commentCount = (this.note.commentCount || 0) + 1
          this.fetchComments()
        } else {
          uni.showToast({ title: res.message, icon: 'none' })
        }
      } finally {
        this.commentSubmitting = false
      }
    },
    replyTo(comment, reply) {
      if (!this.ensureLogin()) return
      const target = reply || comment
      uni.showModal({
        title: `回复 @${target.username}`,
        editable: true,
        placeholderText: '输入回复内容',
        success: async (r) => {
          if (!r.confirm || !r.content) return
          const res = await addNoteComment(this.noteId, {
            parentId: comment.id,
            replyUid: target.userId,
            content: r.content
          })
          if (res.code === 200) {
            this.commentsPage = 1
            this.comments = []
            if (this.note) this.note.commentCount = (this.note.commentCount || 0) + 1
            this.fetchComments()
          } else {
            uni.showToast({ title: res.message, icon: 'none' })
          }
        }
      })
    },
    removeComment(commentId) {
      uni.showModal({
        title: '提示',
        content: '确定删除该评论吗？',
        success: async (r) => {
          if (!r.confirm) return
          const res = await deleteNoteComment(commentId)
          if (res.code === 200) {
            if (this.note) this.note.commentCount = Math.max((this.note.commentCount || 0) - 1, 0)
            this.commentsPage = 1
            this.comments = []
            this.fetchComments()
          } else {
            uni.showToast({ title: res.message, icon: 'none' })
          }
        }
      })
    },
    formatTime(value) {
      if (!value) return ''
      return String(value).replace('T', ' ').slice(0, 16)
    }
  }
}
</script>

<style scoped>
.note-detail {
  min-height: 100vh;
  background-color: var(--background-color, #f8faff);
  padding: 24rpx;
}

.header {
  background-color: #fff;
  border-radius: 16rpx;
  padding: 28rpx;
}

.title {
  font-size: 38rpx;
  font-weight: 700;
  color: var(--text-color, #1f2329);
  line-height: 1.5;
}

.author-row {
  margin-top: 20rpx;
  display: flex;
  align-items: center;
  gap: 16rpx;
}

.avatar {
  width: 72rpx;
  height: 72rpx;
  border-radius: 50%;
  background-color: #eef2f8;
}

.author-info {
  display: flex;
  flex-direction: column;
  flex: 1;
}

.author-name {
  font-size: 28rpx;
  font-weight: 600;
  color: var(--text-color, #1f2329);
}

.publish-time {
  font-size: 22rpx;
  color: var(--text-secondary-color, #7d7e80);
}

.dest {
  font-size: 24rpx;
  color: var(--primary-color, #3a80d2);
}

.trip-info {
  margin-top: 20rpx;
  display: flex;
  flex-wrap: wrap;
  gap: 24rpx;
  padding: 16rpx 20rpx;
  background-color: #f4f7fc;
  border-radius: 12rpx;
}

.trip-item {
  font-size: 24rpx;
  color: var(--text-secondary-color, #7d7e80);
}

.tags {
  margin-top: 20rpx;
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
}

.tag {
  font-size: 24rpx;
  color: var(--primary-color, #3a80d2);
}

.gallery {
  margin-top: 24rpx;
  display: flex;
  flex-wrap: wrap;
  gap: 12rpx;
}

.gallery-item {
  width: 218rpx;
  height: 218rpx;
  border-radius: 12rpx;
  background-color: #eef2f8;
}

.content {
  margin-top: 28rpx;
  background-color: #fff;
  border-radius: 16rpx;
  padding: 28rpx;
  font-size: 30rpx;
  line-height: 1.9;
  color: var(--text-color, #1f2329);
}

.content-plain {
  white-space: pre-wrap;
}

.section {
  margin-top: 28rpx;
  background-color: #fff;
  border-radius: 16rpx;
  padding: 28rpx;
}

.section-title {
  display: block;
  font-size: 30rpx;
  font-weight: 600;
  color: var(--text-color, #1f2329);
  margin-bottom: 20rpx;
}

.relation-item {
  display: flex;
  align-items: center;
  gap: 16rpx;
  padding: 12rpx 0;
}

.relation-cover {
  width: 88rpx;
  height: 88rpx;
  border-radius: 12rpx;
  background-color: #eef2f8;
}

.relation-info {
  display: flex;
  flex-direction: column;
}

.relation-type {
  font-size: 22rpx;
  color: var(--primary-color, #3a80d2);
}

.relation-name {
  font-size: 28rpx;
  color: var(--text-color, #1f2329);
}

.route-item {
  padding: 14rpx 0;
  border-bottom: 1rpx solid #f0f2f5;
}

.route-day {
  font-size: 24rpx;
  color: var(--primary-color, #3a80d2);
  margin-right: 12rpx;
}

.route-poi {
  font-size: 28rpx;
  color: var(--text-color, #1f2329);
}

.route-remark {
  display: block;
  margin-top: 6rpx;
  font-size: 24rpx;
  color: var(--text-secondary-color, #7d7e80);
}

.actions {
  margin-top: 28rpx;
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
}

.action-btn {
  padding: 14rpx 28rpx;
  background-color: #fff;
  border-radius: 36rpx;
  font-size: 26rpx;
  color: var(--text-secondary-color, #7d7e80);
}

.action-btn.active {
  background-color: #ffe9e9;
  color: #e64340;
}

.comment-input-row {
  display: flex;
  gap: 16rpx;
  margin-bottom: 24rpx;
}

.comment-input {
  flex: 1;
  height: 72rpx;
  padding: 0 24rpx;
  background-color: #f4f7fc;
  border-radius: 36rpx;
  font-size: 26rpx;
}

.comment-submit {
  height: 72rpx;
  padding: 0 32rpx;
  background-color: var(--primary-color, #3a80d2);
  color: #fff;
  border: none;
  border-radius: 36rpx;
  font-size: 26rpx;
}

.comment-item {
  display: flex;
  gap: 16rpx;
  padding: 20rpx 0;
  border-bottom: 1rpx solid #f0f2f5;
}

.comment-avatar {
  width: 64rpx;
  height: 64rpx;
  border-radius: 50%;
  background-color: #eef2f8;
  flex-shrink: 0;
}

.comment-body {
  flex: 1;
  min-width: 0;
}

.comment-head {
  display: flex;
  align-items: baseline;
  gap: 16rpx;
}

.comment-user {
  font-size: 26rpx;
  font-weight: 600;
  color: var(--text-color, #1f2329);
}

.comment-time {
  font-size: 22rpx;
  color: var(--text-secondary-color, #7d7e80);
}

.comment-text {
  display: block;
  margin-top: 8rpx;
  font-size: 28rpx;
  line-height: 1.7;
  color: var(--text-color, #1f2329);
}

.comment-ops {
  margin-top: 8rpx;
  display: flex;
  gap: 28rpx;
}

.comment-op {
  font-size: 24rpx;
  color: var(--primary-color, #3a80d2);
}

.comment-op--danger {
  color: #e64340;
}

.reply-item {
  margin-top: 16rpx;
  padding: 16rpx;
  background-color: #f8fafd;
  border-radius: 12rpx;
}

.load-more {
  text-align: center;
  padding: 24rpx 0;
  color: var(--primary-color, #3a80d2);
  font-size: 26rpx;
}

.empty-tip {
  text-align: center;
  padding: 120rpx 0;
  color: var(--text-secondary-color, #7d7e80);
  font-size: 28rpx;
}
</style>
