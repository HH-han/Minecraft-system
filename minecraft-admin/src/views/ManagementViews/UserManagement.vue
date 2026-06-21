<template>
  <div class="management-page-background">
    <div class="container-management">
      <!-- 列表栏 -->
      <div class="search-bar">
        <div class="search-container">
          <div class="search-title">
            <i class="fas fa-search"></i>
            <h2>搜索条件</h2>
          </div>

          <form class="search-form" @submit.prevent="searchUsers">
            <!-- 搜索框 -->
            <div class="search-box-management-container">
              <!-- ID -->
              <div class="search-box-management">
                <label for="id" class="required-label">ID:</label>
                <input type="number" id="id" v-model="searchParams.id" class="search-input-management"
                  placeholder="输入用户ID">
              </div>
              <!-- 用户名 -->
              <div class="search-box-management">
                <label for="username" class="required-label">用户名:</label>
                <input type="text" id="username" v-model="searchParams.username" class="search-input-management"
                  placeholder="输入用户名">
              </div>
              <!-- 昵称 -->
              <div class="search-box-management">
                <label for="nickname" class="required-label">昵称:</label>
                <input type="text" id="nickname" v-model="searchParams.nickname" class="search-input-management"
                  placeholder="输入昵称">
              </div>
              <!-- 邮箱 -->
              <div class="search-box-management">
                <label for="email" class="required-label">邮箱:</label>
                <input type="email" id="email" v-model="searchParams.email" class="search-input-management"
                  placeholder="输入邮箱地址">
              </div>
              <!-- 手机号 -->
              <div class="search-box-management">
                <label for="phone" class="required-label">手机号:</label>
                <input type="tel" id="phone" v-model="searchParams.phone" class="search-input-management"
                  placeholder="输入手机号码">
              </div>
              <!-- 用户ID -->
              <div class="search-box-management">
                <label for="userID" class="required-label">用户ID:</label>
                <input type="number" id="userID" v-model="searchParams.userID" class="search-input-management"
                  placeholder="输入用户ID">
              </div>

            </div>
            <!-- 搜索按钮 -->
            <div class="search-box-management-btn">
              <button type="button" class="btn reset-btn" @click="resetForm">
                <i class="fas fa-redo"></i> 重置
              </button>
              <button type="submit" class="btn search-btn">
                <i class="fas fa-search"></i> 搜索
              </button>
            </div>
          </form>
        </div>
      </div>
      <!-- 操作栏 -->
      <div class="action-bar">
        <div class="operate-bar">
          <button class="btn batchlogin-btn" @click="Batchlogin">批量登录</button>
          <button class="btn batchpermissions-btn" @click="batchpermissions">批量权限</button>
          <button class="btn delete-btn" @click="handleReset">批量删除</button>
          <button class="btn import-btn" @click="handleImport">Excel数据导入导出</button>
          <button class="btn add-btn" @click="showAddDialog">新增用户</button>
        </div>
      </div>
      <!-- 数据表格 -->
      <div class="results-container">
        <div class="results-header">
          <h2>搜索结果</h2>
          <div class="results-count" v-if="!loading && !error">
            共找到 {{ users.length }} 个用户
          </div>
        </div>

        <div v-if="loading" class="loading">
          <div class="spinner"></div>
          <p>正在搜索用户，请稍候...</p>
        </div>

        <div v-else-if="error" class="error-message">
          <i class="fas fa-exclamation-circle"></i>
          <span>{{ error }}</span>
        </div>

        <div v-else-if="users.length === 0" class="no-results">
          <i class="fas fa-users"></i>
          <h3>未找到用户</h3>
          <p>请尝试调整搜索条件后重新搜索</p>
        </div>

        <div v-else class="data-table-container">
          <div class="data-table-wrapper">
            <table class="data-table">
              <thead>
                <tr>
                  <th v-for="col in columns" :key="col.key">{{ col.title }}</th>
                  <th>操作</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="user in filteredUsers" :key="user.id">
                  <td>
                    <input type="checkbox" :checked="user.checked" @change="handleCheck(user)" class="ui-checkbox" />
                  </td>
                  <td>{{ user.id }}</td>
                  <td>
                    <img :src="user.avatar" alt="头像" style="width: 35px; height: 35px;" @click="triggerFileInput(user)">
                  </td>
                  <td>{{ user.account }}</td>
                  <td>{{ user.username }}</td>
                  <td>{{ user.email }}</td>
                  <td>{{ user.phone }}</td>
                  <td>{{ user.nickname ? user.nickname : '未设置' }}</td>
                  <td>{{ user.signature ? user.signature.substring(0, 10) : '未设置' }}...</td>
                  <td>{{ user.experience ? user.experience.substring(0, 15) : '未设置' }}...</td>
                  <td>{{ user.bio }}</td>
                  <td>{{ user.age }}</td>
                  <td>{{ user.gender === "1" ? '男' : user.gender === "0" ? '女' : '未知' }}</td>
                  <td>{{ user.hobbies ? user.hobbies : '未设置' }}</td>
                  <td>{{ user.occupation ? user.occupation : '未设置' }}</td>
                  <td>{{ formatDate(user.createTime) }}</td>
                  <td>{{ formatDate(user.updateTime) }}</td>
                  <td>
                    <label class="switch">
                      <input type="checkbox" :checked="user.permissions === '0'" @change="togglePermission(user)">
                      <span class="slider"
                        :class="{ 'green': user.permissions === '0', 'red': user.permissions !== '0' }"></span>
                      <span class="knob"></span>
                    </label>
                  </td>
                  <td>
                    <label class="switch">
                      <input type="checkbox" :checked="user.status === 1" @change="toggleStatus(user)">
                      <span class="slider" :class="{ 'green': user.status === 1, 'red': user.status !== 1 }"></span>
                      <span class="knob"></span>
                    </label>
                  </td>
                  <td class="table-btn-display">
                    <button class="btn details-btn" @click="showUserDetails(user)">详情</button>
                    <button class="btn edit-btn" @click="showEditDialog(user)">编辑</button>
                    <button class="btn delete-btn" @click="handleDelete(user.id)">删除</button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
      <!-- 分页器 -->
      <div class="block">
        <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page="currentPage"
          :page-sizes="[10, 20, 50, 100]" :page-size="pageSize" layout="total, sizes, prev, pager, next, jumper"
          :total="total">
        </el-pagination>
      </div>

      <!-- 通用新增/编辑弹窗 -->
      <FormDialog
        v-model:visible="showDialog"
        title="用户"
        :isEdit="isEditing"
        :fields="formFields"
        :initialData="formData"
        :showImageUpload="true"
        imageUploadLabel="上传头像"
        recommendedSize="推荐尺寸：200×200px"
        imageField="avatar"
        :validateFn="validateForm"
        :submitFn="handleSubmit"
        @error="handleError"
      />

      <!-- 详情弹窗 -->
      <div class="user-detail-container" v-if="showDetailsUser" @click.self="closeDialog">
        <!-- 主卡片 -->
        <div class="user-detail-card" @click.stop>
          <!-- 关闭按钮 -->
          <button class="close-btn" @click="closeDialog">
            <i class="fas fa-times"></i>
          </button>

          <!-- 用户头像区 -->
          <div class="user-avatar-section">
            <div class="avatar-wrapper">
              <img :src="userDetail.avatar" alt="用户头像" class="user-avatar">
              <div class="avatar-border"></div>
            </div>
            <h2 class="user-name">{{ userDetail.username }}</h2>
            <p class="user-nickname">{{ userDetail.nickname }}</p>
            <div class="user-tags">
              <span class="tag">VIP</span>
              <span class="tag">活跃用户</span>
              <span class="tag">注册于 {{ formatDate(userDetail.createTime) }}</span>
            </div>
          </div>

          <!-- 用户详细信息 -->
          <div class="user-info-section">
            <div class="info-grid">
              <!-- 基本信息 -->
              <div class="info-card">
                <div class="info-header">
                  <i class="fas fa-id-card"></i>
                  <h3>基本信息</h3>
                </div>
                <div class="info-content">
                  <div class="info-item">
                    <span class="info-label">用户ID:</span>
                    <span class="info-value">{{ userDetail.id }}</span>
                  </div>
                  <div class="info-item">
                    <span class="info-label">用户名:</span>
                    <span class="info-value">{{ userDetail.username }}</span>
                  </div>
                  <div class="info-item">
                    <span class="info-label">昵称:</span>
                    <span class="info-value">{{ userDetail.nickname }}</span>
                  </div>
                </div>
              </div>

              <!-- 联系信息 -->
              <div class="info-card">
                <div class="info-header">
                  <i class="fas fa-envelope"></i>
                  <h3>联系信息</h3>
                </div>
                <div class="info-content">
                  <div class="info-item">
                    <span class="info-label">电子邮箱:</span>
                    <span class="info-value">{{ userDetail.email }}</span>
                  </div>
                  <div class="info-item">
                    <span class="info-label">联系电话:</span>
                    <span class="info-value">{{ userDetail.phone }}</span>
                  </div>
                </div>
              </div>

              <!-- 签名 -->
              <div class="info-card signature-card">
                <div class="info-header">
                  <i class="fas fa-quote-left"></i>
                  <h3>个性签名</h3>
                </div>
                <div class="info-content">
                  <p class="signature-text">{{ userDetail.signature || '暂无签名' }}</p>
                </div>
              </div>

              <!-- 经历 -->
              <div class="info-card experience-card">
                <div class="info-header">
                  <i class="fas fa-briefcase"></i>
                  <h3>个人经历</h3>
                </div>
                <div class="info-content">
                  <p class="experience-text">{{ userDetail.experience || '暂无经历信息' }}</p>
                </div>
              </div>

              <!-- 时间信息 -->
              <div class="info-card timeline-card">
                <div class="info-header">
                  <i class="fas fa-clock"></i>
                  <h3>时间信息</h3>
                </div>
                <div class="info-content">
                  <div class="timeline">
                    <div class="timeline-item">
                      <span class="timeline-label">注册时间:</span>
                      <span class="timeline-value">{{ formatDate(userDetail.createTime) }}</span>
                    </div>
                    <div class="timeline-item">
                      <span class="timeline-label">最后更新:</span>
                      <span class="timeline-value">{{ formatDate(userDetail.updateTime) }}</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <!-- 操作按钮 -->
          <div class="action-buttons">
            <button class="action-btn message-btn" @click="sendMessage">
              <i class="fas fa-paper-plane"></i> 发送消息
            </button>
          </div>
        </div>
      </div>

      <!-- 删除提示框组件 -->
      <DeleteConfirmation v-if="isDeletePromptVisible" @close="closeDeletePrompt" @confirm="confirmDelete" />
      <!-- 自定义提示框组件 -->
      <ToastType v-if="showToast" :toastMessage="toastMessage" :toastType="toastType" />

      <!-- 导入导出Excel -->
      <div class="app-container" v-if="showExcel">
        <ExcelImportExportAPI @import-complete="handleImportComplete" @export-complete="handleExportComplete"
          @import-error="handleImportError" @export-error="handleExportError" @close="handleCloseImportExport"
          :export-api-url="exportApiUrl" :import-api-url="importApiUrl" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, reactive } from 'vue';
import request from '@/utils/request';
import { getAllUsers, deleteUser, uploadAvatar, updateUserInfo, createUser } from '@/api/user';
import FormDialog from '@/components/FormDialog.vue';
import ExcelImportExportAPI from '@/components/DisplayBox/ExcelImportExportAPI.vue';
import DeleteConfirmation from '@/components/PromptComponent/DeleteConfirmation.vue';
import ToastType from '@/components/PromptComponent/ToastType.vue';

const columns = [
  { key: 'checked', title: '多选' },
  { key: 'id', title: 'ID' },
  { key: 'avatar', title: '头像' },
  { key: 'account', title: '账号' },
  { key: 'username', title: '用户名' },
  { key: 'email', title: '邮箱' },
  { key: 'phone', title: '手机号' },
  { key: 'nickname', title: '昵称' },
  { key: 'signature', title: '签名' },
  { key: 'experience', title: '经验' },
  { key: 'bio', title: '简介' },
  { key: 'age', title: '年龄' },
  { key: 'gender', title: '性别' },
  { key: 'hobbies', title: '爱好' },
  { key: 'occupation', title: '职业' },
  { key: 'createTime', title: '创建时间' },
  { key: 'updateTime', title: '更新时间' },
  { key: 'permissions', title: '管理员权限(关闭-开启)' },
  { key: 'status', title: '登录状态(禁止-启用)' },
];

const showToast = ref(false);
const toastMessage = ref('');
const toastType = ref('success');
const users = ref([]);
const showDialog = ref(false);
const showExcel = ref(false);
const isEditing = ref(false);
const showDetailsUser = ref(false);

// 表单数据
const formData = ref({
  id: null,
  username: '',
  email: '',
  password: '',
  phone: '',
  nickname: '',
  avatar: '',
  account: '',
  signature: '',
  experience: '',
  bio: '',
  age: null,
  gender: '',
  hobbies: '',
  occupation: '',
  createTime: new Date(),
  updateTime: new Date(),
});

// 表单字段配置
const formFields = [
  [
    { name: 'account', label: '账号', type: 'text', required: true, placeholder: '请输入账号' },
    { name: 'username', label: '用户名', type: 'text', required: true, placeholder: '请输入用户名' },
    { name: 'email', label: '邮箱', type: 'email', required: true, placeholder: 'example@example.com' },
    { name: 'password', label: '密码', type: 'password', placeholder: '请输入密码（编辑时可不填）' },
  ],
  [
    { name: 'phone', label: '手机号', type: 'text', required: true, placeholder: '请输入手机号' },
    { name: 'nickname', label: '昵称', type: 'text', placeholder: '请输入昵称' },
    { name: 'signature', label: '签名', type: 'text', placeholder: '请输入签名' },
    { name: 'age', label: '年龄', type: 'number', min: 0, placeholder: '请输入年龄' },
  ],
  [
    { name: 'gender', label: '性别', type: 'select', placeholder: '请选择', options: [
      { value: '1', label: '男' },
      { value: '0', label: '女' },
    ]},
    { name: 'occupation', label: '职业', type: 'text', placeholder: '请输入职业' },
    { name: 'hobbies', label: '爱好', type: 'text', placeholder: '请输入爱好' },
  ],
  [
    { name: 'bio', label: '简介', type: 'textarea', fullWidth: true, rows: 3, placeholder: '请输入简介' },
  ],
  [
    { name: 'experience', label: '经验', type: 'textarea', fullWidth: true, rows: 3, placeholder: '请输入个人经历' },
  ],
];

// 详情弹窗
const userDetail = ref({
  avatar: '',
  username: '',
  nickname: '',
  createTime: null,
  id: '',
  email: '',
  phone: '',
  signature: '',
  experience: '',
  updateTime: null
});

// 格式化手机号显示
const formatPhone = (phone) => {
  return String(phone).replace(/(\d{3})(\d{4})(\d{4})/, '$1****$3');
};

// 格式化日期显示
const formatDate = (date) => {
  if (!date) return '未知日期';
  const options = { year: 'numeric', month: 'long', day: 'numeric' };
  return new Intl.DateTimeFormat('zh-CN', options).format(new Date(date));
};

// 搜索功能
const searchParams = reactive({
  id: null,
  username: '',
  nickname: '',
  email: '',
  phone: '',
  userID: null
});

const loading = ref(false);
const error = ref(null);

const searchUsers = async () => {
  loading.value = true;
  error.value = null;

  try {
    let filtered = users.value;

    if (searchParams.id) {
      filtered = filtered.filter(user => user.id === searchParams.id);
    }
    if (searchParams.username) {
      filtered = filtered.filter(user => user.username.includes(searchParams.username));
    }
    if (searchParams.nickname) {
      filtered = filtered.filter(user => user.nickname.includes(searchParams.nickname));
    }
    if (searchParams.email) {
      filtered = filtered.filter(user => user.email.includes(searchParams.email));
    }
    if (searchParams.phone) {
      filtered = filtered.filter(user => user.phone.includes(searchParams.phone));
    }
    if (searchParams.userID) {
      filtered = filtered.filter(user => user.id === searchParams.userID);
    }

    users.value = filtered;
    total.value = filtered.length;
  } catch (err) {
    error.value = '搜索用户时发生错误: ' + err.message;
    console.error('搜索错误:', err);
  } finally {
    loading.value = false;
  }
};

const resetForm = () => {
  searchParams.id = null;
  searchParams.username = '';
  searchParams.nickname = '';
  searchParams.email = '';
  searchParams.phone = '';
  searchParams.userID = null;
  fetchUsers();
  error.value = null;
};

// 处理复选框选择
const handleCheck = (user) => {
  user.checked = !user.checked;
};

const filteredUsers = computed(() => {
  return users.value;
});

// 分页功能
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);

const handleSizeChange = (newSize) => {
  pageSize.value = newSize;
  currentPage.value = 1;
  fetchUsers();
};

const handleCurrentChange = (newPage) => {
  currentPage.value = newPage;
  fetchUsers();
};

// 获取用户数据
const fetchUsers = async () => {
  try {
    const response = await getAllUsers();
    users.value = response.data || [];
    total.value = users.value.length;
  } catch (error) {
    console.error('获取用户数据失败:', error);
    users.value = [];
    total.value = 0;
  }
};

// 显示新增对话框
const showAddDialog = () => {
  isEditing.value = false;
  formData.value = {
    id: null,
    account: '',
    username: '',
    email: '',
    password: '',
    phone: '',
    nickname: '',
    signature: '',
    experience: '',
    bio: '',
    age: null,
    gender: '',
    hobbies: '',
    occupation: '',
    avatar: '',
    createTime: new Date(),
    updateTime: new Date(),
  };
  showDialog.value = true;
};

// 显示编辑对话框
const showEditDialog = (user) => {
  isEditing.value = true;
  formData.value = {
    id: user.id,
    account: user.account,
    username: user.username,
    email: user.email,
    password: '', // 编辑时密码为空，不修改密码
    phone: user.phone,
    nickname: user.nickname || '',
    signature: user.signature || '',
    experience: user.experience || '',
    bio: user.bio || '',
    age: user.age || null,
    gender: user.gender || '',
    hobbies: user.hobbies || '',
    occupation: user.occupation || '',
    avatar: user.avatar || '',
    createTime: user.createTime,
    updateTime: new Date(),
  };
  showDialog.value = true;
};

// 显示用户详情
const showUserDetails = (user) => {
  userDetail.value = { ...user };
  showDetailsUser.value = true;
};

// 表单验证
const validateForm = (data, isEdit) => {
  if (!data.username || !data.email || !data.phone) {
    return '请填写所有必填字段';
  }
  if (!isEdit && !data.password) {
    return '请输入密码';
  }
  return null;
};

// 提交表单
const handleSubmit = async (data) => {
  const submitData = { ...data };

  if (isEditing.value) {
    submitData.updateTime = new Date();
    // 编辑时，如果密码为空，则不包含密码字段
    if (!submitData.password) {
      delete submitData.password;
    }
    await updateUserInfo(submitData);
    showToastMessage('更新用户成功');
  } else {
    await createUser(submitData);
    showToastMessage('新增用户成功');
  }
  await fetchUsers();
};

// 处理错误
const handleError = (error) => {
  showToastMessage(error.message || '操作失败', 'error');
};

// 显示提示消息的方法
const showToastMessage = (message, type = 'success') => {
  toastMessage.value = message;
  toastType.value = type;
  showToast.value = true;
  setTimeout(() => {
    showToast.value = false;
  }, 3000);
};

// 删除用户
const isDeletePromptVisible = ref(false);
const deleteUserId = ref(null);

const handleDelete = (id) => {
  deleteUserId.value = id;
  isDeletePromptVisible.value = true;
};

const closeDeletePrompt = () => {
  isDeletePromptVisible.value = false;
  deleteUserId.value = null;
};

const confirmDelete = async () => {
  if (deleteUserId.value) {
    try {
      await deleteUser(deleteUserId.value);
      showToastMessage('删除用户成功');
      await fetchUsers();
    } catch (error) {
      console.error('删除失败:', error);
      showToastMessage('删除用户失败', 'error');
    } finally {
      closeDeletePrompt();
    }
  }
};

// 更改权限
const togglePermission = async (user) => {
  try {
    const newPermission = user.permissions === '0' ? '1' : '0';
    user.permissions = newPermission;
    await updateUserInfo(user);
    showToastMessage('修改权限成功');
  } catch (error) {
    console.error('修改权限失败:', error);
    showToastMessage('修改权限失败', 'error');
  }
};

// 更改状态
const toggleStatus = async (user) => {
  try {
    const newStatus = user.status === 1 ? 0 : 1;
    user.status = newStatus;
    await updateUserInfo(user);
    showToastMessage('修改状态成功');
  } catch (error) {
    console.error('修改状态失败:', error);
    showToastMessage('修改状态失败', 'error');
  }
};

// 关闭对话框
const closeDialog = () => {
  showDialog.value = false;
  showDetailsUser.value = false;
};

// 触发文件输入框
const triggerFileInput = (user) => {
  if (user.avatar) {
    window.open(user.avatar, '_blank');
  }
};

// 批量登录
const Batchlogin = () => {
  const selectedUsers = users.value.filter(user => user.checked);
  if (selectedUsers.length === 0) {
    showToastMessage('请先选择用户', 'error');
    return;
  }
  showToastMessage(`已选择 ${selectedUsers.length} 个用户进行批量登录`);
};

// 批量权限
const batchpermissions = () => {
  const selectedUsers = users.value.filter(user => user.checked);
  if (selectedUsers.length === 0) {
    showToastMessage('请先选择用户', 'error');
    return;
  }
  selectedUsers.forEach(user => {
    user.permissions = 1;
  });
  showToastMessage(`已为 ${selectedUsers.length} 个用户设置管理员权限`);
};

// 批量删除
const handleReset = async () => {
  const selectedUsers = users.value.filter(user => user.checked);
  if (selectedUsers.length === 0) {
    showToastMessage('请先选择用户', 'error');
    return;
  }

  try {
    for (const user of selectedUsers) {
      await deleteUser(user.id);
    }
    showToastMessage(`已删除 ${selectedUsers.length} 个用户`);
    await fetchUsers();
  } catch (error) {
    console.error('批量删除失败:', error);
    showToastMessage('批量删除失败', 'error');
  }
};

// Excel 导入导出结果处理
const handleImport = (result) => {
  showExcel.value = true;
  console.log('打开Excel导入组件:', showExcel.value);
};

const exportApiUrl = 'http://localhost:2025/api/public/user/export';
const importApiUrl = '/api/public/user/import';

const handleImportComplete = (result) => {
  console.log('导入完成:', result);
};

const handleExportComplete = (result) => {
  console.log('导出完成:', result);
};

const handleImportError = (error) => {
  console.error('导入错误:', error);
};

const handleExportError = (error) => {
  console.error('导出错误:', error);
};

const handleCloseImportExport = () => {
  showExcel.value = false;
  console.log('关闭Excel导入组件:', showExcel.value);
};

// 生命周期钩子
onMounted(fetchUsers);
</script>

<style scoped>
@import '@/css/Management/BackgroundManagement.css';
</style>
