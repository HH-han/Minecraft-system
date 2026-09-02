<script lang="ts" setup>
import type { TableColumnsType } from 'antdv-next';

import { computed, onMounted, reactive, ref } from 'vue';

import { Page } from '@vben/common-ui';
import {
  Bookmark,
  Heart,
  ImageIcon,
  ImageOff,
  MapPin,
  MessageCircle,
  Plus,
} from '@vben/icons';
import { $t } from '@vben/locales';

import {
  Avatar,
  Button,
  Card,
  Descriptions,
  Divider,
  Empty,
  Form,
  FormItem,
  Input,
  InputNumber,
  message,
  Modal,
  Popconfirm,
  Result,
  Select,
  Skeleton,
  Space,
  Table,
  Tag,
  Upload,
} from 'antdv-next';

import {
  addFood,
  deleteFood,
  getFoodList,
  updateFood,
} from '#/api/management/travel/food';
import { uploadFile } from '#/api/management/user/upload';

defineOptions({ name: 'FoodManagement' });

// =========================
// 美食数据类型（行结构较灵活，列表统一使用 any 便于承载 tagList/imageList 派生字段）
// =========================
interface FoodFormState {
  id?: number | string;
  name: string;
  province: string;
  city: string;
  address: string;
  cuisineType: string;
  price?: null | number;
  rating?: null | number;
  status: number;
  commodity: string;
  tags: string;
  coverImage: string;
  description: string;
  images: string;
}

// =========================
// 基础状态（服务端分页）
// =========================
const loading = ref(false);
const errorMsg = ref('');
const dataSource = ref<any[]>([]);

const pageNum = ref(1);
const pageSize = ref(10);
const pages = ref(0);
const totalCount = ref(0);

// 封面加载失败的记录 id 集合
const coverErrorSet = reactive<Set<number | string>>(new Set());

// =========================
// 弹窗 / 表单状态
// =========================
type ModalMode = 'create' | 'detail' | 'edit' | null;
const modalMode = ref<ModalMode>(null);
const modalVisible = ref(false);
const submitting = ref(false);

const formRef = ref<InstanceType<typeof Form> | null>(null);
const coverUploading = ref(false);

// 详情视图当前记录（含图集列表）
const currentRecord = ref<any>(null);

function emptyForm(): FoodFormState {
  return {
    name: '',
    province: '',
    city: '',
    address: '',
    cuisineType: '',
    price: undefined,
    rating: undefined,
    status: 1,
    commodity: '0',
    tags: '',
    coverImage: '',
    description: '',
    images: '',
  };
}

const formData = reactive<FoodFormState>(emptyForm());

const rules = computed(() => ({
  name: [
    {
      required: true,
      message: $t('travel.food_list.form.name_required'),
      trigger: 'blur',
    },
  ],
}));

const statusOptions = computed(() => [
  { label: $t('travel.food_list.status.on'), value: 1 },
  { label: $t('travel.food_list.status.off'), value: 0 },
]);

const commodityOptions = computed(() => [
  { label: $t('travel.food_list.commodity.yes'), value: '1' },
  { label: $t('travel.food_list.commodity.no'), value: '0' },
]);

// =========================
// 图片 URL 规范化
// =========================
const BACKEND_STATIC_BASE = 'http://localhost:8080';

function normalizeImageUrl(url?: null | string): string {
  if (!url) return '';
  const raw = String(url).trim();
  if (!raw || raw === 'null' || raw === 'undefined') return '';
  if (/^https?:\/\//i.test(raw)) return raw;
  if (raw.startsWith('/')) return BACKEND_STATIC_BASE + raw;
  return `${BACKEND_STATIC_BASE}/${raw}`;
}

// =========================
// 数据解析工具
// =========================
/** 逗号分隔字符串 / 数组 → string[] */
function splitList(value: any): string[] {
  if (Array.isArray(value)) return value.map((v) => String(v)).filter(Boolean);
  if (typeof value !== 'string' || !value.trim()) return [];
  return value
    .split(',')
    .map((s) => s.trim())
    .filter(Boolean);
}

/** 状态："1" 上架 / "0" 下架 */
function statusText(status: null | number | string | undefined): string {
  return String(status) === '1'
    ? $t('travel.food_list.status.on')
    : $t('travel.food_list.status.off');
}

function isListed(status: null | number | string | undefined): boolean {
  return String(status) === '1';
}

function locationText(rec: any): string {
  return [rec.province, rec.city, rec.address]
    .filter((v) => v !== undefined && v !== null && v !== '')
    .join(' ');
}

// =========================
// 数据加载 + 规范化
// =========================
async function fetchData() {
  loading.value = true;
  errorMsg.value = '';
  try {
    const res: any = await getFoodList({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
    });
    const records: any[] = Array.isArray(res)
      ? res
      : (res?.records ?? res?.list ?? res?.items ?? res?.rows ?? []);
    dataSource.value = normalizeList(records);
    pageNum.value = Number(res?.pageNum ?? pageNum.value);
    pageSize.value = Number(res?.pageSize ?? pageSize.value);
    pages.value = Number(res?.pages ?? res?.totalPages ?? 0);
    totalCount.value = Number(
      res?.total ?? (pages.value ? pages.value * pageSize.value : records.length),
    );
    coverErrorSet.clear();
  } catch (error: any) {
    errorMsg.value = error?.message || $t('travel.food_list.error_desc');
  } finally {
    loading.value = false;
  }
}

/**
 * 行数据规范化：
 * - coverImage 补全域名
 * - tags / images（逗号分隔字符串）拆成数组
 */
function normalizeList(records: any[]): any[] {
  return (records ?? []).map((raw) => {
    const row: any = { ...(raw ?? {}) };
    row.coverImage = normalizeImageUrl(row.coverImage);
    row.tagList = splitList(row.tags);
    row.imageList = splitList(row.images).map((u) => normalizeImageUrl(u));
    return row;
  });
}

onMounted(() => {
  fetchData();
});

// =========================
// 分页（服务端）
// =========================
const total = computed(() => {
  if (totalCount.value) return totalCount.value;
  if (!pages.value) return dataSource.value.length;
  if (
    pageNum.value === pages.value &&
    dataSource.value.length < pageSize.value
  ) {
    return (pages.value - 1) * pageSize.value + dataSource.value.length;
  }
  return pages.value * pageSize.value;
});

const pagination = computed(() => ({
  current: pageNum.value,
  pageSize: pageSize.value,
  total: total.value,
  showSizeChanger: true,
  pageSizeOptions: ['5', '10', '20', '50'],
  showTotal: (t: number) => $t('travel.food_list.total', { total: t }),
}));

function handleTableChange(pag: any) {
  pageNum.value = pag.current;
  pageSize.value = pag.pageSize;
  fetchData();
}

// =========================
// 表格列定义
// =========================
const columns = computed<TableColumnsType>(() => [
  {
    title: $t('travel.food_list.fields.id'),
    dataIndex: 'id',
    width: 80,
    fixed: 'left',
  },
  {
    title: $t('travel.food_list.fields.coverImage'),
    dataIndex: 'coverImage',
    width: 110,
    fixed: 'left',
  },
  {
    title: $t('travel.food_list.fields.name'),
    dataIndex: 'name',
    width: 180,
    fixed: 'left',
    ellipsis: true,
  },
  {
    title: $t('travel.food_list.fields.location'),
    key: 'location',
    width: 260,
    ellipsis: true,
  },
  {
    title: $t('travel.food_list.fields.cuisineType'),
    dataIndex: 'cuisineType',
    width: 100,
  },
  {
    title: $t('travel.food_list.fields.rating'),
    dataIndex: 'rating',
    width: 80,
  },
  {
    title: $t('travel.food_list.fields.price'),
    dataIndex: 'price',
    width: 120,
  },
  {
    title: $t('travel.food_list.fields.tags'),
    key: 'tags',
    width: 220,
    ellipsis: true,
  },
  {
    title: $t('travel.food_list.fields.status'),
    dataIndex: 'status',
    width: 90,
  },
  {
    title: $t('travel.food_list.fields.commodity'),
    dataIndex: 'commodity',
    width: 90,
  },
  {
    title: $t('travel.food_list.fields.likeCount'),
    dataIndex: 'likeCount',
    width: 100,
  },
  {
    title: $t('travel.food_list.fields.createTime'),
    dataIndex: 'createTime',
    width: 180,
  },
  {
    title: $t('travel.food_list.action.operation'),
    key: 'actions',
    width: 220,
    fixed: 'right',
  },
]);

// =========================
// 辅助函数
// =========================
function isCoverFailed(id: number | string): boolean {
  return coverErrorSet.has(id);
}

function handleCoverError(id: number | string) {
  coverErrorSet.add(id);
}

/**
 * 把后端单行记录转换为弹窗表单字段
 * （tags / images 保持逗号分隔字符串便于编辑）
 */
function recordToForm(rec: any): FoodFormState {
  return {
    id: rec.id,
    name: String(rec.name ?? ''),
    province: String(rec.province ?? ''),
    city: String(rec.city ?? ''),
    address: String(rec.address ?? ''),
    cuisineType: String(rec.cuisineType ?? ''),
    price:
      rec.price === null || rec.price === undefined
        ? undefined
        : Number(rec.price),
    rating:
      rec.rating === null || rec.rating === undefined
        ? undefined
        : Number(rec.rating),
    status: Number(rec.status ?? 1),
    commodity: String(rec.commodity ?? '0'),
    tags: String(rec.tags ?? ''),
    coverImage: normalizeImageUrl(rec.coverImage),
    description: String(rec.description ?? ''),
    images: String(rec.images ?? ''),
  };
}

// 表单内封面预览 URL（实时规范化）
const formCoverDisplayUrl = computed(() =>
  normalizeImageUrl(formData.coverImage),
);

// =========================
// 弹窗开关
// =========================
function openCreate() {
  currentRecord.value = null;
  Object.assign(formData, emptyForm());
  modalMode.value = 'create';
  modalVisible.value = true;
}

function openEdit(record: any) {
  currentRecord.value = record;
  Object.assign(formData, recordToForm(record));
  modalMode.value = 'edit';
  modalVisible.value = true;
}

function openDetail(record: any) {
  currentRecord.value = record;
  Object.assign(formData, recordToForm(record));
  modalMode.value = 'detail';
  modalVisible.value = true;
}

function closeModal() {
  modalVisible.value = false;
  modalMode.value = null;
  currentRecord.value = null;
  formRef.value?.resetFields?.();
  Object.assign(formData, emptyForm());
}

const modalTitle = computed(() => {
  if (modalMode.value === 'create')
    return $t('travel.food_list.modal.create_title');
  if (modalMode.value === 'edit')
    return $t('travel.food_list.modal.edit_title');
  return $t('travel.food_list.modal.detail_title');
});

// =========================
// 封面图片上传
// =========================
async function handleCoverUpload(file: File) {
  if (!file) return;
  if (!file.type.startsWith('image/')) {
    message.error($t('travel.food_list.form.cover_invalid_image'));
    return;
  }
  coverUploading.value = true;
  try {
    const res: any = await uploadFile(file, '/food/upload');
    // requestClient 解包成功响应后 data 即 URL 字符串，这里同时兼容对象形式
    const relativePath =
      typeof res === 'string'
        ? res
        : (res?.url ??
          res?.path ??
          res?.filePath ??
          res?.data?.url ??
          res?.data ??
          '');
    if (!relativePath) {
      message.error($t('travel.food_list.form.cover_upload_failed'));
      return;
    }
    formData.coverImage = String(relativePath);
    message.success($t('travel.food_list.form.cover_upload_success'));
  } catch (error: any) {
    const msg =
      error?.message || $t('travel.food_list.form.cover_upload_failed');
    message.error(msg);
  } finally {
    coverUploading.value = false;
  }
}

function handleCoverChange(event: any) {
  const file = event?.fileList?.[0]?.originFileObj;
  if (file) handleCoverUpload(file as File);
}

function hideBrokenImage(e: Event) {
  const target = e.target as HTMLImageElement;
  if (!target) return;
  target.style.display = 'none';
  // 图片地址更新并重新加载成功后自动恢复显示，避免更换封面后预览一直被隐藏
  target.onload = () => {
    target.style.display = '';
  };
}

// =========================
// 提交：新增 / 修改
// =========================
async function handleSubmit() {
  if (modalMode.value === 'detail') {
    closeModal();
    return;
  }
  try {
    await formRef.value?.validate();
  } catch {
    return;
  }
  submitting.value = true;
  try {
    const payload: any = {
      name: formData.name,
      province: formData.province || null,
      city: formData.city || null,
      address: formData.address || null,
      cuisineType: formData.cuisineType || null,
      price: formData.price ?? null,
      rating: formData.rating ?? null,
      status: Number(formData.status),
      commodity: formData.commodity,
      tags: formData.tags || null,
      coverImage: formData.coverImage || null,
      description: formData.description || null,
      images: formData.images || null,
    };
    if (formData.id !== undefined && formData.id !== null) {
      payload.id = formData.id;
      await updateFood(payload);
      message.success($t('travel.food_list.action.update_success'));
    } else {
      await addFood(payload);
      message.success($t('travel.food_list.action.create_success'));
    }
    closeModal();
    pageNum.value = 1;
    await fetchData();
  } catch (err: any) {
    const msg = err?.message || err?.msg || String(err || '');
    message.error(
      modalMode.value === 'create'
        ? msg || $t('travel.food_list.action.create_failed')
        : msg || $t('travel.food_list.action.update_failed'),
    );
  } finally {
    submitting.value = false;
  }
}

// =========================
// 删除（带二次确认）
// =========================
const deletingId = ref<null | number | string>(null);
async function handleDelete(record: any) {
  if (!record || record?.id === undefined || record?.id === null) return;
  deletingId.value = record.id;
  try {
    await deleteFood(record.id);
    message.success($t('travel.food_list.action.delete_success'));
    if (dataSource.value.length === 1 && pageNum.value > 1) {
      pageNum.value -= 1;
    }
    await fetchData();
  } catch (err: any) {
    const msg =
      err?.message || err?.msg || $t('travel.food_list.action.delete_failed');
    message.error(msg);
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
        v-if="loading && !dataSource.length"
        :styles="{ body: { padding: '24px' } }"
      >
        <Skeleton :active="true" :paragraph="{ rows: 8 }" />
      </Card>

      <!-- 加载失败：错误占位 + 重试 -->
      <Card
        v-else-if="errorMsg && !dataSource.length"
        :styles="{ body: { padding: '24px' } }"
      >
        <Result
          status="error"
          :title="$t('travel.food_list.error_title')"
          :sub-title="errorMsg"
        >
          <template #extra>
            <Button type="primary" @click="fetchData">
              {{ $t('travel.food_list.retry') }}
            </Button>
          </template>
        </Result>
      </Card>

      <!-- 美食列表 -->
      <Card
        v-else
        :title="$t('travel.food_list.title')"
        :styles="{ body: { padding: '0' } }"
      >
        <template #extra>
          <Button type="primary" :loading="loading" @click="openCreate">
            <template #icon>
              <Plus class="mr-1 size-4 align-middle" />
            </template>
            {{ $t('travel.food_list.action.add') }}
          </Button>
        </template>

        <Table
          :columns="columns"
          :data-source="dataSource"
          :pagination="pagination"
          :loading="loading"
          :scroll="{ x: 1940 }"
          :bordered="true"
          size="middle"
          row-key="id"
          @change="handleTableChange"
        >
          <template #bodyCell="{ column, record }">
            <!-- 封面缩略图（圆形） -->
            <template v-if="column.dataIndex === 'coverImage'">
              <div class="cover-cell">
                <img
                  v-if="record.coverImage && !isCoverFailed(record.id)"
                  :src="record.coverImage"
                  :alt="record.name"
                  loading="lazy"
                  class="cover-thumb"
                  @error="handleCoverError(record.id)"
                />
                <span v-else class="cover-fallback">
                  {{ $t('travel.food_list.no_cover') }}
                </span>
              </div>
            </template>

            <!-- 所在地 -->
            <template v-else-if="column.key === 'location'">
              <span class="inline-flex items-start gap-1">
                <MapPin
                  class="size-4 flex-shrink-0 text-muted-foreground"
                />
                <span class="text-sm">{{ locationText(record) || '-' }}</span>
              </span>
            </template>

            <!-- 菜系 -->
            <template v-else-if="column.dataIndex === 'cuisineType'">
              <Tag v-if="record.cuisineType" color="blue">
                {{ record.cuisineType }}
              </Tag>
              <span v-else class="text-xs text-muted-foreground">-</span>
            </template>

            <!-- 评分 -->
            <template v-else-if="column.dataIndex === 'rating'">
              <Tag color="gold">{{ record.rating ?? '-' }}</Tag>
            </template>

            <!-- 价格 -->
            <template v-else-if="column.dataIndex === 'price'">
              <span v-if="record.price !== null && record.price !== undefined" class="font-semibold text-red-500">
                ¥{{ record.price }}
              </span>
              <span v-else class="text-xs text-muted-foreground">-</span>
            </template>

            <!-- 标签 -->
            <template v-else-if="column.key === 'tags'">
              <span class="inline-flex flex-wrap gap-1">
                <template v-if="record.tagList?.length">
                  <Tag v-for="tg in record.tagList" :key="tg" color="orange">
                    {{ tg }}
                  </Tag>
                </template>
                <span v-else class="text-xs text-muted-foreground">-</span>
              </span>
            </template>

            <!-- 状态："1" 上架 / "0" 下架 -->
            <template v-else-if="column.dataIndex === 'status'">
              <Tag :color="isListed(record.status) ? 'green' : 'default'">
                {{ statusText(record.status) }}
              </Tag>
            </template>

            <!-- 商品："1" 是 / "0" 否 -->
            <template v-else-if="column.dataIndex === 'commodity'">
              <Tag :color="String(record.commodity) === '1' ? 'cyan' : 'default'">
                {{
                  String(record.commodity) === '1'
                    ? $t('travel.food_list.commodity.yes')
                    : $t('travel.food_list.commodity.no')
                }}
              </Tag>
            </template>

            <!-- 点赞数 -->
            <template v-else-if="column.dataIndex === 'likeCount'">
              <span class="inline-flex items-center gap-1 text-sm">
                <Heart class="size-3.5 text-muted-foreground" />
                {{ record.likeCount ?? 0 }}
              </span>
            </template>

            <!-- 创建时间 -->
            <template v-else-if="column.dataIndex === 'createTime'">
              {{ record.createTime || '-' }}
            </template>

            <!-- 操作列：详情 / 修改 / 删除 -->
            <template v-else-if="column.key === 'actions'">
              <Space :size="4" wrap>
                <Button type="link" size="small" @click="openDetail(record)">
                  {{ $t('travel.food_list.action.detail') }}
                </Button>
                <Button type="link" size="small" @click="openEdit(record)">
                  {{ $t('travel.food_list.action.edit') }}
                </Button>
                <Popconfirm
                  :title="$t('travel.food_list.action.confirm_delete_title')"
                  :description="$t('travel.food_list.action.confirm_delete')"
                  :ok-text="$t('travel.food_list.action.delete_text')"
                  :cancel-text="$t('travel.food_list.action.cancel')"
                  :ok-button-props="{ danger: true }"
                  @confirm="handleDelete(record)"
                >
                  <Button
                    type="link"
                    size="small"
                    danger
                    :loading="deletingId === record.id"
                  >
                    {{ $t('travel.food_list.action.delete') }}
                  </Button>
                </Popconfirm>
              </Space>
            </template>
          </template>

          <template #emptyText>
            <Empty :description="$t('travel.food_list.empty')" />
          </template>
        </Table>
      </Card>
    </div>

    <!-- 新增 / 修改 / 详情 弹窗 -->
    <Modal
      v-model:open="modalVisible"
      :title="modalTitle"
      :width="720"
      :mask-closable="false"
      :confirm-loading="submitting"
      :ok-text="$t('travel.food_list.action.save')"
      :cancel-text="$t('travel.food_list.action.cancel')"
      :styles="{
        body: {
          maxHeight: 'calc(100vh - 260px)',
          overflowY: 'auto',
          padding: '20px 24px',
        },
      }"
      @ok="handleSubmit"
      @cancel="closeModal"
    >
      <!-- 详情视图 -->
      <div v-if="modalMode === 'detail' && currentRecord">
        <!-- 头部：封面 + 名称 + 标签 + 互动数据 -->
        <div class="mb-4 flex items-start gap-4">
          <Avatar
            v-if="formCoverDisplayUrl"
            :src="formCoverDisplayUrl"
            :size="96"
            shape="circle"
            class="border border-border overflow-hidden object-cover flex-shrink-0"
          />
          <div v-else class="cover-placeholder-circle">
            <ImageOff class="size-8 text-muted-foreground" />
          </div>
          <div class="min-w-0">
            <div class="text-xl font-semibold truncate">{{ formData.name }}</div>
            <Space class="mt-1" :size="6">
              <Tag color="gold">{{ formData.rating ?? '-' }}</Tag>
              <Tag color="red">¥{{ formData.price ?? '-' }}</Tag>
              <Tag v-if="formData.cuisineType" color="blue">
                {{ formData.cuisineType }}
              </Tag>
              <Tag :color="isListed(formData.status) ? 'green' : 'default'">
                {{ statusText(formData.status) }}
              </Tag>
              <Tag :color="String(formData.commodity) === '1' ? 'cyan' : 'default'">
                {{
                  String(formData.commodity) === '1'
                    ? $t('travel.food_list.commodity.yes')
                    : $t('travel.food_list.commodity.no')
                }}
              </Tag>
            </Space>
            <div
              v-if="currentRecord.tagList?.length"
              class="mt-1 flex flex-wrap gap-1"
            >
              <Tag v-for="tg in currentRecord.tagList" :key="tg" color="orange">
                {{ tg }}
              </Tag>
            </div>
            <div
              class="mt-2 flex flex-wrap items-center gap-3 text-xs text-muted-foreground"
            >
              <span>
                <Heart class="mr-1 align-middle" />
                {{ currentRecord.likeCount ?? 0 }}
              </span>
              <span>
                <MessageCircle class="mr-1 align-middle" />
                {{ currentRecord.commentCount ?? 0 }}
              </span>
              <span>
                <Bookmark class="mr-1 align-middle" />
                {{ currentRecord.collectCount ?? 0 }}
              </span>
            </div>
          </div>
        </div>

        <Divider class="my-4">
          {{ $t('travel.food_list.detail.basic_info') }}
        </Divider>
        <Descriptions :column="2" bordered size="small">
          <Descriptions.Item :label="$t('travel.food_list.fields.id')">
            {{ (formData as any).id ?? '-' }}
          </Descriptions.Item>
          <Descriptions.Item :label="$t('travel.food_list.fields.cuisineType')">
            {{ formData.cuisineType || '-' }}
          </Descriptions.Item>
          <Descriptions.Item :label="$t('travel.food_list.fields.rating')">
            {{ formData.rating ?? '-' }}
          </Descriptions.Item>
          <Descriptions.Item :label="$t('travel.food_list.fields.price')">
            {{ formData.price !== null && formData.price !== undefined ? `¥${formData.price}` : '-' }}
          </Descriptions.Item>
        </Descriptions>

        <Divider class="my-4">
          {{ $t('travel.food_list.detail.location_info') }}
        </Divider>
        <Descriptions :column="2" bordered size="small">
          <Descriptions.Item :label="$t('travel.food_list.form.province_label')">
            {{ formData.province || '-' }}
          </Descriptions.Item>
          <Descriptions.Item :label="$t('travel.food_list.form.city_label')">
            {{ formData.city || '-' }}
          </Descriptions.Item>
          <Descriptions.Item
            :label="$t('travel.food_list.form.address_label')"
            :span="2"
          >
            {{ formData.address || '-' }}
          </Descriptions.Item>
        </Descriptions>

        <Divider class="my-4">
          {{ $t('travel.food_list.detail.desc_info') }}
        </Divider>
        <p class="m-0 whitespace-pre-wrap text-sm leading-6 text-muted-foreground">
          {{ formData.description || '-' }}
        </p>

        <Divider class="my-4">
          {{ $t('travel.food_list.detail.gallery_info') }}
        </Divider>
        <div
          v-if="currentRecord.imageList?.length"
          class="flex flex-wrap gap-2"
        >
          <img
            v-for="(img, idx) in currentRecord.imageList"
            :key="idx"
            :src="img"
            :alt="`${formData.name} ${Number(idx) + 1}`"
            loading="lazy"
            class="size-16 rounded-lg border border-border object-cover"
            @error="hideBrokenImage"
          />
        </div>
        <span v-else class="text-xs text-muted-foreground">-</span>

        <Divider class="my-4">
          {{ $t('travel.food_list.detail.time_info') }}
        </Divider>
        <Descriptions :column="2" bordered size="small">
          <Descriptions.Item :label="$t('travel.food_list.fields.createTime')">
            {{ currentRecord.createTime || '-' }}
          </Descriptions.Item>
          <Descriptions.Item :label="$t('travel.food_list.fields.updateTime')">
            {{ currentRecord.updateTime || '-' }}
          </Descriptions.Item>
        </Descriptions>
      </div>

      <!-- 创建 / 修改：Form -->
      <div
        v-if="modalMode === 'create' || modalMode === 'edit'"
        class="food-form-wrapper"
        style="display: block; width: 100%;"
      >
        <!-- 编辑模式：展示 ID -->
        <div v-if="modalMode === 'edit' && (formData as any).id" class="mb-4">
          <Descriptions :column="2" size="small" bordered>
            <Descriptions.Item
              :label="$t('travel.food_list.fields.id')"
              :span="2"
            >
              {{ (formData as any).id }}
            </Descriptions.Item>
          </Descriptions>
        </div>

        <Form
          ref="formRef"
          :model="formData"
          :rules="rules"
          layout="vertical"
          class="w-full"
          style="display: block;"
        >
          <div
            class="grid grid-cols-1 gap-x-6 gap-y-1 md:grid-cols-2"
            style="display: grid;"
          >
            <FormItem
              :label="$t('travel.food_list.form.name_label')"
              name="name"
              prop="name"
              style="display: block; width: 100%; margin-bottom: 16px;"
            >
              <Input
                v-model:value="formData.name"
                class="w-full"
                :placeholder="$t('travel.food_list.form.name_required')"
              />
            </FormItem>

            <FormItem
              :label="$t('travel.food_list.form.cuisine_label')"
              name="cuisineType"
              prop="cuisineType"
              style="display: block; width: 100%; margin-bottom: 16px;"
            >
              <Input
                v-model:value="formData.cuisineType"
                class="w-full"
                :placeholder="$t('travel.food_list.form.cuisine_label')"
              />
            </FormItem>

            <FormItem
              :label="$t('travel.food_list.form.province_label')"
              name="province"
              prop="province"
              style="display: block; width: 100%; margin-bottom: 16px;"
            >
              <Input
                v-model:value="formData.province"
                class="w-full"
                :placeholder="$t('travel.food_list.form.province_label')"
              />
            </FormItem>

            <FormItem
              :label="$t('travel.food_list.form.city_label')"
              name="city"
              prop="city"
              style="display: block; width: 100%; margin-bottom: 16px;"
            >
              <Input
                v-model:value="formData.city"
                class="w-full"
                :placeholder="$t('travel.food_list.form.city_label')"
              />
            </FormItem>

            <!-- 地址：全宽 -->
            <div class="col-span-2" style="width: 100%;">
              <FormItem
                :label="$t('travel.food_list.form.address_label')"
                name="address"
                prop="address"
                style="display: block; width: 100%; margin-bottom: 16px;"
              >
                <Input
                  v-model:value="formData.address"
                  class="w-full"
                  :placeholder="$t('travel.food_list.form.address_label')"
                />
              </FormItem>
            </div>

            <FormItem
              :label="$t('travel.food_list.form.price_label')"
              name="price"
              prop="price"
              style="display: block; width: 100%; margin-bottom: 16px;"
            >
              <InputNumber
                v-model:value="formData.price"
                class="w-full"
                :min="0"
                :placeholder="$t('travel.food_list.form.price_label')"
              />
            </FormItem>

            <FormItem
              :label="$t('travel.food_list.form.rating_label')"
              name="rating"
              prop="rating"
              style="display: block; width: 100%; margin-bottom: 16px;"
            >
              <InputNumber
                v-model:value="formData.rating"
                class="w-full"
                :min="0"
                :max="5"
                :step="0.5"
                :placeholder="$t('travel.food_list.form.rating_label')"
              />
            </FormItem>

            <FormItem
              :label="$t('travel.food_list.form.status_label')"
              name="status"
              prop="status"
              style="display: block; width: 100%; margin-bottom: 16px;"
            >
              <Select
                v-model:value="formData.status"
                class="w-full"
                :options="statusOptions"
              />
            </FormItem>

            <FormItem
              :label="$t('travel.food_list.form.commodity_label')"
              name="commodity"
              prop="commodity"
              style="display: block; width: 100%; margin-bottom: 16px;"
            >
              <Select
                v-model:value="formData.commodity"
                class="w-full"
                :options="commodityOptions"
              />
            </FormItem>

            <!-- 标签：全宽 -->
            <div class="col-span-2" style="width: 100%;">
              <FormItem
                :label="$t('travel.food_list.form.tags_label')"
                name="tags"
                prop="tags"
                :extra="$t('travel.food_list.form.tags_tip')"
                style="display: block; width: 100%; margin-bottom: 16px;"
              >
                <Input
                  v-model:value="formData.tags"
                  class="w-full"
                  :placeholder="$t('travel.food_list.form.tags_label')"
                />
              </FormItem>
            </div>

            <!-- 封面上传：全宽 -->
            <div class="col-span-2" style="width: 100%;">
              <FormItem
                :label="$t('travel.food_list.form.cover_label')"
                name="coverImage"
                prop="coverImage"
                style="display: block; width: 100%; margin-bottom: 16px;"
              >
                <Upload
                  :max-count="1"
                  :show-upload-list="false"
                  accept="image/*"
                  :before-upload="() => false"
                  @change="handleCoverChange"
                >
                  <Button
                    class="cover-upload-trigger flex w-full items-center gap-3 border-dashed"
                    :loading="coverUploading"
                    style="height: auto; padding: 8px 12px;"
                  >
                    <img
                      v-if="formCoverDisplayUrl"
                      :src="formCoverDisplayUrl"
                      :alt="$t('travel.food_list.form.cover_label')"
                      class="size-12 rounded-full border border-border flex-shrink-0 object-cover"
                      @error="hideBrokenImage"
                    />
                    <ImageIcon
                      v-else
                      class="size-12 flex-shrink-0 text-muted-foreground"
                    />
                    <div class="flex flex-col text-left">
                      <span class="text-sm font-medium">
                        {{
                          formData.coverImage
                            ? $t('travel.food_list.form.cover_change')
                            : $t('travel.food_list.form.cover_upload')
                        }}
                      </span>
                      <span class="text-xs text-muted-foreground">
                        {{ $t('travel.food_list.form.cover_tip') }}
                      </span>
                    </div>
                  </Button>
                </Upload>
              </FormItem>
            </div>

            <!-- 简介：大文本域，全宽 -->
            <div class="col-span-2" style="width: 100%;">
              <FormItem
                :label="$t('travel.food_list.form.desc_label')"
                name="description"
                prop="description"
                style="display: block; width: 100%; margin-bottom: 16px;"
              >
                <Input.TextArea
                  v-model:value="formData.description"
                  :rows="3"
                  class="w-full"
                  :placeholder="$t('travel.food_list.form.desc_label')"
                />
              </FormItem>
            </div>

            <!-- 图集链接：大文本域，全宽 -->
            <div class="col-span-2" style="width: 100%;">
              <FormItem
                :label="$t('travel.food_list.form.images_label')"
                name="images"
                prop="images"
                :extra="$t('travel.food_list.form.images_tip')"
                style="display: block; width: 100%; margin-bottom: 16px;"
              >
                <Input.TextArea
                  v-model:value="formData.images"
                  :rows="3"
                  class="w-full"
                  :placeholder="$t('travel.food_list.form.images_label')"
                />
              </FormItem>
            </div>
          </div>
        </Form>
      </div>
    </Modal>
  </Page>
</template>

<style scoped>
.cover-cell {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 54px;
  height: 54px;
  border-radius: 50%;
  overflow: hidden;
  background-color: var(--app-fill-color, rgb(0 0 0 / 4%));
  flex-shrink: 0;
}

.cover-thumb {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.cover-fallback {
  color: var(--muted-foreground-color, rgb(0 0 0 / 45%));
  font-size: 11px;
  text-align: center;
}

.cover-placeholder-circle {
  display: flex;
  width: 96px;
  height: 96px;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  background-color: var(--app-fill-color, rgb(0 0 0 / 4%));
  flex-shrink: 0;
}

.cover-upload-trigger {
  text-align: left;
}
</style>