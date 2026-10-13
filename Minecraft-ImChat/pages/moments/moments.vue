<template>
  <view class="moments-page">
    <scroll-view
      class="moments-scroll"
      scroll-y
      :show-scrollbar="false"
      @scrolltolower="onReachLower"
    >
      <!-- 封面 -->
      <view class="cover" :style="coverStyle">
        <view class="cover__status" :style="{ height: statusBarHeight + 'px' }"></view>
        <view class="cover__bar" :style="{ top: (statusBarHeight + 8) + 'px' }">
          <view class="cover__back" @click="goBack">
            <UiIcon name="back" :size="48" color="#FFFFFF" />
          </view>
          <view class="cover__change" @click="chooseCover">
            <UiIcon name="image" :size="30" color="#FFFFFF" />
            <text>更换封面</text>
          </view>
        </view>
        <view class="cover__spacer"></view>
        <view class="cover__entry" @click="openComposer">
          <text class="cover__name">{{ profile.name }}</text>
          <UiAvatar shape="square" :size="80" :src="profile.avatar" :name="profile.name" />
        </view>
      </view>

      <!-- 信息流 -->
      <view class="feed">
        <view v-for="moment in moments" :key="moment.id" class="mom-card">
          <view class="mom-card__main">
            <view class="mom-card__avatar">
              <UiAvatar
                shape="square"
                :size="80"
                :src="moment.avatar"
                :name="moment.nickname || moment.username"
              />
            </view>
            <view class="mom-card__content">
              <text class="mom-card__name">{{ moment.nickname || moment.username }}</text>
              <text v-if="moment.content" class="mom-card__text">{{ moment.content }}</text>

              <view
                v-if="moment.images && moment.images.length"
                class="mom-grid"
                :class="gridClass(moment.images)"
              >
                <view
                  v-for="(img, i) in moment.images"
                  :key="i"
                  class="mom-grid__item"
                  @click="previewImages(moment.images, img)"
                >
                  <image class="mom-grid__img" :src="img" mode="aspectFill" />
                </view>
              </view>

              <view v-if="moment.location" class="mom-card__loc">
                <UiIcon name="map-pin" :size="24" color="#888888" />
                <text>{{ moment.location }}</text>
              </view>

              <view class="mom-card__foot">
                <text class="mom-card__time">{{ formatRelativeTime(moment.createTime) }}</text>
                <view class="mom-card__more" @click="openActions(moment)">
                  <UiIcon name="more" :size="40" color="#888888" />
                </view>
              </view>

              <view v-if="hasInteraction(moment)" class="mom-inter">
                <view v-if="moment.likes && moment.likes.length" class="mom-inter__likes">
                  <UiIcon name="heart-filled" :size="28" color="#576B95" />
                  <text class="mom-inter__names">{{ likeNames(moment) }}</text>
                </view>
                <view v-if="moment.comments && moment.comments.length" class="mom-inter__comments">
                  <view
                    v-for="(c, ci) in moment.comments"
                    :key="c.id || ci"
                    class="mom-inter__comment"
                    @longpress="onCommentLongPress(moment, c)"
                  >
                    <text class="mom-inter__cname">{{ displayName(c) }}</text>
                    <text v-if="c.replyToUserId" class="mom-inter__reply"> 回复 </text>
                    <text v-if="c.replyToUserId" class="mom-inter__cname">{{ c.replyToNickname }}</text>
                    <text class="mom-inter__ctext">：{{ c.content }}</text>
                  </view>
                </view>
              </view>
            </view>
          </view>
        </view>

        <UiEmpty v-if="!moments.length && !loading" icon="🖼️" text="还没有动态，快去发布吧" />
        <view v-if="loading && moments.length" class="mom-footer">
          <text>加载中…</text>
        </view>
      </view>

      <view class="moments-safe"></view>
    </scroll-view>

    <!-- 发布弹层 -->
    <view v-if="composerVisible" class="sheet-mask" @click="closeComposer"></view>
    <view v-if="composerVisible" class="composer" @click.stop>
      <view class="composer__head">
        <text class="composer__cancel" @click="closeComposer">取消</text>
        <view
          class="composer__publish"
          :class="{ 'is-disabled': !canPublish }"
          @click="submitPublish"
        >
          <text>{{ publishing ? '发表中…' : '发表' }}</text>
        </view>
      </view>
      <scroll-view scroll-y class="composer__body">
        <textarea
          class="composer__text"
          v-model="composerText"
          :maxlength="500"
          placeholder="这一刻的想法…"
          placeholder-class="composer__ph"
          :adjust-position="true"
          auto-height
        />
        <view class="composer__grid">
          <view v-for="(img, i) in composerImages" :key="i" class="composer__cell">
            <image class="composer__img" :src="img" mode="aspectFill" />
            <view class="composer__del" @click="removeComposerImage(i)">
              <UiIcon name="close" :size="24" color="#FFFFFF" />
            </view>
          </view>
          <view
            v-if="composerImages.length < 9"
            class="composer__cell composer__cell--add"
            @click="pickComposerImages"
          >
            <UiIcon name="plus" :size="52" color="#B2B2B2" />
          </view>
        </view>
        <view class="composer__row" @click="chooseVisibility">
          <UiIcon name="eye" :size="36" color="#576B95" />
          <text class="composer__row-label">{{ visibilityLabel }}</text>
          <UiIcon name="chevron-right" :size="36" color="#C8C8CD" />
        </view>
        <view class="composer__row">
          <UiIcon name="map-pin" :size="36" color="#576B95" />
          <input
            class="composer__loc-input"
            v-model="composerLocation"
            :maxlength="50"
            placeholder="所在位置"
            placeholder-class="composer__ph"
          />
        </view>
      </scroll-view>
      <view class="composer__safe"></view>
    </view>

    <!-- 评论输入 -->
    <view v-if="commentVisible" class="sheet-mask" @click="closeComment"></view>
    <view v-if="commentVisible" class="comment-bar" @click.stop>
      <input
        class="comment-bar__input"
        v-model="commentText"
        :focus="commentFocus"
        :placeholder="commentPlaceholder"
        placeholder-class="composer__ph"
        confirm-type="send"
        :cursor-spacing="10"
        @confirm="submitComment"
      />
      <view
        class="comment-bar__send"
        :class="{ 'is-disabled': !commentText.trim() }"
        @click="submitComment"
      >
        <text>发送</text>
      </view>
    </view>
  </view>
</template>

<script>
import UiIcon from '../../components/Icon.vue'
import UiAvatar from '../../components/Avatar.vue'
import UiEmpty from '../../components/EmptyState.vue'
import {
  getMomentFeed,
  getMomentCover,
  uploadMomentCover,
  publishMoment,
  toggleMomentLike,
  getMomentLikes,
  addMomentComment,
  getMomentComments,
  deleteMomentComment,
  deleteMoment as deleteMomentApi
} from '../../utils/moment-api.js'
import { uploadFile } from '../../utils/chat-api.js'
import { getUserId, getCurrentProfile } from '../../utils/session.js'
import { formatRelativeTime } from '../../utils/format.js'

export default {
  components: { UiIcon, UiAvatar, UiEmpty },
  data() {
    return {
      userId: null,
      profile: { name: '', avatar: '' },
      statusBarHeight: 20,
      coverUrl: '',

      moments: [],
      page: 1,
      size: 10,
      hasMore: true,
      loading: false,

      composerVisible: false,
      composerText: '',
      composerImages: [],
      composerLocation: '',
      composerVisibility: 0,
      publishing: false,

      commentVisible: false,
      commentFocus: false,
      commentText: '',
      commentTarget: null,
      replyTarget: null
    }
  },
  computed: {
    coverStyle() {
      if (this.coverUrl) {
        return {
          backgroundImage: `url(${this.coverUrl})`,
          backgroundSize: 'cover',
          backgroundPosition: 'center'
        }
      }
      return {
        backgroundImage: 'linear-gradient(135deg, #232526 0%, #414345 100%)'
      }
    },
    canPublish() {
      return !this.publishing && (this.composerText.trim().length > 0 || this.composerImages.length > 0)
    },
    visibilityLabel() {
      return ['公开', '仅好友可见', '私密'][this.composerVisibility] || '公开'
    },
    commentPlaceholder() {
      return this.replyTarget ? `回复 ${this.replyTarget.nickname}：` : '评论'
    }
  },
  onLoad() {
    this.profile = getCurrentProfile()
    this.userId = getUserId()
    this.statusBarHeight = uni.getSystemInfoSync().statusBarHeight || 20
    this.loadCover()
    this.loadFeed(true)
  },
  methods: {
    formatRelativeTime,
    goBack() {
      uni.navigateBack()
    },
    async loadCover() {
      if (!this.userId) return
      try {
        const res = await getMomentCover(this.userId)
        if (res && res.code === 200 && res.data) {
          this.coverUrl = res.data
        }
      } catch (e) {
        // 无封面时保留渐变兜底
      }
    },
    async loadFeed(reset = false) {
      if (!this.userId || this.loading) return
      if (reset) {
        this.page = 1
        this.hasMore = true
      }
      this.loading = true
      try {
        const res = await getMomentFeed(this.userId, this.page, this.size)
        if (res && res.code === 200 && Array.isArray(res.data)) {
          const list = res.data
          this.moments = reset ? list : this.moments.concat(list)
          this.hasMore = list.length >= this.size
        }
      } catch (e) {
        console.error('加载朋友圈失败:', e)
      } finally {
        this.loading = false
      }
    },
    onReachLower() {
      if (!this.hasMore || this.loading) return
      this.page += 1
      this.loadFeed(false)
    },
    isMine(moment) {
      return Number(moment.userId) === Number(getUserId())
    },
    gridClass(images) {
      const len = (images && images.length) || 0
      if (len <= 1) return 'is-single'
      if (len <= 4) return 'is-2col'
      return 'is-3col'
    },
    hasInteraction(moment) {
      const hasLikes = moment.likes && moment.likes.length
      const hasComments = moment.comments && moment.comments.length
      return !!(hasLikes || hasComments)
    },
    likeNames(moment) {
      return (moment.likes || [])
        .map((l) => l.nickname || l.username || '')
        .filter(Boolean)
        .join('，')
    },
    displayName(comment) {
      return comment.nickname || comment.username || ''
    },
    previewImages(images, current) {
      uni.previewImage({ urls: images, current })
    },
    openActions(moment) {
      const items = [moment.liked ? '取消赞' : '赞', '评论']
      if (this.isMine(moment)) items.push('删除')
      uni.showActionSheet({
        itemList: items,
        success: (res) => {
          if (res.tapIndex === 0) this.toggleLike(moment)
          else if (res.tapIndex === 1) this.openComment(moment)
          else if (res.tapIndex === 2 && this.isMine(moment)) this.deleteMoment(moment)
        }
      })
    },
    async toggleLike(moment) {
      try {
        const res = await toggleMomentLike(moment.id, this.userId)
        if (res && res.code === 200 && res.data) {
          moment.liked = !!res.data.liked
          moment.likeCount = res.data.likeCount
        }
        const likesRes = await getMomentLikes(moment.id)
        if (likesRes && likesRes.code === 200 && Array.isArray(likesRes.data)) {
          moment.likes = likesRes.data
        }
      } catch (e) {
        uni.showToast({ title: '操作失败', icon: 'none' })
      }
    },
    deleteMoment(moment) {
      uni.showModal({
        title: '提示',
        content: '确定删除这条动态吗？',
        success: async (r) => {
          if (!r.confirm) return
          try {
            const res = await deleteMomentApi(moment.id, this.userId)
            if (res && res.code === 200) {
              this.moments = this.moments.filter((m) => m.id !== moment.id)
              uni.showToast({ title: '已删除', icon: 'success' })
            }
          } catch (e) {
            uni.showToast({ title: '删除失败', icon: 'none' })
          }
        }
      })
    },
    openComment(moment, comment) {
      this.commentTarget = moment
      this.replyTarget = comment
        ? { userId: comment.userId, nickname: comment.nickname || comment.username }
        : null
      this.commentText = ''
      this.commentVisible = true
      this.$nextTick(() => {
        this.commentFocus = true
      })
    },
    closeComment() {
      this.commentVisible = false
      this.commentFocus = false
      this.commentText = ''
      this.commentTarget = null
      this.replyTarget = null
    },
    submitComment() {
      const content = this.commentText.trim()
      if (!content || !this.commentTarget) return
      const moment = this.commentTarget
      const payload = {
        userId: this.userId,
        content,
        replyToUserId: this.replyTarget ? this.replyTarget.userId : null
      }
      addMomentComment(moment.id, payload)
        .then((res) => {
          if (res && res.code === 200) {
            if (res.data) {
              if (!moment.comments) moment.comments = []
              moment.comments.push(res.data)
              moment.commentCount = (moment.commentCount || 0) + 1
            }
            this.closeComment()
          } else {
            uni.showToast({ title: (res && res.message) || '评论失败', icon: 'none' })
          }
        })
        .catch(() => {
          uni.showToast({ title: '评论失败', icon: 'none' })
        })
    },
    onCommentLongPress(moment, comment) {
      if (Number(comment.userId) !== Number(getUserId())) return
      uni.showModal({
        title: '提示',
        content: '确定删除这条评论吗？',
        success: async (r) => {
          if (!r.confirm) return
          try {
            const res = await deleteMomentComment(comment.id, this.userId)
            if (res && res.code === 200) {
              const cRes = await getMomentComments(moment.id)
              if (cRes && cRes.code === 200 && Array.isArray(cRes.data)) {
                moment.comments = cRes.data
                moment.commentCount = cRes.data.length
              }
            }
          } catch (e) {
            uni.showToast({ title: '删除失败', icon: 'none' })
          }
        }
      })
    },
    openComposer() {
      this.resetComposer()
      this.composerVisible = true
    },
    closeComposer() {
      this.composerVisible = false
      this.resetComposer()
    },
    resetComposer() {
      this.composerText = ''
      this.composerImages = []
      this.composerLocation = ''
      this.composerVisibility = 0
    },
    chooseVisibility() {
      uni.showActionSheet({
        itemList: ['公开', '仅好友可见', '私密'],
        success: (res) => {
          this.composerVisibility = res.tapIndex
        }
      })
    },
    pickComposerImages() {
      const remain = 9 - this.composerImages.length
      if (remain <= 0) return
      uni.chooseImage({
        count: remain,
        success: async (res) => {
          uni.showLoading({ title: '上传中…', mask: true })
          try {
            for (const path of res.tempFilePaths) {
              const uploadRes = await uploadFile(path)
              if (uploadRes && uploadRes.code === 200 && uploadRes.data) {
                this.composerImages.push(uploadRes.data)
              }
            }
          } catch (e) {
            uni.showToast({ title: '图片上传失败', icon: 'none' })
          } finally {
            uni.hideLoading()
          }
        }
      })
    },
    removeComposerImage(index) {
      this.composerImages.splice(index, 1)
    },
    async chooseCover() {
      uni.chooseImage({
        count: 1,
        success: async (res) => {
          const path = res.tempFilePaths[0]
          if (!path) return
          uni.showLoading({ title: '上传中…', mask: true })
          try {
            const uploadRes = await uploadMomentCover(this.userId, path)
            if (uploadRes && uploadRes.code === 200 && uploadRes.data) {
              this.coverUrl = uploadRes.data
              uni.showToast({ title: '封面已更新', icon: 'success' })
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
    async submitPublish() {
      if (!this.canPublish) return
      this.publishing = true
      try {
        const res = await publishMoment({
          userId: this.userId,
          content: this.composerText.trim(),
          images: this.composerImages,
          location: this.composerLocation.trim() || null,
          visibility: this.composerVisibility
        })
        if (res && res.code === 200) {
          this.closeComposer()
          this.loadFeed(true)
          uni.showToast({ title: '发表成功', icon: 'success' })
        } else {
          uni.showToast({ title: (res && res.message) || '发表失败', icon: 'none' })
        }
      } catch (e) {
        uni.showToast({ title: '发表失败', icon: 'none' })
      } finally {
        this.publishing = false
      }
    }
  }
}
</script>

<style scoped>
.moments-page {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background-color: #ededed;
}

.moments-scroll {
  flex: 1;
  height: 0;
}

/* 封面 */
.cover {
  position: relative;
  background-color: #414345;
  background-size: cover;
  background-position: center;
}

.cover__bar {
  position: absolute;
  left: 0;
  right: 0;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24rpx;
  z-index: 2;
}

.cover__back {
  width: 64rpx;
  height: 64rpx;
  border-radius: 50%;
  background-color: rgba(0, 0, 0, 0.25);
  display: flex;
  align-items: center;
  justify-content: center;
}

.cover__back:active {
  background-color: rgba(0, 0, 0, 0.4);
}

.cover__change {
  display: flex;
  align-items: center;
  height: 56rpx;
  padding: 0 20rpx;
  border-radius: 28rpx;
  background-color: rgba(255, 255, 255, 0.25);
}

.cover__change:active {
  background-color: rgba(255, 255, 255, 0.4);
}

.cover__change text {
  font-size: 24rpx;
  color: #ffffff;
  margin-left: 8rpx;
}

.cover__spacer {
  height: 360rpx;
}

.cover__entry {
  position: absolute;
  right: 24rpx;
  bottom: 16rpx;
  display: flex;
  align-items: center;
}

.cover__entry:active {
  opacity: 0.85;
}

.cover__name {
  font-size: 30rpx;
  color: #ffffff;
  font-weight: 500;
  margin-right: 16rpx;
  text-shadow: 0 1rpx 6rpx rgba(0, 0, 0, 0.4);
}

/* 信息流 */
.feed {
  background-color: #ffffff;
}

.mom-card {
  position: relative;
  padding: 24rpx 32rpx;
  background-color: #ffffff;
}

.mom-card::after {
  content: '';
  position: absolute;
  left: 0;
  right: 0;
  bottom: 0;
  height: 1rpx;
  background-color: #ededed;
  transform: scaleY(0.5);
}

.mom-card__main {
  display: flex;
  align-items: flex-start;
}

.mom-card__avatar {
  margin-right: 20rpx;
}

.mom-card__content {
  flex: 1;
  min-width: 0;
}

.mom-card__name {
  display: block;
  font-size: 28rpx;
  font-weight: 600;
  color: #576b95;
  line-height: 1.3;
  margin-bottom: 8rpx;
}

.mom-card__text {
  display: block;
  font-size: 30rpx;
  color: #191919;
  line-height: 1.5;
  word-break: break-word;
  white-space: pre-wrap;
}

/* 九宫格 */
.mom-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 12rpx;
  margin-top: 14rpx;
}

.mom-grid__item {
  border-radius: 8rpx;
  overflow: hidden;
  background-color: #f0f0f0;
}

.mom-grid__item:active {
  opacity: 0.85;
}

.mom-grid.is-single .mom-grid__item {
  width: 340rpx;
  height: 340rpx;
}

.mom-grid.is-2col .mom-grid__item {
  width: 282rpx;
  height: 282rpx;
}

.mom-grid.is-3col .mom-grid__item {
  width: 182rpx;
  height: 182rpx;
}

.mom-grid__img {
  width: 100%;
  height: 100%;
  display: block;
}

.mom-card__loc {
  display: flex;
  align-items: center;
  margin-top: 14rpx;
}

.mom-card__loc text {
  font-size: 24rpx;
  color: #888888;
  margin-left: 6rpx;
}

.mom-card__foot {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 16rpx;
}

.mom-card__time {
  font-size: 24rpx;
  color: #b2b2b2;
}

.mom-card__more {
  width: 56rpx;
  height: 44rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.mom-card__more:active {
  opacity: 0.5;
}

/* 点赞/评论 */
.mom-inter {
  margin-top: 12rpx;
  background-color: #f7f7f7;
  border-radius: 8rpx;
  padding: 4rpx 16rpx;
}

.mom-inter__likes {
  display: flex;
  align-items: center;
  padding: 12rpx 0;
}

.mom-inter__names {
  flex: 1;
  font-size: 26rpx;
  color: #576b95;
  margin-left: 8rpx;
  line-height: 1.4;
}

.mom-inter__comments {
  border-top: 1rpx solid #e5e5e5;
}

.mom-inter__comment {
  padding: 12rpx 0;
}

.mom-inter__comment + .mom-inter__comment {
  border-top: 1rpx solid #e5e5e5;
}

.mom-inter__cname {
  font-size: 26rpx;
  color: #576b95;
  line-height: 1.5;
}

.mom-inter__reply {
  font-size: 26rpx;
  color: #888888;
}

.mom-inter__ctext {
  font-size: 26rpx;
  color: #191919;
  line-height: 1.5;
  word-break: break-word;
}

.mom-footer {
  display: flex;
  justify-content: center;
  padding: 24rpx 0;
}

.mom-footer text {
  font-size: 24rpx;
  color: #b2b2b2;
}

.moments-safe {
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

/* 发布面板 */
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
}

.composer__publish {
  height: 64rpx;
  padding: 0 36rpx;
  background-color: #07c160;
  border-radius: 10rpx;
  display: flex;
  align-items: center;
}

.composer__publish:active {
  background-color: #06ad56;
}

.composer__publish.is-disabled {
  background-color: #b7ebcb;
}

.composer__publish text {
  font-size: 28rpx;
  color: #ffffff;
  font-weight: 500;
}

.composer__body {
  flex: 1;
  height: 0;
  padding: 24rpx 32rpx;
}

.composer__text {
  width: 100%;
  min-height: 180rpx;
  font-size: 30rpx;
  color: #191919;
  line-height: 1.5;
}

.composer__ph {
  color: #b2b2b2;
}

.composer__grid {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
  margin-top: 20rpx;
}

.composer__cell {
  position: relative;
  width: 210rpx;
  height: 210rpx;
  border-radius: 12rpx;
  overflow: hidden;
  background-color: #f7f7f7;
}

.composer__img {
  width: 100%;
  height: 100%;
  display: block;
}

.composer__del {
  position: absolute;
  top: 0;
  right: 0;
  width: 44rpx;
  height: 44rpx;
  background-color: rgba(0, 0, 0, 0.5);
  border-bottom-left-radius: 12rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.composer__cell--add {
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1rpx solid #e2e2e2;
}

.composer__cell--add:active {
  background-color: #ececec;
}

.composer__row {
  display: flex;
  align-items: center;
  height: 96rpx;
  border-top: 1rpx solid #ededed;
  margin-top: 20rpx;
}

.composer__row:active {
  opacity: 0.7;
}

.composer__row-label {
  flex: 1;
  font-size: 30rpx;
  color: #191919;
  margin-left: 16rpx;
}

.composer__loc-input {
  flex: 1;
  font-size: 30rpx;
  color: #191919;
  margin-left: 16rpx;
}

.composer__safe {
  height: env(safe-area-inset-bottom);
}

/* 评论输入条 */
.comment-bar {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 901;
  display: flex;
  align-items: center;
  padding: 16rpx 24rpx;
  padding-bottom: calc(16rpx + env(safe-area-inset-bottom));
  background-color: #f7f7f7;
  border-top: 1rpx solid #e2e2e2;
  animation: sheetUp 0.22s ease;
}

.comment-bar__input {
  flex: 1;
  height: 68rpx;
  padding: 0 22rpx;
  background-color: #ffffff;
  border-radius: 10rpx;
  font-size: 30rpx;
  color: #191919;
}

.comment-bar__send {
  margin-left: 16rpx;
  height: 68rpx;
  padding: 0 30rpx;
  background-color: #07c160;
  border-radius: 10rpx;
  display: flex;
  align-items: center;
}

.comment-bar__send:active {
  background-color: #06ad56;
}

.comment-bar__send.is-disabled {
  background-color: #b7ebcb;
}

.comment-bar__send text {
  font-size: 28rpx;
  color: #ffffff;
  font-weight: 500;
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
