<template>
    <div class="management-page-background">
        <div class="container-management">
            <!-- 操作栏 -->
            <div class="action-bar">
                <div class="search-bar">
                    <div class="search-box-management">
                        <input type="text" v-model="searchKeyword" placeholder="输入ID或类型搜索"
                            class="search-input-management" />
                    </div>
                    <button class="btn search-btn" @click="handleSearch">搜索</button>
                    <button class="btn delete-btn" @click="handleReset">批量删除</button>
                </div>
                <div class="operate-bar">
                    <button class="btn import-btn" @click="handleImport">Excel数据导入导出</button>
                    <button class="btn add-btn" @click="showAddDialog">新增票务</button>
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
                                <td>{{ getTypeName(card.type) }}</td>
                                <td>{{ card.departureCity }}</td>
                                <td>{{ card.arrivalCity }}</td>
                                <td>{{ card.departureTime }}</td>
                                <td>{{ card.arrivalTime }}</td>
                                <td>{{ card.carrier }}</td>
                                <td>{{ card.price }}</td>
                                <td>{{ card.stock }}</td>
                                <td>{{ card.seatClass }}</td>
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
            <FormDialog
                v-model:visible="showDialog"
                title="票务"
                :isEdit="isEditing"
                :fields="formFields"
                :initialData="formData"
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
import { getTicketList, addTicket, updateTicket, deleteTicket } from '@/api/ticket';
import FormDialog from '@/components/FormDialog.vue';
import DeleteConfirmation from '@/components/PromptComponent/DeleteConfirmation.vue';
import ToastType from '@/components/PromptComponent/ToastType.vue';

const columns = [
    { key: 'checked', title: '多选' },
    { key: 'id', title: 'ID' },
    { key: 'type', title: '类型' },
    { key: 'departureCity', title: '出发城市' },
    { key: 'arrivalCity', title: '到达城市' },
    { key: 'departureTime', title: '出发时间' },
    { key: 'arrivalTime', title: '到达时间' },
    { key: 'carrier', title: '承运方' },
    { key: 'price', title: '价格' },
    { key: 'stock', title: '库存' },
    { key: 'seatClass', title: '座位等级' },
    { key: 'status', title: '状态' },
];

const typeMap = {
    train: '火车',
    taxi: '汽车',
    flight: '飞机',
    ship: '轮船'
};

const typeOptions = [
    { value: 'train', label: '火车' },
    { value: 'taxi', label: '汽车' },
    { value: 'flight', label: '飞机' },
    { value: 'ship', label: '轮船' }
];

const cityOptions = [
    { value: '北京', label: '北京' },
    { value: '上海', label: '上海' },
    { value: '广州', label: '广州' },
    { value: '深圳', label: '深圳' },
    { value: '成都', label: '成都' },
    { value: '杭州', label: '杭州' },
    { value: '武汉', label: '武汉' },
    { value: '西安', label: '西安' },
    { value: '重庆', label: '重庆' },
    { value: '南京', label: '南京' },
    { value: '天津', label: '天津' },
    { value: '苏州', label: '苏州' },
    { value: '郑州', label: '郑州' },
    { value: '长沙', label: '长沙' },
    { value: '沈阳', label: '沈阳' },
    { value: '青岛', label: '青岛' },
    { value: '济南', label: '济南' },
    { value: '大连', label: '大连' },
    { value: '哈尔滨', label: '哈尔滨' },
    { value: '长春', label: '长春' },
    { value: '昆明', label: '昆明' },
    { value: '福州', label: '福州' },
    { value: '厦门', label: '厦门' },
    { value: '南昌', label: '南昌' },
    { value: '贵阳', label: '贵阳' },
    { value: '太原', label: '太原' },
    { value: '石家庄', label: '石家庄' },
    { value: '兰州', label: '兰州' },
    { value: '南宁', label: '南宁' },
    { value: '东莞', label: '东莞' },
    { value: '佛山', label: '佛山' },
    { value: '宁波', label: '宁波' },
    { value: '无锡', label: '无锡' },
    { value: '温州', label: '温州' },
    { value: '珠海', label: '珠海' },
    { value: '中山', label: '中山' },
    { value: '惠州', label: '惠州' },
    { value: '海口', label: '海口' },
    { value: '三亚', label: '三亚' }
];

const seatClassOptions = [
    { value: '一等座', label: '一等座' },
    { value: '二等座', label: '二等座' },
    { value: '商务座', label: '商务座' },
    { value: '经济舱', label: '经济舱' },
    { value: '头等舱', label: '头等舱' }
];

const carrierOptions = [
    { value: '中国铁路', label: '中国铁路' },
    { value: '中国航空', label: '中国航空' },
    { value: '东方航空', label: '东方航空' },
    { value: '南方航空', label: '南方航空' },
    { value: '国泰航空', label: '国泰航空' },
    { value: '长途汽车', label: '长途汽车' },
    { value: '北京铁路局', label: '北京铁路局' },
    { value: '上海铁路局', label: '上海铁路局' },
    { value: '广州铁路局', label: '广州铁路局' },
    { value: '成都航空', label: '成都航空' },
    { value: '厦门航空', label: '厦门航空' },
    { value: '山东航空', label: '山东航空' },
    { value: '深圳航空', label: '深圳航空' },
    { value: '海南航空', label: '海南航空' },
    { value: '西部航空', label: '西部航空' },
    { value: '长安汽车', label: '长安汽车' },
    { value: '宇通客车', label: '宇通客车' }
];

const getTypeName = (type) => {
    return typeMap[type] || type;
};
const showToast = ref(false);
const toastMessage = ref('');
const toastType = ref('success');
const cards = ref([]);
const searchKeyword = ref('');
const showDialog = ref(false);
const isEditing = ref(false);
const formData = ref({
    id: '',
    type: '',
    departureCity: '',
    arrivalCity: '',
    departureTime: '',
    arrivalTime: '',
    carrier: '',
    price: 0,
    stock: 0,
    seatClass: '',
    status: 1,
});

// 表单字段配置
const formFields = [
    [
        { name: 'type', label: '类型', type: 'select', required: true, options: typeOptions, placeholder: '请选择类型' },
        { name: 'departureCity', label: '出发城市', type: 'select', required: true, options: cityOptions, placeholder: '请选择出发城市' },
        { name: 'arrivalCity', label: '到达城市', type: 'select', required: true, options: cityOptions, placeholder: '请选择到达城市' },
        { name: 'departureTime', label: '出发时间', type: 'time', required: true },
    ],
    [
        { name: 'arrivalTime', label: '到达时间', type: 'time', required: true },
        { name: 'carrier', label: '承运方', type: 'select', required: true, options: carrierOptions, placeholder: '请选择承运方' },
        { name: 'price', label: '价格', type: 'number', min: 0, required: true, placeholder: '请输入价格' },
        { name: 'stock', label: '库存', type: 'number', min: 0, required: true, placeholder: '请输入库存' },
    ],
    [
        { name: 'seatClass', label: '座位等级', type: 'select', required: true, options: seatClassOptions, placeholder: '请选择座位等级' },
        { name: 'status', label: '状态', type: 'select', options: [{ value: 1, label: '启用' }, { value: 0, label: '禁用' }] },
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
            (card.type && card.type.toLowerCase().includes(keyword)) ||
            (getTypeName(card.type) && getTypeName(card.type).toLowerCase().includes(keyword))
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
        const response = await getTicketList(params);
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
        type: '',
        departureCity: '',
        arrivalCity: '',
        departureTime: '',
        arrivalTime: '',
        carrier: '',
        price: 0,
        stock: 0,
        seatClass: '',
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
    if (!data.type || !data.departureCity || !data.arrivalCity || !data.departureTime || !data.arrivalTime || !data.carrier) {
        return '请填写所有必填字段';
    }
    return null;
};

// 提交表单
const handleSubmit = async (data, isEdit) => {
    const payload = {
        ...data,
        updateTime: new Date().toISOString(),
    };
    if (!isEdit) {
        payload.createTime = new Date().toISOString();
    }
    if (isEdit) {
        await updateTicket(payload);
        showToastMessage('更新成功');
    } else {
        await addTicket(payload);
        showToastMessage('新增成功');
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
            await deleteTicket(deleteCardId.value);
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
onMounted(fetchScenic);


</script>

<style scoped>
@import '@/css/Management/BackgroundManagement.css';
</style>