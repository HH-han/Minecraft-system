<script lang="ts" setup>
import type { TableColumnsType } from 'antdv-next';

import { computed, onMounted, ref } from 'vue';

import { Page } from '@vben/common-ui';
import { RefreshCw, Search } from '@vben/icons';
import { $t } from '@vben/locales';

import {
  Button,
  Empty,
  Image,
  Input,
  InputNumber,
  message,
  Popconfirm,
  Select,
  Table,
  Tag,
} from 'antdv-next';

import {
  deleteFavorite,
  getFavoritePage,
} from '#/api/management/im/favorite';

defineOptions({ name: 'ImFavoriteManagement' });

interface FavoriteRow {
  id: number;
  userId?: null | number;
  type?: null | string;
  title?: null | string;
  content?: null | string;
  cover?: null | string;
  url?: null | string;
  fileName?: null | string;
  fileSize?: null | number;
  tags?: null | string[];
  source?: null | string;
  createTime?: null | string;
  updateTime?: null | string;
}

const loading = ref(false);
const rows = ref<FavoriteRow[]>([]);
const pageNum = ref(1);
const pageSize = ref(10);
const total = ref(0);

const userId = ref<null | number>(null);
const type = ref<null | string>(null);
const tag = ref('');
const keyword = ref('');

const typeOptions = computed(() => [
  { value: 'note', label: $t('im.favorite.type.note') },
  { value: 'link', label: $t('im.favorite.type.link') },
  { value: 'image', label: $t('im.favorite.type.image') },
  { value: 'video', label: $t('im.favorite.type.video') },
  { value: 'file', label: $t('im.favorite.type.file') },
  { value: 'chat', label: $t('im.favorite.type.chat') },
  { value: 'voice', label: $t('im.favorite.type.voice') },
  { value: 'location', label: $t('im.favorite.type.location') },
  { value: 'miniapp', label: $t('im.favorite.type.miniapp') },
]);

function formatTime(value?: null | string): string {
  if (!value) return '-';
  return String(value).replace('T', ' ').slice(0, 19);
}
function typeText(t?: null | string): string {
  return typeOptions.value.find((o) => o.value === String(t))?.label ?? (t || '-');
}
function isCoverImage(url?: null | string): boolean {
  return /\.(png|jpe?g|gif|webp|bmp)(\?.*)?$/i.test(String(url || ''));
}

const columns = computed<TableColumnsType>(() => [
  { title: 'ID', dataIndex: 'id', width: 70 },
  { title: $t('im.favorite.columns.userId'), dataIndex: 'userId', width: 90 },
  { title: $t('im.favorite.columns.type'), key: 'type', width: 100 },
  { title: $t('im.favorite.columns.title'), dataIndex: 'title', width: 180, ellipsis: true },
  { title: $t('im.favorite.columns.content'), key: 'content', width: 220 },
  { title: $t('im.favorite.columns.cover'), key: 'cover', width: 80 },
  { title: $t('im.favorite.columns.tags'), key: 'tags', width: 160 },
  { title: $t('im.favorite.columns.source'), dataIndex: 'source', width: 120, ellipsis: true },
  { title: $t('im.favorite.columns.updateTime'), key: 'updateTime', width: 170 },
  { title: $t('im.favorite.columns.action'), key: 'action', width: 100, fixed: 'right' },
]);

async function fetchData() {
  loading.value = true;
  try {
    const res: any = await getFavoritePage({
      page: pageNum.value,
      size: pageSize.value,
      userId: userId.value ?? undefined,
      type: type.value ?? undefined,
      tag: tag.value.trim() || undefined,
      keyword: keyword.value.trim() || undefined,
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
  userId.value = null;
  type.value = null;
  tag.value = '';
  keyword.value = '';
  pageNum.value = 1;
  fetchData();
}
function handlePageChange(page: number, size: number) {
  pageNum.value = page;
  pageSize.value = size;
  fetchData();
}

async function handleDelete(row: any) {
  try {
    await deleteFavorite(row.id);
    message.success($t('im.favorite.delete_success'));
    await fetchData();
  } catch (e: any) {
    message.error(e?.message || $t('im.favorite.failed'));
  }
}

onMounted(fetchData);
</script>

<template>
  <Page :title="$t('im.favorite.title')" :description="$t('im.favorite.description')">
    <div class="bg-card rounded-lg p-4">
      <div class="mb-4 flex flex-wrap items-center gap-2">
        <Input
          v-model:value="keyword"
          :placeholder="$t('im.favorite.search_placeholder')"
          class="w-48"
          allow-clear
          @press-enter="handleSearch"
        />
        <InputNumber
          v-model:value="userId"
          :placeholder="$t('im.favorite.filter.userId')"
          class="w-32"
          :min="1"
          :precision="0"
        />
        <Select
          v-model:value="type"
          :placeholder="$t('im.favorite.filter.type')"
          allow-clear
          class="w-32"
          :options="typeOptions"
          @change="handleSearch"
        />
        <Input
          v-model:value="tag"
          :placeholder="$t('im.favorite.filter.tag')"
          class="w-32"
          allow-clear
          @press-enter="handleSearch"
        />
        <Button type="primary" @click="handleSearch">
          <template #icon><Search class="size-4" /></template>
          {{ $t('im.favorite.search') }}
        </Button>
        <div class="flex-1"></div>
        <Button @click="handleReset">
          <template #icon><RefreshCw class="size-4" /></template>
          {{ $t('im.favorite.reset') }}
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
          showTotal: (t: number) => $t('im.favorite.total', { total: t }),
          onChange: handlePageChange,
        }"
        :scroll="{ x: 1300 }"
        row-key="id"
        size="middle"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'type'">
            <Tag color="blue">{{ typeText(record.type) }}</Tag>
          </template>
          <template v-else-if="column.key === 'content'">
            <span class="line-clamp-2 break-all">{{ record.content || '-' }}</span>
          </template>
          <template v-else-if="column.key === 'cover'">
            <Image
              v-if="record.cover && isCoverImage(record.cover)"
              :src="record.cover"
              :width="40"
              :height="40"
              class="rounded object-cover"
            />
            <span v-else class="text-muted-foreground">-</span>
          </template>
          <template v-else-if="column.key === 'tags'">
            <template v-if="(record.tags || []).length">
              <Tag v-for="t in record.tags" :key="t" class="mb-1">{{ t }}</Tag>
            </template>
            <span v-else class="text-muted-foreground">-</span>
          </template>
          <template v-else-if="column.key === 'updateTime'">
            {{ formatTime(record.updateTime) }}
          </template>
          <template v-else-if="column.key === 'action'">
            <Popconfirm :title="$t('im.favorite.delete_confirm')" @confirm="handleDelete(record)">
              <Button type="link" size="small" danger>
                {{ $t('im.favorite.action.delete') }}
              </Button>
            </Popconfirm>
          </template>
        </template>
        <template #emptyText>
          <Empty :description="$t('im.favorite.empty')" />
        </template>
      </Table>
    </div>
  </Page>
</template>
