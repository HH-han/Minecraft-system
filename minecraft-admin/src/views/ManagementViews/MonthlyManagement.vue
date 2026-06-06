<template>
  <div class="management-page-background">
    <div class="container-management">
      <!-- 顶部操作栏：搜索 + 新增 -->
      <div class="action-bar">
        <div class="search-bar">
          <div class="search-box-management">
            <input
              type="text"
              v-model="searchKeyword"
              placeholder="输入目的地名称或描述进行搜索"
              class="search-input-management"
              @keyup.enter="handleSearch"
            />
          </div>
          <button class="btn search-btn" @click="handleSearch">搜索</button>
          <button class="btn search-btn-secondary" @click="handleResetSearch">重置</button>
        </div>

        <button class="btn add-btn" @click="showAddDialog">新增推荐</button>
      </div>

      <!-- 月份筛选 -->
      <div class="action-bar filter-bar">
        <div class="filter-label">按月份筛选：</div>
        <div class="month-tabs">
          <button
            v-for="month in monthOptions"
            :key="month.value"
            :class="['month-tab', { active: currentMonth === month.value }]"
            @click="handleMonthChange(month.value)"
          >
            {{ month.label }}
          </button>
        </div>
      </div>

      <!-- 数据表格 -->
      <div class="data-table-container">
        <div class="data-table-wrapper">
          <table class="data-table">
            <thead>
              <tr>
                <th v-for="col in columns" :key="col.key" :style="{ width: col.width }">
                  {{ col.title }}
                </th>
                <th style="width: 180px">操作</th>
              </tr>
            </thead>
            <tbody>
              <tr v-if="loading">
                <td :colspan="columns.length + 1" class="empty-cell">数据加载中...</td>
              </tr>
              <tr v-else-if="recommendations.length === 0">
                <td :colspan="columns.length + 1" class="empty-cell">暂无推荐数据</td>
              </tr>
              <tr v-for="item in recommendations" :key="item.id">
                <td>
                  <input
                    type="checkbox"
                    :checked="item.checked"
                    @change="handleCheck(item)"
                    class="ui-checkbox"
                  />
                </td>
                <td>{{ item.id }}</td>
                <td>{{ item.destinationName }}</td>
                <td>
                  <img
                    :src="item.imageUrl"
                    :alt="item.destinationName"
                    class="table-thumb"
                    @click="openImagePreview(item.imageUrl)"
                  />
                </td>
                <td class="desc-cell">{{ truncate(item.description, 24) }}</td>
                <td class="desc-cell">{{ truncate(item.seasonFeatures, 20) }}</td>
                <td class="desc-cell">{{ truncate(item.climateInfo, 16) }}</td>
                <td>{{ item.recommendedDays }}天</td>
                <td>
                  <span class="tag-chip" v-for="tag in parseTags(item.tags)" :key="tag">
                    {{ tag }}
                  </span>
                </td>
                <td>
                  <label class="switch">
                    <input type="checkbox" :checked="item.isRecommended === 1" @change="toggleIsRecommended(item)" />
                    <span class="slider"
                      :class="{ 'green': item.isRecommended === 1, 'red': item.isRecommended !== 1 }"></span>
                    <span class="knob"></span>
                  </label>
                </td>
                <td>{{ item.sortOrder }}</td>
                <td>{{ item.viewCount || 0 }}</td>
                <td>{{ formatDate(item.createdAt) }}</td>
                <td>{{ formatDate(item.updatedAt) }}</td>
                <td class="table-btn-display">
                  <button class="btn details-btn" @click="showDetailDialog(item)">详情</button>
                  <button class="btn edit-btn" @click="showEditDialog(item)">编辑</button>
                  <button class="btn delete-btn" @click="handleDelete(item.id)">删除</button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <!-- 批量操作 -->
      <div class="action-bar batch-bar" v-if="checkedIds.length > 0">
        <span class="batch-info">已选 {{ checkedIds.length }} 项</span>
        <button class="btn delete-btn" @click="handleBatchDelete">批量删除</button>
      </div>

      <!-- 分页器 -->
      <div class="block">
        <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="currentPage"
          :page-sizes="[10, 20, 50, 100]"
          :page-size="pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
        />
      </div>

      <!-- 新增/编辑弹窗 -->
      <div v-if="showDialog" class="dialog-overlay" @click.self="closeDialog">
        <div class="dialog" @click.stop>
          <h2>{{ isEditing ? '编辑推荐' : '新增推荐' }}</h2>
          <form @submit.prevent="submitForm" class="form-container">
            <div class="form-group">
              <div class="image-upload-container">
                <div class="upload-header">
                  <h3>上传图片</h3>
                  <p>支持 JPG, PNG 格式，最大 5MB</p>
                </div>

                <div
                  class="upload-area"
                  @click="triggerFileInput"
                  @dragover.prevent="dragOver = true"
                  @dragleave="dragOver = false"
                  @drop.prevent="handleDrop"
                  :class="{ 'drag-active': dragOver }"
                >
                  <input
                    type="file"
                    ref="fileInput"
                    @change="handleFileUpload"
                    accept="image/*"
                    class="file-input"
                  />

                  <div class="upload-content">
                    <div class="upload-icon">
                      <svg viewBox="0 0 24 24">
                        <path
                          d="M19,13H13V19H11V13H5V11H11V5H13V11H19V13Z"
                        />
                      </svg>
                    </div>
                    <p class="upload-text">点击或拖拽文件到此处</p>
                    <p class="upload-hint">推荐尺寸：1200×800px</p>
                  </div>
                </div>

                <div class="preview-container" v-if="previewImage">
                  <div class="preview-card">
                    <img :src="previewImage" alt="预览图片" class="preview-image" />
                    <div class="preview-actions">
                      <button
                        class="action-btn-image edit-btn-image"
                        @click="triggerFileInput"
                        type="button"
                      >
                        <svg viewBox="0 0 24 24">
                          <path
                            d="M20.71,7.04C21.1,6.65 21.1,6 20.71,5.63L18.37,3.29C18,2.9 17.35,2.9 16.96,3.29L15.12,5.12L18.87,8.87M3,17.25V21H6.75L17.81,9.93L14.06,6.18L3,17.25Z"
                          />
                        </svg>
                      </button>
                      <button
                        class="action-btn-image delete-btn-image"
                        @click="removeImage"
                        type="button"
                      >
                        <svg viewBox="0 0 24 24">
                          <path
                            d="M19,4H15.5L14.5,3H9.5L8.5,4H5V6H19M6,19A2,2 0 0,0 8,21H16A2,2 0 0,0 18,19V7H6V19Z"
                          />
                        </svg>
                      </button>
                    </div>
                    <div class="preview-footer">
                      <div class="file-info">
                        <span class="file-name">{{ fileName }}</span>
                        <span class="file-size">{{ fileSize }}</span>
                      </div>
                      <div class="upload-progress" v-if="uploading">
                        <div
                          class="progress-bar"
                          :style="{ width: progress + '%' }"
                        ></div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <!-- 基本信息 -->
            <div class="form-section-title">基本信息</div>
            <div class="form-row">
              <div class="form-group">
                <label>目的地名称 <span class="req">*</span></label>
                <input v-model="formData.destinationName" required placeholder="如：哈尔滨" />
              </div>
              <div class="form-group">
                <label>关联月份 <span class="req">*</span></label>
                <select v-model.number="formData.monthId" class="form-select">
                  <option
                    v-for="month in monthOptions"
                    :key="month.value"
                    :value="month.value"
                  >
                    {{ month.label }}
                  </option>
                </select>
              </div>
              <div class="form-group">
                <label>推荐游玩天数</label>
                <input
                  v-model.number="formData.recommendedDays"
                  type="number"
                  min="1"
                  placeholder="如：3"
                />
              </div>
              <div class="form-group">
                <label>排序</label>
                <input
                  v-model.number="formData.sortOrder"
                  type="number"
                  min="0"
                  placeholder="数字越小越靠前"
                />
              </div>
            </div>

            <!-- 描述信息 -->
            <div class="form-section-title">描述信息</div>
            <div class="form-row">
              <div class="form-group form-group-full">
                <label>目的地描述 <span class="req">*</span></label>
                <textarea
                  v-model="formData.description"
                  rows="2"
                  required
                  placeholder="简要描述该目的地的特点"
                ></textarea>
              </div>
              <div class="form-group form-group-full">
                <label>季节特色</label>
                <textarea
                  v-model="formData.seasonFeatures"
                  rows="2"
                  placeholder="如：哈尔滨国际冰雪节，欣赏精美冰雕雪雕"
                ></textarea>
              </div>
            </div>

            <div class="form-row">
              <div class="form-group form-group-half">
                <label>气候信息</label>
                <input
                  v-model="formData.climateInfo"
                  placeholder="如：气温约-20℃至-10℃，寒冷干燥"
                />
              </div>
              <div class="form-group form-group-half">
                <label>旅行贴士</label>
                <input
                  v-model="formData.travelTips"
                  placeholder="如：注意保暖，穿羽绒服、雪地靴"
                />
              </div>
            </div>

            <!-- 标签与状态 -->
            <div class="form-section-title">标签与状态</div>
            <div class="form-row">
              <div class="form-group form-group-half">
                <label>标签（英文逗号分隔）</label>
                <input v-model="tagsInput" placeholder='如：["冰雪","冰雕","滑雪"] 或 冰雪,冰雕,滑雪' />
                <small class="hint">支持 JSON 数组或逗号分隔文本</small>
              </div>
              <div class="form-group">
                <label>浏览量</label>
                <input v-model.number="formData.viewCount" type="number" min="0" placeholder="0" />
              </div>
              <div class="form-group">
                <label>是否推荐</label>
                <select v-model.number="formData.isRecommended" class="form-select">
                  <option :value="1">推荐中</option>
                  <option :value="0">已下架</option>
                </select>
              </div>
            </div>

            <!-- 提交按钮 -->
            <div class="dialog-buttons">
              <button type="button" class="btn cancel-btn" @click="closeDialog">取消</button>
              <button type="submit" class="btn confirm-btn" :disabled="submitting">
                {{ submitting ? '提交中...' : (isEditing ? '保存' : '创建') }}
              </button>
            </div>
          </form>
        </div>
      </div>

      <!-- 详情弹窗 -->
      <div v-if="showDetail" class="dialog-overlay" @click.self="closeDetailDialog">
        <div class="dialog dialog-detail" @click.stop>
          <h2>推荐详情</h2>
          <div class="detail-body" v-if="detailItem">
            <div class="detail-image-wrap">
              <img :src="detailItem.imageUrl" :alt="detailItem.destinationName" class="detail-big-image" />
            </div>
            <div class="detail-info-wrap">
              <div class="detail-row"><span class="detail-label">目的地：</span><span class="detail-value">{{ detailItem.destinationName }}</span></div>
              <div class="detail-row"><span class="detail-label">月份：</span><span class="detail-value">{{ getMonthLabel(detailItem.monthId) }}</span></div>
              <div class="detail-row"><span class="detail-label">推荐天数：</span><span class="detail-value">{{ detailItem.recommendedDays }} 天</span></div>
              <div class="detail-row"><span class="detail-label">排序：</span><span class="detail-value">{{ detailItem.sortOrder }}</span></div>
              <div class="detail-row"><span class="detail-label">浏览量：</span><span class="detail-value">{{ detailItem.viewCount || 0 }}</span></div>
              <div class="detail-row"><span class="detail-label">是否推荐：</span><span class="detail-value">{{ detailItem.isRecommended === 1 ? '推荐中' : '已下架' }}</span></div>
              <div class="detail-row"><span class="detail-label">描述：</span><span class="detail-value">{{ detailItem.description }}</span></div>
              <div class="detail-row"><span class="detail-label">季节特色：</span><span class="detail-value">{{ detailItem.seasonFeatures }}</span></div>
              <div class="detail-row"><span class="detail-label">气候信息：</span><span class="detail-value">{{ detailItem.climateInfo }}</span></div>
              <div class="detail-row"><span class="detail-label">旅行贴士：</span><span class="detail-value">{{ detailItem.travelTips }}</span></div>
              <div class="detail-row">
                <span class="detail-label">标签：</span>
                <span class="detail-value">
                  <span class="tag-chip" v-for="tag in parseTags(detailItem.tags)" :key="tag">{{ tag }}</span>
                </span>
              </div>
              <div class="detail-row"><span class="detail-label">创建时间：</span><span class="detail-value">{{ formatDate(detailItem.createdAt) }}</span></div>
              <div class="detail-row"><span class="detail-label">更新时间：</span><span class="detail-value">{{ formatDate(detailItem.updatedAt) }}</span></div>
            </div>
          </div>
          <div class="dialog-buttons">
            <button type="button" class="btn confirm-btn" @click="closeDetailDialog">关闭</button>
          </div>
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
import { ref, computed, onMounted } from 'vue'
import {
  getAllMonths,
  getRecommendationsPage,
  getRecommendationsList,
  addRecommendation,
  updateRecommendation,
  deleteRecommendation
} from '@/api/monthlyRecommendations'
import DeleteConfirmation from '@/components/PromptComponent/DeleteConfirmation.vue'
import ToastType from '@/components/PromptComponent/ToastType.vue'

// ============ 列定义 ============
const columns = [
  { key: 'checked', title: '多选', width: '50px' },
  { key: 'id', title: 'ID', width: '60px' },
  { key: 'destinationName', title: '目的地名称', width: '120px' },
  { key: 'imageUrl', title: '图片', width: '80px' },
  { key: 'description', title: '描述', width: '180px' },
  { key: 'seasonFeatures', title: '季节特色', width: '160px' },
  { key: 'climateInfo', title: '气候信息', width: '140px' },
  { key: 'recommendedDays', title: '天数', width: '70px' },
  { key: 'tags', title: '标签', width: '160px' },
  { key: 'isRecommended', title: '状态', width: '80px' },
  { key: 'sortOrder', title: '排序', width: '60px' },
  { key: 'viewCount', title: '浏览', width: '70px' },
  { key: 'createdAt', title: '创建时间', width: '160px' },
  { key: 'updatedAt', title: '更新时间', width: '160px' }
]

// ============ 月份选项 ============
const monthOptions = [
  { value: 1, label: '一月' },
  { value: 2, label: '二月' },
  { value: 3, label: '三月' },
  { value: 4, label: '四月' },
  { value: 5, label: '五月' },
  { value: 6, label: '六月' },
  { value: 7, label: '七月' },
  { value: 8, label: '八月' },
  { value: 9, label: '九月' },
  { value: 10, label: '十月' },
  { value: 11, label: '十一月' },
  { value: 12, label: '十二月' }
]

const getMonthLabel = (monthId) => {
  const m = monthOptions.find((i) => i.value === Number(monthId))
  return m ? m.label : String(monthId)
}

// ============ 响应式状态 ============
const showToast = ref(false)
const toastMessage = ref('')
const toastType = ref('success')

const loading = ref(false)
const recommendations = ref([])
const searchKeyword = ref('')
const currentMonth = ref(0) // 0 表示全部

const showDialog = ref(false)
const showDetail = ref(false)
const isEditing = ref(false)
const submitting = ref(false)
const detailItem = ref(null)

const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 表单数据（与后端字段对齐）
const formData = ref(createEmptyForm())

function createEmptyForm() {
  return {
    id: null,
    destinationName: '',
    description: '',
    imageUrl: '',
    seasonFeatures: '',
    climateInfo: '',
    travelTips: '',
    recommendedDays: 1,
    monthId: 1,
    isRecommended: 1,
    sortOrder: 0,
    viewCount: 0,
    tags: '[]'
  }
}

// 标签的输入控件（文本形式，提交时再转换为 JSON 字符串）
const tagsInput = ref('')

// 图片上传相关
const dragOver = ref(false)
const previewImage = ref(null)
const fileName = ref('')
const fileSize = ref('')
const uploading = ref(false)
const progress = ref(0)

// 删除提示
const isDeletePromptVisible = ref(false)
const deleteId = ref(null)
const batchMode = ref(false)

// ============ 工具方法 ============
const formatDate = (date) => {
  if (!date) return '未记录'
  try {
    return new Date(date).toLocaleString('zh-CN')
  } catch {
    return '未记录'
  }
}

const truncate = (text, max) => {
  if (!text) return '-'
  if (text.length <= max) return text
  return text.slice(0, max) + '...'
}

const parseTags = (tags) => {
  if (!tags) return []
  // 若已是数组
  if (Array.isArray(tags)) return tags
  // 若是字符串，尝试 JSON 解析
  if (typeof tags === 'string') {
    try {
      const parsed = JSON.parse(tags)
      if (Array.isArray(parsed)) return parsed
    } catch {
      // fallback：按逗号分隔
      return tags.split(/[,，]/).map((t) => t.trim()).filter(Boolean)
    }
  }
  return []
}

const showToastMessage = (message, type = 'success') => {
  toastMessage.value = message
  toastType.value = type
  showToast.value = true
  setTimeout(() => {
    showToast.value = false
  }, 3000)
}

// ============ 数据加载 ============
const fetchData = async () => {
  loading.value = true
  try {
    const response = await getRecommendationsPage(currentPage.value, pageSize.value)
    if (response && (response.code === 200 || response.code === '200' || response.code === 0 || response.code === '0')) {
      const data = response.data || {}
      // 支持 PageInfo（records / total）或 list 格式
      let list = data.records || data.list || []
      let count = data.total ?? data.totalCount ?? data.size ?? list.length
      if (typeof count !== 'number') count = list.length

      // 月份过滤 + 关键词搜索（前端二次筛选，因为后端分页接口未提供过滤参数）
      const keyword = (searchKeyword.value || '').trim()
      if (currentMonth.value && currentMonth.value > 0) {
        list = list.filter((it) => Number(it.monthId) === Number(currentMonth.value))
      }
      if (keyword) {
        const lower = keyword.toLowerCase()
        list = list.filter(
          (it) =>
            (it.destinationName || '').toLowerCase().includes(lower) ||
            (it.description || '').toLowerCase().includes(lower)
        )
      }
      recommendations.value = list.map((it) => ({ ...it, checked: false }))
      total.value = Number(count) || list.length
    } else {
      // 兼容 getRecommendationsList 的返回
      try {
        const fallback = await getRecommendationsList()
        const list =
          (fallback && (fallback.data?.records || fallback.data?.list || fallback.data || [])) || []
        const keyword = (searchKeyword.value || '').trim()
        let filtered = Array.isArray(list) ? list : []
        if (currentMonth.value && currentMonth.value > 0) {
          filtered = filtered.filter((it) => Number(it.monthId) === Number(currentMonth.value))
        }
        if (keyword) {
          const lower = keyword.toLowerCase()
          filtered = filtered.filter(
            (it) =>
              (it.destinationName || '').toLowerCase().includes(lower) ||
              (it.description || '').toLowerCase().includes(lower)
          )
        }
        recommendations.value = filtered.map((it) => ({ ...it, checked: false }))
        total.value = filtered.length
      } catch (err) {
        console.error('获取推荐数据失败：', err)
        recommendations.value = []
        total.value = 0
      }
    }
  } catch (error) {
    console.error('请求失败：', error)
    recommendations.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

// ============ 分页 ============
const handleSizeChange = (newSize) => {
  pageSize.value = newSize
  currentPage.value = 1
  fetchData()
}

const handleCurrentChange = (newPage) => {
  currentPage.value = newPage
  fetchData()
}

// ============ 搜索/筛选 ============
const handleSearch = () => {
  currentPage.value = 1
  fetchData()
}

const handleResetSearch = () => {
  searchKeyword.value = ''
  currentMonth.value = 0
  currentPage.value = 1
  fetchData()
}

const handleMonthChange = (val) => {
  currentMonth.value = val
  currentPage.value = 1
  fetchData()
}

// ============ 多选 ============
const handleCheck = (item) => {
  item.checked = !item.checked
}

const checkedIds = computed(() => {
  return recommendations.value.filter((it) => it.checked).map((it) => it.id)
})

// ============ 图片上传（前端仅转成 base64 保存在 formData.imageUrl）============
const handleFileUpload = (event) => {
  dragOver.value = false
  const file = event.target.files?.[0] || event.dataTransfer?.files?.[0]
  if (!file) return
  if (!file.type.match('image.*')) {
    showToastMessage('请选择图片文件', 'error')
    return
  }
  if (file.size > 5 * 1024 * 1024) {
    showToastMessage('文件大小不能超过 5MB', 'error')
    return
  }
  fileName.value = file.name
  fileSize.value = formatFileSize(file.size)

  const reader = new FileReader()
  reader.onload = (e) => {
    previewImage.value = e.target.result
    formData.value.imageUrl = e.target.result
  }
  reader.readAsDataURL(file)

  // 模拟上传进度
  uploading.value = true
  const interval = setInterval(() => {
    progress.value += Math.random() * 15
    if (progress.value >= 100) {
      clearInterval(interval)
      setTimeout(() => {
        uploading.value = false
        progress.value = 100
      }, 300)
    }
  }, 200)
}

const triggerFileInput = () => {
  const input = document.querySelector('.file-input')
  input && input.click()
}

const handleDrop = (e) => {
  e.preventDefault()
  handleFileUpload(e)
}

const removeImage = () => {
  previewImage.value = null
  fileName.value = ''
  fileSize.value = ''
  progress.value = 0
  uploading.value = false
  formData.value.imageUrl = ''
  const input = document.querySelector('.file-input')
  if (input) input.value = ''
}

const formatFileSize = (bytes) => {
  if (!bytes) return '0 Bytes'
  const k = 1024
  const sizes = ['Bytes', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

// ============ 新增/编辑弹窗 ============
const showAddDialog = () => {
  isEditing.value = false
  formData.value = createEmptyForm()
  tagsInput.value = ''
  resetUploadState()
  showDialog.value = true
}

const showEditDialog = (item) => {
  isEditing.value = true
  formData.value = {
    id: item.id,
    destinationName: item.destinationName || '',
    description: item.description || '',
    imageUrl: item.imageUrl || '',
    seasonFeatures: item.seasonFeatures || '',
    climateInfo: item.climateInfo || '',
    travelTips: item.travelTips || '',
    recommendedDays: item.recommendedDays ?? 1,
    monthId: Number(item.monthId) || 1,
    isRecommended: Number(item.isRecommended) ?? 1,
    sortOrder: Number(item.sortOrder) ?? 0,
    viewCount: Number(item.viewCount) ?? 0,
    tags: item.tags || '[]'
  }
  // tags 回显：支持 JSON 字符串或数组
  const tagsArr = parseTags(item.tags)
  tagsInput.value = tagsArr.join(',')
  if (formData.value.imageUrl) {
    previewImage.value = formData.value.imageUrl
    fileName.value = '已有图片'
    fileSize.value = ''
  } else {
    resetUploadState()
  }
  showDialog.value = true
}

const resetUploadState = () => {
  previewImage.value = null
  fileName.value = ''
  fileSize.value = ''
  progress.value = 0
  uploading.value = false
}

const closeDialog = () => {
  if (submitting.value) return
  showDialog.value = false
}

// ============ 详情弹窗 ============
const showDetailDialog = (item) => {
  detailItem.value = item
  showDetail.value = true
}

const closeDetailDialog = () => {
  showDetail.value = false
  detailItem.value = null
}

const openImagePreview = (url) => {
  if (!url) return
  window.open(url, '_blank')
}

// ============ 表单提交 ============
const submitForm = async () => {
  if (!formData.value.destinationName) {
    showToastMessage('请输入目的地名称', 'error')
    return
  }
  if (!formData.value.description) {
    showToastMessage('请输入目的地描述', 'error')
    return
  }
  if (!formData.value.imageUrl) {
    showToastMessage('请上传或填写图片地址', 'error')
    return
  }

  // 将 tags 输入转为 JSON 字符串
  if (tagsInput.value && tagsInput.value.trim()) {
    const input = tagsInput.value.trim()
    if (input.startsWith('[') || input.startsWith('"')) {
      // 尝试 JSON 解析
      try {
        const arr = JSON.parse(input)
        formData.value.tags = JSON.stringify(Array.isArray(arr) ? arr : [input])
      } catch {
        const parts = input.split(/[,，]/).map((s) => s.trim()).filter(Boolean)
        formData.value.tags = JSON.stringify(parts)
      }
    } else {
      const parts = input.split(/[,，]/).map((s) => s.trim()).filter(Boolean)
      formData.value.tags = JSON.stringify(parts)
    }
  } else {
    formData.value.tags = JSON.stringify([])
  }

  submitting.value = true
  try {
    // 发送到后端的 payload 按后端约定组织
    const payload = { ...formData.value }
    if (!isEditing.value) {
      // 新增：移除 id
      delete payload.id
    }

    if (isEditing.value) {
      const res = await updateRecommendation(payload)
      if (res && (res.code === 200 || res.code === '200' || res.code === 0 || res.code === '0' || res.success === true)) {
        showToastMessage('更新推荐成功')
      } else {
        showToastMessage(res?.msg || res?.message || '更新推荐成功', 'success')
      }
    } else {
      const res = await addRecommendation(payload)
      if (res && (res.code === 200 || res.code === '200' || res.code === 0 || res.code === '0' || res.success === true)) {
        showToastMessage('新增推荐成功')
      } else {
        showToastMessage(res?.msg || res?.message || '新增推荐成功', 'success')
      }
    }

    await fetchData()
    closeDialog()
  } catch (error) {
    console.error('操作失败：', error)
    const msg = isEditing.value ? '更新推荐失败' : '新增推荐失败'
    showToastMessage(msg, 'error')
  } finally {
    submitting.value = false
  }
}

// ============ 删除 ============
const toggleIsRecommended = async (item) => {
  const newVal = item.isRecommended === 1 ? 0 : 1
  try {
    const res = await updateRecommendation({
      id: item.id,
      isRecommended: newVal
    })
    if (
      res &&
      (res.code === 200 ||
        res.code === '200' ||
        res.code === 0 ||
        res.code === '0' ||
        res.success === true)
    ) {
      item.isRecommended = newVal
      showToastMessage(newVal === 1 ? '已开启推荐' : '已下架')
    } else {
      showToastMessage(res?.msg || res?.message || '状态更新成功', 'success')
      item.isRecommended = newVal
    }
  } catch (error) {
    console.error('更新推荐状态失败：', error)
    showToastMessage('状态更新失败', 'error')
  }
}

const handleDelete = (id) => {
  deleteId.value = id
  batchMode.value = false
  isDeletePromptVisible.value = true
}

const handleBatchDelete = () => {
  if (checkedIds.value.length === 0) {
    showToastMessage('请先选择要删除的项', 'error')
    return
  }
  deleteId.value = null
  batchMode.value = true
  isDeletePromptVisible.value = true
}

const closeDeletePrompt = () => {
  isDeletePromptVisible.value = false
  deleteId.value = null
  batchMode.value = false
}

const confirmDelete = async () => {
  try {
    if (batchMode.value && checkedIds.value.length > 0) {
      // 依次删除（后端无批量接口时回退到逐个删除）
      await Promise.all(checkedIds.value.map((id) => deleteRecommendation(id)))
      showToastMessage(`批量删除成功（共 ${checkedIds.value.length} 项）`)
    } else if (deleteId.value) {
      await deleteRecommendation(deleteId.value)
      showToastMessage('删除推荐成功')
    }
    await fetchData()
  } catch (error) {
    console.error('删除失败：', error)
    showToastMessage('删除推荐失败', 'error')
  } finally {
    closeDeletePrompt()
  }
}

// ============ 生命周期 ============
onMounted(() => {
  fetchData()
})
</script>

<style scoped>
@import '@/css/Management/BackgroundManagement.css';

.filter-bar {
  justify-content: flex-start;
  align-items: center;
  gap: 16px;
}

.filter-label {
  font-weight: 600;
  color: #2c3e50;
}

.month-tabs {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.month-tab {
  padding: 6px 14px;
  font-size: 13px;
  color: #475569;
  background: #f1f5f9;
  border: 1px solid transparent;
  border-radius: 18px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.month-tab:hover {
  background: #e2e8f0;
  color: #1e293b;
}

.month-tab.active {
  background: #3b82f6;
  color: #fff;
  box-shadow: 0 4px 10px rgba(59, 130, 246, 0.3);
}

.table-thumb {
  width: 48px;
  height: 48px;
  border-radius: 8px;
  object-fit: cover;
  cursor: pointer;
  border: 1px solid #e2e8f0;
}

.desc-cell {
  max-width: 200px;
  overflow: hidden;
  text-overflow: ellipsis;
}

.tag-chip {
  display: inline-block;
  padding: 2px 8px;
  margin-right: 4px;
  margin-bottom: 4px;
  background: #eff6ff;
  color: #2563eb;
  border-radius: 12px;
  font-size: 12px;
}

.status-chip {
  display: inline-block;
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 12px;
}

.status-on {
  background: #ecfdf5;
  color: #059669;
}

.status-off {
  background: #f1f5f9;
  color: #64748b;
}

.empty-cell {
  text-align: center;
  padding: 40px 0;
  color: #94a3b8;
}

.batch-bar {
  justify-content: flex-start;
  gap: 12px;
}

.batch-info {
  color: #2c3e50;
  font-weight: 500;
}

.search-btn-secondary {
  background: #64748b;
  color: #fff;
  padding: 8px 16px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  transition: background 0.2s;
}

.search-btn-secondary:hover {
  background: #475569;
}

.form-section-title {
  width: 100%;
  font-size: 14px;
  font-weight: 600;
  color: #334155;
  padding: 8px 0;
  margin: 8px 0 0;
  border-bottom: 1px solid #e2e8f0;
}

.form-group-full {
  width: 100%;
}

.form-group-half {
  flex: 1 1 45%;
}

.form-select {
  width: 100%;
  padding: 8px 12px;
  font-size: 14px;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  background: #fff;
  color: #1e293b;
}

.form-select:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.15);
}

textarea {
  width: 100%;
  padding: 8px 12px;
  font-size: 14px;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  resize: vertical;
  font-family: inherit;
  color: #1e293b;
}

textarea:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.15);
}

.req {
  color: #ef4444;
  margin-left: 2px;
}

.hint {
  display: block;
  color: #94a3b8;
  font-size: 12px;
  margin-top: 4px;
}

/* 详情弹窗 */
.dialog-detail {
  max-width: 720px;
}

.detail-body {
  display: flex;
  flex-direction: column;
  gap: 20px;
  padding: 4px 0 16px;
}

.detail-image-wrap {
  width: 100%;
  border-radius: 12px;
  overflow: hidden;
  background: #f1f5f9;
  max-height: 280px;
}

.detail-big-image {
  width: 100%;
  height: 100%;
  max-height: 280px;
  object-fit: cover;
  display: block;
}

.detail-info-wrap {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.detail-row {
  display: flex;
  align-items: flex-start;
  font-size: 14px;
  line-height: 1.6;
}

.detail-label {
  flex: 0 0 100px;
  color: #64748b;
  font-weight: 500;
}

.detail-value {
  flex: 1;
  color: #1e293b;
  word-break: break-all;
}

@media (max-width: 768px) {
  .month-tab {
    padding: 5px 10px;
    font-size: 12px;
  }
  .filter-bar {
    flex-direction: column;
    align-items: flex-start;
  }
  .detail-label {
    flex: 0 0 80px;
  }
}
</style>
