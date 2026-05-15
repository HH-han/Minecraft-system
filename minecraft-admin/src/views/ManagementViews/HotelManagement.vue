<template>
  <div class="management-page-background">
    <div class="container-management">
      <div class="action-bar">
        <div class="search-bar">
          <div class="search-box-management">
            <input type="text" v-model="searchKeyword" placeholder="输入酒店ID或名称搜索" class="search-input-management" />
          </div>
          <button class="btn search-btn" @click="handleSearch">搜索</button>
          <button class="btn delete-btn" @click="handleBatchDelete">批量删除</button>
        </div>
        <button class="btn add-btn" @click="showAddDialog">新增酒店</button>
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
              <tr v-for="hotel in filteredHotels" :key="hotel.id">
                <td>
                  <input type="checkbox" :checked="hotel.checked" @change="handleCheck(hotel)" class="ui-checkbox" />
                </td>
                <td>{{ hotel.id }}</td>
                <td>{{ hotel.name }}</td>
                <td>{{ hotel.city }}</td>
                <td>{{ hotel.province }}</td>
                <td>{{ hotel.address }}</td>
                <td>{{ hotel.description ? hotel.description.substring(0, 20) : '未设置' }}</td>
                <td>
                  <img :src="hotel.coverImage?.replace(/[`\s]/g, '')" alt="酒店图片" style="width: 35px; height: 35px;"
                    @click="triggerFileInput(hotel)" />
                </td>
                <td>￥{{ hotel.price }}</td>
                <td>{{ hotel.rating }}</td>
                <td>{{ hotel.likeCount }}</td>
                <td>{{ hotel.collectCount }}</td>
                <td>{{ hotel.commentCount }}</td>
                <td>
                  <div class="room-info">
                    <div class="room-count">{{ hotel.rooms ? hotel.rooms.length : 0 }}种房型</div>
                    <div class="room-names" v-if="hotel.rooms && hotel.rooms.length > 0">
                      <span v-for="room in hotel.rooms.slice(0, 3)" :key="room.id" class="room-tag">{{ room.name }}</span>
                      <span v-if="hotel.rooms.length > 3" class="room-more">+{{ hotel.rooms.length - 3 }}</span>
                    </div>
                  </div>
                </td>
                <td>
                  <div class="facility-info">
                    <div class="facility-count">{{ hotel.facilities ? hotel.facilities.length : 0 }}项设施</div>
                    <div class="facility-names" v-if="hotel.facilities && hotel.facilities.length > 0">
                      <span v-for="facility in hotel.facilities.slice(0, 3)" :key="facility" class="facility-tag">{{ facility }}</span>
                      <span v-if="hotel.facilities.length > 3" class="facility-more">+{{ hotel.facilities.length - 3 }}</span>
                    </div>
                  </div>
                </td>
                <td>{{ formatDate(hotel.createTime) }}</td>
                <td class="table-btn-display">
                  <button class="btn details-btn" @click="showDetailsDialog(hotel)">详情</button>
                  <button class="btn edit-btn" @click="showEditDialog(hotel)">编辑</button>
                  <button class="btn delete-btn" @click="handleDelete(hotel.id)">删除</button>
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

      <!-- 新增/编辑弹窗 -->
      <div v-if="showDialog" class="dialog-overlay" @click.self="closeDialog">
        <div class="dialog" @click.stop>
          <h2>{{ isEditing ? '编辑酒店' : '新增酒店' }}</h2>
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
                <label>酒店名称:</label>
                <input v-model="formData.name" required />
              </div>
              <div class="form-group">
                <label>城市:</label>
                <input v-model="formData.city" required />
              </div>
              <div class="form-group">
                <label>省份:</label>
                <input v-model="formData.province" required />
              </div>
              <div class="form-group">
                <label>地址:</label>
                <input v-model="formData.address" required />
              </div>
            </div>
            <div class="form-row">
              <div class="form-group">
                <label>描述:</label>
                <textarea v-model="formData.description" required></textarea>
              </div>
              <div class="form-group">
                <label>价格:</label>
                <input v-model="formData.price" required type="number" step="0.01" />
              </div>
              <div class="form-group" v-if="isEditing">
                <label>评分:</label>
                <input v-model="formData.rating" required type="number" min="0" max="5" step="0.1" />
              </div>
            </div>

            <!-- 酒店设施选择 -->
            <div class="form-group">
              <FacilitySelector v-model="selectedFacilities" :facilities="allFacilities" label="选择酒店设施" />
            </div>

            <!-- 房型管理 -->
            <div class="form-group">
              <RoomManager v-model="formData.rooms" :room-facilities="roomFacilities" label="房型列表" title="房型" />
            </div>

            <div class="dialog-buttons">
              <button type="button" class="btn cancel-btn" @click="closeDialog">取消</button>
              <button type="submit" class="btn confirm-btn">{{ isEditing ? '保存' : '创建' }}</button>
            </div>
          </form>
        </div>
      </div>

      <!-- 详情弹窗 -->
      <div v-if="showDetails" class="dialog-overlay" @click.self="closeDetailsDialog">
        <div class="dialog" @click.stop>
          <h2>酒店详情</h2>
          <div class="details-container">
            <div class="detail-item">
              <label>酒店名称:</label>
              <span>{{ selectedHotel?.name }}</span>
            </div>
            <div class="detail-item">
              <label>城市:</label>
              <span>{{ selectedHotel?.city }}</span>
            </div>
            <div class="detail-item">
              <label>省份:</label>
              <span>{{ selectedHotel?.province }}</span>
            </div>
            <div class="detail-item">
              <label>地址:</label>
              <span>{{ selectedHotel?.address }}</span>
            </div>
            <div class="detail-item">
              <label>描述:</label>
              <span>{{ selectedHotel?.description }}</span>
            </div>
            <div class="detail-item">
              <label>价格:</label>
              <span>￥{{ selectedHotel?.price }}</span>
            </div>
            <div class="detail-item">
              <label>评分:</label>
              <span>{{ selectedHotel?.rating }}</span>
            </div>
            <div class="detail-item">
              <label>酒店设施:</label>
              <div class="tags-container">
                <span v-for="facility in selectedHotel?.facilities" :key="facility" class="tag-item">
                  {{ facility }}
                </span>
              </div>
            </div>
            <div class="detail-item" v-if="selectedHotel?.rooms && selectedHotel.rooms.length > 0">
              <label>酒店房型:</label>
              <div class="detail-list">
                <div v-for="room in selectedHotel.rooms" :key="room.id" class="detail-sub-item">
                  <span>{{ room.name }}</span>
                  <span>{{ room.description }}</span>
                  <span>￥{{ room.price }}</span>
                  <span v-if="room.facilities">设施: {{ room.facilities.join(', ') }}</span>
                </div>
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
import { getHotelList, getHotelDetail, addHotel, updateHotel, deleteHotel } from '@/api/hotel';
import FacilitySelector from '@/components/FacilitySelector.vue';
import RoomManager from '@/components/RoomManager.vue';
import DeleteConfirmation from '@/components/PromptComponent/DeleteConfirmation.vue';
import ToastType from '@/components/PromptComponent/ToastType.vue';

const columns = [
  { key: 'checked', title: '多选' },
  { key: 'id', title: '酒店ID' },
  { key: 'name', title: '酒店名称' },
  { key: 'city', title: '城市' },
  { key: 'province', title: '省份' },
  { key: 'address', title: '地址' },
  { key: 'description', title: '描述' },
  { key: 'coverImage', title: '封面图片' },
  { key: 'price', title: '价格' },
  { key: 'rating', title: '评分' },
  { key: 'likeCount', title: '点赞数' },
  { key: 'collectCount', title: '收藏数' },
  { key: 'commentCount', title: '评论数' },
  { key: 'rooms', title: '房型' },
  { key: 'facilities', title: '设施' },
  { key: 'createTime', title: '创建时间' },
];

const showToast = ref(false);
const toastMessage = ref('');
const toastType = ref('success');
const hotels = ref([]);
const searchKeyword = ref('');
const showDialog = ref(false);
const showDetails = ref(false);
const isEditing = ref(false);
const selectedHotel = ref(null);

const allFacilities = [
  '免费WiFi', '游泳池', '健身房', '停车场', '餐厅',
  '空调', '电视', '独立卫浴', '迷你吧', '行政酒廊',
  '商务中心', '会议设施', '洗衣服务', '24小时前台', '客房服务'
];

const roomFacilities = [
  '免费WiFi', '空调', '电视', '独立卫浴', '迷你吧',
  '浴缸', '淋浴', '吹风机', '热水', '阳台'
];

const formData = ref({
  id: null,
  name: '',
  city: '',
  province: '',
  address: '',
  description: '',
  coverImage: '',
  images: '',
  price: '',
  rating: 0,
  likeCount: 0,
  collectCount: 0,
  commentCount: 0,
  createTime: '',
  rooms: [],
  facilities: [],
});

const selectedFacilities = ref([]);

const formatDate = (date) => {
  if (!date) return '未知日期';
  const options = { year: 'numeric', month: 'long', day: 'numeric' };
  return new Intl.DateTimeFormat('zh-CN', options).format(new Date(date));
};

const filteredHotels = computed(() => {
  const keyword = searchKeyword.value.toLowerCase();
  return (hotels.value || []).filter(
    (hotel) =>
      String(hotel.id).includes(keyword) ||
      (hotel.name && hotel.name.toLowerCase().includes(keyword))
  );
});

const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);

const handleSizeChange = (newSize) => {
  pageSize.value = newSize;
  currentPage.value = 1;
  fetchHotels();
};

const handleCurrentChange = (newPage) => {
  currentPage.value = newPage;
  fetchHotels();
};

const handleSearch = () => {
  currentPage.value = 1;
  fetchHotels();
};

const handleCheck = (hotel) => {
  hotel.checked = !hotel.checked;
};

const handleBatchDelete = () => {
  const selectedHotels = hotels.value.filter(hotel => hotel.checked);
  if (selectedHotels.length === 0) {
    showToastMessage('请选择要删除的酒店', 'warning');
    return;
  }
  showToastMessage(`已选择 ${selectedHotels.length} 个酒店，请逐个删除`, 'info');
};

const fetchHotels = async () => {
  try {
    const params = {
      pageNum: currentPage.value,
      pageSize: pageSize.value,
      keyword: searchKeyword.value || null
    };
    const response = await getHotelList(params);
    
    if (response.code === 200) {
      const responseData = response.data || {};
      const data = responseData.list || responseData.records || [];
      hotels.value = data.map(hotel => ({
        ...hotel,
        checked: false,
        rooms: hotel.rooms || [],
        facilities: hotel.facilities || []
      }));
      total.value = responseData.total || 0;
    } else {
      console.error('获取酒店数据失败:', response.msg || response.message || '未知错误');
      hotels.value = [];
      total.value = 0;
    }
  } catch (error) {
    console.error('获取酒店数据失败:', error);
    hotels.value = [];
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
    description: '',
    coverImage: '',
    images: '',
    price: '',
    rating: 0,
    likeCount: 0,
    collectCount: 0,
    commentCount: 0,
    rooms: [{
      id: null,
      name: '',
      description: '',
      price: '',
      facilities: []
    }],
    facilities: [],
  };
  selectedFacilities.value = [];
  previewImage.value = '';
  fileName.value = '';
  fileSize.value = '';
  showDialog.value = true;
};

const showEditDialog = async (hotel) => {
  isEditing.value = true;
  try {
    const response = await getHotelDetail(hotel.id);
    if (response.code === 200 && response.data) {
      const detailData = response.data;
      formData.value = { 
        ...detailData,
        rooms: detailData.rooms?.length > 0 ? detailData.rooms : [{ id: null, name: '', description: '', price: '', facilities: [] }]
      };
      selectedFacilities.value = detailData.facilities || [];
    } else {
      formData.value = { 
        ...hotel,
        rooms: hotel.rooms?.length > 0 ? hotel.rooms : [{ id: null, name: '', description: '', price: '', facilities: [] }]
      };
      selectedFacilities.value = hotel.facilities || [];
    }
  } catch (error) {
    console.error('获取酒店详情失败:', error);
    formData.value = { 
      ...hotel,
      rooms: hotel.rooms?.length > 0 ? hotel.rooms : [{ id: null, name: '', description: '', price: '', facilities: [] }]
    };
    selectedFacilities.value = hotel.facilities || [];
  }
  previewImage.value = hotel.coverImage || '';
  showDialog.value = true;
};

const showDetailsDialog = async (hotel) => {
  selectedHotel.value = {
    ...hotel,
    rooms: hotel.rooms || [],
    facilities: hotel.facilities || []
  };
  try {
    const response = await getHotelDetail(hotel.id);
    if (response.code === 200 && response.data) {
      selectedHotel.value = {
        ...response.data,
        rooms: response.data.rooms || [],
        facilities: response.data.facilities || []
      };
    }
  } catch (error) {
    console.error('获取酒店详情失败:', error);
  }
  showDetails.value = true;
};

const closeDetailsDialog = () => {
  showDetails.value = false;
  selectedHotel.value = null;
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

  const validRooms = formData.value.rooms.filter(room => room.name && room.name.trim() !== '');
  const submitData = {
    ...formData.value,
    rooms: validRooms,
    facilityList: selectedFacilities.value
  };

  try {
    if (isEditing.value) {
      await updateHotel(submitData);
      showToastMessage('更新酒店成功');
    } else {
      await addHotel(submitData);
      showToastMessage('新增酒店成功');
    }
    await fetchHotels();
    closeDialog();
  } catch (error) {
    const message = isEditing.value ? '更新酒店失败' : '新增酒店失败';
    showToastMessage(message, 'error');
    console.error('操作失败:', error);
  }
};

const isDeletePromptVisible = ref(false);
const deleteHotelId = ref(null);

const handleDelete = (id) => {
  deleteHotelId.value = id;
  isDeletePromptVisible.value = true;
};

const closeDeletePrompt = () => {
  isDeletePromptVisible.value = false;
  deleteHotelId.value = null;
};

const confirmDelete = async () => {
  if (deleteHotelId.value) {
    try {
      await deleteHotel(deleteHotelId.value);
      await fetchHotels();
      showToastMessage('删除酒店成功');
    } catch (error) {
      console.error('删除失败:', error);
      showToastMessage('删除酒店失败', 'error');
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

onMounted(fetchHotels);
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

.room-info, .facility-info {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  width: 100%;
}

.room-count, .facility-count {
  font-weight: bold;
  font-size: 13px;
  color: #333;
  margin-bottom: 4px;
}

.room-names, .facility-names {
  display: flex;
  flex-wrap: nowrap;
  gap: 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 100%;
}

.room-tag, .facility-tag {
  background-color: #e3f2fd;
  color: #1976d2;
  padding: 2px 8px;
  border-radius: 10px;
  font-size: 11px;
  flex-shrink: 0;
}

.room-more, .facility-more {
  color: #999;
  font-size: 11px;
  flex-shrink: 0;
}
</style>