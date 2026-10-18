<template>
    <div class="accountsettings-container">
        <!-- 环境光背景 -->
        <div class="accountsettings-aurora" aria-hidden="true">
            <span class="accountsettings-blob accountsettings-blob--1"></span>
            <span class="accountsettings-blob accountsettings-blob--2"></span>
            <span class="accountsettings-grid"></span>
        </div>

        <!-- 主内容区域 -->
        <div class="accountsettings-content" v-loading="loading" :element-loading-text="$t('common.loading')">
            <header class="accountsettings-header">
                <span class="accountsettings-header-icon">
                    <i class="fas fa-sliders-h"></i>
                </span>
                <div>
                    <h1 class="accountsettings-title">{{ $t('accountSettings.title') }}</h1>
                    <p class="accountsettings-subtitle">{{ $t('accountSettings.subtitle') }}</p>
                </div>
            </header>

            <!-- 头像上传区域 -->
            <div class="accountsettings-avatar-container">
                <div class="accountsettings-avatar-wrapper" role="button" tabindex="0"
                    :aria-label="$t('accountSettings.avatarUpload')" @click="triggerFileInput"
                    @keydown.enter="triggerFileInput">
                    <img v-if="avatarPreview" :src="avatarPreview" class="accountsettings-avatar"
                        :alt="$t('accountSettings.avatarUpload')" />
                    <div v-else class="accountsettings-avatar-placeholder">
                        <i class="fas fa-user"></i>
                    </div>
                    <div class="accountsettings-avatar-hover">
                        <i class="fas fa-camera"></i>
                        <span>{{ $t('accountSettings.avatarUpload') }}</span>
                    </div>
                </div>
                <input type="file" ref="fileInput" @change="handleAvatarUpload" accept="image/jpeg,image/png"
                    style="display: none;" />
                <p class="accountsettings-avatar-tip">{{ $t('accountSettings.avatarTip') }}</p>
            </div>

            <!-- 表单区域 -->
            <form class="accountsettings-form" novalidate @submit.prevent="saveChanges">
                <!-- 用户名 -->
                <div class="accountsettings-form-group">
                    <label class="accountsettings-label">{{ $t('accountSettings.fields.username') }}</label>
                    <div class="accountsettings-input-container">
                        <i class="fas fa-user accountsettings-input-icon"></i>
                        <input type="text" v-model="username" class="accountsettings-input"
                            :placeholder="$t('accountSettings.fields.usernamePlaceholder')" autocomplete="username" />
                    </div>
                </div>

                <!-- 邮箱 -->
                <div class="accountsettings-form-group">
                    <label class="accountsettings-label">{{ $t('accountSettings.fields.email') }}</label>
                    <div class="accountsettings-input-container">
                        <i class="fas fa-envelope accountsettings-input-icon"></i>
                        <input type="email" v-model="email" class="accountsettings-input"
                            :placeholder="$t('accountSettings.fields.emailPlaceholder')" autocomplete="email" />
                    </div>
                </div>

                <!-- 密码 -->
                <div class="accountsettings-form-group">
                    <label class="accountsettings-label">{{ $t('accountSettings.fields.currentPassword') }}</label>
                    <div class="accountsettings-input-container">
                        <i class="fas fa-lock accountsettings-input-icon"></i>
                        <input type="password" v-model="currentPassword" class="accountsettings-input"
                            :placeholder="$t('accountSettings.fields.currentPasswordPlaceholder')"
                            autocomplete="current-password" />
                    </div>
                </div>
                <div class="accountsettings-form-group">
                    <label class="accountsettings-label">{{ $t('accountSettings.fields.newPassword') }}</label>
                    <div class="accountsettings-input-container">
                        <i class="fas fa-key accountsettings-input-icon"></i>
                        <input type="password" v-model="newPassword" class="accountsettings-input"
                            :placeholder="$t('accountSettings.fields.newPasswordPlaceholder')"
                            autocomplete="new-password" />
                    </div>
                </div>

                <!-- 通知偏好 -->
                <div class="accountsettings-form-group">
                    <label class="accountsettings-label">{{ $t('accountSettings.notifications.title') }}</label>
                    <div class="accountsettings-toggle-group">
                        <div v-for="pref in notificationPrefs" :key="pref.key" class="accountsettings-toggle-item">
                            <span class="accountsettings-toggle-label">
                                {{ $t(`accountSettings.notifications.${pref.key}`) }}
                            </span>
                            <label class="accountsettings-switch">
                                <input type="checkbox" v-model="pref.value" />
                                <span class="accountsettings-slider"></span>
                            </label>
                        </div>
                    </div>
                </div>

                <!-- 按钮区域 -->
                <div class="accountsettings-button-group">
                    <button type="button" class="accountsettings-button accountsettings-button-cancel"
                        :disabled="saving" @click="cancelChanges">
                        {{ $t('accountSettings.actions.cancel') }}
                    </button>
                    <button type="submit" class="accountsettings-button accountsettings-button-save"
                        :disabled="saving || loading">
                        {{ saving ? $t('accountSettings.actions.saving') : $t('accountSettings.actions.save') }}
                    </button>
                </div>
            </form>
        </div>
    </div>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { useI18n } from 'vue-i18n';
import { getUserInfo, updateUserInfo, updatePassword, uploadAvatar } from '@/api/user';

const { t } = useI18n();

// 通知偏好本地存储键（后端暂未提供通知偏好接口，先持久化在本地）
const NOTIFICATION_STORAGE_KEY = 'account-settings:notifications';

// 用户数据
const userId = ref(null);
const username = ref('');
const email = ref('');
const currentPassword = ref('');
const newPassword = ref('');
const avatarPreview = ref('');
// 已保存的用户名 / 邮箱，用于取消时回滚
const savedProfile = ref({ username: '', email: '' });

// 通知偏好设置
const notificationPrefs = ref([
    { key: 'email', value: true },
    { key: 'push', value: true },
    { key: 'sms', value: false }
]);

// 加载 / 保存状态
const loading = ref(false);
const saving = ref(false);

// 文件输入引用
const fileInput = ref(null);

const EMAIL_REGEX = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

// 同步本地用户缓存，保证导航栏等处的用户信息及时更新
const syncLocalUser = (patch) => {
    try {
        const cached = JSON.parse(localStorage.getItem('user') || 'null') || {};
        localStorage.setItem('user', JSON.stringify({ ...cached, ...patch }));
    } catch {
        // 本地缓存异常时忽略
    }
};

// 读取本地通知偏好
const loadNotificationPrefs = () => {
    try {
        const saved = JSON.parse(localStorage.getItem(NOTIFICATION_STORAGE_KEY) || 'null');
        if (!saved) return;
        notificationPrefs.value.forEach((pref) => {
            if (typeof saved[pref.key] === 'boolean') pref.value = saved[pref.key];
        });
    } catch {
        // 本地数据损坏时忽略，使用默认值
    }
};

// 持久化通知偏好
const persistNotificationPrefs = () => {
    const value = {};
    notificationPrefs.value.forEach((pref) => {
        value[pref.key] = pref.value;
    });
    localStorage.setItem(NOTIFICATION_STORAGE_KEY, JSON.stringify(value));
};

// 开关变化时自动保存到本地
watch(notificationPrefs, persistNotificationPrefs, { deep: true });

// 获取用户信息
const fetchUserInfo = async () => {
    loading.value = true;
    try {
        const response = await getUserInfo();
        if (response.code === 200 && response.data) {
            const data = response.data;
            userId.value = data.id;
            username.value = data.username || '';
            email.value = data.email || '';
            avatarPreview.value = data.avatar || '';
            savedProfile.value = { username: username.value, email: email.value };
        } else {
            ElMessage.error(response.message || t('accountSettings.messages.loadFailed'));
        }
    } catch (error) {
        console.error('获取用户信息失败:', error);
    } finally {
        loading.value = false;
    }
};

// 触发文件选择
const triggerFileInput = () => {
    fileInput.value.click();
};

// 处理头像上传
const handleAvatarUpload = async (event) => {
    const file = event.target.files && event.target.files[0];
    event.target.value = '';
    if (!file) return;

    // 验证文件类型
    const validTypes = ['image/jpeg', 'image/png'];
    if (!validTypes.includes(file.type)) {
        ElMessage.error(t('accountSettings.messages.avatarType'));
        return;
    }

    // 验证文件大小 (2MB)
    if (file.size > 2 * 1024 * 1024) {
        ElMessage.error(t('accountSettings.messages.avatarSize'));
        return;
    }

    loading.value = true;
    try {
        const formData = new FormData();
        formData.append('avatar', file);
        const response = await uploadAvatar(formData);
        if (response.code === 200) {
            avatarPreview.value = response.data;
            syncLocalUser({ avatar: response.data });
            ElMessage.success(t('accountSettings.messages.avatarSuccess'));
        } else {
            ElMessage.error(response.message || t('accountSettings.messages.avatarFailed'));
        }
    } catch (error) {
        console.error('头像上传失败:', error);
    } finally {
        loading.value = false;
    }
};

// 保存更改
const saveChanges = async () => {
    const nextUsername = username.value.trim();
    const nextEmail = email.value.trim();

    if (!nextUsername) {
        ElMessage.warning(t('accountSettings.messages.usernameRequired'));
        return;
    }
    if (!nextEmail) {
        ElMessage.warning(t('accountSettings.messages.emailRequired'));
        return;
    }
    if (!EMAIL_REGEX.test(nextEmail)) {
        ElMessage.warning(t('accountSettings.messages.emailInvalid'));
        return;
    }
    if (newPassword.value && !currentPassword.value) {
        ElMessage.warning(t('accountSettings.messages.currentPasswordRequired'));
        return;
    }

    const profileChanged =
        nextUsername !== savedProfile.value.username || nextEmail !== savedProfile.value.email;
    if (!profileChanged && !newPassword.value) {
        ElMessage.info(t('accountSettings.messages.noChanges'));
        return;
    }

    saving.value = true;
    try {
        if (profileChanged) {
            const response = await updateUserInfo({
                id: userId.value,
                username: nextUsername,
                email: nextEmail
            });
            if (response.code !== 200) {
                ElMessage.error(response.message || t('accountSettings.messages.saveFailed'));
                return;
            }
            savedProfile.value = { username: nextUsername, email: nextEmail };
            syncLocalUser({ username: nextUsername, email: nextEmail });
            ElMessage.success(t('accountSettings.messages.saveSuccess'));
        }

        if (newPassword.value) {
            const response = await updatePassword(currentPassword.value, newPassword.value);
            if (response.code !== 200) {
                ElMessage.error(response.message || t('accountSettings.messages.passwordFailed'));
                return;
            }
            currentPassword.value = '';
            newPassword.value = '';
            ElMessage.success(t('accountSettings.messages.passwordSuccess'));
        }

        // 重新拉取最新用户信息，重置表单基线
        await fetchUserInfo();
    } catch (error) {
        console.error('保存账户设置失败:', error);
    } finally {
        saving.value = false;
    }
};

// 取消更改
const cancelChanges = () => {
    username.value = savedProfile.value.username;
    email.value = savedProfile.value.email;
    currentPassword.value = '';
    newPassword.value = '';
    loadNotificationPrefs();
};

onMounted(() => {
    loadNotificationPrefs();
    fetchUserInfo();
});
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

.accountsettings-button:disabled {
    opacity: 0.6;
    cursor: not-allowed;
    transform: none;
}

.accountsettings-button-cancel {
    background: var(--theme-bg-surface-2);
    border-color: var(--theme-border);
    color: var(--theme-text-1);
}

.accountsettings-button-cancel:hover:not(:disabled) {
    background: var(--theme-bg-hover);
    border-color: var(--theme-border-strong);
}

.accountsettings-button-save {
    background: var(--theme-gradient);
    color: var(--theme-primary-text);
    box-shadow: var(--theme-shadow-sm);
}

.accountsettings-button-save:hover:not(:disabled) {
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
