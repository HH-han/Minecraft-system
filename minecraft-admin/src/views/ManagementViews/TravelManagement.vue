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
            <!-- 通用新增/编辑弹窗 -->
            <FormDialog
                v-model:visible="showDialog"
                title="出行计划"
                :isEdit="isEditing"
                :fields="formFields"
                :initialData="formData"
                :showImageUpload="false"
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
import { getTravelPlanList, createTravelPlan, updateTravelPlan, deleteTravelPlan } from '@/api/travel';
import FormDialog from '@/components/FormDialog.vue';
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

// 表单字段配置
const formFields = [
    [
        { name: 'destination', label: '目的地', type: 'text', required: true, placeholder: '请输入目的地' },
        { name: 'startDate', label: '开始日期', type: 'text', required: true, placeholder: '请输入开始日期' },
        { name: 'endDate', label: '结束日期', type: 'text', required: true, placeholder: '请输入结束日期' },
        { name: 'status', label: '状态', type: 'text', required: true, placeholder: '请输入状态' },
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

// 表单验证
const validateForm = (data, isEdit) => {
    if (!data.destination || !data.startDate || !data.endDate || !data.status) {
        return '请填写所有必填字段';
    }
    return null;
};

// 提交表单
const handleSubmit = async (data, isEdit) => {
    if (isEdit) {
        await updateTravelPlan(data);
        showToastMessage('更新成功');
    } else {
        await createTravelPlan(data);
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

onMounted(fetchScenic);


</script>

<style scoped>
@import '@/css/Management/BackgroundManagement.css';
</style>