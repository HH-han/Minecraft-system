<script lang="ts" setup>
import type { TableColumnsType } from 'antdv-next';
import type { EchartsUIType } from '@vben/plugins/echarts';

import { computed, onMounted, ref } from 'vue';

import { Page } from '@vben/common-ui';
import { RefreshCw, Search } from '@vben/icons';
import { $t } from '@vben/locales';
import { EchartsUI, useEcharts } from '@vben/plugins/echarts';

import {
  Button,
  Card,
  Descriptions,
  Input,
  Modal,
  Select,
  Table,
  Tag,
} from 'antdv-next';

import {
  getRailwayOrderList,
  getRailwayStats,
  getRailwayTrend,
} from '#/api/management/railway';

defineOptions({ name: 'RailwayOrderManagement' });

interface TicketInfo {
  id: number;
  passengerName: string;
  idCardNo: string;
  seatNo: string;
  priceCents: number;
  status: string;
}

interface OrderInfo {
  id: number;
  orderNo: string;
  trainNo: string;
  trainType: string;
  serviceDate: string;
  fromStationName: string;
  toStationName: string;
  fromTime: string;
  toTime: string;
  arriveDayOffset?: number;
  seatType: string;
  passengerCount: number;
  totalAmount: number;
  contactPhone?: string;
  status: string;
  expireTime?: string;
  payTime?: string;
  createTime?: string;
  tickets?: TicketInfo[];
}

interface StatsInfo {
  todayOrders?: number;
  todayRevenue?: number;
  pendingOrders?: number;
  paidOrders?: number;
  totalOrders?: number;
  totalRevenue?: number;
}

interface TrendInfo {
  date: string;
  amount: number;
  orders: number;
}

const loading = ref(false);
const orders = ref<OrderInfo[]>([]);
const stats = ref<StatsInfo>({});
const trend = ref<TrendInfo[]>([]);
const chartRef = ref<EchartsUIType>();
const { renderEcharts } = useEcharts(chartRef);
const pageNum = ref(1);
const pageSize = ref(10);
const total = ref(0);
const statusFilter = ref<null | string>(null);
const keyword = ref('');

const statusOptions = computed(() => [
  { label: $t('railway.order_page.status_pending'), value: 'PENDING' },
  { label: $t('railway.order_page.status_paid'), value: 'PAID' },
  { label: $t('railway.order_page.status_cancelled'), value: 'CANCELLED' },
  { label: $t('railway.order_page.status_refunded'), value: 'REFUNDED' },
]);

const statusText = (status?: string) => {
  if (!status) return '-';
  return $t(`railway.order_page.status_${String(status).toLowerCase()}`);
};

const statusColor = (status?: string) => {
  if (status === 'PENDING') return 'orange';
  if (status === 'PAID') return 'green';
  if (status === 'REFUNDED') return 'purple';
  return 'default';
};

const formatYuan = (cents?: number) => (Number(cents || 0) / 100).toFixed(2);
const displayValue = (value: any) => (value === null || value === undefined || value === '' ? '-' : String(value));

const columns = computed<TableColumnsType>(() => [
  { title: $t('railway.order_page.order_no'), dataIndex: 'orderNo', width: 210 },
  { title: $t('railway.order_page.travel_date'), dataIndex: 'serviceDate', width: 110 },
  { title: $t('railway.order_page.train'), key: 'train', width: 110 },
  { title: $t('railway.order_page.route'), key: 'route', width: 210 },
  { title: $t('railway.order_page.seat_type'), key: 'seatType', width: 100 },
  { title: $t('railway.order_page.passengers'), dataIndex: 'passengerCount', width: 90 },
  { title: $t('railway.order_page.amount'), key: 'amount', width: 110 },
  { title: $t('railway.order_page.phone'), dataIndex: 'contactPhone', width: 130 },
  { title: $t('railway.common.status'), key: 'status', width: 100 },
  { title: $t('railway.order_page.create_time'), dataIndex: 'createTime', width: 170 },
  {
    title: $t('railway.common.actions'),
    key: 'action',
    width: 90,
    fixed: 'right',
  },
]);

const ticketColumns = computed<TableColumnsType>(() => [
  { title: $t('railway.order_page.passenger_name'), dataIndex: 'passengerName' },
  { title: $t('railway.order_page.id_card'), dataIndex: 'idCardNo' },
  { title: $t('railway.order_page.seat_no'), dataIndex: 'seatNo' },
  { title: $t('railway.order_page.ticket_status'), dataIndex: 'status' },
]);

const statCards = computed(() => [
  {
    key: 'todayOrders',
    label: $t('railway.order_page.today_orders'),
    value: Number(stats.value.todayOrders || 0),
    tone: 'text-blue-500',
  },
  {
    key: 'todayRevenue',
    label: $t('railway.order_page.today_revenue'),
    value: `¥${formatYuan(stats.value.todayRevenue)}`,
    tone: 'text-emerald-500',
  },
  {
    key: 'pendingOrders',
    label: $t('railway.order_page.pending_orders'),
    value: Number(stats.value.pendingOrders || 0),
    tone: 'text-orange-500',
  },
  {
    key: 'paidOrders',
    label: $t('railway.order_page.paid_orders'),
    value: Number(stats.value.paidOrders || 0),
    tone: 'text-cyan-500',
  },
  {
    key: 'totalOrders',
    label: $t('railway.order_page.total_orders'),
    value: Number(stats.value.totalOrders || 0),
    tone: 'text-purple-500',
  },
  {
    key: 'totalRevenue',
    label: $t('railway.order_page.total_revenue'),
    value: `¥${formatYuan(stats.value.totalRevenue)}`,
    tone: 'text-red-500',
  },
]);

async function fetchStats() {
  try {
    const res: any = await getRailwayStats();
    stats.value = res || {};
  } catch {
    stats.value = {};
  }
}

async function fetchTrend() {
  try {
    const res: any = await getRailwayTrend({ days: 7 });
    trend.value = Array.isArray(res) ? res : [];
    renderEcharts({
      grid: { bottom: 28, left: 48, right: 20, top: 24 },
      tooltip: { trigger: 'axis' },
      xAxis: {
        boundaryGap: false,
        data: trend.value.map((item) => String(item.date).slice(5)),
        type: 'category',
      },
      yAxis: {
        axisLabel: { formatter: (value: any) => `¥${value}` },
        type: 'value',
      },
      series: [
        {
          areaStyle: { opacity: 0.12 },
          data: trend.value.map((item) => Number(item.amount || 0) / 100),
          lineStyle: { width: 2 },
          name: $t('railway.order_page.revenue_trend'),
          smooth: true,
          symbolSize: 6,
          type: 'line',
        },
      ],
    });
  } catch {
    trend.value = [];
  }
}

async function fetchOrders() {
  loading.value = true;
  try {
    const res: any = await getRailwayOrderList({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      status: statusFilter.value || undefined,
      keyword: keyword.value || undefined,
    });
    orders.value = res?.records ?? [];
    total.value = Number(res?.total || 0);
  } catch {
    orders.value = [];
    total.value = 0;
  } finally {
    loading.value = false;
  }
}

async function fetchAll() {
  await Promise.all([fetchOrders(), fetchStats(), fetchTrend()]);
}

onMounted(fetchAll);

function handlePageChange(page: number, size: number) {
  pageNum.value = page;
  pageSize.value = size;
  fetchOrders();
}

function handlePageSizeChange(_page: number, size: number) {
  pageSize.value = size;
  pageNum.value = 1;
  fetchOrders();
}

// =========================
// 详情
// =========================
const detailVisible = ref(false);
const detailRecord = ref<null | OrderInfo>(null);

function openDetail(record: OrderInfo) {
  detailRecord.value = record;
  detailVisible.value = true;
}
</script>

<template>
  <Page :auto-content-height="true">
    <div class="flex flex-col gap-4">
      <!-- 统计 -->
      <div class="grid grid-cols-2 gap-3 md:grid-cols-3 xl:grid-cols-6">
        <Card
          v-for="card in statCards"
          :key="card.key"
          :styles="{ body: { padding: '16px 18px' } }"
        >
          <div class="text-xs text-muted-foreground">{{ card.label }}</div>
          <div class="mt-2 text-2xl font-semibold tabular-nums" :class="card.tone">
            {{ card.value }}
          </div>
        </Card>
      </div>

      <!-- 收入趋势 -->
      <Card :title="$t('railway.order_page.revenue_trend')">
        <div class="h-64 w-full">
          <EchartsUI ref="chartRef" />
        </div>
      </Card>

      <!-- 订单列表 -->
      <Card :title="$t('railway.order_page.title')" :styles="{ body: { padding: '0' } }">
        <template #extra>
          <div class="flex flex-wrap items-center gap-2">
            <Select
              v-model:value="statusFilter"
              :placeholder="$t('railway.common.status')"
              allow-clear
              class="w-36"
              :options="statusOptions"
              @change="handlePageChange(1, pageSize)"
            />
            <Input
              v-model:value="keyword"
              :placeholder="$t('railway.order_page.search_placeholder')"
              allow-clear
              class="w-52"
              @press-enter="handlePageChange(1, pageSize)"
            >
              <template #prefix>
                <Search class="size-4 text-muted-foreground" />
              </template>
            </Input>
            <Button :loading="loading" @click="fetchAll">
              <template #icon>
                <RefreshCw class="mr-1 size-4 align-middle" />
              </template>
              {{ $t('railway.common.refresh') }}
            </Button>
          </div>
        </template>

        <Table
          :columns="columns"
          :data-source="orders"
          :loading="loading"
          :pagination="{
            current: pageNum,
            pageSize,
            showSizeChanger: true,
            pageSizeOptions: ['10', '20', '50'],
            total,
            showTotal: (count: number) =>
              $t('railway.common.total', { total: count }),
            onChange: handlePageChange,
            onShowSizeChange: handlePageSizeChange,
          }"
          :scroll="{ x: 1400 }"
          row-key="id"
          size="small"
        >
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'train'">
              <div class="flex items-center gap-1">
                <span class="font-medium">{{ record.trainNo }}</span>
                <Tag color="blue">{{ record.trainType }}</Tag>
              </div>
            </template>
            <template v-else-if="column.key === 'route'">
              <span class="text-xs">
                {{ record.fromStationName || '-' }}
                <span class="mx-1 text-muted-foreground">→</span>
                {{ record.toStationName || '-' }}
              </span>
            </template>
            <template v-else-if="column.key === 'seatType'">
              <Tag color="cyan">{{ $t(`railway.seat.${record.seatType}`) }}</Tag>
            </template>
            <template v-else-if="column.key === 'amount'">
              <span class="font-medium tabular-nums text-red-500">
                ¥{{ formatYuan(record.totalAmount) }}
              </span>
            </template>
            <template v-else-if="column.key === 'status'">
              <Tag :color="statusColor(record.status)">{{ statusText(record.status) }}</Tag>
            </template>
            <template v-else-if="column.key === 'action'">
              <Button type="link" size="small" @click="openDetail(record)">
                {{ $t('railway.common.detail') }}
              </Button>
            </template>
          </template>
          <template #emptyText>
            <span class="text-sm text-muted-foreground">{{ $t('railway.common.empty') }}</span>
          </template>
        </Table>
      </Card>
    </div>

    <!-- 订单详情 -->
    <Modal
      v-model:open="detailVisible"
      :title="$t('railway.order_page.detail_title')"
      :width="760"
      :footer="null"
      @cancel="detailRecord = null"
    >
      <template v-if="detailRecord">
        <div class="mb-4 rounded-md border border-border bg-accent/40 p-4">
          <div class="flex items-center justify-between gap-4">
            <div>
              <div class="text-lg font-semibold">{{ detailRecord.trainNo }}</div>
              <div class="mt-1 text-xs text-muted-foreground">
                {{ detailRecord.serviceDate }} · {{ displayValue(detailRecord.fromTime) }} -
                {{ displayValue(detailRecord.toTime) }}
              </div>
            </div>
            <Tag :color="statusColor(detailRecord.status)">{{ statusText(detailRecord.status) }}</Tag>
          </div>
          <div class="mt-3 flex items-center gap-2 text-sm">
            <span class="font-medium">{{ detailRecord.fromStationName }}</span>
            <span class="text-muted-foreground">→</span>
            <span class="font-medium">{{ detailRecord.toStationName }}</span>
            <Tag color="cyan">{{ $t(`railway.seat.${detailRecord.seatType}`) }}</Tag>
          </div>
        </div>

        <Descriptions :column="2" size="small" bordered>
          <Descriptions.Item :label="$t('railway.order_page.order_no')" :span="2">
            {{ detailRecord.orderNo }}
          </Descriptions.Item>
          <Descriptions.Item :label="$t('railway.order_page.passengers')">
            {{ detailRecord.passengerCount }}
          </Descriptions.Item>
          <Descriptions.Item :label="$t('railway.order_page.amount')">
            <span class="font-medium text-red-500">¥{{ formatYuan(detailRecord.totalAmount) }}</span>
          </Descriptions.Item>
          <Descriptions.Item :label="$t('railway.order_page.phone')">
            {{ displayValue(detailRecord.contactPhone) }}
          </Descriptions.Item>
          <Descriptions.Item :label="$t('railway.order_page.create_time')">
            {{ displayValue(detailRecord.createTime) }}
          </Descriptions.Item>
          <Descriptions.Item :label="$t('railway.order_page.travel_date')">
            {{ displayValue(detailRecord.serviceDate) }}
          </Descriptions.Item>
          <Descriptions.Item :label="$t('railway.common.status')">
            {{ statusText(detailRecord.status) }}
          </Descriptions.Item>
        </Descriptions>

        <div class="mt-4 mb-2 text-sm font-semibold">{{ $t('railway.order_page.ticket_list') }}</div>
        <Table
          :columns="ticketColumns"
          :data-source="detailRecord.tickets || []"
          :pagination="false"
          size="small"
          row-key="id"
        />
      </template>
    </Modal>
  </Page>
</template>
