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

      <!-- 通用新增/编辑弹窗 -->
      <FormDialog
        v-model:visible="showDialog"
        title="酒店"
        :isEdit="isEditing"
        :fields="formFields"
        :initialData="formData"
        :showImageUpload="true"
        imageUploadLabel="上传酒店图片"
        recommendedSize="推荐尺寸：1200×800px"
        imageField="coverImage"
        :validateFn="validateForm"
        :submitFn="handleSubmit"
        @error="handleError"
      >
        <template #custom-fields>
          <!-- 酒店设施选择 -->
          <div class="form-section">
            <label class="section-label">选择酒店设施</label>
            <FacilitySelector v-model="selectedFacilities" :facilities="allFacilities" />
          </div>
          <!-- 房型管理 -->
          <div class="form-section">
            <label class="section-label">房型列表</label>
            <RoomManager v-model="formData.rooms" :room-facilities="roomFacilities" />
          </div>
        </template>
      </FormDialog>

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
import FormDialog from '@/components/FormDialog.vue';
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
const selectedFacilities = ref([]);

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

// 表单字段配置
const formFields = [
  [
    { name: 'name', label: '酒店名称', type: 'text', required: true, placeholder: '请输入酒店名称' },
    { name: 'city', label: '城市', type: 'text', required: true, placeholder: '请输入城市' },
    { name: 'province', label: '省份', type: 'text', required: true, placeholder: '请输入省份' },
    { name: 'address', label: '地址', type: 'text', required: true, placeholder: '请输入详细地址' },
  ],
  [
    { name: 'description', label: '描述', type: 'textarea', required: true, rows: 3, placeholder: '请输入酒店描述' },
    { name: 'price', label: '价格', type: 'number', required: true, step: '0.01', min: 0, placeholder: '请输入价格' },
    { name: 'rating', label: '评分', type: 'number', min: 0, max: 5, step: 0.1, placeholder: '0-5分' },
  ],
];

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

const validateForm = (data) => {
  if (!data.name || !data.city || !data.province || !data.address) {
    return '请填写所有必填字段';
  }
  if (!data.price) {
    return '请填写价格';
  }
  return null;
};

const handleSubmit = async (data) => {
  const validRooms = (data.rooms || []).filter(room => room.name && room.name.trim() !== '');
  const submitData = {
    ...data,
    rooms: validRooms,
    facilityList: selectedFacilities.value
  };

  if (isEditing.value) {
    await updateHotel(submitData);
    showToastMessage('更新酒店成功');
  } else {
    await addHotel(submitData);
    showToastMessage('新增酒店成功');
  }
  await fetchHotels();
};

const handleError = (error) => {
  showToastMessage(error.message || '操作失败', 'error');
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

const triggerFileInput = (hotel) => {
  if (hotel.coverImage) {
    window.open(hotel.coverImage, '_blank');
  }
};

onMounted(fetchHotels);
</script>

<style scoped>
@import '@/css/Management/BackgroundManagement.css';

/* 自定义字段插槽样式 */
.form-section {
  margin-bottom: 16px;
}

.section-label {
  display: block;
  font-size: 14px;
  font-weight: 500;
  color: #1d1d1f;
  margin-bottom: 12px;
}

/* 标签样式 */
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
