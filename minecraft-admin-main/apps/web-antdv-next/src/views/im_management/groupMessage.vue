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
  Table,
  Tag,
} from 'antdv-next';

import { getGroupMessagePage } from '#/api/management/im/message';

defineOptions({ name: 'ImGroupMessageManagement' });

interface GroupMessageRow {
  id: number;
  groupId?: null | number;
  groupName?: null | string;
  senderId?: null | number;
  senderName?: null | string;
  senderAvatar?: null | string;
  content?: null | string;
  messageType?: null | string;
  createTime?: null | string;
}

const loading = ref(false);
const rows = ref<GroupMessageRow[]>([]);
const pageNum = ref(1);
const pageSize = ref(10);
const total = ref(0);

const keyword = ref('');
const groupId = ref<null | number>(null);

function formatTime(value?: null | string): string {
  if (!value) return '-';
  return String(value).replace('T', ' ').slice(0, 19);
}
function typeText(type?: null | string): string {
  const key = String(type || '').toLowerCase();
  const map: Record<string, string> = {
    text: $t('im.message.type.text'),
    image: $t('im.message.type.image'),
    emoji: $t('im.message.type.emoji'),
    video: $t('im.message.type.video'),
    audio: $t('im.message.type.audio'),
    file: $t('im.message.type.file'),
  };
  return map[key] ?? (type || '-');
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
  { title: $t('im.message.columns.group'), key: 'group', width: 160 },
  { title: $t('im.message.columns.sender'), key: 'sender', width: 180 },
  { title: $t('im.message.columns.content'), key: 'content', width: 300 },
  { title: $t('im.message.columns.type'), key: 'type', width: 90 },
  { title: $t('im.message.columns.time'), key: 'createTime', width: 170 },
]);

async function fetchData() {
  loading.value = true;
  try {
    const res: any = await getGroupMessagePage({
      page: pageNum.value,
      size: pageSize.value,
      keyword: keyword.value.trim() || undefined,
      groupId: groupId.value ?? undefined,
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
  groupId.value = null;
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
  <Page :title="$t('im.groupMessage.title')" :description="$t('im.groupMessage.description')">
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
          v-model:value="groupId"
          :placeholder="$t('im.groupMessage.filter.groupId')"
          class="w-36"
          :min="1"
          :precision="0"
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
        :scroll="{ x: 1000 }"
        row-key="id"
        size="middle"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'group'">
            {{ record.groupName || `#${record.groupId}` }}
          </template>
          <template v-else-if="column.key === 'sender'">
            <div class="flex items-center gap-2">
              <AntAvatar :src="record.senderAvatar || undefined" :size="28">
                {{ (record.senderName || '?').slice(0, 1) }}
              </AntAvatar>
              <span class="truncate">{{ record.senderName || `#${record.senderId}` }}</span>
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
