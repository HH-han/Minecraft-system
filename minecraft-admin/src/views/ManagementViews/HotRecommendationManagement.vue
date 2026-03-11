<template>
    <div class="management-page-background">
        <div class="container-management">
            <!-- 操作栏 -->
            <div class="action-bar">
                <div class="search-bar">
                    <div class="search-box-management">
                        <input type="text" v-model="searchKeyword" placeholder="输入卡片ID或标题搜索"
                            class="search-input-management" />
                    </div>
                    <button class="btn search-btn" @click="handleSearch">搜索</button>
                    <button class="btn delete-btn" @click="handleReset">批量删除</button>
                </div>
                <button class="btn add-btn" @click="showAddDialog">新增轮播</button>
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
                                    <input type="checkbox" :checked="card.checked" @change="handleCheck(card)"
                                        class="ui-checkbox" />
                                </td>
                                <td>{{ card.id }}</td>
                                <td>{{ card.name }}</td>
                                <td>{{ card.englishName || '-' }}</td>
                                <td>
                                    <img :src="card.coverImageUrl" alt="图片" style="width: 35px; height: 35px;"
                                        @click="triggerFileInput(card)" />
                                </td>
                                <td>{{ card.recommendationReason ? card.recommendationReason.substring(0, 25) : '无' }}...</td>
                                <td>{{ card.categoryName }}</td>
                                <td>{{ card.city }}</td>
                                <td>{{ card.district || '-' }}</td>
                                <td>{{ card.isHot ? '是' : '否' }}</td>
                                <td>{{ card.isNew ? '是' : '否' }}</td>
                                <td>{{ card.isTop ? '是' : '否' }}</td>
                                <td>{{ card.status === 1 ? '上架' : card.status === 0 ? '下架' : card.status === 2 ? '审核中' : '待发布' }}</td>
                                <td>{{ card.minPrice || '-' }}</td>
                                <td>{{ card.maxPrice || '-' }}</td>
                                <td>{{ card.visitDuration || '-' }}</td>
                                <td>{{ formatDate(card.createdAt) }}</td>
                                <td>{{ formatDate(card.updatedAt) }}</td>
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
                <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange"
                    :current-page="currentPage" :page-sizes="[10, 20, 50, 100]" :page-size="pageSize"
                    layout="total, sizes, prev, pager, next, jumper" :total="total">
                </el-pagination>
            </div>
            <!-- 新增/编辑弹窗 -->
            <div v-if="showDialog" class="dialog-overlay" @click.self="closeDialog">
                <div class="dialog" @click.stop>
                    <h2>{{ isEditing ? '编辑推荐' : '新增推荐' }}</h2>
                    <form @submit.prevent="submitForm" class="form-container">
                        <div class="form-group">
                            <div class="image-upload-container">
                                <div class="upload-header">
                                    <h3>上传封面图片</h3>
                                    <p>支持 JPG, PNG 格式，最大 5MB</p>
                                </div>

                                <div class="upload-area" @click="triggerFileInput" @dragover.prevent="dragOver = true"
                                    @dragleave="dragOver = false" @drop.prevent="handleDrop"
                                    :class="{ 'drag-active': dragOver }">
                                    <input type="file" ref="fileInput" @change="handleFileUpload" accept="image/*"
                                        class="file-input" />

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

                                <!-- 图片预览区域 -->
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
                                                    <path
                                                        d="M19,4H15.5L14.5,3H9.5L8.5,4H5V6H19M6,19A2,2 0 0,0 8,21H16A2,2 0 0,0 18,19V7H6V19Z" />
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
                                <label>推荐名称:</label>
                                <input v-model="formData.name" required />
                            </div>
                            <div class="form-group">
                                <label>英文名:</label>
                                <input v-model="formData.englishName" />
                            </div>
                            <div class="form-group">
                                <label>别名:</label>
                                <input v-model="formData.aliasName" />
                            </div>
                            <div class="form-group">
                                <label>分类:</label>
                                <el-select v-model="formData.categoryId" placeholder="请选择分类" clearable style="width: 100%" required>
                                    <el-option 
                                        v-for="option in categoryOptions" 
                                        :key="option.value" 
                                        :label="option.label" 
                                        :value="option.value" 
                                    />
                                </el-select>
                            </div>
                        </div>
                        <div class="form-row">
                            <div class="form-group">
                                <label>国家:</label>
                                <input v-model="formData.country" />
                            </div>
                            <div class="form-group">
                                <label>省份:</label>
                                <input v-model="formData.province" />
                            </div>
                            <div class="form-group">
                                <label>城市:</label>
                                <input v-model="formData.city" required />
                            </div>
                            <div class="form-group">
                                <label>区县:</label>
                                <input v-model="formData.district" />
                            </div>
                        </div>
                        <div class="form-row">
                            <div class="form-group">
                                <label>地址:</label>
                                <input v-model="formData.address" />
                            </div>
                            <div class="form-group">
                                <label>交通:</label>
                                <input v-model="formData.transportation" />
                            </div>
                            <div class="form-group">
                                <label>状态:</label>
                                <el-select v-model="formData.status" placeholder="请选择状态" clearable style="width: 100%" required>
                                    <el-option 
                                        v-for="option in statusOptions" 
                                        :key="option.value" 
                                        :label="option.label" 
                                        :value="option.value" 
                                    />
                                </el-select>
                            </div>
                        </div>
                        <div class="form-row">
                            <div class="form-group">
                                <label>推荐理由:</label>
                                <textarea v-model="formData.recommendationReason" rows="3" required></textarea>
                            </div>
                            <div class="form-group">
                                <label>亮点特色:</label>
                                <textarea v-model="formData.highlights" rows="3"></textarea>
                            </div>
                        </div>
                        <div class="form-row">
                            <div class="form-group">
                                <label>最佳时间:</label>
                                <input v-model="formData.bestTravelTime" />
                            </div>
                            <div class="form-group">
                                <label>游玩时长:</label>
                                <input v-model="formData.visitDuration" />
                            </div>
                            <div class="form-group">
                                <label>季节:</label>
                                <input v-model="formData.season" />
                            </div>
                            <div class="form-group">
                                <label>节日:</label>
                                <input v-model="formData.festival" />
                            </div>
                        </div>
                        <div class="form-row">
                            <div class="form-group">
                                <label>门票价格:</label>
                                <input v-model="formData.ticketPriceDescription" />
                            </div>
                            <div class="form-group">
                                <label>最低价格:</label>
                                <input v-model="formData.minPrice" type="number" step="0.01" />
                            </div>
                            <div class="form-group">
                                <label>最高价格:</label>
                                <input v-model="formData.maxPrice" type="number" step="0.01" />
                            </div>
                            <div class="form-group">
                                <label>货币:</label>
                                <input v-model="formData.priceCurrency" />
                            </div>
                        </div>
                        <div class="form-row">
                            <div class="form-group">
                                <label>平日开放时间:</label>
                                <input v-model="formData.openingHoursWeekday" />
                            </div>
                            <div class="form-group">
                                <label>周末开放时间:</label>
                                <input v-model="formData.openingHoursWeekend" />
                            </div>
                            <div class="form-group">
                                <label>开放时间说明:</label>
                                <input v-model="formData.openingHoursDescription" />
                            </div>
                        </div>
                        <div class="form-row">
                            <div class="form-group">
                                <label>联系电话:</label>
                                <input v-model="formData.contactPhone" />
                            </div>
                            <div class="form-group">
                                <label>官网:</label>
                                <input v-model="formData.officialWebsite" />
                            </div>
                            <div class="form-group">
                                <label>社交媒体:</label>
                                <input v-model="formData.socialMedia" />
                            </div>
                        </div>
                        <div class="form-row">
                            <div class="form-group">
                                <label>标签:</label>
                                <input v-model="formData.tags" />
                            </div>
                            <div class="form-group">
                                <label>适合人群:</label>
                                <input v-model="formData.suitableCrowd" />
                            </div>
                            <div class="form-group">
                                <label>设施信息:</label>
                                <input v-model="formData.facilityInfo" />
                            </div>
                            <div class="form-group">
                                <label>小贴士:</label>
                                <input v-model="formData.tips" />
                            </div>
                        </div>
                        <div class="form-row">
                            <div class="form-group">
                                <label>是否热门:</label>
                                <el-switch v-model="formData.isHot" />
                            </div>
                            <div class="form-group">
                                <label>是否新晋:</label>
                                <el-switch v-model="formData.isNew" />
                            </div>
                            <div class="form-group">
                                <label>是否置顶:</label>
                                <el-switch v-model="formData.isTop" />
                            </div>
                            <div class="form-group">
                                <label>是否免费:</label>
                                <el-switch v-model="formData.isFree" />
                            </div>
                        </div>
                        <div class="form-row">
                            <div class="form-group">
                                <label>摘要:</label>
                                <textarea v-model="formData.summary" rows="2"></textarea>
                            </div>
                            <div class="form-group">
                                <label>内容:</label>
                                <textarea v-model="formData.content" rows="2"></textarea>
                            </div>
                        </div>
                        <div class="form-row">
                            <div class="form-group">
                                <label>SEO标题:</label>
                                <input v-model="formData.seoTitle" />
                            </div>
                            <div class="form-group">
                                <label>SEO关键词:</label>
                                <input v-model="formData.seoKeywords" />
                            </div>
                            <div class="form-group">
                                <label>SEO描述:</label>
                                <input v-model="formData.seoDescription" />
                            </div>
                        </div>
                        <div class="form-row">
                            <div class="form-group">
                                <label>来源:</label>
                                <input v-model="formData.source" />
                            </div>
                            <div class="form-group">
                                <label>作者:</label>
                                <input v-model="formData.author" />
                            </div>
                            <div class="form-group">
                                <label>排序权重:</label>
                                <input v-model="formData.weight" type="number" />
                            </div>
                        </div>
                        <!-- 创建修改时间 -->
                        <div class="dialog-buttons">
                            <button type="button" class="btn cancel-btn" @click="closeDialog">取消</button>
                            <button type="submit" class="btn confirm-btn">{{ isEditing ? '保存' : '创建' }}</button>
                        </div>
                    </form>
                </div>
            </div>

            <!-- 删除提示框组件 -->
            <DeleteConfirmation v-if="isDeletePromptVisible" @close="closeDeletePrompt" @confirm="confirmDelete" />
            <!-- 自定义提示框组件 -->
            <ToastType v-if="showToast" :toastMessage="toastMessage" :toastType="toastType" />
        </div>
    </div>

</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import request from '@/utils/request';
import { getHomeRecommendationsList, addHomeRecommendation, updateHomeRecommendation, deleteHomeRecommendation } from '@/api/homeRecommendations';
import DeleteConfirmation from '@/components/PromptComponent/DeleteConfirmation.vue';
import ToastType from '@/components/PromptComponent/ToastType.vue';

const columns = [
    { key: 'checked', title: '多选' },
    { key: 'id', title: '推荐ID' },
    { key: 'name', title: '推荐名称' },
    { key: 'englishName', title: '英文名' },
    { key: 'coverImageUrl', title: '封面图片' },
    { key: 'recommendationReason', title: '推荐理由' },
    { key: 'categoryName', title: '分类' },
    { key: 'city', title: '城市' },
    { key: 'district', title: '区县' },
    { key: 'isHot', title: '是否热门' },
    { key: 'isNew', title: '是否新晋' },
    { key: 'isTop', title: '是否置顶' },
    { key: 'status', title: '状态' },
    { key: 'minPrice', title: '最低价格' },
    { key: 'maxPrice', title: '最高价格' },
    { key: 'visitDuration', title: '游玩时长' },
    { key: 'createdAt', title: '创建时间' },
    { key: 'updatedAt', title: '更新时间' },
];

// 状态选项
const statusOptions = [
    { label: '下架', value: 0 },
    { label: '上架', value: 1 },
    { label: '审核中', value: 2 },
    { label: '待发布', value: 3 },
];

// 分类选项
const categoryOptions = [
    { label: '自然风光', value: 1 },
    { label: '历史文化', value: 2 },
    { label: '主题乐园', value: 3 },
    { label: '美食探店', value: 4 },
    { label: '休闲度假', value: 5 },
];
const showToast = ref(false);
const toastMessage = ref('');
const toastType = ref('success');
const cards = ref([]);
const searchKeyword = ref('');
const showDialog = ref(false);
const isEditing = ref(false);
const formData = ref({
    id: null,
    name: '',
    englishName: '',
    aliasName: '',
    categoryId: null,
    categoryName: '',
    tags: '',
    country: '中国',
    province: '',
    city: '',
    district: '',
    address: '',
    transportation: '',
    recommendationReason: '',
    highlights: '',
    bestTravelTime: '',
    visitDuration: '',
    coverImageUrl: '',
    bannerImageUrl: '',
    videoUrl: '',
    imageUrls: '',
    ticketPriceDescription: '',
    minPrice: null,
    maxPrice: null,
    priceCurrency: 'CNY',
    isFree: false,
    openingHoursWeekday: '',
    openingHoursWeekend: '',
    openingHoursDescription: '',
    contactPhone: '',
    officialWebsite: '',
    socialMedia: '',
    isHot: false,
    isNew: false,
    isTop: false,
    status: 1,
    season: '',
    festival: '',
    tips: '',
    suitableCrowd: '',
    facilityInfo: '',
    seoTitle: '',
    seoKeywords: '',
    seoDescription: '',
    content: '',
    summary: '',
    source: '',
    author: '',
    weight: 100,
});

// 格式化日期显示
const formatDate = (date) => {
    if (!date || isNaN(new Date(date))) return '未知日期';
    const options = { year: 'numeric', month: 'long', day: 'numeric' };
    return new Intl.DateTimeFormat('zh-CN', options).format(new Date(date));
};

// 搜索功能
const filteredCards = computed(() => {
    const keyword = searchKeyword.value.toLowerCase();
    if (!keyword) {
        return cards.value;
    }
    return cards.value.filter(
        (card) =>
            String(card.id).includes(keyword) ||
            card.name.toLowerCase().includes(keyword) ||
            card.recommendationReason.toLowerCase().includes(keyword) ||
            card.categoryName.toLowerCase().includes(keyword) ||
            card.city.toLowerCase().includes(keyword)
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
        const response = await getHomeRecommendationsList({ pageNum: currentPage.value, pageSize: pageSize.value });
        // 检查响应数据格式
        if (response.code === 200 && response.data) {
            if (response.data.records) {
                // 处理分页响应
                cards.value = response.data.records;
                total.value = response.data.total;
            } else if (Array.isArray(response.data)) {
                // 处理直接返回数组的情况
                cards.value = response.data;
                total.value = response.data.length;
            } else {
                // 如果数据格式不正确，使用空数组
                cards.value = [];
                total.value = 0;
                console.error('获取数据失败: 响应数据格式不正确', response.data);
            }
        } else {
            cards.value = [];
            total.value = 0;
            console.error('获取数据失败: 响应格式不正确', response);
        }
    } catch (error) {
        console.error('获取数据失败:', error);
        // 发生错误时使用空数组
        cards.value = [];
        total.value = 0;
    }
};

// 显示新增对话框
const showAddDialog = () => {
    isEditing.value = false;
    formData.value = {
        id: null,
        name: '',
        englishName: '',
        aliasName: '',
        categoryId: null,
        categoryName: '',
        tags: '',
        country: '中国',
        province: '',
        city: '',
        district: '',
        address: '',
        transportation: '',
        recommendationReason: '',
        highlights: '',
        bestTravelTime: '',
        visitDuration: '',
        coverImageUrl: '',
        bannerImageUrl: '',
        videoUrl: '',
        imageUrls: '',
        ticketPriceDescription: '',
        minPrice: null,
        maxPrice: null,
        priceCurrency: 'CNY',
        isFree: false,
        openingHoursWeekday: '',
        openingHoursWeekend: '',
        openingHoursDescription: '',
        contactPhone: '',
        officialWebsite: '',
        socialMedia: '',
        isHot: false,
        isNew: false,
        isTop: false,
        status: 1,
        season: '',
        festival: '',
        tips: '',
        suitableCrowd: '',
        facilityInfo: '',
        seoTitle: '',
        seoKeywords: '',
        seoDescription: '',
        content: '',
        summary: '',
        source: '',
        author: '',
        weight: 100,
    };
    showDialog.value = true;
};

// 显示编辑对话框
const showEditDialog = (card) => {
    isEditing.value = true;
    formData.value = { ...card };
    showDialog.value = true;
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
// 提交表单
const submitForm = async () => {
    try {
        if (isEditing.value) {
            await updateHomeRecommendation(formData.value);
            showToastMessage('更新推荐成功');
        } else {
            await addHomeRecommendation(formData.value);
            showToastMessage('新增推荐成功');
        }
        await fetchScenic();
        closeDialog();
    } catch (error) {
        const message = isEditing.value ? '更新失败' : '新增失败';
        showToastMessage(message, 'error');
        console.error('操作失败:', error);
    }
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
            await deleteHomeRecommendation(deleteCardId.value);
            await fetchScenic();
            showToastMessage('删除推荐成功');
        } catch (error) {
            console.error('删除失败:', error);
            showToastMessage('删除失败', 'error');

        } finally {
            closeDeletePrompt();
        }
    }
};

// 关闭对话框
const closeDialog = () => {
    showDialog.value = false;
};

// 图片上传相关状态
const dragOver = ref(false);
const previewImage = ref('');
const fileName = ref('');
const fileSize = ref('');
const uploading = ref(false);
const progress = ref(0);

// 处理文件上传
const handleFileUpload = (event) => {
    const file = event.target.files[0];
    if (!file) return;

    // 验证文件类型
    const validTypes = ['image/jpeg', 'image/png'];
    if (!validTypes.includes(file.type)) {
        showToastMessage('仅支持JPG/PNG格式图片', 'error');
        return;
    }

    // 验证文件大小 (5MB)
    const maxSize = 5 * 1024 * 1024;
    if (file.size > maxSize) {
        showToastMessage('图片大小不能超过5MB', 'error');
        return;
    }

    // 显示预览
    const reader = new FileReader();
    reader.onload = (e) => {
        previewImage.value = e.target.result;
        fileName.value = file.name;
        fileSize.value = formatFileSize(file.size);
        formData.value.coverImageUrl = e.target.result;
    };
    reader.readAsDataURL(file);

    // 模拟上传进度
    uploading.value = true;
    progress.value = 0;
    const interval = setInterval(() => {
        progress.value += 10;
        if (progress.value >= 100) {
            clearInterval(interval);
            uploading.value = false;
            showToastMessage('图片上传成功');
        }
    }, 200);
};

// 处理拖放上传
const handleDrop = (event) => {
    dragOver.value = false;
    const file = event.dataTransfer.files[0];
    if (file) {
        const inputEvent = { target: { files: [file] } };
        handleFileUpload(inputEvent);
    }
};

// 移除图片
const removeImage = () => {
    previewImage.value = '';
    fileName.value = '';
    fileSize.value = '';
    formData.value.coverImageUrl = '';
};

// 格式化文件大小
const formatFileSize = (bytes) => {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
};

// 触发文件输入框
const triggerFileInput = () => {
    const fileInput = document.createElement('input');
    fileInput.type = 'file';
    fileInput.accept = 'image/*';
    fileInput.onchange = (event) => handleFileUpload(event);
    fileInput.click();
};

onMounted(fetchScenic);


</script>

<style scoped>
@import '@/css/Management/BackgroundManagement.css';
</style>