<script lang="ts" setup>
import type { TableColumnsType } from 'antdv-next';

import { computed, onMounted, ref } from 'vue';

import { Page } from '@vben/common-ui';
import { Phone, RefreshCw, Search, Video } from '@vben/icons';
import { $t } from '@vben/locales';

import {
  Button,
  Empty,
  Input,
  message,
  Popconfirm,
  Select,
  Table,
  Tag,
} from 'antdv-next';

import {
  deleteCallRecord,
  getCallRecordPage,
} from '#/api/management/im/call';

defineOptions({ name: 'ImCallManagement' });

interface CallRow {
  id: number;
  callId?: null | string;
  callerId?: null | number;
  callerName?: null | string;
  receiverId?: null | number;
  receiverName?: null | string;
  callType?: null | string;
  status?: null | string;
  duration?: null | number;
  startTime?: null | string;
  endTime?: null | string;
  createdAt?: null | string;
}

const loading = ref(false);
const rows = ref<CallRow[]>([]);
const pageNum = ref(1);
const pageSize = ref(10);
const total = ref(0);
const keyword = ref('');
const callType = ref<null | string>(null);

const typeOptions = computed(() => [
  { value: 'voice', label: $t('im.call.type.voice') },
  { value: 'video', label: $t('im.call.type.video') },
]);

function formatTime(value?: null | string): string {
  if (!value) return '-';
  return String(value).replace('T', ' ').slice(0, 19);
}
function formatDuration(seconds?: null | number): string {
  const s = Number(seconds ?? 0);
  if (!s || s <= 0) return '0s';
  const m = Math.floor(s / 60);
  const rest = s % 60;
  return m > 0 ? `${m}m${rest}s` : `${rest}s`;
}
function typeText(type?: null | string): string {
  return typeOptions.value.find((o) => o.value === String(type))?.label ?? '-';
}
function statusText(status?: null | string): string {
  const key = String(status || '');
  const map: Record<string, string> = {
    initiated: $t('im.call.status.initiated'),
    accepted: $t('im.call.status.accepted'),
    rejected: $t('im.call.status.rejected'),
    ended: $t('im.call.status.ended'),
    canceled: $t('im.call.status.canceled'),
    missed: $t('im.call.status.missed'),
  };
  return map[key] ?? (status || '-');
}
function statusColor(status?: null | string): string {
  const map: Record<string, string> = {
    initiated: 'blue',
    accepted: 'green',
    rejected: 'red',
    ended: 'default',
    canceled: 'default',
    missed: 'orange',
  };
  return map[String(status || '')] ?? 'default';
}

const columns = computed<TableColumnsType>(() => [
  { title: 'ID', dataIndex: 'id', width: 70 },
  { title: $t('im.call.columns.callId'), dataIndex: 'callId', width: 200, ellipsis: true },
  { title: $t('im.call.columns.caller'), key: 'caller', width: 150 },
  { title: $t('im.call.columns.receiver'), key: 'receiver', width: 150 },
  { title: $t('im.call.columns.callType'), key: 'callType', width: 100 },
  { title: $t('im.call.columns.status'), key: 'status', width: 100 },
  { title: $t('im.call.columns.duration'), key: 'duration', width: 100 },
  { title: $t('im.call.columns.startTime'), key: 'startTime', width: 170 },
  { title: $t('im.call.columns.action'), key: 'action', width: 100, fixed: 'right' },
]);

async function fetchData() {
  loading.value = true;
  try {
    const res: any = await getCallRecordPage({
      page: pageNum.value,
      size: pageSize.value,
      keyword: keyword.value.trim() || undefined,
      callType: callType.value ?? undefined,
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
  callType.value = null;
  pageNum.value = 1;
  fetchData();
}
function handlePageChange(page: number, size: number) {
  pageNum.value = page;
  pageSize.value = size;
  fetchData();
}

async function handleDelete(row: CallRow) {
  try {
    await deleteCallRecord(row.id);
    message.success($t('im.call.delete_success'));
    await fetchData();
  } catch (e: any) {
    message.error(e?.message || $t('im.call.failed'));
  }
}

onMounted(fetchData);
</script>

<template>
  <Page :title="$t('im.call.title')" :description="$t('im.call.description')">
    <div class="bg-card rounded-lg p-4">
      <div class="mb-4 flex flex-wrap items-center gap-2">
        <Input
          v-model:value="keyword"
          :placeholder="$t('im.call.search_placeholder')"
          class="w-52"
          allow-clear
          @press-enter="handleSearch"
        />
        <Select
          v-model:value="callType"
          :placeholder="$t('im.call.filter.callType')"
          allow-clear
          class="w-32"
          :options="typeOptions"
          @change="handleSearch"
        />
        <Button type="primary" @click="handleSearch">
          <template #icon><Search class="size-4" /></template>
          {{ $t('im.call.search') }}
        </Button>
        <div class="flex-1"></div>
        <Button @click="handleReset">
          <template #icon><RefreshCw class="size-4" /></template>
          {{ $t('im.call.reset') }}
        </Button>
      </div>

      <Table
        :columns="columns"
        :data-source="rows"
        :loading="loading"
        :pagination="{
          current: pageNum,
          pageSize,
          total,
          showSizeChanger: true,
          showTotal: (t: number) => $t('im.call.total', { total: t }),
          onChange: handlePageChange,
        }"
        :scroll="{ x: 1200 }"
        row-key="id"
        size="middle"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'caller'">
            {{ record.callerName || `#${record.callerId}` }}
          </template>
          <template v-else-if="column.key === 'receiver'">
            {{ record.receiverName || `#${record.receiverId}` }}
          </template>
          <template v-else-if="column.key === 'callType'">
            <Tag :color="record.callType === 'video' ? 'purple' : 'blue'">
              <component
                :is="record.callType === 'video' ? Video : Phone"
                class="mr-1 inline size-3 align-middle"
              />
              {{ typeText(record.callType) }}
            </Tag>
          </template>
          <template v-else-if="column.key === 'status'">
            <Tag :color="statusColor(record.status)">{{ statusText(record.status) }}</Tag>
          </template>
          <template v-else-if="column.key === 'duration'">
            {{ formatDuration(record.duration) }}
          </template>
          <template v-else-if="column.key === 'startTime'">
            {{ formatTime(record.startTime) }}
          </template>
          <template v-else-if="column.key === 'action'">
            <Popconfirm :title="$t('im.call.delete_confirm')" @confirm="handleDelete(record)">
              <Button type="link" size="small" danger>
                {{ $t('im.call.delete') }}
              </Button>
            </Popconfirm>
          </template>
        </template>
        <template #emptyText>
          <Empty :description="$t('im.call.empty')" />
        </template>
      </Table>
    </div>
  </Page>
</template>
