<template>
  <transition name="settings-fade">
    <div v-if="visible" class="settings-overlay" @click.self="onClose">
      <div class="settings-card theme-transition">
        <!-- ============ 左侧分类导航 ============ -->
        <aside class="settings-nav">
          <div class="nav-title">设置</div>
          <nav class="nav-list">
            <button
              v-for="tab in TABS"
              :key="tab.id"
              :class="['nav-item', { active: activeTab === tab.id }]"
              @click="activeTab = tab.id"
            >
              <Icon :name="tab.icon" :size="'18px'" />
              <span class="nav-text">{{ tab.name }}</span>
            </button>
          </nav>
        </aside>

        <!-- ============ 右侧内容面板 ============ -->
        <section class="settings-content">
          <!-- ---------- 账号与存储 ---------- -->
          <template v-if="activeTab === 'account'">
            <div class="section-title">账号</div>

            <div class="set-row">
              <div class="account-info">
                <img class="account-avatar" :src="userAvatar || defaultAvatar" :alt="userName" />
                <div class="set-row-main">
                  <div class="set-row-title">{{ userName || '用户' }}</div>
                  <div class="set-row-sub">账号 {{ account || '-' }}</div>
                </div>
              </div>
              <button class="set-btn" @click="onLogout">退出登录</button>
            </div>

            <div class="set-row">
              <div class="set-row-main">
                <div class="set-row-title">登录方式</div>
                <div class="set-row-sub">在本机登录将无需手机确认。</div>
              </div>
              <div class="set-row-right">
                <label class="select-wrap">
                  <select v-model="settings.loginMode" class="set-select">
                    <option value="auto">自动</option>
                    <option value="confirm">每次确认</option>
                  </select>
                  <span class="select-chevron"><Icon name="chevron-right" :size="'14px'" /></span>
                </label>
                <button :class="['switch', { on: settings.autoLogin }]" @click="toggle('autoLogin')">
                  <span class="knob"></span>
                </button>
              </div>
            </div>

            <div class="set-row">
              <div class="set-row-main">
                <div class="set-row-title">保留聊天记录</div>
              </div>
              <button :class="['switch', { on: settings.keepChatHistory }]" @click="toggle('keepChatHistory')">
                <span class="knob"></span>
              </button>
            </div>

            <div class="section-title">存储</div>

            <div class="set-row">
              <div class="set-row-main">
                <div class="set-row-title">存储空间</div>
              </div>
              <button class="set-btn" @click="onDev('存储空间管理')">管理</button>
            </div>

            <div class="set-row">
              <div class="set-row-main">
                <div class="set-row-title">存储位置</div>
                <div class="set-row-sub">{{ storagePath }}</div>
              </div>
              <button class="set-btn" @click="changeStoragePath">更改</button>
            </div>

            <div class="set-row">
              <div class="set-row-main">
                <div class="set-row-title">
                  自动下载小于
                  <input v-model.number="settings.downloadSize" type="number" min="1" class="inline-input" />
                  MB 的文件
                </div>
              </div>
              <button :class="['switch', { on: settings.autoDownload }]" @click="toggle('autoDownload')">
                <span class="knob"></span>
              </button>
            </div>

            <div class="set-row">
              <div class="set-row-main">
                <div class="set-row-title">自动下载在其他设备查看过的内容</div>
                <div class="set-row-sub">内容包括「图片、视频和文件」</div>
              </div>
              <button :class="['switch', { on: settings.autoDownloadOther }]" @click="toggle('autoDownloadOther')">
                <span class="knob"></span>
              </button>
            </div>
          </template>

          <!-- ---------- 通用 ---------- -->
          <template v-else-if="activeTab === 'general'">
            <div class="section-title">外观</div>

            <div class="set-row">
              <div class="set-row-main">
                <div class="set-row-title">主题</div>
                <div class="set-row-sub">切换界面配色方案</div>
              </div>
              <div class="theme-chips">
                <ThemeSwitching />
              </div>
            </div>

            <div class="set-row">
              <div class="set-row-main">
                <div class="set-row-title">字体大小</div>
              </div>
              <label class="select-wrap">
                <select v-model="settings.fontSize" class="set-select">
                  <option value="small">小</option>
                  <option value="standard">标准</option>
                  <option value="large">大</option>
                </select>
                <span class="select-chevron"><Icon name="chevron-right" :size="'14px'" /></span>
              </label>
            </div>

            <div v-for="row in generalRows" :key="row.key" class="set-row">
              <div class="set-row-main">
                <div class="set-row-title">{{ row.title }}</div>
                <div v-if="row.sub" class="set-row-sub">{{ row.sub }}</div>
              </div>
              <button :class="['switch', { on: settings[row.key] }]" @click="toggle(row.key)">
                <span class="knob"></span>
              </button>
            </div>

            <div class="section-title">语言</div>

            <div class="set-row">
              <div class="set-row-main">
                <div class="set-row-title">界面语言</div>
              </div>
              <label class="select-wrap">
                <select v-model="settings.language" class="set-select">
                  <option value="zh-CN">简体中文</option>
                  <option value="en-US">English</option>
                </select>
                <span class="select-chevron"><Icon name="chevron-right" :size="'14px'" /></span>
              </label>
            </div>
          </template>

          <!-- ---------- 快捷键 ---------- -->
          <template v-else-if="activeTab === 'shortcuts'">
            <div class="section-title">常用快捷键</div>
            <div v-for="item in SHORTCUTS" :key="item.title" class="set-row">
              <div class="set-row-main">
                <div class="set-row-title">{{ item.title }}</div>
              </div>
              <span class="shortcut-keys">
                <kbd v-for="(key, i) in item.keys" :key="i" class="key">{{ key }}</kbd>
              </span>
            </div>
            <div class="panel-footer">
              <button class="set-btn" @click="onDev('恢复默认快捷键')">恢复默认</button>
            </div>
          </template>

          <!-- ---------- 通知 ---------- -->
          <template v-else-if="activeTab === 'notification'">
            <div class="section-title">消息通知</div>
            <div v-for="row in notificationRows" :key="row.key" class="set-row">
              <div class="set-row-main">
                <div class="set-row-title">{{ row.title }}</div>
                <div v-if="row.sub" class="set-row-sub">{{ row.sub }}</div>
              </div>
              <button :class="['switch', { on: settings[row.key] }]" @click="toggle(row.key)">
                <span class="knob"></span>
              </button>
            </div>
          </template>

          <!-- ---------- 插件 ---------- -->
          <template v-else-if="activeTab === 'plugins'">
            <div class="section-title">已安装插件</div>
            <div v-for="row in pluginRows" :key="row.key" class="set-row">
              <div class="set-row-main">
                <div class="set-row-title">{{ row.title }}</div>
                <div v-if="row.sub" class="set-row-sub">{{ row.sub }}</div>
              </div>
              <button :class="['switch', { on: settings[row.key] }]" @click="toggle(row.key)">
                <span class="knob"></span>
              </button>
            </div>
            <div class="panel-footer">
              <button class="set-btn" @click="onDev('插件市场')">前往插件市场</button>
            </div>
          </template>

          <!-- ---------- 关于 ---------- -->
          <template v-else-if="activeTab === 'about'">
            <div class="about-hero">
              <div class="about-logo">博</div>
              <div class="about-name">博览通讯</div>
              <div class="about-version">版本 {{ APP_VERSION }}</div>
            </div>

            <div class="set-row">
              <div class="set-row-main">
                <div class="set-row-title">检查更新</div>
                <div class="set-row-sub">当前已是最新版本</div>
              </div>
              <button class="set-btn" @click="onDev('检查更新')">检查更新</button>
            </div>
            <div class="set-row" @click="onDev('用户协议')">
              <div class="set-row-main">
                <div class="set-row-title">用户协议</div>
              </div>
              <Icon name="chevron-right" :size="'16px'" class="row-arrow" />
            </div>
            <div class="set-row" @click="onDev('隐私政策')">
              <div class="set-row-main">
                <div class="set-row-title">隐私政策</div>
              </div>
              <Icon name="chevron-right" :size="'16px'" class="row-arrow" />
            </div>
          </template>
        </section>
      </div>
    </div>
  </transition>
</template>

<script setup>
import { ref, reactive, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import ThemeSwitching from '@/components/ThemeComponents/ThemeSwitching.vue';
import { THEME_LIST, getCurrentTheme, setTheme } from '@/utils/theme'
import Icon from './Icon.vue'

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  userName: {
    type: String,
    default: ''
  },
  userAvatar: {
    type: String,
    default: ''
  },
  account: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['update:visible', 'logout'])

const defaultAvatar = '/src/assets/defaultimage/moren.webp'
const SETTINGS_KEY = 'im-settings'
const APP_VERSION = '1.0.0'

const TABS = [
  { id: 'account', name: '账号与存储', icon: 'user-circle' },
  { id: 'general', name: '通用', icon: 'settings' },
  { id: 'shortcuts', name: '快捷键', icon: 'keyboard' },
  { id: 'notification', name: '通知', icon: 'bell' },
  { id: 'plugins', name: '插件', icon: 'puzzle' },
  { id: 'about', name: '关于', icon: 'info' }
]

const SHORTCUTS = [
  { title: '发送消息', keys: ['Enter'] },
  { title: '消息内换行', keys: ['Shift', 'Enter'] },
  { title: '搜索会话', keys: ['Ctrl', 'F'] },
  { title: '关闭当前窗口', keys: ['Esc'] },
  { title: '切换下一个会话', keys: ['Ctrl', 'Tab'] }
]

const generalRows = [
  { key: 'compactMode', title: '紧凑模式', sub: '减小列表与消息间距，同屏展示更多内容' },
  { key: 'launchAtStartup', title: '开机自启动', sub: '开机后自动运行博览通讯' }
]

const notificationRows = [
  { key: 'newMsgNotify', title: '新消息提醒' },
  { key: 'soundNotify', title: '声音提醒' },
  { key: 'desktopNotify', title: '桌面通知', sub: '在系统通知中心显示消息提醒' },
  { key: 'msgPreview', title: '显示消息预览', sub: '在通知中展示消息内容' },
  { key: 'doNotDisturb', title: '免打扰', sub: '开启后仅保留未读红点，不弹出提醒' }
]

const pluginRows = [
  { key: 'emojiPlugin', title: '表情插件', sub: '提供更丰富的表情包' },
  { key: 'screenshotPlugin', title: '屏幕截图', sub: '快速截屏并发送' },
  { key: 'voiceTextPlugin', title: '语音转文字', sub: '将语音消息自动转为文字' }
]

const DEFAULT_SETTINGS = {
  autoLogin: true,
  keepChatHistory: true,
  autoDownload: true,
  autoDownloadOther: true,
  downloadSize: 20,
  loginMode: 'auto',
  compactMode: false,
  launchAtStartup: false,
  fontSize: 'standard',
  language: 'zh-CN',
  newMsgNotify: true,
  soundNotify: true,
  desktopNotify: false,
  msgPreview: true,
  doNotDisturb: false,
  emojiPlugin: true,
  screenshotPlugin: true,
  voiceTextPlugin: false
}

const loadSettings = () => {
  try {
    return { ...DEFAULT_SETTINGS, ...JSON.parse(localStorage.getItem(SETTINGS_KEY) || '{}') }
  } catch (error) {
    return { ...DEFAULT_SETTINGS }
  }
}

const settings = reactive(loadSettings())
const activeTab = ref('account')
const currentTheme = ref(getCurrentTheme())
const storagePath = ref('D:\\MinecraftChat\\files')

watch(
  settings,
  () => {
    try {
      localStorage.setItem(SETTINGS_KEY, JSON.stringify(settings))
    } catch (error) {
      // 忽略存储异常（隐私模式等）
    }
  },
  { deep: true }
)

watch(
  () => props.visible,
  (val) => {
    if (val) {
      activeTab.value = 'account'
      currentTheme.value = getCurrentTheme()
    }
  },
  { immediate: true }
)

const toggle = (key) => {
  settings[key] = !settings[key]
}

const changeTheme = (theme) => {
  currentTheme.value = setTheme(theme)
}

const changeStoragePath = () => {
  ElMessage.info('存储位置更改功能开发中')
}

const onLogout = () => {
  ElMessageBox.confirm('确定要退出登录吗？', '退出登录', {
    confirmButtonText: '退出',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(() => {
      emit('logout')
    })
    .catch(() => {})
}

const onDev = (name) => {
  ElMessage.info(`${name}功能开发中`)
}

const onClose = () => {
  emit('update:visible', false)
}
</script>

<style scoped>
.settings-overlay {
  position: fixed;
  inset: 0;
  z-index: 1200;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--theme-overlay);
  -webkit-backdrop-filter: blur(8px);
  backdrop-filter: blur(8px);
  font-family: Inter, 'PingFang SC', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
}

.settings-card {
  display: flex;
  width: 860px;
  max-width: calc(100vw - 32px);
  height: 620px;
  max-height: calc(100vh - 64px);
  background: var(--theme-bg-elevated);
  border: 1px solid var(--theme-border);
  border-radius: var(--theme-radius-lg);
  box-shadow: var(--theme-shadow-lg);
  overflow: hidden;
}

/* ============ 左侧导航 ============ */
.settings-nav {
  width: 208px;
  flex-shrink: 0;
  background: var(--theme-bg-surface-2);
  border-right: 1px solid var(--theme-border);
  padding: 18px 12px;
  display: flex;
  flex-direction: column;
  gap: 6px;
  box-sizing: border-box;
  overflow-y: auto;
}

.nav-title {
  padding: 4px 10px 12px;
  font-size: 15px;
  font-weight: 600;
  color: var(--theme-text-1);
  letter-spacing: -0.01em;
}

.nav-list {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 10px;
  width: 100%;
  padding: 10px 12px;
  border: none;
  border-radius: 10px;
  background: transparent;
  color: var(--theme-text-2);
  font-size: 14px;
  font-family: inherit;
  text-align: left;
  cursor: pointer;
  transition: background var(--theme-duration-fast) var(--theme-ease), color var(--theme-duration-fast) var(--theme-ease);
}

.nav-item:hover {
  background: var(--theme-bg-hover);
  color: var(--theme-text-1);
}

.nav-item.active {
  background: var(--theme-primary-soft);
  color: var(--theme-primary);
  font-weight: 500;
}

.nav-text {
  white-space: nowrap;
}

/* ============ 右侧内容 ============ */
.settings-content {
  flex: 1;
  min-width: 0;
  overflow-y: auto;
  padding-bottom: 16px;
}

.settings-content::-webkit-scrollbar {
  width: 6px;
}

.settings-content::-webkit-scrollbar-thumb {
  background: var(--theme-scrollbar-thumb);
  border-radius: 3px;
}

.section-title {
  padding: 20px 24px 8px;
  font-size: 13px;
  font-weight: 600;
  color: var(--theme-text-3);
  letter-spacing: 0.3px;
}

.set-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  padding: 14px 24px;
  border-bottom: 1px solid var(--theme-hairline);
}

.set-row:last-child {
  border-bottom: none;
}

.set-row-main {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 3px;
}

.set-row-title {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: var(--theme-text-1);
}

.set-row-sub {
  font-size: 12.5px;
  color: var(--theme-text-3);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.set-row-right {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-shrink: 0;
}

.row-arrow {
  color: var(--theme-text-3);
  flex-shrink: 0;
}

/* ---- 账号信息 ---- */
.account-info {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 1;
  min-width: 0;
}

.account-avatar {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  object-fit: cover;
  flex-shrink: 0;
  box-shadow: var(--theme-shadow-sm);
}

/* ---- 开关 ---- */
.switch {
  position: relative;
  width: 40px;
  height: 22px;
  padding: 0;
  border: none;
  border-radius: var(--theme-radius-pill);
  background: var(--theme-bg-active);
  cursor: pointer;
  flex-shrink: 0;
  transition: background var(--theme-duration-fast) var(--theme-ease);
}

.switch.on {
  background: var(--theme-primary);
}

.switch .knob {
  position: absolute;
  top: 2px;
  left: 2px;
  width: 18px;
  height: 18px;
  border-radius: 50%;
  background: #ffffff;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.28);
  transition: transform var(--theme-duration-fast) var(--theme-ease);
}

.switch.on .knob {
  transform: translateX(18px);
}

/* ---- 按钮 ---- */
.set-btn {
  padding: 7px 16px;
  border: 1px solid var(--theme-border);
  border-radius: 10px;
  background: var(--theme-bg-surface-2);
  color: var(--theme-text-1);
  font-size: 13px;
  font-family: inherit;
  cursor: pointer;
  flex-shrink: 0;
  white-space: nowrap;
  transition: background var(--theme-duration-fast) var(--theme-ease), border-color var(--theme-duration-fast) var(--theme-ease);
}

.set-btn:hover {
  background: var(--theme-bg-hover);
  border-color: var(--theme-border-strong);
}

/* ---- 下拉框 ---- */
.select-wrap {
  position: relative;
  display: inline-flex;
  align-items: center;
  flex-shrink: 0;
}

.set-select {
  height: 32px;
  padding: 0 30px 0 12px;
  border: 1px solid var(--theme-border);
  border-radius: 10px;
  background: var(--theme-bg-surface-2);
  color: var(--theme-text-1);
  font-size: 13px;
  font-family: inherit;
  cursor: pointer;
  outline: none;
  appearance: none;
  -webkit-appearance: none;
  transition: border-color var(--theme-duration-fast) var(--theme-ease);
}

.set-select:hover {
  border-color: var(--theme-border-strong);
}

.set-select:focus {
  border-color: var(--theme-primary);
}

.select-chevron {
  position: absolute;
  right: 8px;
  display: flex;
  align-items: center;
  color: var(--theme-text-3);
  transform: rotate(90deg);
  pointer-events: none;
}

/* ---- 行内数字输入 ---- */
.inline-input {
  width: 56px;
  height: 28px;
  padding: 0 8px;
  border: 1px solid var(--theme-border);
  border-radius: 8px;
  background: var(--theme-bg-surface-2);
  color: var(--theme-text-1);
  font-size: 13px;
  font-family: inherit;
  text-align: center;
  outline: none;
  transition: border-color var(--theme-duration-fast) var(--theme-ease);
}

.inline-input:focus {
  border-color: var(--theme-primary);
}

/* ---- 主题选择 ---- */
.theme-chips {
  display: flex;
  gap: 8px;
  flex-shrink: 0;
}

.theme-chip {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  padding: 6px 12px;
  border: 1px solid var(--theme-border);
  border-radius: var(--theme-radius-pill);
  background: var(--theme-bg-surface-2);
  color: var(--theme-text-2);
  font-size: 13px;
  font-family: inherit;
  cursor: pointer;
  transition: all var(--theme-duration-fast) var(--theme-ease);
}

.theme-chip:hover {
  background: var(--theme-bg-hover);
}

.theme-chip.active {
  background: var(--theme-primary-soft);
  border-color: color-mix(in srgb, var(--theme-primary) 34%, transparent);
  color: var(--theme-primary);
  font-weight: 500;
}

.theme-chip-icon {
  font-size: 13px;
  line-height: 1;
}

/* ---- 快捷键 ---- */
.shortcut-keys {
  display: flex;
  align-items: center;
  gap: 4px;
  flex-shrink: 0;
}

.key {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 26px;
  height: 26px;
  padding: 0 8px;
  border: 1px solid var(--theme-border);
  border-bottom-width: 2px;
  border-radius: 7px;
  background: var(--theme-bg-surface-2);
  color: var(--theme-text-2);
  font-size: 12px;
  font-family: inherit;
}

/* ---- 面板底部操作 ---- */
.panel-footer {
  display: flex;
  justify-content: flex-end;
  padding: 16px 24px;
}

/* ---- 关于 ---- */
.about-hero {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 34px 24px 22px;
  border-bottom: 1px solid var(--theme-hairline);
}

.about-logo {
  width: 62px;
  height: 62px;
  border-radius: 18px;
  background: var(--theme-gradient);
  color: var(--theme-primary-text);
  font-size: 28px;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: var(--theme-shadow-md);
}

.about-name {
  margin-top: 14px;
  font-size: 16px;
  font-weight: 600;
  color: var(--theme-text-1);
}

.about-version {
  margin-top: 4px;
  font-size: 12.5px;
  color: var(--theme-text-3);
}

/* ---- 过渡 ---- */
.settings-fade-enter-active,
.settings-fade-leave-active {
  transition: opacity var(--theme-duration) var(--theme-ease);
}

.settings-fade-enter-active .settings-card,
.settings-fade-leave-active .settings-card {
  transition: transform var(--theme-duration) var(--theme-ease);
}

.settings-fade-enter-from,
.settings-fade-leave-to {
  opacity: 0;
}

.settings-fade-enter-from .settings-card,
.settings-fade-leave-to .settings-card {
  transform: translateY(12px) scale(0.98);
}

/* ---- 移动端 ---- */
@media (max-width: 640px) {
  .settings-card {
    flex-direction: column;
    width: calc(100vw - 24px);
    height: calc(100vh - 48px);
  }

  .settings-nav {
    width: 100%;
    flex-direction: row;
    align-items: center;
    gap: 8px;
    padding: 10px 12px;
    border-right: none;
    border-bottom: 1px solid var(--theme-border);
    overflow-x: auto;
  }

  .nav-title {
    display: none;
  }

  .nav-list {
    flex-direction: row;
    gap: 6px;
  }

  .nav-item {
    width: auto;
    white-space: nowrap;
  }

  .set-row {
    padding: 13px 16px;
  }

  .section-title {
    padding: 16px 16px 6px;
  }
}
</style>
