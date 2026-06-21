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
                                <td>{{ card.title }}</td>
                                <td>{{ card.content.substring(0, 15) }}...</td>
                                <td>{{ formatDate(card.createdAt) }}</td>
                                <td>{{ formatDate(card.updatedAt) }}</td>
                                <td>{{ card.color }}</td>
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
                title="笔记"
                :isEdit="isEditing"
                :fields="formFields"
                :initialData="formData"
                :showImageUpload="true"
                imageUploadLabel="上传笔记图片"
                recommendedSize="推荐尺寸：1200×800px"
                imageField="image"
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
import FormDialog from '@/components/FormDialog.vue';
import DeleteConfirmation from '@/components/PromptComponent/DeleteConfirmation.vue';
import ToastType from '@/components/PromptComponent/ToastType.vue';

const columns = [
    { key: 'checked', title: '多选' },
    { key: 'id', title: '笔记ID' },
    { key: 'username', title: '用户名' },
    { key: 'title', title: '笔记标题' },
    { key: 'content', title: '笔记内容' },
    { key: 'createdAt', title: '创建时间' },
    { key: 'updatedAt', title: '更新时间' },
    { key: 'color', title: '颜色' },
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
    image: '',
    location: '',
    createdAt: '',
    updatedAt: '',
});

// 表单字段配置
const formFields = [
    [
        { name: 'title', label: '笔记标题', type: 'text', required: true, placeholder: '请输入笔记标题' },
        { name: 'location', label: '笔记地点', type: 'text', required: true, placeholder: '请输入笔记地点' },
    ],
    [
        { name: 'content', label: '笔记内容', type: 'textarea', required: true, rows: 3, placeholder: '请输入笔记内容' },
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
        const response = await request.get('/api/public/notes/all', { params });
        cards.value = response.data.list.map(card => ({
            ...card,
            images: typeof card.images === 'string' ? JSON.parse(card.images) : card.images
        }));
        total.value = response.data.total;
    } catch (error) {
        console.error('获取笔记数据失败:', error);
    }
};

// 显示新增对话框
const showAddDialog = () => {
    isEditing.value = false;
    formData.value = {
        id: '',
        title: '',
        content: '',
        image: '',
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
    if (!data.title || !data.content || !data.location) {
        return '请填写所有必填字段';
    }
    if (!isEdit && !data.image) {
        return '请上传笔记图片';
    }
    return null;
};

// 提交表单
const handleSubmit = async (data, isEdit) => {
    if (isEdit) {
        data.updatedAt = new Date().toISOString();
        await request.put(`/api/public/notes/${data.id}`, data);
        showToastMessage('更新笔记成功');
    } else {
        data.createdAt = new Date().toISOString();
        await request.post('/api/public/notes', data);
        showToastMessage('新增笔记成功');
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
};;

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
            await request.delete(`/api/public/notes/${deleteCardId.value}`);
            await fetchScenic();
            closeDeletePrompt();
            showToastMessage('删除笔记成功');
        } catch (error) {
            console.error('删除失败:', error);
            showToastMessage('删除笔记失败', 'error');

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