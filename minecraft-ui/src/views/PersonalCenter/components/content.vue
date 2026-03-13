<template>
  <div class="profile-container">
    <!-- 玻璃拟态背景装饰 -->
    <div class="bg-decoration">
      <div class="glass-orb orb1"></div>
      <div class="glass-orb orb2"></div>
      <div class="glass-orb orb3"></div>
    </div>

    <!-- 主要内容卡片 -->
    <div class="profile-card" v-loading="loading" element-loading-text="加载中...">
      <!-- 头部区域：头像 + 基本信息 -->
      <div class="profile-header">
        <div class="avatar-section">
          <!-- 头像上传区域 -->
          <div class="avatar-container">
            <div class="avatar-wrapper" @click="triggerFileInput">
              <img v-if="avatarPreview" :src="avatarPreview" class="avatar" alt="用户头像" />
              <div v-else class="avatar-placeholder">
                <i class="fas fa-user"></i>
              </div>
              <div class="avatar-hover">
                <i class="fas fa-camera"></i>
                <span>上传照片</span>
              </div>
            </div>
            <input type="file" ref="fileInput" @change="handleAvatarUpload" accept="image/*" style="display: none;" />
          </div>
          <div class="account-badge">
            <span class="account-label">账号</span>
            <span class="account-value">{{ userInfo.account }}</span>
          </div>
        </div>
        <div class="header-info">
          <h1 class="user-name">{{ userInfo.username }}</h1>
          <div class="user-meta">
            <span class="meta-item" v-if="userInfo.email">
              <svg class="icon" viewBox="0 0 24 24" width="18" height="18">
                <path
                  d="M20 4H4c-1.1 0-2 .9-2 2v12c0 1.1.9 2 2 2h16c1.1 0 2-.9 2-2V6c0-1.1-.9-2-2-2zm0 4l-8 5-8-5V6l8 5 8-5v2z"
                  fill="currentColor" />
              </svg>
              {{ userInfo.email }}
            </span>
            <span class="meta-item" v-if="userInfo.phone">
              <svg class="icon" viewBox="0 0 24 24" width="18" height="18">
                <path
                  d="M6.62 10.79c1.44 2.83 3.76 5.14 6.59 6.59l2.2-2.2c.27-.27.67-.36 1.02-.24 1.12.37 2.33.57 3.57.57.55 0 1 .45 1 1V20c0 .55-.45 1-1 1-9.39 0-17-7.61-17-17 0-.55.45-1 1-1h3.5c.55 0 1 .45 1 1 0 1.25.2 2.45.57 3.57.11.35.03.74-.25 1.02l-2.2 2.2z"
                  fill="currentColor" />
              </svg>
              {{ userInfo.phone }}
            </span>
          </div>
          <div class="action-buttons">
            <el-button type="primary" :icon="Edit" @click="openEditModal" round>修改信息</el-button>
            <el-button :icon="Lock" @click="openPasswordModal" round>更改密码</el-button>
          </div>
        </div>
      </div>

      <!-- 详细信息网格 -->
      <div class="info-grid">
        <div class="info-item" v-if="userInfo.gender !== null">
          <span class="item-label">性别</span>
          <span class="item-value">{{ userInfo.gender === "1" ? '男' : userInfo.gender === "0" ? '女' : '未设置' }}</span>
        </div>
        <div class="info-item" v-if="userInfo.age">
          <span class="item-label">年龄</span>
          <span class="item-value">{{ userInfo.age }}岁</span>
        </div>
        <div class="info-item" v-if="userInfo.occupation">
          <span class="item-label">职业</span>
          <span class="item-value">{{ userInfo.occupation }}</span>
        </div>
        <div class="info-item" v-if="userInfo.hobbies">
          <span class="item-label">爱好</span>
          <span class="item-value">{{ userInfo.hobbies }}</span>
        </div>
        <div class="info-item" v-if="userInfo.bio">
          <span class="item-label">个人简介</span>
          <span class="item-value bio-value">{{ userInfo.bio }}</span>
        </div>
        <div class="info-item">
          <span class="item-label">账号状态</span>
          <span class="item-value">
            <el-tag :type="userInfo.status === 1 ? 'success' : 'danger'" size="small" effect="light">
              {{ userInfo.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </span>
        </div>
        <div class="info-item">
          <span class="item-label">注册时间</span>
          <span class="item-value">{{ formatDate(userInfo.createTime) }}</span>
        </div>
        <div class="info-item" v-if="userInfo.updateTime">
          <span class="item-label">最后更新</span>
          <span class="item-value">{{ formatDate(userInfo.updateTime) }}</span>
        </div>
      </div>

      <!-- 在线状态 (根据online字段) -->
      <div class="online-status" v-if="userInfo.online !== undefined">
        <span class="status-dot" :class="{ 'online': userInfo.online === 0 }"></span>
        <span>{{ userInfo.online === 0 ? '当前在线' : '离线' }}</span>
      </div>
    </div>

    <!-- 修改信息模态框 (玻璃拟态) -->
    <el-dialog v-model="showEditModal" title="修改个人信息" width="500px" :close-on-click-modal="false" class="glass-dialog"
      destroy-on-close>
      <el-form :model="editForm" label-width="80px" class="edit-form">
        <el-form-item label="用户名">
          <el-input v-model="editForm.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="editForm.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="editForm.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="性别">
          <el-select v-model="editForm.gender" placeholder="请选择" clearable>
            <el-option :value="1" label="男" />
            <el-option :value="0" label="女" />
          </el-select>
        </el-form-item>
        <el-form-item label="年龄">
          <el-input-number v-model="editForm.age" :min="1" :max="120" controls-position="right" />
        </el-form-item>
        <el-form-item label="职业">
          <el-input v-model="editForm.occupation" placeholder="请输入职业" />
        </el-form-item>
        <el-form-item label="爱好">
          <el-input v-model="editForm.hobbies" placeholder="请输入爱好" />
        </el-form-item>
        <el-form-item label="个人简介">
          <el-input v-model="editForm.bio" type="textarea" :rows="3" placeholder="介绍一下自己" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="closeEditModal">取消</el-button>
          <el-button type="primary" :loading="loading" @click="handleUpdateUserInfo">保存修改</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 更改密码模态框 (玻璃拟态) -->
    <el-dialog v-model="showPasswordModal" title="更改密码" width="400px" :close-on-click-modal="false" class="glass-dialog"
      destroy-on-close>
      <el-form :model="passwordForm" label-width="100px" class="edit-form">
        <el-form-item label="原密码">
          <el-input v-model="passwordForm.oldPassword" type="password" placeholder="请输入原密码" show-password />
        </el-form-item>
        <el-form-item label="新密码">
          <el-input v-model="passwordForm.newPassword" type="password" placeholder="请输入新密码" show-password />
        </el-form-item>
        <el-form-item label="确认密码">
          <el-input v-model="passwordForm.confirmPassword" type="password" placeholder="请再次输入新密码" show-password />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="closePasswordModal">取消</el-button>
          <el-button type="primary" :loading="passwordLoading" @click="handleUpdatePassword">确认更改</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { ElMessage } from 'element-plus';
import { Edit, Lock } from '@element-plus/icons-vue';
import { getUserInfo, updateUserInfo, updatePassword, uploadAvatar } from '@/api/user';

// 用户信息 (根据后端返回完整字段)
const userInfo = ref({
  id: '',
  account: '',
  username: '',
  email: '',
  phone: '',
  status: '',
  createTime: '',
  updateTime: '',
  avatar: '',
  gender: null,
  age: null,
  occupation: '',
  hobbies: '',
  bio: '',
  online: 0,
  password: '' // 不展示
});

// 加载状态
const loading = ref(false);
const passwordLoading = ref(false);

// 模态框状态
const showEditModal = ref(false);
const showPasswordModal = ref(false);

// 修改信息表单
const editForm = ref({
  username: '',
  email: '',
  phone: '',
  gender: null,
  age: null,
  occupation: '',
  hobbies: '',
  bio: ''
});

// 更改密码表单
const passwordForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
});

// 头像预览
const avatarPreview = ref('');

// 触发文件输入
const fileInput = ref(null);
const triggerFileInput = () => {
  fileInput.value.click();
};

// 初始化获取用户信息
onMounted(() => {
  fetchUserInfo();
});

// 获取用户信息 (从localStorage获取token)
const fetchUserInfo = async () => {
  try {
    loading.value = true;
    const token = localStorage.getItem('token');
    const response = await getUserInfo(token);
    if (response.code === 200) {
      userInfo.value = response.data;
      // 设置头像预览
      if (response.data.avatar) {
        avatarPreview.value = response.data.avatar;
      }
      console.log('用户信息:', response.data);
    } else {
      ElMessage.error(response.message || '获取用户信息失败');
    }
  } catch (error) {
    ElMessage.error('获取用户信息失败，请检查网络');
    console.error('获取用户信息失败:', error);
  } finally {
    loading.value = false;
  }
};

// 处理头像上传
const handleAvatarUpload = async (event) => {
  const file = event.target.files[0];
  if (!file) return;

  // 验证文件类型
  const validTypes = ['image/jpeg', 'image/png', 'image/gif', 'image/webp'];
  if (!validTypes.includes(file.type)) {
    ElMessage.error('请选择有效的图片文件');
    return;
  }

  // 验证文件大小 (2MB)
  const maxSize = 2 * 1024 * 1024;
  if (file.size > maxSize) {
    ElMessage.error('图片大小不能超过2MB');
    return;
  }

  try {
    loading.value = true;
    const formData = new FormData();
    formData.append('avatar', file);
    
    const response = await uploadAvatar(formData);
    if (response.code === 200) {
      ElMessage.success('头像上传成功');
      // 更新头像预览
      avatarPreview.value = response.data;
      // 重新获取用户信息
      await fetchUserInfo();
    } else {
      ElMessage.error(response.message || '头像上传失败');
    }
  } catch (error) {
    ElMessage.error('头像上传失败，请检查网络');
    console.error('头像上传失败:', error);
  } finally {
    loading.value = false;
    // 重置文件输入
    event.target.value = '';
  }
};

// 打开修改信息模态框
const openEditModal = () => {
  // 填充表单数据
  editForm.value = {
    username: userInfo.value.username || '',
    email: userInfo.value.email || '',
    phone: userInfo.value.phone || '',
    gender: userInfo.value.gender,
    age: userInfo.value.age,
    occupation: userInfo.value.occupation || '',
    hobbies: userInfo.value.hobbies || '',
    bio: userInfo.value.bio || ''
  };
  showEditModal.value = true;
};

// 关闭修改信息模态框
const closeEditModal = () => {
  showEditModal.value = false;
};

// 打开更改密码模态框
const openPasswordModal = () => {
  // 重置表单
  passwordForm.value = {
    oldPassword: '',
    newPassword: '',
    confirmPassword: ''
  };
  showPasswordModal.value = true;
};

// 关闭更改密码模态框
const closePasswordModal = () => {
  showPasswordModal.value = false;
};

// 更新用户信息
const handleUpdateUserInfo = async () => {
  try {
    loading.value = true;

    // 验证表单
    if (!editForm.value.username) {
      ElMessage.warning('请输入用户名');
      return;
    }
    if (!editForm.value.email) {
      ElMessage.warning('请输入邮箱');
      return;
    }
    if (!editForm.value.phone) {
      ElMessage.warning('请输入手机号');
      return;
    }

    // 构建更新数据
    const updateData = {
      id: userInfo.value.id,
      username: editForm.value.username,
      email: editForm.value.email,
      phone: editForm.value.phone,
      gender: editForm.value.gender,
      age: editForm.value.age,
      occupation: editForm.value.occupation,
      hobbies: editForm.value.hobbies,
      bio: editForm.value.bio
    };

    const response = await updateUserInfo(updateData);
    if (response.code === 200) {
      ElMessage.success('修改成功');
      // 重新获取用户信息
      await fetchUserInfo();
      // 关闭模态框
      closeEditModal();
    } else {
      ElMessage.error(response.message || '修改失败');
    }
  } catch (error) {
    ElMessage.error('修改失败，请检查网络');
    console.error('修改用户信息失败:', error);
  } finally {
    loading.value = false;
  }
};

// 更新密码
const handleUpdatePassword = async () => {
  try {
    passwordLoading.value = true;

    // 验证表单
    if (!passwordForm.value.oldPassword) {
      ElMessage.warning('请输入原密码');
      return;
    }
    if (!passwordForm.value.newPassword) {
      ElMessage.warning('请输入新密码');
      return;
    }
    if (passwordForm.value.newPassword !== passwordForm.value.confirmPassword) {
      ElMessage.warning('两次输入的密码不一致');
      return;
    }

    const response = await updatePassword(passwordForm.value.oldPassword, passwordForm.value.newPassword);
    if (response.code === 200) {
      ElMessage.success('密码更改成功');
      // 关闭模态框
      closePasswordModal();
    } else {
      ElMessage.error(response.message || '密码更改失败');
    }
  } catch (error) {
    ElMessage.error('密码更改失败，请检查网络');
    console.error('更改密码失败:', error);
  } finally {
    passwordLoading.value = false;
  }
};

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  return date.toLocaleString('zh-CN', { year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' });
};
</script>

<style scoped>
/* 全局容器 */
.profile-container {
  min-height: 100vh;
  padding: 2rem;
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
  background-image: linear-gradient(to right, #4facfe 0%, #00f2fe 100%);
  overflow: hidden;
  font-family: 'Inter', system-ui, -apple-system, sans-serif;
}

/* 玻璃背景装饰 */
.bg-decoration {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 0;
}

.glass-orb {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.3);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.orb1 {
  width: 400px;
  height: 400px;
  top: -100px;
  right: -100px;
  background: radial-gradient(circle at 30% 30%, rgba(255, 255, 255, 0.4), rgba(255, 255, 255, 0.1));
}

.orb2 {
  width: 300px;
  height: 300px;
  bottom: -50px;
  left: -50px;
  background: radial-gradient(circle at 70% 70%, rgba(255, 255, 255, 0.4), rgba(255, 255, 255, 0.1));
}

.orb3 {
  width: 200px;
  height: 200px;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  background: radial-gradient(circle at 50% 50%, rgba(255, 255, 255, 0.3), rgba(255, 255, 255, 0.05));
  filter: blur(20px);
}

/* 主卡片 — 玻璃拟态 */
.profile-card {
  position: relative;
  z-index: 1;
  width: 100%;
  max-width: 900px;
  background: rgba(255, 255, 255, 0.25);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border-radius: 40px;
  border: 1px solid rgba(255, 255, 255, 0.4);
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
  padding: 2.5rem;
  color: #1e293b;
  transition: transform 0.3s ease;
}

.profile-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 30px 60px -12px rgba(0, 0, 0, 0.3);
}

/* 头部区域 */
.profile-header {
  display: flex;
  gap: 2rem;
  margin-bottom: 2.5rem;
  flex-wrap: wrap;
}

.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1rem;
}

.avatar-container {
  display: flex;
  justify-content: center;
  margin-bottom: 30px;
}

.avatar-wrapper {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  position: relative;
  cursor: pointer;
  border: 2px dashed rgba(255, 255, 255, 0.5);
  transition: all 0.3s ease;
  overflow: hidden;
}

.avatar-wrapper:hover {
  transform: scale(1.05);
  border-color: rgba(255, 255, 255, 0.8);
}

.avatar {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 50%;
}

.avatar-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 50%;
}

.avatar-placeholder i {
  font-size: 40px;
  color: rgba(255, 255, 255, 0.7);
}

.avatar-hover {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(191, 191, 191, 0.5);
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  opacity: 0;
  transition: opacity 0.3s ease;
  border-radius: 50%;
}

.avatar-wrapper:hover .avatar-hover {
  opacity: 1;
}

.avatar-hover i {
  font-size: 20px;
  margin-bottom: 5px;
}

.avatar-hover span {
  font-size: 12px;
}

.account-badge {
  background: rgba(255, 255, 255, 0.4);
  backdrop-filter: blur(4px);
  padding: 0.4rem 1rem;
  border-radius: 40px;
  border: 1px solid rgba(255, 255, 255, 0.6);
  font-size: 0.85rem;
  display: flex;
  gap: 0.3rem;
}

.account-label {
  opacity: 0.7;
}

.account-value {
  font-weight: 600;
}

.header-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.user-name {
  font-size: 2.2rem;
  font-weight: 700;
  margin: 0 0 0.5rem 0;
  color: #0f172a;
  text-shadow: 0 2px 4px rgba(255, 255, 255, 0.3);
}

.user-meta {
  display: flex;
  gap: 1.5rem;
  flex-wrap: wrap;
  margin-bottom: 1.2rem;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  background: rgba(255, 255, 255, 0.3);
  padding: 0.4rem 1rem;
  border-radius: 40px;
  border: 1px solid rgba(255, 255, 255, 0.4);
  font-size: 0.9rem;
}

.action-buttons {
  display: flex;
  gap: 1rem;
  flex-wrap: wrap;
}

/* 按钮自定义 — 保持玻璃感 */
:deep(.el-button) {
  backdrop-filter: blur(4px);
  border: 1px solid rgba(255, 255, 255, 0.6) !important;
  background: rgba(255, 255, 255, 0.3) !important;
  color: #1e293b !important;
  font-weight: 500;
  transition: all 0.2s ease;
}

:deep(.el-button:hover) {
  background: rgba(255, 255, 255, 0.5) !important;
  border-color: rgba(255, 255, 255, 0.8) !important;
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
}

:deep(.el-button--primary) {
  background: rgba(64, 128, 255, 0.4) !important;
  border-color: rgba(255, 255, 255, 0.8) !important;
}

:deep(.el-button--primary:hover) {
  background: rgba(64, 128, 255, 0.6) !important;
}

/* 信息网格 */
.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 1.5rem;
  margin-top: 1rem;
}

.info-item {
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(4px);
  border: 1px solid rgba(255, 255, 255, 0.4);
  border-radius: 24px;
  padding: 1rem 1.2rem;
  transition: all 0.2s;
}

.info-item:hover {
  background: rgba(255, 255, 255, 0.3);
  border-color: rgba(255, 255, 255, 0.6);
}

.item-label {
  display: block;
  font-size: 0.75rem;
  text-transform: uppercase;
  letter-spacing: 1px;
  opacity: 0.6;
  margin-bottom: 0.4rem;
}

.item-value {
  font-size: 1.1rem;
  font-weight: 500;
  color: #0f172a;
  word-break: break-word;
}

.bio-value {
  font-size: 0.95rem;
  line-height: 1.5;
  opacity: 0.9;
}

/* 在线状态 */
.online-status {
  margin-top: 2rem;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  justify-content: flex-end;
  font-size: 0.9rem;
  background: rgba(255, 255, 255, 0.2);
  padding: 0.5rem 1.5rem;
  border-radius: 40px;
  float: right;
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.status-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: #94a3b8;
  transition: background 0.3s;
}

.status-dot.online {
  background: #10b981;
  box-shadow: 0 0 8px #10b981;
}

/* 玻璃拟态对话框覆盖 */
:deep(.glass-dialog) {
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
}

:deep(.glass-dialog .el-dialog) {
  background: rgba(255, 255, 255, 0.3) !important;
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  border: 1px solid rgba(255, 255, 255, 0.5);
  border-radius: 32px !important;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
}

:deep(.glass-dialog .el-dialog__header) {
  border-bottom: 1px solid rgba(255, 255, 255, 0.3);
  padding: 1.5rem 1.5rem 1rem;
}

:deep(.glass-dialog .el-dialog__title) {
  color: #0f172a;
  font-weight: 600;
  font-size: 1.3rem;
}

:deep(.glass-dialog .el-dialog__body) {
  padding: 1.5rem;
}

:deep(.glass-dialog .el-dialog__footer) {
  border-top: 1px solid rgba(255, 255, 255, 0.3);
  padding: 1rem 1.5rem 1.5rem;
}

/* 表单元素适配玻璃背景 */
:deep(.edit-form .el-input__wrapper),
:deep(.edit-form .el-textarea__inner),
:deep(.edit-form .el-select .el-input__wrapper) {
  background: rgba(255, 255, 255, 0.3) !important;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05) !important;
  border: 1px solid rgba(255, 255, 255, 0.5) !important;
  backdrop-filter: blur(4px);
}

:deep(.edit-form .el-input__inner),
:deep(.edit-form .el-textarea__inner) {
  color: #0f172a;
}

:deep(.edit-form .el-input__inner::placeholder) {
  color: rgba(15, 23, 42, 0.5);
}

:deep(.edit-form .el-form-item__label) {
  color: #1e293b;
  font-weight: 500;
}

:deep(.el-select .el-input__wrapper.is-focused),
:deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px rgba(255, 255, 255, 0.8) inset !important;
}

/* 响应式 */
@media (max-width: 640px) {
  .profile-container {
    padding: 1rem;
  }

  .profile-card {
    padding: 1.5rem;
  }

  .profile-header {
    flex-direction: column;
    align-items: center;
    text-align: center;
  }

  .user-meta {
    justify-content: center;
  }

  .action-buttons {
    justify-content: center;
  }

  .info-grid {
    grid-template-columns: 1fr;
  }

  .online-status {
    float: none;
    justify-content: center;
  }
}
</style>