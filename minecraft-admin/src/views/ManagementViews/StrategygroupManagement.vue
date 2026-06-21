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
                <button class="btn add-btn" @click="showAddDialog">新增策略组</button>
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
                                <td>
                                    <img :src="card.images ? card.images.split(',')[0] : ''" alt="旅行团图片" style="width: 35px; height: 35px;"
                                        @click="triggerFileInput(card)" />
                                </td>
                                <td>{{ card.destination }}</td>
                                <td>{{ card.maxMembers }}</td>
                                <td>{{ card.currentMembers }}</td>
                                <td>{{ card.price }}</td>
                                <td>{{ card.description.substring(0, 15) }}...</td>
                                <td>{{ card.status === 1 ? '招募中' : '已结束' }}</td>
                                <td>{{ formatDate(card.createTime) }}</td>
                                <td>{{ formatDate(card.updateTime) }}</td>
                                <td>{{ formatDate(card.departureTime) }}</td>
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
            <FormDialog
                v-model:visible="showDialog"
                title="旅行团"
                :isEdit="isEditing"
                :fields="formFields"
                :initialData="formData"
                :showImageUpload="true"
                imageUploadLabel="上传图片"
                recommendedSize="推荐尺寸：1200×800px"
                imageField="images"
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
import { getGroupList, getGroupDetail, createGroup, joinGroup, leaveGroup, updateGroup, deleteGroup } from '@/api/group';
import FormDialog from '@/components/FormDialog.vue';
import DeleteConfirmation from '@/components/PromptComponent/DeleteConfirmation.vue';
import ToastType from '@/components/PromptComponent/ToastType.vue';

const columns = [
    { key: 'checked', title: '多选' },
    { key: 'id', title: 'ID' },
    { key: 'name', title: '旅行团名称' },
    { key: 'images', title: '图片预览' },
    { key: 'destination', title: '目的地' },
    { key: 'maxMembers', title: '最大成员数' },
    { key: 'currentMembers', title: '当前成员数' },
    { key: 'price', title: '价格' },
    { key: 'description', title: '描述' },
    { key: 'status', title: '状态' },
    { key: 'createTime', title: '创建时间' },
    { key: 'updateTime', title: '修改时间' },
    { key: 'departureTime', title: '出发时间' }

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
    creatorId: '',
    name: '',
    description: '',
    destination: '',
    departureTime: '',
    returnTime: '',
    maxMembers: 0,
    currentMembers: 0,
    price: 0,
    images: '',
    status: 1,
});

// 表单字段配置
const formFields = [
    [
        { name: 'name', label: '旅行团名称', type: 'text', required: true, placeholder: '请输入旅行团名称' },
        { name: 'destination', label: '目的地', type: 'text', required: true, placeholder: '请输入目的地' },
        { name: 'departureTime', label: '出发时间', type: 'datetime', required: true },
        { name: 'returnTime', label: '返回时间', type: 'datetime', required: true },
    ],
    [
        { name: 'maxMembers', label: '最大成员数', type: 'number', min: 1, required: true, placeholder: '请输入最大成员数' },
        { name: 'currentMembers', label: '当前成员数', type: 'number', min: 0, required: true, placeholder: '请输入当前成员数' },
        { name: 'price', label: '价格', type: 'number', min: 0, step: 0.01, required: true, placeholder: '请输入价格' },
        { name: 'status', label: '状态', type: 'select', required: true, options: [{ value: 1, label: '招募中' }, { value: 0, label: '已结束' }] },
    ],
    [
        { name: 'description', label: '描述', type: 'textarea', fullWidth: true, rows: 4, required: true, placeholder: '请输入描述' },
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
            (card.name && card.name.toLowerCase().includes(keyword)) ||
            (card.destination && card.destination.toLowerCase().includes(keyword))
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
            pageNum: currentPage.value,
            pageSize: pageSize.value
        };
        const response = await getGroupList(params);
        if (response.data && response.data.records) {
            cards.value = response.data.records.map(card => ({
                ...card,
                checked: false
            }));
            total.value = response.data.total;
        }
    } catch (error) {
        console.error('获取旅行团数据失败:', error);
    }
};

// 显示新增对话框
const showAddDialog = () => {
    isEditing.value = false;
    formData.value = {
        id: '',
        creatorId: '',
        name: '',
        description: '',
        destination: '',
        departureTime: '',
        returnTime: '',
        maxMembers: 0,
        currentMembers: 0,
        price: 0,
        images: '',
        status: 1,
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
// 表单验证
const validateForm = (data, isEdit) => {
    if (!data.name || !data.destination || !data.departureTime || !data.returnTime) {
        return '请填写所有必填字段';
    }
    return null;
};

// 提交表单
const handleSubmit = async (data, isEdit) => {
    if (isEdit) {
        await updateGroup(data);
        showToastMessage('更新旅行团成功');
    } else {
        await createGroup(data);
        showToastMessage('新增旅行团成功');
    }
    await fetchScenic();
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
            await deleteGroup(deleteCardId.value);
            showToastMessage('删除旅行团成功');
            await fetchScenic();
        } catch (error) {
            console.error('删除失败:', error);
            showToastMessage('删除旅行团失败', 'error');

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