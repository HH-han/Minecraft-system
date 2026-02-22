<template>
  <div class="introduce-container">
    <!-- 背景元素 -->
    <div class="introduce-background">
      <div class="introduce-particle" v-for="(particle, index) in particles" :key="index"
        :style="particleStyle(particle)">
      </div>
    </div>

    <!-- 导航栏 -->
    <nav class="introduce-nav">
      <div class="introduce-nav-container">
        <div class="introduce-nav-logo">
          <span class="introduce-logo-text">Vue3</span>
          <span class="introduce-logo-highlight">Element-plus</span>
        </div>
        <!-- 导航链接 -->
        <ul class="introduce-nav-links">
          <li v-for="(link, index) in navLinks" :key="index" @mouseenter="hoverLink(index)"
            @mouseleave="resetLink(index)">
            <button :class="{ 'introduce-link-active': activeLink === index }" class="elegant-button">
              <span class="introduce-link-text">{{ link.text }}</span>
              <span class="introduce-link-underline" :style="underlineStyle(index)"></span>
            </button>
          </li>
        </ul>
      </div>

      <button class="introduce-nav-button" @click="showContactForm = true">
        <span>联系我们</span>
        <div class="introduce-button-underline"></div>
      </button>
    </nav>
    <!-- 主要内容 -->
    <main class="introduce-main">
      <section class="introduce-hero">
        <div class="introduce-hero-content">
          <h1 class="introduce-hero-title">
            <span class="introduce-title-line">创造令人惊叹的</span>
            <span class="introduce-title-line introduce-title-highlight">数字体验</span>
          </h1>
          <p class="introduce-hero-subtitle">使用Vue3构建现代化、响应式和高性能的Web应用程序</p>
          <div class="introduce-hero-buttons">
            <button class="introduce-primary-button" @click="scrollToFeatures">
              探索更多
              <svg class="introduce-button-icon" viewBox="0 0 24 24">
                <path d="M7.41,8.58L12,13.17L16.59,8.58L18,10L12,16L6,10L7.41,8.58Z" />
              </svg>
            </button>
            <button class="introduce-secondary-button" @click="showDemoVideo = true">
              <svg class="introduce-button-icon" viewBox="0 0 24 24">
                <path
                  d="M10,16.5V7.5L16,12M12,2A10,10 0 0,0 2,12A10,10 0 0,0 12,22A10,10 0 0,0 22,12A10,10 0 0,0 12,2Z" />
              </svg>
              观看演示
            </button>
          </div>
        </div>
        <div class="introduce-hero-image">
          <img src="@/assets/videos/index.png" alt="Vue3 Design" class="introduce-image">
          <div class="introduce-image-highlight"></div>
        </div>
      </section>

      <!-- 特性部分 -->
      <section class="introduce-features" ref="featuresSection">
        <div class="introduce-section-header">
          <h2 class="introduce-section-title">我们的优势</h2>
          <p class="introduce-section-subtitle">为什么选择我们的Vue3解决方案</p>
          <div class="introduce-section-divider"></div>
        </div>

        <div class="introduce-features-grid">
          <div class="introduce-feature-card" v-for="(feature, index) in features" :key="index"
            @mouseenter="hoverFeature(index)" @mouseleave="resetFeature(index)">
            <div class="introduce-feature-icon" :style="featureIconStyle(index)">
              <svg viewBox="0 0 24 24">
                <path :d="feature.icon" />
              </svg>
            </div>
            <h3 class="introduce-feature-title">{{ feature.title }}</h3>
            <p class="introduce-feature-description">{{ feature.description }}</p>
            <div class="introduce-feature-highlight" :style="featureHighlightStyle(index)"></div>
          </div>
        </div>
      </section>

      <!-- 统计数据 -->
      <section class="introduce-stats">
        <div class="introduce-stats-item" v-for="(stat, index) in stats" :key="index">
          <div class="introduce-stat-value" ref="statValues">
            <span>{{ animatedStats[index] }}</span>
            <span v-if="stat.unit">{{ stat.unit }}</span>
          </div>
          <p class="introduce-stat-label">{{ stat.label }}</p>
        </div>
      </section>

      <!-- 客户评价 -->
      <section class="introduce-testimonials">
        <div class="introduce-section-header">
          <h2 class="introduce-section-title">客户评价</h2>
          <p class="introduce-section-subtitle">听听他们怎么说</p>
          <div class="introduce-section-divider"></div>
        </div>

        <div class="introduce-testimonial-slider">
          <div class="introduce-testimonial-track" :style="testimonialTrackStyle">
            <div class="introduce-testimonial-slide" v-for="(testimonial, index) in testimonials" :key="index">
              <div class="introduce-testimonial-card">
                <div class="introduce-testimonial-quote">"</div>
                <p class="introduce-testimonial-text">{{ testimonial.text }}</p>
                <div class="introduce-testimonial-author">
                  <img :src="testimonial.avatar" :alt="testimonial.name" class="introduce-testimonial-avatar">
                  <div class="introduce-testimonial-author-info">
                    <h4 class="introduce-testimonial-name">{{ testimonial.name }}</h4>
                    <p class="introduce-testimonial-position">{{ testimonial.position }}</p>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <button class="introduce-slider-button introduce-slider-prev" @click="prevTestimonial">
            <svg viewBox="0 0 24 24">
              <path d="M15.41,16.58L10.83,12L15.41,7.41L14,6L8,12L14,18L15.41,16.58Z" />
            </svg>
          </button>
          <button class="introduce-slider-button introduce-slider-next" @click="nextTestimonial">
            <svg viewBox="0 0 24 24">
              <path d="M8.59,16.58L13.17,12L8.59,7.41L10,6L16,12L10,18L8.59,16.58Z" />
            </svg>
          </button>
        </div>
      </section>
    </main>

    <!-- 页脚 -->
    <footer class="introduce-footer">
      <div class="introduce-footer-content">
        <div class="introduce-footer-section">
          <div class="introduce-nav-logo">
            <span class="introduce-logo-text">Vue3</span>
            <span class="introduce-logo-highlight">Element-plus</span>
          </div>
          <p class="introduce-footer-description">使用Vue3构建现代化、响应式和高性能的Web应用程序</p>
          <div class="introduce-social-links">
            <a href="#" class="introduce-social-link" v-for="(social, index) in socialLinks" :key="index">
              <svg viewBox="0 0 24 24">
                <path :d="social.icon" />
              </svg>
            </a>
          </div>
        </div>
        <div class="introduce-footer-section" v-for="(section, index) in footerSections" :key="index">
          <h3 class="introduce-footer-title">{{ section.title }}</h3>
          <ul class="introduce-footer-links">
            <li v-for="(link, linkIndex) in section.links" :key="linkIndex">
              <a href="#" class="introduce-footer-link">{{ link }}</a>
            </li>
          </ul>
        </div>
      </div>
      <div class="introduce-footer-bottom">
        <p class="introduce-copyright">© 2024 Vue3 Element-plus. 保留所有权利。</p>
        <div class="introduce-legal-links">
          <a href="#" class="introduce-legal-link">隐私政策</a>
          <a href="#" class="introduce-legal-link">服务条款</a>
          <a href="#" class="introduce-legal-link">Cookie政策</a>
        </div>
      </div>
    </footer>

    <!-- 联系表单模态框 -->
    <div class="introduce-modal" v-if="showContactForm" @click.self="showContactForm = false">
      <div class="introduce-modal-content">
        <button class="introduce-modal-close" @click="showContactForm = false">
          <svg viewBox="0 0 24 24">
            <path
              d="M19,6.41L17.59,5L12,10.59L6.41,5L5,6.41L10.59,12L5,17.59L6.41,19L12,13.41L17.59,19L19,17.59L13.41,12L19,6.41Z" />
          </svg>
        </button>
        <h2 class="introduce-modal-title">联系我们</h2>
        <form class="introduce-contact-form" @submit.prevent="submitContactForm">
          <div class="introduce-form-group">
            <label for="name" class="introduce-form-label">您的姓名</label>
            <input type="text" id="name" v-model="contactForm.name" class="introduce-form-input" required>
          </div>
          <div class="introduce-form-group">
            <label for="email" class="introduce-form-label">电子邮件</label>
            <input type="email" id="email" v-model="contactForm.email" class="introduce-form-input" required>
          </div>
          <div class="introduce-form-group">
            <label for="subject" class="introduce-form-label">主题</label>
            <input type="text" id="subject" v-model="contactForm.subject" class="introduce-form-input" required>
          </div>
          <div class="introduce-form-group">
            <label for="message" class="introduce-form-label">消息</label>
            <textarea id="message" v-model="contactForm.message" class="introduce-form-textarea" required></textarea>
          </div>
          <button type="submit" class="introduce-submit-button">
            <span>发送消息</span>
            <svg class="introduce-button-icon" viewBox="0 0 24 24">
              <path d="M2,21L23,12L2,3V10L17,12L2,14V21Z" />
            </svg>
          </button>
        </form>
      </div>
    </div>

    <!-- 演示视频模态框 -->
    <div class="introduce-modal" v-if="showDemoVideo" @click.self="showDemoVideo = false">
      <div class="introduce-modal-content introduce-video-modal">
        <button class="introduce-modal-close" @click="showDemoVideo = false">
          <svg viewBox="0 0 24 24">
            <path
              d="M19,6.41L17.59,5L12,10.59L6.41,5L5,6.41L10.59,12L5,17.59L6.41,19L12,13.41L17.59,19L19,17.59L13.41,12L19,6.41Z" />
          </svg>
        </button>
        <div class="introduce-video-container">
          <video controls class="introduce-video">
            <source src="" type="video/mp4">
          </video>
        </div>
      </div>
    </div>
    <!-- 返回按钮 -->
    <div class="introduce-back-button">
      <CloseButton @click="closethePage" />
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue';
import CloseButton from '@/components/PromptComponent/CloseButton.vue'
// 导航链接
const navLinks = reactive([
  { text: '首页' },
]);
// 路由
import { useRouter } from 'vue-router';
const router = useRouter();
const home = () => {
  router.push({ name: 'systemhomeView' });
};
const activeLink = ref(0);
const underlineWidths = reactive([0, 0, 0, 0, 0]);

const hoverLink = (index) => {
  underlineWidths[index] = 100;
};

const resetLink = (index) => {
  underlineWidths[index] = activeLink.value === index ? 100 : 0;
};

const underlineStyle = (index) => {
  return {
    width: `${underlineWidths[index]}%`,
    opacity: underlineWidths[index] > 0 ? 1 : 0
  };
};

// 背景粒子
const particles = reactive([]);
const createParticles = () => {
  for (let i = 0; i < 30; i++) {
    particles.push({
      size: Math.random() * 5 + 1,
      x: Math.random() * 100,
      y: Math.random() * 100,
      opacity: Math.random() * 0.5 + 0.1,
      delay: Math.random() * 5
    });
  }
};

const particleStyle = (particle) => {
  return {
    width: `${particle.size}px`,
    height: `${particle.size}px`,
    left: `${particle.x}%`,
    top: `${particle.y}%`,
    opacity: particle.opacity,
    animationDelay: `${particle.delay}s`
  };
};

// 特性部分
const features = reactive([
  {
    title: '响应式设计',
    description: '我们的网站完美适配各种设备尺寸，从手机到桌面电脑都能提供最佳体验。',
    icon: 'M12,15.5A3.5,3.5 0 0,1 8.5,12A3.5,3.5 0 0,1 12,8.5A3.5,3.5 0 0,1 15.5,12A3.5,3.5 0 0,1 12,15.5M19.43,12.97C19.47,12.65 19.5,12.33 19.5,12C19.5,11.67 19.47,11.34 19.43,11L21.54,9.37C21.73,9.22 21.78,8.95 21.66,8.73L19.66,5.27C19.54,5.05 19.27,4.96 19.05,5.05L16.56,6.05C16.04,5.66 15.5,5.32 14.87,5.07L14.5,2.42C14.46,2.18 14.25,2 14,2H10C9.75,2 9.54,2.18 9.5,2.42L9.13,5.07C8.5,5.32 7.96,5.66 7.44,6.05L4.95,5.05C4.73,4.96 4.46,5.05 4.34,5.27L2.34,8.73C2.21,8.95 2.27,9.22 2.46,9.37L4.57,11C4.53,11.34 4.5,11.67 4.5,12C4.5,12.33 4.53,12.65 4.57,12.97L2.46,14.63C2.27,14.78 2.21,15.05 2.34,15.27L4.34,18.73C4.46,18.95 4.73,19.03 4.95,18.95L7.44,17.94C7.96,18.34 8.5,18.68 9.13,18.93L9.5,21.58C9.54,21.82 9.75,22 10,22H14C14.25,22 14.46,21.82 14.5,21.58L14.87,18.93C15.5,18.67 16.04,18.34 16.56,17.94L19.05,18.95C19.27,19.03 19.54,18.95 19.66,18.73L21.66,15.27C21.78,15.05 21.73,14.78 21.54,14.63L19.43,12.97Z'
  },
  {
    title: '高性能',
    description: '利用Vue3的优化特性，我们提供闪电般的加载速度和流畅的交互体验。',
    icon: 'M12,6V9L16,5L12,1V4A8,8 0 0,0 4,12C4,13.57 4.46,15.03 5.24,16.26L6.7,14.8C6.25,13.97 6,13 6,12A6,6 0 0,1 12,6M18.76,7.74L17.3,9.2C17.74,10.04 18,11 18,12A6,6 0 0,1 12,18V15L8,19L12,23V20A8,8 0 0,0 20,12C20,10.43 19.54,8.97 18.76,7.74Z'
  },
  {
    title: '现代化UI',
    description: '采用最新的设计趋势和动画效果，为用户提供视觉上令人愉悦的界面。',
    icon: 'M21,16.5C21,16.88 20.79,17.21 20.47,17.38L12.57,21.82C12.41,21.94 12.21,22 12,22C11.79,22 11.59,21.94 11.43,21.82L3.53,17.38C3.21,17.21 3,16.88 3,16.5V7.5C3,7.12 3.21,6.79 3.53,6.62L11.43,2.18C11.59,2.06 11.79,2 12,2C12.21,2 12.41,2.06 12.57,2.18L20.47,6.62C20.79,6.79 21,7.12 21,7.5V16.5M12,4.15L5,8.09V15.91L12,19.85L19,15.91V8.09L12,4.15Z'
  },
  {
    title: 'SEO优化',
    description: '我们的代码结构和架构设计有助于提高搜索引擎排名和可见性。',
    icon: 'M9.5,3A6.5,6.5 0 0,1 16,9.5C16,11.11 15.41,12.59 14.44,13.73L14.71,14H15.5L20.5,19L19,20.5L14,15.5V14.71L13.73,14.44C12.59,15.41 11.11,16 9.5,16A6.5,6.5 0 0,1 3,9.5A6.5,6.5 0 0,1 9.5,3M9.5,5C7,5 5,7 5,9.5C5,12 7,14 9.5,14C12,14 14,12 14,9.5C14,7 12,5 9.5,5Z'
  },
  {
    title: '易于维护',
    description: '清晰的代码结构和模块化设计使得未来更新和维护变得简单。',
    icon: 'M12,15.5A3.5,3.5 0 0,1 8.5,12A3.5,3.5 0 0,1 12,8.5A3.5,3.5 0 0,1 15.5,12A3.5,3.5 0 0,1 12,15.5M19.43,12.97C19.47,12.65 19.5,12.33 19.5,12C19.5,11.67 19.47,11.34 19.43,11L21.54,9.37C21.73,9.22 21.78,8.95 21.66,8.73L19.66,5.27C19.54,5.05 19.27,4.96 19.05,5.05L16.56,6.05C16.04,5.66 15.5,5.32 14.87,5.07L14.5,2.42C14.46,2.18 14.25,2 14,2H10C9.75,2 9.54,2.18 9.5,2.42L9.13,5.07C8.5,5.32 7.96,5.66 7.44,6.05L4.95,5.05C4.73,4.96 4.46,5.05 4.34,5.27L2.34,8.73C2.21,8.95 2.27,9.22 2.46,9.37L4.57,11C4.53,11.34 4.5,11.67 4.5,12C4.5,12.33 4.53,12.65 4.57,12.97L2.46,14.63C2.27,14.78 2.21,15.05 2.34,15.27L4.34,18.73C4.46,18.95 4.73,19.03 4.95,18.95L7.44,17.94C7.96,18.34 8.5,18.68 9.13,18.93L9.5,21.58C9.54,21.82 9.75,22 10,22H14C14.25,22 14.46,21.82 14.5,21.58L14.87,18.93C15.5,18.67 16.04,18.34 16.56,17.94L19.05,18.95C19.27,19.03 19.54,18.95 19.66,18.73L21.66,15.27C21.78,15.05 21.73,14.78 21.54,14.63L19.43,12.97Z'
  },
  {
    title: '安全可靠',
    description: '我们采用最新的安全措施来保护您的数据和用户隐私。',
    icon: 'M12,1L3,5V11C3,16.55 6.84,21.74 12,23C17.16,21.74 21,16.55 21,11V5L12,1M12,7C13.4,7 14.8,8.1 14.8,9.5V11C15.4,11 16,11.6 16,12.2V15.7C16,16.4 15.4,17 14.8,17H9.2C8.6,17 8,16.4 8,15.7V12.2C8,11.6 8.6,11 9.2,11V9.5C9.2,8.1 10.6,7 12,7M12,8.2C11.2,8.2 10.5,8.7 10.5,9.5V11H13.5V9.5C13.5,8.7 12.8,8.2 12,8.2Z'
  }
]);

const hoveredFeature = ref(null);

const hoverFeature = (index) => {
  hoveredFeature.value = index;
};

const resetFeature = () => {
  hoveredFeature.value = null;
};

const featureIconStyle = (index) => {
  return {
    transform: hoveredFeature.value === index ? 'scale(1.1) rotate(5deg)' : 'scale(1) rotate(0)',
    backgroundColor: hoveredFeature.value === index ? 'rgba(123, 31, 162, 0.1)' : 'rgba(123, 31, 162, 0.05)'
  };
};

const featureHighlightStyle = (index) => {
  return {
    opacity: hoveredFeature.value === index ? 0.2 : 0,
    transform: hoveredFeature.value === index ? 'scale(1.1)' : 'scale(1)'
  };
};

// 统计数据
const stats = reactive([
  { value: 100, unit: '+', label: '完成项目' },
  { value: 50, unit: '+', label: '满意客户' },
  { value: 99, unit: '%', label: '客户留存率' },
  { value: 24, unit: '/7', label: '技术支持' }
]);

const animatedStats = reactive([0, 0, 0, 0]);

const animateStats = () => {
  stats.forEach((stat, index) => {
    let current = 0;
    const increment = stat.value / 30;
    const timer = setInterval(() => {
      current += increment;
      if (current >= stat.value) {
        current = stat.value;
        clearInterval(timer);
      }
      animatedStats[index] = Math.floor(current);
    }, 30);
  });
};

// 客户评价
const testimonials = reactive([
  {
    text: '我建了一个令人惊叹的网站。使用了springboot(Spring Security,JWT,MyBatis...)和vue3(Elment-Plus...),同时使用了DeepPeek等Ai工具,非常棒,快速帮我完成了我的网站构建。',
    name: '张伟',
    position: '博览媒体主管',
    avatar: 'https://randomuser.me/api/portraits/men/32.jpg'
  },
  {
    text: '我建了一个令人惊叹的网站。使用了springboot(Spring Security,JWT,MyBatis...)和vue3(Elment-Plus...),同时使用了DeepPeek等Ai工具,非常棒,快速帮我完成了我的网站构建。',
    name: '张伟',
    position: '博览媒体主管',
    avatar: 'https://randomuser.me/api/portraits/men/32.jpg'
  },
]);

const currentTestimonial = ref(0);

const testimonialTrackStyle = computed(() => {
  return {
    transform: `translateX(-${currentTestimonial.value * 100}%)`
  };
});

const nextTestimonial = () => {
  currentTestimonial.value = (currentTestimonial.value + 1) % testimonials.length;
};

const prevTestimonial = () => {
  currentTestimonial.value = (currentTestimonial.value - 1 + testimonials.length) % testimonials.length;
};

// 社交链接
const socialLinks = reactive([
  { icon: 'M22.46,6C21.69,6.35 20.86,6.58 20,6.69C20.88,6.16 21.56,5.32 21.88,4.31C21.05,4.81 20.13,5.16 19.16,5.36C18.37,4.5 17.26,4 16,4C13.65,4 11.73,5.92 11.73,8.29C11.73,8.63 11.77,8.96 11.84,9.27C8.28,9.09 5.11,7.38 3,4.79C2.63,5.42 2.42,6.16 2.42,6.94C2.42,8.43 3.17,9.75 4.33,10.5C3.62,10.5 2.96,10.3 2.38,10C2.38,10 2.38,10 2.38,10.03C2.38,12.11 3.86,13.85 5.82,14.24C5.46,14.34 5.08,14.39 4.69,14.39C4.42,14.39 4.15,14.36 3.89,14.31C4.43,16 6,17.26 7.89,17.29C6.43,18.45 4.58,19.13 2.56,19.13C2.22,19.13 1.88,19.11 1.54,19.07C3.44,20.29 5.7,21 8.12,21C16,21 20.33,14.46 20.33,8.79C20.33,8.6 20.33,8.42 20.32,8.23C21.16,7.63 21.88,6.87 22.46,6Z' },
  { icon: 'M7.8,2H16.2C19.4,2 22,4.6 22,7.8V16.2A5.8,5.8 0 0,1 16.2,22H7.8C4.6,22 2,19.4 2,16.2V7.8A5.8,5.8 0 0,1 7.8,2M7.6,4A3.6,3.6 0 0,0 4,7.6V16.4C4,18.39 5.61,20 7.6,20H16.4A3.6,3.6 0 0,0 20,16.4V7.6C20,5.61 18.39,4 16.4,4H7.6M17.25,5.5A1.25,1.25 0 0,1 18.5,6.75A1.25,1.25 0 0,1 17.25,8A1.25,1.25 0 0,1 16,6.75A1.25,1.25 0 0,1 17.25,5.5M12,7A5,5 0 0,1 17,12A5,5 0 0,1 12,17A5,5 0 0,1 7,12A5,5 0 0,1 12,7M12,9A3,3 0 0,0 9,12A3,3 0 0,0 12,15A3,3 0 0,0 15,12A3,3 0 0,0 12,9Z' },
  { icon: 'M12,2.04C6.5,2.04 2,6.53 2,12.06C2,17.06 5.66,21.21 10.44,21.96V14.96H7.9V12.06H10.44V9.85C10.44,7.34 11.93,5.96 14.22,5.96C15.31,5.96 16.45,6.15 16.45,6.15V8.62H15.19C13.95,8.62 13.56,9.39 13.56,10.18V12.06H16.34L15.89,14.96H13.56V21.96A10,10 0 0,0 22,12.06C22,6.53 17.5,2.04 12,2.04Z' },
  { icon: 'M19,3A2,2 0 0,1 21,5V19A2,2 0 0,1 19,21H5A2,2 0 0,1 3,19V5A2,2 0 0,1 5,3H19M18.5,18.5V13.2A3.26,3.26 0 0,0 15.24,9.94C14.39,9.94 13.4,10.46 12.92,11.24V10.13H10.13V18.5H12.92V13.57C12.92,12.8 13.54,12.17 14.31,12.17A1.4,1.4 0 0,1 15.71,13.57V18.5H18.5M6.88,8.56A1.68,1.68 0 0,0 8.56,6.88C8.56,5.95 7.81,5.19 6.88,5.19A1.69,1.69 0 0,0 5.19,6.88C5.19,7.81 5.95,8.56 6.88,8.56M8.27,18.5V10.13H5.5V18.5H8.27Z' }
]);

// 页脚部分
const footerSections = reactive([
  {
    title: '产品',
    links: ['Vue3开发', 'UI/UX设计', '网站优化', '移动适配']
  },
  {
    title: '服务',
    links: ['定制开发', '技术咨询', '维护支持', '性能优化']
  },
  {
    title: '资源',
    links: ['文档', '教程', '博客', '案例研究']
  }
]);

// 联系表单
const showContactForm = ref(false);
const contactForm = reactive({
  name: '',
  email: '',
  subject: '',
  message: ''
});

const submitContactForm = () => {
  console.log('提交表单:', contactForm);
  // 这里可以添加实际的表单提交逻辑
  showContactForm.value = false;
  contactForm.name = '';
  contactForm.email = '';
  contactForm.subject = '';
  contactForm.message = '';
};

// 演示视频
const showDemoVideo = ref(false);

// 滚动到特性部分
const featuresSection = ref(null);

const scrollToFeatures = () => {
  featuresSection.value.scrollIntoView({ behavior: 'smooth' });
  animateStats();
};

// 初始化
onMounted(() => {
  createParticles();

  // 设置导航链接激活状态
  window.addEventListener('scroll', () => {
    const scrollPosition = window.scrollY;
    if (scrollPosition < 100) {
      activeLink.value = 0;
    }
  });
});
</script>

<style scoped>
/* 玻璃拟态核心样式 - 精美现代设计 */
.introduce-container {
  min-height: 100vh;
  background-image: linear-gradient(to right, #11998e , #38ef7d );
  position: relative;
  font-family: 'Inter', system-ui, -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
  color: #fff;
  overflow-x: hidden;
}

/* 背景粒子效果 */
.introduce-background {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 0;
  overflow: hidden;
}

.introduce-particle {
  position: absolute;
  background: rgba(255, 255, 255, 0.5);
  border-radius: 50%;
  pointer-events: none;
  animation: float 6s ease-in-out infinite;
  box-shadow: 0 0 20px rgba(255, 255, 255, 0.3);
}

@keyframes float {

  0%,
  100% {
    transform: translateY(0) translateX(0);
  }

  25% {
    transform: translateY(-20px) translateX(10px);
  }

  50% {
    transform: translateY(-10px) translateX(-10px);
  }

  75% {
    transform: translateY(-30px) translateX(5px);
  }
}

/* 导航栏 - 玻璃拟态效果 */
.introduce-nav {
  position: sticky;
  top: 20px;
  z-index: 100;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 2.5rem;
  margin: 0 2rem;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border-radius: 50px;
  border: 1px solid rgba(255, 255, 255, 0.2);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.introduce-nav-container {
  display: flex;
  align-items: center;
  gap: 3rem;
}

.introduce-nav-logo {
  font-size: 1.5rem;
  font-weight: 600;
  letter-spacing: -0.5px;
  background: rgba(255, 255, 255, 0.2);
  padding: 0.5rem 1rem;
  border-radius: 30px;
  backdrop-filter: blur(5px);
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.introduce-logo-text {
  color: white;
  opacity: 0.9;
}

.introduce-logo-highlight {
  color: #ffd700;
  margin-left: 0.5rem;
  font-weight: 700;
  text-shadow: 0 2px 10px rgba(255, 215, 0, 0.3);
}

.introduce-nav-links {
  display: flex;
  gap: 2rem;
  list-style: none;
  margin: 0;
  padding: 0;
}

.elegant-button {
  background: transparent;
  border: none;
  color: white;
  font-size: 1rem;
  padding: 0.5rem 1rem;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  border-radius: 20px;
  transition: all 0.3s ease;
}

.introduce-link-text {
  position: relative;
  z-index: 1;
  font-weight: 500;
}

.introduce-link-underline {
  position: absolute;
  bottom: 0;
  left: 0;
  height: 2px;
  background: linear-gradient(90deg, #ffd700, #ff8c00);
  transition: width 0.3s ease, opacity 0.3s ease;
  border-radius: 2px;
  box-shadow: 0 0 10px #ffd700;
}

.introduce-link-active .introduce-link-text {
  color: #ffd700;
}

.introduce-nav-button {
  background: rgba(255, 255, 255, 0.15);
  border: 1px solid rgba(255, 255, 255, 0.3);
  color: white;
  padding: 0.7rem 1.8rem;
  border-radius: 30px;
  font-size: 1rem;
  font-weight: 500;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: all 0.3s ease;
  backdrop-filter: blur(5px);
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
}

.introduce-nav-button:hover {
  background: rgba(255, 255, 255, 0.25);
  border-color: rgba(255, 255, 255, 0.5);
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.2);
}

.introduce-button-underline {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 2px;
  background: linear-gradient(90deg, #ffd700, #ff8c00);
  transform: scaleX(0);
  transition: transform 0.3s ease;
}

.introduce-nav-button:hover .introduce-button-underline {
  transform: scaleX(1);
}

/* 主要内容区域 */
.introduce-main {
  position: relative;
  z-index: 1;
  padding: 2rem 2rem 0;
}

/* Hero 部分 - 玻璃卡片 */
.introduce-hero {
  display: flex;
  align-items: center;
  justify-content: space-between;
  min-height: 80vh;
  padding: 0 4rem;
  margin-bottom: 4rem;
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(5px);
  border-radius: 60px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  box-shadow: 0 20px 50px rgba(0, 0, 0, 0.2);
}

.introduce-hero-content {
  flex: 1;
  max-width: 600px;
}

.introduce-hero-title {
  font-size: 4rem;
  font-weight: 800;
  line-height: 1.2;
  margin-bottom: 1.5rem;
  text-shadow: 0 2px 20px rgba(0, 0, 0, 0.2);
}

.introduce-title-line {
  display: block;
}

.introduce-title-highlight {
  background: linear-gradient(135deg, #ffd700, #ff8c00);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  text-shadow: 0 0 30px rgba(255, 215, 0, 0.5);
}

.introduce-hero-subtitle {
  font-size: 1.25rem;
  opacity: 0.9;
  margin-bottom: 2.5rem;
  line-height: 1.6;
  background: rgba(255, 255, 255, 0.1);
  padding: 1rem 2rem;
  border-radius: 50px;
  display: inline-block;
  backdrop-filter: blur(5px);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.introduce-hero-buttons {
  display: flex;
  gap: 1.5rem;
}

.introduce-primary-button,
.introduce-secondary-button {
  padding: 1rem 2.5rem;
  border-radius: 50px;
  font-size: 1.1rem;
  font-weight: 600;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 0.8rem;
  transition: all 0.3s ease;
  border: none;
}

.introduce-primary-button {
  background: linear-gradient(135deg, #ffd700, #ff8c00);
  color: #2d3748;
  box-shadow: 0 10px 30px rgba(255, 215, 0, 0.3);
}

.introduce-primary-button:hover {
  transform: translateY(-3px);
  box-shadow: 0 15px 40px rgba(255, 215, 0, 0.4);
}

.introduce-secondary-button {
  background: rgba(255, 255, 255, 0.15);
  color: white;
  backdrop-filter: blur(5px);
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.introduce-secondary-button:hover {
  background: rgba(255, 255, 255, 0.25);
  transform: translateY(-3px);
  border-color: rgba(255, 255, 255, 0.5);
}

.introduce-button-icon {
  width: 20px;
  height: 20px;
  fill: currentColor;
}

.introduce-hero-image {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
  max-width: 500px;
  margin-left: 2rem;
}

.introduce-image {
  width: 100%;
  height: auto;
  border-radius: 30px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.3);
  border: 1px solid rgba(255, 255, 255, 0.2);
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(5px);
  transition: transform 0.5s ease;
}

.introduce-image:hover {
  transform: scale(1.02) rotate(1deg);
}

.introduce-image-highlight {
  position: absolute;
  top: -10px;
  left: -10px;
  right: -10px;
  bottom: -10px;
  border-radius: 40px;
  background: linear-gradient(135deg, rgba(255, 215, 0, 0.2), rgba(255, 140, 0, 0.2));
  z-index: -1;
  filter: blur(20px);
}

/* 特性部分 */
.introduce-features {
  padding: 6rem 2rem;
  background: rgba(255, 255, 255, 0.03);
  backdrop-filter: blur(5px);
  border-radius: 60px;
  margin: 4rem 0;
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.introduce-section-header {
  text-align: center;
  margin-bottom: 4rem;
}

.introduce-section-title {
  font-size: 3rem;
  font-weight: 700;
  margin-bottom: 1rem;
  background: linear-gradient(135deg, #fff, #e0e0e0);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.introduce-section-subtitle {
  font-size: 1.25rem;
  opacity: 0.8;
  margin-bottom: 1rem;
}

.introduce-section-divider {
  width: 80px;
  height: 4px;
  background: linear-gradient(90deg, #ffd700, #ff8c00);
  margin: 0 auto;
  border-radius: 2px;
  box-shadow: 0 0 20px #ffd700;
}

.introduce-features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 2rem;
  max-width: 1200px;
  margin: 0 auto;
}

.introduce-feature-card {
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border-radius: 30px;
  padding: 2.5rem 2rem;
  text-align: center;
  border: 1px solid rgba(255, 255, 255, 0.2);
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
  cursor: pointer;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
}

.introduce-feature-card:hover {
  transform: translateY(-10px);
  border-color: rgba(255, 215, 0, 0.5);
  box-shadow: 0 20px 40px rgba(255, 215, 0, 0.2);
}

.introduce-feature-icon {
  width: 80px;
  height: 80px;
  margin: 0 auto 1.5rem;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  border: 1px solid rgba(255, 255, 255, 0.2);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.2);
}

.introduce-feature-icon svg {
  width: 40px;
  height: 40px;
  fill: #ffd700;
}

.introduce-feature-title {
  font-size: 1.5rem;
  font-weight: 600;
  margin-bottom: 1rem;
  color: #fff;
}

.introduce-feature-description {
  font-size: 1rem;
  line-height: 1.6;
  opacity: 0.9;
  color: rgba(255, 255, 255, 0.8);
}

.introduce-feature-highlight {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: radial-gradient(circle at center, rgba(255, 215, 0, 0.3), transparent 70%);
  pointer-events: none;
  transition: opacity 0.3s ease;
  border-radius: 30px;
}

/* 统计数据 */
.introduce-stats {
  display: flex;
  justify-content: space-around;
  padding: 4rem 2rem;
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(5px);
  border-radius: 60px;
  margin: 4rem 0;
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.introduce-stats-item {
  text-align: center;
}

.introduce-stat-value {
  font-size: 3.5rem;
  font-weight: 800;
  margin-bottom: 0.5rem;
  background: linear-gradient(135deg, #ffd700, #ff8c00);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  text-shadow: 0 0 30px rgba(255, 215, 0, 0.5);
}

.introduce-stat-label {
  font-size: 1.1rem;
  opacity: 0.9;
  text-transform: uppercase;
  letter-spacing: 1px;
}

/* 客户评价 */
.introduce-testimonials {
  padding: 4rem 2rem;
  background: rgba(255, 255, 255, 0.03);
  backdrop-filter: blur(5px);
  border-radius: 60px;
  margin: 4rem 0;
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.introduce-testimonial-slider {
  position: relative;
  max-width: 800px;
  margin: 0 auto;
  overflow: hidden;
  border-radius: 30px;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  padding: 2rem;
}

.introduce-testimonial-track {
  display: flex;
  transition: transform 0.5s ease;
}

.introduce-testimonial-slide {
  flex: 0 0 100%;
  padding: 0 1rem;
}

.introduce-testimonial-card {
  text-align: center;
}

.introduce-testimonial-quote {
  font-size: 6rem;
  font-family: serif;
  color: #ffd700;
  opacity: 0.5;
  line-height: 1;
  margin-bottom: -1rem;
}

.introduce-testimonial-text {
  font-size: 1.25rem;
  line-height: 1.8;
  margin-bottom: 2rem;
  font-style: italic;
  color: rgba(255, 255, 255, 0.9);
}

.introduce-testimonial-author {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 1rem;
}

.introduce-testimonial-avatar {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  border: 3px solid #ffd700;
  object-fit: cover;
}

.introduce-testimonial-name {
  font-size: 1.1rem;
  font-weight: 600;
  margin-bottom: 0.25rem;
}

.introduce-testimonial-position {
  font-size: 0.9rem;
  opacity: 0.7;
}

.introduce-slider-button {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  width: 50px;
  height: 50px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.3);
  color: white;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  backdrop-filter: blur(5px);
}

.introduce-slider-button:hover {
  background: rgba(255, 255, 255, 0.3);
  border-color: #ffd700;
}

.introduce-slider-prev {
  left: 1rem;
}

.introduce-slider-next {
  right: 1rem;
}

.introduce-slider-button svg {
  width: 24px;
  height: 24px;
  fill: currentColor;
}

/* 页脚 - 玻璃拟态 */
.introduce-footer {
  background: rgba(0, 0, 0, 0.3);
  backdrop-filter: blur(10px);
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  padding: 4rem 2rem 2rem;
  margin-top: 4rem;
  border-radius: 60px 60px 0 0;
}

.introduce-footer-content {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 3rem;
  max-width: 1200px;
  margin: 0 auto;
}

.introduce-footer-section {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.introduce-footer-description {
  line-height: 1.6;
  opacity: 0.8;
}

.introduce-social-links {
  display: flex;
  gap: 1rem;
}

.introduce-social-link {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.introduce-social-link:hover {
  background: #ffd700;
  transform: translateY(-3px);
}

.introduce-social-link svg {
  width: 20px;
  height: 20px;
  fill: white;
}

.introduce-footer-title {
  font-size: 1.2rem;
  font-weight: 600;
  margin-bottom: 1rem;
  color: #ffd700;
}

.introduce-footer-links {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: 0.8rem;
}

.introduce-footer-link {
  color: rgba(255, 255, 255, 0.7);
  text-decoration: none;
  transition: all 0.3s ease;
}

.introduce-footer-link:hover {
  color: #ffd700;
  padding-left: 0.5rem;
}

.introduce-footer-bottom {
  max-width: 1200px;
  margin: 3rem auto 0;
  padding-top: 2rem;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 1rem;
}

.introduce-copyright {
  opacity: 0.7;
  font-size: 0.9rem;
}

.introduce-legal-links {
  display: flex;
  gap: 2rem;
}

.introduce-legal-link {
  color: rgba(255, 255, 255, 0.7);
  text-decoration: none;
  font-size: 0.9rem;
  transition: color 0.3s ease;
}

.introduce-legal-link:hover {
  color: #ffd700;
}

/* 模态框 - 玻璃拟态 */
.introduce-modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  backdrop-filter: blur(10px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: 1rem;
}

.introduce-modal-content {
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(20px);
  border-radius: 40px;
  padding: 3rem;
  max-width: 500px;
  width: 100%;
  position: relative;
  border: 1px solid rgba(255, 255, 255, 0.2);
  box-shadow: 0 25px 50px rgba(0, 0, 0, 0.5);
}

.introduce-modal-close {
  position: absolute;
  top: 1rem;
  right: 1rem;
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: white;
  transition: all 0.3s ease;
}

.introduce-modal-close:hover {
  background: rgba(255, 255, 255, 0.2);
  transform: rotate(90deg);
}

.introduce-modal-close svg {
  width: 20px;
  height: 20px;
  fill: currentColor;
}

.introduce-modal-title {
  font-size: 2rem;
  font-weight: 600;
  margin-bottom: 2rem;
  text-align: center;
  background: linear-gradient(135deg, #ffd700, #ff8c00);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.introduce-contact-form {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.introduce-form-group {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.introduce-form-label {
  font-size: 0.95rem;
  font-weight: 500;
  opacity: 0.9;
  margin-left: 0.5rem;
}

.introduce-form-input,
.introduce-form-textarea {
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 30px;
  padding: 1rem 1.5rem;
  color: white;
  font-size: 1rem;
  transition: all 0.3s ease;
  outline: none;
}

.introduce-form-textarea {
  min-height: 120px;
  resize: vertical;
}

.introduce-form-input:focus,
.introduce-form-textarea:focus {
  border-color: #ffd700;
  box-shadow: 0 0 0 3px rgba(255, 215, 0, 0.1);
}

.introduce-submit-button {
  background: linear-gradient(135deg, #ffd700, #ff8c00);
  color: #2d3748;
  border: none;
  padding: 1rem 2rem;
  border-radius: 50px;
  font-size: 1.1rem;
  font-weight: 600;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.8rem;
  transition: all 0.3s ease;
  margin-top: 1rem;
}

.introduce-submit-button:hover {
  transform: translateY(-3px);
  box-shadow: 0 10px 30px rgba(255, 215, 0, 0.3);
}

.introduce-video-modal {
  max-width: 800px;
  padding: 2rem;
}

.introduce-video-container {
  width: 100%;
  border-radius: 20px;
  overflow: hidden;
  background: #000;
}

.introduce-video {
  width: 100%;
  display: block;
}

/* 返回按钮 */
.introduce-back-button {
  position: fixed;
  bottom: 2rem;
  right: 2rem;
  z-index: 100;
  background: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(10px);
  border-radius: 50%;
  width: 50px;
  height: 50px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid rgba(255, 255, 255, 0.3);
  cursor: pointer;
  transition: all 0.3s ease;
}

.introduce-back-button:hover {
  transform: scale(1.1);
  background: rgba(255, 255, 255, 0.25);
  border-color: #ffd700;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .introduce-nav {
    flex-direction: column;
    gap: 1rem;
    padding: 1rem;
    margin: 1rem;
  }

  .introduce-nav-container {
    flex-direction: column;
    gap: 1rem;
  }

  .introduce-nav-links {
    flex-wrap: wrap;
    justify-content: center;
  }

  .introduce-hero {
    flex-direction: column;
    padding: 2rem;
    text-align: center;
  }

  .introduce-hero-title {
    font-size: 2.5rem;
  }

  .introduce-hero-buttons {
    justify-content: center;
  }

  .introduce-stats {
    flex-direction: column;
    gap: 2rem;
  }

  .introduce-footer-bottom {
    flex-direction: column;
    text-align: center;
  }

  .introduce-legal-links {
    justify-content: center;
  }
}

/* 动画效果 */
@keyframes pulse {

  0%,
  100% {
    opacity: 0.6;
  }

  50% {
    opacity: 1;
  }
}

.introduce-feature-card {
  animation: fadeInUp 0.6s ease backwards;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 为特性卡片添加延迟动画 */
.introduce-feature-card:nth-child(1) {
  animation-delay: 0.1s;
}

.introduce-feature-card:nth-child(2) {
  animation-delay: 0.2s;
}

.introduce-feature-card:nth-child(3) {
  animation-delay: 0.3s;
}

.introduce-feature-card:nth-child(4) {
  animation-delay: 0.4s;
}

.introduce-feature-card:nth-child(5) {
  animation-delay: 0.5s;
}

.introduce-feature-card:nth-child(6) {
  animation-delay: 0.6s;
}
</style>