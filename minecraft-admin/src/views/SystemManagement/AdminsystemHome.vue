<template>
  <div class="home-container">
    <!-- 动态背景 -->
    <div class="animated-bg">
      <div class="gradient-sphere"></div>
      <div class="gradient-sphere second"></div>
    </div>

    <!-- 导航栏 液态玻璃效果 -->
    <header class="glass-nav">
      <div class="nav-container">
        <div class="logo-area">
          <div class="logo-icon">
            <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M12 2L2 7L12 12L22 7L12 2Z" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M2 17L12 22L22 17" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M2 12L12 17L22 12" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
          <span class="logo-text">Minecraft<span>旅游</span></span>
        </div>
        
        <div class="nav-links">
          <a v-for="item in navItems" :key="item.name" href="#" class="nav-link">{{ item.name }}</a>
        </div>

        <div class="nav-actions">
          <button class="icon-btn" @click="toggleTheme">
            <el-icon><Sunny v-if="!isDark" /><Moon v-else /></el-icon>
          </button>
          <button class="login-btn" @click="handleLogin">登录</button>
          <button class="register-btn" @click="handleRegister">注册</button>
        </div>
      </div>
    </header>

    <!-- 主视觉区 Hero Section -->
    <section class="hero">
      <div class="hero-content">
        <div class="hero-badge">
          <span>✨ AI 智能推荐</span>
        </div>
        <h1 class="hero-title">
          探索世界<br>
          <span class="gradient-text">从Minecraft开始</span>
        </h1>
        <p class="hero-desc">
          一站式智慧旅游服务平台，为您提供景点推荐、酒店预订、票务预订、美食推荐等全方位旅游服务
        </p>
        
        <!-- 搜索框 液态玻璃 -->
        <div class="search-bar glass-card">
          <el-input 
            v-model="searchKeyword" 
            placeholder="搜索景点、酒店、美食..." 
            :prefix-icon="Search"
            size="large"
            class="search-input"
          />
          <button class="search-btn">搜索</button>
        </div>

        <!-- 快捷入口 -->
        <div class="quick-stats">
          <div class="stat-item">
            <div class="stat-number">500+</div>
            <div class="stat-label">精选景点</div>
          </div>
          <div class="stat-item">
            <div class="stat-number">200+</div>
            <div class="stat-label">品质酒店</div>
          </div>
          <div class="stat-item">
            <div class="stat-number">1000+</div>
            <div class="stat-label">地道美食</div>
          </div>
          <div class="stat-item">
            <div class="stat-number">10k+</div>
            <div class="stat-label">社区用户</div>
          </div>
        </div>
      </div>
    </section>

    <!-- 服务模块 -->
    <section class="services">
      <div class="container">
        <div class="section-header">
          <span class="section-tag">核心服务</span>
          <h2>一站式旅游解决方案</h2>
          <p>从出行规划到旅游体验，我们提供完整服务链路</p>
        </div>
        <div class="services-grid">
          <div v-for="service in services" :key="service.title" class="service-card glass-card-hover">
            <div class="service-icon" :style="{ background: service.gradient }">
              <el-icon :size="32"><component :is="service.icon" /></el-icon>
            </div>
            <h3>{{ service.title }}</h3>
            <p>{{ service.desc }}</p>
          </div>
        </div>
      </div>
    </section>

    <!-- 热门推荐 轮播区域 -->
    <section class="featured">
      <div class="container">
        <div class="section-header">
          <span class="section-tag">热门推荐</span>
          <h2>探索精彩目的地</h2>
          <p>根据季节和用户评分精选的旅行目的地</p>
        </div>
        
        <div class="featured-tabs">
          <button 
            v-for="tab in featuredTabs" 
            :key="tab.value"
            :class="['tab-btn', { active: activeTab === tab.value }]"
            @click="activeTab = tab.value"
          >
            {{ tab.label }}
          </button>
        </div>

        <div class="featured-carousel">
          <div class="carousel-container">
            <div class="carousel-track" :style="{ transform: `translateX(-${currentSlide * 33.333}%)` }">
              <div v-for="item in currentFeaturedList" :key="item.id" class="carousel-item">
                <div class="feature-card glass-card">
                  <div class="card-image">
                    <img :src="item.image" :alt="item.name">
                    <div class="card-badge" v-if="item.rating >= 4.5">🔥 必去</div>
                  </div>
                  <div class="card-content">
                    <h4>{{ item.name }}</h4>
                    <div class="card-meta">
                      <span class="rating">
                        <el-icon><StarFilled /></el-icon>
                        {{ item.rating }}
                      </span>
                      <span class="location">
                        <el-icon><Location /></el-icon>
                        {{ item.location }}
                      </span>
                    </div>
                    <p class="card-desc">{{ item.desc }}</p>
                    <button class="card-btn">查看详情</button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 特色功能 -->
    <section class="features">
      <div class="container">
        <div class="features-grid">
          <div class="features-text">
            <span class="section-tag">智慧出行</span>
            <h2>智能规划，<br>让旅行更简单</h2>
            <p>基于AI的出行计划系统，为您量身定制完美旅程。从景点推荐到酒店预订，一站式解决所有出行需求。</p>
            <ul class="feature-list">
              <li><el-icon><Check /></el-icon> 个性化行程推荐</li>
              <li><el-icon><Check /></el-icon> 实时票务查询预订</li>
              <li><el-icon><Check /></el-icon> 社区交流与分享</li>
              <li><el-icon><Check /></el-icon> 好友结伴出行</li>
            </ul>
            <button class="feature-btn">开始规划 <el-icon><ArrowRight /></el-icon></button>
          </div>
          <div class="features-image">
            <div class="image-mockup glass-card">
              <div class="mockup-screen">
                <div class="mockup-header">
                  <span>我的行程</span>
                  <el-icon><MoreFilled /></el-icon>
                </div>
                <div class="mockup-list">
                  <div class="mockup-item" v-for="i in 3" :key="i">
                    <div class="mockup-dot"></div>
                    <div class="mockup-line"></div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 社区动态 -->
    <section class="community">
      <div class="container">
        <div class="section-header">
          <span class="section-tag">社区交流</span>
          <h2>旅行者分享</h2>
          <p>看看大家都在聊什么</p>
        </div>
        <div class="community-grid">
          <div v-for="post in communityPosts" :key="post.id" class="post-card glass-card-hover">
            <div class="post-header">
              <div class="post-avatar">
                <el-avatar :size="40" :src="post.avatar" />
              </div>
              <div class="post-info">
                <h4>{{ post.author }}</h4>
                <span>{{ post.time }}</span>
              </div>
            </div>
            <p class="post-content">{{ post.content }}</p>
            <div class="post-stats">
              <span><el-icon><ChatDotRound /></el-icon> {{ post.comments }}</span>
              <span><el-icon><Star /></el-icon> {{ post.likes }}</span>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 底部 -->
    <footer class="footer glass-footer">
      <div class="container">
        <div class="footer-content">
          <div class="footer-brand">
            <div class="logo-area">
              <div class="logo-icon small">
                <svg viewBox="0 0 24 24" fill="none">
                  <path d="M12 2L2 7L12 12L22 7L12 2Z" stroke="currentColor" stroke-width="1.5"/>
                  <path d="M2 17L12 22L22 17" stroke="currentColor" stroke-width="1.5"/>
                  <path d="M2 12L12 17L22 12" stroke="currentColor" stroke-width="1.5"/>
                </svg>
              </div>
              <span>Minecraft 旅游系统</span>
            </div>
            <p>让旅行更简单、更美好。</p>
          </div>
          <div class="footer-links">
            <div class="link-group">
              <h4>关于我们</h4>
              <a href="#">公司简介</a>
              <a href="#">联系我们</a>
              <a href="#">加入我们</a>
            </div>
            <div class="link-group">
              <h4>帮助中心</h4>
              <a href="#">常见问题</a>
              <a href="#">服务条款</a>
              <a href="#">隐私政策</a>
            </div>
            <div class="link-group">
              <h4>关注我们</h4>
              <a href="#">微信公众号</a>
              <a href="#">官方微博</a>
              <a href="#">小红书</a>
            </div>
          </div>
        </div>
        <div class="footer-bottom">
          <p>&copy; 2025 Minecraft 旅游系统 | 一站式智慧旅游服务平台</p>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { 
  Search, Sunny, Moon, StarFilled, Location, 
  ArrowLeft, ArrowRight, Check, MoreFilled,
  ChatDotRound, Star, Guide, House, Ticket, 
  ForkSpoon, ShoppingCart, ChatLineSquare, MapLocation
} from '@element-plus/icons-vue'

// 主题切换
const isDark = ref(false)
const toggleTheme = () => {
  isDark.value = !isDark.value
  document.documentElement.classList.toggle('dark', isDark.value)
}

// 导航菜单
const navItems = [
  { name: '首页', link: '/' },
  { name: '景点', link: '/attractions' },
  { name: '酒店', link: '/hotels' },
  { name: '美食', link: '/foods' },
  { name: '商城', link: '/mall' },
  { name: '社区', link: '/community' }
]

// 服务模块
const services = [
  { title: '景点推荐', desc: '智能推荐热门景点，按城市/季节筛选', icon: Guide, gradient: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)' },
  { title: '酒店预订', desc: '海量酒店资源，按星级筛选预订', icon: House, gradient: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)' },
  { title: '票务预订', desc: '火车票/机票/出租车票一站式查询', icon: Ticket, gradient: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)' },
  { title: '美食推荐', desc: '地道美食推荐，按菜系口味筛选', icon: ForkSpoon, gradient: 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)' },
  { title: '特产商城', desc: '精选特产商品，购物车订单支付', icon: ShoppingCart, gradient: 'linear-gradient(135deg, #fa709a 0%, #fee140 100%)' },
  { title: '社区交流', desc: '分享旅行故事，点赞收藏评论', icon: ChatLineSquare, gradient: 'linear-gradient(135deg, #a18cd1 0%, #fbc2eb 100%)' },
  { title: '出行计划', desc: '创建编辑行程，智能规划管理', icon: MapLocation, gradient: 'linear-gradient(135deg, #ff9a9e 0%, #fecfef 100%)' },
  { title: '跟团游', desc: '发起/加入团队，结伴同行', icon: Guide, gradient: 'linear-gradient(135deg, #ffecd2 0%, #fcb69f 100%)' }
]

// 推荐分类
const featuredTabs = [
  { label: '热门景点', value: 'attractions' },
  { label: '精品酒店', value: 'hotels' },
  { label: '特色美食', value: 'foods' }
]
const activeTab = ref('attractions')

const attractionsList = ref([
  { id: 1, name: '黄山风景区', location: '安徽·黄山', rating: 4.8, desc: '五岳归来不看山，黄山归来不看岳', image: 'https://picsum.photos/id/104/400/300' },
  { id: 2, name: '九寨沟', location: '四川·阿坝', rating: 4.9, desc: '人间仙境，童话世界', image: 'https://picsum.photos/id/106/400/300' },
  { id: 3, name: '丽江古城', location: '云南·丽江', rating: 4.7, desc: '邂逅时光，浪漫之都', image: 'https://picsum.photos/id/108/400/300' },
  { id: 4, name: '张家界', location: '湖南·张家界', rating: 4.8, desc: '奇峰三千，秀水八百', image: 'https://picsum.photos/id/110/400/300' }
])

const hotelsList = ref([
  { id: 1, name: '希尔顿酒店', location: '上海·陆家嘴', rating: 4.9, desc: '江景房，五星级服务', image: 'https://picsum.photos/id/20/400/300' },
  { id: 2, name: '悦榕庄', location: '杭州·西湖', rating: 4.8, desc: '湖畔度假胜地', image: 'https://picsum.photos/id/22/400/300' }
])

const foodsList = ref([
  { id: 1, name: '北京烤鸭', location: '北京·前门', rating: 4.9, desc: '外酥里嫩，百年老字号', image: 'https://picsum.photos/id/30/400/300' },
  { id: 2, name: '重庆火锅', location: '重庆·解放碑', rating: 4.8, desc: '麻辣鲜香，地道重庆味', image: 'https://picsum.photos/id/32/400/300' }
])

const currentFeaturedList = computed(() => {
  switch(activeTab.value) {
    case 'attractions': return attractionsList.value
    case 'hotels': return hotelsList.value
    case 'foods': return foodsList.value
    default: return attractionsList.value
  }
})

// 轮播逻辑
const currentSlide = ref(0)
const slideCount = computed(() => Math.ceil(currentFeaturedList.value.length / 3))

const prevSlide = () => {
  if (currentSlide.value > 0) {
    currentSlide.value--
  } else {
    currentSlide.value = slideCount.value - 1
  }
}
const nextSlide = () => {
  if (currentSlide.value < slideCount.value - 1) {
    currentSlide.value++
  } else {
    currentSlide.value = 0
  }
}

// 自动轮播
let autoTimer = null
onMounted(() => {
  autoTimer = setInterval(() => {
    nextSlide()
  }, 5000)
})
onUnmounted(() => {
  if (autoTimer) clearInterval(autoTimer)
})

// 社区动态
const communityPosts = ref([
  { id: 1, author: '旅行家小明', avatar: 'https://picsum.photos/id/64/100/100', time: '2小时前', content: '刚刚从黄山回来，日出真的太震撼了！强烈推荐大家去体验一下。', comments: 24, likes: 128 },
  { id: 2, author: '美食探索者', avatar: 'https://picsum.photos/id/65/100/100', time: '昨天', content: '在成都发现了超好吃的火锅，麻辣鲜香，价格也很亲民！', comments: 56, likes: 342 },
  { id: 3, author: '摄影阿杰', avatar: 'https://picsum.photos/id/66/100/100', time: '昨天', content: '大理的洱海真的太美了，随手一拍都是大片。', comments: 32, likes: 267 }
])

const searchKeyword = ref('')

const handleLogin = () => {
  console.log('登录')
}

const handleRegister = () => {
  console.log('注册')
}
</script>

<style scoped>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

.home-container {
  min-height: 100vh;
  background: #f5f7fb;
  position: relative;
  overflow-x: hidden;
}

/* 动态背景 */
.animated-bg {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 0;
  overflow: hidden;
}

.gradient-sphere {
  position: absolute;
  width: 600px;
  height: 600px;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(102,126,234,0.3) 0%, rgba(118,75,162,0) 70%);
  top: -200px;
  right: -200px;
  animation: float 20s ease-in-out infinite;
}

.gradient-sphere.second {
  width: 500px;
  height: 500px;
  background: radial-gradient(circle, rgba(245,87,108,0.25) 0%, rgba(245,87,108,0) 70%);
  bottom: -150px;
  left: -150px;
  top: auto;
  animation: float 15s ease-in-out infinite reverse;
}

@keyframes float {
  0%, 100% { transform: translate(0, 0) rotate(0deg); }
  50% { transform: translate(30px, 20px) rotate(5deg); }
}

/* 液态玻璃导航栏 */
.glass-nav {
  position: sticky;
  border-radius: 16px;
  top: 0;
  backdrop-filter: blur(20px);
  background: rgba(255, 255, 255, 0.75);
  border-bottom: 1px solid rgba(255, 255, 255, 0.2);
  box-shadow: 0 4px 30px rgba(0, 0, 0, 0.05);
}

.nav-container {
  max-width: 1280px;
  margin: 0 auto;
  padding: 1rem 2rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.logo-area {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.logo-icon {
  width: 36px;
  height: 36px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.logo-icon svg {
  width: 22px;
  height: 22px;
}

.logo-text {
  font-size: 1.5rem;
  font-weight: 700;
  background: linear-gradient(135deg, #1e293b, #334155);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
}

.logo-text span {
  background: linear-gradient(135deg, #667eea, #764ba2);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
}

.nav-links {
  display: flex;
  gap: 2rem;
}

.nav-link {
  text-decoration: none;
  color: #334155;
  font-weight: 500;
  transition: color 0.3s;
}

.nav-link:hover {
  color: #667eea;
}

.nav-actions {
  display: flex;
  gap: 1rem;
  align-items: center;
}

.icon-btn {
  background: rgba(255, 255, 255, 0.6);
  border: none;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  font-size: 1.2rem;
  backdrop-filter: blur(4px);
  transition: all 0.3s;
}

.icon-btn:hover {
  background: rgba(255, 255, 255, 0.9);
  transform: scale(1.05);
}

.login-btn, .register-btn {
  padding: 0.5rem 1.25rem;
  border-radius: 40px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  border: none;
}

.login-btn {
  background: transparent;
  color: #667eea;
  border: 1px solid #667eea;
}

.login-btn:hover {
  background: #667eea;
  color: white;
}

.register-btn {
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
}

.register-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 20px -10px rgba(102,126,234,0.5);
}

/* Hero 区域 */
.hero {
  position: relative;
  z-index: 2;
  min-height: 85vh;
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
  padding: 4rem 2rem;
}

.hero-content {
  max-width: 900px;
  margin: 0 auto;
}

.hero-badge {
  display: inline-block;
  padding: 0.5rem 1rem;
  background: rgba(102,126,234,0.1);
  backdrop-filter: blur(10px);
  border-radius: 40px;
  font-size: 0.85rem;
  color: #667eea;
  margin-bottom: 1.5rem;
}

.hero-title {
  font-size: 4rem;
  font-weight: 800;
  line-height: 1.2;
  margin-bottom: 1.5rem;
  color: #1e293b;
}

.gradient-text {
  background: linear-gradient(135deg, #667eea, #764ba2);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
}

.hero-desc {
  font-size: 1.2rem;
  color: #475569;
  max-width: 600px;
  margin: 0 auto 2rem;
  line-height: 1.6;
}

.search-bar {
  display: flex;
  gap: 1rem;
  max-width: 600px;
  margin: 0 auto 3rem;
  padding: 0.5rem;
  background: rgba(255, 255, 255, 0.7);
  border-radius: 60px;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.search-input {
  flex: 1;
}

.search-input :deep(.el-input__wrapper) {
  background: transparent;
  box-shadow: none;
  border-radius: 40px;
}

.search-btn {
  background: linear-gradient(135deg, #667eea, #764ba2);
  border: none;
  padding: 0 1.8rem;
  border-radius: 40px;
  color: white;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.search-btn:hover {
  transform: scale(1.02);
  box-shadow: 0 5px 15px rgba(102,126,234,0.4);
}

.quick-stats {
  display: flex;
  justify-content: center;
  gap: 3rem;
  margin-top: 2rem;
}

.stat-item {
  text-align: center;
}

.stat-number {
  font-size: 1.8rem;
  font-weight: 800;
  color: #667eea;
}

.stat-label {
  color: #64748b;
  font-size: 0.9rem;
}

/* 通用容器 */
.container {
  max-width: 1280px;
  margin: 0 auto;
  padding: 4rem 2rem;
}

.section-header {
  text-align: center;
  margin-bottom: 3rem;
}

.section-tag {
  display: inline-block;
  padding: 0.3rem 1rem;
  background: rgba(102,126,234,0.1);
  border-radius: 40px;
  font-size: 0.85rem;
  color: #667eea;
  margin-bottom: 1rem;
}

.section-header h2 {
  font-size: 2.5rem;
  color: #1e293b;
  margin-bottom: 0.5rem;
}

.section-header p {
  color: #64748b;
}

/* 服务网格 */
.services-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
  gap: 2rem;
}

.service-card {
  background: rgba(255, 255, 255, 0.6);
  backdrop-filter: blur(10px);
  border-radius: 28px;
  padding: 2rem;
  text-align: center;
  transition: all 0.3s;
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.glass-card-hover:hover {
  transform: translateY(-8px);
  background: rgba(255, 255, 255, 0.8);
  box-shadow: 0 20px 35px -12px rgba(0, 0, 0, 0.1);
}

.service-icon {
  width: 70px;
  height: 70px;
  margin: 0 auto 1.5rem;
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.service-card h3 {
  font-size: 1.3rem;
  margin-bottom: 0.5rem;
  color: #1e293b;
}

.service-card p {
  color: #64748b;
  font-size: 0.9rem;
  line-height: 1.5;
}

/* 推荐区域 */
.featured-tabs {
  display: flex;
  justify-content: center;
  gap: 1rem;
  margin-bottom: 2.5rem;
}

.tab-btn {
  padding: 0.6rem 1.5rem;
  border-radius: 40px;
  border: none;
  background: rgba(255, 255, 255, 0.5);
  backdrop-filter: blur(5px);
  cursor: pointer;
  font-weight: 500;
  transition: all 0.3s;
}

.tab-btn.active {
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
}

.featured-carousel {
  position: relative;
  overflow: hidden;
}

.carousel-container {
  overflow: hidden;
  border-radius: 28px;
}

.carousel-track {
  display: flex;
  transition: transform 0.5s ease;
}

.carousel-item {
  flex: 0 0 33.333%;
  padding: 0 1rem;
}

.feature-card {
  background: white;
  border-radius: 28px;
  overflow: hidden;
  transition: all 0.3s;
  height: 100%;
  box-shadow: 0 10px 25px -5px rgba(0, 0, 0, 0.05);
}

.card-image {
  position: relative;
  height: 200px;
  overflow: hidden;
}

.card-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
}

.feature-card:hover .card-image img {
  transform: scale(1.05);
}

.card-badge {
  position: absolute;
  top: 1rem;
  right: 1rem;
  background: rgba(0, 0, 0, 0.6);
  backdrop-filter: blur(4px);
  padding: 0.3rem 0.8rem;
  border-radius: 40px;
  font-size: 0.75rem;
  color: white;
}

.card-content {
  padding: 1.5rem;
}

.card-content h4 {
  font-size: 1.2rem;
  margin-bottom: 0.5rem;
}

.card-meta {
  display: flex;
  gap: 1rem;
  margin-bottom: 0.75rem;
  font-size: 0.85rem;
  color: #667eea;
}

.card-meta span {
  display: flex;
  align-items: center;
  gap: 0.2rem;
}

.card-desc {
  color: #64748b;
  font-size: 0.85rem;
  margin-bottom: 1rem;
}

.card-btn {
  background: transparent;
  border: 1px solid #e2e8f0;
  padding: 0.5rem 1rem;
  border-radius: 40px;
  cursor: pointer;
  transition: all 0.3s;
}

.card-btn:hover {
  background: #667eea;
  border-color: #667eea;
  color: white;
}

/* 特色功能 */
.features-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 4rem;
  align-items: center;
}

.features-text h2 {
  font-size: 2.5rem;
  margin: 1rem 0;
  color: #1e293b;
}

.feature-list {
  list-style: none;
  margin: 1.5rem 0;
}

.feature-list li {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin-bottom: 0.75rem;
  color: #475569;
}

.feature-list li .el-icon {
  color: #667eea;
}

.feature-btn {
  background: linear-gradient(135deg, #667eea, #764ba2);
  border: none;
  padding: 0.8rem 2rem;
  border-radius: 40px;
  color: white;
  font-weight: 600;
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  cursor: pointer;
  transition: all 0.3s;
}

.feature-btn:hover {
  transform: translateX(5px);
  box-shadow: 0 10px 20px -10px rgba(102,126,234,0.5);
}

.image-mockup {
  background: rgba(255, 255, 255, 0.5);
  backdrop-filter: blur(10px);
  border-radius: 32px;
  padding: 1.5rem;
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.mockup-screen {
  background: white;
  border-radius: 24px;
  overflow: hidden;
}

.mockup-header {
  padding: 1rem;
  background: #f8fafc;
  display: flex;
  justify-content: space-between;
  font-weight: 600;
  border-bottom: 1px solid #e2e8f0;
}

.mockup-list {
  padding: 1rem;
}

.mockup-item {
  display: flex;
  gap: 0.75rem;
  margin-bottom: 1rem;
  align-items: center;
}

.mockup-dot {
  width: 8px;
  height: 8px;
  background: #667eea;
  border-radius: 50%;
}

.mockup-line {
  flex: 1;
  height: 8px;
  background: #e2e8f0;
  border-radius: 4px;
}

/* 社区网格 */
.community-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
  gap: 2rem;
}

.post-card {
  background: rgba(255, 255, 255, 0.7);
  backdrop-filter: blur(10px);
  border-radius: 28px;
  padding: 1.5rem;
  transition: all 0.3s;
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.post-header {
  display: flex;
  gap: 1rem;
  margin-bottom: 1rem;
}

.post-info h4 {
  font-size: 1rem;
  margin-bottom: 0.2rem;
}

.post-info span {
  font-size: 0.75rem;
  color: #94a3b8;
}

.post-content {
  margin-bottom: 1rem;
  color: #334155;
  line-height: 1.5;
}

.post-stats {
  display: flex;
  gap: 1rem;
  color: #94a3b8;
  font-size: 0.85rem;
}

.post-stats span {
  display: flex;
  align-items: center;
  gap: 0.3rem;
}

/* 底部 */
.footer {
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(20px);
  border-top: 1px solid rgba(255, 255, 255, 0.2);
  margin-top: 2rem;
}

.footer-content {
  display: flex;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 2rem;
  margin-bottom: 2rem;
}

.footer-brand p {
  color: #64748b;
  margin-top: 0.5rem;
}

.footer-links {
  display: flex;
  gap: 3rem;
}

.link-group h4 {
  margin-bottom: 1rem;
  color: #1e293b;
}

.link-group a {
  display: block;
  color: #64748b;
  text-decoration: none;
  margin-bottom: 0.5rem;
  font-size: 0.9rem;
  transition: color 0.3s;
}

.link-group a:hover {
  color: #667eea;
}

.footer-bottom {
  text-align: center;
  padding-top: 2rem;
  border-top: 1px solid rgba(0, 0, 0, 0.05);
  color: #94a3b8;
  font-size: 0.85rem;
}

.glass-card {
  background: rgba(255, 255, 255, 0.6);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.3);
}

@media (max-width: 768px) {
  .nav-links {
    display: none;
  }
  .hero-title {
    font-size: 2.5rem;
  }
  .services-grid {
    grid-template-columns: 1fr;
  }
  .features-grid {
    grid-template-columns: 1fr;
  }
  .carousel-item {
    flex: 0 0 100%;
  }
  .quick-stats {
    flex-wrap: wrap;
    gap: 1.5rem;
  }
}
</style>