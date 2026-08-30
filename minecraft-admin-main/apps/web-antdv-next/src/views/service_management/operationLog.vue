<script lang="ts" setup>
import type { TableColumnsType } from 'antdv-next';

import { computed, onMounted, ref } from 'vue';

import { Page } from '@vben/common-ui';
import { $t } from '@vben/locales';

import {
  Button,
  Card,
  Descriptions,
  Input,
  message,
  Modal,
  Popconfirm,
  Result,
  Skeleton,
  Table,
  Tag,
} from 'antdv-next';

import {
  deleteOperationLog,
  getOperationLogList,
} from '#/api/management/service/operationLog';

defineOptions({ name: 'OperationLogManagement' });

interface OperationLog {
  browser?: null | string;
  createdAt?: null | string;
  errorMsg?: null | string;
  executionTime?: null | number;
  id: number;
  ipAddress?: null | string;
  module?: null | string;
  operationDesc?: null | string;
  operationType?: null | string;
  os?: null | string;
  requestMethod?: null | string;
  requestParams?: null | string;
  requestUri?: null | string;
  responseResult?: null | string;
  status?: null | number;
  updatedAt?: null | string;
  userAgent?: null | string;
  userId?: null | number;
  username?: null | string;
}

// =========================
// 状态
// =========================
const loading = ref(false);
const errorMsg = ref('');
const logList = ref<OperationLog[]>([]);
const keyword = ref('');
const pageNum = ref(1);
const pageSize = ref(10);
const pages = ref(0);
const total = ref(0);
const deletingId = ref<null | number>(null);

// =========================
// 工具
// =========================
function displayValue(value: any): string {
  if (value === null || value === undefined || value === '') return '-';
  return String(value);
}

function formatTime(value?: null | string): string {
  if (!value) return '-';
  return String(value).replace('T', ' ');
}

/** status: 1 成功 / 0 失败 */
function isOk(status?: null | number): boolean {
  return Number(status) === 1;
}

function toNumber(value: any): number {
  const n = Number(value);
  return Number.isFinite(n) ? n : 0;
}

/** 请求方式 → Tag 颜色 */
function methodColor(method?: null | string): string {
  const m = String(method ?? '').toUpperCase();
  if (m === 'GET') return 'green';
  if (m === 'POST') return 'blue';
  if (m === 'PUT') return 'orange';
  if (m === 'DELETE') return 'red';
  return 'default';
}

/** 尝试美化 JSON 文本，失败原样返回 */
function prettyJson(text?: null | string): string {
  if (text === null || text === undefined || String(text).trim() === '') return '';
  const raw = String(text);
  try {
    return JSON.stringify(JSON.parse(raw), null, 2);
  } catch {
    return raw;
  }
}

// =========================
// 前端过滤（用户 / 描述 / 类型 / 模块 / URI）
// =========================
const filteredLogs = computed(() => {
  const kw = keyword.value.trim().toLowerCase();
  if (!kw) return logList.value;
  return logList.value.filter(
    (l) =>
      (l.username ?? '').toLowerCase().includes(kw) ||
      (l.operationDesc ?? '').toLowerCase().includes(kw) ||
      (l.operationType ?? '').toLowerCase().includes(kw) ||
      (l.module ?? '').toLowerCase().includes(kw) ||
      (l.requestUri ?? '').toLowerCase().includes(kw),
  );
});

// =========================
// 列定义
// =========================
const columns = computed<TableColumnsType>(() => [
  { title: 'ID', dataIndex: 'id', width: 70 },
  { title: $t('service.oplog_page.fields.username'), key: 'username', width: 160 },
  { title: $t('service.oplog_page.fields.operationType'), key: 'operationType', width: 110 },
  { title: $t('service.oplog_page.fields.module'), key: 'module', width: 100 },
  { title: $t('service.oplog_page.fields.operationDesc'), dataIndex: 'operationDesc', ellipsis: true },
  { title: $t('service.oplog_page.fields.requestMethod'), key: 'requestMethod', width: 90 },
  { title: $t('service.oplog_page.fields.requestUri'), key: 'requestUri', width: 200, ellipsis: true },
  { title: $t('service.oplog_page.fields.ipAddress'), key: 'ipAddress', width: 130 },
  { title: $t('service.oplog_page.fields.executionTime'), key: 'executionTime', width: 100 },
  { title: $t('service.oplog_page.fields.status'), key: 'status', width: 80 },
  { title: $t('service.oplog_page.fields.createdAt'), key: 'createdAt', width: 170 },
  { title: $t('service.oplog_page.fields.action'), key: 'action', width: 130, fixed: 'right' },
]);

// =========================
// 数据加载（兼容数组 / 分页对象）
// =========================
async function fetchData() {
  loading.value = true;
  errorMsg.value = '';
  try {
    const res: any = await getOperationLogList({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      page: pageNum.value,
      size: pageSize.value,
    });
    if (Array.isArray(res)) {
      logList.value = res;
      pages.value = 0;
      total.value = res.length;
    } else {
      logList.value = res?.records ?? res?.list ?? [];
      pages.value = toNumber(res?.pages);
      total.value = toNumber(res?.total ?? res?.totalCount);
    }
  } catch (error: any) {
    errorMsg.value = error?.message || $t('service.oplog_page.error_desc');
  } finally {
    loading.value = false;
  }
}

onMounted(() => {
  fetchData();
});

function handlePageChange(page: number, size: number) {
  pageNum.value = page;
  pageSize.value = size;
  fetchData();
}

function handlePageSizeChange(_page: number, size: number) {
  pageSize.value = size;
  pageNum.value = 1;
  fetchData();
}

// =========================
// 详情弹窗
// =========================
const detailVisible = ref(false);
const detailRecord = ref<null | OperationLog>(null);

function openDetail(record: any) {
  detailRecord.value = record;
  detailVisible.value = true;
}

// =========================
// 删除
// =========================
async function handleDelete(record: any) {
  deletingId.value = record.id;
  try {
    await deleteOperationLog(record.id);
    message.success($t('service.oplog_page.delete_success'));
    // 删空当前页自动回退
    if (
      pages.value > 0 &&
      pageNum.value > 1 &&
      logList.value.length <= 1
    ) {
      pageNum.value -= 1;
    }
    await fetchData();
  } catch (err: any) {
    message.error(err?.message || $t('service.oplog_page.delete_failed'));
  } finally {
    deletingId.value = null;
  }
}
</script>

<template>
  <Page :auto-content-height="true">
    <div class="mx-auto w-full">
      <!-- 加载中：骨架屏（仅首次加载） -->
      <Card
        v-if="loading && logList.length === 0"
        :title="$t('service.oplog_page.title')"
        :styles="{ body: { padding: '24px' } }"
      >
        <Skeleton :active="true" :paragraph="{ rows: 8 }" />
      </Card>

      <!-- 加载失败：错误占位 + 重试 -->
      <Card
        v-else-if="errorMsg && logList.length === 0"
        :title="$t('service.oplog_page.title')"
        :styles="{ body: { padding: '24px' } }"
      >
        <Result
          status="error"
          :title="$t('service.oplog_page.error_title')"
          :sub-title="errorMsg"
        >
          <template #extra>
            <Button type="primary" @click="fetchData">
              {{ $t('service.oplog_page.retry') }}
            </Button>
          </template>
        </Result>
      </Card>

      <!-- 列表 -->
      <Card
        v-else
        :title="$t('service.oplog_page.title')"
        :styles="{ body: { padding: '0' } }"
      >
        <template #extra>
          <div class="flex items-center gap-2">
            <Input
              v-model:value="keyword"
              :placeholder="$t('service.oplog_page.search_placeholder')"
              allow-clear
              class="w-64"
            >
              <template #prefix>
                <span class="i-lucide-search size-4 text-muted-foreground"></span>
              </template>
            </Input>
            <Button :loading="loading" @click="fetchData">
              <template #icon>
                <span class="i-lucide-refresh-cw mr-1 size-4 align-middle"></span>
              </template>
              {{ $t('service.oplog_page.refresh') }}
            </Button>
          </div>
        </template>

        <Table
          :columns="columns"
          :data-source="filteredLogs"
          :loading="loading"
          :pagination="
            pages === 0
              ? { pageSize: 10, showSizeChanger: false }
              : {
                  current: pageNum,
                  pageSize: pageSize,
                  showSizeChanger: true,
                  pageSizeOptions: ['10', '20', '50'],
                  total: total,
                  showTotal: (t: number) =>
                    `${t} ${$t('service.oplog_page.total_suffix')}`,
                  onChange: handlePageChange,
                  onShowSizeChange: handlePageSizeChange,
                }
          "
          :scroll="{ x: 1600 }"
          size="small"
          row-key="id"
        >
          <template #bodyCell="{ column, record }">
            <!-- 操作用户 -->
            <template v-if="column.key === 'username'">
              <div class="flex items-center gap-2">
                <div
                  class="flex size-7 shrink-0 items-center justify-center rounded-full bg-primary/10 text-xs font-medium text-primary"
                >
                  {{ (record.username ?? '?').charAt(0) }}
                </div>
                <div class="min-w-0 leading-4">
                  <div class="truncate text-xs font-medium">
                    {{ displayValue(record.username) }}
                  </div>
                  <div class="text-[11px] text-muted-foreground">
                    #{{ displayValue(record.userId) }}
                  </div>
                </div>
              </div>
            </template>

            <!-- 操作类型 -->
            <template v-else-if="column.key === 'operationType'">
              <Tag color="blue">{{ displayValue(record.operationType) }}</Tag>
            </template>

            <!-- 所属模块 -->
            <template v-else-if="column.key === 'module'">
              <Tag color="cyan">{{ displayValue(record.module) }}</Tag>
            </template>

            <!-- 请求方式 -->
            <template v-else-if="column.key === 'requestMethod'">
              <Tag :color="methodColor(record.requestMethod)">
                {{ displayValue(record.requestMethod) }}
              </Tag>
            </template>

            <!-- 请求地址 -->
            <template v-else-if="column.key === 'requestUri'">
              <code class="text-xs">{{ displayValue(record.requestUri) }}</code>
            </template>

            <!-- IP -->
            <template v-else-if="column.key === 'ipAddress'">
              <code class="text-xs">{{ displayValue(record.ipAddress) }}</code>
            </template>

            <!-- 耗时 -->
            <template v-else-if="column.key === 'executionTime'">
              <span
                class="tabular-nums"
                :class="toNumber(record.executionTime) >= 1000 ? 'font-medium text-red-500' : ''"
              >
                {{ toNumber(record.executionTime) }}
                {{ $t('service.oplog_page.ms_unit') }}
              </span>
            </template>

            <!-- 状态 -->
            <template v-else-if="column.key === 'status'">
              <Tag :color="isOk(record.status) ? 'green' : 'red'">
                {{
                  isOk(record.status)
                    ? $t('service.oplog_page.status_success')
                    : $t('service.oplog_page.status_failed')
                }}
              </Tag>
            </template>

            <!-- 时间 -->
            <template v-else-if="column.key === 'createdAt'">
              <span class="text-xs text-muted-foreground">
                {{ formatTime(record.createdAt) }}
              </span>
            </template>

            <!-- 操作 -->
            <template v-else-if="column.key === 'action'">
              <Button type="link" size="small" @click="openDetail(record)">
                {{ $t('service.oplog_page.detail') }}
              </Button>
              <Popconfirm
                :title="$t('service.oplog_page.delete_confirm_title')"
                :description="$t('service.oplog_page.delete_confirm')"
                :ok-text="$t('service.oplog_page.delete')"
                :cancel-text="$t('service.oplog_page.cancel')"
                :ok-button-props="{ danger: true }"
                @confirm="handleDelete(record)"
              >
                <Button
                  type="link"
                  size="small"
                  danger
                  :loading="deletingId === record.id"
                >
                  {{ $t('service.oplog_page.delete') }}
                </Button>
              </Popconfirm>
            </template>
          </template>

          <template #emptyText>
            <span class="text-sm text-muted-foreground">
              {{ $t('service.oplog_page.empty') }}
            </span>
          </template>
        </Table>
      </Card>
    </div>

    <!-- 详情弹窗 -->
    <Modal
      v-model:open="detailVisible"
      :title="$t('service.oplog_page.detail_title')"
      :width="760"
      :footer="null"
      :styles="{
        body: {
          maxHeight: 'calc(100vh - 220px)',
          overflowY: 'auto',
          padding: '20px 24px',
        },
      }"
    >
      <template v-if="detailRecord">
        <div class="mb-4">
          <div class="text-base font-semibold">
            {{ displayValue(detailRecord.operationDesc) }}
          </div>
          <div class="mt-2 flex flex-wrap items-center gap-2">
            <Tag :color="isOk(detailRecord.status) ? 'green' : 'red'">
              {{
                isOk(detailRecord.status)
                  ? $t('service.oplog_page.status_success')
                  : $t('service.oplog_page.status_failed')
              }}
            </Tag>
            <Tag color="blue">
              {{ displayValue(detailRecord.operationType) }}
            </Tag>
            <Tag v-if="detailRecord.module" color="cyan">
              {{ displayValue(detailRecord.module) }}
            </Tag>
            <Tag :color="methodColor(detailRecord.requestMethod)">
              {{ displayValue(detailRecord.requestMethod) }}
            </Tag>
          </div>
          <div
            v-if="!isOk(detailRecord.status) && detailRecord.errorMsg"
            class="mt-3 whitespace-pre-wrap rounded-md border border-red-200 bg-red-50 p-3 text-xs leading-5 text-red-600"
          >
            {{ $t('service.oplog_page.error_msg') }}:
            {{ detailRecord.errorMsg }}
          </div>
        </div>

        <Descriptions :column="2" size="small" bordered class="mb-4">
          <Descriptions.Item :label="$t('service.oplog_page.fields.id')">
            {{ detailRecord.id }}
          </Descriptions.Item>
          <Descriptions.Item :label="$t('service.oplog_page.fields.userId')">
            {{ displayValue(detailRecord.userId) }}
          </Descriptions.Item>
          <Descriptions.Item :label="$t('service.oplog_page.fields.username')">
            {{ displayValue(detailRecord.username) }}
          </Descriptions.Item>
          <Descriptions.Item
            :label="$t('service.oplog_page.fields.executionTime')"
          >
            <span class="tabular-nums">
              {{ toNumber(detailRecord.executionTime) }}
              {{ $t('service.oplog_page.ms_unit') }}
            </span>
          </Descriptions.Item>
          <Descriptions.Item :label="$t('service.oplog_page.fields.ipAddress')">
            <code class="text-xs">{{ displayValue(detailRecord.ipAddress) }}</code>
          </Descriptions.Item>
          <Descriptions.Item :label="$t('service.oplog_page.fields.os')">
            {{ displayValue(detailRecord.os) }}
          </Descriptions.Item>
          <Descriptions.Item :label="$t('service.oplog_page.fields.browser')">
            {{ displayValue(detailRecord.browser) }}
          </Descriptions.Item>
          <Descriptions.Item :label="$t('service.oplog_page.fields.createdAt')">
            {{ formatTime(detailRecord.createdAt) }}
          </Descriptions.Item>
        </Descriptions>

        <!-- 请求地址 -->
        <div class="mb-3">
          <div class="mb-1 text-xs font-medium text-muted-foreground">
            {{ $t('service.oplog_page.fields.requestMethod') }} /
            {{ $t('service.oplog_page.fields.requestUri') }}
          </div>
          <div class="rounded-md border border-border bg-accent/40 p-3">
            <code class="text-xs leading-5">
              {{ displayValue(detailRecord.requestMethod) }}
              {{ displayValue(detailRecord.requestUri) }}
            </code>
          </div>
        </div>

        <!-- 请求参数 -->
        <div class="mb-3">
          <div class="mb-1 text-xs font-medium text-muted-foreground">
            {{ $t('service.oplog_page.fields.requestParams') }}
          </div>
          <pre
            v-if="prettyJson(detailRecord.requestParams)"
            class="max-h-48 overflow-auto whitespace-pre-wrap break-all rounded-md border border-border bg-accent/40 p-3 text-xs leading-5"
          >{{ prettyJson(detailRecord.requestParams) }}</pre>
          <div v-else class="text-xs text-muted-foreground">
            {{ $t('service.oplog_page.params_empty') }}
          </div>
        </div>

        <!-- 响应结果 -->
        <div class="mb-3">
          <div class="mb-1 text-xs font-medium text-muted-foreground">
            {{ $t('service.oplog_page.fields.responseResult') }}
          </div>
          <pre
            v-if="prettyJson(detailRecord.responseResult)"
            class="max-h-48 overflow-auto whitespace-pre-wrap break-all rounded-md border border-border bg-accent/40 p-3 text-xs leading-5"
          >{{ prettyJson(detailRecord.responseResult) }}</pre>
          <div v-else class="text-xs text-muted-foreground">
            {{ $t('service.oplog_page.result_empty') }}
          </div>
        </div>

        <!-- User-Agent -->
        <div v-if="detailRecord.userAgent">
          <div class="mb-1 text-xs font-medium text-muted-foreground">
            {{ $t('service.oplog_page.fields.userAgent') }}
          </div>
          <div
            class="whitespace-pre-wrap break-all rounded-md border border-border bg-accent/40 p-3 text-xs leading-5"
          >
            {{ detailRecord.userAgent }}
          </div>
        </div>
      </template>
    </Modal>
  </Page>
</template>
