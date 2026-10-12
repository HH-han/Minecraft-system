<template>
  <transition name="profile-fade">
    <div v-if="visible" class="profile-overlay" @click.self="onClose">
      <div class="profile-card theme-transition">
        <div class="profile-header">
          <button class="icon-btn" title="返回" @click="onClose">
            <Icon name="arrow-left" :size="'18px'" />
          </button>
          <h3 class="profile-title">个人信息</h3>
          <button v-if="!editing" class="text-btn" @click="startEdit">编辑</button>
          <span v-else class="header-spacer"></span>
        </div>

        <div class="profile-body">
          <div class="profile-hero">
            <div
              :class="['hero-avatar', { editable: editing }]"
              @click="editing && triggerAvatarPick()"
            >
              <img :src="form.avatar || defaultAvatar" :alt="form.username" />
              <span v-if="editing" class="hero-avatar-mask">
                <Icon name="camera" :size="'20px'" />
              </span>
            </div>
            <input
              ref="avatarInputRef"
              type="file"
              accept="image/*"
              class="hidden-input"
              @change="onAvatarChange"
            />
            <h2 class="hero-name">{{ form.nickname || form.username || '用户' }}</h2>
            <p class="hero-sub">{{ form.signature || '这个人很懒，什么都没有留下' }}</p>
            <div class="hero-chips">
              <span class="hero-chip">账号 {{ form.account || '-' }}</span>
              <span class="hero-chip hero-chip--accent">积分 {{ form.points ?? 0 }}</span>
            </div>
          </div>

          <!-- ============ 查看态 ============ -->
          <template v-if="!editing">
            <div class="info-card">
              <div v-for="item in basicItems" :key="item.label" class="info-row">
                <span class="info-label">{{ item.label }}</span>
                <span class="info-value">{{ item.value || '-' }}</span>
              </div>
            </div>

            <div class="info-card">
              <div v-for="item in longItems" :key="item.label" class="info-block">
                <span class="info-label">{{ item.label }}</span>
                <p class="info-text">{{ item.value || '暂无' }}</p>
              </div>
            </div>
          </template>

          <!-- ============ 编辑态 ============ -->
          <template v-else>
            <div class="form-card">
              <div class="form-row">
                <label class="form-label">昵称</label>
                <input v-model="editForm.nickname" class="form-input" placeholder="请输入昵称" />
              </div>
              <div class="form-row">
                <label class="form-label">性别</label>
                <select v-model="editForm.gender" class="form-input">
                  <option value="1">男</option>
                  <option value="2">女</option>
                  <option value="0">保密</option>
                </select>
              </div>
              <div class="form-row">
                <label class="form-label">年龄</label>
                <input v-model.number="editForm.age" type="number" min="0" max="150" class="form-input" placeholder="请输入年龄" />
              </div>
              <div class="form-row">
                <label class="form-label">手机号</label>
                <input v-model="editForm.phone" class="form-input" placeholder="请输入手机号" />
              </div>
              <div class="form-row">
                <label class="form-label">邮箱</label>
                <input v-model="editForm.email" class="form-input" placeholder="请输入邮箱" />
              </div>
              <div class="form-row">
                <label class="form-label">职业</label>
                <input v-model="editForm.occupation" class="form-input" placeholder="请输入职业" />
              </div>
              <div class="form-row">
                <label class="form-label">个性签名</label>
                <input v-model="editForm.signature" class="form-input" placeholder="请输入个性签名" />
              </div>
              <div class="form-row">
                <label class="form-label">爱好</label>
                <input v-model="editForm.hobbies" class="form-input" placeholder="请输入爱好" />
              </div>
              <div class="form-row form-row--block">
                <label class="form-label">个人简介</label>
                <textarea v-model="editForm.bio" class="form-textarea" rows="3" placeholder="介绍一下自己吧"></textarea>
              </div>
              <div class="form-row form-row--block">
                <label class="form-label">工作经验</label>
                <textarea v-model="editForm.experience" class="form-textarea" rows="3" placeholder="填写你的工作经验"></textarea>
              </div>
            </div>
          </template>
        </div>

        <div v-if="editing" class="profile-footer">
          <button class="btn btn-secondary" @click="cancelEdit">取消</button>
          <button class="btn btn-primary" :disabled="saving" @click="save">
            {{ saving ? '保存中...' : '保存' }}
          </button>
        </div>
      </div>
    </div>
  </transition>
</template>

<script setup>
import { ref, reactive, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { getUserInfo as getUserInfoApi, updateUserInfo, uploadAvatar } from '@/api/user'
import Icon from './Icon.vue'

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  userInfo: {
    type: Object,
    default: null
  }
})

const emit = defineEmits(['update:visible', 'updated'])

const defaultAvatar = '/src/assets/defaultimage/moren.webp'
const EDITABLE_FIELDS = ['nickname', 'gender', 'age', 'phone', 'email', 'occupation', 'signature', 'hobbies', 'bio', 'experience']

const editing = ref(false)
const saving = ref(false)
const avatarInputRef = ref(null)
const form = ref({})
const editForm = reactive({})

const genderText = (gender) => {
  if (gender === '1' || gender === 1) return '男'
  if (gender === '2' || gender === 2) return '女'
  return '保密'
}

const formatTime = (timeStr) => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  if (Number.isNaN(date.getTime())) return timeStr
  const pad = (n) => String(n).padStart(2, '0')
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())} ${pad(date.getHours())}:${pad(date.getMinutes())}`
}

const basicItems = computed(() => [
  { label: '昵称', value: form.value.nickname },
  { label: '账号', value: form.value.account },
  { label: '性别', value: genderText(form.value.gender) },
  { label: '年龄', value: form.value.age },
  { label: '手机号', value: form.value.phone },
  { label: '邮箱', value: form.value.email },
  { label: '职业', value: form.value.occupation },
  { label: '个性签名', value: form.value.signature },
  { label: '注册时间', value: formatTime(form.value.createTime) }
])

const longItems = computed(() => [
  { label: '爱好', value: form.value.hobbies },
  { label: '个人简介', value: form.value.bio },
  { label: '工作经验', value: form.value.experience }
])

const loadUserInfo = async () => {
  try {
    const response = await getUserInfoApi()
    if (response.code === 200 && response.data) {
      form.value = { ...form.value, ...response.data }
    }
  } catch (error) {
    console.error('[Profile] 获取个人信息失败:', error)
  }
}

watch(
  () => props.visible,
  (val) => {
    if (val) {
      editing.value = false
      form.value = { ...(props.userInfo || {}) }
      loadUserInfo()
    }
  },
  { immediate: true }
)

const startEdit = () => {
  EDITABLE_FIELDS.forEach((key) => {
    editForm[key] = form.value[key] ?? ''
  })
  editing.value = true
}

const cancelEdit = () => {
  editing.value = false
}

const triggerAvatarPick = () => {
  avatarInputRef.value && avatarInputRef.value.click()
}

const onAvatarChange = async (event) => {
  const file = event.target.files && event.target.files[0]
  event.target.value = ''
  if (!file) return
  if (!file.type.startsWith('image/')) {
    ElMessage.warning('请选择图片文件')
    return
  }
  const localUrl = URL.createObjectURL(file)
  form.value.avatar = localUrl
  try {
    const formData = new FormData()
    formData.append('avatar', file)
    const response = await uploadAvatar(formData)
    if (response.code === 200 && response.data) {
      form.value.avatar = response.data
      emit('updated', { avatar: response.data })
      ElMessage.success('头像更新成功')
    } else {
      ElMessage.error(response.message || '头像上传失败')
    }
  } catch (error) {
    console.error('[Profile] 头像上传失败:', error)
    ElMessage.error('头像上传失败')
  }
}

const save = async () => {
  if (saving.value) return
  if (!editForm.nickname || !String(editForm.nickname).trim()) {
    ElMessage.warning('昵称不能为空')
    return
  }
  saving.value = true
  try {
    const payload = { id: form.value.id }
    EDITABLE_FIELDS.forEach((key) => {
      payload[key] = editForm[key]
    })
    const response = await updateUserInfo(payload)
    if (response.code === 200) {
      form.value = { ...form.value, ...payload }
      editing.value = false
      emit('updated', { ...payload, avatar: form.value.avatar })
      ElMessage.success('保存成功')
    } else {
      ElMessage.error(response.message || '保存失败')
    }
  } catch (error) {
    console.error('[Profile] 保存个人信息失败:', error)
    ElMessage.error('保存失败')
  } finally {
    saving.value = false
  }
}

const onClose = () => {
  emit('update:visible', false)
}
</script>

<style scoped>
.profile-overlay {
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

.profile-card {
  width: 650px;
  max-width: calc(100vw - 32px);
  max-height: calc(100vh - 64px);
  display: flex;
  flex-direction: column;
  background: var(--theme-bg-elevated);
  border: 1px solid var(--theme-border);
  border-radius: var(--theme-radius-lg);
  box-shadow: var(--theme-shadow-lg);
  overflow: hidden;
}

.profile-header {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 14px 18px;
  border-bottom: 1px solid var(--theme-hairline);
}

.profile-title {
  flex: 1;
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: var(--theme-text-1);
  text-align: center;
  letter-spacing: -0.01em;
}

.icon-btn {
  width: 32px;
  height: 32px;
  border: none;
  border-radius: 10px;
  background: transparent;
  color: var(--theme-text-2);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  flex-shrink: 0;
  transition: background var(--theme-duration-fast) var(--theme-ease), color var(--theme-duration-fast) var(--theme-ease);
}

.icon-btn:hover {
  background: var(--theme-bg-hover);
  color: var(--theme-text-1);
}

.text-btn {
  min-width: 32px;
  padding: 6px 12px;
  border: none;
  border-radius: var(--theme-radius-pill);
  background: transparent;
  color: var(--theme-primary);
  font-size: 13.5px;
  font-weight: 500;
  font-family: inherit;
  cursor: pointer;
  flex-shrink: 0;
  transition: background var(--theme-duration-fast) var(--theme-ease);
}

.text-btn:hover {
  background: var(--theme-primary-soft);
}

.header-spacer {
  width: 32px;
  flex-shrink: 0;
}

.profile-body {
  flex: 1;
  min-height: 0;
  overflow-y: auto;
  padding: 22px 20px 24px;
}

.profile-body::-webkit-scrollbar {
  width: 6px;
}

.profile-body::-webkit-scrollbar-thumb {
  background: var(--theme-scrollbar-thumb);
  border-radius: 3px;
}

.profile-hero {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding-bottom: 22px;
  border-bottom: 1px solid var(--theme-hairline);
  margin-bottom: 18px;
}

.hero-avatar {
  position: relative;
  width: 88px;
  height: 88px;
  border-radius: 50%;
  overflow: hidden;
  box-shadow: var(--theme-shadow-md);
}

.hero-avatar.editable {
  cursor: pointer;
}

.hero-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.hero-avatar-mask {
  position: absolute;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--theme-overlay);
  color: #ffffff;
  opacity: 0;
  transition: opacity var(--theme-duration-fast) var(--theme-ease);
}

.hero-avatar.editable:hover .hero-avatar-mask {
  opacity: 1;
}

.hidden-input {
  display: none;
}

.hero-name {
  margin: 14px 0 0;
  font-size: 19px;
  font-weight: 600;
  color: var(--theme-text-1);
  letter-spacing: -0.2px;
}

.hero-sub {
  margin: 6px 0 0;
  font-size: 13px;
  color: var(--theme-text-3);
  text-align: center;
  padding: 0 20px;
}

.hero-chips {
  display: flex;
  gap: 8px;
  margin-top: 14px;
  flex-wrap: wrap;
  justify-content: center;
}

.hero-chip {
  padding: 4px 12px;
  border-radius: var(--theme-radius-pill);
  background: var(--theme-bg-surface-2);
  color: var(--theme-text-2);
  font-size: 12px;
}

.hero-chip--accent {
  background: var(--theme-primary-soft);
  color: var(--theme-primary);
  font-weight: 500;
}

.info-card {
  background: var(--theme-bg-surface);
  border: 1px solid var(--theme-border);
  border-radius: var(--theme-radius-md);
  margin-bottom: 14px;
  overflow: hidden;
}

.info-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  padding: 13px 16px;
  border-bottom: 1px solid var(--theme-hairline);
}

.info-row:last-child {
  border-bottom: none;
}

.info-label {
  font-size: 13.5px;
  color: var(--theme-text-2);
  flex-shrink: 0;
}

.info-value {
  font-size: 13.5px;
  color: var(--theme-text-1);
  font-weight: 500;
  text-align: right;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 62%;
}

.info-block {
  padding: 13px 16px;
  border-bottom: 1px solid var(--theme-hairline);
}

.info-block:last-child {
  border-bottom: none;
}

.info-text {
  margin: 6px 0 0;
  font-size: 13.5px;
  line-height: 1.6;
  color: var(--theme-text-1);
  white-space: pre-wrap;
  word-break: break-word;
}

.form-card {
  background: var(--theme-bg-surface);
  border: 1px solid var(--theme-border);
  border-radius: var(--theme-radius-md);
  padding: 4px 16px;
}

.form-row {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 11px 0;
  border-bottom: 1px solid var(--theme-hairline);
}

.form-row:last-child {
  border-bottom: none;
}

.form-row--block {
  flex-direction: column;
  align-items: stretch;
  gap: 8px;
}

.form-label {
  font-size: 13.5px;
  color: var(--theme-text-2);
  width: 72px;
  flex-shrink: 0;
}

.form-row--block .form-label {
  width: auto;
}

.form-input {
  flex: 1;
  min-width: 0;
  height: 36px;
  padding: 0 12px;
  border: 1px solid var(--theme-border);
  border-radius: 10px;
  background: var(--theme-bg-surface-2);
  color: var(--theme-text-1);
  font-size: 13.5px;
  font-family: inherit;
  outline: none;
  box-sizing: border-box;
  transition: border-color var(--theme-duration-fast) var(--theme-ease), box-shadow var(--theme-duration-fast) var(--theme-ease);
}

.form-input:focus {
  border-color: var(--theme-primary);
  box-shadow: var(--theme-ring);
}

.form-textarea {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid var(--theme-border);
  border-radius: 10px;
  background: var(--theme-bg-surface-2);
  color: var(--theme-text-1);
  font-size: 13.5px;
  line-height: 1.6;
  font-family: inherit;
  outline: none;
  resize: vertical;
  box-sizing: border-box;
  transition: border-color var(--theme-duration-fast) var(--theme-ease), box-shadow var(--theme-duration-fast) var(--theme-ease);
}

.form-textarea:focus {
  border-color: var(--theme-primary);
  box-shadow: var(--theme-ring);
}

.profile-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 14px 20px;
  border-top: 1px solid var(--theme-hairline);
}

.btn {
  padding: 9px 24px;
  border: none;
  border-radius: var(--theme-radius-pill);
  font-size: 14px;
  font-weight: 500;
  font-family: inherit;
  cursor: pointer;
  transition: background var(--theme-duration-fast) var(--theme-ease), filter var(--theme-duration-fast) var(--theme-ease);
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-secondary {
  background: var(--theme-bg-surface-2);
  color: var(--theme-text-1);
}

.btn-secondary:hover {
  background: var(--theme-bg-hover);
}

.btn-primary {
  background: var(--theme-gradient);
  color: var(--theme-primary-text);
}

.btn-primary:hover {
  filter: brightness(1.06);
}

.profile-fade-enter-active,
.profile-fade-leave-active {
  transition: opacity var(--theme-duration) var(--theme-ease);
}

.profile-fade-enter-active .profile-card,
.profile-fade-leave-active .profile-card {
  transition: transform var(--theme-duration) var(--theme-ease);
}

.profile-fade-enter-from,
.profile-fade-leave-to {
  opacity: 0;
}

.profile-fade-enter-from .profile-card,
.profile-fade-leave-to .profile-card {
  transform: translateY(12px) scale(0.98);
}
</style>
