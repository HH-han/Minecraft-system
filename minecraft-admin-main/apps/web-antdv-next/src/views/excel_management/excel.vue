<script lang="ts" setup>
import type { TableColumnsType } from 'antdv-next';

import { computed, onBeforeUnmount, onMounted, reactive, ref } from 'vue';

import { Page } from '@vben/common-ui';
import { Download, Inbox, RefreshCw, X } from '@vben/icons';
import { $t } from '@vben/locales';

import {
  Alert,
  Button,
  Card,
  Empty,
  Input,
  message,
  Popconfirm,
  Progress,
  Segmented,
  Select,
  Space,
  Table,
  Tag,
  UploadDragger,
} from 'antdv-next';
import dayjs from 'dayjs';

import type { ExcelBizTypeItem, ExcelTaskItem } from '#/api/management/excel';
import {
  cancelExcelTask,
  deleteExcelTask,
  downloadExcelErrorFile,
  downloadExcelTemplate,
  exportExcel,
  getExcelBizTypes,
  getExcelTaskList,
  getImportProgress,
  importExcel,
  saveBlob,
} from '#/api/management/excel';

defineOptions({ name: 'ExcelTools' });

// =========================
// 业务类型字典
// =========================
const FALLBACK_BIZ_TYPES: ExcelBizTypeItem[] = [
  { code: 'attraction', desc: '景点' },
  { code: 'hotel', desc: '酒店' },
  { code: 'food', desc: '美食' },
  { code: 'product', desc: '商品' },
  { code: 'ticket', desc: '票务' },
  { code: 'city', desc: '城市' },
  { code: 'points_product', desc: '积分商品' },
  { code: 'announcement', desc: '公告' },
  { code: 'carousel', desc: '轮播图' },
  { code: 'railway_station', desc: '火车站点' },
  { code: 'tags', desc: '标签' },
  { code: 'safety_tips', desc: '安全提示' },
  { code: 'countries', desc: '国家' },
  { code: 'continents', desc: '大洲' },
  { code: 'news', desc: '资讯' },
  { code: 'user', desc: '用户' },
];

const bizTypes = ref<ExcelBizTypeItem[]>([]);

const bizTypeOptions = computed(() =>
  bizTypes.value.map((item) => ({
    label: bizLabel(item.code),
    value: item.code,
  })),
);

const strategyOptions = computed(() => [
  { label: $t('excel.strategyOptions.skip'), value: 'skip' },
  { label: $t('excel.strategyOptions.overwrite'), value: 'overwrite' },
  { label: $t('excel.strategyOptions.error'), value: 'error' },
]);

function bizLabel(code?: string): string {
  if (!code) return '-';
  const key = `excel.biz.${code}`;
  const label = $t(key);
  if (label !== key) return label;
  return bizTypes.value.find((item) => item.code === code)?.desc || code;
}

async function loadBizTypes() {
  try {
    const list = await getExcelBizTypes();
    bizTypes.value = Array.isArray(list) && list.length ? list : FALLBACK_BIZ_TYPES;
  } catch {
    bizTypes.value = FALLBACK_BIZ_TYPES;
  }
  const first = bizTypes.value[0]?.code;
  if (first) {
    if (!importForm.bizType) importForm.bizType = first;
    if (!exportForm.bizType) exportForm.bizType = first;
  }
}

// =========================
// 导入
// =========================
const importForm = reactive({
  bizType: '',
  strategy: 'skip',
});

const selectedFile = ref<File | null>(null);
const importing = ref(false);
const templateLoading = ref(false);

interface ProgressState {
  errorFile: boolean;
  errorMsg: string;
  fail: number;
  percent: number;
  status: number;
  success: number;
  total: number;
}

const currentTaskNo = ref('');
const progress = reactive<ProgressState>(emptyProgress());

function emptyProgress(): ProgressState {
  return {
    percent: 0,
    total: 0,
    success: 0,
    fail: 0,
    status: 0,
    errorFile: false,
    errorMsg: '',
  };
}

function applyProgress(data: any) {
  progress.percent = Number(data?.percent ?? 0);
  progress.total = Number(data?.total ?? 0);
  progress.success = Number(data?.success ?? 0);
  progress.fail = Number(data?.fail ?? 0);
  progress.status = Number(data?.status ?? 0);
  progress.errorFile = Boolean(data?.errorFile);
  progress.errorMsg = String(data?.errorMsg ?? '');
}

let pollTimer: null | ReturnType<typeof setTimeout> = null;
let pollFailCount = 0;

function stopPolling() {
  if (pollTimer) {
    clearTimeout(pollTimer);
    pollTimer = null;
  }
}

async function pollProgress() {
  const taskNo = currentTaskNo.value;
  if (!taskNo) return;
  try {
    const data = await getImportProgress(taskNo);
    if (currentTaskNo.value !== taskNo) return;
    pollFailCount = 0;
    applyProgress(data);
    if (!isActive(progress.status)) {
      stopPolling();
      notifyFinished(progress.status);
      await loadTasks();
      return;
    }
    pollTimer = setTimeout(pollProgress, 1000);
  } catch {
    if (currentTaskNo.value !== taskNo) return;
    pollFailCount += 1;
    if (pollFailCount >= 3) {
      stopPolling();
      message.error($t('excel.messages.pollingFailed'));
      return;
    }
    pollTimer = setTimeout(pollProgress, 1500);
  }
}

function notifyFinished(status: number) {
  const summary = {
    fail: progress.fail,
    success: progress.success,
  };
  if (status === 2) {
    message.success($t('excel.messages.importFinished', summary));
  } else if (status === 3) {
    message.warning($t('excel.messages.importFinished', summary));
  } else if (status === 4) {
    message.error($t('excel.messages.importFailed'));
  } else if (status === 5) {
    message.info($t('excel.messages.importCancelled'));
  }
}

function handleFileChange(info: any) {
  const raw = info?.file?.originFileObj ?? info?.file;
  if (!raw) return;
  const fileName = String(raw.name ?? '');
  if (!/\.(xlsx?|XLSX?)$/.test(fileName)) {
    message.error($t('excel.messages.invalidFileType'));
    selectedFile.value = null;
    return;
  }
  if (Number(raw.size ?? 0) > 50 * 1024 * 1024) {
    message.error($t('excel.messages.fileTooLarge'));
    selectedFile.value = null;
    return;
  }
  selectedFile.value = raw as File;
}

function clearSelectedFile() {
  selectedFile.value = null;
}

function resetImport() {
  stopPolling();
  currentTaskNo.value = '';
  selectedFile.value = null;
  pollFailCount = 0;
  Object.assign(progress, emptyProgress());
}

async function handleDownloadTemplate() {
  if (!importForm.bizType) {
    message.warning($t('excel.messages.selectBizType'));
    return;
  }
  templateLoading.value = true;
  try {
    const blob = await downloadExcelTemplate(importForm.bizType);
    await saveBlob(
      blob,
      `${bizLabel(importForm.bizType)}_${$t('excel.import.template')}.xlsx`,
    );
    message.success($t('excel.messages.templateDownloaded'));
  } catch (error: any) {
    message.error(error?.message || $t('excel.messages.downloadFailed'));
  } finally {
    templateLoading.value = false;
  }
}

async function startImport() {
  if (!importForm.bizType) {
    message.warning($t('excel.messages.selectBizType'));
    return;
  }
  if (!selectedFile.value) {
    message.warning($t('excel.messages.selectFile'));
    return;
  }
  importing.value = true;
  try {
    const result = await importExcel(importForm.bizType, selectedFile.value, {
      duplicateStrategy: importForm.strategy,
    });
    if (!result?.taskNo) {
      message.error($t('excel.messages.importFailed'));
      return;
    }
    currentTaskNo.value = result.taskNo;
    Object.assign(progress, emptyProgress());
    message.success($t('excel.messages.importSubmitted'));
    await loadTasks(true);
    pollFailCount = 0;
    stopPolling();
    pollProgress();
  } catch {
    // 请求层已统一提示错误
  } finally {
    importing.value = false;
  }
}

async function handleCancelCurrent() {
  if (!currentTaskNo.value) return;
  try {
    await cancelExcelTask(currentTaskNo.value);
    message.success($t('excel.messages.cancelSubmitted'));
    await loadTasks();
    pollProgress();
  } catch {
    // 请求层已统一提示错误
  }
}

// =========================
// 导出
// =========================
const exportForm = reactive({
  bizType: '',
  keyword: '',
});

const exporting = ref(false);

async function handleExport() {
  if (!exportForm.bizType) {
    message.warning($t('excel.messages.selectBizType'));
    return;
  }
  exporting.value = true;
  try {
    const keyword = exportForm.keyword.trim();
    const blob = await exportExcel(
      exportForm.bizType,
      keyword ? { keyword } : {},
    );
    await saveBlob(
      blob,
      `${bizLabel(exportForm.bizType)}_${dayjs().format('YYYYMMDDHHmmss')}.xlsx`,
    );
    message.success($t('excel.messages.exportSuccess'));
    await loadTasks(true);
  } catch (error: any) {
    message.error(error?.message || $t('excel.messages.downloadFailed'));
  } finally {
    exporting.value = false;
  }
}

async function handleDownloadErrorFile(taskNo: string) {
  if (!taskNo) return;
  try {
    const blob = await downloadExcelErrorFile(taskNo);
    await saveBlob(blob, `errors_${taskNo}.xlsx`);
  } catch (error: any) {
    message.error(error?.message || $t('excel.messages.noErrorFile'));
  }
}

// =========================
// 任务列表
// =========================
const tasks = ref<ExcelTaskItem[]>([]);
const tasksLoading = ref(false);
const filters = reactive<{ bizType?: string; status?: number }>({
  bizType: undefined,
  status: undefined,
});
const pageNum = ref(1);
const pageSize = ref(10);
const total = ref(0);

const STATUS_MAP: Record<number, { color: string; key: string }> = {
  0: { key: 'pending', color: 'default' },
  1: { key: 'processing', color: 'processing' },
  2: { key: 'success', color: 'success' },
  3: { key: 'partialFail', color: 'warning' },
  4: { key: 'failed', color: 'error' },
  5: { key: 'cancelled', color: 'default' },
};

const statusFilterOptions = computed(() =>
  Object.entries(STATUS_MAP).map(([value, item]) => ({
    label: $t(`excel.status.${item.key}`),
    value: Number(value),
  })),
);

function statusLabel(status?: number): string {
  const key = STATUS_MAP[status ?? 0]?.key ?? 'pending';
  return $t(`excel.status.${key}`);
}

function statusColor(status?: number): string {
  return STATUS_MAP[status ?? 0]?.color ?? 'default';
}

function isActive(status?: number): boolean {
  return status === 0 || status === 1;
}

const progressStatus = computed(() => {
  if (progress.status === 2) return 'success';
  if (progress.status === 3 || progress.status === 4) return 'exception';
  return 'active';
});

function formatDateTime(value?: string): string {
  if (!value) return '-';
  const parsed = dayjs(value);
  return parsed.isValid() ? parsed.format('YYYY-MM-DD HH:mm:ss') : String(value);
}

function formatFileSize(bytes?: number): string {
  const size = Number(bytes ?? 0);
  if (!size) return '';
  if (size < 1024) return `${size} B`;
  if (size < 1024 * 1024) return `${(size / 1024).toFixed(1)} KB`;
  return `${(size / 1024 / 1024).toFixed(1)} MB`;
}

const columns = computed<TableColumnsType>(() => [
  {
    title: $t('excel.task.taskNo'),
    dataIndex: 'taskNo',
    width: 200,
    fixed: 'left',
    ellipsis: true,
  },
  {
    title: $t('excel.task.business'),
    dataIndex: 'bizType',
    width: 120,
  },
  {
    title: $t('excel.task.type'),
    dataIndex: 'taskType',
    width: 100,
  },
  {
    title: $t('excel.task.fileName'),
    dataIndex: 'fileName',
    minWidth: 200,
    ellipsis: true,
  },
  {
    title: $t('excel.task.rows'),
    key: 'rows',
    width: 130,
  },
  {
    title: $t('excel.task.status'),
    dataIndex: 'status',
    width: 110,
  },
  {
    title: $t('excel.task.time'),
    dataIndex: 'createTime',
    width: 180,
  },
  {
    title: $t('excel.task.actions'),
    key: 'actions',
    width: 190,
    fixed: 'right',
  },
]);

const pagination = computed(() => ({
  current: pageNum.value,
  pageSize: pageSize.value,
  total: total.value,
  showSizeChanger: true,
  pageSizeOptions: ['10', '20', '50'],
  showTotal: (value: number) => $t('excel.messages.total', { total: value }),
}));

async function loadTasks(reset = false) {
  if (reset) pageNum.value = 1;
  tasksLoading.value = true;
  try {
    const result = await getExcelTaskList({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      bizType: filters.bizType,
      status: filters.status,
    });
    tasks.value = result?.records ?? [];
    total.value = Number(result?.total ?? 0);
  } catch {
    message.error($t('excel.messages.loadFailed'));
  } finally {
    tasksLoading.value = false;
  }
}

function handleTableChange(pag: any) {
  pageNum.value = pag.current;
  pageSize.value = pag.pageSize;
  loadTasks();
}

function handleFilter() {
  loadTasks(true);
}

async function handleCancelTask(taskNo: string) {
  if (!taskNo) return;
  try {
    await cancelExcelTask(taskNo);
    message.success($t('excel.messages.cancelSubmitted'));
    await loadTasks();
  } catch {
    // 请求层已统一提示错误
  }
}

async function handleDeleteTask(taskNo: string) {
  if (!taskNo) return;
  try {
    await deleteExcelTask(taskNo);
    message.success($t('excel.messages.deleteSuccess'));
    if (currentTaskNo.value === taskNo) {
      resetImport();
    }
    await loadTasks();
  } catch {
    // 请求层已统一提示错误
  }
}

onMounted(async () => {
  await loadBizTypes();
  await loadTasks();
});

onBeforeUnmount(() => {
  stopPolling();
});
</script>

<template>
  <Page
    :auto-content-height="true"
    :description="$t('excel.subtitle')"
    :title="$t('excel.title')"
  >
    <div class="mx-auto flex w-full flex-col gap-4">
      <div class="grid grid-cols-1 gap-4 xl:grid-cols-3">
        <!-- 批量导入 -->
        <Card class="xl:col-span-2" :styles="{ body: { padding: '20px 24px' } }">
          <template #title>
            <span class="inline-flex items-center gap-2">
              <span class="excel-title-icon">
                <Inbox class="size-4" />
              </span>
              {{ $t('excel.import.title') }}
            </span>
          </template>
          <template #extra>
            <Button
              :loading="templateLoading"
              type="link"
              @click="handleDownloadTemplate"
            >
              <template #icon>
                <Download class="mr-1 size-4 align-middle" />
              </template>
              {{ $t('excel.import.template') }}
            </Button>
          </template>

          <p class="excel-tip">{{ $t('excel.import.tip') }}</p>

          <div class="mb-4 grid grid-cols-1 gap-4 md:grid-cols-2">
            <div>
              <div class="excel-field-label">{{ $t('excel.bizType') }}</div>
              <Select
                v-model:value="importForm.bizType"
                :options="bizTypeOptions"
                :placeholder="$t('excel.bizTypePlaceholder')"
                class="w-full"
              />
            </div>
            <div>
              <div class="excel-field-label">{{ $t('excel.strategy') }}</div>
              <Segmented
                v-model:value="importForm.strategy"
                :options="strategyOptions"
                block
              />
            </div>
          </div>

          <UploadDragger
            accept=".xls,.xlsx"
            :before-upload="() => false"
            :max-count="1"
            :multiple="false"
            :show-upload-list="false"
            @change="handleFileChange"
          >
            <div class="excel-dragger-body">
              <Inbox class="excel-dragger-icon" />
              <div class="text-sm font-medium">
                {{ $t('excel.import.dragger') }}
              </div>
              <div class="text-xs text-muted-foreground">
                {{ $t('excel.import.draggerHint') }}
              </div>
            </div>
          </UploadDragger>

          <div v-if="selectedFile" class="excel-file-chip">
            <span class="min-w-0 flex-1 truncate text-sm">
              {{ selectedFile.name }}
            </span>
            <span class="text-xs text-muted-foreground">
              {{ formatFileSize(selectedFile.size) }}
            </span>
            <Button
              danger
              size="small"
              type="text"
              @click="clearSelectedFile"
            >
              <template #icon>
                <X class="size-3.5" />
              </template>
            </Button>
          </div>

          <div class="mt-4 flex items-center gap-3">
            <Button
              :disabled="!selectedFile || !importForm.bizType"
              :loading="importing"
              type="primary"
              @click="startImport"
            >
              {{ $t('excel.import.start') }}
            </Button>
            <Button v-if="currentTaskNo" @click="resetImport">
              {{ $t('excel.import.reimport') }}
            </Button>
          </div>

          <div v-if="currentTaskNo" class="excel-progress">
            <div class="mb-2 flex flex-wrap items-center justify-between gap-2">
              <span class="text-sm font-medium">
                {{ $t('excel.import.progress') }}
              </span>
              <span class="text-xs text-muted-foreground">
                {{ $t('excel.import.taskNo') }}: {{ currentTaskNo }}
              </span>
            </div>
            <Progress
              :percent="progress.percent"
              :show-info="false"
              :status="progressStatus"
              :stroke-width="10"
            />
            <div class="mt-2 flex flex-wrap items-center justify-between gap-2">
              <span class="text-xs text-muted-foreground">
                {{ statusLabel(progress.status) }} ·
                {{ $t('excel.task.rows') }}:
                <span class="text-green-500">{{ progress.success }}</span>
                /
                <span class="text-red-500">{{ progress.fail }}</span>
                · {{ progress.percent }}%
              </span>
              <Space :size="6">
                <Button
                  v-if="progress.errorFile"
                  size="small"
                  @click="handleDownloadErrorFile(currentTaskNo)"
                >
                  {{ $t('excel.task.errorFile') }}
                </Button>
                <Button
                  v-if="isActive(progress.status)"
                  danger
                  size="small"
                  @click="handleCancelCurrent"
                >
                  {{ $t('excel.task.cancel') }}
                </Button>
              </Space>
            </div>
            <Alert
              v-if="progress.errorMsg"
              class="mt-3"
              :message="progress.errorMsg"
              show-icon
              type="error"
            />
          </div>
        </Card>

        <!-- 数据导出 -->
        <Card :styles="{ body: { padding: '20px 24px' } }">
          <template #title>
            <span class="inline-flex items-center gap-2">
              <span class="excel-title-icon">
                <Download class="size-4" />
              </span>
              {{ $t('excel.export.title') }}
            </span>
          </template>

          <p class="excel-tip">{{ $t('excel.export.tip') }}</p>

          <div class="excel-field-label">{{ $t('excel.bizType') }}</div>
          <Select
            v-model:value="exportForm.bizType"
            :options="bizTypeOptions"
            :placeholder="$t('excel.bizTypePlaceholder')"
            class="w-full"
          />

          <div class="excel-field-label mt-4">
            {{ $t('excel.export.keyword') }}
          </div>
          <Input
            v-model:value="exportForm.keyword"
            allow-clear
            :placeholder="$t('excel.export.keywordPlaceholder')"
            @press-enter="handleExport"
          />

          <Button
            class="mt-6 w-full"
            :loading="exporting"
            type="primary"
            @click="handleExport"
          >
            {{ $t('excel.export.button') }}
          </Button>
        </Card>
      </div>

      <!-- 任务记录 -->
      <Card :styles="{ body: { padding: '20px 24px' } }">
        <template #title>{{ $t('excel.task.title') }}</template>
        <template #extra>
          <Space :size="8" wrap>
            <Select
              v-model:value="filters.bizType"
              allow-clear
              class="w-40"
              :options="bizTypeOptions"
              :placeholder="$t('excel.task.filterBizType')"
              @change="handleFilter"
            />
            <Select
              v-model:value="filters.status"
              allow-clear
              class="w-36"
              :options="statusFilterOptions"
              :placeholder="$t('excel.task.filterStatus')"
              @change="handleFilter"
            />
            <Button :loading="tasksLoading" @click="loadTasks()">
              <template #icon>
                <RefreshCw class="mr-1 size-4 align-middle" />
              </template>
              {{ $t('excel.task.refresh') }}
            </Button>
          </Space>
        </template>

        <Table
          :bordered="true"
          :columns="columns"
          :data-source="tasks"
          :loading="tasksLoading"
          :pagination="pagination"
          :scroll="{ x: 1200 }"
          row-key="taskNo"
          size="middle"
          @change="handleTableChange"
        >
          <template #bodyCell="{ column, record }">
            <template v-if="column.dataIndex === 'taskNo'">
              <span class="font-mono text-xs">{{ record.taskNo }}</span>
            </template>

            <template v-else-if="column.dataIndex === 'bizType'">
              <Tag color="blue">{{ bizLabel(record.bizType) }}</Tag>
            </template>

            <template v-else-if="column.dataIndex === 'taskType'">
              <Tag :color="record.taskType === 2 ? 'green' : 'geekblue'">
                {{
                  record.taskType === 2
                    ? $t('excel.task.export')
                    : $t('excel.task.import')
                }}
              </Tag>
            </template>

            <template v-else-if="column.dataIndex === 'fileName'">
              {{ record.fileName || '-' }}
            </template>

            <template v-else-if="column.key === 'rows'">
              <span class="text-green-500">{{ record.successRows ?? 0 }}</span>
              <span class="mx-1 text-muted-foreground">/</span>
              <span class="text-red-500">{{ record.failRows ?? 0 }}</span>
            </template>

            <template v-else-if="column.dataIndex === 'status'">
              <Tag :color="statusColor(record.status)">
                {{ statusLabel(record.status) }}
              </Tag>
            </template>

            <template v-else-if="column.dataIndex === 'createTime'">
              {{ formatDateTime(record.createTime) }}
            </template>

            <template v-else-if="column.key === 'actions'">
              <Space :size="4" wrap>
                <Button
                  v-if="record.taskType === 1 && Number(record.failRows) > 0"
                  size="small"
                  type="link"
                  @click="handleDownloadErrorFile(record.taskNo)"
                >
                  {{ $t('excel.task.errorFile') }}
                </Button>
                <Popconfirm
                  v-if="isActive(record.status)"
                  :cancel-text="$t('excel.task.continueText')"
                  :description="$t('excel.task.confirmCancelDesc')"
                  :ok-text="$t('excel.task.cancelText')"
                  :title="$t('excel.task.confirmCancel')"
                  @confirm="handleCancelTask(record.taskNo)"
                >
                  <Button danger size="small" type="link">
                    {{ $t('excel.task.cancel') }}
                  </Button>
                </Popconfirm>
                <Popconfirm
                  v-if="!isActive(record.status)"
                  :cancel-text="$t('excel.task.continueText')"
                  :description="$t('excel.task.confirmDeleteDesc')"
                  :ok-text="$t('excel.task.deleteText')"
                  :title="$t('excel.task.confirmDelete')"
                  @confirm="handleDeleteTask(record.taskNo)"
                >
                  <Button danger size="small" type="link">
                    {{ $t('excel.task.delete') }}
                  </Button>
                </Popconfirm>
              </Space>
            </template>
          </template>

          <template #emptyText>
            <Empty :description="$t('excel.task.empty')" />
          </template>
        </Table>
      </Card>
    </div>
  </Page>
</template>

<style scoped>
.excel-title-icon {
  display: inline-flex;
  width: 26px;
  height: 26px;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
  color: hsl(var(--primary));
  background-color: var(--app-fill-color, rgb(0 0 0 / 5%));
}

.excel-tip {
  margin: 0 0 16px;
  font-size: 13px;
  color: var(--muted-foreground-color, rgb(0 0 0 / 45%));
}

.excel-field-label {
  margin-bottom: 8px;
  font-size: 13px;
  font-weight: 500;
}

.excel-dragger-body {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  padding: 16px 0;
}

.excel-dragger-icon {
  width: 34px;
  height: 34px;
  margin-bottom: 4px;
  color: var(--muted-foreground-color, rgb(0 0 0 / 45%));
}

.excel-file-chip {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 12px;
  padding: 8px 12px;
  border-radius: 8px;
  background-color: var(--app-fill-color, rgb(0 0 0 / 4%));
}

.excel-progress {
  margin-top: 16px;
  padding: 14px 16px;
  border-radius: 10px;
  background-color: var(--app-fill-color, rgb(0 0 0 / 3%));
}
</style>
