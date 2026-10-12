<template>
    <div class="accountsettings-container">
        <!-- 环境光背景 -->
        <div class="accountsettings-aurora" aria-hidden="true">
            <span class="accountsettings-blob accountsettings-blob--1"></span>
            <span class="accountsettings-blob accountsettings-blob--2"></span>
            <span class="accountsettings-grid"></span>
        </div>

        <!-- 主内容区域 -->
        <div class="accountsettings-content">
            <header class="accountsettings-header">
                <span class="accountsettings-header-icon">
                    <i class="fas fa-sliders-h"></i>
                </span>
                <div>
                    <h1 class="accountsettings-title">账户设置</h1>
                    <p class="accountsettings-subtitle">管理你的账号资料与通知偏好</p>
                </div>
            </header>

            <!-- 头像上传区域 -->
            <div class="accountsettings-avatar-container">
                <div class="accountsettings-avatar-wrapper" role="button" tabindex="0" aria-label="上传照片"
                    @click="triggerFileInput" @keydown.enter="triggerFileInput">
                    <img v-if="avatarPreview" :src="avatarPreview" class="accountsettings-avatar" alt="用户头像" />
                    <div v-else class="accountsettings-avatar-placeholder">
                        <i class="fas fa-user"></i>
                    </div>
                    <div class="accountsettings-avatar-hover">
                        <i class="fas fa-camera"></i>
                        <span>上传照片</span>
                    </div>
                </div>
                <input type="file" ref="fileInput" @change="handleAvatarUpload" accept="image/*"
                    style="display: none;" />
                <p class="accountsettings-avatar-tip">支持 JPG / PNG，建议 1:1 正方形图片</p>
            </div>

            <!-- 表单区域 -->
            <form class="accountsettings-form" @submit.prevent="saveChanges">
                <!-- 用户名 -->
                <div class="accountsettings-form-group">
                    <label class="accountsettings-label">用户名</label>
                    <div class="accountsettings-input-container">
                        <i class="fas fa-user accountsettings-input-icon"></i>
                        <input type="text" v-model="username" class="accountsettings-input" placeholder="输入用户名" />
                    </div>
                </div>

                <!-- 邮箱 -->
                <div class="accountsettings-form-group">
                    <label class="accountsettings-label">电子邮件地址</label>
                    <div class="accountsettings-input-container">
                        <i class="fas fa-envelope accountsettings-input-icon"></i>
                        <input type="email" v-model="email" class="accountsettings-input" placeholder="输入电子邮件" />
                    </div>
                </div>

                <!-- 密码 -->
                <div class="accountsettings-form-group">
                    <label class="accountsettings-label">密码</label>
                    <div class="accountsettings-input-container">
                        <i class="fas fa-lock accountsettings-input-icon"></i>
                        <input type="password" v-model="password" class="accountsettings-input" placeholder="输入新密码" />
                    </div>
                </div>

                <!-- 通知偏好 -->
                <div class="accountsettings-form-group">
                    <label class="accountsettings-label">通知偏好</label>
                    <div class="accountsettings-toggle-group">
                        <div v-for="(pref, index) in notificationPrefs" :key="index"
                            class="accountsettings-toggle-item">
                            <span class="accountsettings-toggle-label">{{ pref.label }}</span>
                            <label class="accountsettings-switch">
                                <input type="checkbox" v-model="pref.value" @change="toggleNotification(pref)" />
                                <span class="accountsettings-slider"></span>
                            </label>
                        </div>
                    </div>
                </div>

                <!-- 按钮区域 -->
                <div class="accountsettings-button-group">
                    <button type="button" class="accountsettings-button accountsettings-button-cancel"
                        @click="cancelChanges">
                        取消
                    </button>
                    <button type="submit" class="accountsettings-button accountsettings-button-save">
                        保存更改
                    </button>
                </div>
            </form>
        </div>
    </div>
</template>

<script setup>
import { ref } from 'vue';

// 用户数据
const username = ref('John Doe');
const email = ref('john.doe@example.com');
const password = ref('');
const avatarPreview = ref(null);

// 通知偏好设置
const notificationPrefs = ref([
    { label: '电子邮件通知', value: true },
    { label: '推送通知', value: true },
    { label: '短信通知', value: false }
]);

// 文件输入引用
const fileInput = ref(null);

// 触发文件选择
const triggerFileInput = () => {
    fileInput.value.click();
};

// 处理头像上传
const handleAvatarUpload = (event) => {
    const file = event.target.files[0];
    if (file) {
        const reader = new FileReader();
        reader.onload = (e) => {
            avatarPreview.value = e.target.result;
        };
        reader.readAsDataURL(file);
    }
};

// 切换通知偏好
const toggleNotification = (pref) => {
    console.log(`${pref.label} 状态已更改为: ${pref.value}`);
};

// 保存更改
const saveChanges = () => {
    console.log('保存更改:', {
        username: username.value,
        email: email.value,
        password: password.value,
        notifications: notificationPrefs.value
    });
    // 这里可以添加API调用
};

// 取消更改
const cancelChanges = () => {
    console.log('取消更改');
    // 这里可以重置表单
};
</script>

<style scoped>
/* ========== 页面外壳 ========== */
.accountsettings-container {
    position: relative;
    display: flex;
    justify-content: center;
    align-items: center;
    min-height: 100vh;
    padding: 40px 24px;
    background: var(--theme-bg-app);
    color: var(--theme-text-1);
    overflow: hidden;
}

.accountsettings-aurora {
    position: absolute;
    inset: 0;
    z-index: 0;
    pointer-events: none;
}

.accountsettings-blob {
    position: absolute;
    border-radius: 50%;
    filter: blur(80px);
    opacity: 0.55;
}

.accountsettings-blob--1 {
    width: 420px;
    height: 420px;
    top: -160px;
    left: -120px;
    background: var(--theme-primary-soft);
}

.accountsettings-blob--2 {
    width: 360px;
    height: 360px;
    bottom: -160px;
    right: -120px;
    background: color-mix(in srgb, var(--theme-accent-2) 22%, transparent);
}

.accountsettings-grid {
    position: absolute;
    inset: 0;
    background-image:
        linear-gradient(to right, var(--theme-hairline) 1px, transparent 1px),
        linear-gradient(to bottom, var(--theme-hairline) 1px, transparent 1px);
    background-size: 58px 58px;
    -webkit-mask-image: radial-gradient(ellipse 78% 58% at 50% 0%, #000 18%, transparent 76%);
    mask-image: radial-gradient(ellipse 78% 58% at 50% 0%, #000 18%, transparent 76%);
    opacity: 0.6;
}

/* ========== 主卡片 ========== */
.accountsettings-content {
    position: relative;
    z-index: 1;
    width: 100%;
    max-width: 480px;
    padding: 30px 32px 32px;
    border-radius: var(--theme-radius-xl);
    background: var(--theme-bg-surface);
    border: 1px solid var(--theme-border);
    box-shadow: var(--theme-shadow-lg);
}

.accountsettings-header {
    display: flex;
    align-items: center;
    gap: 14px;
    padding-bottom: 22px;
    border-bottom: 1px solid var(--theme-hairline);
}

.accountsettings-header-icon {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    flex: 0 0 auto;
    width: 44px;
    height: 44px;
    border-radius: var(--theme-radius-md);
    background: var(--theme-primary-soft);
    color: var(--theme-primary);
    font-size: 18px;
}

.accountsettings-title {
    margin: 0;
    font-size: 22px;
    font-weight: 700;
    letter-spacing: -0.01em;
    color: var(--theme-text-1);
}

.accountsettings-subtitle {
    margin: 3px 0 0;
    font-size: 13px;
    color: var(--theme-text-3);
}

/* ========== 头像区域 ========== */
.accountsettings-avatar-container {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 10px;
    margin: 26px 0 28px;
}

.accountsettings-avatar-wrapper {
    position: relative;
    width: 104px;
    height: 104px;
    border-radius: 28px;
    border: 2px dashed var(--theme-border-strong);
    background: var(--theme-bg-surface-2);
    cursor: pointer;
    overflow: hidden;
    transition: transform var(--theme-duration) var(--theme-ease),
        border-color var(--theme-duration) var(--theme-ease);
}

.accountsettings-avatar-wrapper:hover {
    transform: translateY(-3px);
    border-color: var(--theme-primary);
    border-style: solid;
}

.accountsettings-avatar-wrapper:focus-visible {
    outline: none;
    box-shadow: var(--theme-ring);
}

.accountsettings-avatar {
    display: block;
    width: 100%;
    height: 100%;
    object-fit: cover;
}

.accountsettings-avatar-placeholder {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 100%;
    height: 100%;
    font-size: 40px;
    color: var(--theme-text-3);
}

.accountsettings-avatar-hover {
    position: absolute;
    inset: 0;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: 5px;
    background: var(--theme-overlay);
    color: #fff;
    font-size: 20px;
    opacity: 0;
    transition: opacity var(--theme-duration) var(--theme-ease);
}

.accountsettings-avatar-wrapper:hover .accountsettings-avatar-hover {
    opacity: 1;
}

.accountsettings-avatar-hover span {
    font-size: 12px;
    letter-spacing: 0.04em;
}

.accountsettings-avatar-tip {
    margin: 0;
    font-size: 12px;
    color: var(--theme-text-3);
}

/* ========== 表单样式 ========== */
.accountsettings-form-group {
    margin-bottom: 18px;
}

.accountsettings-label {
    display: block;
    margin-bottom: 8px;
    font-size: 13px;
    font-weight: 500;
    color: var(--theme-text-2);
}

.accountsettings-input-container {
    position: relative;
    display: flex;
    align-items: center;
}

.accountsettings-input-icon {
    position: absolute;
    left: 15px;
    font-size: 15px;
    color: var(--theme-text-3);
    pointer-events: none;
    transition: color var(--theme-duration) var(--theme-ease);
}

.accountsettings-input-container:focus-within .accountsettings-input-icon {
    color: var(--theme-primary);
}

.accountsettings-input {
    width: 100%;
    box-sizing: border-box;
    padding: 12px 16px 12px 42px;
    border: 1.5px solid var(--theme-border);
    border-radius: var(--theme-radius-md);
    background: var(--theme-bg-surface-2);
    color: var(--theme-text-1);
    font: inherit;
    font-size: 14px;
    transition: border-color var(--theme-duration) var(--theme-ease),
        box-shadow var(--theme-duration) var(--theme-ease),
        background-color var(--theme-duration) var(--theme-ease);
}

.accountsettings-input::placeholder {
    color: var(--theme-text-3);
}

.accountsettings-input:hover {
    border-color: var(--theme-border-strong);
}

.accountsettings-input:focus {
    outline: none;
    border-color: var(--theme-primary);
    background: var(--theme-bg-surface);
    box-shadow: var(--theme-ring);
}

/* ========== 通知偏好 ========== */
.accountsettings-toggle-group {
    padding: 4px 16px;
    border-radius: var(--theme-radius-md);
    background: var(--theme-bg-surface-2);
    border: 1px solid var(--theme-border);
}

.accountsettings-toggle-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    gap: 16px;
    padding: 13px 0;
    border-bottom: 1px solid var(--theme-hairline);
}

.accountsettings-toggle-item:last-child {
    border-bottom: none;
}

.accountsettings-toggle-label {
    font-size: 14px;
    color: var(--theme-text-1);
}

/* 开关 */
.accountsettings-switch {
    position: relative;
    display: inline-block;
    flex: 0 0 auto;
    width: 48px;
    height: 26px;
}

.accountsettings-switch input {
    opacity: 0;
    width: 0;
    height: 0;
}

.accountsettings-slider {
    position: absolute;
    inset: 0;
    border-radius: var(--theme-radius-pill);
    background: var(--theme-bg-active);
    cursor: pointer;
    transition: background-color var(--theme-duration) var(--theme-ease);
}

.accountsettings-slider::before {
    content: "";
    position: absolute;
    top: 3px;
    left: 3px;
    width: 20px;
    height: 20px;
    border-radius: 50%;
    background: var(--theme-bg-elevated);
    box-shadow: var(--theme-shadow-sm);
    transition: transform var(--theme-duration) var(--theme-ease);
}

input:checked + .accountsettings-slider {
    background: var(--theme-primary);
}

input:checked + .accountsettings-slider::before {
    transform: translateX(22px);
}

input:focus-visible + .accountsettings-slider {
    box-shadow: var(--theme-ring);
}

/* ========== 按钮样式 ========== */
.accountsettings-button-group {
    display: flex;
    gap: 12px;
    margin-top: 26px;
}

.accountsettings-button {
    flex: 1;
    padding: 13px 20px;
    border: 1px solid transparent;
    border-radius: var(--theme-radius-pill);
    font: inherit;
    font-size: 14px;
    font-weight: 600;
    cursor: pointer;
    transition: background-color var(--theme-duration) var(--theme-ease),
        border-color var(--theme-duration) var(--theme-ease),
        transform var(--theme-duration) var(--theme-ease),
        box-shadow var(--theme-duration) var(--theme-ease);
}

.accountsettings-button:focus-visible {
    outline: none;
    box-shadow: var(--theme-ring);
}

.accountsettings-button:active {
    transform: translateY(1px);
}

.accountsettings-button-cancel {
    background: var(--theme-bg-surface-2);
    border-color: var(--theme-border);
    color: var(--theme-text-1);
}

.accountsettings-button-cancel:hover {
    background: var(--theme-bg-hover);
    border-color: var(--theme-border-strong);
}

.accountsettings-button-save {
    background: var(--theme-gradient);
    color: var(--theme-primary-text);
    box-shadow: var(--theme-shadow-sm);
}

.accountsettings-button-save:hover {
    transform: translateY(-1px);
    filter: brightness(1.06);
    box-shadow: var(--theme-shadow-md);
}

/* ========== 响应式 ========== */
@media (max-width: 520px) {
    .accountsettings-container {
        padding: 16px 14px;
        align-items: flex-start;
    }

    .accountsettings-content {
        padding: 24px 20px 26px;
    }

    .accountsettings-title {
        font-size: 19px;
    }

    .accountsettings-avatar-container {
        margin: 20px 0 22px;
    }
}
</style>
