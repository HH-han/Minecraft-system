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
                  <img :src="card.coverImage" alt="景点图片" style="width: 35px; height: 35px;" @click="previewImage(card)">
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

      <!-- 通用新增/编辑弹窗 -->
      <FormDialog
        v-model:visible="showDialog"
        title="景点"
        :isEdit="isEditing"
        :fields="formFields"
        :initialData="formData"
        :showImageUpload="true"
        imageUploadLabel="上传景点图片"
        recommendedSize="推荐尺寸：1200×800px"
        imageField="coverImage"
        :validateFn="validateForm"
        :submitFn="handleSubmit"
        @error="handleError"
      >
        <template #custom-fields>
          <div class="form-group">
            <FacilitySelector v-model="selectedFacilities" :facilities="allFacilities" label="选择景点设施" />
          </div>
          <div class="form-group">
            <TicketManager v-model="formData.tickets" label="门票列表" title="门票" />
          </div>
        </template>
      </FormDialog>

      <DeleteConfirmation v-if="isDeletePromptVisible" @close="closeDeletePrompt" @confirm="confirmDelete" />
      <ToastType v-if="showToast" :toastMessage="toastMessage" :toastType="toastType" />
    </div>
  </div>
</template>

<script setup>

import { ref, computed, onMounted } from 'vue';
import { getAttractionList, getAttractionDetail, addAttraction, updateAttraction, deleteAttraction } from '@/api/attraction';
import FacilitySelector from '@/components/FacilitySelector.vue';
import TicketManager from '@/components/TicketManager.vue';
import FormDialog from '@/components/FormDialog.vue';
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

// 表单字段配置
const formFields = [
  [
    { name: 'name', label: '景点名称', type: 'text', required: true, placeholder: '请输入景点名称' },
    { name: 'price', label: '景点价格', type: 'number', required: true, placeholder: '请输入景点价格' },
  ],
  [
    { name: 'city', label: '城市', type: 'text', required: true, placeholder: '请输入城市' },
    { name: 'province', label: '省份', type: 'text', required: true, placeholder: '请输入省份' },
  ],
  [
    { name: 'address', label: '地址', type: 'text', required: true, placeholder: '请输入详细地址' },
  ],
  [
    { name: 'description', label: '描述', type: 'textarea', required: true, placeholder: '请输入景点描述', fullWidth: true, rows: 3 },
  ],
  [
    { name: 'rating', label: '评分', type: 'number', min: 0, max: 5, step: 0.1, placeholder: '0-5分' },
    { name: 'season', label: '最佳季节', type: 'text', placeholder: '如：春季、夏季' },
  ],
];

const formatDate = (date) => {
  if (!date) return '未知日期';
  const options = { year: 'numeric', month: 'long', day: 'numeric' };
  return new Intl.DateTimeFormat('zh-CN', options).format(new Date(date));
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
      const responseData = response.data || {};
      const data = responseData.list || responseData.records || [];
      cards.value = data.map(card => ({
        ...card,
        checked: false,
        tickets: card.tickets || [],
        facilities: card.facilities || []
      }));
      total.value = responseData.total || 0;
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
  showDialog.value = true;
};

const showEditDialog = async (card) => {
  isEditing.value = true;
  try {
    const response = await getAttractionDetail(card.id);
    if (response.code === 200 && response.data) {
      const detailData = response.data;
      formData.value = { 
        ...detailData,
        tickets: detailData.tickets?.length > 0 ? detailData.tickets : [{ id: null, name: '', description: '', price: '', rules: [] }]
      };
      selectedFacilities.value = detailData.facilities || [];
    } else {
      formData.value = { 
        ...card,
        tickets: card.tickets?.length > 0 ? card.tickets : [{ id: null, name: '', description: '', price: '', rules: [] }]
      };
      selectedFacilities.value = card.facilities || [];
    }
  } catch (error) {
    console.error('获取景点详情失败:', error);
    formData.value = { 
      ...card,
      tickets: card.tickets?.length > 0 ? card.tickets : [{ id: null, name: '', description: '', price: '', rules: [] }]
    };
    selectedFacilities.value = card.facilities || [];
  }
  showDialog.value = true;
};

const showDetailsDialog = async (card) => {
  selectedCard.value = {
    ...card,
    tickets: card.tickets || [],
    facilities: card.facilities || []
  };
  try {
    const response = await getAttractionDetail(card.id);
    if (response.code === 200 && response.data) {
      selectedCard.value = {
        ...response.data,
        tickets: response.data.tickets || [],
        facilities: response.data.facilities || []
      };
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

const validateForm = (data, isEdit) => {
  if (!data.name || !data.city || !data.province || !data.address) {
    return '请填写所有必填字段（名称、城市、省份、地址）';
  }
  if (!data.price) {
    return '请填写价格';
  }
  return null;
};

const handleSubmit = async (data, isEdit) => {
  const submitData = { ...data };
  submitData.facilities = selectedFacilities.value;
  
  const validTickets = (submitData.tickets || []).filter(ticket => ticket.name && ticket.name.trim() !== '');
  submitData.tickets = validTickets;

  if (isEdit) {
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
};

const handleError = (error) => {
  showToastMessage(error.message || '操作失败', 'error');
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

// 预览图片
const previewImage = (card) => {
  if (card.coverImage) {
    window.open(card.coverImage, '_blank');
  }
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