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
  Progress,
  Result,
  Skeleton,
  Table,
  Tag,
} from 'antdv-next';

import {
  clearAllCache,
  clearCache,
  getCacheDetail,
  getCacheKeys,
  getCacheStats,
} from '#/api/management/service/cache';

defineOptions({ name: 'CacheMonitoring' });

// =========================
// 状态
// =========================
const loading = ref(false);
const errorMsg = ref('');
const deletingKey = ref('');
const clearingAll = ref(false);

// 统计信息
const stats = ref<{
  entries?: Record<string, any>;
  hitRate?: number;
  maxSize?: number;
  memoryUsage?: number;
  missRate?: number;
  name?: null | string;
  size?: number;
  status?: null | string;
  type?: null | string;
}>({});

// 键列表
const cacheKeys = ref<string[]>([]);
const keyword = ref('');

// 详情弹窗
const detailVisible = ref(false);
const detailKey = ref('');
const detailValue = ref<any>(null);
const detailLoading = ref(false);

// =========================
// 工具
// =========================
function formatBytes(bytes?: null | number): string {
  const n = Number(bytes);
  if (!Number.isFinite(n) || n < 0) return '-';
  const units = ['B', 'KB', 'MB', 'GB', 'TB'];
  let value = n;
  let unitIndex = 0;
  while (value >= 1024 && unitIndex < units.length - 1) {
    value /= 1024;
    unitIndex += 1;
  }
  return `${value.toFixed(value >= 100 || unitIndex === 0 ? 0 : 1)} ${units[unitIndex]}`;
}

/** hitRate/missRate 为 0~1 比例，转为百分比 */
function toPercent(rate?: null | number): number {
  const n = Number(rate);
  if (!Number.isFinite(n)) return 0;
  return Number((n * 100).toFixed(1));
}

function rateColor(percent: number): string {
  if (percent >= 80) return '#52c41a';
  if (percent >= 50) return '#fa8c16';
  return '#f5222d';
}

function valueTypeOf(value: any): string {
  if (Array.isArray(value)) return $t('service.cache_page.type_array');
  if (typeof value === 'object' && value !== null)
    return $t('service.cache_page.type_object');
  if (typeof value === 'string') return $t('service.cache_page.type_string');
  if (typeof value === 'number') return $t('service.cache_page.type_number');
  if (typeof value === 'boolean')
    return $t('service.cache_page.type_boolean');
  return typeof value;
}

/** 值类型对应 Tag 颜色 */
function valueTypeColor(value: any): string {
  if (Array.isArray(value)) return 'geekblue';
  if (typeof value === 'object' && value !== null) return 'blue';
  return 'default';
}

function previewOf(value: any): string {
  try {
    const text = typeof value === 'string' ? value : JSON.stringify(value);
    if (!text) return '-';
    return text.length > 80 ? `${text.slice(0, 80)}…` : text;
  } catch {
    return '-';
  }
}

/** entries 对象 → 键值行（支持关键字过滤） */
const keyRows = computed(() => {
  const entries = stats.value.entries ?? {};
  const kw = keyword.value.trim().toLowerCase();
  return cacheKeys.value
    .filter((k) => !kw || k.toLowerCase().includes(kw))
    .map((key) => ({
      key,
      value: Object.prototype.hasOwnProperty.call(entries, key)
        ? entries[key]
        : undefined,
    }));
});

const columns = computed<TableColumnsType>(() => [
  {
    title: $t('service.cache_page.fields.key'),
    dataIndex: 'key',
    width: 200,
  },
  {
    title: $t('service.cache_page.fields.valueType'),
    key: 'valueType',
    width: 100,
  },
  {
    title: $t('service.cache_page.fields.preview'),
    key: 'preview',
    ellipsis: true,
  },
  {
    title: $t('service.cache_page.fields.action'),
    key: 'action',
    width: 140,
  },
]);

// =========================
// 数据加载
// =========================
async function fetchData() {
  loading.value = true;
  errorMsg.value = '';
  try {
    const [statsRes, keysRes] = await Promise.all([
      getCacheStats(),
      getCacheKeys({}),
    ]);
    // 兼容拦截器已解包 / 未解包（code 为字符串 "200" 时由全局拦截器规范化）
    stats.value = statsRes?.name ? statsRes : (statsRes?.data ?? {});
    cacheKeys.value = Array.isArray(keysRes)
      ? keysRes
      : (keysRes?.data ?? keysRes?.list ?? []);
  } catch (error: any) {
    errorMsg.value = error?.message || $t('service.cache_page.error_desc');
  } finally {
    loading.value = false;
  }
}

onMounted(() => {
  fetchData();
});

// =========================
// 操作：详情 / 清除 / 清空
// =========================
async function openDetail(key: string) {
  detailKey.value = key;
  detailVisible.value = true;
  detailLoading.value = true;
  try {
    const res: any = await getCacheDetail(key);
    detailValue.value = res && typeof res === 'object' && 'key' in (res ?? {})
      ? res
      : (stats.value.entries?.[key] ?? res);
  } catch (error: any) {
    // 详情接口失败时回退到 stats.entries 中的值
    const fallback = stats.value.entries?.[key];
    if (fallback !== undefined) {
      detailValue.value = fallback;
    } else {
      message.error(error?.message || $t('service.cache_page.error_desc'));
    }
  } finally {
    detailLoading.value = false;
  }
}

async function handleDelete(key: string) {
  deletingKey.value = key;
  try {
    await clearCache(key);
    message.success($t('service.cache_page.delete_success'));
    await fetchData();
  } catch (error: any) {
    message.error(error?.message || $t('service.cache_page.delete_failed'));
  } finally {
    deletingKey.value = '';
  }
}

async function handleClearAll() {
  clearingAll.value = true;
  try {
    await clearAllCache();
    message.success($t('service.cache_page.clear_all_success'));
    await fetchData();
  } catch (error: any) {
    message.error(error?.message || $t('service.cache_page.delete_failed'));
  } finally {
    clearingAll.value = false;
  }
}
</script>

<template>
  <Page :auto-content-height="true">
    <div class="mx-auto w-full">
      <!-- 加载中：骨架屏 -->
      <Card
        v-if="loading && !stats.name"
        :title="$t('service.cache_page.title')"
        :styles="{ body: { padding: '24px' } }"
      >
        <Skeleton :active="true" :paragraph="{ rows: 8 }" />
      </Card>

      <!-- 加载失败：错误占位 + 重试 -->
      <Card
        v-else-if="errorMsg && !stats.name"
        :title="$t('service.cache_page.title')"
        :styles="{ body: { padding: '24px' } }"
      >
        <Result
          status="error"
          :title="$t('service.cache_page.error_title')"
          :sub-title="errorMsg"
        >
          <template #extra>
            <Button type="primary" @click="fetchData">
              {{ $t('service.cache_page.retry') }}
            </Button>
          </template>
        </Result>
      </Card>

      <template v-else>
        <!-- 缓存统计 -->
        <Card
          :title="$t('service.cache_page.stats_info')"
          :styles="{ body: { padding: '16px 24px' } }"
          class="mb-4"
        >
          <template #extra>
            <div class="flex items-center gap-2">
              <Popconfirm
                :title="$t('service.cache_page.clear_all_confirm_title')"
                :description="$t('service.cache_page.clear_all_confirm')"
                :ok-text="$t('service.cache_page.clear_all')"
                :cancel-text="$t('service.cache_page.cancel')"
                :ok-button-props="{ danger: true }"
                @confirm="handleClearAll"
              >
                <Button danger :loading="clearingAll">
                  {{ $t('service.cache_page.clear_all') }}
                </Button>
              </Popconfirm>
              <Button :loading="loading" @click="fetchData">
                <template #icon>
                  <span class="i-lucide-refresh-cw mr-1 size-4 align-middle"></span>
                </template>
                {{ $t('service.cache_page.refresh') }}
              </Button>
            </div>
          </template>
          <Descriptions :column="4" size="small">
            <Descriptions.Item
              :label="$t('service.cache_page.fields.name')"
            >
              {{ stats.name || '-' }}
            </Descriptions.Item>
            <Descriptions.Item
              :label="$t('service.cache_page.fields.type')"
            >
              {{ stats.type || '-' }}
            </Descriptions.Item>
            <Descriptions.Item
              :label="$t('service.cache_page.fields.status')"
            >
              <Tag :color="stats.status === 'active' ? 'green' : 'default'">
                {{
                  stats.status === 'active'
                    ? $t('service.cache_page.status_active')
                    : (stats.status || $t('service.cache_page.status_inactive'))
                }}
              </Tag>
            </Descriptions.Item>
            <Descriptions.Item
              :label="$t('service.cache_page.fields.size')"
            >
              {{ stats.size ?? 0 }} /
              {{ stats.maxSize ?? '-' }}
              {{ $t('service.cache_page.entries') }}
            </Descriptions.Item>
            <Descriptions.Item
              :label="$t('service.cache_page.fields.memoryUsage')"
            >
              {{ formatBytes(stats.memoryUsage) }}
            </Descriptions.Item>
            <Descriptions.Item
              :label="$t('service.cache_page.fields.hitRate')"
            >
              <div class="flex min-w-32 items-center gap-2">
                <Progress
                  type="line"
                  size="small"
                  :percent="toPercent(stats.hitRate)"
                  :stroke-color="rateColor(toPercent(stats.hitRate))"
                  :show-info="false"
                  class="flex-1"
                />
                <span class="text-xs tabular-nums">
                  {{ toPercent(stats.hitRate) }}%
                </span>
              </div>
            </Descriptions.Item>
            <Descriptions.Item
              :label="$t('service.cache_page.fields.missRate')"
            >
              <div class="flex min-w-32 items-center gap-2">
                <Progress
                  type="line"
                  size="small"
                  :percent="toPercent(stats.missRate)"
                  :stroke-color="rateColor(toPercent(stats.missRate))"
                  :show-info="false"
                  class="flex-1"
                />
                <span class="text-xs tabular-nums">
                  {{ toPercent(stats.missRate) }}%
                </span>
              </div>
            </Descriptions.Item>
          </Descriptions>
        </Card>

        <!-- 缓存键列表 -->
        <Card
          :title="$t('service.cache_page.key_list')"
          :styles="{ body: { padding: '0' } }"
        >
          <template #extra>
            <Input
              v-model:value="keyword"
              :placeholder="`${$t('service.cache_page.fields.key')}...`"
              allow-clear
              class="w-56"
            >
              <template #prefix>
                <span class="i-lucide-search size-4 text-muted-foreground"></span>
              </template>
            </Input>
          </template>
          <Table
            :columns="columns"
            :data-source="keyRows"
            :loading="loading"
            :pagination="false"
            size="small"
            row-key="key"
          >
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'valueType'">
                <Tag :color="valueTypeColor(record.value)">
                  {{ valueTypeOf(record.value) }}
                </Tag>
              </template>
              <template v-else-if="column.key === 'preview'">
                <code class="text-xs">
                  {{ record.value === undefined ? '-' : previewOf(record.value) }}
                </code>
              </template>
              <template v-else-if="column.key === 'action'">
                <Button
                  type="link"
                  size="small"
                  @click="openDetail(record.key)"
                >
                  {{ $t('service.cache_page.view_detail') }}
                </Button>
                <Popconfirm
                  :title="$t('service.cache_page.delete_confirm_title')"
                  :description="$t('service.cache_page.delete_confirm')"
                  :ok-text="$t('service.cache_page.delete')"
                  :cancel-text="$t('service.cache_page.cancel')"
                  :ok-button-props="{ danger: true }"
                  @confirm="handleDelete(record.key)"
                >
                  <Button
                    type="link"
                    size="small"
                    danger
                    :loading="deletingKey === record.key"
                  >
                    {{ $t('service.cache_page.delete') }}
                  </Button>
                </Popconfirm>
              </template>
            </template>
            <template #emptyText>
              <span class="text-sm text-muted-foreground">
                {{ $t('service.cache_page.empty_keys') }}
              </span>
            </template>
          </Table>
        </Card>
      </template>
    </div>

    <!-- 缓存详情弹窗 -->
    <Modal
      v-model:open="detailVisible"
      :title="`${$t('service.cache_page.detail_title')} · ${detailKey}`"
      :width="680"
      :footer="null"
      :styles="{ body: { padding: '20px 24px' } }"
    >
      <Skeleton v-if="detailLoading" :active="true" :paragraph="{ rows: 4 }" />
      <pre
        v-else
        class="max-h-[60vh] overflow-auto rounded-md border border-border bg-muted/40 p-4 text-xs leading-5"
      >{{ JSON.stringify(detailValue, null, 2) }}</pre>
    </Modal>
  </Page>
</template>

<style scoped>
.code-cell {
  font-family: var(--font-mono, monospace);
  font-size: 12px;
}
</style>
