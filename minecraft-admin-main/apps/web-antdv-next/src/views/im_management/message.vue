<script lang="ts" setup>
import type { TableColumnsType } from 'antdv-next';

import { computed, onMounted, ref } from 'vue';

import { Page } from '@vben/common-ui';
import { RefreshCw, Search } from '@vben/icons';
import { $t } from '@vben/locales';

import {
  Avatar as AntAvatar,
  Button,
  Empty,
  Image,
  Input,
  InputNumber,
  Select,
  Table,
  Tag,
} from 'antdv-next';

import { getSingleMessagePage } from '#/api/management/im/message';

defineOptions({ name: 'ImMessageManagement' });

interface MessageRow {
  id: number;
  senderId?: null | number;
  senderName?: null | string;
  senderAvatar?: null | string;
  receiverId?: null | number;
  receiverName?: null | string;
  receiverAvatar?: null | string;
  content?: null | string;
  messageType?: null | string;
  isRead?: null | number;
  createTime?: null | string;
}

const loading = ref(false);
const rows = ref<MessageRow[]>([]);
const pageNum = ref(1);
const pageSize = ref(10);
const total = ref(0);

const keyword = ref('');
const userId = ref<null | number>(null);
const messageType = ref<null | string>(null);

const typeOptions = computed(() => [
  { value: 'text', label: $t('im.message.type.text') },
  { value: 'image', label: $t('im.message.type.image') },
  { value: 'emoji', label: $t('im.message.type.emoji') },
  { value: 'video', label: $t('im.message.type.video') },
  { value: 'audio', label: $t('im.message.type.audio') },
  { value: 'file', label: $t('im.message.type.file') },
]);

function formatTime(value?: null | string): string {
  if (!value) return '-';
  return String(value).replace('T', ' ').slice(0, 19);
}
function typeText(type?: null | string): string {
  const key = String(type || '').toLowerCase();
  return typeOptions.value.find((o) => o.value === key)?.label ?? (type || '-');
}
function isImage(type?: null | string, content?: null | string): boolean {
  const t = String(type || '').toLowerCase();
  const c = String(content || '');
  if (t === 'image') return true;
  if (t === 'emoji' && /^https?:\/\//i.test(c)) return true;
  return /\.(png|jpe?g|gif|webp|bmp)(\?.*)?$/i.test(c);
}

const columns = computed<TableColumnsType>(() => [
  { title: 'ID', dataIndex: 'id', width: 70 },
  { title: $t('im.message.columns.sender'), key: 'sender', width: 180 },
  { title: $t('im.message.columns.receiver'), key: 'receiver', width: 180 },
  { title: $t('im.message.columns.content'), key: 'content', width: 280 },
  { title: $t('im.message.columns.type'), key: 'type', width: 90 },
  { title: $t('im.message.columns.isRead'), key: 'isRead', width: 90 },
  { title: $t('im.message.columns.time'), key: 'createTime', width: 170 },
]);

async function fetchData() {
  loading.value = true;
  try {
    const res: any = await getSingleMessagePage({
      page: pageNum.value,
      size: pageSize.value,
      keyword: keyword.value.trim() || undefined,
      userId: userId.value ?? undefined,
      messageType: messageType.value ?? undefined,
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
  userId.value = null;
  messageType.value = null;
  pageNum.value = 1;
  fetchData();
}
function handlePageChange(page: number, size: number) {
  pageNum.value = page;
  pageSize.value = size;
  fetchData();
}

onMounted(fetchData);
</script>

<template>
  <Page :title="$t('im.message.title')" :description="$t('im.message.description')">
    <div class="bg-card rounded-lg p-4">
      <div class="mb-4 flex flex-wrap items-center gap-2">
        <Input
          v-model:value="keyword"
          :placeholder="$t('im.message.search_placeholder')"
          class="w-52"
          allow-clear
          @press-enter="handleSearch"
        />
        <InputNumber
          v-model:value="userId"
          :placeholder="$t('im.message.filter.userId')"
          class="w-36"
          :min="1"
          :precision="0"
        />
        <Select
          v-model:value="messageType"
          :placeholder="$t('im.message.filter.messageType')"
          allow-clear
          class="w-32"
          :options="typeOptions"
        />
        <Button type="primary" @click="handleSearch">
          <template #icon><Search class="size-4" /></template>
          {{ $t('im.message.search') }}
        </Button>
        <div class="flex-1"></div>
        <Button @click="handleReset">
          <template #icon><RefreshCw class="size-4" /></template>
          {{ $t('im.message.reset') }}
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
          showTotal: (t: number) => $t('im.message.total', { total: t }),
          onChange: handlePageChange,
        }"
        :scroll="{ x: 1100 }"
        row-key="id"
        size="middle"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'sender'">
            <div class="flex items-center gap-2">
              <AntAvatar :src="record.senderAvatar || undefined" :size="28">
                {{ (record.senderName || '?').slice(0, 1) }}
              </AntAvatar>
              <span class="truncate">{{ record.senderName || `#${record.senderId}` }}</span>
            </div>
          </template>
          <template v-else-if="column.key === 'receiver'">
            <div class="flex items-center gap-2">
              <AntAvatar :src="record.receiverAvatar || undefined" :size="28">
                {{ (record.receiverName || '?').slice(0, 1) }}
              </AntAvatar>
              <span class="truncate">{{ record.receiverName || `#${record.receiverId}` }}</span>
            </div>
          </template>
          <template v-else-if="column.key === 'content'">
            <Image
              v-if="isImage(record.messageType, record.content)"
              :src="record.content"
              :width="48"
              :height="48"
              class="rounded object-cover"
            />
            <span v-else class="line-clamp-2 break-all">{{ record.content }}</span>
          </template>
          <template v-else-if="column.key === 'type'">
            <Tag>{{ typeText(record.messageType) }}</Tag>
          </template>
          <template v-else-if="column.key === 'isRead'">
            <Tag :color="Number(record.isRead) === 1 ? 'green' : 'orange'">
              {{ Number(record.isRead) === 1 ? $t('im.message.read.yes') : $t('im.message.read.no') }}
            </Tag>
          </template>
          <template v-else-if="column.key === 'createTime'">
            {{ formatTime(record.createTime) }}
          </template>
        </template>
        <template #emptyText>
          <Empty :description="$t('im.message.empty')" />
        </template>
      </Table>
    </div>
  </Page>
</template>
