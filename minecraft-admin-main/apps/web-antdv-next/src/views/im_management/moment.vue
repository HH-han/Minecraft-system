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
  message,
  Modal,
  Popconfirm,
  Select,
  Space,
  Spin,
  Table,
  Tag,
} from 'antdv-next';

import {
  deleteMoment,
  getMomentComments,
  getMomentLikes,
  getMomentPage,
} from '#/api/management/im/moment';

defineOptions({ name: 'ImMomentManagement' });

interface MomentRow {
  id: number;
  userId?: null | number;
  username?: null | string;
  nickname?: null | string;
  avatar?: null | string;
  content?: null | string;
  images?: null | string[];
  location?: null | string;
  visibility?: null | number;
  likeCount?: null | number;
  commentCount?: null | number;
  createTime?: null | string;
}

interface CommentRow {
  id: number;
  userId?: null | number;
  nickname?: null | string;
  username?: null | string;
  avatar?: null | string;
  replyToNickname?: null | string;
  content?: null | string;
  createTime?: null | string;
}

interface LikeRow {
  userId?: null | number;
  nickname?: null | string;
  username?: null | string;
  avatar?: null | string;
}

const loading = ref(false);
const rows = ref<MomentRow[]>([]);
const pageNum = ref(1);
const pageSize = ref(10);
const total = ref(0);

const userId = ref<null | number>(null);
const keyword = ref('');
const visibility = ref<null | number>(null);

const showDetail = ref(false);
const detailLoading = ref(false);
const currentMoment = ref<MomentRow | null>(null);
const comments = ref<CommentRow[]>([]);
const likes = ref<LikeRow[]>([]);

const visibilityOptions = computed(() => [
  { value: 0, label: $t('im.moment.visibility.public') },
  { value: 1, label: $t('im.moment.visibility.friends') },
  { value: 2, label: $t('im.moment.visibility.private') },
]);

function formatTime(value?: null | string): string {
  if (!value) return '-';
  return String(value).replace('T', ' ').slice(0, 19);
}
function visibilityText(v?: null | number): string {
  return visibilityOptions.value.find((o) => o.value === Number(v))?.label ?? '-';
}
function visibilityColor(v?: null | number): string {
  return { 0: 'green', 1: 'blue', 2: 'default' }[Number(v)] ?? 'default';
}
function displayName(name?: null | string, username?: null | string): string {
  return name || username || '-';
}

const columns = computed<TableColumnsType>(() => [
  { title: 'ID', dataIndex: 'id', width: 70 },
  { title: $t('im.moment.columns.author'), key: 'author', width: 180 },
  { title: $t('im.moment.columns.content'), key: 'content', width: 280 },
  { title: $t('im.moment.columns.images'), key: 'images', width: 160 },
  { title: $t('im.moment.columns.location'), dataIndex: 'location', width: 130, ellipsis: true },
  { title: $t('im.moment.columns.visibility'), key: 'visibility', width: 100 },
  { title: $t('im.moment.columns.likeCount'), dataIndex: 'likeCount', width: 80 },
  { title: $t('im.moment.columns.commentCount'), dataIndex: 'commentCount', width: 80 },
  { title: $t('im.moment.columns.createTime'), key: 'createTime', width: 170 },
  { title: $t('im.moment.columns.action'), key: 'action', width: 160, fixed: 'right' },
]);

async function fetchData() {
  loading.value = true;
  try {
    const res: any = await getMomentPage({
      page: pageNum.value,
      size: pageSize.value,
      userId: userId.value ?? undefined,
      keyword: keyword.value.trim() || undefined,
      visibility: visibility.value ?? undefined,
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
  keyword.value = '';
  visibility.value = null;
  pageNum.value = 1;
  fetchData();
}
function handlePageChange(page: number, size: number) {
  pageNum.value = page;
  pageSize.value = size;
  fetchData();
}

async function openDetail(row: any) {
  currentMoment.value = row;
  showDetail.value = true;
  detailLoading.value = true;
  try {
    const [commentRes, likeRes]: any = await Promise.all([
      getMomentComments(row.id),
      getMomentLikes(row.id),
    ]);
    comments.value = commentRes ?? [];
    likes.value = likeRes ?? [];
  } catch {
    comments.value = [];
    likes.value = [];
  } finally {
    detailLoading.value = false;
  }
}

async function handleDelete(row: any) {
  try {
    await deleteMoment(row.id);
    message.success($t('im.moment.delete_success'));
    await fetchData();
  } catch (e: any) {
    message.error(e?.message || $t('im.moment.failed'));
  }
}

onMounted(fetchData);
</script>

<template>
  <Page :title="$t('im.moment.title')" :description="$t('im.moment.description')">
    <div class="bg-card rounded-lg p-4">
      <div class="mb-4 flex flex-wrap items-center gap-2">
        <Input
          v-model:value="keyword"
          :placeholder="$t('im.moment.search_placeholder')"
          class="w-52"
          allow-clear
          @press-enter="handleSearch"
        />
        <InputNumber
          v-model:value="userId"
          :placeholder="$t('im.moment.filter.userId')"
          class="w-36"
          :min="1"
          :precision="0"
        />
        <Select
          v-model:value="visibility"
          :placeholder="$t('im.moment.filter.visibility')"
          allow-clear
          class="w-32"
          :options="visibilityOptions"
          @change="handleSearch"
        />
        <Button type="primary" @click="handleSearch">
          <template #icon><Search class="size-4" /></template>
          {{ $t('im.moment.search') }}
        </Button>
        <div class="flex-1"></div>
        <Button @click="handleReset">
          <template #icon><RefreshCw class="size-4" /></template>
          {{ $t('im.moment.reset') }}
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
          showTotal: (t: number) => $t('im.moment.total', { total: t }),
          onChange: handlePageChange,
        }"
        :scroll="{ x: 1400 }"
        row-key="id"
        size="middle"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'author'">
            <div class="flex items-center gap-2">
              <AntAvatar :src="record.avatar || undefined" :size="32">
                {{ (record.username || '?').slice(0, 1) }}
              </AntAvatar>
              <div class="min-w-0">
                <div class="truncate">{{ displayName(record.nickname, record.username) }}</div>
                <div class="text-xs text-muted-foreground">ID: {{ record.userId }}</div>
              </div>
            </div>
          </template>
          <template v-else-if="column.key === 'content'">
            <span class="line-clamp-2 break-all">{{ record.content || '-' }}</span>
          </template>
          <template v-else-if="column.key === 'images'">
            <div class="flex gap-1">
              <Image
                v-for="(img, idx) in (record.images || []).slice(0, 3)"
                :key="idx"
                :src="img"
                :width="36"
                :height="36"
                class="rounded object-cover"
              />
              <span v-if="!(record.images || []).length" class="text-muted-foreground">-</span>
            </div>
          </template>
          <template v-else-if="column.key === 'visibility'">
            <Tag :color="visibilityColor(record.visibility)">
              {{ visibilityText(record.visibility) }}
            </Tag>
          </template>
          <template v-else-if="column.key === 'createTime'">
            {{ formatTime(record.createTime) }}
          </template>
          <template v-else-if="column.key === 'action'">
            <Space :size="4" wrap>
              <Button type="link" size="small" @click="openDetail(record)">
                {{ $t('im.moment.action.detail') }}
              </Button>
              <Popconfirm :title="$t('im.moment.delete_confirm')" @confirm="handleDelete(record)">
                <Button type="link" size="small" danger>
                  {{ $t('im.moment.action.delete') }}
                </Button>
              </Popconfirm>
            </Space>
          </template>
        </template>
        <template #emptyText>
          <Empty :description="$t('im.moment.empty')" />
        </template>
      </Table>
    </div>

    <!-- 评论 / 点赞明细 -->
    <Modal
      v-model:open="showDetail"
      :title="$t('im.moment.detail_title')"
      width="720px"
      :footer="null"
    >
      <div v-if="currentMoment" class="mb-4 rounded-lg bg-muted/40 p-3">
        <div class="flex items-center gap-2">
          <AntAvatar :src="currentMoment.avatar || undefined" :size="32">
            {{ (currentMoment.username || '?').slice(0, 1) }}
          </AntAvatar>
          <span class="font-medium">
            {{ displayName(currentMoment.nickname, currentMoment.username) }}
          </span>
          <span class="text-xs text-muted-foreground">
            {{ formatTime(currentMoment.createTime) }}
          </span>
        </div>
        <p class="mt-2 whitespace-pre-wrap break-all text-sm">
          {{ currentMoment.content || '-' }}
        </p>
      </div>

      <div class="mb-2 font-medium">{{ $t('im.moment.likes') }}（{{ likes.length }}）</div>
      <div v-if="likes.length" class="mb-4 flex flex-wrap gap-3">
        <div v-for="(like, idx) in likes" :key="idx" class="flex items-center gap-2">
          <AntAvatar :src="like.avatar || undefined" :size="26">
            {{ (like.username || '?').slice(0, 1) }}
          </AntAvatar>
          <span class="text-sm">{{ displayName(like.nickname, like.username) }}</span>
        </div>
      </div>
      <Empty v-else :description="$t('im.moment.no_likes')" class="mb-4" />

      <div class="mb-2 font-medium">
        {{ $t('im.moment.comments') }}（{{ comments.length }}）
      </div>
      <Spin :spinning="detailLoading">
        <div v-if="comments.length" class="space-y-3">
          <div v-for="item in comments" :key="item.id" class="flex items-start gap-2">
            <AntAvatar :src="item.avatar || undefined" :size="26">
              {{ (item.username || '?').slice(0, 1) }}
            </AntAvatar>
            <div class="min-w-0 flex-1">
              <div class="text-sm">
                <span class="font-medium">
                  {{ displayName(item.nickname, item.username) }}
                </span>
                <span v-if="item.replyToNickname" class="text-muted-foreground">
                  {{ ` → ${item.replyToNickname}` }}
                </span>
              </div>
              <div class="break-all text-sm">{{ item.content }}</div>
              <div class="text-xs text-muted-foreground">{{ formatTime(item.createTime) }}</div>
            </div>
          </div>
        </div>
        <Empty v-else :description="$t('im.moment.no_comments')" />
      </Spin>
    </Modal>
  </Page>
</template>
