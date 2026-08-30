<script lang="ts" setup>
import type { TableColumnsType } from 'antdv-next';

import { computed, onMounted, reactive, ref } from 'vue';

import { Page } from '@vben/common-ui';
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
  Skeleton,
  Space,
  Table,
  Tag,
  Upload,
} from 'antdv-next';
import dayjs from 'dayjs';

import {
  addHotel,
  deleteHotel,
  getHotelList,
  updateHotel,
} from '#/api/management/travel/hotel';
import { uploadFile } from '#/api/management/user/upload';

defineOptions({ name: 'HotelList' });

// =========================
// 酒店数据类型
// =========================
interface HotelRoom {
  id: number | string;
  name: string;
  description?: string;
  facilities?: string[];
  price: number;
}

interface HotelItem {
  id: number | string;
  name: string;
  city?: string;
  province?: string;
  address?: string;
  coverImage?: string;
  images?: string[];
  description?: string;
  rating?: number;
  price?: number;
  collectCount?: number;
  commentCount?: number;
  likeCount?: number;
  facilities?: string[];
  rooms?: HotelRoom[];
  createTime?: number | string;
}

interface HotelFormState {
  id?: number | string;
  name: string;
  province: string;
  city: string;
  address: string;
  coverImage: string;
  description: string;
  rating: number;
  price: number;
  facilities: string;
  // 详情视图专用（表单不提交）
  likeCount: number;
  collectCount: number;
  commentCount: number;
  rooms: HotelRoom[];
  createTime?: number | string;
}

// =========================
// 基础状态
// =========================
const loading = ref(false);
const errorMsg = ref('');
const dataSource = ref<HotelItem[]>([]);

// 分页状态（与后端字段对齐：服务端分页）
const pageNum = ref(1);
const pageSize = ref(10);
const pages = ref(0);

// 表格中封面图加载失败的酒店 id 集合
const coverErrorSet = reactive<Set<number | string>>(new Set());

// =========================
// 弹窗 / 表单状态
// =========================
type ModalMode = 'create' | 'detail' | 'edit' | null;
const modalMode = ref<ModalMode>(null);
const modalVisible = ref(false);
const submitting = ref(false);

const formRef = ref<InstanceType<typeof Form> | null>(null);

// 封面上传中
const coverUploading = ref(false);

function emptyForm(): HotelFormState {
  return {
    name: '',
    province: '',
    city: '',
    address: '',
    coverImage: '',
    description: '',
    rating: 0,
    price: 0,
    facilities: '',
    likeCount: 0,
    collectCount: 0,
    commentCount: 0,
    rooms: [],
    createTime: undefined,
  };
}

const formData = reactive<HotelFormState>(emptyForm());

const rules = computed(() => ({
  name: [
    {
      required: true,
      message: $t('travel.hotel_list.form.name_required'),
      trigger: 'blur',
    },
  ],
  rating: [
    {
      type: 'number',
      min: 0,
      max: 5,
      message: $t('travel.hotel_list.form.rating_range'),
      trigger: 'blur',
    },
  ],
  price: [
    {
      type: 'number',
      min: 0,
      message: $t('travel.hotel_list.form.price_range'),
      trigger: 'blur',
    },
  ],
}));

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
// 数据加载 + 规范化
// =========================
async function fetchData() {
  loading.value = true;
  errorMsg.value = '';
  try {
    const res: any = await getHotelList({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
    });
    // 响应拦截器已剥离外层 data，res 即 { pageNum, pageSize, pages, records }
    const records: any[] = Array.isArray(res)
      ? res
      : (res?.records ?? res?.list ?? res?.items ?? res?.rows ?? []);
    dataSource.value = normalizeList(records);
    pageNum.value = Number(res?.pageNum ?? pageNum.value);
    pageSize.value = Number(res?.pageSize ?? pageSize.value);
    pages.value = Number(res?.pages ?? res?.totalPages ?? 0);
    coverErrorSet.clear();
  } catch (error: any) {
    errorMsg.value = error?.message || $t('travel.hotel_list.error_desc');
  } finally {
    loading.value = false;
  }
}

function normalizeList(records: any[]): HotelItem[] {
  return (records ?? []).map((raw) => {
    const row: any = { ...(raw ?? {}) };
    row.coverImage = normalizeImageUrl(row.coverImage);
    row.images = Array.isArray(row.images)
      ? row.images.map((u: string) => normalizeImageUrl(u)).filter(Boolean)
      : [];
    row.facilities = Array.isArray(row.facilities) ? row.facilities : [];
    row.rooms = Array.isArray(row.rooms) ? row.rooms : [];
    return row as HotelItem;
  });
}

onMounted(() => {
  fetchData();
});

// =========================
// 分页：后端仅返回 pages（总页数），total 估算并按最后一页修正
// =========================
const total = computed(() => {
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
  showTotal: (t: number) => $t('travel.hotel_list.total', { total: t }),
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
    title: $t('travel.hotel_list.fields.id'),
    dataIndex: 'id',
    width: 80,
    fixed: 'left',
  },
  {
    title: $t('travel.hotel_list.fields.coverImage'),
    dataIndex: 'coverImage',
    width: 110,
    fixed: 'left',
  },
  {
    title: $t('travel.hotel_list.fields.name'),
    dataIndex: 'name',
    width: 220,
    fixed: 'left',
    ellipsis: true,
  },
  {
    title: $t('travel.hotel_list.fields.location'),
    key: 'location',
    width: 260,
    ellipsis: true,
  },
  {
    title: $t('travel.hotel_list.fields.rating'),
    dataIndex: 'rating',
    width: 90,
  },
  {
    title: $t('travel.hotel_list.fields.price'),
    dataIndex: 'price',
    width: 110,
  },
  {
    title: $t('travel.hotel_list.fields.facilities'),
    dataIndex: 'facilities',
    width: 380,
    ellipsis: true,
  },
  {
    title: $t('travel.hotel_list.fields.rooms'),
    dataIndex: 'rooms',
    width: 380,
    ellipsis: true,
  },
  {
    title: $t('travel.hotel_list.fields.createTime'),
    dataIndex: 'createTime',
    width: 180,
  },
  {
    title: $t('travel.hotel_list.action.operation'),
    key: 'actions',
    width: 220,
    fixed: 'right',
  },
]);

// =========================
// 辅助函数
// =========================
function formatDateTime(value: number | string | undefined): string {
  if (value === null || value === undefined || value === '') return '-';
  if (typeof value === 'number') return dayjs(value).format('YYYY-MM-DD HH:mm:ss');
  const parsed = dayjs(value);
  return parsed.isValid() ? parsed.format('YYYY-MM-DD HH:mm:ss') : String(value);
}

function locationText(record: any): string {
  return [record.province, record.city, record.address]
    .filter((v) => v !== undefined && v !== null && v !== '')
    .join(' ');
}

function isCoverFailed(id: number | string): boolean {
  return coverErrorSet.has(id);
}

function handleCoverError(id: number | string) {
  coverErrorSet.add(id);
}

/**
 * 把后端单行记录转换为弹窗表单字段（设施数组 → 逗号分隔字符串便于编辑）
 */
function recordToForm(rec: any): HotelFormState {
  return {
    id: rec.id,
    name: String(rec.name ?? ''),
    province: String(rec.province ?? ''),
    city: String(rec.city ?? ''),
    address: String(rec.address ?? ''),
    coverImage: normalizeImageUrl(rec.coverImage),
    description: String(rec.description ?? ''),
    rating: Number(rec.rating) || 0,
    price: Number(rec.price) || 0,
    facilities: Array.isArray(rec.facilities)
      ? rec.facilities.join(',')
      : String(rec.facilities ?? ''),
    likeCount: Number(rec.likeCount) || 0,
    collectCount: Number(rec.collectCount) || 0,
    commentCount: Number(rec.commentCount) || 0,
    rooms: Array.isArray(rec.rooms) ? rec.rooms : [],
    createTime: rec.createTime,
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
  Object.assign(formData, emptyForm());
  modalMode.value = 'create';
  modalVisible.value = true;
}

function openEdit(record: any) {
  Object.assign(formData, recordToForm(record));
  modalMode.value = 'edit';
  modalVisible.value = true;
}

function openDetail(record: any) {
  Object.assign(formData, recordToForm(record));
  modalMode.value = 'detail';
  modalVisible.value = true;
}

function closeModal() {
  modalVisible.value = false;
  modalMode.value = null;
  formRef.value?.resetFields?.();
  Object.assign(formData, emptyForm());
}

const modalTitle = computed(() => {
  if (modalMode.value === 'create') return $t('travel.hotel_list.modal.create_title');
  if (modalMode.value === 'edit') return $t('travel.hotel_list.modal.edit_title');
  return $t('travel.hotel_list.modal.detail_title');
});

// =========================
// 封面图片上传
// =========================
async function handleCoverUpload(file: File) {
  if (!file) return;
  if (!file.type.startsWith('image/')) {
    message.error($t('travel.hotel_list.form.cover_invalid_image'));
    return;
  }
  coverUploading.value = true;
  try {
    const res: any = await uploadFile(file);
    const relativePath =
      res?.url ?? res?.path ?? res?.filePath ?? res?.data?.url ?? '';
    if (!relativePath) {
      message.error($t('travel.hotel_list.form.cover_upload_failed'));
      return;
    }
    // 存相对路径，由 normalizeImageUrl 统一补全展示
    formData.coverImage = String(relativePath);
    message.success($t('travel.hotel_list.form.cover_upload_success'));
  } catch (error: any) {
    const msg = error?.message || $t('travel.hotel_list.form.cover_upload_failed');
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
  if (target) target.style.display = 'none';
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
    const facilities = String(formData.facilities)
      .split(',')
      .map((s) => s.trim())
      .filter(Boolean);
    const payload: any = {
      name: formData.name,
      province: formData.province,
      city: formData.city,
      address: formData.address,
      coverImage: formData.coverImage,
      description: formData.description,
      rating: Number(formData.rating),
      price: Number(formData.price),
      facilities,
    };
    if (formData.id !== undefined && formData.id !== null) {
      payload.id = formData.id;
      await updateHotel(payload);
      message.success($t('travel.hotel_list.action.update_success'));
    } else {
      await addHotel(payload);
      message.success($t('travel.hotel_list.action.create_success'));
    }
    closeModal();
    // 新增/修改后回到第一页，确保新数据可见
    pageNum.value = 1;
    await fetchData();
  } catch (err: any) {
    const msg =
      err?.message || err?.msg || String(err || '');
    message.error(
      modalMode.value === 'create'
        ? msg || $t('travel.hotel_list.action.create_failed')
        : msg || $t('travel.hotel_list.action.update_failed'),
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
    await deleteHotel(record.id);
    message.success($t('travel.hotel_list.action.delete_success'));
    // 删除后若当前页空了，回退一页
    if (dataSource.value.length === 1 && pageNum.value > 1) {
      pageNum.value -= 1;
    }
    await fetchData();
  } catch (err: any) {
    const msg = err?.message || err?.msg || $t('travel.hotel_list.action.delete_failed');
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
      <Card v-if="loading && !dataSource.length" :styles="{ body: { padding: '24px' } }">
        <Skeleton :active="true" :paragraph="{ rows: 8 }" />
      </Card>

      <!-- 加载失败：错误占位 + 重试 -->
      <Card v-else-if="errorMsg && !dataSource.length" :styles="{ body: { padding: '24px' } }">
        <Result
          status="error"
          :title="$t('travel.hotel_list.error_title')"
          :sub-title="errorMsg"
        >
          <template #extra>
            <Button type="primary" @click="fetchData">
              {{ $t('travel.hotel_list.retry') }}
            </Button>
          </template>
        </Result>
      </Card>

      <!-- 酒店列表 -->
      <Card v-else :title="$t('travel.hotel_list.title')" :styles="{ body: { padding: '0' } }">
        <template #extra>
          <Button type="primary" :loading="loading" @click="openCreate">
            <template #icon>
              <span class="i-lucide-plus mr-1 size-4 align-middle"></span>
            </template>
            {{ $t('travel.hotel_list.action.add') }}
          </Button>
        </template>

        <Table
          :columns="columns"
          :data-source="dataSource"
          :pagination="pagination"
          :loading="loading"
          :scroll="{ x: 1600 }"
          :bordered="true"
          size="middle"
          row-key="id"
          @change="handleTableChange"
        >
          <template #bodyCell="{ column, record }">
            <!-- 封面图缩略图 -->
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
                  {{ $t('travel.hotel_list.no_cover') }}
                </span>
              </div>
            </template>

            <!-- 所在地 -->
            <template v-else-if="column.key === 'location'">
              <span class="inline-flex items-start gap-1">
                <span class="i-lucide-map-pin size-4 flex-shrink-0 text-muted-foreground"></span>
                <span class="text-sm">{{ locationText(record) || '-' }}</span>
              </span>
            </template>

            <!-- 评分 -->
            <template v-else-if="column.dataIndex === 'rating'">
              <Tag color="gold">{{ record.rating ?? 0 }}</Tag>
            </template>

            <!-- 价格 -->
            <template v-else-if="column.dataIndex === 'price'">
              <span class="font-semibold text-red-500">
                ¥{{ record.price ?? 0 }}
                <span class="text-xs font-normal text-muted-foreground">
                  {{ $t('travel.hotel_list.per_night') }}
                </span>
              </span>
            </template>

            <!-- 设施 -->
            <template v-else-if="column.dataIndex === 'facilities'">
              <span class="inline-flex flex-wrap gap-1">
                <template v-if="record.facilities?.length">
                  <Tag v-for="f in record.facilities" :key="f" color="blue">
                    {{ f }}
                  </Tag>
                </template>
                <span v-else class="text-xs text-muted-foreground">-</span>
              </span>
            </template>

            <!-- 房间类型 -->
            <template v-else-if="column.dataIndex === 'rooms'">
              <span class="inline-flex flex-wrap gap-1">
                <template v-if="record.rooms?.length">
                  <Tag v-for="room in record.rooms" :key="room.id" color="purple">
                    {{ room.name }}
                  </Tag>
                </template>
                <span v-else class="text-xs text-muted-foreground">-</span>
              </span>
            </template>

            <!-- 创建时间 -->
            <template v-else-if="column.dataIndex === 'createTime'">
              {{ formatDateTime(record.createTime) }}
            </template>

            <!-- 操作列：详情 / 修改 / 删除 -->
            <template v-else-if="column.key === 'actions'">
              <Space :size="4" wrap>
                <Button type="link" size="small" @click="openDetail(record)">
                  {{ $t('travel.hotel_list.action.detail') }}
                </Button>
                <Button type="link" size="small" @click="openEdit(record)">
                  {{ $t('travel.hotel_list.action.edit') }}
                </Button>
                <Popconfirm
                  :title="$t('travel.hotel_list.action.confirm_delete_title')"
                  :description="$t('travel.hotel_list.action.confirm_delete')"
                  :ok-text="$t('travel.hotel_list.action.delete_text')"
                  :cancel-text="$t('travel.hotel_list.action.cancel')"
                  :ok-button-props="{ danger: true }"
                  @confirm="handleDelete(record)"
                >
                  <Button
                    type="link"
                    size="small"
                    danger
                    :loading="deletingId === record.id"
                  >
                    {{ $t('travel.hotel_list.action.delete') }}
                  </Button>
                </Popconfirm>
              </Space>
            </template>
          </template>

          <template #emptyText>
            <Empty :description="$t('travel.hotel_list.empty')" />
          </template>
        </Table>
      </Card>
    </div>

    <!-- 新增 / 修改 / 详情 弹窗 -->
    <Modal
      v-model:open="modalVisible"
      :title="modalTitle"
      :width="760"
      :mask-closable="false"
      :confirm-loading="submitting"
      :ok-text="$t('travel.hotel_list.action.save')"
      :cancel-text="$t('travel.hotel_list.action.cancel')"
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
      <div v-if="modalMode === 'detail'">
        <div class="mb-4 flex items-start gap-4">
          <Avatar
            v-if="formCoverDisplayUrl"
            :src="formCoverDisplayUrl"
            :size="96"
            shape="circle"
            class="border border-border overflow-hidden object-cover flex-shrink-0"
          />
          <div v-else class="cover-placeholder-square">
            <span class="i-lucide-image-off size-8 text-muted-foreground"></span>
          </div>
          <div class="min-w-0">
            <div class="text-xl font-semibold truncate">{{ formData.name }}</div>
            <Space class="mt-1" :size="6">
              <Tag color="gold">{{ formData.rating }}</Tag>
              <Tag color="red">
                ¥{{ formData.price }}{{ $t('travel.hotel_list.per_night') }}
              </Tag>
            </Space>
            <div class="mt-2 flex items-center gap-3 text-xs text-muted-foreground">
              <span>
                <span class="i-lucide-heart mr-1 align-middle"></span>
                {{ formData.likeCount }}
              </span>
              <span>
                <span class="i-lucide-bookmark mr-1 align-middle"></span>
                {{ formData.collectCount }}
              </span>
              <span>
                <span class="i-lucide-message-circle mr-1 align-middle"></span>
                {{ formData.commentCount }}
              </span>
            </div>
          </div>
        </div>

        <Divider class="my-4">{{ $t('travel.hotel_list.detail.basic_info') }}</Divider>
        <Descriptions :column="2" bordered size="small">
          <Descriptions.Item :label="$t('travel.hotel_list.fields.id')">
            {{ (formData as any).id ?? '-' }}
          </Descriptions.Item>
          <Descriptions.Item :label="$t('travel.hotel_list.fields.name')">
            {{ formData.name || '-' }}
          </Descriptions.Item>
          <Descriptions.Item :label="$t('travel.hotel_list.fields.rating')">
            {{ formData.rating || '-' }}
          </Descriptions.Item>
          <Descriptions.Item :label="$t('travel.hotel_list.fields.price')">
            ¥{{ formData.price }}{{ $t('travel.hotel_list.per_night') }}
          </Descriptions.Item>
        </Descriptions>

        <Divider class="my-4">{{ $t('travel.hotel_list.detail.location_info') }}</Divider>
        <Descriptions :column="2" bordered size="small">
          <Descriptions.Item :label="$t('travel.hotel_list.fields.province')">
            {{ formData.province || '-' }}
          </Descriptions.Item>
          <Descriptions.Item :label="$t('travel.hotel_list.fields.city')">
            {{ formData.city || '-' }}
          </Descriptions.Item>
          <Descriptions.Item :label="$t('travel.hotel_list.fields.address')" :span="2">
            {{ formData.address || '-' }}
          </Descriptions.Item>
        </Descriptions>

        <Divider class="my-4">{{ $t('travel.hotel_list.detail.facilities_info') }}</Divider>
        <div v-if="formData.facilities" class="flex flex-wrap gap-1">
          <Tag v-for="f in formData.facilities.split(',').filter(Boolean)" :key="f" color="blue">
            {{ f }}
          </Tag>
        </div>
        <span v-else class="text-xs text-muted-foreground">
          {{ $t('travel.hotel_list.detail.no_facilities') }}
        </span>

        <Divider class="my-4">{{ $t('travel.hotel_list.fields.description') }}</Divider>
        <p class="m-0 text-sm leading-6 text-muted-foreground">
          {{ formData.description || $t('travel.hotel_list.detail.no_facilities') }}
        </p>

        <Divider class="my-4">{{ $t('travel.hotel_list.detail.rooms_info') }}</Divider>
        <div v-if="formData.rooms?.length" class="flex flex-col gap-2">
          <div
            v-for="room in formData.rooms"
            :key="room.id"
            class="room-detail-card"
          >
            <div class="flex items-center justify-between gap-3">
              <div class="flex items-center gap-2 font-medium">
                <span class="i-lucide-bed size-4"></span>
                <span>{{ room.name }}</span>
              </div>
              <span class="font-semibold text-red-500">
                ¥{{ room.price }}{{ $t('travel.hotel_list.per_night') }}
              </span>
            </div>
            <div v-if="room.description" class="mt-1 text-xs text-muted-foreground">
              {{ room.description }}
            </div>
            <div v-if="room.facilities?.length" class="mt-2 flex flex-wrap gap-1">
              <Tag v-for="f in room.facilities" :key="f" color="default">{{ f }}</Tag>
            </div>
          </div>
        </div>
        <span v-else class="text-xs text-muted-foreground">
          {{ $t('travel.hotel_list.detail.no_rooms') }}
        </span>

        <Divider class="my-4">{{ $t('travel.hotel_list.fields.createTime') }}</Divider>
        <Descriptions :column="1" bordered size="small">
          <Descriptions.Item :label="$t('travel.hotel_list.fields.createTime')">
            {{ formatDateTime((formData as any).createTime) }}
          </Descriptions.Item>
        </Descriptions>
      </div>

      <!-- 创建 / 修改：Form -->
      <div
        v-if="modalMode === 'create' || modalMode === 'edit'"
        class="hotel-form-wrapper"
        style="display: block; width: 100%;"
      >
        <!-- 编辑模式：展示 ID -->
        <div v-if="modalMode === 'edit' && (formData as any).id" class="mb-4">
          <Descriptions :column="2" size="small" bordered>
            <Descriptions.Item :label="$t('travel.hotel_list.fields.id')" :span="2">
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
              :label="$t('travel.hotel_list.form.name_label')"
              name="name"
              prop="name"
              class="col-span-2"
              style="display: block; width: 100%; margin-bottom: 16px;"
            >
              <Input
                v-model:value="formData.name"
                class="w-full"
                :placeholder="`${$t('travel.hotel_list.form.placeholder_text')}${$t('travel.hotel_list.form.name_label')}`"
              />
            </FormItem>

            <FormItem
              :label="$t('travel.hotel_list.form.province_label')"
              name="province"
              prop="province"
              style="display: block; width: 100%; margin-bottom: 16px;"
            >
              <Input
                v-model:value="formData.province"
                class="w-full"
                :placeholder="`${$t('travel.hotel_list.form.placeholder_text')}${$t('travel.hotel_list.form.province_label')}`"
              />
            </FormItem>

            <FormItem
              :label="$t('travel.hotel_list.form.city_label')"
              name="city"
              prop="city"
              style="display: block; width: 100%; margin-bottom: 16px;"
            >
              <Input
                v-model:value="formData.city"
                class="w-full"
                :placeholder="`${$t('travel.hotel_list.form.placeholder_text')}${$t('travel.hotel_list.form.city_label')}`"
              />
            </FormItem>

            <FormItem
              :label="$t('travel.hotel_list.form.address_label')"
              name="address"
              prop="address"
              class="col-span-2"
              style="display: block; width: 100%; margin-bottom: 16px;"
            >
              <Input
                v-model:value="formData.address"
                class="w-full"
                :placeholder="`${$t('travel.hotel_list.form.placeholder_text')}${$t('travel.hotel_list.form.address_label')}`"
              />
            </FormItem>

            <FormItem
              :label="$t('travel.hotel_list.form.price_label')"
              name="price"
              prop="price"
              style="display: block; width: 100%; margin-bottom: 16px;"
            >
              <InputNumber
                v-model:value="formData.price"
                class="w-full"
                :min="0"
                :placeholder="`${$t('travel.hotel_list.form.placeholder_text')}${$t('travel.hotel_list.form.price_label')}`"
              />
            </FormItem>

            <FormItem
              :label="$t('travel.hotel_list.form.rating_label')"
              name="rating"
              prop="rating"
              style="display: block; width: 100%; margin-bottom: 16px;"
            >
              <InputNumber
                v-model:value="formData.rating"
                class="w-full"
                :min="0"
                :max="5"
                :step="0.1"
                :placeholder="`${$t('travel.hotel_list.form.placeholder_text')}${$t('travel.hotel_list.form.rating_label')}`"
              />
            </FormItem>

            <FormItem
              :label="$t('travel.hotel_list.form.facilities_label')"
              name="facilities"
              prop="facilities"
              class="col-span-2"
              :extra="$t('travel.hotel_list.form.facilities_tip')"
              style="display: block; width: 100%; margin-bottom: 16px;"
            >
              <Input
                v-model:value="formData.facilities"
                class="w-full"
                :placeholder="`${$t('travel.hotel_list.form.placeholder_text')}${$t('travel.hotel_list.form.facilities_label')}`"
              />
            </FormItem>

            <!-- 封面上传：全宽 -->
            <div class="col-span-2" style="width: 100%;">
              <FormItem
                :label="$t('travel.hotel_list.form.cover_image_label')"
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
                      :alt="$t('travel.hotel_list.form.cover_image_label')"
                      class="size-12 rounded-full border border-border flex-shrink-0 object-cover"
                      @error="hideBrokenImage"
                    />
                    <span
                      v-else
                      class="i-lucide-image size-12 flex-shrink-0 text-muted-foreground"
                    ></span>
                    <div class="flex flex-col text-left">
                      <span class="text-sm font-medium">
                        {{
                          formData.coverImage
                            ? $t('travel.hotel_list.form.cover_change')
                            : $t('travel.hotel_list.form.cover_upload')
                        }}
                      </span>
                      <span class="text-xs text-muted-foreground">
                        {{ $t('travel.hotel_list.form.cover_tip') }}
                      </span>
                    </div>
                  </Button>
                </Upload>
              </FormItem>
            </div>

            <!-- 简介：大文本域，全宽 -->
            <div class="col-span-2" style="width: 100%;">
              <FormItem
                :label="$t('travel.hotel_list.form.description_label')"
                name="description"
                prop="description"
                style="display: block; width: 100%; margin-bottom: 16px;"
              >
                <Input.TextArea
                  v-model:value="formData.description"
                  :rows="3"
                  class="w-full"
                  :placeholder="`${$t('travel.hotel_list.form.placeholder_text')}${$t('travel.hotel_list.form.description_label')}`"
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

.cover-placeholder-square {
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

.room-detail-card {
  padding: 10px 12px;
  border-radius: 8px;
  background-color: var(--app-fill-color, rgb(0 0 0 / 3%));
}
</style>
