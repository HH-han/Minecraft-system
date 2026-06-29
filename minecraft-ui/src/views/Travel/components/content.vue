<template>
    <div class="travel-content">
        <div class="content-header">
            <h2 class="section-title">我的旅行计划</h2>
            <button class="btn-create" @click="openCreateModal">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <line x1="12" y1="5" x2="12" y2="19"></line>
                    <line x1="5" y1="12" x2="19" y2="12"></line>
                </svg>
                新增计划
            </button>
        </div>
        <div v-if="loading" class="loading">
            <div class="loading-spinner"></div>
            <p>加载中...</p>
        </div>
        <div v-else-if="error" class="error-state">
            <p>加载失败: {{ error }}</p>
            <button @click="fetchTravelPlans" class="btn retry">重试</button>
        </div>
        <div v-else class="travel-list">
            <div v-for="plan in travelPlans" :key="plan.id" class="travel-card" @click="openDetail(plan)">
                <div class="card-header">
                    <h3 class="card-title">{{ plan.title }}</h3>
                    <span class="card-status" :class="getStatusClass(plan.status)">
                        {{ getStatusText(plan.status) }}
                    </span>
                </div>
                <p class="card-description">{{ plan.description }}</p>
                <div class="card-meta">
                    <div class="meta-item">
                        <svg class="meta-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
                            <line x1="16" y1="2" x2="16" y2="6"></line>
                            <line x1="8" y1="2" x2="8" y2="6"></line>
                            <line x1="3" y1="10" x2="21" y2="10"></line>
                        </svg>
                        <span>{{ formatDate(plan.startDate) }} - {{ formatDate(plan.endDate) }}</span>
                    </div>
                    <div class="meta-item">
                        <svg class="meta-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"></path>
                            <circle cx="12" cy="10" r="3"></circle>
                        </svg>
                        <span>{{ plan.city }}</span>
                    </div>
                </div>
            </div>
            <div v-if="travelPlans.length === 0 && !loading" class="empty-state">
                <p>暂无旅行计划</p>
                <p class="empty-hint">开始规划您的第一次旅行吧</p>
            </div>
        </div>

        <Paging 
            v-if="totalPages > 0"
            :total-pages="totalPages" 
            :current-page="currentPage" 
            @update:current-page="handlePageChange" 
        />

        <div v-if="showModal" class="modal-overlay" @click="closeModal">
            <div class="modal-content" @click.stop>
                <button class="close-btn" @click="closeModal">&times;</button>
                <div class="modal-body">
                    <div class="modal-header">
                        <h2>{{ selectedPlan?.title }}</h2>
                        <span class="status-badge" :class="getStatusClass(selectedPlan?.status)">
                            {{ getStatusText(selectedPlan?.status) }}
                        </span>
                    </div>
                    <div class="modal-info">
                        <div class="info-row">
                            <div class="info-item">
                                <span class="info-label">旅行地点</span>
                                <span class="info-value">{{ selectedPlan?.city }}</span>
                            </div>
                            <div class="info-item">
                                <span class="info-label">旅行日期</span>
                                <span class="info-value">{{ formatDate(selectedPlan?.startDate) }} - {{ formatDate(selectedPlan?.endDate) }}</span>
                            </div>
                        </div>
                    </div>
                    <div class="modal-description">
                        <h3>计划描述</h3>
                        <p>{{ selectedPlan?.description }}</p>
                    </div>
                    <div class="modal-content-detail">
                        <h3>行程安排</h3>
                        <div class="content-text" v-html="selectedPlan?.content"></div>
                    </div>
                    <div class="modal-actions">
                        <button class="btn secondary" @click="closeModal">关闭</button>
                        <button class="btn danger" @click="handleDelete">删除</button>
                        <button class="btn primary" @click="openEditModal">编辑计划</button>
                    </div>
                </div>
            </div>
        </div>

        <div v-if="showCreateModal" class="modal-overlay" @click="closeCreateModal">
            <div class="modal-content create-modal" @click.stop>
                <button class="close-btn" @click="closeCreateModal">&times;</button>
                <div class="modal-body">
                    <h2 class="modal-title">新增旅行计划</h2>
                    <form @submit.prevent="handleCreate" class="create-form">
                        <div class="form-item">
                            <label>计划标题</label>
                            <input v-model="createForm.title" type="text" placeholder="请输入计划标题" required />
                        </div>
                        <div class="form-item">
                            <label>旅行地点</label>
                            <input v-model="createForm.city" type="text" placeholder="请输入旅行地点" required />
                        </div>
                        <div class="form-row">
                            <div class="form-item">
                                <label>开始日期</label>
                                <input v-model="createForm.startDate" type="date" required />
                            </div>
                            <div class="form-item">
                                <label>结束日期</label>
                                <input v-model="createForm.endDate" type="date" required />
                            </div>
                        </div>
                        <div class="form-item">
                            <label>计划描述</label>
                            <textarea v-model="createForm.description" placeholder="请输入计划描述" rows="3"></textarea>
                        </div>
                        <div class="form-item">
                            <label>行程安排</label>
                            <textarea v-model="createForm.content" placeholder="请输入行程安排" rows="5"></textarea>
                        </div>
                        <div class="form-actions">
                            <button type="button" class="btn secondary" @click="closeCreateModal">取消</button>
                            <button type="submit" class="btn primary" :disabled="creating">
                                {{ creating ? '创建中...' : '创建计划' }}
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <div v-if="showEditModal" class="modal-overlay" @click="closeEditModal">
            <div class="modal-content create-modal" @click.stop>
                <button class="close-btn" @click="closeEditModal">&times;</button>
                <div class="modal-body">
                    <h2 class="modal-title">编辑旅行计划</h2>
                    <form @submit.prevent="handleUpdate" class="create-form">
                        <div class="form-item">
                            <label>计划标题</label>
                            <input v-model="editForm.title" type="text" placeholder="请输入计划标题" required />
                        </div>
                        <div class="form-item">
                            <label>旅行地点</label>
                            <input v-model="editForm.city" type="text" placeholder="请输入旅行地点" required />
                        </div>
                        <div class="form-row">
                            <div class="form-item">
                                <label>开始日期</label>
                                <input v-model="editForm.startDate" type="date" required />
                            </div>
                            <div class="form-item">
                                <label>结束日期</label>
                                <input v-model="editForm.endDate" type="date" required />
                            </div>
                        </div>
                        <div class="form-item">
                            <label>计划描述</label>
                            <textarea v-model="editForm.description" placeholder="请输入计划描述" rows="3"></textarea>
                        </div>
                        <div class="form-item">
                            <label>行程安排</label>
                            <textarea v-model="editForm.content" placeholder="请输入行程安排" rows="5"></textarea>
                        </div>
                        <div class="form-actions">
                            <button type="button" class="btn secondary" @click="closeEditModal">取消</button>
                            <button type="submit" class="btn primary" :disabled="updating">
                                {{ updating ? '更新中...' : '更新计划' }}
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getTravelPlanList, createTravelPlan, updateTravelPlan, deleteTravelPlan } from '@/api/travel.js'
import Paging from '@/components/paging/index.vue'

const travelPlans = ref([])
const loading = ref(false)
const error = ref('')
const showModal = ref(false)
const selectedPlan = ref(null)
const showCreateModal = ref(false)
const showEditModal = ref(false)
const creating = ref(false)
const updating = ref(false)

// 分页数据
const currentPage = ref(1)
const totalPages = ref(0)
const pageSize = ref(10)

const createForm = ref({
    title: '',
    city: '',
    startDate: '',
    endDate: '',
    description: '',
    content: ''
})
const editForm = ref({
    id: null,
    title: '',
    city: '',
    startDate: '',
    endDate: '',
    description: '',
    content: ''
})

const fetchTravelPlans = async (page = 1) => {
    loading.value = true
    error.value = ''
    try {
        const response = await getTravelPlanList({ pageNum: page, pageSize: pageSize.value })
        travelPlans.value = response.data?.records || []
        totalPages.value = response.data?.pages || 0
    } catch (err) {
        error.value = err.message || '获取数据失败'
        console.error('获取旅行计划数据失败:', err)
    } finally {
        loading.value = false
    }
}

// 分页切换
const handlePageChange = (page) => {
    currentPage.value = page
    fetchTravelPlans(page)
}

const formatDate = (date) => {
    if (!date) return ''
    return date
}

const getStatusClass = (status) => {
    if (status === 1) return 'status-active'
    if (status === 0) return 'status-pending'
    return 'status-completed'
}

const getStatusText = (status) => {
    if (status === 1) return '进行中'
    if (status === 0) return '未开始'
    return '已结束'
}

const openDetail = (plan) => {
    selectedPlan.value = plan
    showModal.value = true
}

const closeModal = () => {
    showModal.value = false
    selectedPlan.value = null
}

const openEditModal = () => {
    if (!selectedPlan.value) return
    editForm.value = {
        id: selectedPlan.value.id,
        title: selectedPlan.value.title,
        city: selectedPlan.value.city,
        startDate: selectedPlan.value.startDate,
        endDate: selectedPlan.value.endDate,
        description: selectedPlan.value.description || '',
        content: selectedPlan.value.content || ''
    }
    showEditModal.value = true
}

const closeEditModal = () => {
    showEditModal.value = false
    editForm.value = {
        id: null,
        title: '',
        city: '',
        startDate: '',
        endDate: '',
        description: '',
        content: ''
    }
}

const handleUpdate = async () => {
    if (updating.value) return
    updating.value = true
    try {
        const response = await updateTravelPlan(editForm.value)
        if (response.code === 200) {
            closeEditModal()
            closeModal()
            fetchTravelPlans()
        }
    } catch (err) {
        console.error('更新旅行计划失败:', err)
    } finally {
        updating.value = false
    }
}

const handleDelete = async () => {
    if (!selectedPlan.value) return
    try {
        await ElMessageBox.confirm('确定要删除这个旅行计划吗？', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning',
        })
        const response = await deleteTravelPlan(selectedPlan.value.id)
        if (response.code === 200) {
            ElMessage.success('删除成功')
            closeModal()
            showEditModal.value = false
            fetchTravelPlans()
        }
    } catch (err) {
        if (err !== 'cancel') {
            console.error('删除旅行计划失败:', err)
        }
    }
}

const openCreateModal = () => {
    showCreateModal.value = true
}

const closeCreateModal = () => {
    showCreateModal.value = false
    createForm.value = {
        title: '',
        city: '',
        startDate: '',
        endDate: '',
        description: '',
        content: ''
    }
}

const handleCreate = async () => {
    if (creating.value) return
    creating.value = true
    try {
        const response = await createTravelPlan(createForm.value)
        if (response.code === 200) {
            closeCreateModal()
            fetchTravelPlans()
        }
    } catch (err) {
        console.error('创建旅行计划失败:', err)
    } finally {
        creating.value = false
    }
}

onMounted(() => {
    fetchTravelPlans()
})
</script>
<style scoped>
.travel-content {
    max-width: 1200px;
    margin: 0 auto;
    padding: 40px 22px;
}

.content-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 32px;
}

.section-title {
    font-size: 28px;
    font-weight: 700;
    color: #1d1d1f;
    margin: 0;
}

.btn-create {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 12px 24px;
    background: #2997ff;
    color: white;
    border: none;
    border-radius: 12px;
    font-size: 15px;
    font-weight: 500;
    cursor: pointer;
    transition: all 0.2s;
}

.btn-create svg {
    width: 18px;
    height: 18px;
}

.btn-create:hover {
    background: #0066cc;
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(41, 151, 255, 0.3);
}

.loading, .error-state {
    text-align: center;
    padding: 60px 20px;
}

.loading-spinner {
    width: 40px;
    height: 40px;
    border: 3px solid #f5f5f7;
    border-top-color: #2997ff;
    border-radius: 50%;
    animation: spin 1s linear infinite;
    margin: 0 auto 20px;
}

@keyframes spin {
    to { transform: rotate(360deg); }
}

.error-state .btn {
    margin-top: 16px;
    padding: 12px 24px;
    background: #2997ff;
    color: white;
    border: none;
    border-radius: 12px;
    cursor: pointer;
}

.travel-list {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(340px, 1fr));
    gap: 24px;
}

.travel-card {
    background: #f5f5f7;
    border-radius: 24px;
    padding: 28px 24px;
    transition: transform 0.2s, box-shadow 0.2s;
    cursor: pointer;
}

.travel-card:hover {
    transform: scale(1.01);
    box-shadow: 0 20px 30px -12px rgba(0,0,0,0.1);
}

.card-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 12px;
}

.card-title {
    font-size: 20px;
    font-weight: 600;
    color: #1d1d1f;
    margin: 0;
    flex: 1;
    padding-right: 12px;
}

.card-status {
    font-size: 12px;
    padding: 4px 10px;
    border-radius: 16px;
    font-weight: 500;
    white-space: nowrap;
}

.status-active {
    background: #d1fae5;
    color: #065f46;
}

.status-pending {
    background: #fef3c7;
    color: #92400e;
}

.status-completed {
    background: #e5e5e7;
    color: #6e6e73;
}

.card-description {
    font-size: 15px;
    color: #6e6e73;
    line-height: 1.5;
    margin-bottom: 20px;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
}

.card-meta {
    display: flex;
    flex-direction: column;
    gap: 8px;
}

.meta-item {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 14px;
    color: #6e6e73;
}

.meta-icon {
    width: 16px;
    height: 16px;
}

.empty-state {
    text-align: center;
    padding: 80px 20px;
}

.empty-state p {
    font-size: 18px;
    color: #6e6e73;
}

.empty-hint {
    margin-top: 8px;
    font-size: 14px !important;
}

.modal-overlay {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: rgba(0,0,0,0.5);
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 1000;
    animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
    from { opacity: 0; }
    to { opacity: 1; }
}

.modal-content {
    background: white;
    border-radius: 24px;
    max-width: 640px;
    width: 90%;
    max-height: 80vh;
    overflow-y: auto;
    position: relative;
    animation: slideUp 0.3s ease;
}

@keyframes slideUp {
    from {
        opacity: 0;
        transform: translateY(30px);
    }
    to {
        opacity: 1;
        transform: translateY(0);
    }
}

.close-btn {
    position: absolute;
    top: 16px;
    right: 16px;
    background: none;
    border: none;
    font-size: 28px;
    cursor: pointer;
    color: #6e6e73;
    z-index: 10;
}

.close-btn:hover {
    color: #1d1d1f;
}

.modal-body {
    padding: 32px;
}

.modal-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;
}

.modal-header h2 {
    font-size: 28px;
    font-weight: 700;
    color: #1d1d1f;
    margin: 0;
}

.status-badge {
    font-size: 13px;
    padding: 6px 14px;
    border-radius: 20px;
    font-weight: 500;
}

.modal-info {
    background: #f5f5f7;
    border-radius: 16px;
    padding: 20px;
    margin-bottom: 24px;
}

.info-row {
    display: flex;
    gap: 32px;
}

.info-item {
    display: flex;
    flex-direction: column;
}

.info-label {
    font-size: 12px;
    color: #6e6e73;
    margin-bottom: 4px;
}

.info-value {
    font-size: 15px;
    font-weight: 500;
    color: #1d1d1f;
}

.modal-description {
    margin-bottom: 24px;
}

.modal-description h3,
.modal-content-detail h3 {
    font-size: 18px;
    font-weight: 600;
    color: #1d1d1f;
    margin-bottom: 12px;
}

.modal-description p {
    font-size: 15px;
    color: #6e6e73;
    line-height: 1.6;
}

.modal-content-detail {
    margin-bottom: 24px;
}

.content-text {
    font-size: 15px;
    color: #6e6e73;
    line-height: 1.6;
    white-space: pre-wrap;
}

.modal-actions {
    display: flex;
    gap: 16px;
}

.modal-actions .btn {
    flex: 1;
    padding: 14px 24px;
    border: none;
    border-radius: 12px;
    font-size: 16px;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.2s;
}

.modal-actions .btn.secondary {
    background: #f5f5f7;
    color: #1d1d1f;
}

.modal-actions .btn.secondary:hover {
    background: #e5e5e7;
}

.modal-actions .btn.primary {
    background: #2997ff;
    color: white;
}

.modal-actions .btn.primary:hover {
    background: #0066cc;
}

.modal-actions .btn.danger {
    background: #ef4444;
    color: white;
}

.modal-actions .btn.danger:hover {
    background: #dc2626;
}

@media (max-width: 768px) {
    .travel-list {
        grid-template-columns: 1fr;
    }

    .info-row {
        flex-direction: column;
        gap: 16px;
    }

    .content-header {
        flex-direction: column;
        gap: 16px;
        align-items: flex-start;
    }

    .btn-create {
        width: 100%;
        justify-content: center;
    }
}

.create-modal {
    max-width: 560px;
}

.create-form {
    display: flex;
    flex-direction: column;
    gap: 20px;
}

.form-item {
    display: flex;
    flex-direction: column;
    gap: 8px;
}

.form-item label {
    font-size: 14px;
    font-weight: 500;
    color: #1d1d1f;
}

.form-item input,
.form-item textarea {
    padding: 12px 16px;
    border: 1px solid #d2d2d6;
    border-radius: 12px;
    font-size: 15px;
    color: #1d1d1f;
    transition: border-color 0.2s, box-shadow 0.2s;
}

.form-item input:focus,
.form-item textarea:focus {
    outline: none;
    border-color: #2997ff;
    box-shadow: 0 0 0 3px rgba(41, 151, 255, 0.15);
}

.form-item textarea {
    resize: vertical;
    min-height: 80px;
}

.form-row {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 16px;
}

.form-actions {
    display: flex;
    gap: 16px;
    margin-top: 8px;
}

.form-actions .btn {
    flex: 1;
    padding: 14px 24px;
    border: none;
    border-radius: 12px;
    font-size: 16px;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.2s;
}

.form-actions .btn.secondary {
    background: #f5f5f7;
    color: #1d1d1f;
}

.form-actions .btn.secondary:hover {
    background: #e5e5e7;
}

.form-actions .btn.primary {
    background: #2997ff;
    color: white;
}

.form-actions .btn.primary:hover:not(:disabled) {
    background: #0066cc;
}

.form-actions .btn.primary:disabled {
    background: #a0cfff;
    cursor: not-allowed;
}
</style>
