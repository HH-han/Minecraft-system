<template>
  <div class="user-info-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1>个人信息</h1>
      <p>查看和管理您的个人资料</p>
    </div>

    <!-- 个人资料卡片 -->
    <div class="profile-card">
      <!-- 头像区域 -->
      <div class="avatar-section">
        <div class="avatar-container">
          <img :src="userData.image" :alt="userData.nickname" class="avatar">
          <div class="avatar-overlay">
            <button class="edit-avatar-btn">
              <svg viewBox="0 0 24 24" class="icon">
                <path d="M19,13H13V19H11V13H5V11H11V5H13V11H19V13Z" />
              </svg>
            </button>
          </div>
        </div>
        <h2 class="nickname">{{ userData.nickname }}</h2>
        <p class="username">{{ userData.username }}</p>
        <p class="signature">{{ userData.signature || '暂无签名' }}</p>
      </div>

      <!-- 统计信息 -->
      <div class="stats-section">
        <div class="stat-item">
          <div class="stat-label">经验</div>
          <div class="stat-value">{{ userData.experience || '暂无' }}</div>
        </div>
        <div class="stat-divider"></div>
        <div class="stat-item">
          <div class="stat-label">账户</div>
          <div class="stat-value">{{ userData.account || '暂无' }}</div>

        </div>
        <div class="stat-divider"></div>
        <div class="stat-item">
          <div class="stat-label">用户ID</div>
          <div class="stat-value">{{ userData.userId || '暂无' }}</div>

        </div>
      </div>
    </div>

    <!-- 详细信息 -->
    <div class="details-section">
      <h3 class="section-title">详细信息</h3>
      <div class="details-grid">
        <div class="detail-item">
          <label class="detail-label">邮箱</label>
          <div class="detail-value">{{ userData.email || '暂无' }}</div>
        </div>
        <div class="detail-item">
          <label class="detail-label">电话</label>
          <div class="detail-value">{{ userData.phone || '暂无' }}</div>
        </div>
        <div class="detail-item">
          <label class="detail-label">账户</label>
          <div class="detail-value">{{ userData.account || '暂无' }}</div>
        </div>
        <div class="detail-item">
          <label class="detail-label">用户ID</label>
          <div class="detail-value">{{ userData.userId || '暂无' }}</div>
        </div>
      </div>
    </div>

    <!-- 操作按钮 -->
    <div class="action-section">
      <button class="btn edit-btn">编辑资料</button>
      <button class="btn change-password-btn">修改密码</button>
      <button class="btn logout-btn" @click="handleLogout">退出登录</button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessageBox, ElMessage } from 'element-plus';
import { logout as logoutApi } from '@/api/auth';

// 接收父组件传递的用户数据
const props = defineProps({
  userData: {
    type: Object,
    default: () => ({
      image: '',
      username: '',
      nickname: '',
      email: '',
      phone: '',
      account: '',
      experience: '',
      signature: '',
      userId: ''
    })
  }
});

// 使用父组件传递的数据
const userData = ref(props.userData);

const router = useRouter();

// 退出登录
const handleLogout = () => {
  ElMessageBox.confirm('您确定要退出登录吗？', '确认操作', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      // 调用后端退出API
      await logoutApi();

      // 清除所有登录相关存储
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      localStorage.removeItem('rememberedUsername');

      // 跳转到登录页面
      router.push('/login');

      ElMessage.success('退出登录成功');
    } catch (error) {
      console.error('退出登录失败:', error);
      // 即使API调用失败，也执行本地退出逻辑
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      localStorage.removeItem('rememberedUsername');
      router.push('/login');
      ElMessage.success('已退出登录');
    }
  }).catch(() => {
    // 用户点击"取消"，不执行任何操作
    console.log('退出操作已取消');
  });
};

// 页面加载时可以从后端获取最新数据
onMounted(() => {
  // 这里可以添加获取用户信息的API调用
  console.log('UserInfo mounted');
});
</script>

<style scoped>
.user-info-container {
  min-height: 100vh;
  background-color: #f5f7fa;
  padding: 24px;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

.page-header {
  text-align: center;
  margin-bottom: 32px;
}

.page-header h1 {
  margin: 0 0 8px 0;
  font-size: 28px;
  font-weight: 600;
  color: #333;
}

.page-header p {
  margin: 0;
  font-size: 16px;
  color: #666;
}

.profile-card {
  background: white;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  margin-bottom: 24px;
}

.avatar-section {
  text-align: center;
  padding: 32px 24px;
  border-bottom: 1px solid #f0f0f0;
}

.avatar-container {
  position: relative;
  display: inline-block;
  margin-bottom: 16px;
}

.avatar {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  object-fit: cover;
  border: 4px solid #fff;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.avatar-overlay {
  position: absolute;
  bottom: 0;
  right: 0;
  background: #4CAF50;
  border-radius: 50%;
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 3px solid #fff;
  cursor: pointer;
  transition: all 0.3s ease;
}

.avatar-overlay:hover {
  transform: scale(1.1);
  background: #45a049;
}

.edit-avatar-btn {
  background: transparent;
  border: none;
  cursor: pointer;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
}

.icon {
  width: 18px;
  height: 18px;
  fill: currentColor;
}

.nickname {
  margin: 0 0 4px 0;
  font-size: 20px;
  font-weight: 600;
  color: #333;
}

.username {
  margin: 0 0 8px 0;
  font-size: 14px;
  color: #999;
}

.signature {
  margin: 0;
  font-size: 14px;
  color: #666;
  max-width: 400px;
  margin: 0 auto;
  line-height: 1.4;
}

.stats-section {
  display: flex;
  justify-content: space-around;
  padding: 24px;
  background: #fafafa;
}

.stat-item {
  text-align: center;
  flex: 1;
}

.stat-value {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  color: #999;
}

.stat-divider {
  width: 1px;
  background: #e0e0e0;
  margin: 0 16px;
}

.details-section {
  background: white;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  padding: 24px;
  margin-bottom: 24px;
}

.section-title {
  margin: 0 0 20px 0;
  font-size: 18px;
  font-weight: 600;
  color: #333;
  border-bottom: 2px solid #4CAF50;
  padding-bottom: 12px;
}

.details-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 16px;
}

.detail-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.detail-label {
  font-size: 14px;
  color: #999;
  font-weight: 500;
}

.detail-value {
  font-size: 16px;
  color: #333;
  font-weight: 500;
  padding: 12px;
  background: #f9f9f9;
  border-radius: 8px;
  border: 1px solid #f0f0f0;
  transition: all 0.3s ease;
}

.detail-value:hover {
  border-color: #4CAF50;
  box-shadow: 0 2px 8px rgba(76, 175, 80, 0.1);
}

.action-section {
  display: flex;
  gap: 16px;
  justify-content: center;
}

.btn {
  padding: 12px 24px;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  min-width: 120px;
}

.edit-btn {
  background: #4CAF50;
  color: white;
}

.edit-btn:hover {
  background: #45a049;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(76, 175, 80, 0.3);
}

.change-password-btn {
  background: #2196F3;
  color: white;
}

.change-password-btn:hover {
  background: #0b7dda;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(33, 150, 243, 0.3);
}

.logout-btn {
  background: #f44336;
  color: white;
}

.logout-btn:hover {
  background: #da190b;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(244, 67, 54, 0.3);
}

@media (max-width: 768px) {
  .user-info-container {
    padding: 16px;
  }

  .avatar {
    width: 100px;
    height: 100px;
  }

  .stats-section {
    flex-direction: column;
    gap: 16px;
  }

  .stat-divider {
    width: 100%;
    height: 1px;
    margin: 0;
  }

  .details-grid {
    grid-template-columns: 1fr;
  }

  .action-section {
    flex-direction: column;
  }

  .btn {
    width: 100%;
  }
}
</style>