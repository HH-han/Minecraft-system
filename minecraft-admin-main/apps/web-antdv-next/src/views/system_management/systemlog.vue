<script lang="ts" setup>
import type { TableColumnsType } from 'antdv-next';

import { computed, onMounted, ref } from 'vue';

import { Page } from '@vben/common-ui';
import { Eraser, Search } from '@vben/icons';
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
  Select,
  Skeleton,
  Table,
  Tag,
} from 'antdv-next';

import {
  deleteLogsBeforeTime,
  deleteSystemLog,
  getSystemLogList,
} from '#/api/management/system/systemlog';

defineOptions({ name: 'SystemLogManagement' });

interface SystemLog {
  action?: null | string;
  content?: null | string;
  createdAt?: null | string;
  errorStack?: null | string;
  executionTime?: null | number;
  id: number;
  logLevel?: null | string;
  logType?: null | string;
  module?: null | string;
  requestId?: null | string;
  requestMethod?: null | string;
  requestParams?: null | string;
  requestUrl?: null | string;
  responseData?: null | string;
  serverHost?: null | string;
  serverPort?: null | number;
  spanId?: null | string;
  status?: null | string;
  traceId?: null | string;
  userAgent?: null | string;
  userIp?: null | string;
  userId?: null | number;
  username?: null | string;
}

// =========================
// 状态
// =========================
const loading = ref(false);
const errorMsg = ref('');
const logList = ref<SystemLog[]>([]);
const pageNum = ref(1);
const pageSize = ref(10);
const pages = ref(0);
const total = ref(0);
const deletingId = ref<null | number>(null);

// 过滤条件（传后端 QueryDTO，不识别则后端忽略）
const levelFilter = ref<null | string>(null);
const typeFilter = ref<null | string>(null);
const keyword = ref('');

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

function toNumber(value: any): number {
  const n = Number(value);
  return Number.isFinite(n) ? n : 0;
}

/** 日志级别 → Tag 颜色 */
function levelColor(level?: null | string): string {
  const l = String(level ?? '').toUpperCase();
  if (l === 'ERROR') return 'red';
  if (l === 'WARN' || l === 'WARNING') return 'orange';
  if (l === 'INFO') return 'blue';
  return 'default';
}

/** status（字符串）→ { color, text } */
const SUCCESS_STATUSES = ['SUCCESS', 'OK', '1', '200', 'DONE'];
const FAIL_STATUSES = ['ERROR', 'FAIL', 'FAILED', '0', '500'];

function statusInfo(status?: null | string): { color: string; text: string } {
  const s = String(status ?? '').trim();
  if (!s) return { color: 'default', text: '-' };
  const upper = s.toUpperCase();
  if (SUCCESS_STATUSES.includes(upper)) return { color: 'green', text: s };
  if (FAIL_STATUSES.includes(upper)) return { color: 'red', text: s };
  return { color: 'default', text: s };
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

/** 服务器地址 host:port */
function serverAddress(record: any): string {
  const host = displayValue(record.serverHost);
  if (host === '-') return '-';
  const port = toNumber(record.serverPort);
  return port > 0 ? `${host}:${port}` : host;
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

/** 类型动态提取（从当前数据去重） */
const typeOptions = computed(() => {
  const set = new Set<string>();
  logList.value.forEach((l) => {
    if (l.logType) set.add(String(l.logType));
  });
  return [...set].map((t) => ({ label: t, value: t }));
});

// =========================
// 列定义
// =========================
const columns = computed<TableColumnsType>(() => [
  { title: 'ID', dataIndex: 'id', width: 70 },
  { title: $t('system.syslog_page.fields.createdAt'), key: 'createdAt', width: 170 },
  { title: $t('system.syslog_page.fields.logLevel'), key: 'logLevel', width: 100 },
  { title: $t('system.syslog_page.fields.logType'), key: 'logType', width: 100 },
  { title: $t('system.syslog_page.fields.module'), key: 'module', width: 110 },
  { title: $t('system.syslog_page.fields.username'), key: 'username', width: 140 },
  { title: $t('system.syslog_page.fields.content'), dataIndex: 'content', ellipsis: true },
  { title: $t('system.syslog_page.fields.requestMethod'), key: 'requestMethod', width: 90 },
  { title: $t('system.syslog_page.fields.requestUrl'), key: 'requestUrl', width: 200, ellipsis: true },
  { title: $t('system.syslog_page.fields.executionTime'), key: 'executionTime', width: 100 },
  { title: $t('system.syslog_page.fields.serverHost'), key: 'serverHost', width: 150 },
  { title: $t('system.syslog_page.fields.action_col'), key: 'action', width: 130, fixed: 'right' },
]);

// =========================
// 数据加载（兼容数组 / 分页对象）
// =========================
async function fetchData() {
  loading.value = true;
  errorMsg.value = '';
  try {
    const res: any = await getSystemLogList({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      page: pageNum.value,
      size: pageSize.value,
      keyword: keyword.value.trim() || undefined,
      logLevel: levelFilter.value || undefined,
      logType: typeFilter.value || undefined,
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
    errorMsg.value = error?.message || $t('system.syslog_page.error_desc');
  } finally {
    loading.value = false;
  }
}

onMounted(() => {
  fetchData();
});

function handleFilterChange() {
  pageNum.value = 1;
  fetchData();
}

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
const detailRecord = ref<null | SystemLog>(null);

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
    await deleteSystemLog(record.id);
    message.success($t('system.syslog_page.delete_success'));
    if (pages.value > 0 && pageNum.value > 1 && logList.value.length <= 1) {
      pageNum.value -= 1;
    }
    await fetchData();
  } catch (err: any) {
    message.error(err?.message || $t('system.syslog_page.delete_failed'));
  } finally {
    deletingId.value = null;
  }
}

// =========================
// 清理历史日志
// =========================
const cleanupTime = ref('');
const cleaning = ref(false);
const TIME_PATTERN = /^\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}$/;

async function handleCleanup() {
  const time = cleanupTime.value.trim();
  if (!time) {
    message.warning($t('system.syslog_page.cleanup_time_required'));
    return;
  }
  if (!TIME_PATTERN.test(time)) {
    message.warning($t('system.syslog_page.cleanup_time_invalid'));
    return;
  }
  cleaning.value = true;
  try {
    await deleteLogsBeforeTime(time);
    message.success($t('system.syslog_page.cleanup_success'));
    pageNum.value = 1;
    await fetchData();
  } catch (err: any) {
    message.error(err?.message || $t('system.syslog_page.cleanup_failed'));
  } finally {
    cleaning.value = false;
  }
}
</script>

<template>
  <Page :auto-content-height="true">
    <div class="mx-auto w-full">
      <!-- 加载中：骨架屏（仅首次加载） -->
      <Card
        v-if="loading && logList.length === 0"
        :title="$t('system.syslog_page.title')"
        :styles="{ body: { padding: '24px' } }"
      >
        <Skeleton :active="true" :paragraph="{ rows: 8 }" />
      </Card>

      <!-- 加载失败：错误占位 + 重试 -->
      <Card
        v-else-if="errorMsg && logList.length === 0"
        :title="$t('system.syslog_page.title')"
        :styles="{ body: { padding: '24px' } }"
      >
        <Result
          status="error"
          :title="$t('system.syslog_page.error_title')"
          :sub-title="errorMsg"
        >
          <template #extra>
            <Button type="primary" @click="fetchData">
              {{ $t('system.syslog_page.retry') }}
            </Button>
          </template>
        </Result>
      </Card>

      <!-- 列表 -->
      <Card
        v-else
        :title="$t('system.syslog_page.title')"
        :styles="{ body: { padding: '0' } }"
      >
        <template #extra>
          <Button @click="handleFilterChange">
            {{ $t('system.syslog_page.refresh') }}
          </Button>
        </template>

        <!-- 筛选工具栏：左侧筛选/搜索，右侧清理 -->
        <div
          class="flex flex-wrap items-center justify-between gap-y-2 border-b border-border px-4 py-3"
        >
          <div class="flex flex-wrap items-center gap-2">
            <Select
              v-model:value="levelFilter"
              :placeholder="$t('system.syslog_page.level_all')"
              allow-clear
              class="w-32"
              :options="[
                { label: 'ERROR', value: 'ERROR' },
                { label: 'WARN', value: 'WARN' },
                { label: 'INFO', value: 'INFO' },
                { label: 'DEBUG', value: 'DEBUG' },
              ]"
              @change="handleFilterChange"
            ></Select>
            <Select
              v-model:value="typeFilter"
              :placeholder="$t('system.syslog_page.type_all')"
              allow-clear
              class="w-32"
              :options="typeOptions"
              @change="handleFilterChange"
            ></Select>
            <Input
              v-model:value="keyword"
              :placeholder="$t('system.syslog_page.search_placeholder')"
              allow-clear
              class="w-56"
              @pressEnter="handleFilterChange"
            >
              <template #prefix>
                <Search class="size-4 text-muted-foreground" />
              </template>
            </Input>
          </div>
          <div class="flex flex-wrap items-center gap-2">
            <Input
              v-model:value="cleanupTime"
              :placeholder="$t('system.syslog_page.cleanup_time_placeholder')"
              class="w-64"
            ></Input>
            <Popconfirm
              :title="$t('system.syslog_page.cleanup_confirm_title')"
              :description="$t('system.syslog_page.cleanup_confirm')"
              :ok-text="$t('system.syslog_page.cleanup_btn')"
              :cancel-text="$t('system.syslog_page.cancel')"
              :ok-button-props="{ danger: true }"
              @confirm="handleCleanup"
            >
              <Button danger :loading="cleaning">
                <template #icon>
                  <Eraser class="size-4" />
                </template>
                {{ $t('system.syslog_page.cleanup_btn') }}
              </Button>
            </Popconfirm>
          </div>
        </div>

        <Table
          :columns="columns"
          :data-source="logList"
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
                    `${t} ${$t('system.syslog_page.total_suffix')}`,
                  onChange: handlePageChange,
                  onShowSizeChange: handlePageSizeChange,
                }
          "
          :scroll="{ x: 1700 }"
          size="small"
          row-key="id"
        >
          <template #bodyCell="{ column, record }">
            <!-- 记录时间 -->
            <template v-if="column.key === 'createdAt'">
              <span class="text-xs text-muted-foreground">
                {{ formatTime(record.createdAt) }}
              </span>
            </template>

            <!-- 日志级别 -->
            <template v-else-if="column.key === 'logLevel'">
              <Tag :color="levelColor(record.logLevel)">
                {{ displayValue(record.logLevel) }}
              </Tag>
            </template>

            <!-- 日志类型 -->
            <template v-else-if="column.key === 'logType'">
              <Tag color="cyan">{{ displayValue(record.logType) }}</Tag>
            </template>

            <!-- 所属模块 -->
            <template v-else-if="column.key === 'module'">
              <Tag color="purple">{{ displayValue(record.module) }}</Tag>
            </template>

            <!-- 操作用户 -->
            <template v-else-if="column.key === 'username'">
              <div class="leading-4">
                <div class="truncate text-xs font-medium">
                  {{ displayValue(record.username) }}
                </div>
                <div class="text-[11px] text-muted-foreground">
                  #{{ displayValue(record.userId) }}
                </div>
              </div>
            </template>

            <!-- 请求方式 -->
            <template v-else-if="column.key === 'requestMethod'">
              <Tag :color="methodColor(record.requestMethod)">
                {{ displayValue(record.requestMethod) }}
              </Tag>
            </template>

            <!-- 请求地址 -->
            <template v-else-if="column.key === 'requestUrl'">
              <code class="text-xs">{{ displayValue(record.requestUrl) }}</code>
            </template>

            <!-- 耗时 -->
            <template v-else-if="column.key === 'executionTime'">
              <span
                class="tabular-nums"
                :class="toNumber(record.executionTime) >= 1000 ? 'font-medium text-red-500' : ''"
              >
                {{ toNumber(record.executionTime) }}
                {{ $t('system.syslog_page.ms_unit') }}
              </span>
            </template>

            <!-- 服务器 -->
            <template v-else-if="column.key === 'serverHost'">
              <code class="text-xs">{{ serverAddress(record) }}</code>
            </template>

            <!-- 操作 -->
            <template v-else-if="column.key === 'action'">
              <Button type="link" size="small" @click="openDetail(record)">
                {{ $t('system.syslog_page.detail') }}
              </Button>
              <Popconfirm
                :title="$t('system.syslog_page.delete_confirm_title')"
                :description="$t('system.syslog_page.delete_confirm')"
                :ok-text="$t('system.syslog_page.delete')"
                :cancel-text="$t('system.syslog_page.cancel')"
                :ok-button-props="{ danger: true }"
                @confirm="handleDelete(record)"
              >
                <Button
                  type="link"
                  size="small"
                  danger
                  :loading="deletingId === record.id"
                >
                  {{ $t('system.syslog_page.delete') }}
                </Button>
              </Popconfirm>
            </template>
          </template>

          <template #emptyText>
            <span class="text-sm text-muted-foreground">
              {{ $t('system.syslog_page.empty') }}
            </span>
          </template>
        </Table>
      </Card>
    </div>

    <!-- 详情弹窗 -->
    <Modal
      v-model:open="detailVisible"
      :title="$t('system.syslog_page.detail_title')"
      :width="780"
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
            {{ displayValue(detailRecord.action) }}
          </div>
          <div class="mt-2 flex flex-wrap items-center gap-2">
            <Tag :color="levelColor(detailRecord.logLevel)">
              {{ displayValue(detailRecord.logLevel) }}
            </Tag>
            <Tag :color="statusInfo(detailRecord.status).color">
              {{ $t('system.syslog_page.fields.status') }}:
              {{ statusInfo(detailRecord.status).text }}
            </Tag>
            <Tag color="cyan">
              {{ displayValue(detailRecord.logType) }}
            </Tag>
            <Tag color="purple">
              {{ displayValue(detailRecord.module) }}
            </Tag>
            <Tag :color="methodColor(detailRecord.requestMethod)">
              {{ displayValue(detailRecord.requestMethod) }}
            </Tag>
          </div>
          <div
            v-if="detailRecord.content"
            class="mt-3 whitespace-pre-wrap rounded-md border border-border bg-accent/40 p-3 text-sm leading-6"
          >
            {{ detailRecord.content }}
          </div>
        </div>

        <!-- 错误堆栈 -->
        <div v-if="detailRecord.errorStack" class="mb-3">
          <div class="mb-1 text-xs font-medium text-red-500">
            {{ $t('system.syslog_page.error_stack') }}
          </div>
          <pre
            class="max-h-56 overflow-auto whitespace-pre-wrap break-all rounded-md border border-red-200 bg-red-50 p-3 text-xs leading-5 text-red-600"
          >{{ detailRecord.errorStack }}</pre>
        </div>

        <Descriptions :column="2" size="small" bordered class="mb-3">
          <Descriptions.Item :label="$t('system.syslog_page.fields.id')">
            {{ detailRecord.id }}
          </Descriptions.Item>
          <Descriptions.Item :label="$t('system.syslog_page.fields.traceId')">
            <code class="text-xs">{{ displayValue(detailRecord.traceId) }}</code>
          </Descriptions.Item>
          <Descriptions.Item :label="$t('system.syslog_page.fields.spanId')">
            <code class="text-xs">{{ displayValue(detailRecord.spanId) }}</code>
          </Descriptions.Item>
          <Descriptions.Item :label="$t('system.syslog_page.fields.requestId')">
            <code class="text-xs">{{ displayValue(detailRecord.requestId) }}</code>
          </Descriptions.Item>
          <Descriptions.Item :label="$t('system.syslog_page.fields.username')">
            {{ displayValue(detailRecord.username) }}
            <span class="text-xs text-muted-foreground">
              #{{ displayValue(detailRecord.userId) }}
            </span>
          </Descriptions.Item>
          <Descriptions.Item :label="$t('system.syslog_page.fields.userIp')">
            <code class="text-xs">{{ displayValue(detailRecord.userIp) }}</code>
          </Descriptions.Item>
          <Descriptions.Item :label="$t('system.syslog_page.fields.serverHost')">
            <code class="text-xs">{{ serverAddress(detailRecord) }}</code>
          </Descriptions.Item>
          <Descriptions.Item
            :label="$t('system.syslog_page.fields.executionTime')"
          >
            <span
              class="tabular-nums"
              :class="toNumber(detailRecord.executionTime) >= 1000 ? 'font-medium text-red-500' : ''"
            >
              {{ toNumber(detailRecord.executionTime) }}
              {{ $t('system.syslog_page.ms_unit') }}
            </span>
          </Descriptions.Item>
          <Descriptions.Item
            :label="$t('system.syslog_page.fields.createdAt')"
            :span="2"
          >
            {{ formatTime(detailRecord.createdAt) }}
          </Descriptions.Item>
        </Descriptions>

        <!-- 请求地址 -->
        <div class="mb-3">
          <div class="mb-1 text-xs font-medium text-muted-foreground">
            {{ $t('system.syslog_page.fields.requestMethod') }} /
            {{ $t('system.syslog_page.fields.requestUrl') }}
          </div>
          <div class="rounded-md border border-border bg-accent/40 p-3">
            <code class="text-xs leading-5">
              {{ displayValue(detailRecord.requestMethod) }}
              {{ displayValue(detailRecord.requestUrl) }}
            </code>
          </div>
        </div>

        <!-- 请求参数 -->
        <div class="mb-3">
          <div class="mb-1 text-xs font-medium text-muted-foreground">
            {{ $t('system.syslog_page.fields.requestParams') }}
          </div>
          <pre
            v-if="prettyJson(detailRecord.requestParams)"
            class="max-h-48 overflow-auto whitespace-pre-wrap break-all rounded-md border border-border bg-accent/40 p-3 text-xs leading-5"
          >{{ prettyJson(detailRecord.requestParams) }}</pre>
          <div v-else class="text-xs text-muted-foreground">
            {{ $t('system.syslog_page.params_empty') }}
          </div>
        </div>

        <!-- 响应数据 -->
        <div class="mb-3">
          <div class="mb-1 text-xs font-medium text-muted-foreground">
            {{ $t('system.syslog_page.fields.responseData') }}
          </div>
          <pre
            v-if="prettyJson(detailRecord.responseData)"
            class="max-h-48 overflow-auto whitespace-pre-wrap break-all rounded-md border border-border bg-accent/40 p-3 text-xs leading-5"
          >{{ prettyJson(detailRecord.responseData) }}</pre>
          <div v-else class="text-xs text-muted-foreground">
            {{ $t('system.syslog_page.response_empty') }}
          </div>
        </div>

        <!-- User-Agent -->
        <div v-if="detailRecord.userAgent">
          <div class="mb-1 text-xs font-medium text-muted-foreground">
            {{ $t('system.syslog_page.fields.userAgent') }}
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