<script lang="ts" setup>
import type { TableColumnsType } from 'antdv-next';

import { computed, onMounted, ref } from 'vue';

import { Page } from '@vben/common-ui';
import { RefreshCw, Search } from '@vben/icons';
import { $t } from '@vben/locales';

import { Button, Card, Input, Table, Tag, message } from 'antdv-next';

import {
  getRefundPage,
  notifyRefund,
} from '#/api/management/logistics/refund';

defineOptions({ name: 'LogisticsRefund' });

const loading = ref(false);
const list = ref<any[]>([]);
const total = ref(0);
const pageNum = ref(1);
const pageSize = ref(10);
const keyword = ref('');

const columns = computed<TableColumnsType>(() => [
  { title: $t('logistics.refund_page.fields.refundNo'), dataIndex: 'refundNo', width: 190 },
  { title: $t('logistics.refund_page.fields.orderNo'), dataIndex: 'orderNo', width: 140 },
  { title: $t('logistics.refund_page.fields.channel'), dataIndex: 'channel', width: 100 },
  { title: $t('logistics.refund_page.fields.amount'), key: 'amount', width: 110 },
  { title: $t('logistics.refund_page.fields.status'), key: 'status', width: 100 },
  { title: $t('logistics.refund_page.fields.channelRefundNo'), dataIndex: 'channelRefundNo', width: 160 },
  { title: $t('logistics.refund_page.fields.failReason'), dataIndex: 'failReason', ellipsis: true },
  { title: $t('logistics.refund_page.fields.refundTime'), key: 'refundTime', width: 170 },
  { title: $t('logistics.common.action'), key: 'action', width: 180, fixed: 'right' },
]);

const filteredList = computed(() => {
  const kw = keyword.value.trim().toLowerCase();
  if (!kw) return list.value;
  return list.value.filter((r) =>
    [r.refundNo, r.orderNo, r.channelRefundNo]
      .filter(Boolean)
      .some((v: string) => String(v).toLowerCase().includes(kw)),
  );
});

function formatTime(v?: string) {
  return v ? String(v).replace('T', ' ') : '-';
}

function statusColor(status?: number) {
  if (status === 2) return 'success';
  if (status === 3) return 'error';
  if (status === 1) return 'warning';
  return 'default';
}

async function fetchList() {
  loading.value = true;
  try {
    const res: any = await getRefundPage({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
    });
    list.value = res?.records ?? [];
    total.value = res?.total ?? 0;
  } catch {
    // 统一错误提示
  } finally {
    loading.value = false;
  }
}

function handleTableChange(pagination: any) {
  pageNum.value = pagination.current;
  fetchList();
}

async function notify(record: any, success: boolean) {
  await notifyRefund(record.id, { success });
  message.success($t('logistics.common.save_success'));
  fetchList();
}

onMounted(fetchList);
</script>

<template>
  <Page :auto-content-height="true">
    <Card :title="$t('logistics.refund_page.title')" :styles="{ body: { padding: '0' } }">
      <template #extra>
        <div class="flex items-center gap-2">
          <Input
            v-model:value="keyword"
            :placeholder="$t('logistics.common.search_placeholder')"
            allow-clear
            class="w-56"
          >
            <template #prefix><Search class="size-4 text-muted-foreground" /></template>
          </Input>
          <Button :loading="loading" @click="fetchList">
            <template #icon><RefreshCw class="mr-1 size-4 align-middle" /></template>
            {{ $t('logistics.common.refresh') }}
          </Button>
        </div>
      </template>

      <Table
        :columns="columns"
        :data-source="filteredList"
        :loading="loading"
        :pagination="{ current: pageNum, pageSize, total, showSizeChanger: false }"
        :scroll="{ x: 1280 }"
        size="small"
        row-key="id"
        @change="handleTableChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'amount'">
            <span class="tabular-nums">¥{{ record.amount ?? '-' }}</span>
          </template>
          <template v-else-if="column.key === 'status'">
            <Tag :color="statusColor(record.status)">{{ record.statusDesc || '-' }}</Tag>
          </template>
          <template v-else-if="column.key === 'refundTime'">
            <span class="text-xs text-muted-foreground">{{ formatTime(record.refundTime) }}</span>
          </template>
          <template v-else-if="column.key === 'action'">
            <div class="flex flex-nowrap items-center whitespace-nowrap">
              <Button type="link" size="small" @click="notify(record, true)">
                {{ $t('logistics.refund_page.notify_success') }}
              </Button>
              <Button type="link" size="small" danger @click="notify(record, false)">
                {{ $t('logistics.refund_page.notify_fail') }}
              </Button>
            </div>
          </template>
        </template>
        <template #emptyText>
          <span class="text-sm text-muted-foreground">{{ $t('logistics.common.empty') }}</span>
        </template>
      </Table>
    </Card>
  </Page>
</template>
