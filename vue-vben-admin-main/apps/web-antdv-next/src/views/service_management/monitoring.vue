<script lang="ts" setup>
import type { TableColumnsType } from 'antdv-next';

import { computed, onBeforeUnmount, onMounted, ref } from 'vue';

import { Page } from '@vben/common-ui';
import { $t } from '@vben/locales';

import {
  Button,
  Card,
  Descriptions,
  Progress,
  Result,
  Skeleton,
  Table,
} from 'antdv-next';

import { getServerMonitor } from '#/api/management/service/monitoring';

defineOptions({ name: 'ServerMonitoring' });

// =========================
// 状态
// =========================
const loading = ref(false);
const errorMsg = ref('');

// 监控数据
const cpu = ref<{ cores?: number; model?: null | string; usage?: number }>({});
const memory = ref<{ total?: number; used?: number }>({});
const disk = ref<{ total?: number; used?: number }>({});
const network = ref<{
  received?: number;
  sent?: number;
  speed?: number;
}>({});
const system = ref<{
  hostname?: null | string;
  kernelVersion?: null | string;
  loadavg?: any[];
  osName?: null | string;
  osVersion?: null | string;
  uptime?: number;
}>({});
const processes = ref<
  { cpuUsage?: number; memoryUsage?: number; name?: null | string }[]
>([]);
const processCount = ref<null | number>(null);

// =========================
// 格式化工具
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

function formatSpeed(bytesPerSecond?: null | number): string {
  const n = Number(bytesPerSecond);
  if (!Number.isFinite(n)) return '-';
  return `${formatBytes(n)}/s`;
}

function formatUptime(seconds?: null | number): string {
  const n = Number(seconds);
  if (!Number.isFinite(n) || n < 0) return '-';
  const days = Math.floor(n / 86_400);
  const hours = Math.floor((n % 86_400) / 3600);
  const minutes = Math.floor((n % 3600) / 60);
  const parts: string[] = [];
  if (days > 0) parts.push(`${days}天`);
  if (hours > 0) parts.push(`${hours}小时`);
  parts.push(`${minutes}分钟`);
  return parts.join('');
}

function usagePercent(used?: null | number, total?: null | number): number {
  const u = Number(used);
  const t = Number(total);
  if (!Number.isFinite(u) || !Number.isFinite(t) || t <= 0) return 0;
  return Number(((u / t) * 100).toFixed(1));
}

/** 资源占用颜色：>=90 红 / >=70 橙 / 其它 绿 */
function usageColor(percent: number): string {
  if (percent >= 90) return '#f5222d';
  if (percent >= 70) return '#fa8c16';
  return '#52c41a';
}

const cpuPercent = computed(() => usagePercent(cpu.value.usage, 100));
const memoryPercent = computed(() =>
  usagePercent(memory.value.used, memory.value.total),
);
const diskPercent = computed(() =>
  usagePercent(disk.value.used, disk.value.total),
);

const loadavgText = computed(() => {
  const arr = system.value.loadavg;
  if (Array.isArray(arr) && arr.length > 0) {
    return arr.map((v) => Number(v).toFixed(2)).join(' / ');
  }
  return $t('service.monitor_page.loadavg_empty');
});

// =========================
// 进程列表列定义
// =========================
const processColumns = computed<TableColumnsType>(() => [
  {
    title: $t('service.monitor_page.fields.name'),
    dataIndex: 'name',
    ellipsis: true,
  },
  {
    title: $t('service.monitor_page.fields.cpuUsage'),
    key: 'cpuUsage',
    width: 110,
  },
  {
    title: $t('service.monitor_page.fields.memoryUsage'),
    key: 'memoryUsage',
    width: 120,
  },
]);

// =========================
// 数据加载
// 响应结构：{ code: "200", msg, data: { cpu, memory, disk, network, system, processes, processCount } }
// =========================
async function fetchData(silent = false) {
  if (!silent) loading.value = true;
  errorMsg.value = '';
  try {
    const res: any = await getServerMonitor();
    // 兼容拦截器已解包（res 即 data）与未解包（res.data）两种情况
    const payload: any = res?.cpu ? res : (res?.data ?? {});
    cpu.value = payload?.cpu ?? {};
    memory.value = payload?.memory ?? {};
    disk.value = payload?.disk ?? {};
    network.value = payload?.network ?? {};
    system.value = payload?.system ?? {};
    processes.value = Array.isArray(payload?.processes) ? payload.processes : [];
    processCount.value =
      payload?.processCount ?? (processes.value.length || null);
  } catch (error: any) {
    errorMsg.value = error?.message || $t('service.monitor_page.error_desc');
  } finally {
    if (!silent) loading.value = false;
  }
}

// =========================
// 每 2 秒自动刷新（静默模式，不触发 loading 遮罩）
// =========================
const REFRESH_INTERVAL = 2000;
let refreshTimer: null | ReturnType<typeof setInterval> = null;

onMounted(() => {
  fetchData();
  refreshTimer = setInterval(() => fetchData(true), REFRESH_INTERVAL);
});

onBeforeUnmount(() => {
  if (refreshTimer) {
    clearInterval(refreshTimer);
    refreshTimer = null;
  }
});
</script>

<template>
  <Page :auto-content-height="true">
    <div class="mx-auto w-full">
      <!-- 加载中：骨架屏 -->
      <Card
        v-if="loading && !system.hostname"
        :title="$t('service.monitor_page.title')"
        :styles="{ body: { padding: '24px' } }"
      >
        <Skeleton :active="true" :paragraph="{ rows: 10 }" />
      </Card>

      <!-- 加载失败：错误占位 + 重试 -->
      <Card
        v-else-if="errorMsg && !system.hostname"
        :title="$t('service.monitor_page.title')"
        :styles="{ body: { padding: '24px' } }"
      >
        <Result
          status="error"
          :title="$t('service.monitor_page.error_title')"
          :sub-title="errorMsg"
        >
          <template #extra>
            <Button type="primary" @click="fetchData">
              {{ $t('service.monitor_page.retry') }}
            </Button>
          </template>
        </Result>
      </Card>

      <template v-else>
        <!-- 系统信息 -->
        <Card
          :title="$t('service.monitor_page.system_info')"
          :styles="{ body: { padding: '16px 24px' } }"
          class="mb-4"
        >
          <template #extra>
            <Button :loading="loading" @click="fetchData">
              <template #icon>
                <span class="i-lucide-refresh-cw mr-1 size-4 align-middle"></span>
              </template>
              {{ $t('service.monitor_page.refresh') }}
            </Button>
          </template>
          <Descriptions :column="3" size="small">
            <Descriptions.Item
              :label="$t('service.monitor_page.fields.hostname')"
            >
              {{ system.hostname || '-' }}
            </Descriptions.Item>
            <Descriptions.Item
              :label="$t('service.monitor_page.fields.osName')"
            >
              {{ system.osName || '-' }}
            </Descriptions.Item>
            <Descriptions.Item
              :label="$t('service.monitor_page.fields.osVersion')"
            >
              {{ system.osVersion || '-' }}
            </Descriptions.Item>
            <Descriptions.Item
              :label="$t('service.monitor_page.fields.kernelVersion')"
            >
              {{ system.kernelVersion || '-' }}
            </Descriptions.Item>
            <Descriptions.Item
              :label="$t('service.monitor_page.fields.uptime')"
            >
              {{ formatUptime(system.uptime) }}
            </Descriptions.Item>
            <Descriptions.Item
              :label="$t('service.monitor_page.process_count')"
            >
              {{ processCount ?? '-' }}
            </Descriptions.Item>
            <Descriptions.Item
              :label="$t('service.monitor_page.fields.loadavg')"
              :span="3"
            >
              {{ loadavgText }}
            </Descriptions.Item>
          </Descriptions>
        </Card>

        <!-- 资源占用：CPU / 内存 / 磁盘 -->
        <div class="mb-4 grid grid-cols-1 gap-4 md:grid-cols-3">
          <!-- CPU -->
          <Card
            :title="$t('service.monitor_page.cpu')"
            :styles="{ body: { padding: '20px' } }"
          >
            <div class="resource-card">
              <Progress
                type="circle"
                :percent="cpuPercent"
                :size="120"
                :stroke-color="usageColor(cpuPercent)"
              />
              <div class="resource-detail">
                <div class="resource-item">
                  <span class="resource-label">
                    {{ $t('service.monitor_page.usage') }}
                  </span>
                  <span class="resource-value">{{ cpuPercent }}%</span>
                </div>
                <div class="resource-item">
                  <span class="resource-label">
                    {{ $t('service.monitor_page.cores') }}
                  </span>
                  <span class="resource-value">
                    {{ cpu.cores ?? '-' }}
                  </span>
                </div>
                <div class="resource-item">
                  <span class="resource-label">
                    {{ $t('service.monitor_page.model') }}
                  </span>
                  <span class="resource-value">
                    {{ cpu.model || '-' }}
                  </span>
                </div>
              </div>
            </div>
          </Card>

          <!-- 内存 -->
          <Card
            :title="$t('service.monitor_page.memory')"
            :styles="{ body: { padding: '20px' } }"
          >
            <div class="resource-card">
              <Progress
                type="circle"
                :percent="memoryPercent"
                :size="120"
                :stroke-color="usageColor(memoryPercent)"
              />
              <div class="resource-detail">
                <div class="resource-item">
                  <span class="resource-label">
                    {{ $t('service.monitor_page.used') }}
                  </span>
                  <span class="resource-value">
                    {{ formatBytes(memory.used) }}
                  </span>
                </div>
                <div class="resource-item">
                  <span class="resource-label">
                    {{ $t('service.monitor_page.total') }}
                  </span>
                  <span class="resource-value">
                    {{ formatBytes(memory.total) }}
                  </span>
                </div>
                <div class="resource-item">
                  <span class="resource-label">
                    {{ $t('service.monitor_page.usage') }}
                  </span>
                  <span class="resource-value">{{ memoryPercent }}%</span>
                </div>
              </div>
            </div>
          </Card>

          <!-- 磁盘 -->
          <Card
            :title="$t('service.monitor_page.disk')"
            :styles="{ body: { padding: '20px' } }"
          >
            <div class="resource-card">
              <Progress
                type="circle"
                :percent="diskPercent"
                :size="120"
                :stroke-color="usageColor(diskPercent)"
              />
              <div class="resource-detail">
                <div class="resource-item">
                  <span class="resource-label">
                    {{ $t('service.monitor_page.used') }}
                  </span>
                  <span class="resource-value">
                    {{ formatBytes(disk.used) }}
                  </span>
                </div>
                <div class="resource-item">
                  <span class="resource-label">
                    {{ $t('service.monitor_page.total') }}
                  </span>
                  <span class="resource-value">
                    {{ formatBytes(disk.total) }}
                  </span>
                </div>
                <div class="resource-item">
                  <span class="resource-label">
                    {{ $t('service.monitor_page.usage') }}
                  </span>
                  <span class="resource-value">{{ diskPercent }}%</span>
                </div>
              </div>
            </div>
          </Card>
        </div>

        <!-- 网络 + 进程列表 -->
        <div class="grid grid-cols-1 gap-4 lg:grid-cols-3">
          <!-- 网络 -->
          <Card
            :title="$t('service.monitor_page.network')"
            :styles="{ body: { padding: '16px 24px' } }"
            class="lg:col-span-1"
          >
            <Descriptions :column="1" size="small">
              <Descriptions.Item
                :label="$t('service.monitor_page.speed')"
              >
                <span class="font-medium text-blue-500">
                  {{ formatSpeed(network.speed) }}
                </span>
              </Descriptions.Item>
              <Descriptions.Item
                :label="$t('service.monitor_page.sent')"
              >
                {{ formatBytes(network.sent) }}
              </Descriptions.Item>
              <Descriptions.Item
                :label="$t('service.monitor_page.received')"
              >
                {{ formatBytes(network.received) }}
              </Descriptions.Item>
            </Descriptions>
          </Card>

          <!-- 进程列表 -->
          <Card
            :title="`${$t('service.monitor_page.process_list')}${processCount !== null ? `（${processCount}）` : ''}`"
            :styles="{ body: { padding: '0' } }"
            class="lg:col-span-2"
          >
            <Table
              :columns="processColumns"
              :data-source="processes"
              :loading="loading"
              :pagination="false"
              :bordered="false"
              size="small"
              row-key="name"
            >
              <template #bodyCell="{ column, record }">
                <template v-if="column.key === 'cpuUsage'">
                  <span
                    class="font-medium"
                    :style="{ color: usageColor(record.cpuUsage ?? 0) }"
                  >
                    {{ Number(record.cpuUsage ?? 0).toFixed(2) }}%
                  </span>
                </template>
                <template v-else-if="column.key === 'memoryUsage'">
                  {{ formatBytes(record.memoryUsage) }}
                </template>
              </template>
              <template #emptyText>
                <span class="text-sm text-muted-foreground">
                  {{ $t('service.monitor_page.empty_processes') }}
                </span>
              </template>
            </Table>
          </Card>
        </div>
      </template>
    </div>
  </Page>
</template>

<style scoped>
.resource-card {
  display: flex;
  align-items: center;
  gap: 24px;
}

.resource-detail {
  display: flex;
  flex: 1;
  min-width: 0;
  flex-direction: column;
  gap: 10px;
}

.resource-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.resource-label {
  font-size: 13px;
  color: var(--muted-foreground-color, rgb(0 0 0 / 45%));
  white-space: nowrap;
}

.resource-value {
  font-size: 13px;
  font-weight: 500;
  font-variant-numeric: tabular-nums;
  text-align: right;
  word-break: break-all;
}
</style>
