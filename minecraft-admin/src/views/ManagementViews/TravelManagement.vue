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
                    <button class="btn add-btn" @click="showAddDialog">新增出行计划</button>
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
                                <td>{{ card.destination }}</td>
                                <td>{{ card.startDate }}</td>
                                <td>{{ card.endDate }}</td>
                                <td>{{ card.status }}</td>
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
                    <h2>{{ isEditing ? '编辑出行计划' : '新增出行计划' }}</h2>
                    <form @submit.prevent="submitForm" class="form-container">
                        <div class="form-row">
                            <div class="form-group">
                                <label>目的地:</label>
                                <input v-model="formData.destination" required />
                            </div>
                            <div class="form-group">
                                <label>开始日期:</label>
                                <input type="date" v-model="formData.startDate" required />
                            </div>
                            <div class="form-group">
                                <label>结束日期:</label>
                                <input type="date" v-model="formData.endDate" required />
                            </div>
                            <div class="form-group">
                                <label>状态:</label>
                                <input v-model="formData.status" required />
                            </div>
                        </div>
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
import { getTravelPlanList, createTravelPlan, updateTravelPlan, deleteTravelPlan } from '@/api/travel';
import DeleteConfirmation from '@/components/PromptComponent/DeleteConfirmation.vue';
import ToastType from '@/components/PromptComponent/ToastType.vue';

const columns = [
    { key: 'checked', title: '多选' },
    { key: 'id', title: 'ID' },
    { key: 'destination', title: '目的地' },
    { key: 'startDate', title: '开始日期' },
    { key: 'endDate', title: '结束日期' },
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
    destination: '',
    startDate: '',
    endDate: '',
    status: '',
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
        const response = await getTravelPlanList(params);
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
        destination: '',
        startDate: '',
        endDate: '',
        status: '',
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
            await updateTravelPlan(formData.value);
            showToastMessage('更新成功');
        } else {
            await createTravelPlan(formData.value);
            showToastMessage('新增成功');
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
            await deleteTravelPlan(deleteCardId.value);
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

// 格式化文件大小
const formatFileSize = (bytes) => {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
};

// 处理文件上传
const handleFileUpload = (event) => {
    const file = event.target.files[0];
    if (!file) return;

    // 验证文件类型
    const validTypes = ['image/jpeg', 'image/png'];
    if (!validTypes.includes(file.type)) {
        showToastMessage('只支持JPG/PNG格式图片', 'error');
        return;
    }

    // 验证文件大小
    const maxSize = 5 * 1024 * 1024; // 5MB
    if (file.size > maxSize) {
        showToastMessage('图片大小不能超过5MB', 'error');
        return;
    }

    // 显示文件信息
    fileName.value = file.name;
    fileSize.value = formatFileSize(file.size);

    // 读取并预览图片
    const reader = new FileReader();
    reader.onload = (e) => {
        previewImage.value = e.target.result;
        formData.value.coverImage = e.target.result;
    };
    reader.readAsDataURL(file);

    // 模拟上传进度
    uploading.value = true;
    const interval = setInterval(() => {
        if (progress.value < 100) {
            progress.value += 10;
        } else {
            clearInterval(interval);
            uploading.value = false;
        }
    }, 100);

    return file;
};

// 处理拖放上传
const handleDrop = (event) => {
    dragOver.value = false;
    const file = event.dataTransfer.files[0];
    if (file) {
        const fakeEvent = { target: { files: [file] } };
        handleFileUpload(fakeEvent);
    }
};

// 移除图片
const removeImage = () => {
    previewImage.value = '';
    fileName.value = '';
    fileSize.value = '';
    formData.value.coverImage = '';
};

// 触发文件输入框
const triggerFileInput = () => {
    const fileInput = document.createElement('input');
    fileInput.type = 'file';
    fileInput.accept = 'image/*';
    fileInput.onchange = (event) => {
        const file = handleFileUpload(event);
        if (file) {
            formData.value.coverImage = file;
        }
    };
    fileInput.click();
};

onMounted(fetchScenic);


</script>

<style scoped>
@import '@/css/Management/BackgroundManagement.css';
</style>