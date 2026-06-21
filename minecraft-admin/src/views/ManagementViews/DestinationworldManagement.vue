<template>
    <div class="management-page-background">
        <div class="container-management">
            <!-- 加载状态 -->
            <div v-if="loading">加载中...</div>
            <div v-else>
                <!-- 操作栏 -->
                <div class="action-bar">
                    <div class="search-bar">
                        <div class="search-box-management">
                            <input type="text" v-model="searchKeyword" placeholder="输入名称或类别搜索"
                                class="search-input-management" />
                        </div>
                        <button class="btn search-btn" @click="handleSearch">搜索</button>
                        <button class="btn delete-btn" @click="handleReset">批量删除</button>
                    </div>
                    <button class="btn add-btn" @click="showAddDialog">新增项目</button>
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
                            <tbody v-if="filteredItems.length > 0">
                                <!-- 确保 filteredItems 有数据时才渲染 -->
                                <tr v-for="item in filteredItems" :key="item.id">
                                    <td>
                                        <input type="checkbox" :checked="item.checked" @change="handleCheck(item)"
                                            class="ui-checkbox" />
                                    </td>
                                    <td>{{ item.id }}</td>
                                    <td>{{ item.name }}</td>
                                    <td>
                                        <img :src="item.image" alt="图片" style="width: 35px; height: 35px;" />
                                    </td>
                                    <td>{{ item.country }}</td>
                                    <td>{{ item.culture }}</td>
                                    <td>{{ item.description }}</td>
                                    <td>{{ item.features }}</td>
                                    <td>{{ item.history }}</td>
                                    <td>{{ item.tags }}</td>
                                    <td>{{ item.category }}</td>
                                    <td>{{ item.color }}</td>
                                    <td class="table-btn-display">
                                        <button class="btn details-btn" @click="showEditDialog(item)">详情</button>
                                        <button class="btn edit-btn" @click="showEditDialog(item)">编辑</button>
                                        <button class="btn delete-btn" @click="handleDelete(item.id)">删除</button>
                                    </td>
                                </tr>
                                <!-- 当 filteredItems 为空时显示提示信息 -->
                                <tr v-if="filteredItems.length === 0">
                                    <td colspan="7">未找到相关数据</td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                </div>
                <!-- 分页器 -->
                <div class="block">
                    <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange"
                        :current-page="currentPage" :page-sizes="[10, 20, 50, 100]" :page-size="pageSize"
                        layout="total, sizes, prev, pager, next, jumper" :total="total" />
                </div>

                <FormDialog
                    v-model:visible="showDialog"
                    title="世界特征"
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

                <!-- 删除提示框组件 -->
                <DeleteConfirmation v-if="isDeletePromptVisible" @close="closeDeletePrompt" @confirm="confirmDelete" />

                <!-- 自定义提示框组件 -->
                <ToastType v-if="showToast" :toastMessage="toastMessage" :toastType="toastType" />
            </div>
        </div>
    </div>

</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { getWorldCharacteristicsPage, addWorldCharacteristics, updateWorldCharacteristics, deleteWorldCharacteristics } from '@/api/worldcharacteristics';
import FormDialog from '@/components/FormDialog.vue';
import DeleteConfirmation from '@/components/PromptComponent/DeleteConfirmation.vue';
import ToastType from '@/components/PromptComponent/ToastType.vue';


// 表格列定义
const columns = [
    { key: 'checked', title: '多选' },
    { key: 'id', title: 'ID' },
    { key: 'name', title: '名称' },
    { key: 'image', title: '图片' },
    { key: 'country', title: '国家' },
    { key: 'culture', title: '文化' },
    { key: 'description', title: '描述' },
    { key: 'features', title: '特征' },
    { key: 'history', title: '历史' },
    { key: 'tags', title: '标签' },
    { key: 'category', title: '分类' },
    { key: 'color', title: '颜色' }
];

// 数据相关
const items = ref([]);
const loading = ref(true);
const searchKeyword = ref('');
const showDialog = ref(false);
const isEditing = ref(false);
const formData = ref({
    id: null,
    name: '',
    country: '',
    culture: '',
    image: '',
    description: '',
    features: '',
    history: '',
    tags: '',
    category: '',
    color: '',
});
// 表单字段配置
const formFields = [
    [
        { name: 'name', label: '名称', type: 'text', required: true, placeholder: '请输入名称' },
        { name: 'country', label: '国家', type: 'text', required: true, placeholder: '请输入国家' },
        { name: 'culture', label: '文化', type: 'text', required: true, placeholder: '请输入文化' },
        { name: 'category', label: '分类', type: 'text', required: true, placeholder: '请输入分类' },
    ],
    [
        { name: 'description', label: '描述', type: 'text', required: true, placeholder: '请输入描述' },
        { name: 'features', label: '特色', type: 'text', required: true, placeholder: '请输入特色' },
        { name: 'history', label: '历史', type: 'text', required: true, placeholder: '请输入历史' },
        { name: 'tags', label: '标签', type: 'text', placeholder: '用逗号分隔多个标签' },
    ],
    [
        { name: 'color', label: '颜色', type: 'text', placeholder: '请输入颜色值' },
    ],
];

// 提示框相关
const showToast = ref(false);
const toastMessage = ref('');
const toastType = ref('success');

// 分页相关
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);

// 删除提示框相关
const isDeletePromptVisible = ref(false);
const deleteItemId = ref(null);

// 格式化城市显示
const formatCities = (cities) => {
    return cities.join(', ');
};

// 搜索功能
const filteredItems = computed(() => {
    console.log('items.value:', items.value);
    if (!items.value) return [];
    const keyword = searchKeyword.value.toLowerCase();
    return items.value.filter(
        (item) =>
            item.name.toLowerCase().includes(keyword) ||
            item.category.toLowerCase().includes(keyword)
    );
});

// 获取数据
const fetchItems = async () => {
    try {
        const response = await getWorldCharacteristicsPage(currentPage.value, pageSize.value);
        console.log('后端返回的数据:', response.data);
        items.value = response.data?.records || response.data?.list || [];

        // 更新分页参数
        total.value = response.data?.total || 0;
    } catch (error) {
        console.error('获取数据失败:', error);
        items.value = [];
        total.value = 0;
    } finally {
        loading.value = false;
    }
};

// 显示新增对话框
const showAddDialog = () => {
    isEditing.value = false;
    formData.value = {
        id: null,
        name: '',
        country: '',
        culture: '',
        image: '',
        description: '',
        features: '',
        history: '',
        tags: '',
        category: '',
        color: '',
    };
    showDialog.value = true;
};

// 显示编辑对话框
const showEditDialog = (item) => {
    isEditing.value = true;
    formData.value = {
        id: item.id,
        name: item.name,
        country: item.country,
        culture: item.culture,
        image: item.image,
        description: item.description,
        features: item.features,
        history: item.history,
        tags: item.tags || '',
        category: item.category || '',
        color: item.color || '',
    };
    showDialog.value = true;
};

// 表单验证
const validateForm = (data, isEdit) => {
    if (!data.name || !data.country || !data.culture || !data.category) {
        return '请填写所有必填字段';
    }
    return null;
};

// 提交表单
const handleSubmit = async (data, isEdit) => {
    const submitData = { ...data };
    if (isEdit) {
        await updateWorldCharacteristics(submitData);
        showToastMessage('更新世界特征成功');
    } else {
        await addWorldCharacteristics(submitData);
        showToastMessage('新增世界特征成功');
    }
    await fetchItems();
};

// 处理错误
const handleError = (error) => {
    showToastMessage(error.message || '操作失败', 'error');
};

// 删除项目
const handleDelete = (id) => {
    deleteItemId.value = id;
    isDeletePromptVisible.value = true;
};

const confirmDelete = async () => {
    if (deleteItemId.value) {
        try {
            await deleteWorldCharacteristics(deleteItemId.value);
            await fetchItems();
            showToastMessage('删除世界特征成功');
        } catch (error) {
            console.error('删除失败:', error);
            showToastMessage('删除世界特征失败', 'error');
        } finally {
            closeDeletePrompt();
        }
    }
};

// 关闭删除提示框
const closeDeletePrompt = () => {
    isDeletePromptVisible.value = false;
    deleteItemId.value = null;
};

// 显示提示消息
const showToastMessage = (message, type = 'success') => {
    toastMessage.value = message;
    toastType.value = type;
    showToast.value = true;
    setTimeout(() => {
        showToast.value = false;
    }, 3000);
};

// 分页处理
const handleSizeChange = (newSize) => {
    pageSize.value = newSize;
    fetchItems(); // 移除 currentPage.value = 1 保持当前页码
};

const handleCurrentChange = (newPage) => {
    currentPage.value = newPage;
    fetchItems();
};

// 初始化加载数据
onMounted(fetchItems);


</script>

<style scoped>
@import '@/css/Management/BackgroundManagement.css';
</style>