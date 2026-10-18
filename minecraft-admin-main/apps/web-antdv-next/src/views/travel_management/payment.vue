<script lang="ts" setup>
import type { EchartsUIType } from '@vben/plugins/echarts';
import type { TableColumnsType } from 'antdv-next';

import { computed, onMounted, ref } from 'vue';

import { Page } from '@vben/common-ui';
import { RefreshCw, Search } from '@vben/icons';
import { $t } from '@vben/locales';
import { EchartsUI, useEcharts } from '@vben/plugins/echarts';

import {
  Button,
  Card,
  Input,
  InputNumber,
  message,
  Modal,
  Select,
  Table,
  Tabs,
  Tag,
} from 'antdv-next';

import { getRefundPage } from '#/api/management/logistics/refund';
import {
  getPayFlowPage,
  getPayNotifyLogPage,
  getPayOrderPage,
  refundPayOrder,
} from '#/api/management/travel/payment';

defineOptions({ name: 'TravelPaymentManagement' });

const activeTab = ref('orders');
const loading = ref(false);
const list = ref<any[]>([]);
const total = ref(0);
const pageNum = ref(1);
const pageSize = ref(10);
const payNo = ref('');
const bizOrderNo = ref('');
const status = ref<string | undefined>();
const channelCode = ref<string | undefined>();

const refundVisible = ref(false);
const refundSubmitting = ref(false);
const refundRecord = ref<any>(null);
const refundAmount = ref<number>();
const refundReason = ref('');

// 流水可视化数据（独立于表格分页，聚合全部流水）
const flowInsights = ref<any[]>([]);
const channelChartRef = ref<EchartsUIType>();
const trendChartRef = ref<EchartsUIType>();
const { renderEcharts: renderChannelChart } = useEcharts(channelChartRef);
const { renderEcharts: renderTrendChart } = useEcharts(trendChartRef);

const flowSummary = computed(() => {
  const records = flowInsights.value;
  const totalAmount = records.reduce(
    (sum, item) => sum + Number(item.amount ?? 0),
    0,
  );
  const successRecords = records.filter((item) => item.status === 'SUCCESS');
  const successAmount = successRecords.reduce(
    (sum, item) => sum + Number(item.amount ?? 0),
    0,
  );
  return {
    count: records.length,
    totalAmount: totalAmount.toFixed(2),
    successAmount: successAmount.toFixed(2),
    successRate: records.length
      ? ((successRecords.length / records.length) * 100).toFixed(1)
      : '0.0',
  };
});

const flowCards = computed(() => [
  {
    key: 'count',
    label: $t('travel.payment_page.insight.count'),
    tone: 'text-blue-500',
    value: flowSummary.value.count,
  },
  {
    key: 'totalAmount',
    label: $t('travel.payment_page.insight.totalAmount'),
    tone: 'text-emerald-500',
    value: `¥${flowSummary.value.totalAmount}`,
  },
  {
    key: 'successAmount',
    label: $t('travel.payment_page.insight.successAmount'),
    tone: 'text-cyan-500',
    value: `¥${flowSummary.value.successAmount}`,
  },
  {
    key: 'successRate',
    label: $t('travel.payment_page.insight.successRate'),
    tone: 'text-orange-500',
    value: `${flowSummary.value.successRate}%`,
  },
]);

const channels = ['MOCK', 'WECHAT', 'ALIPAY', 'UNIONPAY'];
const payStatuses = ['INIT', 'PAYING', 'SUCCESS', 'FAILED', 'CLOSED', 'REFUNDING', 'REFUNDED'];

const statusOptions = computed(() =>
  payStatuses.map((item) => ({
    label: $t(`travel.payment_page.status.${item}`),
    value: item,
  })),
);

const channelOptions = computed(() =>
  channels.map((item) => ({
    label: $t(`travel.payment_page.channel.${item}`),
    value: item,
  })),
);

const tabs = computed(() => [
  { key: 'orders', label: $t('travel.payment_page.tabs.orders') },
  { key: 'flows', label: $t('travel.payment_page.tabs.flows') },
  { key: 'refunds', label: $t('travel.payment_page.tabs.refunds') },
  { key: 'logs', label: $t('travel.payment_page.tabs.logs') },
]);

const columns = computed<TableColumnsType>(() => {
  if (activeTab.value === 'flows') {
    return [
      { title: $t('travel.payment_page.fields.flowNo'), dataIndex: 'flowNo', width: 220 },
      { title: $t('travel.payment_page.fields.payNo'), dataIndex: 'payNo', width: 220 },
      { title: $t('travel.payment_page.fields.channel'), key: 'channel', width: 110 },
      { title: $t('travel.payment_page.fields.amount'), key: 'amount', width: 110 },
      { title: $t('travel.payment_page.fields.status'), key: 'status', width: 100 },
      { title: $t('travel.payment_page.fields.prepayId'), dataIndex: 'channelPrepayId', width: 200 },
      { title: $t('travel.payment_page.fields.createTime'), key: 'createdAt', width: 170 },
    ];
  }
  if (activeTab.value === 'refunds') {
    return [
      { title: $t('travel.payment_page.fields.refundNo'), dataIndex: 'refundNo', width: 210 },
      { title: $t('travel.payment_page.fields.orderNo'), dataIndex: 'orderNo', width: 150 },
      { title: $t('travel.payment_page.fields.channel'), dataIndex: 'channel', width: 110 },
      { title: $t('travel.payment_page.fields.refundAmount'), key: 'amount', width: 120 },
      { title: $t('travel.payment_page.fields.status'), key: 'refundStatus', width: 100 },
      { title: $t('travel.payment_page.fields.channelRefundNo'), dataIndex: 'channelRefundNo', width: 190 },
      { title: $t('travel.payment_page.fields.failReason'), dataIndex: 'failReason', ellipsis: true },
      { title: $t('travel.payment_page.fields.refundTime'), key: 'refundTime', width: 170 },
    ];
  }
  if (activeTab.value === 'logs') {
    return [
      { title: $t('travel.payment_page.fields.channel'), key: 'channel', width: 110 },
      { title: $t('travel.payment_page.fields.verifyResult'), key: 'verifyResult', width: 120 },
      { title: $t('travel.payment_page.fields.processResult'), key: 'processResult', width: 130 },
      { title: $t('travel.payment_page.fields.errorMsg'), dataIndex: 'errorMsg', ellipsis: true },
      { title: $t('travel.payment_page.fields.rawBody'), dataIndex: 'rawBody', ellipsis: true },
      { title: $t('travel.payment_page.fields.createTime'), key: 'createdAt', width: 170 },
    ];
  }
  return [
    { title: $t('travel.payment_page.fields.payNo'), dataIndex: 'payNo', width: 210 },
    { title: $t('travel.payment_page.fields.bizOrderNo'), dataIndex: 'bizOrderNo', width: 150 },
    { title: $t('travel.payment_page.fields.bizType'), dataIndex: 'bizType', width: 100 },
    { title: $t('travel.payment_page.fields.channel'), key: 'channel', width: 110 },
    { title: $t('travel.payment_page.fields.payAmount'), key: 'payAmount', width: 110 },
    { title: $t('travel.payment_page.fields.status'), key: 'status', width: 110 },
    { title: $t('travel.payment_page.fields.payTime'), key: 'payTime', width: 170 },
    { title: $t('travel.payment_page.fields.createTime'), key: 'createdAt', width: 170 },
    { title: $t('logistics.common.action'), key: 'action', width: 110, fixed: 'right' },
  ];
});

function formatTime(value?: string) {
  return value ? String(value).replace('T', ' ') : '-';
}

function statusColor(value?: string) {
  if (value === 'SUCCESS') return 'success';
  if (value === 'FAILED' || value === 'CLOSED') return 'error';
  if (value === 'PAYING' || value === 'REFUNDING') return 'warning';
  return 'default';
}

function refundStatusColor(value?: number) {
  if (value === 2) return 'success';
  if (value === 3) return 'error';
  if (value === 1) return 'warning';
  return 'default';
}

async function fetchFlowInsights() {
  try {
    const res: any = await getPayFlowPage({
      pageNum: 1,
      pageSize: 500,
      payNo: payNo.value || undefined,
    });
    flowInsights.value = res?.records ?? [];
    renderFlowCharts();
  } catch {
    flowInsights.value = [];
  }
}

function renderFlowCharts() {
  const channelMap = new Map<string, number>();
  const trendMap = new Map<string, number>();

  flowInsights.value.forEach((item) => {
    const channel = String(item.channelCode || item.channel || 'MOCK');
    channelMap.set(
      channel,
      (channelMap.get(channel) ?? 0) + Number(item.amount ?? 0),
    );
    const date = String(item.createdAt ?? '').slice(0, 10);
    if (date) {
      trendMap.set(date, (trendMap.get(date) ?? 0) + Number(item.amount ?? 0));
    }
  });

  if (channelMap.size > 0) {
    renderChannelChart({
      tooltip: {
        trigger: 'item',
        formatter: (params: any) =>
          `${params.name}<br/>¥${Number(params.value).toFixed(2)} (${params.percent}%)`,
      },
      legend: { bottom: 0, type: 'scroll' },
      series: [
        {
          type: 'pie',
          radius: ['45%', '70%'],
          center: ['50%', '45%'],
          avoidLabelOverlap: true,
          itemStyle: { borderRadius: 6, borderColor: 'transparent', borderWidth: 2 },
          label: { show: false },
          emphasis: {
            label: {
              show: true,
              fontSize: 14,
              fontWeight: 'bold',
              formatter: '{b}\n{d}%',
            },
          },
          data: [...channelMap.entries()].map(([key, value]) => ({
            name: $t(`travel.payment_page.channel.${key}`),
            value: Number(value.toFixed(2)),
          })),
        },
      ],
    });
  }

  const trendEntries = [...trendMap.entries()]
    .sort((a, b) => a[0].localeCompare(b[0]))
    .slice(-14);

  if (trendEntries.length > 0) {
    renderTrendChart({
      grid: { bottom: 28, left: 64, right: 20, top: 24 },
      tooltip: {
        trigger: 'axis',
        valueFormatter: (value: any) => `¥${Number(value).toFixed(2)}`,
      },
      xAxis: {
        type: 'category',
        boundaryGap: false,
        data: trendEntries.map(([date]) => date.slice(5)),
      },
      yAxis: {
        type: 'value',
        axisLabel: { formatter: (value: any) => `¥${value}` },
      },
      series: [
        {
          name: $t('travel.payment_page.insight.trend_series'),
          type: 'line',
          smooth: true,
          symbolSize: 6,
          areaStyle: { opacity: 0.12 },
          lineStyle: { width: 2 },
          data: trendEntries.map(([, amount]) => Number(amount.toFixed(2))),
        },
      ],
    });
  }
}

async function fetchData() {
  loading.value = true;
  try {
    let res: any;
    if (activeTab.value === 'orders') {
      res = await getPayOrderPage({
        pageNum: pageNum.value,
        pageSize: pageSize.value,
        payNo: payNo.value || undefined,
        bizOrderNo: bizOrderNo.value || undefined,
        status: status.value || undefined,
        channelCode: channelCode.value || undefined,
      });
    } else if (activeTab.value === 'flows') {
      res = await getPayFlowPage({
        pageNum: pageNum.value,
        pageSize: pageSize.value,
        payNo: payNo.value || undefined,
      });
    } else if (activeTab.value === 'refunds') {
      res = await getRefundPage({
        pageNum: pageNum.value,
        pageSize: pageSize.value,
      });
    } else {
      res = await getPayNotifyLogPage({
        pageNum: pageNum.value,
        pageSize: pageSize.value,
        channelCode: channelCode.value || undefined,
      });
    }
    list.value = res?.records ?? [];
    total.value = res?.total ?? 0;
    if (activeTab.value === 'flows') {
      fetchFlowInsights();
    }
  } catch {
    // 统一错误提示
  } finally {
    loading.value = false;
  }
}

function switchTab(key: string | number) {
  activeTab.value = String(key);
  pageNum.value = 1;
  payNo.value = '';
  bizOrderNo.value = '';
  status.value = undefined;
  channelCode.value = undefined;
  list.value = [];
  total.value = 0;
  fetchData();
}

function handleTableChange(pagination: any) {
  pageNum.value = pagination.current;
  fetchData();
}

function resetFilters() {
  payNo.value = '';
  bizOrderNo.value = '';
  status.value = undefined;
  channelCode.value = undefined;
  pageNum.value = 1;
  fetchData();
}

function openRefund(record: any) {
  refundRecord.value = record;
  refundAmount.value = Number(record.payAmount ?? 0) - Number(record.refundedAmount ?? 0);
  refundReason.value = '';
  refundVisible.value = true;
}

async function submitRefund() {
  if (!refundRecord.value || !refundAmount.value || refundAmount.value <= 0) {
    message.warning($t('travel.payment_page.refund.amount_required'));
    return;
  }
  refundSubmitting.value = true;
  try {
    await refundPayOrder(refundRecord.value.payNo, {
      refundAmount: refundAmount.value,
      reason: refundReason.value,
    });
    message.success($t('travel.payment_page.refund.success'));
    refundVisible.value = false;
    fetchData();
  } finally {
    refundSubmitting.value = false;
  }
}

onMounted(fetchData);
</script>

<template>
  <Page :auto-content-height="true">
    <Card :title="$t('travel.payment_page.title')" :styles="{ body: { padding: '0' } }">
      <Tabs :active-key="activeTab" class="px-4 pt-2" @change="switchTab">
        <Tabs.TabPane v-for="tab in tabs" :key="tab.key" :tab="tab.label" />
      </Tabs>

      <div class="flex flex-wrap items-center gap-2 border-b p-4">
        <template v-if="activeTab === 'orders'">
          <Input
            v-model:value="payNo"
            :placeholder="$t('travel.payment_page.filters.payNo')"
            allow-clear
            class="w-52"
            @press-enter="fetchData"
          />
          <Input
            v-model:value="bizOrderNo"
            :placeholder="$t('travel.payment_page.filters.bizOrderNo')"
            allow-clear
            class="w-48"
            @press-enter="fetchData"
          />
          <Select
            v-model:value="status"
            :options="statusOptions"
            :placeholder="$t('travel.payment_page.filters.status')"
            allow-clear
            class="w-36"
          />
          <Select
            v-model:value="channelCode"
            :options="channelOptions"
            :placeholder="$t('travel.payment_page.filters.channel')"
            allow-clear
            class="w-36"
          />
        </template>
        <template v-else-if="activeTab === 'flows'">
          <Input
            v-model:value="payNo"
            :placeholder="$t('travel.payment_page.filters.payNo')"
            allow-clear
            class="w-64"
            @press-enter="fetchData"
          />
        </template>
        <template v-else-if="activeTab === 'logs'">
          <Select
            v-model:value="channelCode"
            :options="channelOptions"
            :placeholder="$t('travel.payment_page.filters.channel')"
            allow-clear
            class="w-40"
          />
        </template>
        <Button v-if="activeTab !== 'refunds'" type="primary" @click="fetchData">
          <template #icon><Search class="mr-1 size-4 align-middle" /></template>
          {{ $t('travel.payment_page.filters.search') }}
        </Button>
        <Button v-if="activeTab !== 'refunds'" @click="resetFilters">
          {{ $t('travel.payment_page.filters.reset') }}
        </Button>
        <Button :loading="loading" @click="fetchData">
          <template #icon><RefreshCw class="mr-1 size-4 align-middle" /></template>
          {{ $t('logistics.common.refresh') }}
        </Button>
      </div>

      <template v-if="activeTab === 'flows'">
        <div class="grid grid-cols-2 gap-3 border-b p-4 xl:grid-cols-4">
          <div
            v-for="card in flowCards"
            :key="card.key"
            class="rounded-lg border border-border bg-accent/40 px-4 py-3"
          >
            <div class="text-xs text-muted-foreground">{{ card.label }}</div>
            <div class="mt-1 text-xl font-semibold tabular-nums" :class="card.tone">
              {{ card.value }}
            </div>
          </div>
        </div>

        <div class="grid gap-3 border-b p-4 xl:grid-cols-2">
          <div class="rounded-lg border border-border p-4">
            <div class="mb-2 text-sm font-medium">
              {{ $t('travel.payment_page.insight.channel_title') }}
            </div>
            <div class="h-56 w-full">
              <EchartsUI
                v-if="flowInsights.length > 0"
                ref="channelChartRef"
                height="100%"
              />
              <div
                v-else
                class="flex h-full items-center justify-center text-sm text-muted-foreground"
              >
                {{ $t('travel.payment_page.insight.empty') }}
              </div>
            </div>
          </div>
          <div class="rounded-lg border border-border p-4">
            <div class="mb-2 text-sm font-medium">
              {{ $t('travel.payment_page.insight.trend_title') }}
            </div>
            <div class="h-56 w-full">
              <EchartsUI
                v-if="flowInsights.length > 0"
                ref="trendChartRef"
                height="100%"
              />
              <div
                v-else
                class="flex h-full items-center justify-center text-sm text-muted-foreground"
              >
                {{ $t('travel.payment_page.insight.empty') }}
              </div>
            </div>
          </div>
        </div>
      </template>

      <Table
        :columns="columns"
        :data-source="list"
        :loading="loading"
        :pagination="{ current: pageNum, pageSize, total, showSizeChanger: false }"
        :scroll="{ x: 1400 }"
        size="small"
        row-key="id"
        @change="handleTableChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'channel'">
            <span>{{ $t(`travel.payment_page.channel.${record.channelCode || record.channel || 'MOCK'}`) }}</span>
          </template>
          <template v-else-if="column.key === 'amount'">
            <span class="tabular-nums">¥{{ record.amount ?? '-' }}</span>
          </template>
          <template v-else-if="column.key === 'payAmount'">
            <span class="tabular-nums">¥{{ record.payAmount ?? '-' }}</span>
          </template>
          <template v-else-if="column.key === 'refundAmount'">
            <span class="tabular-nums">¥{{ record.amount ?? '-' }}</span>
          </template>
          <template v-else-if="column.key === 'status'">
            <Tag :color="statusColor(record.status)">
              {{ record.statusDesc || $t(`travel.payment_page.status.${record.status}`) }}
            </Tag>
          </template>
          <template v-else-if="column.key === 'refundStatus'">
            <Tag :color="refundStatusColor(record.status)">{{ record.statusDesc || '-' }}</Tag>
          </template>
          <template v-else-if="column.key === 'verifyResult'">
            <Tag :color="record.verifyResult === 1 ? 'success' : 'error'">
              {{ $t(`travel.payment_page.verify.${record.verifyResult ?? 0}`) }}
            </Tag>
          </template>
          <template v-else-if="column.key === 'processResult'">
            <Tag :color="record.processResult === 'SUCCESS' ? 'success' : record.processResult === 'IGNORED' ? 'warning' : 'error'">
              {{ record.processResult || '-' }}
            </Tag>
          </template>
          <template v-else-if="column.key === 'payTime'">
            <span class="text-xs text-muted-foreground">{{ formatTime(record.payTime) }}</span>
          </template>
          <template v-else-if="column.key === 'refundTime'">
            <span class="text-xs text-muted-foreground">{{ formatTime(record.refundTime) }}</span>
          </template>
          <template v-else-if="column.key === 'createdAt'">
            <span class="text-xs text-muted-foreground">{{ formatTime(record.createdAt) }}</span>
          </template>
          <template v-else-if="column.key === 'action'">
            <Button
              type="link"
              size="small"
              :disabled="!['SUCCESS', 'REFUNDING'].includes(record.status)"
              @click="openRefund(record)"
            >
              {{ $t('travel.payment_page.refund.action') }}
            </Button>
          </template>
        </template>
        <template #emptyText>
          <span class="text-sm text-muted-foreground">{{ $t('logistics.common.empty') }}</span>
        </template>
      </Table>
    </Card>

    <Modal
      v-model:open="refundVisible"
      :title="$t('travel.payment_page.refund.title')"
      :confirm-loading="refundSubmitting"
      @ok="submitRefund"
    >
      <div class="space-y-4 py-2">
        <div class="text-sm text-muted-foreground">
          {{ $t('travel.payment_page.fields.payNo') }}：{{ refundRecord?.payNo }}
        </div>
        <div class="flex items-center gap-2">
          <span class="w-24 text-right">{{ $t('travel.payment_page.refund.amount') }}</span>
          <InputNumber
            v-model:value="refundAmount"
            :min="0.01"
            :max="Number(refundRecord?.payAmount ?? 0) - Number(refundRecord?.refundedAmount ?? 0)"
            :precision="2"
            class="w-48"
          />
          <span class="text-xs text-muted-foreground">
            {{ $t('travel.payment_page.refund.max') }}：¥{{ (Number(refundRecord?.payAmount ?? 0) - Number(refundRecord?.refundedAmount ?? 0)).toFixed(2) }}
          </span>
        </div>
        <div class="flex items-start gap-2">
          <span class="w-24 pt-1 text-right">{{ $t('travel.payment_page.refund.reason') }}</span>
          <Input.TextArea v-model:value="refundReason" :rows="3" class="flex-1" />
        </div>
      </div>
    </Modal>
  </Page>
</template>
