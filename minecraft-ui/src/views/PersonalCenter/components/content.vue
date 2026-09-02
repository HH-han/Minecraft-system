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
            <el-button type="success" @click="goToPoints" round>积分商城</el-button>
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
        <div class="info-item" v-if="userInfo.nickname">
          <span class="item-label">昵称</span>
          <span class="item-value bio-value">{{ userInfo.nickname }}</span>
        </div>
        <div class="info-item" v-if="userInfo.hobbies">
          <span class="item-label">爱好</span>
          <span class="item-value">{{ userInfo.hobbies }}</span>
        </div>
        <div class="info-item" v-if="userInfo.bio">
          <span class="item-label">个人简介</span>
          <span class="item-value bio-value">{{ userInfo.bio }}</span>
        </div>
        <div class="info-item" v-if="userInfo.experience">
          <span class="item-label">经验</span>
          <span class="item-value bio-value">{{ userInfo.experience }}</span>
        </div>
        <div class="info-item" v-if="userInfo.signature">
          <span class="item-label">签名</span>
          <span class="item-value bio-value">{{ userInfo.signature }}</span>
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

    <!-- 修改信息模态框 (Apple 风格玻璃拟态) -->
    <div v-if="showEditModal" class="modal-overlay" @click.self="">
      <div class="modal-dialog modal-edit" role="dialog" aria-modal="true">
        <div class="modal-header">
          <h3 class="modal-title">修改个人信息</h3>
          <button class="modal-close" type="button" aria-label="关闭" @click="closeEditModal">
            <svg viewBox="0 0 24 24" width="18" height="18" aria-hidden="true">
              <path d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z" fill="currentColor"/>
            </svg>
          </button>
        </div>
        <div class="modal-body">
          <div class="form-list">
            <div class="form-item">
              <label class="form-label">用户名</label>
              <input class="form-input" type="text" v-model="editForm.username" placeholder="请输入用户名" />
            </div>
            <div class="form-item">
              <label class="form-label">昵称</label>
              <input class="form-input" type="text" v-model="editForm.nickname" placeholder="请输入昵称" />
            </div>
            <div class="form-item">
              <label class="form-label">邮箱</label>
              <input class="form-input" type="email" v-model="editForm.email" placeholder="请输入邮箱" />
            </div>
            <div class="form-item">
              <label class="form-label">手机号</label>
              <input class="form-input" type="tel" v-model="editForm.phone" placeholder="请输入手机号" />
            </div>
            <div class="form-item">
              <label class="form-label">性别</label>
              <div class="form-select-wrap">
                <select class="form-input form-select" v-model="editForm.gender">
                  <option :value="null" value="">请选择</option>
                  <option :value="1" value="1">男</option>
                  <option :value="0" value="0">女</option>
                </select>
                <svg class="select-chevron" viewBox="0 0 24 24" width="16" height="16" aria-hidden="true">
                  <path d="M7 10l5 5 5-5z" fill="currentColor"/>
                </svg>
              </div>
            </div>
            <div class="form-item">
              <label class="form-label">年龄</label>
              <input class="form-input" type="number" min="1" max="120" v-model.number="editForm.age" placeholder="请输入年龄" />
            </div>
            <div class="form-item">
              <label class="form-label">职业</label>
              <input class="form-input" type="text" v-model="editForm.occupation" placeholder="请输入职业" />
            </div>
            <div class="form-item">
              <label class="form-label">爱好</label>
              <input class="form-input" type="text" v-model="editForm.hobbies" placeholder="请输入爱好" />
            </div>
            <div class="form-item form-item--block">
              <label class="form-label">个人简介</label>
              <textarea class="form-input form-textarea" rows="3" v-model="editForm.bio" placeholder="介绍一下自己"></textarea>
            </div>
            <div class="form-item">
              <label class="form-label">签名</label>
              <input class="form-input" type="text" v-model="editForm.signature" placeholder="请输入签名" />
            </div>
            <div class="form-item form-item--block">
              <label class="form-label">经验</label>
              <textarea class="form-input form-textarea" rows="3" v-model="editForm.experience" placeholder="请输入经验"></textarea>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn btn-secondary" type="button" @click="closeEditModal">取消</button>
          <button class="btn btn-primary" type="button" :disabled="loading" @click="handleUpdateUserInfo">
            <span v-if="loading" class="btn-spinner" aria-hidden="true"></span>
            {{ loading ? '保存中…' : '保存修改' }}
          </button>
        </div>
      </div>
    </div>

    <!-- 更改密码模态框 (Apple 风格玻璃拟态) -->
    <div v-if="showPasswordModal" class="modal-overlay" @click.self="">
      <div class="modal-dialog modal-password" role="dialog" aria-modal="true">
        <div class="modal-header">
          <h3 class="modal-title">更改密码</h3>
          <button class="modal-close" type="button" aria-label="关闭" @click="closePasswordModal">
            <svg viewBox="0 0 24 24" width="18" height="18" aria-hidden="true">
              <path d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z" fill="currentColor"/>
            </svg>
          </button>
        </div>
        <div class="modal-body">
          <div class="form-list">
            <div class="form-item form-item--block">
              <label class="form-label">原密码</label>
              <div class="form-input-wrap">
                <input class="form-input" :type="showOldPwd ? 'text' : 'password'" v-model="passwordForm.oldPassword" placeholder="请输入原密码" />
                <button type="button" class="pwd-toggle" :aria-label="showOldPwd ? '隐藏密码' : '显示密码'" @click="showOldPwd = !showOldPwd">
                  <svg v-if="showOldPwd" viewBox="0 0 24 24" width="18" height="18" aria-hidden="true">
                    <path d="M12 4.5C7 4.5 2.73 7.61 1 12c1.73 4.39 6 7.5 11 7.5s9.27-3.11 11-7.5c-1.73-4.39-6-7.5-11-7.5zM12 17c-2.76 0-5-2.24-5-5s2.24-5 5-5 5 2.24 5 5-2.24 5-5 5zm0-8c-1.66 0-3 1.34-3 3s1.34 3 3 3 3-1.34 3-3-1.34-3-3-3z" fill="currentColor"/>
                  </svg>
                  <svg v-else viewBox="0 0 24 24" width="18" height="18" aria-hidden="true">
                    <path d="M12 7c2.76 0 5 2.24 5 5 0 .65-.13 1.26-.36 1.83l2.92 2.92c1.51-1.26 2.7-2.89 3.43-4.75-1.73-4.39-6-7.5-11-7.5-1.4 0-2.74.25-3.98.7l2.16 2.16C10.74 7.13 11.35 7 12 7zM2 4.27l2.28 2.28.46.46A11.804 11.804 0 001 12c1.73 4.39 6 7.5 11 7.5 1.55 0 3.03-.3 4.38-.84l.42.42L19.73 22 21 20.73 3.27 3 2 4.27zM7.53 9.8l1.55 1.55c-.05.21-.08.43-.08.65 0 1.66 1.34 3 3 3 .22 0 .44-.03.65-.08l1.55 1.55c-.67.33-1.41.53-2.2.53-2.76 0-5-2.24-5-5 0-.79.2-1.53.53-2.2zm4.31-.78l3.15 3.15.02-.16c0-1.66-1.34-3-3-3l-.17.01z" fill="currentColor"/>
                  </svg>
                </button>
              </div>
            </div>
            <div class="form-item form-item--block">
              <label class="form-label">新密码</label>
              <div class="form-input-wrap">
                <input class="form-input" :type="showNewPwd ? 'text' : 'password'" v-model="passwordForm.newPassword" placeholder="请输入新密码" />
                <button type="button" class="pwd-toggle" :aria-label="showNewPwd ? '隐藏密码' : '显示密码'" @click="showNewPwd = !showNewPwd">
                  <svg v-if="showNewPwd" viewBox="0 0 24 24" width="18" height="18" aria-hidden="true">
                    <path d="M12 4.5C7 4.5 2.73 7.61 1 12c1.73 4.39 6 7.5 11 7.5s9.27-3.11 11-7.5c-1.73-4.39-6-7.5-11-7.5zM12 17c-2.76 0-5-2.24-5-5s2.24-5 5-5 5 2.24 5 5-2.24 5-5 5zm0-8c-1.66 0-3 1.34-3 3s1.34 3 3 3 3-1.34 3-3-1.34-3-3-3z" fill="currentColor"/>
                  </svg>
                  <svg v-else viewBox="0 0 24 24" width="18" height="18" aria-hidden="true">
                    <path d="M12 7c2.76 0 5 2.24 5 5 0 .65-.13 1.26-.36 1.83l2.92 2.92c1.51-1.26 2.7-2.89 3.43-4.75-1.73-4.39-6-7.5-11-7.5-1.4 0-2.74.25-3.98.7l2.16 2.16C10.74 7.13 11.35 7 12 7zM2 4.27l2.28 2.28.46.46A11.804 11.804 0 001 12c1.73 4.39 6 7.5 11 7.5 1.55 0 3.03-.3 4.38-.84l.42.42L19.73 22 21 20.73 3.27 3 2 4.27zM7.53 9.8l1.55 1.55c-.05.21-.08.43-.08.65 0 1.66 1.34 3 3 3 .22 0 .44-.03.65-.08l1.55 1.55c-.67.33-1.41.53-2.2.53-2.76 0-5-2.24-5-5 0-.79.2-1.53.53-2.2zm4.31-.78l3.15 3.15.02-.16c0-1.66-1.34-3-3-3l-.17.01z" fill="currentColor"/>
                  </svg>
                </button>
              </div>
            </div>
            <div class="form-item form-item--block">
              <label class="form-label">确认密码</label>
              <div class="form-input-wrap">
                <input class="form-input" :type="showConfirmPwd ? 'text' : 'password'" v-model="passwordForm.confirmPassword" placeholder="请再次输入新密码" />
                <button type="button" class="pwd-toggle" :aria-label="showConfirmPwd ? '隐藏密码' : '显示密码'" @click="showConfirmPwd = !showConfirmPwd">
                  <svg v-if="showConfirmPwd" viewBox="0 0 24 24" width="18" height="18" aria-hidden="true">
                    <path d="M12 4.5C7 4.5 2.73 7.61 1 12c1.73 4.39 6 7.5 11 7.5s9.27-3.11 11-7.5c-1.73-4.39-6-7.5-11-7.5zM12 17c-2.76 0-5-2.24-5-5s2.24-5 5-5 5 2.24 5 5-2.24 5-5 5zm0-8c-1.66 0-3 1.34-3 3s1.34 3 3 3 3-1.34 3-3-1.34-3-3-3z" fill="currentColor"/>
                  </svg>
                  <svg v-else viewBox="0 0 24 24" width="18" height="18" aria-hidden="true">
                    <path d="M12 7c2.76 0 5 2.24 5 5 0 .65-.13 1.26-.36 1.83l2.92 2.92c1.51-1.26 2.7-2.89 3.43-4.75-1.73-4.39-6-7.5-11-7.5-1.4 0-2.74.25-3.98.7l2.16 2.16C10.74 7.13 11.35 7 12 7zM2 4.27l2.28 2.28.46.46A11.804 11.804 0 001 12c1.73 4.39 6 7.5 11 7.5 1.55 0 3.03-.3 4.38-.84l.42.42L19.73 22 21 20.73 3.27 3 2 4.27zM7.53 9.8l1.55 1.55c-.05.21-.08.43-.08.65 0 1.66 1.34 3 3 3 .22 0 .44-.03.65-.08l1.55 1.55c-.67.33-1.41.53-2.2.53-2.76 0-5-2.24-5-5 0-.79.2-1.53.53-2.2zm4.31-.78l3.15 3.15.02-.16c0-1.66-1.34-3-3-3l-.17.01z" fill="currentColor"/>
                  </svg>
                </button>
              </div>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn btn-secondary" type="button" @click="closePasswordModal">取消</button>
          <button class="btn btn-primary" type="button" :disabled="passwordLoading" @click="handleUpdatePassword">
            <span v-if="passwordLoading" class="btn-spinner" aria-hidden="true"></span>
            {{ passwordLoading ? '更改中…' : '确认更改' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed } from 'vue';
import { ElMessage } from 'element-plus';
import { Edit, Lock } from '@element-plus/icons-vue';
import { useRouter } from 'vue-router';
import { getUserInfo, updateUserInfo, updatePassword, uploadAvatar } from '@/api/user';
import { getUserPoints } from '@/api/points';

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
  password: '',
  nickname:'',
  signature:'',
  experience:''
});

// 积分信息
const userPoints = ref(0);

// 加载状态
const loading = ref(false);
const passwordLoading = ref(false);

// 路由实例
const router = useRouter();

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
  bio: '',
  nickname:'',
  signature:'',
  experience:''
});

// 更改密码表单
const passwordForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
});

// 密码显示开关
const showOldPwd = ref(false);
const showNewPwd = ref(false);
const showConfirmPwd = ref(false);

// 头像预览
const avatarPreview = ref('');

// 触发文件输入
const fileInput = ref(null);
const triggerFileInput = () => {
  fileInput.value.click();
};

// 初始化获取用户信息
const onEscKey = (e) => {
  if (e.key !== 'Escape') return;
  if (showEditModal.value)     closeEditModal();
  if (showPasswordModal.value) closePasswordModal();
};

onMounted(() => {
  fetchUserInfo();
  fetchUserPoints();
  window.addEventListener('keydown', onEscKey);
});

onUnmounted(() => {
  window.removeEventListener('keydown', onEscKey);
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

// 获取用户积分
const fetchUserPoints = async () => {
  try {
    const response = await getUserPoints();
    if (response.code === 200) {
      userPoints.value = response.data;
      console.log('用户积分:', response.data);
    } else {
      ElMessage.error(response.message || '获取用户积分失败');
    }
  } catch (error) {
    console.error('获取用户积分失败:', error);
  }
};

// 跳转到积分商城
const goToPoints = () => {
  router.push('/points');
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
    bio: userInfo.value.bio || '',
    nickname: userInfo.value.nickname || '',
    signature: userInfo.value.signature || '',
    experience: userInfo.value.experience || ''
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
      bio: editForm.value.bio,
      nickname: editForm.value.nickname,
      signature: editForm.value.signature,
      experience: editForm.value.experience
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

/* ========== 模态框 (Apple 风格玻璃拟态) ========== */
.modal-overlay {
  position: fixed;
  inset: 0;
  z-index: 1000;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 24px;
  background: rgba(0, 0, 0, 0.36);
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
  animation: modal-fade 0.2s ease;
}

@keyframes modal-fade {
  from { opacity: 0; }
  to   { opacity: 1; }
}

.modal-dialog {
  position: relative;
  width: 100%;
  max-height: calc(100vh - 48px);
  display: flex;
  flex-direction: column;
  /* Apple 导航栏玻璃色: rgba(255,255,255,0.72) + blur(20px) */
  background: rgba(255, 255, 255, 0.78);
  backdrop-filter: blur(20px) saturate(180%);
  -webkit-backdrop-filter: blur(20px) saturate(180%);
  border: 1px solid rgba(255, 255, 255, 0.6);
  border-radius: 28px;                              /* Apple 卡片圆角 24~28px */
  box-shadow:
    0 30px 60px -20px rgba(0, 0, 0, 0.25),
    0 0 0 0.5px rgba(0, 0, 0, 0.06);
  overflow: hidden;
  color: #1d1d1f;                                    /* Apple 主文本色 */
  font-family: 'Inter', system-ui, -apple-system, 'PingFang SC', 'SF Pro Display', sans-serif;
  animation: modal-pop 0.25s cubic-bezier(0.22, 1, 0.36, 1);
}

@keyframes modal-pop {
  from { opacity: 0; transform: translateY(12px) scale(0.98); }
  to   { opacity: 1; transform: translateY(0) scale(1); }
}

.modal-edit      { max-width: 560px; }
.modal-password  { max-width: 440px; }

/* --- 头部 --- */
.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px 24px 16px;
  border-bottom: 1px solid #d2d2d6;                /* Apple 分割线色 */
  flex-shrink: 0;
}

.modal-title {
  margin: 0;
  font-size: 20px;
  font-weight: 600;                                 /* Apple H3 600~700 */
  line-height: 1.2;
  color: #1d1d1f;
  letter-spacing: -0.01em;
}

.modal-close {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  border: none;
  border-radius: 50%;
  background: transparent;
  color: #6e6e73;                                   /* Apple 次要文本色 */
  cursor: pointer;
  transition: background 0.2s ease, color 0.2s ease;
}
.modal-close:hover {
  background: #f5f5f7;                              /* Apple 背景辅色 */
  color: #1d1d1f;
}
.modal-close:focus-visible {
  outline: 2px solid #2997ff;
  outline-offset: 2px;
}

/* --- 主体 --- */
.modal-body {
  padding: 24px;
  overflow-y: auto;
  flex: 1 1 auto;
}

/* --- 表单 (Apple 风格) --- */
.form-list {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px 20px;                                   /* 8px 网格基准 */
}
.form-item--block {
  grid-column: 1 / -1;
}

.form-label {
  display: block;
  margin-bottom: 6px;
  font-size: 13px;
  font-weight: 500;
  color: #6e6e73;                                   /* Apple 次要文本: 标签 */
  letter-spacing: 0.01em;
}

.form-input-wrap {
  position: relative;
}

.form-input {
  display: block;
  width: 100%;
  box-sizing: border-box;
  padding: 10px 14px;
  font-size: 15px;                                  /* Apple 正文 16~18px, 输入稍小 */
  line-height: 1.4;
  font-family: inherit;
  color: #1d1d1f;                                   /* 输入主文本 */
  background: #ffffff;                              /* Apple 白色输入底 */
  border: 1px solid #d2d2d6;                        /* Apple 边框 */
  border-radius: 12px;                              /* 输入圆角, 与 Apple 表单匹配 */
  outline: none;
  transition: border-color 0.2s ease, box-shadow 0.2s ease, background 0.2s ease;
  -webkit-appearance: none;
  appearance: none;
}

.form-input::placeholder {
  color: #a1a1a6;
}

.form-input:hover {
  border-color: #b5b5ba;
}

.form-input:focus {
  border-color: #2997ff;                            /* Apple 强调色 */
  box-shadow: 0 0 0 4px rgba(41, 151, 255, 0.12);  /* 蓝色焦点光晕 */
  background: #ffffff;
}

/* textarea 高度微调 */
.form-textarea {
  min-height: 80px;
  resize: vertical;
  font: inherit;
}

/* 原生数字输入去箭头 (视觉统一) */
.form-input[type="number"]::-webkit-outer-spin-button,
.form-input[type="number"]::-webkit-inner-spin-button {
  -webkit-appearance: none;
  margin: 0;
}
.form-input[type="number"] {
  -moz-appearance: textfield;
}

/* --- 下拉选择 --- */
.form-select-wrap {
  position: relative;
}
.form-select {
  padding-right: 36px;                              /* 给 chevron 留位 */
  cursor: pointer;
}
.form-select-wrap .select-chevron {
  position: absolute;
  top: 50%;
  right: 12px;
  transform: translateY(-50%);
  color: #6e6e73;
  pointer-events: none;
}

/* --- 密码显示按钮 --- */
.pwd-toggle {
  position: absolute;
  top: 50%;
  right: 8px;
  transform: translateY(-50%);
  width: 36px;
  height: 36px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border: none;
  border-radius: 10px;
  background: transparent;
  color: #6e6e73;
  cursor: pointer;
  transition: background 0.2s ease, color 0.2s ease;
}
.pwd-toggle:hover {
  background: #f5f5f7;
  color: #1d1d1f;
}
.pwd-toggle:focus-visible {
  outline: 2px solid #2997ff;
  outline-offset: 1px;
}

/* --- 底部 --- */
.modal-footer {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 12px;
  padding: 16px 24px 20px;
  border-top: 1px solid #d2d2d6;
  flex-shrink: 0;
  background: rgba(245, 245, 247, 0.4);
}

/* --- 按钮 (Apple 风格) --- */
.btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  min-width: 104px;
  padding: 12px 28px;                               /* Apple 按钮内边距 */
  border-radius: 40px;                              /* Apple 按钮圆角 40px */
  font-size: 15px;
  font-weight: 500;
  line-height: 1;
  font-family: inherit;
  border: 1px solid transparent;
  cursor: pointer;
  transition: background 0.2s ease, color 0.2s ease,
              border-color 0.2s ease, transform 0.2s ease,
              box-shadow 0.2s ease;
  user-select: none;
}
.btn:focus-visible {
  outline: 2px solid #2997ff;
  outline-offset: 2px;
}
.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-secondary {
  background: #ffffff;
  color: #1d1d1f;
  border-color: #d2d2d6;
}
.btn-secondary:hover:not(:disabled) {
  background: #f5f5f7;
  border-color: #b5b5ba;
}

.btn-primary {
  background: #2997ff;                              /* Apple 强调色 */
  color: #ffffff;
  border-color: #2997ff;
  box-shadow: 0 4px 12px rgba(41, 151, 255, 0.25);
}
.btn-primary:hover:not(:disabled) {
  background: #0066cc;                              /* Apple 悬停加深 */
  border-color: #0066cc;
  transform: translateY(-1px);
  box-shadow: 0 8px 20px rgba(0, 102, 204, 0.3);
}
.btn-primary:active:not(:disabled) {
  transform: translateY(0);
}

/* --- 按钮加载 spinner --- */
.btn-spinner {
  display: inline-block;
  width: 16px;
  height: 16px;
  border: 2px solid rgba(255, 255, 255, 0.5);
  border-top-color: #ffffff;
  border-radius: 50%;
  animation: spin 0.7s linear infinite;
}
.btn-secondary .btn-spinner {
  border-color: rgba(29, 29, 31, 0.3);
  border-top-color: #1d1d1f;
}
@keyframes spin {
  to { transform: rotate(360deg); }
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

  /* 模态框移动端适配 */
  .modal-overlay {
    padding: 12px;
    align-items: flex-end;
  }
  .modal-dialog {
    border-radius: 24px 24px 28px 28px;
    max-height: 92vh;
    animation: modal-pop-mobile 0.28s cubic-bezier(0.22, 1, 0.36, 1);
  }
  @keyframes modal-pop-mobile {
    from { opacity: 0; transform: translateY(24px); }
    to   { opacity: 1; transform: translateY(0); }
  }
  .modal-header  { padding: 16px 20px 12px; }
  .modal-body    { padding: 16px 20px; }
  .modal-footer  { padding: 12px 20px 16px; gap: 8px; }
  .modal-title   { font-size: 18px; }
  .form-list     { grid-template-columns: 1fr; gap: 12px; }
  .btn           { min-width: 92px; padding: 11px 20px; font-size: 14px; }
}
</style>