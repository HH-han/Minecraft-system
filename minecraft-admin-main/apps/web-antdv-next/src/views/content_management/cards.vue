<script lang="ts" setup>
import type { TableColumnsType } from 'antdv-next';

import { computed, onMounted, reactive, ref } from 'vue';

import { Page } from '@vben/common-ui';
import { ImageOff, Plus, RefreshCw, Search } from '@vben/icons';
import { $t } from '@vben/locales';

import {
  Button,
  Card,
  Descriptions,
  Form,
  FormItem,
  Input,
  message,
  Modal,
  Popconfirm,
  Result,
  Skeleton,
  Table,
  Tag,
  Upload,
} from 'antdv-next';

import {
  addCard,
  deleteCard,
  getCardDetail,
  getCardList,
  updateCard,
} from '#/api/management/content/cards';
import { uploadFile } from '#/api/management/user/upload';

defineOptions({ name: 'CardsManagement' });

interface CardRecord {
  content?: null | string;
  createdAt?: null | string;
  date?: null | string;
  id: number;
  imageList?: null | string[];
  images?: null | string;
  likeCount?: null | number;
  location?: null | string;
  status?: null | number;
  tags?: null | string;
  tagNames?: null | string[];
  title?: null | string;
  updatedAt?: null | string;
  userId?: null | number;
  username?: null | string;
  viewCount?: null | number;
}

// =========================
// 图片 URL 规范化
// =========================
const BACKEND_STATIC_BASE = 'http://localhost:8080';

function normalizeImageUrl(url?: null | string): string {
  if (!url) return '';
  const raw = String(url).trim();
  if (!raw || raw === 'null' || raw === 'undefined') return '';
  if (/^data:/i.test(raw)) return raw;
  if (/^https?:\/\//i.test(raw)) return raw;
  if (raw.startsWith('/')) return BACKEND_STATIC_BASE + raw;
  return `${BACKEND_STATIC_BASE}/${raw}`;
}

/** 解析后端图片字段：优先使用已解析的 imageList，兼容 JSON 字符串与逗号分隔字符串 */
function parseImageList(record?: CardRecord | null): string[] {
  if (!record) return [];
  if (Array.isArray(record.imageList)) return record.imageList;
  const raw = record.images;
  if (!raw) return [];
  const str = String(raw).trim();
  if (!str) return [];
  if (str.startsWith('[')) {
    try {
      const parsed = JSON.parse(str);
      if (Array.isArray(parsed)) {
        return parsed.map((item) => String(item).trim()).filter(Boolean);
      }
    } catch {
      // JSON 解析失败时回退为逗号分隔解析
    }
  }
  return str
    .split(',')
    .map((item) => item.trim().replaceAll('`', '').replaceAll('"', ''))
    .filter(Boolean);
}

/** 解析标签展示：优先使用后端关联表标签名，兼容逗号分隔 tags 字段 */
function parseTagNames(record?: CardRecord | null): string[] {
  if (!record) return [];
  if (Array.isArray(record.tagNames) && record.tagNames.length > 0) {
    return record.tagNames;
  }
  if (!record.tags) return [];
  return String(record.tags)
    .split(',')
    .map((item) => item.trim())
    .filter(Boolean);
}

// =========================
// 状态
// =========================
const loading = ref(false);
const errorMsg = ref('');
const cardList = ref<CardRecord[]>([]);
const imageErrorSet = reactive<Set<number>>(new Set());
const deletingId = ref<null | number>(null);

// 分页与筛选
const pageNum = ref(1);
const pageSize = ref(10);
const pages = ref(0);
const total = ref(0);
const keyword = ref('');
const locationFilter = ref('');

// =========================
// 工具
// =========================
function displayValue(value: any): string {
  if (value === null || value === undefined || value === '') return '-';
  return String(value);
}

function formatTime(value?: null | string): string {
  if (!value) return '-';
  return String(value).replace('T', ' ');
}

function toNumber(value: any): number {
  const n = Number(value);
  return Number.isFinite(n) ? n : 0;
}

function statusMeta(status?: null | number): { color: string; text: string } {
  if (Number(status) === 0) {
    return {
      color: 'default',
      text: $t('content.cards_page.status.deleted'),
    };
  }
  if (Number(status) === 2) {
    return {
      color: 'orange',
      text: $t('content.cards_page.status.reviewing'),
    };
  }
  return { color: 'green', text: $t('content.cards_page.status.normal') };
}

// =========================
// 列定义
// =========================
const columns = computed<TableColumnsType>(() => [
  { title: 'ID', dataIndex: 'id', width: 70 },
  { title: $t('content.cards_page.fields.cover'), key: 'cover', width: 90 },
  {
    title: $t('content.cards_page.fields.title'),
    key: 'title',
    width: 200,
    ellipsis: true,
  },
  {
    title: $t('content.cards_page.fields.author'),
    key: 'username',
    width: 120,
    ellipsis: true,
  },
  {
    title: $t('content.cards_page.fields.location'),
    key: 'location',
    width: 120,
    ellipsis: true,
  },
  { title: $t('content.cards_page.fields.tags'), key: 'tags', width: 170 },
  { title: $t('content.cards_page.fields.date'), key: 'date', width: 115 },
  {
    title: $t('content.cards_page.fields.viewCount'),
    key: 'viewCount',
    width: 80,
  },
  {
    title: $t('content.cards_page.fields.likeCount'),
    key: 'likeCount',
    width: 80,
  },
  { title: $t('content.cards_page.fields.status'), key: 'status', width: 85 },
  {
    title: $t('content.cards_page.fields.createTime'),
    key: 'createdAt',
    width: 170,
  },
  {
    title: $t('content.cards_page.fields.action'),
    key: 'action',
    width: 170,
    fixed: 'right',
  },
]);

// =========================
// 数据加载（服务端分页）
// =========================
async function fetchData() {
  loading.value = true;
  errorMsg.value = '';
  try {
    const res: any = await getCardList({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      keyword: keyword.value.trim() || undefined,
      location: locationFilter.value.trim() || undefined,
    });
    if (Array.isArray(res)) {
      cardList.value = res;
      pages.value = 0;
      total.value = res.length;
    } else {
      cardList.value = res?.records ?? [];
      pages.value = toNumber(res?.pages);
      total.value = toNumber(res?.total);
    }
    imageErrorSet.clear();
  } catch (error: any) {
    errorMsg.value = error?.message || $t('content.cards_page.error_desc');
  } finally {
    loading.value = false;
  }
}

function handleSearch() {
  pageNum.value = 1;
  fetchData();
}

function handlePageChange(page: number, size: number) {
  pageNum.value = page;
  pageSize.value = size;
  fetchData();
}

function handlePageSizeChange(current: number, size: number) {
  pageNum.value = 1;
  pageSize.value = size;
  fetchData();
}

onMounted(() => {
  fetchData();
});

function handleImageError(id: number) {
  imageErrorSet.add(id);
}

// =========================
// 详情弹窗
// =========================
const detailVisible = ref(false);
const detailRecord = ref<CardRecord | null>(null);
const detailImages = ref<string[]>([]);
const detailTags = ref<string[]>([]);

async function openDetail(record: CardRecord) {
  detailRecord.value = record;
  detailImages.value = parseImageList(record);
  detailTags.value = parseTagNames(record);
  detailVisible.value = true;
  // 拉取最新详情（后端同时累加浏览次数）
  try {
    const res: any = await getCardDetail(record.id);
    if (res && typeof res === 'object') {
      detailRecord.value = res;
      detailImages.value = parseImageList(res);
      detailTags.value = parseTagNames(res);
    }
  } catch {
    // 详情拉取失败时保留列表行数据
  }
}

// =========================
// 新增 / 编辑
// =========================
const modalVisible = ref(false);
const submitting = ref(false);
const formRef = ref<InstanceType<typeof Form> | null>(null);
const editingId = ref<null | number>(null);
const imageUploading = ref(false);

const formRecord = reactive({
  title: '',
  content: '',
  location: '',
  tags: '',
  date: '' as string,
  imageList: [] as string[],
});

const modalTitle = computed(() =>
  editingId.value === null
    ? $t('content.cards_page.create_title')
    : $t('content.cards_page.edit_title'),
);

function resetForm() {
  formRecord.title = '';
  formRecord.content = '';
  formRecord.location = '';
  formRecord.tags = '';
  formRecord.date = '';
  formRecord.imageList = [];
}

function openCreate() {
  editingId.value = null;
  resetForm();
  modalVisible.value = true;
}

function openEdit(record: CardRecord) {
  editingId.value = record.id;
  formRecord.title = record.title ?? '';
  formRecord.content = record.content ?? '';
  formRecord.location = record.location ?? '';
  formRecord.tags = record.tags ?? '';
  formRecord.date = record.date ?? '';
  formRecord.imageList = parseImageList(record);
  modalVisible.value = true;
}

function closeModal() {
  modalVisible.value = false;
  editingId.value = null;
  formRef.value?.resetFields?.();
}

// =========================
// 图片上传（支持多张）
// =========================
async function handleImageUpload(file: File) {
  if (!file.type.startsWith('image/')) {
    message.error($t('content.cards_page.image_invalid_image'));
    return;
  }
  imageUploading.value = true;
  try {
    const res: any = await uploadFile(file);
    const relativePath =
      res?.url ?? res?.path ?? res?.filePath ?? res?.data?.url ?? '';
    if (!relativePath) {
      message.error($t('content.cards_page.image_upload_failed'));
      return;
    }
    formRecord.imageList.push(String(relativePath));
    message.success($t('content.cards_page.image_upload_success'));
  } catch (error: any) {
    message.error(
      error?.message || $t('content.cards_page.image_upload_failed'),
    );
  } finally {
    imageUploading.value = false;
  }
}

function handleImageChange(event: any) {
  const file = event?.fileList?.[0]?.originFileObj;
  if (file) handleImageUpload(file as File);
}

function removeImage(index: number) {
  formRecord.imageList.splice(index, 1);
}

function hideBrokenImage(e: Event) {
  const target = e.target as HTMLImageElement;
  if (target) target.style.display = 'none';
}

// =========================
// 保存
// =========================
async function handleSave() {
  try {
    await formRef.value?.validate();
  } catch {
    return;
  }
  submitting.value = true;
  try {
    const payload: any = {
      title: formRecord.title,
      content: formRecord.content,
      location: formRecord.location || null,
      tags: formRecord.tags || null,
      date: formRecord.date || null,
      images:
        formRecord.imageList.length > 0
          ? JSON.stringify(formRecord.imageList)
          : null,
    };
    if (editingId.value !== null) {
      payload.id = editingId.value;
      await updateCard(payload);
    } else {
      await addCard(payload);
    }
    message.success($t('content.cards_page.save_success'));
    closeModal();
    await fetchData();
  } catch (err: any) {
    message.error(err?.message || $t('content.cards_page.save_failed'));
  } finally {
    submitting.value = false;
  }
}

// =========================
// 删除
// =========================
async function handleDelete(record: CardRecord) {
  deletingId.value = record.id;
  try {
    await deleteCard(record.id);
    message.success($t('content.cards_page.delete_success'));
    if (
      pages.value > 0 &&
      pageNum.value > 1 &&
      cardList.value.length <= 1
    ) {
      pageNum.value -= 1;
    }
    await fetchData();
  } catch (err: any) {
    message.error(err?.message || $t('content.cards_page.delete_failed'));
  } finally {
    deletingId.value = null;
  }
}
</script>

<template>
  <Page :auto-content-height="true">
    <div class="mx-auto w-full">
      <!-- 加载中：骨架屏（仅首次加载） -->
      <Card
        v-if="loading && cardList.length === 0"
        :title="$t('content.cards_page.title')"
        :styles="{ body: { padding: '24px' } }"
      >
        <Skeleton :active="true" :paragraph="{ rows: 8 }" />
      </Card>

      <!-- 加载失败：错误占位 + 重试 -->
      <Card
        v-else-if="errorMsg && cardList.length === 0"
        :title="$t('content.cards_page.title')"
        :styles="{ body: { padding: '24px' } }"
      >
        <Result
          status="error"
          :title="$t('content.cards_page.error_title')"
          :sub-title="errorMsg"
        >
          <template #extra>
            <Button type="primary" @click="fetchData">
              {{ $t('content.cards_page.retry') }}
            </Button>
          </template>
        </Result>
      </Card>

      <!-- 列表 -->
      <Card
        v-else
        :title="$t('content.cards_page.title')"
        :styles="{ body: { padding: '0' } }"
      >
        <template #extra>
          <div class="flex flex-wrap items-center gap-2">
            <Input
              v-model:value="keyword"
              :placeholder="$t('content.cards_page.search_placeholder')"
              allow-clear
              class="w-48"
              @press-enter="handleSearch"
            >
              <template #prefix>
                <Search class="size-4 text-muted-foreground" />
              </template>
            </Input>
            <Input
              v-model:value="locationFilter"
              :placeholder="$t('content.cards_page.location_placeholder')"
              allow-clear
              class="w-36"
              @press-enter="handleSearch"
            />
            <Button :loading="loading" @click="handleSearch">
              <template #icon>
                <RefreshCw class="mr-1 size-4 align-middle" />
              </template>
              {{ $t('content.cards_page.refresh') }}
            </Button>
            <Button type="primary" @click="openCreate">
              <template #icon>
                <Plus class="mr-1 size-4 align-middle" />
              </template>
              {{ $t('content.cards_page.add') }}
            </Button>
          </div>
        </template>

        <Table
          :columns="columns"
          :data-source="cardList"
          :loading="loading"
          :pagination="
            pages === 0
              ? { pageSize: 10, showSizeChanger: false }
              : {
                  current: pageNum,
                  pageSize,
                  showSizeChanger: true,
                  pageSizeOptions: ['10', '20', '50'],
                  total,
                  showTotal: (t: number) =>
                    `${t} ${$t('content.cards_page.total_suffix')}`,
                  onChange: handlePageChange,
                  onShowSizeChange: handlePageSizeChange,
                }
          "
          :scroll="{ x: 1620 }"
          size="small"
          row-key="id"
        >
          <template #bodyCell="{ column, record }">
            <!-- 封面图（取第一张图片） -->
            <template v-if="column.key === 'cover'">
              <img
                v-if="
                  parseImageList(record)[0] && !imageErrorSet.has(record.id)
                "
                :src="normalizeImageUrl(parseImageList(record)[0])"
                :alt="record.title || 'card'"
                loading="lazy"
                class="size-13.5 rounded-full border border-border object-cover"
                @error="handleImageError(record.id)"
              />
              <div
                v-else
                class="flex size-13.5 items-center justify-center rounded-full border border-border bg-accent"
              >
                <ImageOff class="size-5 text-muted-foreground" />
              </div>
            </template>

            <!-- 标题 -->
            <template v-else-if="column.key === 'title'">
              <span class="font-medium">{{ displayValue(record.title) }}</span>
            </template>

            <!-- 作者 -->
            <template v-else-if="column.key === 'username'">
              {{ displayValue(record.username) }}
            </template>

            <!-- 地点 -->
            <template v-else-if="column.key === 'location'">
              {{ displayValue(record.location) }}
            </template>

            <!-- 标签 -->
            <template v-else-if="column.key === 'tags'">
              <div class="flex flex-wrap gap-1">
                <Tag
                  v-for="tag in parseTagNames(record).slice(0, 3)"
                  :key="tag"
                  color="blue"
                >
                  {{ tag }}
                </Tag>
                <span
                  v-if="parseTagNames(record).length === 0"
                  class="text-muted-foreground"
                  >-</span>
              </div>
            </template>

            <!-- 旅行日期 -->
            <template v-else-if="column.key === 'date'">
              <span class="tabular-nums">{{ displayValue(record.date) }}</span>
            </template>

            <!-- 浏览次数 -->
            <template v-else-if="column.key === 'viewCount'">
              <span class="tabular-nums">{{ record.viewCount ?? 0 }}</span>
            </template>

            <!-- 点赞数 -->
            <template v-else-if="column.key === 'likeCount'">
              <span class="tabular-nums">{{ record.likeCount ?? 0 }}</span>
            </template>

            <!-- 状态 -->
            <template v-else-if="column.key === 'status'">
              <Tag :color="statusMeta(record.status).color">
                {{ statusMeta(record.status).text }}
              </Tag>
            </template>

            <!-- 创建时间 -->
            <template v-else-if="column.key === 'createdAt'">
              <span class="text-xs text-muted-foreground">
                {{ formatTime(record.createdAt) }}
              </span>
            </template>

            <!-- 操作 -->
            <template v-else-if="column.key === 'action'">
              <Button type="link" size="small" @click="openDetail(record)">
                {{ $t('content.cards_page.detail') }}
              </Button>
              <Button type="link" size="small" @click="openEdit(record)">
                {{ $t('content.cards_page.edit') }}
              </Button>
              <Popconfirm
                :title="$t('content.cards_page.delete_confirm_title')"
                :description="$t('content.cards_page.delete_confirm')"
                :ok-text="$t('content.cards_page.delete')"
                :cancel-text="$t('content.cards_page.cancel')"
                :ok-button-props="{ danger: true }"
                @confirm="handleDelete(record)"
              >
                <Button
                  type="link"
                  size="small"
                  danger
                  :loading="deletingId === record.id"
                >
                  {{ $t('content.cards_page.delete') }}
                </Button>
              </Popconfirm>
            </template>
          </template>

          <template #emptyText>
            <span class="text-sm text-muted-foreground">
              {{ $t('content.cards_page.list_empty') }}
            </span>
          </template>
        </Table>
      </Card>
    </div>

    <!-- 详情弹窗 -->
    <Modal
      v-model:open="detailVisible"
      :title="$t('content.cards_page.detail_title')"
      :width="720"
      :footer="null"
      :styles="{ body: { padding: '20px 24px' } }"
    >
      <template v-if="detailRecord">
        <div class="mb-4 flex items-start justify-between gap-4">
          <div class="min-w-0">
            <div class="text-base font-semibold">
              {{ displayValue(detailRecord.title) }}
            </div>
            <div class="mt-2 flex flex-wrap items-center gap-2">
              <Tag :color="statusMeta(detailRecord.status).color">
                {{ statusMeta(detailRecord.status).text }}
              </Tag>
              <Tag
                v-for="tag in detailTags"
                :key="tag"
                color="blue"
              >
                {{ tag }}
              </Tag>
            </div>
          </div>
        </div>

        <!-- 图片墙 -->
        <div
          v-if="detailImages.length > 0"
          class="mb-4 grid grid-cols-3 gap-2"
        >
          <img
            v-for="(img, index) in detailImages"
            :key="`${img}-${index}`"
            :src="normalizeImageUrl(img)"
            :alt="`card-image-${index}`"
            loading="lazy"
            class="aspect-square w-full rounded-md border border-border object-cover"
            @error="hideBrokenImage"
          />
        </div>

        <div
          v-if="detailRecord.content"
          class="mb-4 max-h-60 overflow-y-auto whitespace-pre-wrap rounded-md border border-border bg-accent/40 p-3 text-sm leading-6"
        >
          {{ detailRecord.content }}
        </div>

        <Descriptions :column="2" size="small" bordered>
          <Descriptions.Item :label="$t('content.cards_page.fields.id')">
            {{ detailRecord.id }}
          </Descriptions.Item>
          <Descriptions.Item
            :label="$t('content.cards_page.fields.author')"
          >
            {{ displayValue(detailRecord.username) }}
          </Descriptions.Item>
          <Descriptions.Item
            :label="$t('content.cards_page.fields.location')"
          >
            {{ displayValue(detailRecord.location) }}
          </Descriptions.Item>
          <Descriptions.Item :label="$t('content.cards_page.fields.date')">
            {{ displayValue(detailRecord.date) }}
          </Descriptions.Item>
          <Descriptions.Item
            :label="$t('content.cards_page.fields.viewCount')"
          >
            {{ detailRecord.viewCount ?? 0 }}
          </Descriptions.Item>
          <Descriptions.Item
            :label="$t('content.cards_page.fields.likeCount')"
          >
            {{ detailRecord.likeCount ?? 0 }}
          </Descriptions.Item>
          <Descriptions.Item
            :label="$t('content.cards_page.fields.createTime')"
          >
            {{ formatTime(detailRecord.createdAt) }}
          </Descriptions.Item>
          <Descriptions.Item
            :label="$t('content.cards_page.fields.updateTime')"
          >
            {{ formatTime(detailRecord.updatedAt) }}
          </Descriptions.Item>
        </Descriptions>
      </template>
    </Modal>

    <!-- 新增 / 编辑弹窗 -->
    <Modal
      v-model:open="modalVisible"
      :title="modalTitle"
      :width="640"
      :mask-closable="false"
      :confirm-loading="submitting"
      :ok-text="$t('content.cards_page.save')"
      :cancel-text="$t('content.cards_page.cancel')"
      :styles="{
        body: {
          maxHeight: 'calc(100vh - 260px)',
          overflowY: 'auto',
          padding: '20px 24px',
        },
      }"
      @ok="handleSave"
      @cancel="closeModal"
    >
      <Form
        ref="formRef"
        :model="formRecord"
        layout="vertical"
        class="w-full"
      >
        <FormItem
          :label="$t('content.cards_page.fields.title')"
          name="title"
          :rules="[
            {
              required: true,
              message: $t('content.cards_page.title_required'),
            },
          ]"
        >
          <Input
            v-model:value="formRecord.title"
            class="w-full"
            :placeholder="$t('content.cards_page.fields.title')"
          />
        </FormItem>

        <FormItem
          :label="$t('content.cards_page.fields.content')"
          name="content"
          :rules="[
            {
              required: true,
              message: $t('content.cards_page.content_required'),
            },
          ]"
        >
          <Input.TextArea
            v-model:value="formRecord.content"
            :rows="5"
            class="w-full"
            :placeholder="$t('content.cards_page.fields.content')"
          />
        </FormItem>

        <div class="grid grid-cols-1 gap-x-6 md:grid-cols-2">
          <FormItem
            :label="$t('content.cards_page.fields.location')"
            name="location"
          >
            <Input
              v-model:value="formRecord.location"
              class="w-full"
              :placeholder="$t('content.cards_page.location_placeholder')"
            />
          </FormItem>

          <FormItem :label="$t('content.cards_page.fields.date')" name="date">
            <Input
              v-model:value="formRecord.date"
              type="date"
              class="w-full"
            />
          </FormItem>

          <FormItem
            :label="$t('content.cards_page.fields.tags')"
            name="tags"
            class="md:col-span-2"
          >
            <Input
              v-model:value="formRecord.tags"
              class="w-full"
              :placeholder="$t('content.cards_page.tags_placeholder')"
            />
          </FormItem>

          <FormItem
            :label="$t('content.cards_page.fields.images')"
            name="images"
            class="md:col-span-2"
          >
            <div class="flex items-center gap-2">
              <Upload
                :max-count="1"
                :show-upload-list="false"
                accept="image/*"
                :before-upload="() => false"
                @change="handleImageChange"
              >
                <Button :loading="imageUploading">
                  {{ $t('content.cards_page.image_upload') }}
                </Button>
              </Upload>
              <span class="text-xs text-muted-foreground">
                {{ $t('content.cards_page.image_hint') }}
              </span>
            </div>
            <div
              v-if="formRecord.imageList.length > 0"
              class="mt-2 flex flex-wrap gap-2"
            >
              <div
                v-for="(img, index) in formRecord.imageList"
                :key="`${img}-${index}`"
                class="group relative size-16"
              >
                <img
                  :src="normalizeImageUrl(img)"
                  alt="preview"
                  class="size-16 rounded-md border border-border object-cover"
                  @error="hideBrokenImage"
                />
                <button
                  type="button"
                  class="absolute -right-1.5 -top-1.5 flex size-5 items-center justify-center rounded-full bg-destructive text-xs text-white opacity-90 hover:opacity-100"
                  @click="removeImage(index)"
                >
                  ×
                </button>
              </div>
            </div>
          </FormItem>
        </div>
      </Form>
    </Modal>
  </Page>
</template>
