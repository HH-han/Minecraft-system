<template>
  <div class="management-page-background">
    <div class="container-management">
      <div class="action-bar">
        <div class="search-bar">
          <div class="search-box-management">
            <input type="text" v-model="searchKeyword" placeholder="输入景点ID或名称搜索" class="search-input-management" />
          </div>
          <button class="btn search-btn" @click="handleSearch">搜索</button>
          <button class="btn delete-btn" @click="handleBatchDelete">批量删除</button>
        </div>
        <button class="btn add-btn" @click="showAddDialog">新增景点</button>
      </div>

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
                <td>{{ card.name }}</td>
                <td>{{ card.price }}</td>
                <td>
                  <img :src="card.coverImage" alt="景点图片" style="width: 35px; height: 35px;" @click="triggerFileInput(card)">
                </td>
                <td>{{ card.description ? card.description.substring(0, 15) : '未设置' }}</td>
                <td>{{ card.city }}</td>
                <td>{{ card.province }}</td>
                <td>{{ card.rating }}</td>
                <td>{{ card.likeCount }}</td>
                <td>{{ card.collectCount }}</td>
                <td>{{ card.commentCount }}</td>
                <td>{{ card.season }}</td>
                <td>
                  <div class="ticket-info">
                    <div class="ticket-count">{{ card.tickets ? card.tickets.length : 0 }}种门票</div>
                    <div class="ticket-names" v-if="card.tickets && card.tickets.length > 0">
                      <span v-for="ticket in card.tickets.slice(0, 3)" :key="ticket.id" class="ticket-tag">{{ ticket.name }}</span>
                      <span v-if="card.tickets.length > 3" class="ticket-more">+{{ card.tickets.length - 3 }}</span>
                    </div>
                  </div>
                </td>
                <td>
                  <div class="facility-info">
                    <div class="facility-count">{{ card.facilities ? card.facilities.length : 0 }}项设施</div>
                    <div class="facility-names" v-if="card.facilities && card.facilities.length > 0">
                      <span v-for="facility in card.facilities.slice(0, 3)" :key="facility" class="facility-tag">{{ facility }}</span>
                      <span v-if="card.facilities.length > 3" class="facility-more">+{{ card.facilities.length - 3 }}</span>
                    </div>
                  </div>
                </td>
                <td>{{ formatDate(card.createTime) }}</td>
                <td class="table-btn-display">
                  <button class="btn details-btn" @click="showDetailsDialog(card)">详情</button>
                  <button class="btn edit-btn" @click="showEditDialog(card)">编辑</button>
                  <button class="btn delete-btn" @click="handleDelete(card.id)">删除</button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <div class="block">
        <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page="currentPage"
          :page-sizes="[10, 20, 50, 100]" :page-size="pageSize" layout="total, sizes, prev, pager, next, jumper"
          :total="total">
        </el-pagination>
      </div>

      <div v-if="showDialog" class="dialog-overlay" @click.self="closeDialog">
        <div class="dialog" @click.stop>
          <h2>{{ isEditing ? '编辑景点' : '新增景点' }}</h2>
          <form @submit.prevent="submitForm" class="form-container">
            <div class="form-group">
              <div class="image-upload-container">
                <div class="upload-header">
                  <h3>上传图片</h3>
                  <p>支持 JPG, PNG 格式，最大 5MB</p>
                </div>

                <div class="upload-area" @click="triggerFileInput" @dragover.prevent="dragOver = true"
                  @dragleave="dragOver = false" @drop.prevent="handleDrop" :class="{ 'drag-active': dragOver }">
                  <input type="file" ref="fileInput" @change="handleFileUpload" accept="image/*" class="file-input" />

                  <div class="upload-content">
                    <div class="upload-icon">
                      <svg viewBox="0 0 24 24">
                        <path d="M19,13H13V19H11V13H5V11H11V5H13V11H19V13Z" />
                      </svg>
                    </div>
                    <p class="upload-text">点击或拖拽文件到此处</p>
                    <p class="upload-hint">推荐尺寸：1200×800px</p>
                  </div>
                </div>

                <div class="preview-container" v-if="previewImage">
                  <div class="preview-card">
                    <img :src="previewImage" alt="预览图片" class="preview-image" />
                    <div class="preview-actions">
                      <button class="action-btn-image edit-btn-image" @click="triggerFileInput">
                        <svg viewBox="0 0 24 24">
                          <path
                            d="M20.71,7.04C21.1,6.65 21.1,6 20.71,5.63L18.37,3.29C18,2.9 17.35,2.9 16.96,3.29L15.12,5.12L18.87,8.87M3,17.25V21H6.75L17.81,9.93L14.06,6.18L3,17.25Z" />
                        </svg>
                      </button>
                      <button class="action-btn-image delete-btn-image" @click="removeImage">
                        <svg viewBox="0 0 24 24">
                          <path d="M19,4H15.5L14.5,3H9.5L8.5,4H5V6H19M6,19A2,2 0 0,0 8,21H16A2,2 0 0,0 18,19V7H6V19Z" />
                        </svg>
                      </button>
                    </div>
                    <div class="preview-footer">
                      <div class="file-info">
                        <span class="file-name">{{ fileName }}</span>
                        <span class="file-size">{{ fileSize }}</span>
                      </div>
                      <div class="upload-progress" v-if="uploading">
                        <div class="progress-bar" :style="{ width: progress + '%' }"></div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
            <div class="form-row">
              <div class="form-group">
                <label>景点名称:</label>
                <input v-model="formData.name" required />
              </div>
              <div class="form-group">
                <label>景点价格:</label>
                <input v-model="formData.price" required type="number" step="0.01" />
              </div>
              <div class="form-group">
                <label>城市:</label>
                <input v-model="formData.city" required />
              </div>
              <div class="form-group">
                <label>省份:</label>
                <input v-model="formData.province" required />
              </div>
            </div>
            <div class="form-row">
              <div class="form-group">
                <label>地址:</label>
                <input v-model="formData.address" required />
              </div>
              <div class="form-group">
                <label>描述:</label>
                <textarea v-model="formData.description" required></textarea>
              </div>
              <div class="form-group">
                <label>评分:</label>
                <input v-model="formData.rating" required type="number" min="0" max="5" step="0.1" />
              </div>
              <div class="form-group">
                <label>最佳季节:</label>
                <input v-model="formData.season" required />
              </div>
            </div>

            <div class="form-group">
              <label>选择景点设施:</label>
              <select v-model="selectedFacilities" multiple class="multiselect" size="4">
                <option v-for="facility in allFacilities" :key="facility" :value="facility">
                  {{ facility }}
                </option>
              </select>
              <p class="select-hint">按住 Ctrl 或 Cmd 键可多选</p>
            </div>

            <div class="form-group">
              <label>门票列表:</label>
              <div class="ticket-list">
                <div v-for="(ticket, index) in formData.tickets" :key="ticket.id || index" class="ticket-item">
                  <div class="ticket-header">
                    <span>门票 {{ index + 1 }}</span>
                    <button class="btn remove-ticket-btn" @click="removeTicket(index)">删除</button>
                  </div>
                  <div class="ticket-form">
                    <input v-model="ticket.name" placeholder="门票名称" class="ticket-input" />
                    <input v-model="ticket.description" placeholder="门票描述" class="ticket-input" />
                    <input v-model="ticket.price" placeholder="价格" type="number" step="0.01" class="ticket-input" />
                  </div>
                </div>
                <button class="btn add-ticket-btn" @click="addTicket">添加门票</button>
              </div>
            </div>

            <div class="dialog-buttons">
              <button type="button" class="btn cancel-btn" @click="closeDialog">取消</button>
              <button type="submit" class="btn confirm-btn">{{ isEditing ? '保存' : '创建' }}</button>
            </div>
          </form>
        </div>
      </div>

      <div v-if="showDetails" class="dialog-overlay" @click.self="closeDetailsDialog">
        <div class="dialog" @click.stop>
          <h2>景点详情</h2>
          <div class="details-container">
            <div class="detail-item">
              <label>景点名称:</label>
              <span>{{ selectedCard?.name }}</span>
            </div>
            <div class="detail-item">
              <label>价格:</label>
              <span>￥{{ selectedCard?.price }}</span>
            </div>
            <div class="detail-item">
              <label>城市:</label>
              <span>{{ selectedCard?.city }}</span>
            </div>
            <div class="detail-item">
              <label>省份:</label>
              <span>{{ selectedCard?.province }}</span>
            </div>
            <div class="detail-item">
              <label>地址:</label>
              <span>{{ selectedCard?.address }}</span>
            </div>
            <div class="detail-item">
              <label>描述:</label>
              <span>{{ selectedCard?.description }}</span>
            </div>
            <div class="detail-item">
              <label>评分:</label>
              <span>{{ selectedCard?.rating }}</span>
            </div>
            <div class="detail-item">
              <label>点赞数:</label>
              <span>{{ selectedCard?.likeCount }}</span>
            </div>
            <div class="detail-item">
              <label>收藏数:</label>
              <span>{{ selectedCard?.collectCount }}</span>
            </div>
            <div class="detail-item">
              <label>评论数:</label>
              <span>{{ selectedCard?.commentCount }}</span>
            </div>
            <div class="detail-item">
              <label>最佳季节:</label>
              <span>{{ selectedCard?.season }}</span>
            </div>
            <div class="detail-item">
              <label>创建时间:</label>
              <span>{{ formatDate(selectedCard?.createTime) }}</span>
            </div>
            <div class="detail-item" v-if="selectedCard?.tickets && selectedCard.tickets.length > 0">
              <label>门票类型:</label>
              <div class="detail-list">
                <div v-for="ticket in selectedCard.tickets" :key="ticket.id" class="detail-sub-item">
                  <span>{{ ticket.name }}</span>
                  <span>{{ ticket.description }}</span>
                  <span>￥{{ ticket.price }}</span>
                  <span v-if="ticket.rules">规则: {{ ticket.rules.join(', ') }}</span>
                </div>
              </div>
            </div>
            <div class="detail-item" v-if="selectedCard?.facilities && selectedCard.facilities.length > 0">
              <label>景点设施:</label>
              <div class="tags-container">
                <span v-for="facility in selectedCard.facilities" :key="facility" class="tag-item">
                  {{ facility }}
                </span>
              </div>
            </div>
          </div>
          <div class="dialog-buttons">
            <button type="button" class="btn cancel-btn" @click="closeDetailsDialog">关闭</button>
          </div>
        </div>
      </div>

      <DeleteConfirmation v-if="isDeletePromptVisible" @close="closeDeletePrompt" @confirm="confirmDelete" />
      <ToastType v-if="showToast" :toastMessage="toastMessage" :toastType="toastType" />
    </div>
  </div>
</template>

<script setup>

import { ref, computed, onMounted } from 'vue';
import { getAttractionList, getAttractionDetail, addAttraction, updateAttraction, deleteAttraction } from '@/api/attraction';
import DeleteConfirmation from '@/components/PromptComponent/DeleteConfirmation.vue';
import ToastType from '@/components/PromptComponent/ToastType.vue';

const columns = [
  { key: 'checked', title: '多选' },
  { key: 'id', title: '景点ID' },
  { key: 'name', title: '景点名称' },
  { key: 'price', title: '景点价格' },
  { key: 'coverImage', title: '景点图片' },
  { key: 'description', title: '景点描述' },
  { key: 'city', title: '城市' },
  { key: 'province', title: '省份' },
  { key: 'rating', title: '评分' },
  { key: 'likeCount', title: '点赞数' },
  { key: 'collectCount', title: '收藏数' },
  { key: 'commentCount', title: '评论数' },
  { key: 'season', title: '最佳季节' },
  { key: 'tickets', title: '门票' },
  { key: 'facilities', title: '设施' },
  { key: 'createTime', title: '创建时间' },
];

const allFacilities = [
  '免费WiFi', '停车场', '卫生间', '游船', '观光车',
  '导游服务', '餐饮服务', '休息区', '纪念品店', '医疗服务',
  '无障碍设施', '儿童游乐区', '宠物友好', '露营地', '停车场'
];

const showToast = ref(false);
const toastMessage = ref('');
const toastType = ref('success');
const cards = ref([]);
const searchKeyword = ref('');
const showDialog = ref(false);
const showDetails = ref(false);
const isEditing = ref(false);
const selectedCard = ref(null);
const formData = ref({
  id: null,
  name: '',
  city: '',
  province: '',
  address: '',
  coverImage: '',
  description: '',
  price: '',
  rating: 0,
  season: '',
  createTime: '',
  updateTime: '',
  collectCount: 0,
  commentCount: 0,
  likeCount: 0,
  images: null,
  tags: null,
  tickets: [],
  facilities: [],
});

const selectedFacilities = ref([]);

const formatDate = (date) => {
  if (!date) return '未知日期';
  const options = { year: 'numeric', month: 'long', day: 'numeric' };
  return new Intl.DateTimeFormat('zh-CN', options).format(new Date(date));
};

const addTicket = () => {
  formData.value.tickets.push({
    id: null,
    name: '',
    description: '',
    price: '',
    rules: []
  });
};

const removeTicket = (index) => {
  formData.value.tickets.splice(index, 1);
};

const filteredCards = computed(() => {
  const keyword = searchKeyword.value.toLowerCase();
  return (cards.value || []).filter(
    (card) =>
      String(card.id).includes(keyword) ||
      (card.name && card.name.toLowerCase().includes(keyword))
  );
});

const handleSearch = () => {
  currentPage.value = 1;
  fetchScenic();
};

const handleBatchDelete = () => {
  const selectedCards = cards.value.filter(card => card.checked);
  if (selectedCards.length === 0) {
    showToastMessage('请选择要删除的景点', 'warning');
    return;
  }
  showToastMessage(`已选择 ${selectedCards.length} 个景点，请逐个删除`, 'info');
};

const handleCheck = (card) => {
  card.checked = !card.checked;
};

const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);

const handleSizeChange = (newSize) => {
  pageSize.value = newSize;
  currentPage.value = 1;
  fetchScenic();
};

const handleCurrentChange = (newPage) => {
  currentPage.value = newPage;
  fetchScenic();
};

const fetchScenic = async () => {
  try {
    const params = {
      pageNum: currentPage.value,
      pageSize: pageSize.value,
      keyword: searchKeyword.value || null
    };
    const response = await getAttractionList(params);
    
    if (response.code === 200) {
      const data = response.data?.list || response.data?.records || [];
      cards.value = data.map(card => ({
        ...card,
        checked: false
      }));
      total.value = response.data?.total || 0;
    } else {
      console.error('获取景点数据失败:', response.msg || response.message || '未知错误');
      cards.value = [];
      total.value = 0;
    }
  } catch (error) {
    console.error('获取景点数据失败:', error);
    cards.value = [];
    total.value = 0;
  }
};

const showAddDialog = () => {
  isEditing.value = false;
  formData.value = {
    id: null,
    name: '',
    city: '',
    province: '',
    address: '',
    coverImage: '',
    description: '',
    price: '',
    rating: 0,
    season: '',
    createTime: '',
    updateTime: '',
    collectCount: 0,
    commentCount: 0,
    likeCount: 0,
    images: null,
    tags: null,
    tickets: [{
      id: null,
      name: '',
      description: '',
      price: '',
      rules: []
    }],
    facilities: [],
  };
  selectedFacilities.value = [];
  previewImage.value = '';
  fileName.value = '';
  fileSize.value = '';
  showDialog.value = true;
};

const showEditDialog = async (card) => {
  isEditing.value = true;
  try {
    const response = await getAttractionDetail(card.id);
    if (response.code === 200 && response.data) {
      formData.value = { 
        ...response.data,
        tickets: response.data.tickets || [{ id: null, name: '', description: '', price: '', rules: [] }]
      };
      selectedFacilities.value = response.data.facilities || [];
    } else {
      formData.value = { 
        ...card,
        tickets: card.tickets || [{ id: null, name: '', description: '', price: '', rules: [] }]
      };
      selectedFacilities.value = card.facilities || [];
    }
  } catch (error) {
    console.error('获取景点详情失败:', error);
    formData.value = { 
      ...card,
      tickets: card.tickets || [{ id: null, name: '', description: '', price: '', rules: [] }]
    };
    selectedFacilities.value = card.facilities || [];
  }
  previewImage.value = card.coverImage || '';
  showDialog.value = true;
};

const showDetailsDialog = async (card) => {
  selectedCard.value = card;
  try {
    const response = await getAttractionDetail(card.id);
    if (response.code === 200 && response.data) {
      selectedCard.value = response.data;
    }
  } catch (error) {
    console.error('获取景点详情失败:', error);
  }
  showDetails.value = true;
};

const closeDetailsDialog = () => {
  showDetails.value = false;
  selectedCard.value = null;
};

const showToastMessage = (message, type = 'success') => {
  toastMessage.value = message;
  toastType.value = type;
  showToast.value = true;
  setTimeout(() => {
    showToast.value = false;
  }, 3000);
};

const validateForm = () => {
  if (!formData.value.name || !formData.value.city || !formData.value.province || !formData.value.address) {
    showToastMessage('请填写所有必填字段', 'error');
    return false;
  }
  if (!formData.value.price) {
    showToastMessage('请填写价格', 'error');
    return false;
  }
  return true;
};

const submitForm = async () => {
  if (!validateForm()) return;

  formData.value.facilities = selectedFacilities.value;
  
  const validTickets = formData.value.tickets.filter(ticket => ticket.name && ticket.name.trim() !== '');
  const submitData = {
    ...formData.value,
    tickets: validTickets
  };

  try {
    if (isEditing.value) {
      submitData.updateTime = new Date().toISOString();
      await updateAttraction(submitData);
      showToastMessage('更新景点成功');
    } else {
      submitData.createTime = new Date().toISOString();
      submitData.updateTime = new Date().toISOString();
      await addAttraction(submitData);
      showToastMessage('新增景点成功');
    }
    await fetchScenic();
    closeDialog();
  } catch (error) {
    const message = isEditing.value ? '更新景点失败' : '新增景点失败';
    showToastMessage(message, 'error');
    console.error('操作失败:', error);
  }
};

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

const confirmDelete = async () => {
  if (deleteCardId.value) {
    try {
      await deleteAttraction(deleteCardId.value);
      await fetchScenic();
      closeDeletePrompt();
      showToastMessage('删除景点成功');
    } catch (error) {
      console.error('删除失败:', error);
      showToastMessage('删除景点失败', 'error');
    } finally {
      closeDeletePrompt();
    }
  }
};

const closeDialog = () => {
  showDialog.value = false;
};

const dragOver = ref(false);
const previewImage = ref('');
const fileName = ref('');
const fileSize = ref('');
const uploading = ref(false);
const progress = ref(0);

const formatFileSize = (bytes) => {
  if (bytes === 0) return '0 Bytes';
  const k = 1024;
  const sizes = ['Bytes', 'KB', 'MB', 'GB'];
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
};

const handleFileUpload = (event) => {
  const file = event.target.files[0];
  if (!file) return;

  const validTypes = ['image/jpeg', 'image/png'];
  if (!validTypes.includes(file.type)) {
    showToastMessage('只支持JPG/PNG格式图片', 'error');
    return;
  }

  const maxSize = 5 * 1024 * 1024;
  if (file.size > maxSize) {
    showToastMessage('图片大小不能超过5MB', 'error');
    return;
  }

  fileName.value = file.name;
  fileSize.value = formatFileSize(file.size);

  const reader = new FileReader();
  reader.onload = (e) => {
    previewImage.value = e.target.result;
    formData.value.coverImage = e.target.result;
  };
  reader.readAsDataURL(file);

  uploading.value = true;
  const interval = setInterval(() => {
    if (progress.value < 100) {
      progress.value += 10;
    } else {
      clearInterval(interval);
      uploading.value = false;
      progress.value = 0;
    }
  }, 100);

  return file;
};

const handleDrop = (event) => {
  dragOver.value = false;
  const file = event.dataTransfer.files[0];
  if (file) {
    const fakeEvent = { target: { files: [file] } };
    handleFileUpload(fakeEvent);
  }
};

const removeImage = () => {
  previewImage.value = '';
  fileName.value = '';
  fileSize.value = '';
  formData.value.coverImage = '';
};

const triggerFileInput = () => {
  const fileInput = document.createElement('input');
  fileInput.type = 'file';
  fileInput.accept = 'image/*';
  fileInput.onchange = handleFileUpload;
  fileInput.click();
};

onMounted(fetchScenic);
</script>

<style scoped>
@import '@/css/Management/BackgroundManagement.css';

.tags-container {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 8px;
}

.tag-item {
  background-color: #e3f2fd;
  color: #1976d2;
  padding: 4px 12px;
  border-radius: 16px;
  font-size: 12px;
}

.details-container {
  max-height: 400px;
  overflow-y: auto;
  padding: 10px 0;
}

.detail-item {
  display: flex;
  flex-direction: column;
  margin-bottom: 16px;
  padding: 8px;
  background-color: #f5f5f5;
  border-radius: 4px;
}

.detail-item label {
  font-weight: bold;
  margin-bottom: 4px;
  color: #333;
}

.detail-item span {
  color: #666;
}

.detail-list {
  margin-top: 8px;
}

.detail-sub-item {
  display: flex;
  flex-direction: column;
  padding: 8px;
  background-color: #fff;
  border-radius: 4px;
  margin-bottom: 8px;
}

.detail-sub-item span {
  color: #666;
  font-size: 14px;
}

.detail-sub-item span:first-child {
  font-weight: bold;
  color: #333;
}

.multiselect {
  width: 100%;
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

.select-hint {
  font-size: 12px;
  color: #666;
  margin-top: 4px;
}

.ticket-list {
  margin-top: 8px;
}

.ticket-item {
  border: 1px solid #ddd;
  border-radius: 4px;
  padding: 12px;
  margin-bottom: 8px;
}

.ticket-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
  font-weight: bold;
}

.ticket-form {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.ticket-input {
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

.add-ticket-btn, .remove-ticket-btn {
  padding: 6px 12px;
  font-size: 14px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.add-ticket-btn {
  background-color: #1976d2;
  color: white;
  margin-top: 8px;
}

.add-ticket-btn:hover {
  background-color: #1565c0;
}

.remove-ticket-btn {
  background-color: #ef5350;
  color: white;
}

.remove-ticket-btn:hover {
  background-color: #e53935;
}

.ticket-info, .facility-info {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  width: 100%;
}

.ticket-count, .facility-count {
  font-weight: bold;
  font-size: 13px;
  color: #333;
  margin-bottom: 4px;
}

.ticket-names, .facility-names {
  display: flex;
  flex-wrap: nowrap;
  gap: 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 100%;
}

.ticket-tag, .facility-tag {
  background-color: #e3f2fd;
  color: #1976d2;
  padding: 2px 8px;
  border-radius: 10px;
  font-size: 11px;
  flex-shrink: 0;
}

.ticket-more, .facility-more {
  color: #999;
  font-size: 11px;
  flex-shrink: 0;
}
</style>