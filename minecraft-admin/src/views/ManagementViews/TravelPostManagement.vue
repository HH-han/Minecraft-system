<template>
  <div class="management-page-background">
    <div class="container-management">
      <!-- 操作栏 -->
      <div class="action-bar">
        <div class="search-bar">
          <div class="search-box-management">
            <input type="text" v-model="searchKeyword" placeholder="输入卡片ID或标题搜索" class="search-input-management" />
          </div>
          <button class="btn search-btn" @click="handleSearch">搜索</button>
          <button class="btn delete-btn" @click="handleReset">批量删除</button>
        </div>
        <div class="operate-bar">
          <button class="btn import-btn" @click="handleImport">Excel数据导入导出</button>
          <button class="btn add-btn" @click="showAddDialog">新增发布</button>
        </div>
      </div>
      <!-- 数据表格 -->
      <div class="data-table-container">
        <div class="data-table-wrapper">
          <table class="data-table">
            <thead>
              <tr>
                <th v-for="col in columns" :key="col.key">{{ col.title }}</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="card in filteredCards" :key="card.id">
                <td>
                  <input type="checkbox" :checked="card.checked" @change="handleCheck(card)" class="ui-checkbox" />
                </td>
                <td>{{ card.id }}</td>
                <td>{{ card.username }}</td>
                <td>{{ card.title }}</td>
                <td>
                  <img v-if="card.images && card.images.length > 0" :src="card.images" alt="景点图片"
                    style="width: 35px; height: 35px;" @click="triggerFileInput(card)">
                  <span v-else>暂无图片</span>
                </td>
                <td>{{ card.content.substring(0, 15) }}</td>
                <td>{{ card.location }}</td>
                <td>{{ card.tags }}</td>
                <td>{{ formatDate(card.createdAt) }}</td>
                <td>{{ formatDate(card.updatedAt) }}</td>
                <td>{{ card.date }}</td>
                <td class="table-btn-display">
                  <button class="btn details-btn" @click="showEditDialog(card)">详情</button>
                  <button class="btn edit-btn" @click="showEditDialog(card)">编辑</button>
                  <button class="btn delete-btn" @click="handleDelete(card.id)">删除</button>
                </td>
              </tr>
            </tbody>
          </table>
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
                title="发布"
                :isEdit="isEditing"
                :fields="formFields"
                :initialData="formData"
                :showImageUpload="true"
                imageUploadLabel="上传图片"
                recommendedSize="推荐尺寸：1200×800px"
                imageField="images"
                :validateFn="validateForm"
                :submitFn="handleSubmit"
                @error="handleError"
            >
                <template #custom-fields>
                    <div class="form-group">
                        <label>用户名:</label>
                        <div class="user-select-container">
                            <button class="user-select-btn" @click="toggleUserList">
                                {{ formData.username || '选择用户' }}
                                <span class="arrow">▼</span>
                            </button>
                            <div v-if="showUserList" class="user-list-wrapper">
                                <div class="user-search-box">
                                    <input type="text" v-model="userSearchKeyword" placeholder="搜索用户..." class="user-search-input" />
                                </div>
                                <div class="user-list">
                                    <div v-for="user in filteredUsers" :key="user.id" class="user-item" @click="selectUser(user)">
                                        <div class="user-username">{{ user.username }}</div>
                                        <div class="user-details">
                                            <span class="user-phone">{{ user.phone || '暂无电话' }}</span>
                                            <span class="user-email">{{ user.email || '暂无邮箱' }}</span>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </template>
            </FormDialog>

      <!-- 删除提示框组件 -->
      <DeleteConfirmation v-if="isDeletePromptVisible" @close="closeDeletePrompt" @confirm="confirmDelete" />
      <!-- 自定义提示框组件 -->
      <ToastType v-if="showToast" :toastMessage="toastMessage" :toastType="toastType" />
    </div>
  </div>

</template>

<script setup>

import { ref, computed, onMounted, onUnmounted } from 'vue';
import request from '@/utils/request';
import FormDialog from '@/components/FormDialog.vue';
import DeleteConfirmation from '@/components/PromptComponent/DeleteConfirmation.vue';
import ToastType from '@/components/PromptComponent/ToastType.vue';

const columns = [
  { key: 'checked', title: '多选' },
  { key: 'id', title: '景点ID' },
  { key: 'username', title: '用户名' },
  { key: 'title', title: '景点标题' },
  { key: 'images', title: '景点图片' },
  { key: 'content', title: '发布内容' },
  { key: 'location', title: '发布地点' },
  { key: 'tages', title: '标签' },
  { key: 'createdAt', title: '创建时间' },
  { key: 'updatedAt', title: '更新时间' },
  { key: 'date', title: '日期' },
];
const showToast = ref(false);
const toastMessage = ref('');
const toastType = ref('success');
const cards = ref([]);
const searchKeyword = ref('');
const showDialog = ref(false);
const isEditing = ref(false);
const formData = ref({
  id: '',
  title: '',
  content: '',
  images: '',
  location: '',
  createdAt: '',
  updatedAt: '',
});

// 表单字段配置
const formFields = [
  [
    { name: 'title', label: '发布标题', type: 'text', required: true, placeholder: '请输入发布标题' },
    { name: 'location', label: '发布地点', type: 'text', required: true, placeholder: '请输入发布地点' },
  ],
  [
    { name: 'content', label: '发布内容', type: 'textarea', required: true, rows: 3, placeholder: '请输入发布内容' },
  ],
];

// 格式化日期显示
const formatDate = (date) => {
  if (!date) return '未知日期';
  const options = { year: 'numeric', month: 'long', day: 'numeric' };
  return new Intl.DateTimeFormat('zh-CN', options).format(new Date(date));
};

// 搜索功能
const filteredCards = computed(() => {
  const keyword = searchKeyword.value.toLowerCase();
  return cards.value.filter(
    (card) =>
      String(card.id).includes(keyword) ||
      card.title.toLowerCase().includes(keyword)
  );
});

// 分页功能
// 分页相关变量
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);

// 分页处理函数
const handleSizeChange = (newSize) => {
  pageSize.value = newSize;
  currentPage.value = 1;
  fetchScenic();
};

const handleCurrentChange = (newPage) => {
  currentPage.value = newPage;
  fetchScenic();
};
// 获取数据
const fetchScenic = async () => {
  try {
    const params = {
      page: currentPage.value,
      pageSize: pageSize.value,
      keyword: searchKeyword.value
    };
    const response = await request.get('/card/list', { params });
    cards.value = response.data.list.map(card => ({
      ...card,
      images: typeof card.image === 'string' ? JSON.parse(card.images) : card.images
    }));
    total.value = response.data.total;
  } catch (error) {
    console.error('获取订单数据失败:', error);
  }
};
const users = ref([]);

// 获取用户数据
const fetchUsers = async () => {
  try {
    const params = {
      page: currentPage.value,
      pageSize: pageSize.value,
      keyword: searchKeyword.value
    };
    const response = await request.get('/card/users', { params });
    users.value = response.data.list;
    total.value = response.data.total;
  } catch (error) {
    console.error('获取用户数据失败:', error);
  }
};
// 搜索用户
const showUserList = ref(false);
const userSearchKeyword = ref('');

const toggleUserList = () => {
  showUserList.value = !showUserList.value;
};

const selectUser = (user) => {
  formData.value.username = user.username;
  showUserList.value = false;
};

const filteredUsers = computed(() => {
  if (!userSearchKeyword.value) return users.value;

  const keyword = userSearchKeyword.value.toLowerCase();
  return users.value.filter(user =>
    user.username.toLowerCase().includes(keyword) ||
    (user.phone && user.phone.includes(keyword)) ||
    (user.email && user.email.toLowerCase().includes(keyword))
  );
});

// 点击外部关闭用户列表
const handleClickOutside = (event) => {
  if (!event.target.closest('.user-select-container')) {
    showUserList.value = false;
  }
};

onMounted(() => {
  document.addEventListener('click', handleClickOutside);
});

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside);
});
// 显示新增对话框
const showAddDialog = () => {
  isEditing.value = false;
  formData.value = {
    id: '',
    title: '',
    content: '',
    images: '',
    location: '',
    createdAt: '',
    updatedAt: '',
  };
  showDialog.value = true;
};

// 显示编辑对话框
const showEditDialog = (card) => {
  isEditing.value = true;
  formData.value = { ...card };
  showDialog.value = true;
};

// 表单验证
const validateForm = (data, isEdit) => {
  if (!data.title) {
    return '标题不能为空';
  }
  if (!data.username) {
    return '请选择用户';
  }
  return null;
};

// 提交表单
const handleSubmit = async (data, isEdit) => {
  if (isEdit) {
    data.updatedAt = new Date().toISOString();
    await request.put(`/card/${data.id}`, data);
    showToastMessage('更新景点成功');
  } else {
    data.createdAt = new Date().toISOString();
    await request.post('/card', data);
    showToastMessage('新增景点成功');
  }
  await fetchScenic();
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

// 删除卡片
const isDeletePromptVisible = ref(false);
const deleteCardId = ref(null);

const handleDelete = (id) => {
  deleteCardId.value = id;
  isDeletePromptVisible.value = true;
};

const closeDeletePrompt = () => {
  isDeletePromptVisible.value = false;
  deleteCardId.value = null;
};
//删除
const confirmDelete = async () => {
  if (deleteCardId.value) {
    try {
      await request.delete(`/card/${deleteCardId.value}`);
      await fetchScenic();
      closeDeletePrompt();
      showToastMessage('删除成功');
    } catch (error) {
      console.error('删除失败:', error);
      showToastMessage('删除失败', 'error');

    } finally {
      closeDeletePrompt();
    }
  }
};

onMounted(
  fetchScenic(),
  fetchUsers()
);


</script>

<style scoped>
@import '@/css/Management/BackgroundManagement.css';
</style>