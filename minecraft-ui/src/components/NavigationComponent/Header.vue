<template>
  <!-- 导航栏 -->
  <div class="navbar-background">
    <div class="navbar-background-container">
      <div class="navbar-home">
        <div class="logo" @click="navigateTo('/home')">
          <img v-if="mediaList.images.length > 0" :src="mediaList.images[0].image" :alt="$t('common.brand')">
          <img v-else :src="defaultLogo" :alt="$t('common.brand')">
          <span class="brand-text">{{ $t('common.brand') }}</span>
        </div>
        <div class="nav-items">
          <div class="center-items">
            <button v-for="navItem in navItems" :key="navItem.path" class="action-btn"
              :class="{ 'nav-item-active': isActiveNav(navItem.path) }"
              @click="navigateTo(navItem.path)">
              {{ navItem.labelKey ? $t(navItem.labelKey) : navItem.label }}
            </button>
          </div>
          <div class="dropdown">
            <button class="action-btn dropdown-toggle">{{ $t('header.more') }}</button>
            <div class="dropdown-menu_Home_2">
              <button v-for="(item, index) in moreMenuItems" :key="item.path"
                :style="index === moreMenuItems.length - 1 ? 'border-radius: 0 0 10px 10px' : ''"
                class="dropdown-item_action-btn"
                @click="navigateTo(item.path)">{{ item.labelKey ? $t(item.labelKey) : item.label }}</button>
            </div>
          </div>
        </div>
        <div class="actions">
          <LanguageSwitcher />
          <ThemeSwitching />
          <!-- 微信 -->
          <button class="action-btn-iocn" @click="openSocialLogin('wechat')">
            <svg t="1737552806004" class="action-btn-iocn-size" viewBox="0 0 1024 1024" version="1.1"
              xmlns="http://www.w3.org/2000/svg" p-id="6242">
              <path
                d="M337.387283 341.82659c-17.757225 0-35.514451 11.83815-35.514451 29.595375s17.757225 29.595376 35.514451 29.595376 29.595376-11.83815 29.595376-29.595376c0-18.49711-11.83815-29.595376-29.595376-29.595375zM577.849711 513.479769c-11.83815 0-22.936416 12.578035-22.936416 23.6763 0 12.578035 11.83815 23.676301 22.936416 23.676301 17.757225 0 29.595376-11.83815 29.595376-23.676301s-11.83815-23.676301-29.595376-23.6763zM501.641618 401.017341c17.757225 0 29.595376-12.578035 29.595376-29.595376 0-17.757225-11.83815-29.595376-29.595376-29.595375s-35.514451 11.83815-35.51445 29.595375 17.757225 29.595376 35.51445 29.595376zM706.589595 513.479769c-11.83815 0-22.936416 12.578035-22.936416 23.6763 0 12.578035 11.83815 23.676301 22.936416 23.676301 17.757225 0 29.595376-11.83815 29.595376-23.676301s-11.83815-23.676301-29.595376-23.6763z"
                fill="#28C445" p-id="6243"></path>
              <path
                d="M510.520231 2.959538C228.624277 2.959538 0 231.583815 0 513.479769s228.624277 510.520231 510.520231 510.520231 510.520231-228.624277 510.520231-510.520231-228.624277-510.520231-510.520231-510.520231zM413.595376 644.439306c-29.595376 0-53.271676-5.919075-81.387284-12.578034l-81.387283 41.433526 22.936416-71.768786c-58.450867-41.433526-93.965318-95.445087-93.965317-159.815029 0-113.202312 105.803468-201.988439 233.803468-201.98844 114.682081 0 216.046243 71.028902 236.023121 166.473989-7.398844-0.739884-14.797688-1.479769-22.196532-1.479769-110.982659 1.479769-198.289017 85.086705-198.289017 188.67052 0 17.017341 2.959538 33.294798 7.398844 49.572255-7.398844 0.739884-15.537572 1.479769-22.936416 1.479768z m346.265896 82.867052l17.757225 59.190752-63.630058-35.514451c-22.936416 5.919075-46.612717 11.83815-70.289017 11.83815-111.722543 0-199.768786-76.947977-199.768786-172.393063-0.739884-94.705202 87.306358-171.653179 198.289017-171.65318 105.803468 0 199.028902 77.687861 199.028902 172.393064 0 53.271676-34.774566 100.624277-81.387283 136.138728z"
                fill="#28C445" p-id="6244"></path>
            </svg>
          </button>
          <!-- QQ -->
          <button class="action-btn-iocn" @click="openSocialLogin('qq')">
            <svg t="1737552871738" class="action-btn-iocn-size" viewBox="0 0 1024 1024" version="1.1"
              xmlns="http://www.w3.org/2000/svg" p-id="7246">
              <path
                d="M512 0C229.003636 0 0 229.003636 0 512s229.003636 512 512 512 512-229.003636 512-512S794.996364 0 512 0z m210.385455 641.396364c-7.447273 9.309091-26.996364-1.861818-41.89091-32.581819-3.723636 13.963636-13.032727 36.305455-34.443636 64.232728 35.374545 8.378182 44.683636 42.821818 33.512727 61.44-8.378182 13.032727-26.996364 24.203636-59.578181 24.203636-58.647273 0-83.781818-15.825455-95.883637-26.996364-1.861818-2.792727-5.585455-3.723636-10.24-3.723636-4.654545 0-7.447273 0.930909-10.24 3.723636-11.170909 11.170909-37.236364 26.996364-95.883636 26.996364-32.581818 0-52.130909-11.170909-59.578182-24.203636-12.101818-18.618182-1.861818-53.061818 33.512727-61.44-20.48-27.927273-29.789091-50.269091-34.443636-64.232728-13.963636 30.72-34.443636 42.821818-41.890909 32.581819-5.585455-8.378182-8.378182-26.065455-7.447273-38.167273 3.723636-46.545455 34.443636-85.643636 53.061818-106.123636-2.792727-5.585455-8.378182-40.029091 14.894546-63.301819v-1.861818c0-92.16 65.163636-158.254545 148.014545-158.254545 81.92 0 148.014545 66.094545 148.014546 158.254545v1.861818c23.272727 23.272727 17.687273 57.716364 14.894545 63.301819 17.687273 20.48 49.338182 59.578182 53.061818 106.123636 0.930909 12.101818-0.930909 29.789091-7.447272 38.167273z"
                fill="#30A5DD" p-id="7247"></path>
            </svg>
          </button>
          <!--微博  -->
          <button class="action-btn-iocn" @click="openSocialLogin('weibo')">
            <svg t="1737552907646" class="action-btn-iocn-size" viewBox="0 0 1024 1024" version="1.1"
              xmlns="http://www.w3.org/2000/svg" p-id="8263">
              <path
                d="M448.698182 482.210909c-96.814545 4.654545-175.010909 56.785455-175.010909 121.949091s78.196364 114.501818 175.010909 109.847273S623.709091 647.912727 623.709091 582.749091c-0.930909-64.232727-79.127273-105.192727-175.010909-100.538182z m65.163636 164.770909c-29.789091 39.098182-88.436364 57.716364-145.221818 26.065455-26.996364-14.894545-26.065455-43.752727-26.065455-43.752728s-11.170909-92.16 85.643637-103.330909c97.745455-12.101818 115.432727 81.92 85.643636 121.018182z"
                fill="#EA5D5C" p-id="8264"></path>
              <path
                d="M448.698182 584.610909c-6.516364 4.654545-7.447273 13.032727-3.723637 18.618182 2.792727 5.585455 11.170909 6.516364 16.756364 1.861818 5.585455-4.654545 8.378182-13.032727 4.654546-18.618182-2.792727-5.585455-10.24-6.516364-17.687273-1.861818zM403.083636 597.643636c-18.618182 1.861818-30.72 17.687273-30.72 33.512728 0 14.894545 14.894545 26.065455 32.581819 24.203636 17.687273-1.861818 32.581818-15.825455 32.581818-31.650909s-13.963636-27.927273-34.443637-26.065455z"
                fill="#EA5D5C" p-id="8265"></path>
              <path
                d="M512 0C229.003636 0 0 229.003636 0 512s229.003636 512 512 512 512-229.003636 512-512S794.996364 0 512 0z m197.352727 626.501818C669.323636 712.145455 538.065455 754.036364 441.250909 746.589091c-92.16-7.447273-211.316364-38.167273-223.418182-151.738182 0 0-6.516364-51.2 42.821818-117.294545 0 0 70.749091-99.607273 152.669091-128.465455 82.850909-27.927273 92.16 19.549091 92.16 48.407273-4.654545 24.203636-12.101818 38.167273 18.618182 28.858182 0 0 80.989091-38.167273 114.501818-4.654546 26.996364 26.996364 4.654545 65.163636 4.654546 65.163637s-11.170909 12.101818 12.101818 16.756363c21.410909 3.723636 94.021818 37.236364 53.992727 122.88z m-80.058182-236.450909c-8.378182 0-15.825455-7.447273-15.825454-15.825454 0-9.309091 7.447273-15.825455 15.825454-15.825455 0 0 99.607273-18.618182 87.505455 89.367273v1.861818c-0.930909 7.447273-7.447273 13.963636-15.825455 13.963636-9.309091 0-15.825455-7.447273-15.825454-15.825454 0-1.861818 15.825455-73.541818-55.854546-57.716364zM797.789091 493.381818c-2.792727 18.618182-12.101818 11.170909-22.341818 11.170909-13.032727 0-23.272727-16.756364-23.272728-29.789091 0-11.170909 4.654545-22.341818 4.654546-22.341818 0.930909-4.654545 12.101818-34.443636-7.447273-78.196363-35.374545-60.509091-106.123636-60.509091-114.501818-57.716364-8.378182 3.723636-21.410909 5.585455-21.410909 5.585454-13.032727 0-23.272727-10.24-23.272727-23.272727 0-11.170909 7.447273-19.549091 16.756363-22.341818 0 0 0 0.930909 0.930909 0.930909s1.861818 0.930909 1.861819 0.930909c10.24-1.861818 45.614545-4.654545 79.127272 3.723637 62.370909 14.894545 146.152727 83.781818 108.916364 211.316363z"
                fill="#EA5D5C" p-id="8266"></path>
            </svg>
          </button>
          <!-- 系统公告铃铛（未读数红点） -->
          <AnnouncementBell />
          <!-- 登录和注册按钮（未登录时显示） -->
          <template v-if="!isLoggedIn">
            <button class="action-btn" @click="LoginName">
              <svg t="1741965110034" class="icon" viewBox="0 0 1024 1024" version="1.1"
                xmlns="http://www.w3.org/2000/svg" p-id="10018" width="20" height="20" style="vertical-align: middle;">
                <path
                  d="M65.843 512.919c0 246.179 199.576 445.755 445.76 445.755 246.179 0 445.755-199.576 445.755-445.755 0-246.185-199.576-445.76-445.755-445.76-246.184 0-445.76 199.575-445.76 445.76z m0 0"
                  fill="#EDEEF5" p-id="10019"></path>
                <path
                  d="M411.306 468.342v111.437s3.898 72.549-44.577 83.581c0 0-1.559 117.01 156.015 117.01V468.342H411.306z m222.876 0v111.437s-3.898 72.549 44.577 83.581c0 0 1.561 117.01-156.014 117.01V468.342h111.437z m0 0"
                  fill="#EDE7E6" p-id="10020"></path>
                <path
                  d="M400.16 546.349s100.296 138.075 267.457 111.439c-54.159-15.934-33.435-133.726-33.435-133.726L400.16 546.349z m0 0"
                  fill="#E3DCDB" p-id="10021"></path>
                <path
                  d="M508.46 958.674c-151.279-0.025-292.22-76.766-374.329-203.822a408.723 408.723 0 0 1 240.598-97.064c22.012 60.866 80.154 101.116 144.872 100.296 93.719 0 133.726-100.296 133.726-100.296s156.014 16.494 236.7 85.586c-80.714 133.632-225.454 215.3-381.567 215.3z m0 0"
                  fill="#3A8BCE" p-id="10022"></path>
                <path
                  d="M656.471 421.201s26.637-49.367 40.453-22.956c13.82 26.412-1.561 93.163-51.596 114.674l11.143-91.718z m0 0"
                  fill="#EFE7E6" p-id="10023"></path>
                <path
                  d="M517.172 278.893c33.434 0 128.157 11.146 128.157 11.146s11.143 106.982 11.143 156.014c0 92.27-68.536 167.162-139.299 167.162-70.764 0-139.299-74.892-139.299-167.162 0-57.951 11.146-156.014 11.146-156.014s101.855-11.146 128.152-11.146z m0 0"
                  fill="#F7EDEB" p-id="10024"></path>
                <path
                  d="M389.019 390.334s-59.732-63.856 0-167.162c30.089-52.041 93.162-55.719 122.585-55.719 104.861 0 170.055 34.323 178.302 122.585 7.687 82.908-17.276 112.887-22.288 111.437 0 0-33.435-53.602-33.435-89.15 0 0-122.579 45.912-200.587-44.577-35.663 9.027-44.577 100.297-44.577 122.586z m0 0"
                  fill="#3A8BCE" p-id="10025"></path>
              </svg>
              <span style="vertical-align: middle;">{{ $t('header.login') }}</span>
            </button>
            <button class="action-btn" @click="EnrolFirst">
              <svg t="1741965357577" class="icon" viewBox="0 0 1024 1024" version="1.1"
                xmlns="http://www.w3.org/2000/svg" p-id="15726" width="20" height="20" style="vertical-align: middle;">
                <path
                  d="M476.86656 537.6c151.97696 0 274.40128-119.46496 274.40128-268.8S628.84352 0 476.86656 0C324.89472 0 198.2464 119.46496 198.2464 268.8c4.224 149.33504 126.64832 268.8 278.62016 268.8z m-105.53344 51.2C177.14176 588.8 33.60768 712.53504 33.60768 900.26496V921.6c0 98.13504 156.19584 98.13504 350.3872 98.13504h194.19136c-84.4288-51.2-139.3152-140.8-139.3152-243.2C438.87616 704 464.20992 640 510.64832 588.8h-139.3152z"
                  fill="#727BB2" p-id="15727"></path>
                <path
                  d="M738.60096 1024c33.77664 0 63.32416-4.26496 97.09568-17.06496 29.55264-12.8 54.88128-29.87008 80.20992-51.2 21.10976-21.33504 37.9904-46.93504 50.65728-76.8 12.66688-29.87008 21.10976-59.73504 21.10976-93.87008 0-34.12992-4.224-64-21.10976-93.86496-12.66176-29.86496-29.54752-55.46496-50.65728-76.8s-50.65728-38.4-80.20992-51.2c-29.54752-12.8-63.31904-17.06496-97.09568-17.06496-33.77152 0-67.54304 4.26496-97.09056 17.06496-29.55264 12.8-54.8864 29.86496-80.20992 51.2-21.10976 21.33504-42.2144 46.93504-54.88128 76.8-12.66176 25.6-21.10464 59.73504-21.10464 93.86496 0 34.13504 4.21888 64 21.10464 93.87008 12.66688 29.86496 29.55264 55.46496 54.88128 76.8 21.10464 21.32992 50.65728 38.4 80.20992 51.2 29.54752 12.8 63.31904 17.06496 97.09056 17.06496z m122.42432-281.6c21.10976 0 42.21952 17.06496 42.21952 38.4 0 8.53504-4.224 21.33504-12.66688 25.6-8.448 4.26496-16.88576 8.53504-29.55264 8.53504h-88.64768V896c0 12.8-4.224 21.33504-8.448 29.86496-8.44288 8.53504-16.88576 12.8-29.54752 12.8-12.66688 0-21.10976-4.26496-25.32864-12.8-4.224-8.52992-8.448-17.06496-8.448-29.86496v-81.06496h-80.2048c-12.66688 0-21.10976 0-29.55264-8.53504-8.44288-4.26496-12.66176-12.8-12.66176-25.6 0-8.53504 4.21888-21.33504 12.66176-25.6 8.448-8.53504 16.88576-12.8 29.55264-12.8h80.20992v-81.06496c0-8.53504 4.21888-21.33504 8.44288-25.6 4.21888-8.53504 16.88576-12.8 25.32864-12.8 12.66176 0 21.10464 4.26496 29.55264 12.8 8.44288 8.52992 8.44288 17.06496 8.44288 25.6V742.4h88.64768z"
                  fill="#727BB2" p-id="15728"></path>
              </svg>
              <span style="vertical-align: middle;">{{ $t('header.register') }}</span>
            </button>
          </template>

          <!-- 用户头像（登录后显示） -->
          <div class="user-actions" v-else>
            <div class="profilepicture">
              <button class="profilepicture_button" @click="toggleDropdown">
                <div class="profilepicture_img">
                  <img v-if="!userInfo.avatar" :src="defaultAvatar" alt="默认头像" class="img1">
                  <img v-else :src="userInfo.avatar" alt="用户头像" class="img1" />
                </div>
              </button>
              <div class="profilepicture-inner" v-show="isDropdownVisible">
                <button v-for="item in filteredUserMenuItems" :key="item.label" :style="item.style" :class="item.class"
                  @click="handleMenuItemClick(item)">
                  {{ item.label }}
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
  <!-- 移动端底部悬浮导航（≤992px 显示）：4 个核心页面 + 「更多」入口 -->
  <nav class="mobile-tabbar" :aria-label="$t('header.nav.menu')">
    <button v-for="tab in mobileTabs" :key="tab.key" class="mobile-tab"
      :class="{ 'is-active': tab.more ? mobileMenuOpen || isMoreActive : isActiveNav(tab.path) }"
      @click.stop="onTabClick(tab)">
      <span class="mobile-tab-icon">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"
          stroke-linejoin="round">
          <path v-for="d in tabIcon(tab)" :key="d" :d="d" />
        </svg>
      </span>
      <span class="mobile-tab-label">{{ $t(tab.labelKey) }}</span>
    </button>
  </nav>

  <!-- 移动端「更多」底部浮层（≤992px 显示，其余页面以宫格展示） -->
  <Transition name="mobile-sheet-fade">
    <div v-if="mobileMenuOpen" class="mobile-sheet-mask" @click="mobileMenuOpen = false"></div>
  </Transition>
  <Transition name="mobile-sheet-slide">
    <div v-if="mobileMenuOpen" class="mobile-sheet" @click.stop>
      <span class="mobile-sheet-handle"></span>
      <div class="mobile-sheet-head">
        <span class="mobile-sheet-title">{{ $t('header.nav.sectionMore') }}</span>
        <button class="mobile-sheet-close" :aria-label="$t('header.nav.menu')" @click="mobileMenuOpen = false">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round">
            <path d="M18 6 6 18" />
            <path d="m6 6 12 12" />
          </svg>
        </button>
      </div>
      <div class="mobile-sheet-grid">
        <button v-for="item in moreSheetItems" :key="item.path" class="mobile-sheet-tile"
          :class="{ 'nav-item-active': isActiveNav(item.path) }" @click="navigateTo(item.path)">
          <span class="mobile-sheet-iconbox">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"
              stroke-linejoin="round">
              <path v-for="d in navIcon(item.path)" :key="d" :d="d" />
            </svg>
          </span>
          <span class="mobile-sheet-label">{{ item.labelKey ? $t(item.labelKey) : item.label }}</span>
        </button>
      </div>
    </div>
  </Transition>
  <!-- 页面渲染区域：由路由驱动 -->
  <main>
    <div class="content-area">
      <router-view />
    </div>
  </main>

  <!-- 退出登录确认对话框 -->
  <div>
    <Launchlogin v-if="showLogoutConfirm" @confirm="confirmLogout" @cancel="cancelLogout" />
  </div>
  <!-- 返回顶部 -->
  <div>
    <BacktoTop />
  </div>
  
  <!-- 社交平台登录模态框 -->
  <SocialLoginModal 
    :visible="showSocialLoginModal" 
    :platform="currentPlatform"
    @close="closeSocialLoginModal" 
  />
</template>
<script setup>
import { useRoute, useRouter } from 'vue-router';
import { ref, onMounted, onBeforeUnmount, computed, watch } from 'vue';
import { ElMessage } from 'element-plus';
import { useI18n } from 'vue-i18n';
// 页面组件
import ThemeSwitching from '@/components/ThemeComponents/ThemeSwitching.vue';
import LanguageSwitcher from '@/components/NavigationComponent/LanguageSwitcher.vue';
import AnnouncementBell from '@/components/AnnouncementComponents/AnnouncementBell.vue';
import Launchlogin from '@/components/PromptComponent/Launchlogin.vue';
import BacktoTop from '@/components/DisplayBox/BacktoTop.vue';
import SocialLoginModal from '@/components/PromptComponent/SocialLoginModal.vue';
// 导航菜单（由路由表 meta 派生，路由即导航的唯一数据源）
import { mainNavMenus, moreNavMenus } from '@/router/routes';
// 接口
import { useAuthStore } from '@/stores/auth';
import { getUserInfo, logout } from '@/api/user';

const props = defineProps({
  mediaList: {
    type: Object,
    default: () => ({ images: [] })
  }
});

const defaultAvatar = new URL('@/assets/defaultimage/mrtx.png', import.meta.url).href
const defaultLogo = new URL('@/assets/logo/logo.png', import.meta.url).href
const authStore = useAuthStore();

// 导航项数据（路由表派生）
const navItems = mainNavMenus;

// 更多下拉菜单项（路由表派生）
const moreMenuItems = moreNavMenus;

const { t } = useI18n();

// 用户操作菜单项数据（label 使用 i18n key，随语言切换响应式更新）
const userMenuItems = computed(() => [
  {
    label: t('header.userMenu.profile'),
    path: '/personalcenter',
    style: 'border-radius: 10px 10px 0 0;',
    show: true
  },
  {
    label: t('header.userMenu.community'),
    path: '/privatecommunity',
    style: 'border-radius: 0 0 10px 10px;',
    show: true
  },
  {
    label: t('header.userMenu.payment'),
    path: '/payment',
    style: 'border-radius: 0 0 10px 10px;',
    show: true
  },
  {
    label: t('header.userMenu.member'),
    path: '/member',
    style: 'border-radius: 0 0 10px 10px;',
    show: true
  },
  {
    label: t('header.userMenu.account'),
    path: '/accountsettings',
    style: 'border-radius: 0 0 10px 10px;',
    show: true
  },
  {
    label: t('header.userMenu.chat'),
    path: '/chat',
    show: true,
    class: 'im-button'
  },
  {
    label: t('header.userMenu.logout'),
    action: 'localLogout',
    show: true,
    class: 'logout-button'
  },
]);

const router = useRouter();
const route = useRoute();

/** 判断导航项是否为当前激活路由（含子路径） */
const isActiveNav = (path) => {
  return route.path === path || route.path.startsWith(`${path}/`);
};

/** 统一的导航跳转入口 */
const navigateTo = (path) => {
  if (path === route.path) return;
  router.push(path);
};
// 默认头像

// 页面跳转
const LoginName = () => {
  router.push('/login');
  ElMessage.success(t('header.messages.backToLogin'));
};
const EnrolFirst = () => {
  router.push('/login');
  ElMessage.success(t('header.messages.goRegister'));
};

const openSocialLogin = (platform) => {
  currentPlatform.value = platform;
  showSocialLoginModal.value = true;
};

const closeSocialLoginModal = () => {
  showSocialLoginModal.value = false;
};

const isLoggedIn = ref(false);
const isDropdownVisible = ref(false);
// 移动端「更多」浮层开关
const mobileMenuOpen = ref(false);

// 路由切换后自动收起移动端浮层
watch(() => route.path, () => {
  mobileMenuOpen.value = false;
});

// 点击页面其他区域时收起移动端浮层
const onDocumentClick = () => {
  mobileMenuOpen.value = false;
};
// 视口回到桌面宽度时收起浮层，避免滚动锁残留
const onWindowResize = () => {
  if (window.innerWidth > 992) mobileMenuOpen.value = false;
};
onMounted(() => {
  document.addEventListener('click', onDocumentClick);
  window.addEventListener('resize', onWindowResize);
});
onBeforeUnmount(() => {
  document.removeEventListener('click', onDocumentClick);
  window.removeEventListener('resize', onWindowResize);
  document.body.style.overflow = '';
});

/** 移动端菜单项图标（按路由路径映射，24x24 线性图标） */
const MOBILE_NAV_ICONS = {
  '/home': ['M3 10.5 12 3l9 7.5', 'M5 9.5V21h14V9.5'],
  '/worldtravel': ['M12 21a9 9 0 1 0 0-18 9 9 0 0 0 0 18z', 'M3 12h18', 'M12 3c2.6 2.6 4 5.6 4 9s-1.4 6.4-4 9c-2.6-2.6-4-5.6-4-9s1.4-6.4 4-9z'],
  '/scenicspot': ['M12 21s-7-5.5-7-11a7 7 0 1 1 14 0c0 5.5-7 11-7 11z', 'M12 12.5a2.5 2.5 0 1 0 0-5 2.5 2.5 0 0 0 0 5z'],
  '/hotel': ['M4 21V7l8-4 8 4v14', 'M9 21v-6h6v6', 'M9 10h.01', 'M15 10h.01'],
  '/food': ['M7 2v20', 'M7 2v6a2.5 2.5 0 0 0 5 0V2', 'M17 2c-1.7 1.2-2.5 3.4-2.5 5.5 0 2.1 1 3.5 2.5 3.5V22'],
  '/souvenir': ['M6 7h12l1 14H5L6 7z', 'M9 7a3 3 0 0 1 6 0'],
  '/strategy': ['M4 5.5A2.5 2.5 0 0 1 6.5 3H20v15H6.5A2.5 2.5 0 0 0 4 20.5z', 'M8 8h8', 'M8 12h6'],
  '/cards': ['M2 6h20v13H2z', 'M2 10h20', 'M6 15h4'],
  '/community': ['M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z', 'M8 9h8', 'M8 13h5'],
  '/ticket': ['M3 9V7a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2v2a3 3 0 0 0 0 6v2a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-2a3 3 0 0 0 0-6z', 'M13 5v2', 'M13 11v2', 'M13 17v2'],
  '/travel': ['M3 5h18v16H3z', 'M3 10h18', 'M8 3v4', 'M16 3v4', 'M8 15h3'],
  '/travel-note': ['M5 3h11l3 3v15H5z', 'M9 8h6', 'M9 12h6', 'M9 16h4'],
  '/maps': ['M12 22a10 10 0 1 0 0-20 10 10 0 0 0 0 20z', 'M2 12h20', 'M12 2c3 3 4 6.5 4 10s-1 7-4 10c-3-3-4-6.5-4-10s1-7 4-10z'],
  '/test': ['M9 3h6', 'M10 3v6L4.5 19a2 2 0 0 0 1.8 3h11.4a2 2 0 0 0 1.8-3L14 9V3', 'M7 15h10'],
  '/aboutweb': ['M12 22a10 10 0 1 0 0-20 10 10 0 0 0 0 20z', 'M12 16v-4', 'M12 8h.01'],
};
const DEFAULT_NAV_ICON = ['M4 6h16', 'M4 12h16', 'M4 18h16'];
/** 「更多」入口图标（2x2 宫格） */
const MORE_TAB_ICON = ['M4 4h6v6H4z', 'M14 4h6v6h-6z', 'M4 14h6v6H4z', 'M14 14h6v6h-6z'];
const navIcon = (path) => MOBILE_NAV_ICONS[path] ?? DEFAULT_NAV_ICON;

/** 移动端底部导航固定标签：只保留高频核心页面，其余收进「更多」浮层 */
const mobileTabs = [
  { key: 'home', path: '/home', labelKey: 'header.nav.home' },
  { key: 'destination', path: '/worldtravel', labelKey: 'header.nav.destination' },
  { key: 'scenic', path: '/scenicspot', labelKey: 'header.nav.scenic' },
  { key: 'hotel', path: '/hotel', labelKey: 'header.nav.hotel' },
  { key: 'more', more: true, labelKey: 'header.more' },
];
const mobileTabPaths = mobileTabs.filter((tab) => tab.path).map((tab) => tab.path);
const tabIcon = (tab) => (tab.more ? MORE_TAB_ICON : navIcon(tab.path));

/** 「更多」浮层内容 = 主菜单中未上底部栏的项 + 全部更多菜单项 */
const moreSheetItems = computed(() => [
  ...navItems.filter((item) => !mobileTabPaths.includes(item.path)),
  ...moreMenuItems,
]);

/** 当前页面属于「更多」中的某一项时，高亮底部「更多」标签 */
const isMoreActive = computed(() => moreSheetItems.value.some((item) => isActiveNav(item.path)));

/** 底部标签点击：核心页面直接跳转，「更多」展开/收起浮层 */
const onTabClick = (tab) => {
  if (tab.more) {
    mobileMenuOpen.value = !mobileMenuOpen.value;
    return;
  }
  mobileMenuOpen.value = false;
  navigateTo(tab.path);
};

// 浮层打开时锁定页面滚动，避免背景跟随滚动
watch(mobileMenuOpen, (open) => {
  document.body.style.overflow = open ? 'hidden' : '';
});
const showLogoutConfirm = ref(false);
const showSocialLoginModal = ref(false);
const currentPlatform = ref('wechat');
const toggleDropdown = () => {
  isDropdownVisible.value = !isDropdownVisible.value;
};
const localLogout = () => {
  // 检查是否有token，没有则直接退出登录状态
  const token = localStorage.getItem('token');
  const tokenTimestamp = localStorage.getItem('tokenTimestamp');
  // 检查token是否存在、是否为anonymousUser或是否超过24小时
  const isTokenExpired = tokenTimestamp && (Date.now() - parseInt(tokenTimestamp) > 24 * 60 * 60 * 1000);
  if (!token || token === 'anonymousUser' || isTokenExpired) {
    // 清除所有登录相关存储
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    localStorage.removeItem('rememberedUsername');
    localStorage.removeItem('tokenTimestamp');
    isLoggedIn.value = false;
    return;
  }

  // 显示退出确认对话框
  showLogoutConfirm.value = true;
};

// 确认退出登录
const confirmLogout = async () => {
  try {
    // 调用API退出登录
    await logout();

    // 清除所有登录相关存储
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    localStorage.removeItem('rememberedUsername');
    localStorage.removeItem('tokenTimestamp');

    // 更新登录状态
    isLoggedIn.value = false;
    // 关闭下拉菜单
    isDropdownVisible.value = false;
    // 关闭确认对话框
    showLogoutConfirm.value = false;

    ElMessage.success(t('header.messages.logoutSuccess'));
  } catch (error) {
    console.error('退出登录失败:', error);
    // 即使API调用失败，也清除本地存储
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    localStorage.removeItem('rememberedUsername');
    localStorage.removeItem('tokenTimestamp');
    isLoggedIn.value = false;
    isDropdownVisible.value = false;
    showLogoutConfirm.value = false;
    ElMessage.success(t('header.messages.loggedOut'));
  }
};

// 取消退出登录
const cancelLogout = () => {
  // 关闭确认对话框
  showLogoutConfirm.value = false;
  console.log('退出操作已取消');
};

// 处理菜单项点击
const handleMenuItemClick = (item) => {
  if (item.path) {
    navigateTo(item.path);
  } else if (item.action) {
    // 根据action名称调用对应的方法
    switch (item.action) {
      case 'AdminLayout':
        AdminLayout();
        break;
      case 'localLogout':
        localLogout();
        break;
      default:
        console.warn(`未知的操作: ${item.action}`);
    }
  }
};

// 用户信息
const userInfo = ref({
  username: '',
  permissions: '',
  avatar: ''
});

// 过滤后的用户菜单项
const filteredUserMenuItems = computed(() => {
  return userMenuItems.value.filter(item => {
    if (typeof item.show === 'function') {
      return item.show();
    }
    return item.show;
  });
});

const loading = ref(false);

// 获取用户信息
const fetchUserInfo = async () => {
  try {
    loading.value = true;
    //检查是否有token
    const token = localStorage.getItem('token');
    if (!token || token === 'anonymousUser') {
      isLoggedIn.value = false;
      return;
    }
    // 先从本地存储获取，优化用户体验
    const localUserStr = localStorage.getItem('user');
    if (localUserStr) {
      try {
        const localUser = JSON.parse(localUserStr);
        userInfo.value = { ...localUser };
        isLoggedIn.value = true;
      } catch (e) {
        console.error('解析本地用户数据失败:', e);
      }
    }

    //从服务器获取最新数据
    const response = await getUserInfo();

    if (response.code === 200) {
      userInfo.value = response.data;
      localStorage.setItem('user', JSON.stringify(response.data));
      isLoggedIn.value = true;
    } else {
      console.warn('获取用户信息返回非200状态:', response);
    }
  } catch (error) {
    console.error('获取用户信息失败:', error);
    // 检查是否是 401 或 403 错误
    const status = error.response?.status;
    if (status === 401 || status === 403) {
      // 清除无效的登录状态
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      localStorage.removeItem('rememberedUsername');
      localStorage.removeItem('tokenTimestamp');
      isLoggedIn.value = false;
      userInfo.value = {
        username: '',
        permissions: '',
        avatar: ''
      };
    }
  } finally {
    loading.value = false;
  }
};



// 初始化检查登录状态
onMounted(() => {
  isLoggedIn.value = !!localStorage.getItem('token');
  fetchUserInfo();
});
</script>

<style scoped>
@import "@/css/Home/HomeViews.css";

/* ================= Logo 品牌区 ================= */

.logo {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-shrink: 0;
  cursor: pointer;
  user-select: none;
}

.logo img {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  object-fit: cover;
  box-shadow: var(--theme-shadow-sm), 0 0 0 1px var(--theme-border);
  transition: transform 0.35s cubic-bezier(0.25, 0.8, 0.25, 1), box-shadow 0.35s ease;
}

.logo:hover img {
  transform: scale(1.06) rotate(-3deg);
  box-shadow: var(--theme-shadow-md), 0 0 0 1px var(--theme-primary-soft);
}

/* 品牌文字：品牌蓝渐变 + hover 光泽扫过 */
.brand-text {
  position: relative;
  font-size: 22px;
  font-weight: 800;
  letter-spacing: 2px;
  background: var(--theme-gradient);
  -webkit-background-clip: text;
  background-clip: text;
  -webkit-text-fill-color: transparent;
  color: transparent;
  white-space: nowrap;
  overflow: hidden;
}

.brand-text::after {
  content: '';
  position: absolute;
  top: 0;
  bottom: 0;
  left: -60%;
  width: 40%;
  transition: left 0.6s ease;
  pointer-events: none;
}

.logo:hover .brand-text::after {
  left: 120%;
}

/* ================= 响应式适配 ================= */

/* 移动端底部导航条与「更多」浮层：默认隐藏，≤992px 显示 */
.mobile-tabbar,
.mobile-sheet-mask,
.mobile-sheet {
  display: none;
}

/* ---- 大屏（≤1400px）：压缩间距 ---- */
@media (max-width: 1400px) {
  .navbar-home {
    gap: 12px;
    padding: 10px 16px;
  }

  .nav-items,
  .center-items {
    gap: 8px;
  }

  .actions {
    gap: 6px;
  }
}

/* ---- 中屏（≤1300px）：解除固定最小宽度，流式布局 ---- */
@media (max-width: 1300px) {
  .navbar-home {
    min-width: 0;
    width: 100%;
    font-size: 20px;
  }
}

/* ---- 平板/手机（≤992px）：底部悬浮导航替代水平导航 ---- */
@media (max-width: 992px) {
  /* 隐藏水平主导航与「更多」下拉 */
  .nav-items {
    display: none;
  }

  /* logo 与操作区两端分布 */
  .navbar-home {
    justify-content: space-between;
  }

  /* 底部悬浮导航条：毛玻璃 + 主题令牌，居中悬浮于安全区之上 */
  .mobile-tabbar {
    position: fixed;
    left: 50%;
    bottom: calc(14px + env(safe-area-inset-bottom));
    transform: translateX(-50%);
    z-index: 110;
    display: flex;
    align-items: stretch;
    gap: 2px;
    width: min(560px, calc(100vw - 24px));
    padding: 6px;
    border: 1px solid var(--theme-border);
    border-radius: 22px;
    background: var(--theme-bg-glass-strong);
    -webkit-backdrop-filter: blur(18px) saturate(160%);
    backdrop-filter: blur(18px) saturate(160%);
    box-shadow: var(--theme-shadow-lg);
    animation: mobile-tabbar-in 0.35s cubic-bezier(0.25, 0.8, 0.25, 1) both;
  }

  @keyframes mobile-tabbar-in {
    from {
      opacity: 0;
      transform: translate(-50%, 16px);
    }

    to {
      opacity: 1;
      transform: translate(-50%, 0);
    }
  }

  .mobile-tab {
    position: relative;
    flex: 1 1 0;
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 2px;
    min-width: 0;
    padding: 6px 2px 5px;
    border: none;
    border-radius: 16px;
    background: transparent;
    color: var(--theme-text-3);
    cursor: pointer;
    -webkit-tap-highlight-color: transparent;
    transition: color 0.2s ease;
  }

  .mobile-tab-icon {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    width: 38px;
    height: 26px;
    border-radius: 999px;
    transition: background 0.25s ease, color 0.25s ease, transform 0.2s ease;
  }

  .mobile-tab-icon svg {
    width: 20px;
    height: 20px;
  }

  .mobile-tab-label {
    max-width: 100%;
    font-size: 11px;
    font-weight: 500;
    line-height: 1.2;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }

  .mobile-tab.is-active {
    color: var(--theme-primary);
    font-weight: 600;
  }

  .mobile-tab.is-active .mobile-tab-icon {
    background: var(--theme-primary-soft);
    color: var(--theme-primary);
    transform: translateY(-1px);
  }

  /* 当前标签顶部渐变指示条 */
  .mobile-tab.is-active::before {
    content: '';
    position: absolute;
    top: -6px;
    left: 50%;
    transform: translateX(-50%);
    width: 16px;
    height: 3px;
    border-radius: 0 0 3px 3px;
    background: var(--theme-gradient);
  }

  .mobile-tab:active .mobile-tab-icon {
    transform: scale(0.92);
  }

  /* 「更多」浮层遮罩 */
  .mobile-sheet-mask {
    display: block;
    position: fixed;
    inset: 0;
    z-index: 130;
    background: var(--theme-overlay);
    -webkit-backdrop-filter: blur(2px);
    backdrop-filter: blur(2px);
  }

  /* 「更多」浮层：底部上滑面板，宫格展示其余入口 */
  .mobile-sheet {
    display: block;
    position: fixed;
    left: 0;
    right: 0;
    bottom: 0;
    z-index: 140;
    max-height: 76vh;
    padding: 6px 16px calc(20px + env(safe-area-inset-bottom));
    border-top: 1px solid var(--theme-border);
    border-radius: 24px 24px 0 0;
    background: var(--theme-bg-elevated);
    box-shadow: var(--theme-shadow-lg);
    overflow-y: auto;
    overscroll-behavior: contain;
  }

  .mobile-sheet-handle {
    display: block;
    width: 38px;
    height: 4px;
    margin: 8px auto 10px;
    border-radius: 999px;
    background: var(--theme-hairline);
  }

  .mobile-sheet-head {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 12px;
    padding: 0 2px 12px;
  }

  .mobile-sheet-title {
    font-size: 15px;
    font-weight: 700;
    color: var(--theme-text-1);
  }

  .mobile-sheet-close {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    width: 32px;
    height: 32px;
    border: 1px solid var(--theme-border);
    border-radius: 10px;
    background: var(--theme-bg-muted);
    color: var(--theme-text-2);
    cursor: pointer;
    transition: background 0.2s ease, color 0.2s ease;
  }

  .mobile-sheet-close svg {
    width: 16px;
    height: 16px;
  }

  .mobile-sheet-close:hover {
    background: var(--theme-bg-hover);
    color: var(--theme-text-1);
  }

  /* 自适应宫格：窄屏自动降为 3 列，避免文字挤压 */
  .mobile-sheet-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(76px, 1fr));
    gap: 8px;
  }

  .mobile-sheet-tile {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 6px;
    padding: 12px 4px 10px;
    border: 1px solid transparent;
    border-radius: 16px;
    background: var(--theme-bg-surface-2);
    color: var(--theme-text-2);
    font-size: 12px;
    font-weight: 500;
    cursor: pointer;
    -webkit-tap-highlight-color: transparent;
    transition: background 0.2s ease, color 0.2s ease, border-color 0.2s ease, transform 0.2s ease;
  }

  .mobile-sheet-tile:active {
    transform: scale(0.97);
  }

  .mobile-sheet-tile:hover,
  .mobile-sheet-tile.nav-item-active {
    border-color: var(--theme-primary);
    background: var(--theme-primary-soft);
    color: var(--theme-primary);
  }

  .mobile-sheet-iconbox {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    width: 36px;
    height: 36px;
    border-radius: 12px;
    background: var(--theme-bg-muted);
    color: var(--theme-primary);
    transition: background 0.2s ease, color 0.2s ease;
  }

  .mobile-sheet-iconbox svg {
    width: 19px;
    height: 19px;
  }

  .mobile-sheet-tile:hover .mobile-sheet-iconbox,
  .mobile-sheet-tile.nav-item-active .mobile-sheet-iconbox {
    background: var(--theme-primary);
    color: var(--theme-primary-text);
  }

  .mobile-sheet-label {
    width: 100%;
    text-align: center;
    line-height: 1.25;
  }

  /* 为底部悬浮导航预留空间，避免遮挡页面内容 */
  main {
    padding-bottom: calc(92px + env(safe-area-inset-bottom));
  }
}

/* 浮层动效：遮罩淡入 + 面板上滑 */
.mobile-sheet-fade-enter-active,
.mobile-sheet-fade-leave-active {
  transition: opacity 0.25s ease;
}

.mobile-sheet-fade-enter-from,
.mobile-sheet-fade-leave-to {
  opacity: 0;
}

.mobile-sheet-slide-enter-active {
  transition: transform 0.32s cubic-bezier(0.25, 0.8, 0.25, 1);
}

.mobile-sheet-slide-leave-active {
  transition: transform 0.24s ease-in;
}

.mobile-sheet-slide-enter-from,
.mobile-sheet-slide-leave-to {
  transform: translateY(100%);
}

/* ---- 手机（≤768px）：压缩顶部栏，操作区控件保留图标形态，避免右侧溢出 ---- */
@media (max-width: 768px) {
  /* 微信 / QQ / 微博 图标按钮 */
  .actions > .action-btn-iocn {
    display: none;
  }

  /* 顶部栏整体收紧 */
  .navbar-home {
    height: 58px;
    padding: 6px 12px;
    gap: 10px;
  }

  .logo {
    gap: 8px;
  }

  .logo img {
    width: 36px;
    height: 36px;
    border-radius: 10px;
  }

  .actions {
    flex: 0 1 auto;
    min-width: 0;
    gap: 6px;
  }

  /* 语言切换：只留地球图标的圆形按钮 */
  .actions :deep(.ls-trigger) {
    justify-content: center;
    width: 34px;
    height: 34px;
    padding: 0;
    gap: 0;
  }

  .actions :deep(.ls-label),
  .actions :deep(.ls-arrow) {
    display: none;
  }

  /* 主题切换：缩小分段与配色按钮，亮/暗/游戏三态仍可直接切换 */
  .actions :deep(.theme-switch),
  .actions :deep(.theme-switch__palette) {
    --ts-item: 24px;
    --ts-pad: 2px;
    --ts-icon: 14px;
  }

  /* 头像与公告铃铛同步缩小，保持同一视觉重量 */
  .profilepicture,
  .profilepicture_img {
    width: 36px;
    height: 36px;
  }

  /* 头像菜单右对齐，避免贴右侧时弹层溢出视口 */
  .profilepicture-inner {
    top: 48px;
    left: auto;
    right: 0;
    transform: none;
  }
}

/* ---- 小屏手机（≤576px）：隐藏品牌文字，登录/注册只留图标 ---- */
@media (max-width: 576px) {
  .logo span {
    display: none;
  }

  .logo img {
    width: 34px;
    height: 34px;
  }

  /* 未登录时登录/注册按钮压缩为图标按钮 */
  .actions .action-btn span {
    display: none;
  }

  .actions .action-btn {
    padding: 8px;
  }
}
</style>