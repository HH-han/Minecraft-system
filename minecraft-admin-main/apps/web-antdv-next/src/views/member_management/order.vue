<script lang="ts" setup>
import type { TableColumnsType } from 'antdv-next';

import { computed, onMounted, ref } from 'vue';

import { Page } from '@vben/common-ui';
import { RefreshCw, Search } from '@vben/icons';
import { $t } from '@vben/locales';

import {
  Button,
  Card,
  Empty,
  Input,
  message,
  Popconfirm,
  Select,
  Space,
  Table,
  Tag,
} from 'antdv-next';
import dayjs from 'dayjs';

import {
  getMemberOrderPage,
  refundMemberOrder,
} from '#/api/management/member/member';

defineOptions({ name: 'MemberOrder' });

interface OrderItem {
  id: number | string;
  orderNo: string;
  userId: number | string;
  account?: string;
  nickname?: string;
  packageName: string;
  amount: number;
  payType?: number;
  status: number;
  payTime?: string;
  startTime?: string;
  endTime?: string;
  createTime?: string;
}

const loading = ref(false);
const rows = ref<OrderItem[]>([]);
const keyword = ref('');
const statusFilter = ref<number | null>(null);
const pageNum = ref(1);
const pageSize = ref(10);
const total = ref(0);

const statusOptions = computed(() => [
  { label: $t('member.order_page.statusPending'), value: 0 },
  { label: $t('member.order_page.statusPaid'), value: 1 },
  { label: $t('member.order_page.statusCancelled'), value: 2 },
  { label: $t('member.order_page.statusRefunded'), value: 3 },
]);

function statusText(status: number) {
  return (
    statusOptions.value.find((item) => item.value === status)?.label ??
    String(status)
  );
}

function statusColor(status: number) {
  switch (status) {
    case 1: {
      return 'green';
    }
    case 2: {
      return 'default';
    }
    case 3: {
      return 'red';
    }
    default: {
      return 'orange';
    }
  }
}

function payTypeText(payType?: number) {
  if (payType === 1) return $t('member.order_page.payAlipay');
  if (payType === 2) return $t('member.order_page.payWechat');
  if (payType === 3) return $t('member.order_page.payBalance');
  return '-';
}

function formatTime(value?: string) {
  return value ? dayjs(value).format('YYYY-MM-DD HH:mm:ss') : '-';
}

const columns = computed<TableColumnsType>(() => [
  { title: $t('member.order_page.orderNo'), dataIndex: 'orderNo', width: 210 },
  { title: $t('member.common.user'), key: 'user', width: 160 },
  { title: $t('member.order_page.package'), dataIndex: 'packageName', width: 130 },
  { title: $t('member.common.amount'), dataIndex: 'amount', width: 100 },
  { title: $t('member.order_page.payType'), key: 'payType', width: 100 },
  { title: $t('member.common.status'), key: 'status', width: 100 },
  { title: $t('member.order_page.payTime'), key: 'payTime', width: 170 },
  { title: $t('member.order_page.startTime'), key: 'startTime', width: 170 },
  { title: $t('member.order_page.endTime'), key: 'endTime', width: 170 },
  { title: $t('member.order_page.createTime'), key: 'createTime', width: 170 },
  {
    title: $t('member.common.operation'),
    key: 'actions',
    width: 100,
    fixed: 'right',
  },
]);

async function fetchData() {
  loading.value = true;
  try {
    const res: any = await getMemberOrderPage({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      keyword: keyword.value.trim() || undefined,
      status: statusFilter.value ?? undefined,
    });
    rows.value = res?.records ?? [];
    total.value = Number(res?.total ?? 0);
  } catch {
    rows.value = [];
    total.value = 0;
  } finally {
    loading.value = false;
  }
}

function handleSearch() {
  pageNum.value = 1;
  fetchData();
}

function handleReset() {
  keyword.value = '';
  statusFilter.value = null;
  pageNum.value = 1;
  fetchData();
}

function handlePageChange(page: number, size: number) {
  pageNum.value = page;
  pageSize.value = size;
  fetchData();
}

async function handleRefund(record: OrderItem) {
  try {
    await refundMemberOrder(record.id);
    message.success($t('member.order_page.refundSuccess'));
    await fetchData();
  } catch (error: any) {
    message.error(error?.message || $t('member.common.saveFailed'));
  }
}

onMounted(fetchData);
</script>

<template>
  <Page
    :title="$t('member.order_page.title')"
    :description="$t('member.order_page.description')"
  >
    <Card :styles="{ body: { padding: '16px' } }">
      <div class="mb-4 flex flex-wrap items-center gap-2">
        <Input
          v-model:value="keyword"
          :placeholder="$t('member.order_page.searchPlaceholder')"
          class="w-60"
          allow-clear
          @press-enter="handleSearch"
        />
        <Select
          v-model:value="statusFilter"
          :placeholder="$t('member.common.status')"
          :options="statusOptions"
          allow-clear
          class="w-36"
          @change="handleSearch"
        />
        <Button type="primary" @click="handleSearch">
          <template #icon><Search class="size-4" /></template>
          {{ $t('member.common.search') }}
        </Button>
        <Button @click="handleReset">
          <template #icon><RefreshCw class="size-4" /></template>
          {{ $t('member.common.reset') }}
        </Button>
      </div>

      <Table
        :columns="columns"
        :data-source="rows"
        :loading="loading"
        :scroll="{ x: 1650 }"
        row-key="id"
        size="middle"
        :bordered="true"
        :pagination="{
          current: pageNum,
          pageSize,
          total,
          showSizeChanger: true,
          pageSizeOptions: ['5', '10', '20', '50'],
          showTotal: (value: number) =>
            $t('member.common.total', { total: value }),
          onChange: handlePageChange,
        }"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'user'">
            <div class="flex flex-col">
              <span>{{ record.nickname || record.account || '-' }}</span>
              <span class="text-xs text-gray-400">
                ID: {{ record.userId }}
              </span>
            </div>
          </template>
          <template v-else-if="column.key === 'amount'">
            ¥{{ record.amount }}
          </template>
          <template v-else-if="column.key === 'payType'">
            {{ payTypeText(record.payType) }}
          </template>
          <template v-else-if="column.key === 'status'">
            <Tag :color="statusColor(record.status)">
              {{ statusText(record.status) }}
            </Tag>
          </template>
          <template v-else-if="column.key === 'payTime'">
            {{ formatTime(record.payTime) }}
          </template>
          <template v-else-if="column.key === 'startTime'">
            {{ formatTime(record.startTime) }}
          </template>
          <template v-else-if="column.key === 'endTime'">
            {{ formatTime(record.endTime) }}
          </template>
          <template v-else-if="column.key === 'createTime'">
            {{ formatTime(record.createTime) }}
          </template>
          <template v-else-if="column.key === 'actions'">
            <Popconfirm
              v-if="record.status === 1"
              :title="$t('member.order_page.confirmRefund')"
              :ok-text="$t('member.common.yes')"
              :cancel-text="$t('member.common.cancel')"
              :ok-button-props="{ danger: true }"
              @confirm="handleRefund(record)"
            >
              <Button type="link" size="small" danger>
                {{ $t('member.order_page.refund') }}
              </Button>
            </Popconfirm>
            <Space v-else :size="4">-</Space>
          </template>
        </template>
        <template #emptyText>
          <Empty :description="$t('member.common.empty')" />
        </template>
      </Table>
    </Card>
  </Page>
</template>
