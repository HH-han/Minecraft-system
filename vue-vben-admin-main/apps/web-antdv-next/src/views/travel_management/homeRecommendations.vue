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
  Select,
  Skeleton,
  Space,
  Switch,
  Table,
  Tag,
  Upload,
} from 'antdv-next';
import dayjs from 'dayjs';

import {
  addHomeRecommendation,
  deleteHomeRecommendation,
  getHomeRecommendationsList,
  updateHomeRecommendation,
} from '#/api/management/travel/homeRecommendations';
import { uploadFile } from '#/api/management/user/upload';

defineOptions({ name: 'HomeRecommendations' });

// =========================
// 首页推荐数据类型（对应后端富字段结构）
// =========================
interface RecommendFormState {
  id?: number | string;
  uuid?: null | string;
  name: string;
  englishName: string;
  categoryId?: null | number;
  categoryName: string;
  country: string;
  province: string;
  city: string;
  district: string;
  address: string;
  summary: string;
  content: string;
  coverImageUrl: string;
  tags: string;
  highlights: string;
  minPrice?: null | number;
  maxPrice?: null | number;
  priceCurrency: string;
  season: string;
  bestTravelTime: string;
  suitableCrowd: string;
  visitDuration: string;
  openingHoursWeekday: string;
  openingHoursWeekend: string;
  contactPhone: string;
  officialWebsite: string;
  ticketPriceDescription: string;
  transportation: string;
  tips: string;
  recommendationReason: string;
  status: number;
  isFree: boolean;
  isHot: boolean;
  isNew: boolean;
  isTop: boolean;
  weight: number;
}

// =========================
// 基础状态（服务端分页：pageNum / pageSize / pages / total）
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

// 详情视图当前记录（含表单外的展示字段）
const currentRecord = ref<any>(null);

function emptyForm(): RecommendFormState {
  return {
    name: '',
    englishName: '',
    categoryId: undefined,
    categoryName: '',
    country: '',
    province: '',
    city: '',
    district: '',
    address: '',
    summary: '',
    content: '',
    coverImageUrl: '',
    tags: '',
    highlights: '',
    minPrice: undefined,
    maxPrice: undefined,
    priceCurrency: 'CNY',
    season: '',
    bestTravelTime: '',
    suitableCrowd: '',
    visitDuration: '',
    openingHoursWeekday: '',
    openingHoursWeekend: '',
    contactPhone: '',
    officialWebsite: '',
    ticketPriceDescription: '',
    transportation: '',
    tips: '',
    recommendationReason: '',
    status: 1,
    isFree: false,
    isHot: false,
    isNew: false,
    isTop: false,
    weight: 0,
  };
}

const formData = reactive<RecommendFormState>(emptyForm());

const rules = computed(() => ({
  name: [
    {
      required: true,
      message: $t('travel.recommend_list.form.name_required'),
      trigger: 'blur',
    },
  ],
}));

const statusOptions = computed(() => [
  { label: $t('travel.recommend_list.status.published'), value: 1 },
  { label: $t('travel.recommend_list.status.unpublished'), value: 0 },
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
/** 分隔符拆分（兼容中英文分号 / 逗号，支持数组直传） */
function splitList(value: any, sep: RegExp | string): string[] {
  if (Array.isArray(value)) return value.map((v) => String(v)).filter(Boolean);
  if (typeof value !== 'string' || !value.trim()) return [];
  return value
    .split(sep)
    .map((s) => s.trim())
    .filter(Boolean);
}

/** imageUrls / socialMedia 为 JSON 字符串，安全解析 */
function parseJsonArray(value: any): string[] {
  if (Array.isArray(value)) return value.map((v) => String(v));
  if (typeof value !== 'string' || !value.trim()) return [];
  try {
    const parsed = JSON.parse(value);
    return Array.isArray(parsed) ? parsed.map((v) => String(v)) : [];
  } catch {
    return [];
  }
}

function parseJsonObject(value: any): null | Record<string, any> {
  if (value && typeof value === 'object' && !Array.isArray(value))
    return value;
  if (typeof value !== 'string' || !value.trim()) return null;
  try {
    const parsed = JSON.parse(value);
    return parsed && typeof parsed === 'object' && !Array.isArray(parsed)
      ? parsed
      : null;
  } catch {
    return null;
  }
}

/** 大数字千分位展示 */
function formatCount(value: any): string {
  const n = Number(value);
  return Number.isFinite(n) ? n.toLocaleString('en-US') : '0';
}

function formatDateTime(value: null | number | string | undefined): string {
  if (value === null || value === undefined || value === '') return '-';
  if (typeof value === 'number')
    return dayjs(value).format('YYYY-MM-DD HH:mm:ss');
  const parsed = dayjs(value);
  return parsed.isValid()
    ? parsed.format('YYYY-MM-DD HH:mm:ss')
    : String(value);
}

function statusText(status: any): string {
  return String(status) === '1'
    ? $t('travel.recommend_list.status.published')
    : $t('travel.recommend_list.status.unpublished');
}

/** 价格区间文案：免费 / ¥min ~ max */
function priceText(rec: any): string {
  if (rec.isFree) return $t('travel.recommend_list.free');
  const currency = rec.priceCurrency === 'CNY' ? '¥' : rec.priceCurrency || '';
  if (rec.minPrice && rec.maxPrice && rec.minPrice !== rec.maxPrice) {
    return `${currency}${rec.minPrice} ~ ${currency}${rec.maxPrice}`;
  }
  const single = rec.maxPrice ?? rec.minPrice;
  return single ? `${currency}${single}` : '-';
}

function locationText(rec: any): string {
  return [rec.province, rec.city, rec.district, rec.address]
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
    const res: any = await getHomeRecommendationsList({
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
    errorMsg.value = error?.message || $t('travel.recommend_list.error_desc');
  } finally {
    loading.value = false;
  }
}

/**
 * 行数据规范化：
 * - coverImageUrl 补全域名
 * - tags / festival / highlights / facilityInfo 拆成数组
 * - imageUrls / socialMedia（JSON 字符串）安全解析
 */
function normalizeList(records: any[]): any[] {
  return (records ?? []).map((raw) => {
    const row: any = { ...(raw ?? {}) };
    row.coverImageUrl = normalizeImageUrl(row.coverImageUrl);
    row.tagList = splitList(row.tags, ',');
    row.festivalList = splitList(row.festival, ',');
    row.highlightList = splitList(row.highlights, /[;；]/);
    row.facilityList = splitList(row.facilityInfo, /[;；]/);
    row.imageList = parseJsonArray(row.imageUrls).map((u) =>
      normalizeImageUrl(u),
    );
    const social = parseJsonObject(row.socialMedia);
    row.socialList = social
      ? Object.entries(social).map(([k, v]) => ({ key: k, value: String(v) }))
      : [];
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
  showTotal: (t: number) => $t('travel.recommend_list.total', { total: t }),
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
    title: $t('travel.recommend_list.fields.id'),
    dataIndex: 'id',
    width: 80,
    fixed: 'left',
  },
  {
    title: $t('travel.recommend_list.fields.coverImageUrl'),
    dataIndex: 'coverImageUrl',
    width: 110,
    fixed: 'left',
  },
  {
    title: $t('travel.recommend_list.fields.name'),
    dataIndex: 'name',
    width: 200,
    fixed: 'left',
    ellipsis: true,
  },
  {
    title: $t('travel.recommend_list.fields.category'),
    dataIndex: 'categoryName',
    width: 110,
    ellipsis: true,
  },
  {
    title: $t('travel.recommend_list.fields.location'),
    key: 'location',
    width: 240,
    ellipsis: true,
  },
  {
    title: $t('travel.recommend_list.fields.rating'),
    dataIndex: 'averageRating',
    width: 90,
  },
  {
    title: $t('travel.recommend_list.fields.price'),
    key: 'price',
    width: 160,
  },
  {
    title: $t('travel.recommend_list.fields.tags'),
    key: 'tags',
    width: 240,
    ellipsis: true,
  },
  {
    title: $t('travel.recommend_list.fields.status'),
    dataIndex: 'status',
    width: 90,
  },
  {
    title: $t('travel.recommend_list.fields.flags'),
    key: 'flags',
    width: 170,
  },
  {
    title: $t('travel.recommend_list.fields.viewCount'),
    dataIndex: 'viewCount',
    width: 100,
  },
  {
    title: $t('travel.recommend_list.fields.weight'),
    dataIndex: 'weight',
    width: 80,
  },
  {
    title: $t('travel.recommend_list.fields.createdAt'),
    dataIndex: 'createdAt',
    width: 180,
  },
  {
    title: $t('travel.recommend_list.action.operation'),
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
 * （tags / highlights 保持原始分隔符字符串便于编辑）
 */
function recordToForm(rec: any): RecommendFormState {
  return {
    id: rec.id,
    uuid: rec.uuid ?? null,
    name: String(rec.name ?? ''),
    englishName: String(rec.englishName ?? ''),
    categoryId:
      rec.categoryId === null || rec.categoryId === undefined
        ? undefined
        : Number(rec.categoryId),
    categoryName: String(rec.categoryName ?? ''),
    country: String(rec.country ?? ''),
    province: String(rec.province ?? ''),
    city: String(rec.city ?? ''),
    district: String(rec.district ?? ''),
    address: String(rec.address ?? ''),
    summary: String(rec.summary ?? ''),
    content: String(rec.content ?? ''),
    coverImageUrl: normalizeImageUrl(rec.coverImageUrl),
    tags: String(rec.tags ?? ''),
    highlights: String(rec.highlights ?? ''),
    minPrice:
      rec.minPrice === null || rec.minPrice === undefined
        ? undefined
        : Number(rec.minPrice),
    maxPrice:
      rec.maxPrice === null || rec.maxPrice === undefined
        ? undefined
        : Number(rec.maxPrice),
    priceCurrency: String(rec.priceCurrency ?? 'CNY'),
    season: String(rec.season ?? ''),
    bestTravelTime: String(rec.bestTravelTime ?? ''),
    suitableCrowd: String(rec.suitableCrowd ?? ''),
    visitDuration: String(rec.visitDuration ?? ''),
    openingHoursWeekday: String(rec.openingHoursWeekday ?? ''),
    openingHoursWeekend: String(rec.openingHoursWeekend ?? ''),
    contactPhone: String(rec.contactPhone ?? ''),
    officialWebsite: String(rec.officialWebsite ?? ''),
    ticketPriceDescription: String(rec.ticketPriceDescription ?? ''),
    transportation: String(rec.transportation ?? ''),
    tips: String(rec.tips ?? ''),
    recommendationReason: String(rec.recommendationReason ?? ''),
    status: Number(rec.status ?? 1),
    isFree: Boolean(rec.isFree),
    isHot: Boolean(rec.isHot),
    isNew: Boolean(rec.isNew),
    isTop: Boolean(rec.isTop),
    weight: Number(rec.weight ?? 0),
  };
}

// 表单内封面预览 URL（实时规范化）
const formCoverDisplayUrl = computed(() =>
  normalizeImageUrl(formData.coverImageUrl),
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
    return $t('travel.recommend_list.modal.create_title');
  if (modalMode.value === 'edit')
    return $t('travel.recommend_list.modal.edit_title');
  return $t('travel.recommend_list.modal.detail_title');
});

// =========================
// 封面图片上传
// =========================
async function handleCoverUpload(file: File) {
  if (!file) return;
  if (!file.type.startsWith('image/')) {
    message.error($t('travel.recommend_list.form.cover_invalid_image'));
    return;
  }
  coverUploading.value = true;
  try {
    const res: any = await uploadFile(file);
    const relativePath =
      res?.url ?? res?.path ?? res?.filePath ?? res?.data?.url ?? '';
    if (!relativePath) {
      message.error($t('travel.recommend_list.form.cover_upload_failed'));
      return;
    }
    formData.coverImageUrl = String(relativePath);
    message.success($t('travel.recommend_list.form.cover_upload_success'));
  } catch (error: any) {
    const msg =
      error?.message ||
      $t('travel.recommend_list.form.cover_upload_failed');
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
    const payload: any = {
      name: formData.name,
      englishName: formData.englishName || null,
      categoryId: formData.categoryId ?? null,
      categoryName: formData.categoryName || null,
      country: formData.country || null,
      province: formData.province || null,
      city: formData.city || null,
      district: formData.district || null,
      address: formData.address || null,
      summary: formData.summary || null,
      content: formData.content || null,
      coverImageUrl: formData.coverImageUrl || null,
      tags: formData.tags || null,
      highlights: formData.highlights || null,
      minPrice: formData.minPrice ?? null,
      maxPrice: formData.maxPrice ?? null,
      priceCurrency: formData.priceCurrency || 'CNY',
      season: formData.season || null,
      bestTravelTime: formData.bestTravelTime || null,
      suitableCrowd: formData.suitableCrowd || null,
      visitDuration: formData.visitDuration || null,
      openingHoursWeekday: formData.openingHoursWeekday || null,
      openingHoursWeekend: formData.openingHoursWeekend || null,
      contactPhone: formData.contactPhone || null,
      officialWebsite: formData.officialWebsite || null,
      ticketPriceDescription: formData.ticketPriceDescription || null,
      transportation: formData.transportation || null,
      tips: formData.tips || null,
      recommendationReason: formData.recommendationReason || null,
      status: Number(formData.status),
      isFree: formData.isFree,
      isHot: formData.isHot,
      isNew: formData.isNew,
      isTop: formData.isTop,
      weight: Number(formData.weight) || 0,
    };
    if (formData.id !== undefined && formData.id !== null) {
      payload.id = formData.id;
      if (formData.uuid) payload.uuid = formData.uuid;
      await updateHomeRecommendation(payload);
      message.success($t('travel.recommend_list.action.update_success'));
    } else {
      await addHomeRecommendation(payload);
      message.success($t('travel.recommend_list.action.create_success'));
    }
    closeModal();
    pageNum.value = 1;
    await fetchData();
  } catch (err: any) {
    const msg = err?.message || err?.msg || String(err || '');
    message.error(
      modalMode.value === 'create'
        ? msg || $t('travel.recommend_list.action.create_failed')
        : msg || $t('travel.recommend_list.action.update_failed'),
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
    await deleteHomeRecommendation(record.id);
    message.success($t('travel.recommend_list.action.delete_success'));
    if (dataSource.value.length === 1 && pageNum.value > 1) {
      pageNum.value -= 1;
    }
    await fetchData();
  } catch (err: any) {
    const msg =
      err?.message ||
      err?.msg ||
      $t('travel.recommend_list.action.delete_failed');
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
          :title="$t('travel.recommend_list.error_title')"
          :sub-title="errorMsg"
        >
          <template #extra>
            <Button type="primary" @click="fetchData">
              {{ $t('travel.recommend_list.retry') }}
            </Button>
          </template>
        </Result>
      </Card>

      <!-- 推荐列表 -->
      <Card
        v-else
        :title="$t('travel.recommend_list.title')"
        :styles="{ body: { padding: '0' } }"
      >
        <template #extra>
          <Button type="primary" :loading="loading" @click="openCreate">
            <template #icon>
              <span class="i-lucide-plus mr-1 size-4 align-middle"></span>
            </template>
            {{ $t('travel.recommend_list.action.add') }}
          </Button>
        </template>

        <Table
          :columns="columns"
          :data-source="dataSource"
          :pagination="pagination"
          :loading="loading"
          :scroll="{ x: 2160 }"
          :bordered="true"
          size="middle"
          row-key="id"
          @change="handleTableChange"
        >
          <template #bodyCell="{ column, record }">
            <!-- 封面缩略图（圆形） -->
            <template v-if="column.dataIndex === 'coverImageUrl'">
              <div class="cover-cell">
                <img
                  v-if="record.coverImageUrl && !isCoverFailed(record.id)"
                  :src="record.coverImageUrl"
                  :alt="record.name"
                  loading="lazy"
                  class="cover-thumb"
                  @error="handleCoverError(record.id)"
                />
                <span v-else class="cover-fallback">
                  {{ $t('travel.recommend_list.no_cover') }}
                </span>
              </div>
            </template>

            <!-- 所在地 -->
            <template v-else-if="column.key === 'location'">
              <span class="inline-flex items-start gap-1">
                <span
                  class="i-lucide-map-pin size-4 flex-shrink-0 text-muted-foreground"
                ></span>
                <span class="text-sm">{{ locationText(record) || '-' }}</span>
              </span>
            </template>

            <!-- 评分 -->
            <template v-else-if="column.dataIndex === 'averageRating'">
              <Tag color="gold">{{ record.averageRating ?? '-' }}</Tag>
            </template>

            <!-- 价格区间 -->
            <template v-else-if="column.key === 'price'">
              <Tag v-if="record.isFree" color="green">
                {{ $t('travel.recommend_list.free') }}
              </Tag>
              <span v-else class="font-semibold text-red-500">
                {{ priceText(record) }}
              </span>
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

            <!-- 状态 -->
            <template v-else-if="column.dataIndex === 'status'">
              <Tag :color="String(record.status) === '1' ? 'green' : 'default'">
                {{ statusText(record.status) }}
              </Tag>
            </template>

            <!-- 标记：热门 / 置顶 / 新上 -->
            <template v-else-if="column.key === 'flags'">
              <span class="inline-flex flex-wrap gap-1">
                <Tag v-if="record.isTop" color="gold">
                  {{ $t('travel.recommend_list.badges.top') }}
                </Tag>
                <Tag v-if="record.isHot" color="red">
                  {{ $t('travel.recommend_list.badges.hot') }}
                </Tag>
                <Tag v-if="record.isNew" color="blue">
                  {{ $t('travel.recommend_list.badges.new') }}
                </Tag>
                <span
                  v-if="!record.isTop && !record.isHot && !record.isNew"
                  class="text-xs text-muted-foreground"
                >
                  -
                </span>
              </span>
            </template>

            <!-- 浏览量 -->
            <template v-else-if="column.dataIndex === 'viewCount'">
              <span class="inline-flex items-center gap-1 text-sm">
                <span class="i-lucide-eye size-3.5 text-muted-foreground"></span>
                {{ formatCount(record.viewCount) }}
              </span>
            </template>

            <!-- 创建时间 -->
            <template v-else-if="column.dataIndex === 'createdAt'">
              {{ formatDateTime(record.createdAt) }}
            </template>

            <!-- 操作列：详情 / 修改 / 删除 -->
            <template v-else-if="column.key === 'actions'">
              <Space :size="4" wrap>
                <Button type="link" size="small" @click="openDetail(record)">
                  {{ $t('travel.recommend_list.action.detail') }}
                </Button>
                <Button type="link" size="small" @click="openEdit(record)">
                  {{ $t('travel.recommend_list.action.edit') }}
                </Button>
                <Popconfirm
                  :title="$t('travel.recommend_list.action.confirm_delete_title')"
                  :description="$t('travel.recommend_list.action.confirm_delete')"
                  :ok-text="$t('travel.recommend_list.action.delete_text')"
                  :cancel-text="$t('travel.recommend_list.action.cancel')"
                  :ok-button-props="{ danger: true }"
                  @confirm="handleDelete(record)"
                >
                  <Button
                    type="link"
                    size="small"
                    danger
                    :loading="deletingId === record.id"
                  >
                    {{ $t('travel.recommend_list.action.delete') }}
                  </Button>
                </Popconfirm>
              </Space>
            </template>
          </template>

          <template #emptyText>
            <Empty :description="$t('travel.recommend_list.empty')" />
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
      :ok-text="$t('travel.recommend_list.action.save')"
      :cancel-text="$t('travel.recommend_list.action.cancel')"
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
        <!-- 头部：封面 + 名称 + 标记 + 标签 + 互动数据 -->
        <div class="mb-4 flex items-start gap-4">
          <Avatar
            v-if="formCoverDisplayUrl"
            :src="formCoverDisplayUrl"
            :size="96"
            shape="circle"
            class="border border-border overflow-hidden object-cover flex-shrink-0"
          />
          <div v-else class="cover-placeholder-circle">
            <span class="i-lucide-image-off size-8 text-muted-foreground"></span>
          </div>
          <div class="min-w-0">
            <div class="text-xl font-semibold truncate">
              {{ formData.name }}
              <span
                v-if="formData.englishName"
                class="ml-1 text-sm font-normal text-muted-foreground"
              >
                {{ formData.englishName }}
              </span>
            </div>
            <Space class="mt-1" :size="6">
              <Tag color="gold">{{ formData.bestTravelTime || formData.season || '' }}</Tag>
              <Tag :color="String(formData.status) === '1' ? 'green' : 'default'">
                {{ statusText(formData.status) }}
              </Tag>
              <Tag v-if="formData.isTop" color="gold">
                {{ $t('travel.recommend_list.badges.top') }}
              </Tag>
              <Tag v-if="formData.isHot" color="red">
                {{ $t('travel.recommend_list.badges.hot') }}
              </Tag>
              <Tag v-if="formData.isNew" color="blue">
                {{ $t('travel.recommend_list.badges.new') }}
              </Tag>
              <Tag v-if="formData.isFree" color="green">
                {{ $t('travel.recommend_list.free') }}
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
                <span class="i-lucide-eye mr-1 align-middle"></span>
                {{ formatCount(currentRecord.viewCount) }}
              </span>
              <span>
                <span class="i-lucide-heart mr-1 align-middle"></span>
                {{ formatCount(currentRecord.likeCount) }}
              </span>
              <span>
                <span class="i-lucide-message-circle mr-1 align-middle"></span>
                {{ formatCount(currentRecord.commentCount) }}
              </span>
              <span>
                <span class="i-lucide-share-2 mr-1 align-middle"></span>
                {{ formatCount(currentRecord.shareCount) }}
              </span>
            </div>
          </div>
        </div>

        <Divider class="my-4">
          {{ $t('travel.recommend_list.detail.basic_info') }}
        </Divider>
        <Descriptions :column="2" bordered size="small">
          <Descriptions.Item :label="$t('travel.recommend_list.fields.id')">
            {{ (formData as any).id ?? '-' }}
          </Descriptions.Item>
          <Descriptions.Item
            :label="$t('travel.recommend_list.fields.category')"
          >
            {{ formData.categoryName || '-' }}
          </Descriptions.Item>
          <Descriptions.Item
            :label="$t('travel.recommend_list.detail.summary')"
            :span="2"
          >
            {{ formData.summary || $t('travel.recommend_list.detail.no_data') }}
          </Descriptions.Item>
          <Descriptions.Item
            :label="$t('travel.recommend_list.detail.reason_info')"
            :span="2"
          >
            {{ formData.recommendationReason || $t('travel.recommend_list.detail.no_data') }}
          </Descriptions.Item>
        </Descriptions>

        <Divider class="my-4">
          {{ $t('travel.recommend_list.detail.location_info') }}
        </Divider>
        <Descriptions :column="2" bordered size="small">
          <Descriptions.Item
            :label="$t('travel.recommend_list.form.country_label')"
          >
            {{ formData.country || '-' }}
          </Descriptions.Item>
          <Descriptions.Item
            :label="$t('travel.recommend_list.form.province_label')"
          >
            {{ formData.province || '-' }}
          </Descriptions.Item>
          <Descriptions.Item :label="$t('travel.recommend_list.form.city_label')">
            {{ formData.city || '-' }}
          </Descriptions.Item>
          <Descriptions.Item
            :label="$t('travel.recommend_list.form.district_label')"
          >
            {{ formData.district || '-' }}
          </Descriptions.Item>
          <Descriptions.Item
            :label="$t('travel.recommend_list.form.address_label')"
            :span="2"
          >
            {{ formData.address || '-' }}
          </Descriptions.Item>
          <Descriptions.Item
            :label="$t('travel.recommend_list.detail.transportation')"
            :span="2"
          >
            {{ formData.transportation || $t('travel.recommend_list.detail.no_data') }}
          </Descriptions.Item>
        </Descriptions>

        <Divider class="my-4">
          {{ $t('travel.recommend_list.detail.rating_price_info') }}
        </Divider>
        <Descriptions :column="2" bordered size="small">
          <Descriptions.Item :label="$t('travel.recommend_list.fields.rating')">
            <span class="i-lucide-star mr-1 inline-block align-middle text-yellow-500"></span>
            {{ currentRecord.averageRating ?? '-' }}
            <span class="ml-1 text-xs text-muted-foreground">
              ({{ formatCount(currentRecord.ratingCount) }})
            </span>
          </Descriptions.Item>
          <Descriptions.Item :label="$t('travel.recommend_list.fields.price')">
            {{ priceText(currentRecord) }}
          </Descriptions.Item>
          <Descriptions.Item
            :label="$t('travel.recommend_list.detail.ticket_desc')"
            :span="2"
          >
            {{ formData.ticketPriceDescription || $t('travel.recommend_list.detail.no_data') }}
          </Descriptions.Item>
        </Descriptions>

        <Divider class="my-4">
          {{ $t('travel.recommend_list.detail.open_info') }}
        </Divider>
        <Descriptions :column="2" bordered size="small">
          <Descriptions.Item
            :label="$t('travel.recommend_list.detail.weekday_hours')"
          >
            {{ formData.openingHoursWeekday || '-' }}
          </Descriptions.Item>
          <Descriptions.Item
            :label="$t('travel.recommend_list.detail.weekend_hours')"
          >
            {{ formData.openingHoursWeekend || '-' }}
          </Descriptions.Item>
          <Descriptions.Item
            :label="$t('travel.recommend_list.detail.best_travel_time')"
          >
            {{ formData.bestTravelTime || '-' }}
          </Descriptions.Item>
          <Descriptions.Item
            :label="$t('travel.recommend_list.detail.visit_duration')"
          >
            {{ formData.visitDuration || '-' }}
          </Descriptions.Item>
          <Descriptions.Item
            :label="$t('travel.recommend_list.detail.suitable_crowd')"
            :span="2"
          >
            <span
              v-if="formData.suitableCrowd"
              class="inline-flex flex-wrap gap-1"
            >
              <Tag
                v-for="c in splitList(formData.suitableCrowd, ',')"
                :key="c"
                color="cyan"
              >
                {{ c }}
              </Tag>
            </span>
            <span v-else>-</span>
          </Descriptions.Item>
        </Descriptions>

        <Divider class="my-4">
          {{ $t('travel.recommend_list.detail.highlights_info') }}
        </Divider>
        <div
          v-if="currentRecord.highlightList?.length"
          class="flex flex-wrap gap-1"
        >
          <Tag
            v-for="h in currentRecord.highlightList"
            :key="h"
            color="geekblue"
          >
            {{ h }}
          </Tag>
        </div>
        <span v-else class="text-xs text-muted-foreground">
          {{ $t('travel.recommend_list.detail.no_data') }}
        </span>

        <Divider class="my-4">
          {{ $t('travel.recommend_list.detail.facilities_info') }}
        </Divider>
        <div
          v-if="currentRecord.facilityList?.length"
          class="flex flex-wrap gap-1"
        >
          <Tag v-for="f in currentRecord.facilityList" :key="f" color="blue">
            {{ f }}
          </Tag>
        </div>
        <span v-else class="text-xs text-muted-foreground">
          {{ $t('travel.recommend_list.detail.no_data') }}
        </span>

        <Divider class="my-4">
          {{ $t('travel.recommend_list.detail.festival_info') }}
        </Divider>
        <div
          v-if="currentRecord.festivalList?.length"
          class="flex flex-wrap gap-1"
        >
          <Tag v-for="f in currentRecord.festivalList" :key="f" color="purple">
            {{ f }}
          </Tag>
        </div>
        <span v-else class="text-xs text-muted-foreground">
          {{ $t('travel.recommend_list.detail.no_data') }}
        </span>

        <Divider class="my-4">
          {{ $t('travel.recommend_list.detail.tips_info') }}
        </Divider>
        <p class="m-0 whitespace-pre-wrap text-sm leading-6 text-muted-foreground">
          {{ formData.tips || $t('travel.recommend_list.detail.no_data') }}
        </p>

        <Divider class="my-4">
          {{ $t('travel.recommend_list.detail.content_info') }}
        </Divider>
        <p class="m-0 whitespace-pre-wrap text-sm leading-6 text-muted-foreground">
          {{ formData.content || $t('travel.recommend_list.detail.no_data') }}
        </p>

        <Divider class="my-4">
          {{ $t('travel.recommend_list.detail.contact_info') }}
        </Divider>
        <Descriptions :column="2" bordered size="small">
          <Descriptions.Item
            :label="$t('travel.recommend_list.form.phone_label')"
          >
            <span class="inline-flex items-center gap-1">
              <span class="i-lucide-phone size-3.5"></span>
              {{ formData.contactPhone || '-' }}
            </span>
          </Descriptions.Item>
          <Descriptions.Item
            :label="$t('travel.recommend_list.form.website_label')"
          >
            <a
              v-if="formData.officialWebsite"
              :href="formData.officialWebsite"
              target="_blank"
              rel="noopener"
              class="inline-flex items-center gap-1"
            >
              <span class="i-lucide-external-link size-3.5"></span>
              {{ formData.officialWebsite }}
            </a>
            <span v-else>-</span>
          </Descriptions.Item>
        </Descriptions>

        <Divider class="my-4">
          {{ $t('travel.recommend_list.detail.media_info') }}
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
        <span v-else class="mr-3 text-xs text-muted-foreground">
          {{ $t('travel.recommend_list.detail.no_data') }}
        </span>
        <a
          v-if="currentRecord.videoUrl"
          :href="currentRecord.videoUrl"
          target="_blank"
          rel="noopener"
          class="inline-flex items-center gap-1 text-sm"
        >
          <span class="i-lucide-video size-4"></span>
          {{ currentRecord.videoUrl }}
        </a>

        <template v-if="currentRecord.socialList?.length">
          <Divider class="my-4">
            {{ $t('travel.recommend_list.detail.social_info') }}
          </Divider>
          <Descriptions :column="1" bordered size="small">
            <Descriptions.Item
              v-for="s in currentRecord.socialList"
              :key="s.key"
              :label="s.key"
            >
              <span class="inline-flex items-center gap-1">
                <span class="i-lucide-globe size-3.5"></span>
                {{ s.value }}
              </span>
            </Descriptions.Item>
          </Descriptions>
        </template>

        <Divider class="my-4">
          {{ $t('travel.recommend_list.detail.seo_info') }}
        </Divider>
        <Descriptions :column="1" bordered size="small">
          <Descriptions.Item label="SEO Title">
            {{ currentRecord.seoTitle || '-' }}
          </Descriptions.Item>
          <Descriptions.Item label="SEO Keywords">
            {{ currentRecord.seoKeywords || '-' }}
          </Descriptions.Item>
          <Descriptions.Item label="SEO Description">
            {{ currentRecord.seoDescription || '-' }}
          </Descriptions.Item>
        </Descriptions>

        <Divider class="my-4">
          {{ $t('travel.recommend_list.detail.time_info') }}
        </Divider>
        <Descriptions :column="2" bordered size="small">
          <Descriptions.Item
            :label="$t('travel.recommend_list.fields.publishedAt')"
          >
            {{ formatDateTime(currentRecord.publishedAt) }}
          </Descriptions.Item>
          <Descriptions.Item
            :label="$t('travel.recommend_list.fields.createdAt')"
          >
            {{ formatDateTime(currentRecord.createdAt) }}
          </Descriptions.Item>
          <Descriptions.Item
            :label="$t('travel.recommend_list.fields.updatedAt')"
          >
            {{ formatDateTime(currentRecord.updatedAt) }}
          </Descriptions.Item>
          <Descriptions.Item :label="$t('travel.recommend_list.fields.weight')">
            {{ currentRecord.weight ?? '-' }}
          </Descriptions.Item>
        </Descriptions>
      </div>

      <!-- 创建 / 修改：Form -->
      <div
        v-if="modalMode === 'create' || modalMode === 'edit'"
        class="recommend-form-wrapper"
        style="display: block; width: 100%;"
      >
        <!-- 编辑模式：展示 ID -->
        <div v-if="modalMode === 'edit' && (formData as any).id" class="mb-4">
          <Descriptions :column="2" size="small" bordered>
            <Descriptions.Item
              :label="$t('travel.recommend_list.fields.id')"
            >
              {{ (formData as any).id }}
            </Descriptions.Item>
            <Descriptions.Item
              :label="$t('travel.recommend_list.fields.uuid')"
            >
              {{ (formData as any).uuid || '-' }}
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
              :label="$t('travel.recommend_list.form.name_label')"
              name="name"
              prop="name"
              style="display: block; width: 100%; margin-bottom: 16px;"
            >
              <Input
                v-model:value="formData.name"
                class="w-full"
                :placeholder="$t('travel.recommend_list.form.name_required')"
              />
            </FormItem>

            <FormItem
              :label="$t('travel.recommend_list.form.english_name_label')"
              name="englishName"
              prop="englishName"
              style="display: block; width: 100%; margin-bottom: 16px;"
            >
              <Input
                v-model:value="formData.englishName"
                class="w-full"
                :placeholder="$t('travel.recommend_list.form.english_name_label')"
              />
            </FormItem>

            <FormItem
              :label="$t('travel.recommend_list.form.category_id_label')"
              name="categoryId"
              prop="categoryId"
              style="display: block; width: 100%; margin-bottom: 16px;"
            >
              <InputNumber
                v-model:value="formData.categoryId"
                class="w-full"
                :min="0"
                :placeholder="$t('travel.recommend_list.form.category_id_label')"
              />
            </FormItem>

            <FormItem
              :label="$t('travel.recommend_list.form.category_name_label')"
              name="categoryName"
              prop="categoryName"
              style="display: block; width: 100%; margin-bottom: 16px;"
            >
              <Input
                v-model:value="formData.categoryName"
                class="w-full"
                :placeholder="$t('travel.recommend_list.form.category_name_label')"
              />
            </FormItem>

            <FormItem
              :label="$t('travel.recommend_list.form.country_label')"
              name="country"
              prop="country"
              style="display: block; width: 100%; margin-bottom: 16px;"
            >
              <Input
                v-model:value="formData.country"
                class="w-full"
                :placeholder="$t('travel.recommend_list.form.country_label')"
              />
            </FormItem>

            <FormItem
              :label="$t('travel.recommend_list.form.province_label')"
              name="province"
              prop="province"
              style="display: block; width: 100%; margin-bottom: 16px;"
            >
              <Input
                v-model:value="formData.province"
                class="w-full"
                :placeholder="$t('travel.recommend_list.form.province_label')"
              />
            </FormItem>

            <FormItem
              :label="$t('travel.recommend_list.form.city_label')"
              name="city"
              prop="city"
              style="display: block; width: 100%; margin-bottom: 16px;"
            >
              <Input
                v-model:value="formData.city"
                class="w-full"
                :placeholder="$t('travel.recommend_list.form.city_label')"
              />
            </FormItem>

            <FormItem
              :label="$t('travel.recommend_list.form.district_label')"
              name="district"
              prop="district"
              style="display: block; width: 100%; margin-bottom: 16px;"
            >
              <Input
                v-model:value="formData.district"
                class="w-full"
                :placeholder="$t('travel.recommend_list.form.district_label')"
              />
            </FormItem>

            <!-- 地址：全宽 -->
            <div class="col-span-2" style="width: 100%;">
              <FormItem
                :label="$t('travel.recommend_list.form.address_label')"
                name="address"
                prop="address"
                style="display: block; width: 100%; margin-bottom: 16px;"
              >
                <Input
                  v-model:value="formData.address"
                  class="w-full"
                  :placeholder="$t('travel.recommend_list.form.address_label')"
                />
              </FormItem>
            </div>

            <!-- 简介：全宽 -->
            <div class="col-span-2" style="width: 100%;">
              <FormItem
                :label="$t('travel.recommend_list.form.summary_label')"
                name="summary"
                prop="summary"
                style="display: block; width: 100%; margin-bottom: 16px;"
              >
                <Input
                  v-model:value="formData.summary"
                  class="w-full"
                  :placeholder="$t('travel.recommend_list.form.summary_label')"
                />
              </FormItem>
            </div>

            <FormItem
              :label="$t('travel.recommend_list.form.min_price_label')"
              name="minPrice"
              prop="minPrice"
              style="display: block; width: 100%; margin-bottom: 16px;"
            >
              <InputNumber
                v-model:value="formData.minPrice"
                class="w-full"
                :min="0"
                :placeholder="$t('travel.recommend_list.form.min_price_label')"
              />
            </FormItem>

            <FormItem
              :label="$t('travel.recommend_list.form.max_price_label')"
              name="maxPrice"
              prop="maxPrice"
              style="display: block; width: 100%; margin-bottom: 16px;"
            >
              <InputNumber
                v-model:value="formData.maxPrice"
                class="w-full"
                :min="0"
                :placeholder="$t('travel.recommend_list.form.max_price_label')"
              />
            </FormItem>

            <FormItem
              :label="$t('travel.recommend_list.form.currency_label')"
              name="priceCurrency"
              prop="priceCurrency"
              style="display: block; width: 100%; margin-bottom: 16px;"
            >
              <Input
                v-model:value="formData.priceCurrency"
                class="w-full"
                :placeholder="$t('travel.recommend_list.form.currency_label')"
              />
            </FormItem>

            <FormItem
              :label="$t('travel.recommend_list.form.status_label')"
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
              :label="$t('travel.recommend_list.form.weight_label')"
              name="weight"
              prop="weight"
              style="display: block; width: 100%; margin-bottom: 16px;"
            >
              <InputNumber
                v-model:value="formData.weight"
                class="w-full"
                :min="0"
                :placeholder="$t('travel.recommend_list.form.weight_label')"
              />
            </FormItem>

            <!-- 开关组：免费 / 热门 / 置顶 / 新上 -->
            <div
              class="col-span-2 grid grid-cols-2 gap-x-6 md:grid-cols-4"
              style="display: grid; width: 100%;"
            >
              <FormItem
                :label="$t('travel.recommend_list.form.is_free_label')"
                style="display: block; width: 100%; margin-bottom: 16px;"
              >
                <Switch v-model:checked="formData.isFree" />
              </FormItem>
              <FormItem
                :label="$t('travel.recommend_list.form.is_hot_label')"
                style="display: block; width: 100%; margin-bottom: 16px;"
              >
                <Switch v-model:checked="formData.isHot" />
              </FormItem>
              <FormItem
                :label="$t('travel.recommend_list.form.is_top_label')"
                style="display: block; width: 100%; margin-bottom: 16px;"
              >
                <Switch v-model:checked="formData.isTop" />
              </FormItem>
              <FormItem
                :label="$t('travel.recommend_list.form.is_new_label')"
                style="display: block; width: 100%; margin-bottom: 16px;"
              >
                <Switch v-model:checked="formData.isNew" />
              </FormItem>
            </div>

            <FormItem
              :label="$t('travel.recommend_list.form.season_label')"
              name="season"
              prop="season"
              style="display: block; width: 100%; margin-bottom: 16px;"
            >
              <Input
                v-model:value="formData.season"
                class="w-full"
                :placeholder="$t('travel.recommend_list.form.season_label')"
              />
            </FormItem>

            <FormItem
              :label="$t('travel.recommend_list.form.best_time_label')"
              name="bestTravelTime"
              prop="bestTravelTime"
              style="display: block; width: 100%; margin-bottom: 16px;"
            >
              <Input
                v-model:value="formData.bestTravelTime"
                class="w-full"
                :placeholder="$t('travel.recommend_list.form.best_time_label')"
              />
            </FormItem>

            <FormItem
              :label="$t('travel.recommend_list.form.crowd_label')"
              name="suitableCrowd"
              prop="suitableCrowd"
              :extra="$t('travel.recommend_list.form.tags_tip')"
              style="display: block; width: 100%; margin-bottom: 16px;"
            >
              <Input
                v-model:value="formData.suitableCrowd"
                class="w-full"
                :placeholder="$t('travel.recommend_list.form.crowd_label')"
              />
            </FormItem>

            <FormItem
              :label="$t('travel.recommend_list.form.duration_label')"
              name="visitDuration"
              prop="visitDuration"
              style="display: block; width: 100%; margin-bottom: 16px;"
            >
              <Input
                v-model:value="formData.visitDuration"
                class="w-full"
                :placeholder="$t('travel.recommend_list.form.duration_label')"
              />
            </FormItem>

            <FormItem
              :label="$t('travel.recommend_list.form.weekday_label')"
              name="openingHoursWeekday"
              prop="openingHoursWeekday"
              style="display: block; width: 100%; margin-bottom: 16px;"
            >
              <Input
                v-model:value="formData.openingHoursWeekday"
                class="w-full"
                placeholder="09:00-20:30"
              />
            </FormItem>

            <FormItem
              :label="$t('travel.recommend_list.form.weekend_label')"
              name="openingHoursWeekend"
              prop="openingHoursWeekend"
              style="display: block; width: 100%; margin-bottom: 16px;"
            >
              <Input
                v-model:value="formData.openingHoursWeekend"
                class="w-full"
                placeholder="09:00-21:00"
              />
            </FormItem>

            <FormItem
              :label="$t('travel.recommend_list.form.phone_label')"
              name="contactPhone"
              prop="contactPhone"
              style="display: block; width: 100%; margin-bottom: 16px;"
            >
              <Input
                v-model:value="formData.contactPhone"
                class="w-full"
                :placeholder="$t('travel.recommend_list.form.phone_label')"
              />
            </FormItem>

            <FormItem
              :label="$t('travel.recommend_list.form.website_label')"
              name="officialWebsite"
              prop="officialWebsite"
              style="display: block; width: 100%; margin-bottom: 16px;"
            >
              <Input
                v-model:value="formData.officialWebsite"
                class="w-full"
                :placeholder="$t('travel.recommend_list.form.website_label')"
              />
            </FormItem>

            <!-- 标签：全宽 -->
            <div class="col-span-2" style="width: 100%;">
              <FormItem
                :label="$t('travel.recommend_list.form.tags_label')"
                name="tags"
                prop="tags"
                :extra="$t('travel.recommend_list.form.tags_tip')"
                style="display: block; width: 100%; margin-bottom: 16px;"
              >
                <Input
                  v-model:value="formData.tags"
                  class="w-full"
                  :placeholder="$t('travel.recommend_list.form.tags_label')"
                />
              </FormItem>
            </div>

            <!-- 亮点：全宽 -->
            <div class="col-span-2" style="width: 100%;">
              <FormItem
                :label="$t('travel.recommend_list.form.highlights_label')"
                name="highlights"
                prop="highlights"
                :extra="$t('travel.recommend_list.form.highlights_tip')"
                style="display: block; width: 100%; margin-bottom: 16px;"
              >
                <Input
                  v-model:value="formData.highlights"
                  class="w-full"
                  :placeholder="$t('travel.recommend_list.form.highlights_label')"
                />
              </FormItem>
            </div>

            <!-- 票价说明：全宽 -->
            <div class="col-span-2" style="width: 100%;">
              <FormItem
                :label="$t('travel.recommend_list.form.ticket_desc_label')"
                name="ticketPriceDescription"
                prop="ticketPriceDescription"
                style="display: block; width: 100%; margin-bottom: 16px;"
              >
                <Input
                  v-model:value="formData.ticketPriceDescription"
                  class="w-full"
                  :placeholder="$t('travel.recommend_list.form.ticket_desc_label')"
                />
              </FormItem>
            </div>

            <!-- 交通指南：全宽 -->
            <div class="col-span-2" style="width: 100%;">
              <FormItem
                :label="$t('travel.recommend_list.form.transport_label')"
                name="transportation"
                prop="transportation"
                style="display: block; width: 100%; margin-bottom: 16px;"
              >
                <Input
                  v-model:value="formData.transportation"
                  class="w-full"
                  :placeholder="$t('travel.recommend_list.form.transport_label')"
                />
              </FormItem>
            </div>

            <!-- 封面上传：全宽 -->
            <div class="col-span-2" style="width: 100%;">
              <FormItem
                :label="$t('travel.recommend_list.form.cover_label')"
                name="coverImageUrl"
                prop="coverImageUrl"
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
                      :alt="$t('travel.recommend_list.form.cover_label')"
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
                          formData.coverImageUrl
                            ? $t('travel.recommend_list.form.cover_change')
                            : $t('travel.recommend_list.form.cover_upload')
                        }}
                      </span>
                      <span class="text-xs text-muted-foreground">
                        {{ $t('travel.recommend_list.form.cover_tip') }}
                      </span>
                    </div>
                  </Button>
                </Upload>
              </FormItem>
            </div>

            <!-- 详情内容：大文本域，全宽 -->
            <div class="col-span-2" style="width: 100%;">
              <FormItem
                :label="$t('travel.recommend_list.form.content_label')"
                name="content"
                prop="content"
                style="display: block; width: 100%; margin-bottom: 16px;"
              >
                <Input.TextArea
                  v-model:value="formData.content"
                  :rows="4"
                  class="w-full"
                  :placeholder="$t('travel.recommend_list.form.content_label')"
                />
              </FormItem>
            </div>

            <!-- 游玩贴士：大文本域，全宽 -->
            <div class="col-span-2" style="width: 100%;">
              <FormItem
                :label="$t('travel.recommend_list.form.tips_label')"
                name="tips"
                prop="tips"
                style="display: block; width: 100%; margin-bottom: 16px;"
              >
                <Input.TextArea
                  v-model:value="formData.tips"
                  :rows="3"
                  class="w-full"
                  :placeholder="$t('travel.recommend_list.form.tips_label')"
                />
              </FormItem>
            </div>

            <!-- 推荐理由：大文本域，全宽 -->
            <div class="col-span-2" style="width: 100%;">
              <FormItem
                :label="$t('travel.recommend_list.form.reason_label')"
                name="recommendationReason"
                prop="recommendationReason"
                style="display: block; width: 100%; margin-bottom: 16px;"
              >
                <Input.TextArea
                  v-model:value="formData.recommendationReason"
                  :rows="3"
                  class="w-full"
                  :placeholder="$t('travel.recommend_list.form.reason_label')"
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
