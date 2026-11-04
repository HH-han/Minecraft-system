<template>
  <div class="excel-page">
    <div class="page-header">
      <div>
        <h2 class="page-title">{{ t('excel.title') }}</h2>
        <p class="page-subtitle">{{ t('excel.subtitle') }}</p>
      </div>
      <el-select v-model="locale" size="small" class="locale-select" @change="changeLocale">
        <el-option v-for="item in locales" :key="item.value" :label="item.label" :value="item.value" />
      </el-select>
    </div>

    <div class="grid-2">
      <section class="panel">
        <div class="panel-title">{{ t('excel.import.title') }}</div>
        <p class="panel-tip">{{ t('excel.import.tip') }}</p>

        <div class="form-row">
          <el-select v-model="importForm.bizType" :placeholder="t('excel.bizTypePlaceholder')" class="grow">
            <el-option v-for="item in bizTypes" :key="item.code" :label="bizLabel(item.code)" :value="item.code" />
          </el-select>
          <el-radio-group v-model="importForm.strategy">
            <el-radio-button value="skip">{{ t('excel.strategy.skip') }}</el-radio-button>
            <el-radio-button value="overwrite">{{ t('excel.strategy.overwrite') }}</el-radio-button>
            <el-radio-button value="error">{{ t('excel.strategy.error') }}</el-radio-button>
          </el-radio-group>
        </div>

        <el-upload
          class="upload-box"
          drag
          action="#"
          accept=".xls,.xlsx"
          :auto-upload="false"
          :show-file-list="false"
          :limit="1"
          :on-change="handleFileChange"
          :on-exceed="handleExceed"
        >
          <el-icon class="upload-icon"><UploadFilled /></el-icon>
          <div class="upload-text">{{ t('excel.import.selectFile') }}</div>
          <div v-if="selectedFile" class="upload-file">{{ t('excel.import.fileSelected', { name: selectedFile.name }) }}</div>
        </el-upload>

        <div class="actions">
          <el-button plain @click="downloadTemplate">{{ t('excel.import.template') }}</el-button>
          <el-button type="primary" :loading="importing" :disabled="!selectedFile" @click="startImport">
            {{ t('excel.import.start') }}
          </el-button>
        </div>

        <div v-if="currentTaskNo" class="progress-box">
          <div class="progress-head">
            <span>{{ t('excel.import.progressTitle') }}</span>
            <span class="task-no">{{ t('excel.import.taskNo') }}: {{ currentTaskNo }}</span>
          </div>
          <el-progress :percentage="progress.percent" :status="progressStatus" :stroke-width="12" />
          <div class="progress-meta">
            {{ t('excel.summary', { total: progress.total, success: progress.success, fail: progress.fail }) }}
          </div>
          <div class="actions">
            <el-button v-if="progress.errorFile" size="small" @click="downloadErrorFile(currentTaskNo)">
              {{ t('excel.task.errorFile') }}
            </el-button>
            <el-button v-if="isActive(progress.status)" size="small" type="warning" plain @click="cancelCurrent">
              {{ t('excel.task.cancel') }}
            </el-button>
            <el-button size="small" text @click="resetImport">{{ t('excel.import.reimport') }}</el-button>
          </div>
        </div>
      </section>

      <section class="panel">
        <div class="panel-title">{{ t('excel.export.title') }}</div>
        <p class="panel-tip">{{ t('excel.bizTypePlaceholder') }}</p>
        <div class="form-row">
          <el-select v-model="exportForm.bizType" :placeholder="t('excel.bizTypePlaceholder')" class="grow">
            <el-option v-for="item in bizTypes" :key="item.code" :label="bizLabel(item.code)" :value="item.code" />
          </el-select>
          <el-input v-model="exportForm.keyword" :placeholder="t('excel.export.keywordPlaceholder')" clearable class="grow" />
        </div>
        <div class="actions">
          <el-button type="primary" :loading="exporting" @click="startExport">{{ t('excel.export.button') }}</el-button>
        </div>
      </section>
    </div>

    <section class="panel">
      <div class="panel-head">
        <div class="panel-title">{{ t('excel.task.title') }}</div>
        <div class="task-filters">
          <el-select v-model="filters.bizType" clearable :placeholder="t('excel.bizType')" class="filter-item">
            <el-option v-for="item in bizTypes" :key="item.code" :label="bizLabel(item.code)" :value="item.code" />
          </el-select>
          <el-select v-model="filters.status" clearable :placeholder="t('excel.task.status')" class="filter-item">
            <el-option v-for="(item, key) in statusOptions" :key="key" :label="item.label" :value="item.value" />
          </el-select>
          <el-button plain :loading="loadingTasks" @click="loadTasks">{{ t('excel.task.refresh') }}</el-button>
        </div>
      </div>

      <el-table :data="tasks" v-loading="loadingTasks" class="task-table" size="small">
        <el-table-column prop="taskNo" :label="t('excel.task.taskNo')" width="200" show-overflow-tooltip />
        <el-table-column :label="t('excel.bizType')" width="110">
          <template #default="{ row }">{{ bizLabel(row.bizType) }}</template>
        </el-table-column>
        <el-table-column :label="t('excel.task.type')" width="90">
          <template #default="{ row }">{{ row.taskType === 2 ? t('excel.task.export') : t('excel.task.import') }}</template>
        </el-table-column>
        <el-table-column prop="fileName" :label="t('excel.task.fileName')" min-width="160" show-overflow-tooltip />
        <el-table-column :label="t('excel.task.rows')" width="120">
          <template #default="{ row }">{{ row.successRows || 0 }} / {{ row.failRows || 0 }}</template>
        </el-table-column>
        <el-table-column :label="t('excel.task.status')" width="110">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)" size="small">{{ statusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column :label="t('excel.task.time')" width="170">
          <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
        </el-table-column>
        <el-table-column :label="t('excel.task.actions')" width="180" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="row.failRows > 0 && row.taskType === 1"
              size="small"
              text
              type="primary"
              @click="downloadErrorFile(row.taskNo)"
            >
              {{ t('excel.task.errorFile') }}
            </el-button>
            <el-button v-if="isActive(row.status)" size="small" text type="warning" @click="cancelTask(row.taskNo)">
              {{ t('excel.task.cancel') }}
            </el-button>
          </template>
        </el-table-column>
        <template #empty>
          <div class="empty-text">{{ t('excel.task.empty') }}</div>
        </template>
      </el-table>

      <el-pagination
        class="pagination"
        background
        layout="total, sizes, prev, pager, next"
        :total="pagination.total"
        :current-page="pagination.pageNum"
        :page-size="pagination.pageSize"
        :page-sizes="[10, 20, 50]"
        @current-change="handlePageChange"
        @size-change="handleSizeChange"
      />
    </section>
  </div>
</template>

<script setup>
import { computed, onBeforeUnmount, onMounted, reactive, ref } from 'vue'
import { useI18n } from 'vue-i18n'
import { ElMessage } from 'element-plus'
import {
  cancelExcelTask,
  downloadExcelErrorFile,
  downloadExcelTemplate,
  exportExcel,
  getExcelBizTypes,
  getExcelTaskList,
  getImportProgress,
  importExcel,
  saveBlob
} from '@/api/excel'
import { SUPPORTED_LOCALES, currentLocale, setLocale } from '@/locales'

const { t } = useI18n()

const locales = SUPPORTED_LOCALES
const locale = ref(currentLocale.value)

const bizTypes = ref([
  { code: 'attraction' },
  { code: 'hotel' },
  { code: 'food' },
  { code: 'product' },
  { code: 'ticket' },
  { code: 'city' },
  { code: 'points_product' }
])

const importForm = reactive({ bizType: 'attraction', strategy: 'skip' })
const exportForm = reactive({ bizType: 'attraction', keyword: '' })

const selectedFile = ref(null)
const importing = ref(false)
const exporting = ref(false)
const currentTaskNo = ref('')
const progress = ref({ percent: 0, total: 0, success: 0, fail: 0, status: 0, errorFile: false })
let pollTimer = null

const filters = reactive({ bizType: '', status: '' })
const tasks = ref([])
const loadingTasks = ref(false)
const pagination = reactive({ pageNum: 1, pageSize: 10, total: 0 })

const statusMap = {
  0: { key: 'pending', type: 'info' },
  1: { key: 'processing', type: 'primary' },
  2: { key: 'success', type: 'success' },
  3: { key: 'partialFail', type: 'warning' },
  4: { key: 'failed', type: 'danger' },
  5: { key: 'cancelled', type: 'info' }
}

const statusOptions = computed(() => {
  const options = {}
  Object.entries(statusMap).forEach(([value, item]) => {
    options[item.key] = { value: Number(value), label: t(`excel.status.${item.key}`) }
  })
  return options
})

const progressStatus = computed(() => {
  if (progress.value.status === 2) return 'success'
  if (progress.value.status === 3 || progress.value.status === 4) return 'exception'
  if (progress.value.status === 5) return 'warning'
  return undefined
})

const bizLabel = (code) => {
  if (!code) return ''
  const key = `excel.biz.${code}`
  const label = t(key)
  return label === key ? code : label
}

const statusLabel = (status) => t(`excel.status.${statusMap[status]?.key || 'pending'}`)
const statusTagType = (status) => statusMap[status]?.type || 'info'
const isActive = (status) => status === 0 || status === 1
const formatTime = (value) => (value ? String(value).replace('T', ' ').slice(0, 19) : '')

const changeLocale = async (value) => {
  await setLocale(value)
  locale.value = value
}

const handleFileChange = (uploadFile) => {
  selectedFile.value = uploadFile.raw || null
}

const handleExceed = (files) => {
  selectedFile.value = files[0]
}

const downloadTemplate = async () => {
  try {
    const blob = await downloadExcelTemplate(importForm.bizType)
    saveBlob(blob, `${bizLabel(importForm.bizType)}_template.xlsx`)
    ElMessage.success(t('excel.messages.templateDownloaded'))
  } catch (error) {
    ElMessage.error(t('excel.messages.downloadFailed'))
  }
}

const startImport = async () => {
  if (!selectedFile.value) {
    ElMessage.warning(t('excel.messages.selectFileFirst'))
    return
  }
  importing.value = true
  try {
    const res = await importExcel(importForm.bizType, selectedFile.value, {
      duplicateStrategy: importForm.strategy
    })
    currentTaskNo.value = res.data.taskNo
    progress.value = { percent: 0, total: 0, success: 0, fail: 0, status: 0, errorFile: false }
    ElMessage.success(t('excel.messages.importSubmitted'))
    pollProgress()
  } catch (error) {
    importing.value = false
  } finally {
    importing.value = false
  }
}

const pollProgress = async () => {
  stopPolling()
  const taskNo = currentTaskNo.value
  if (!taskNo) return
  try {
    const res = await getImportProgress(taskNo)
    const data = res.data || {}
    progress.value = {
      percent: data.percent || 0,
      total: data.total || 0,
      success: data.success || 0,
      fail: data.fail || 0,
      status: data.status || 0,
      errorFile: !!data.errorFile
    }
    if (!isActive(data.status)) {
      ElMessage.success(t('excel.messages.importFinished', { success: data.success || 0, fail: data.fail || 0 }))
      loadTasks()
      return
    }
  } catch (error) {
    // 单次轮询失败不中断，继续下一次
  }
  if (currentTaskNo.value === taskNo) {
    pollTimer = setTimeout(pollProgress, 1000)
  }
}

const stopPolling = () => {
  if (pollTimer) {
    clearTimeout(pollTimer)
    pollTimer = null
  }
}

const resetImport = () => {
  stopPolling()
  currentTaskNo.value = ''
  selectedFile.value = null
  progress.value = { percent: 0, total: 0, success: 0, fail: 0, status: 0, errorFile: false }
}

const cancelCurrent = async () => {
  await cancelTask(currentTaskNo.value)
  pollProgress()
}

const cancelTask = async (taskNo) => {
  if (!taskNo) return
  try {
    await cancelExcelTask(taskNo)
    ElMessage.success(t('excel.messages.cancelSubmitted'))
    loadTasks()
  } catch (error) {
    // 全局拦截器已提示
  }
}

const downloadErrorFile = async (taskNo) => {
  try {
    const blob = await downloadExcelErrorFile(taskNo)
    saveBlob(blob, `errors_${taskNo}.xlsx`)
  } catch (error) {
    ElMessage.error(t('excel.messages.noErrorFile'))
  }
}

const startExport = async () => {
  exporting.value = true
  try {
    const blob = await exportExcel(exportForm.bizType, { keyword: exportForm.keyword })
    saveBlob(blob, `${bizLabel(exportForm.bizType)}_export_${Date.now()}.xlsx`)
    ElMessage.success(t('excel.messages.exportStarted'))
    loadTasks()
  } catch (error) {
    ElMessage.error(t('excel.messages.downloadFailed'))
  } finally {
    exporting.value = false
  }
}

const loadTasks = async () => {
  loadingTasks.value = true
  try {
    const res = await getExcelTaskList({
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      bizType: filters.bizType || undefined,
      status: filters.status === '' ? undefined : filters.status
    })
    const page = res.data || {}
    tasks.value = page.records || []
    pagination.total = page.total || 0
  } catch (error) {
    ElMessage.error(t('excel.messages.loadFailed'))
  } finally {
    loadingTasks.value = false
  }
}

const handlePageChange = (page) => {
  pagination.pageNum = page
  loadTasks()
}

const handleSizeChange = (size) => {
  pagination.pageSize = size
  pagination.pageNum = 1
  loadTasks()
}

onMounted(async () => {
  try {
    const res = await getExcelBizTypes()
    if (res.data?.length) {
      bizTypes.value = res.data
    }
  } catch (error) {
    // 使用本地默认类型
  }
  loadTasks()
})

onBeforeUnmount(stopPolling)
</script>

<style scoped>
.excel-page {
  display: flex;
  flex-direction: column;
  gap: var(--space-5);
  padding: var(--space-6);
  background: var(--bg-secondary);
  color: var(--text-primary);
  min-height: 100%;
}

.page-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: var(--space-4);
}

.page-title {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: var(--text-primary);
}

.page-subtitle {
  margin: var(--space-2) 0 0;
  font-size: 13px;
  color: var(--text-secondary);
}

.locale-select {
  width: 130px;
}

.grid-2 {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(380px, 1fr));
  gap: var(--space-5);
}

.panel {
  padding: var(--space-5);
  background: var(--bg-primary);
  border: 1px solid var(--border-light);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-sm);
}

.panel-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--space-4);
  flex-wrap: wrap;
}

.panel-title {
  font-size: 15px;
  font-weight: 600;
  color: var(--text-primary);
}

.panel-tip {
  margin: var(--space-2) 0 var(--space-4);
  font-size: 12px;
  color: var(--text-tertiary);
  line-height: 1.6;
}

.form-row {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  flex-wrap: wrap;
  margin-bottom: var(--space-4);
}

.grow {
  flex: 1;
  min-width: 180px;
}

.upload-box {
  margin-bottom: var(--space-4);
}

.upload-icon {
  font-size: 32px;
  color: var(--primary-500);
}

.upload-text {
  margin-top: var(--space-2);
  font-size: 13px;
  color: var(--text-secondary);
}

.upload-file {
  margin-top: var(--space-1);
  font-size: 12px;
  color: var(--text-tertiary);
  word-break: break-all;
}

.actions {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  flex-wrap: wrap;
}

.progress-box {
  margin-top: var(--space-4);
  padding: var(--space-4);
  border: 1px dashed var(--border-medium);
  border-radius: var(--radius-lg);
  background: var(--bg-secondary);
}

.progress-head {
  display: flex;
  justify-content: space-between;
  gap: var(--space-3);
  margin-bottom: var(--space-3);
  font-size: 13px;
  color: var(--text-secondary);
}

.task-no {
  color: var(--text-tertiary);
  font-size: 12px;
  word-break: break-all;
}

.progress-meta {
  margin: var(--space-3) 0;
  font-size: 12px;
  color: var(--text-secondary);
}

.task-filters {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  flex-wrap: wrap;
}

.filter-item {
  width: 140px;
}

.task-table {
  margin-top: var(--space-4);
  width: 100%;
}

.pagination {
  margin-top: var(--space-4);
  justify-content: flex-end;
}

.empty-text {
  padding: var(--space-5) 0;
  color: var(--text-tertiary);
  font-size: 13px;
}

:global([data-theme='dark']) .excel-page {
  --el-bg-color: var(--bg-primary);
  --el-bg-color-overlay: var(--bg-secondary);
  --el-text-color-primary: var(--text-primary);
  --el-text-color-regular: var(--text-secondary);
  --el-border-color: var(--border-light);
  --el-border-color-lighter: var(--border-light);
  --el-fill-color-blank: var(--bg-secondary);
  --el-fill-color-light: var(--bg-tertiary);
}
</style>
