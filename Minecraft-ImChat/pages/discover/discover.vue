<template>
  <view class="discover-page">
    <CustomNavBar title="发现" @search="onSearch" @add="onAdd" />

    <!-- 功能宫格 -->
    <view class="function-grid">
      <view
        v-for="(item, index) in functionList"
        :key="item.id"
        :style="{ animationDelay: `${index * 0.05}s` }"
        class="function-card fade-in"
        @click="handleClick(item)"
      >
        <view class="function-icon" :style="{ background: item.bg }">
          <text class="icon-emoji">{{ item.icon }}</text>
        </view>
        <view class="function-info">
          <text class="function-name">{{ item.name }}</text>
          <text v-if="item.badge" class="function-badge">{{ item.badge }}</text>
        </view>
        <view class="function-arrow">›</view>
      </view>
    </view>

    <!-- 热门推荐 -->
    <view class="section">
      <view class="section-header">
        <text class="section-title">热门推荐</text>
        <view class="section-more" @click="goToMore">
          <text>查看更多</text>
          <text class="more-arrow">›</text>
        </view>
      </view>

      <scroll-view class="recommend-scroll" scroll-x>
        <view
          v-for="(item, index) in recommendList"
          :key="item.id"
          :style="{ animationDelay: `${index * 0.08}s` }"
          class="recommend-card slide-right"
          @click="goToDetail(item)"
        >
          <image
            class="recommend-cover"
            :src="item.cover"
            mode="aspectFill"
          />
          <view class="recommend-info">
            <text class="recommend-title text-ellipsis">{{ item.title }}</text>
            <view class="recommend-meta">
              <text class="meta-likes">♥ {{ item.likes }}</text>
              <text class="meta-views">👁 {{ item.views }}</text>
            </view>
          </view>
        </view>
      </scroll-view>
    </view>

    <!-- 话题列表 -->
    <view class="section">
      <view class="section-header">
        <text class="section-title">热门话题</text>
        <view class="section-more" @click="goToTopic">
          <text>更多话题</text>
          <text class="more-arrow">›</text>
        </view>
      </view>

      <view class="topic-list">
        <view
          v-for="(topic, index) in topicList"
          :key="topic.id"
          :style="{ animationDelay: `${index * 0.05}s` }"
          class="topic-item fade-in"
          @click="joinTopic(topic)"
        >
          <view class="topic-rank" :class="getRankClass(index)">
            <text>{{ index + 1 }}</text>
          </view>
          <view class="topic-content">
            <text class="topic-title text-ellipsis">{{ topic.title }}</text>
            <view class="topic-meta">
              <text class="meta-heat">🔥 {{ topic.heat }}</text>
              <text class="meta-category">{{ topic.category }}</text>
            </view>
          </view>
          <view class="topic-action" @click.stop="joinTopic(topic)">
            <text>参与</text>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import { getUserInfo } from '../../utils/storage.js'
import CustomNavBar from '../../components/CustomNavBar.vue'

export default {
  components: {
    CustomNavBar
  },
  data() {
    return {
      // 功能列表
      functionList: [
        {
          id: 'moments',
          name: '朋友圈',
          icon: '📷',
          bg: 'linear-gradient(135deg, #FF6B6B 0%, #FF8E53 100%)',
          badge: ''
        },
        {
          id: 'video',
          name: '视频号',
          icon: '🎬',
          bg: 'linear-gradient(135deg, #5856D6 0%, #AF52DE 100%)',
          badge: 'NEW'
        },
        {
          id: 'live',
          name: '直播',
          icon: '📺',
          bg: 'linear-gradient(135deg, #FF3B30 0%, #FF9500 100%)',
          badge: ''
        },
        {
          id: 'scan',
          name: '扫一扫',
          icon: '📷',
          bg: 'linear-gradient(135deg, #07C160 0%, #34C759 100%)',
          badge: ''
        },
        {
          id: 'nearby',
          name: '附近',
          icon: '📍',
          bg: 'linear-gradient(135deg, #007AFF 0%, #5AC8FA 100%)',
          badge: ''
        },
        {
          id: 'shop',
          name: '购物',
          icon: '🛍️',
          bg: 'linear-gradient(135deg, #FF9500 0%, #FFCC00 100%)',
          badge: ''
        },
        {
          id: 'game',
          name: '小游戏',
          icon: '🎮',
          bg: 'linear-gradient(135deg, #AF52DE 0%, #FF2D55 100%)',
          badge: ''
        },
        {
          id: 'mini',
          name: '小程序',
          icon: '⚡',
          bg: 'linear-gradient(135deg, #1D1D1F 0%, #6E6E73 100%)',
          badge: ''
        }
      ],

      // 推荐列表
      recommendList: [
        {
          id: 1,
          title: '春日旅行攻略：十大必去目的地',
          cover: 'https://picsum.photos/seed/spring/400/300',
          likes: 1280,
          views: '12.5w'
        },
        {
          id: 2,
          title: '城市夜景摄影技巧分享',
          cover: 'https://picsum.photos/seed/night/400/300',
          likes: 892,
          views: '8.3w'
        },
        {
          id: 3,
          title: '美食探店：本地人气餐厅推荐',
          cover: 'https://picsum.photos/seed/food/400/300',
          likes: 2156,
          views: '20.1w'
        },
        {
          id: 4,
          title: '周末徒步路线全攻略',
          cover: 'https://picsum.photos/seed/hike/400/300',
          likes: 543,
          views: '5.7w'
        }
      ],

      // 话题列表
      topicList: [
        {
          id: 1,
          title: '#春日旅行计划#',
          heat: '128.5w',
          category: '旅行'
        },
        {
          id: 2,
          title: '#美食探店vlog#',
          heat: '98.2w',
          category: '美食'
        },
        {
          id: 3,
          title: '#城市夜跑路线#',
          heat: '76.8w',
          category: '运动'
        },
        {
          id: 4,
          title: '#周末好去处#',
          heat: '54.3w',
          category: '生活'
        },
        {
          id: 5,
          title: '#摄影技巧分享#',
          heat: '42.1w',
          category: '摄影'
        }
      ]
    }
  },

  onLoad() {
    const userInfo = getUserInfo()
    if (userInfo) {
      this.userInfo = userInfo
    }
  },

  onShow() {
    // 页面显示时的逻辑
  },

  onPullDownRefresh() {
    setTimeout(() => {
      uni.stopPullDownRefresh()
      uni.showToast({ title: '刷新成功', icon: 'success' })
    }, 800)
  },

  methods: {
    /**
     * 点击功能项
     */
    handleClick(item) {
      uni.showToast({
        title: `${item.name} - 开发中`,
        icon: 'none'
      })
    },

    /**
     * 跳转到详情
     */
    goToDetail(item) {
      uni.showToast({
        title: `查看：${item.title}`,
        icon: 'none'
      })
    },

    /**
     * 参与话题
     */
    joinTopic(topic) {
      uni.showToast({
        title: `参与 ${topic.title}`,
        icon: 'none'
      })
    },

    /**
     * 查看更多
     */
    goToMore() {
      uni.showToast({ title: '更多推荐开发中', icon: 'none' })
    },

    /**
     * 更多话题
     */
    goToTopic() {
      uni.showToast({ title: '话题广场开发中', icon: 'none' })
    },

    /**
     * 获取排名样式
     */
    getRankClass(index) {
      if (index === 0) return 'rank-gold'
      if (index === 1) return 'rank-silver'
      if (index === 2) return 'rank-bronze'
      return 'rank-normal'
    },

    onSearch() {
      uni.showToast({ title: '搜索功能开发中', icon: 'none' })
    },

    onAdd() {
      uni.showToast({ title: '添加功能开发中', icon: 'none' })
    }
  }
}
</script>

<style scoped>
.discover-page {
  min-height: 100vh;
  background-color: #f5f5f7;
  padding-bottom: 40rpx;
}

/* 功能宫格 */
.function-grid {
  background-color: #ffffff;
  margin: 20rpx 24rpx;
  border-radius: 24rpx;
  overflow: hidden;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.04);
  margin-top: calc(var(--status-bar-height, 44px) + 120rpx);
}

.function-card {
  display: flex;
  align-items: center;
  padding: 28rpx 32rpx;
  border-bottom: 1rpx solid #f0f0f0;
  transition: all 0.2s ease;
}

.function-card:last-child {
  border-bottom: none;
}

.function-card:active {
  background-color: #f5f5f7;
  transform: scale(0.995);
}

.function-icon {
  width: 80rpx;
  height: 80rpx;
  border-radius: 20rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 24rpx;
  flex-shrink: 0;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.08);
}

.icon-emoji {
  font-size: 40rpx;
  line-height: 1;
}

.function-info {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 16rpx;
}

.function-name {
  font-size: 32rpx;
  color: #1d1d1f;
  font-weight: 500;
  letter-spacing: -0.3rpx;
}

.function-badge {
  font-size: 20rpx;
  color: #ffffff;
  background-color: #ff3b30;
  padding: 4rpx 12rpx;
  border-radius: 8rpx;
  font-weight: 600;
  line-height: 1.2;
}

.function-arrow {
  font-size: 36rpx;
  color: #c7c7cc;
  flex-shrink: 0;
  transition: all 0.2s ease;
}

.function-card:active .function-arrow {
  color: #07c160;
  transform: translateX(8rpx);
}

/* 通用 section */
.section {
  margin-top: 32rpx;
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 32rpx 20rpx;
}

.section-title {
  font-size: 30rpx;
  color: #1d1d1f;
  font-weight: 700;
  letter-spacing: -0.3rpx;
}

.section-more {
  display: flex;
  align-items: center;
  gap: 4rpx;
  color: #a1a1a6;
  font-size: 24rpx;
}

.more-arrow {
  font-size: 24rpx;
}

/* 推荐卡片 - 横向滚动 */
.recommend-scroll {
  white-space: nowrap;
  padding: 0 24rpx;
  box-sizing: border-box;
}

.recommend-card {
  display: inline-block;
  width: 280rpx;
  margin-right: 20rpx;
  background-color: #ffffff;
  border-radius: 20rpx;
  overflow: hidden;
  vertical-align: top;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.04);
  transition: all 0.2s ease;
}

.recommend-card:last-child {
  margin-right: 24rpx;
}

.recommend-card:active {
  transform: scale(0.98);
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.08);
}

.recommend-cover {
  width: 100%;
  height: 200rpx;
  background-color: #f5f5f7;
  display: block;
}

.recommend-info {
  padding: 16rpx 20rpx 20rpx;
  white-space: normal;
}

.recommend-title {
  font-size: 28rpx;
  color: #1d1d1f;
  font-weight: 600;
  line-height: 1.4;
  margin-bottom: 12rpx;
  display: block;
}

.recommend-meta {
  display: flex;
  align-items: center;
  gap: 16rpx;
  font-size: 22rpx;
  color: #a1a1a6;
}

.meta-likes, .meta-views {
  font-size: 22rpx;
  color: #a1a1a6;
}

/* 话题列表 */
.topic-list {
  background-color: #ffffff;
  margin: 0 24rpx;
  border-radius: 24rpx;
  overflow: hidden;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.04);
}

.topic-item {
  display: flex;
  align-items: center;
  padding: 24rpx 32rpx;
  border-bottom: 1rpx solid #f5f5f7;
  transition: all 0.2s ease;
}

.topic-item:last-child {
  border-bottom: none;
}

.topic-item:active {
  background-color: #f5f5f7;
}

.topic-rank {
  width: 48rpx;
  height: 48rpx;
  border-radius: 12rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 24rpx;
  flex-shrink: 0;
  font-size: 26rpx;
  font-weight: 700;
  color: #ffffff;
}

.rank-gold {
  background: linear-gradient(135deg, #FFD700 0%, #FFA500 100%);
}

.rank-silver {
  background: linear-gradient(135deg, #C0C0C0 0%, #909399 100%);
}

.rank-bronze {
  background: linear-gradient(135deg, #CD7F32 0%, #A0522D 100%);
}

.rank-normal {
  background-color: #e5e5ea;
  color: #6e6e73;
}

.topic-rank text {
  font-size: 24rpx;
  font-weight: 700;
}

.topic-content {
  flex: 1;
  overflow: hidden;
  min-width: 0;
}

.topic-title {
  font-size: 30rpx;
  color: #1d1d1f;
  font-weight: 600;
  display: block;
  margin-bottom: 8rpx;
}

.topic-meta {
  display: flex;
  align-items: center;
  gap: 16rpx;
  font-size: 22rpx;
  color: #a1a1a6;
}

.meta-heat {
  color: #ff9500;
  font-weight: 500;
}

.meta-category {
  color: #a1a1a6;
}

.topic-action {
  flex-shrink: 0;
  padding: 12rpx 28rpx;
  background: linear-gradient(135deg, #07C160 0%, #06AD56 100%);
  border-radius: 32rpx;
  transition: all 0.2s ease;
  margin-left: 16rpx;
}

.topic-action:active {
  transform: scale(0.95);
  opacity: 0.9;
}

.topic-action text {
  color: #ffffff;
  font-size: 24rpx;
  font-weight: 500;
}

/* 动画 */
.fade-in {
  animation: fadeInUp 0.4s ease forwards;
  opacity: 0;
}

.slide-right {
  animation: slideInRight 0.5s ease forwards;
  opacity: 0;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20rpx);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes slideInRight {
  from {
    opacity: 0;
    transform: translateX(40rpx);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}
</style>
