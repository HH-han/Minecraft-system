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
                <div class="operate-bar">
                    <button class="btn import-btn" @click="handleImport">Excel数据导入导出</button>
                    <button class="btn add-btn" @click="showAddDialog">新增用户</button>
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
                                    <input type="checkbox" :checked="card.checked" @change="handleCheck(card)"
                                        class="ui-checkbox" />
                                </td>
                                <td>{{ card.id }}</td>
                                <td>{{ card.username }}</td>
                                <td>{{ card.collectionname }}</td>
                                <td>
                                    <img :src="card.image" alt="美食图片" style="width: 35px; height: 35px;"
                                        @click="triggerFileInput(card)" />
                                </td>
                                <td>{{ card.price }}</td>
                                <td>{{ card.location }}</td>
                                <td>{{ card.profile }}</td>
                                <td>{{ card.characteristics }}</td>
                                <td>{{ card.score }}</td>
                                <td>{{ card.collection }}</td>
                                <td>{{ card.sales }}</td>
                                <td>{{ card.classification }}</td>
                                <td>{{ formatDate(card.createTime) }}</td>
                                <td>{{ formatDate(card.updateTime) }}</td>
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
                title="收藏"
                :isEdit="isEditing"
                :fields="formFields"
                :initialData="formData"
                :showImageUpload="true"
                imageUploadLabel="上传图片"
                recommendedSize="推荐尺寸：1200×800px"
                imageField="image"
                :validateFn="validateForm"
                :submitFn="handleSubmit"
                @error="handleError"
            />

            <DeleteConfirmation v-if="isDeletePromptVisible" @close="closeDeletePrompt" @confirm="confirmDelete" />
            <!-- 自定义提示框组件 -->
            <ToastType v-if="showToast" :toastMessage="toastMessage" :toastType="toastType" />
        </div>
    </div>

</template>

<script setup>

import { ref, computed, onMounted } from 'vue';
import request from '@/utils/request';
import FormDialog from '@/components/FormDialog.vue';
import DeleteConfirmation from '@/components/PromptComponent/DeleteConfirmation.vue';
import ToastType from '@/components/PromptComponent/ToastType.vue';

const columns = [
    { key: 'checked', title: '多选' },
    { key: 'id', title: '收藏ID' },
    { key: 'username', title: '用户名' },
    { key: 'collectionname', title: '收藏名称' },
    { key: 'image', title: '封面图' },
    { key: 'price', title: '价格' },
    { key: 'location', title: '收藏地点' },
    { key: 'profile', title: '收藏简介' },
    { key: 'characteristics', title: '收藏特点' },
    { key: 'score', title: '评分' },
    { key: 'collection', title: '收藏数量' },
    { key: 'sales', title: '销量' },
    { key: 'classification', title: '分类' },
    { key: 'createdTime', title: '创建时间' },
    { key: 'updatedTime', title: '更新时间' },
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
    username: '',
    collectionname: '',
    image: '',
    price: '',
    location: '',
    profile: '',
    characteristics: '',
    score: '',
    collection: '',
    sales: '',
    classification: '',
    createdTime: '',
    updatedTime: '',
});

// 表单字段配置
const formFields = [
    [
        { name: 'username', label: '用户名', type: 'text', required: true, placeholder: '请输入用户名' },
        { name: 'collectionname', label: '收藏名称', type: 'text', required: true, placeholder: '请输入收藏名称' },
        { name: 'price', label: '价格', type: 'text', required: true, placeholder: '请输入价格' },
        { name: 'location', label: '收藏地点', type: 'text', required: true, placeholder: '请输入收藏地点' },
    ],
    [
        { name: 'profile', label: '收藏简介', type: 'text', required: true, placeholder: '请输入收藏简介' },
        { name: 'characteristics', label: '收藏特点', type: 'text', required: true, placeholder: '请输入收藏特点' },
        { name: 'score', label: '评分', type: 'text', required: true, placeholder: '请输入评分' },
        { name: 'collection', label: '收藏数量', type: 'text', required: true, placeholder: '请输入收藏数量' },
    ],
    [
        { name: 'sales', label: '销量', type: 'text', required: true, placeholder: '请输入销量' },
        { name: 'classification', label: '分类', type: 'text', required: true, placeholder: '请输入分类' },
    ],
];

// 格式化日期显示
const formatDate = (date) => {
    if (!date || isNaN(new Date(date))) return '未知日期';
    const options = { year: 'numeric', month: 'long', day: 'numeric' };
    return new Intl.DateTimeFormat('zh-CN', options).format(new Date(date));
};

// 搜索功能
const filteredCards = computed(() => {
    const keyword = searchKeyword.value.toLowerCase();
    return (cards.value || []).filter(
        (card) =>
            String(card.id).includes(keyword) ||
            (card.title && card.title.toLowerCase().includes(keyword))
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
    const response = await request.get('/api/public/travel-collections/search', { params });
    cards.value = (response.data?.records || []).map(card => ({
      ...card,
      images: typeof card.images === 'string' ? JSON.parse(card.images) : card.images
    }));
    console.log(cards.value, "数据");
    total.value = response.data?.total || 0;
  } catch (error) {
    console.error('获取笔记数据失败:', error);
    cards.value = [];
    total.value = 0;
  }
};

// 显示新增对话框
const showAddDialog = () => {
    isEditing.value = false;
    formData.value = {
        id: '',
        username: '',
        collectionname: '',
        image: '',
        price: '',
        location: '',
        profile: '',
        characteristics: '',
        score: '',
        collection: '',
        sales: '',
        classification: '',
        createdTime: '',
        updatedTime: '',
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
    if (!data.username) {
        return '请输入用户名';
    }
    if (!data.collectionname) {
        return '请输入收藏名称';
    }
    return null;
};

// 提交表单
const handleSubmit = async (data, isEdit) => {
    const submitData = { ...data };
    if (isEdit) {
        submitData.updatedTime = new Date().toISOString();
        await request.put(`/api/public/travel-collections/${submitData.id}`, submitData);
        showToastMessage('更新收藏成功');
    } else {
        submitData.createdTime = new Date().toISOString();
        await request.post('/api/public/travel-collections', submitData);
        showToastMessage('新增收藏成功');
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
            await request.delete(`/api/public/travel-collections/${deleteCardId.value}`);
            await fetchScenic();
            closeDeletePrompt();
            showToastMessage('删除笔记成功');
            console.log(deleteCardId.value, "删除成功");
        } catch (error) {
            console.error('删除失败:', error);
            showToastMessage('删除笔记失败', 'error');
        } finally {
            closeDeletePrompt();
        }
    }
};

// 触发文件输入框 - 用于表格图片预览
const triggerFileInput = (card) => {
    if (card.image) {
        window.open(card.image, '_blank');
    }
};

onMounted(fetchScenic);


</script>

<style scoped>
@import '@/css/Management/BackgroundManagement.css';
</style>