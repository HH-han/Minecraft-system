<script lang="ts" setup>
import type { TableColumnsType } from 'antdv-next';

import { computed, onMounted, ref } from 'vue';

import { Page } from '@vben/common-ui';
import { RefreshCw, Search } from '@vben/icons';
import { $t } from '@vben/locales';

import {
  Avatar,
  Button,
  Empty,
  Input,
  message,
  Popconfirm,
  Select,
  Space,
  Table,
  Tag,
} from 'antdv-next';

import {
  approveFriendRequest,
  deleteFriendRelation,
  getFriendPage,
  rejectFriendRequest,
} from '#/api/management/im/friend';

defineOptions({ name: 'ImFriendManagement' });

interface FriendRow {
  id: number;
  userId?: null | number;
  userUsername?: null | string;
  userNickname?: null | string;
  userAvatar?: null | string;
  friendId?: null | number;
  friendUsername?: null | string;
  friendNickname?: null | string;
  friendAvatar?: null | string;
  remark?: null | string;
  status?: null | number;
  createTime?: null | string;
}

const loading = ref(false);
const rows = ref<FriendRow[]>([]);
const pageNum = ref(1);
const pageSize = ref(10);
const total = ref(0);
const keyword = ref('');
const statusFilter = ref<null | number>(null);

const statusOptions = computed(() => [
  { value: 0, label: $t('im.friend.status.pending') },
  { value: 1, label: $t('im.friend.status.accepted') },
]);

function statusText(s?: null | number): string {
  return statusOptions.value.find((o) => o.value === Number(s))?.label ?? '-';
}
function statusColor(s?: null | number): string {
  return Number(s) === 1 ? 'green' : 'orange';
}
function formatTime(value?: null | string): string {
  if (!value) return '-';
  return String(value).replace('T', ' ').slice(0, 19);
}
function displayName(name?: null | string, username?: null | string): string {
  return name || username || '-';
}

const columns = computed<TableColumnsType>(() => [
  { title: 'ID', dataIndex: 'id', width: 70 },
  { title: $t('im.friend.columns.user'), key: 'user', width: 220 },
  { title: $t('im.friend.columns.friend'), key: 'friend', width: 220 },
  { title: $t('im.friend.columns.remark'), dataIndex: 'remark', width: 140, ellipsis: true },
  { title: $t('im.friend.columns.status'), key: 'status', width: 100 },
  { title: $t('im.friend.columns.createTime'), key: 'createTime', width: 170 },
  { title: $t('im.friend.columns.action'), key: 'action', width: 220, fixed: 'right' },
]);

async function fetchData() {
  loading.value = true;
  try {
    const res: any = await getFriendPage({
      page: pageNum.value,
      size: pageSize.value,
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

async function handleApprove(row: any) {
  try {
    await approveFriendRequest(row.id);
    message.success($t('im.friend.action.approve_success'));
    await fetchData();
  } catch (e: any) {
    message.error(e?.message || $t('im.friend.action.failed'));
  }
}
async function handleReject(row: any) {
  try {
    await rejectFriendRequest(row.id);
    message.success($t('im.friend.action.reject_success'));
    await fetchData();
  } catch (e: any) {
    message.error(e?.message || $t('im.friend.action.failed'));
  }
}
async function handleDelete(row: any) {
  try {
    await deleteFriendRelation(row.id);
    message.success($t('im.friend.action.delete_success'));
    await fetchData();
  } catch (e: any) {
    message.error(e?.message || $t('im.friend.action.delete_failed'));
  }
}

onMounted(fetchData);
</script>

<template>
  <Page :title="$t('im.friend.title')" :description="$t('im.friend.description')">
    <div class="bg-card rounded-lg p-4">
      <div class="mb-4 flex flex-wrap items-center gap-2">
        <Input
          v-model:value="keyword"
          :placeholder="$t('im.friend.search_placeholder')"
          class="w-52"
          allow-clear
          @press-enter="handleSearch"
        />
        <Button type="primary" @click="handleSearch">
          <template #icon><Search class="size-4" /></template>
          {{ $t('im.friend.search') }}
        </Button>
        <Select
          v-model:value="statusFilter"
          :placeholder="$t('im.friend.filter.status')"
          allow-clear
          class="w-32"
          :options="statusOptions"
          @change="handleSearch"
        />
        <div class="flex-1"></div>
        <Button @click="handleReset">
          <template #icon><RefreshCw class="size-4" /></template>
          {{ $t('im.friend.reset') }}
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
          showTotal: (t: number) => $t('im.friend.total', { total: t }),
          onChange: handlePageChange,
        }"
        :scroll="{ x: 1100 }"
        row-key="id"
        size="middle"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'user'">
            <div class="flex items-center gap-2">
              <Avatar :src="record.userAvatar || undefined" :size="32">
                {{ (record.userUsername || '?').slice(0, 1) }}
              </Avatar>
              <div class="min-w-0">
                <div class="truncate">
                  {{ displayName(record.userNickname, record.userUsername) }}
                </div>
                <div class="text-xs text-muted-foreground">ID: {{ record.userId }}</div>
              </div>
            </div>
          </template>
          <template v-else-if="column.key === 'friend'">
            <div class="flex items-center gap-2">
              <Avatar :src="record.friendAvatar || undefined" :size="32">
                {{ (record.friendUsername || '?').slice(0, 1) }}
              </Avatar>
              <div class="min-w-0">
                <div class="truncate">
                  {{ displayName(record.friendNickname, record.friendUsername) }}
                </div>
                <div class="text-xs text-muted-foreground">ID: {{ record.friendId }}</div>
              </div>
            </div>
          </template>
          <template v-else-if="column.key === 'status'">
            <Tag :color="statusColor(record.status)">{{ statusText(record.status) }}</Tag>
          </template>
          <template v-else-if="column.key === 'createTime'">
            {{ formatTime(record.createTime) }}
          </template>
          <template v-else-if="column.key === 'action'">
            <Space :size="4" wrap>
              <template v-if="Number(record.status) === 0">
                <Button type="link" size="small" @click="handleApprove(record)">
                  {{ $t('im.friend.action.approve') }}
                </Button>
                <Button type="link" size="small" @click="handleReject(record)">
                  {{ $t('im.friend.action.reject') }}
                </Button>
              </template>
              <Popconfirm
                :title="$t('im.friend.action.delete_confirm')"
                @confirm="handleDelete(record)"
              >
                <Button type="link" size="small" danger>
                  {{ $t('im.friend.action.delete') }}
                </Button>
              </Popconfirm>
            </Space>
          </template>
        </template>
        <template #emptyText>
          <Empty :description="$t('im.friend.empty')" />
        </template>
      </Table>
    </div>
  </Page>
</template>
