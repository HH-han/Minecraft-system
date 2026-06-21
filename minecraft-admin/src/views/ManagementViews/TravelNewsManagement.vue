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
                    <button class="btn add-btn" @click="showAddDialog">新增新闻</button>
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
                                <td>{{ card.title }}</td>
                                <td>
                                    <img :src="card.coverImage" alt="封面图片" class="cover-image" />
                                </td>
                                <td>{{ card.content.substring(0, 15) }}...</td>
                                <td>{{ card.source }}</td>
                                <td>{{ card.viewCount }}</td>
                                <td>{{ card.likeCount }}</td>
                                <td>{{ card.commentCount }}</td>
                                <td>{{ card.collectCount }}</td>
                                <td>{{ formatDate(card.createTime) }}</td>
                                <td>{{ formatDate(card.updateTime) }}</td>
                                <td>{{ card.status === 1 ? '启用' : '禁用' }}</td>
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
                title="新闻"
                :isEdit="isEditing"
                :fields="formFields"
                :initialData="formData"
                :showImageUpload="true"
                imageUploadLabel="上传新闻封面"
                recommendedSize="推荐尺寸：1200×800px"
                imageField="coverImage"
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
import { getNewsList, addNews, updateNews, deleteNews } from '@/api/news';
import FormDialog from '@/components/FormDialog.vue';
import DeleteConfirmation from '@/components/PromptComponent/DeleteConfirmation.vue';
import ToastType from '@/components/PromptComponent/ToastType.vue';

const columns = [
    { key: 'checked', title: '多选' },
    { key: 'id', title: 'ID' },
    { key: 'title', title: '标题' },
    { key: 'coverImage', title: '图片' },
    { key: 'content', title: '内容' },
    { key: 'source', title: '来源' },
    { key: 'viewCount', title: '浏览量' },
    { key: 'likeCount', title: '点赞数' },
    { key: 'commentCount', title: '评论数' },
    { key: 'collectCount', title: '收藏数' },
    { key: 'createTime', title: '创建时间' },
    { key: 'updateTime', title: '更新时间' },
    { key: 'status', title: '状态' },
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
    coverImage: '',
    location: '',
    createTime: '',
    updateTime: '',
    collectCount: 0,
    commentCount: 0,
    likeCount: 0,
    source: '',
    status: 1,
    viewCount: 0,
});

// 表单字段配置
const formFields = [
    [
        { name: 'title', label: '新闻标题', type: 'text', required: true, placeholder: '请输入新闻标题' },
        { name: 'source', label: '新闻来源', type: 'text', required: true, placeholder: '请输入新闻来源' },
    ],
    [
        { name: 'content', label: '新闻内容', type: 'textarea', required: true, rows: 4, placeholder: '请输入新闻内容' },
    ],
    [
        { name: 'viewCount', label: '浏览量', type: 'number', min: 0, placeholder: '请输入浏览量' },
        { name: 'likeCount', label: '点赞数', type: 'number', min: 0, placeholder: '请输入点赞数' },
        { name: 'commentCount', label: '评论数', type: 'number', min: 0, placeholder: '请输入评论数' },
        { name: 'collectCount', label: '收藏数', type: 'number', min: 0, placeholder: '请输入收藏数' },
    ],
    [
        { name: 'status', label: '状态', type: 'select', required: true, options: [
            { value: 1, label: '启用' },
            { value: 0, label: '禁用' },
        ]},
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
        const response = await getNewsList(params);
        cards.value = response.data?.records || [];
        total.value = response.data?.total || 0;
    } catch (error) {
        console.error('获取数据失败:', error);
        cards.value = [];
        total.value = 0;
    }
};

// 显示新增对话框
const showAddDialog = () => {
    isEditing.value = false;
    formData.value = {
        id: '',
        title: '',
        content: '',
        coverImage: '',
        createTime: '',
        updateTime: '',
        collectCount: 0,
        commentCount: 0,
        likeCount: 0,
        source: '',
        status: 1,
        viewCount: 0,
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
    if (!data.title || !data.source || !data.content) {
        return '请填写所有必填字段';
    }
    if (!isEdit && !data.coverImage) {
        return '请上传新闻封面图片';
    }
    return null;
};

// 提交表单
const handleSubmit = async (data, isEdit) => {
    if (isEdit) {
        data.updateTime = new Date().toISOString();
        await updateNews(data);
        showToastMessage('更新成功');
    } else {
        data.createTime = new Date().toISOString();
        data.updateTime = new Date().toISOString();
        await addNews(data);
        showToastMessage('新增成功');
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
            await deleteNews(deleteCardId.value);
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

onMounted(fetchScenic);


</script>

<style scoped>
@import '@/css/Management/BackgroundManagement.css';
</style>