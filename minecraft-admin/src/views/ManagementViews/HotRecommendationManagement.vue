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
            <!-- 通用新增/编辑弹窗 -->
            <FormDialog
                v-model:visible="showDialog"
                title="推荐"
                :isEdit="isEditing"
                :fields="formFields"
                :initialData="formData"
                :showImageUpload="true"
                imageUploadLabel="上传封面图片"
                recommendedSize="推荐尺寸：1200×800px"
                imageField="coverImageUrl"
                :validateFn="validateForm"
                :submitFn="handleSubmit"
                @error="handleError"
            />

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
import FormDialog from '@/components/FormDialog.vue';
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
// 表单字段配置
const formFields = [
  [
    { name: 'name', label: '推荐名称', type: 'text', required: true, placeholder: '请输入推荐名称' },
    { name: 'englishName', label: '英文名', type: 'text', placeholder: '请输入英文名' },
    { name: 'aliasName', label: '别名', type: 'text', placeholder: '请输入别名' },
    { name: 'categoryId', label: '分类', type: 'select', required: true, options: [
      { value: 1, label: '自然风光' },
      { value: 2, label: '历史文化' },
      { value: 3, label: '主题乐园' },
      { value: 4, label: '美食探店' },
      { value: 5, label: '休闲度假' },
    ]},
  ],
  [
    { name: 'country', label: '国家', type: 'text', placeholder: '请输入国家' },
    { name: 'province', label: '省份', type: 'text', placeholder: '请输入省份' },
    { name: 'city', label: '城市', type: 'text', required: true, placeholder: '请输入城市' },
    { name: 'district', label: '区县', type: 'text', placeholder: '请输入区县' },
  ],
  [
    { name: 'address', label: '地址', type: 'text', placeholder: '请输入地址' },
    { name: 'transportation', label: '交通', type: 'text', placeholder: '请输入交通信息' },
    { name: 'status', label: '状态', type: 'select', required: true, options: [
      { value: 0, label: '下架' },
      { value: 1, label: '上架' },
      { value: 2, label: '审核中' },
      { value: 3, label: '待发布' },
    ]},
  ],
  [
    { name: 'recommendationReason', label: '推荐理由', type: 'textarea', rows: 3, placeholder: '请输入推荐理由' },
    { name: 'highlights', label: '亮点特色', type: 'textarea', rows: 3, placeholder: '请输入亮点特色' },
  ],
  [
    { name: 'bestTravelTime', label: '最佳时间', type: 'text', placeholder: '如：春季、全年' },
    { name: 'visitDuration', label: '游玩时长', type: 'text', placeholder: '如：2-3小时' },
    { name: 'season', label: '季节', type: 'text', placeholder: '如：四季皆宜' },
    { name: 'festival', label: '节日', type: 'text', placeholder: '如：春节、国庆' },
  ],
  [
    { name: 'ticketPriceDescription', label: '门票价格说明', type: 'text', placeholder: '如：免费' },
    { name: 'minPrice', label: '最低价格', type: 'number', step: 0.01, placeholder: '请输入最低价格' },
    { name: 'maxPrice', label: '最高价格', type: 'number', step: 0.01, placeholder: '请输入最高价格' },
    { name: 'priceCurrency', label: '货币', type: 'text', placeholder: '如：CNY' },
  ],
  [
    { name: 'openingHoursWeekday', label: '平日开放时间', type: 'text', placeholder: '如：09:00-18:00' },
    { name: 'openingHoursWeekend', label: '周末开放时间', type: 'text', placeholder: '如：09:00-20:00' },
    { name: 'openingHoursDescription', label: '开放时间说明', type: 'text', placeholder: '请输入开放时间说明' },
  ],
  [
    { name: 'contactPhone', label: '联系电话', type: 'text', placeholder: '请输入联系电话' },
    { name: 'officialWebsite', label: '官网', type: 'text', placeholder: '请输入官网地址' },
    { name: 'socialMedia', label: '社交媒体', type: 'text', placeholder: '请输入社交媒体' },
  ],
  [
    { name: 'tags', label: '标签', type: 'text', placeholder: '请输入标签，多个用逗号分隔' },
    { name: 'suitableCrowd', label: '适合人群', type: 'text', placeholder: '如：家庭、情侣' },
    { name: 'facilityInfo', label: '设施信息', type: 'text', placeholder: '请输入设施信息' },
    { name: 'tips', label: '小贴士', type: 'text', placeholder: '请输入小贴士' },
  ],
  [
    { name: 'isHot', label: '是否热门', type: 'switch' },
    { name: 'isNew', label: '是否新晋', type: 'switch' },
    { name: 'isTop', label: '是否置顶', type: 'switch' },
    { name: 'isFree', label: '是否免费', type: 'switch' },
  ],
  [
    { name: 'summary', label: '摘要', type: 'textarea', rows: 2, placeholder: '请输入摘要' },
    { name: 'content', label: '内容', type: 'textarea', rows: 2, placeholder: '请输入内容' },
  ],
  [
    { name: 'seoTitle', label: 'SEO标题', type: 'text', placeholder: '请输入SEO标题' },
    { name: 'seoKeywords', label: 'SEO关键词', type: 'text', placeholder: '请输入SEO关键词' },
    { name: 'seoDescription', label: 'SEO描述', type: 'text', placeholder: '请输入SEO描述' },
  ],
  [
    { name: 'source', label: '来源', type: 'text', placeholder: '请输入来源' },
    { name: 'author', label: '作者', type: 'text', placeholder: '请输入作者' },
    { name: 'weight', label: '排序权重', type: 'number', min: 0, placeholder: '数字越大越靠前' },
  ],
];

// 表单验证
const validateForm = (data, isEdit) => {
  if (!data.name || !data.city) {
    return '请填写推荐名称和城市';
  }
  if (!isEdit && !data.coverImageUrl) {
    return '请上传封面图片';
  }
  return null;
};

// 提交表单
const handleSubmit = async (data, isEdit) => {
  if (isEdit) {
    await updateHomeRecommendation(data);
  } else {
    await addHomeRecommendation(data);
  }
};

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

// 处理错误
const handleError = (error) => {
    showToastMessage(error.message || '操作失败', 'error');
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

onMounted(fetchScenic);


</script>

<style scoped>
@import '@/css/Management/BackgroundManagement.css';
</style>