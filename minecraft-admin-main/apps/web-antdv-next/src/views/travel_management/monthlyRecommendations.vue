<script lang="ts" setup>
import type { TableColumnsType } from 'antdv-next';

import { computed, onMounted, reactive, ref } from 'vue';

import { Page } from '@vben/common-ui';
import { Eye, ImageIcon, ImageOff, Plus } from '@vben/icons';
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
  addRecommendation,
  deleteRecommendation,
  getRecommendationsByMonth,
  getRecommendationsPage,
  updateRecommendation,
} from '#/api/management/travel/monthlyRecommendations';
import { uploadFile } from '#/api/management/user/upload';

defineOptions({ name: 'MonthlyRecommendationsManagement' });

// =========================
// 月份推荐数据类型
// =========================
interface MonthlyFormState {
  id?: number | string;
  destinationName: string;
  // 1-12
  monthId: number;
  recommendedDays?: null | number;
  // 1 推荐 / 0 未推荐
  isRecommended: number;
  sortOrder?: null | number;
  tags: string;
  imageUrl: string;
  climateInfo: string;
  seasonFeatures: string;
  travelTips: string;
  description: string;
}

// =========================
// 月份常量（monthId 1-12 → 中文名）
// =========================
const MONTH_IDS = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12];

function monthLabel(monthId: null | number | string | undefined): string {
  const idx = Number(monthId);
  if (!idx || idx < 1 || idx > 12) return '-';
  return $t(`travel.monthly_list.months.m${idx}`);
}

// =========================
// 基础状态（服务端分页 + 月份筛选）
// =========================
const loading = ref(false);
const errorMsg = ref('');
const dataSource = ref<any[]>([]);

const pageNum = ref(1);
const pageSize = ref(10);
const pages = ref(0);
const totalCount = ref(0);

// 图片加载失败的记录 id 集合
const imageErrorSet = reactive<Set<number | string>>(new Set());

// 月份筛选：'all' 或 '一月'...'十二月'
const filterMonth = ref('all');

const monthFilterOptions = computed(() => [
  { value: 'all', label: $t('travel.monthly_list.months.all') },
  ...MONTH_IDS.map((m) => ({
    value: monthLabel(m),
    label: monthLabel(m),
  })),
]);

// 表单月份下拉
const monthSelectOptions = computed(() =>
  MONTH_IDS.map((m) => ({
    value: m,
    label: monthLabel(m),
  })),
);

const recommendedOptions = computed(() => [
  { label: $t('travel.monthly_list.recommended.on'), value: 1 },
  { label: $t('travel.monthly_list.recommended.off'), value: 0 },
]);

// =========================
// 弹窗 / 表单状态
// =========================
type ModalMode = 'create' | 'detail' | 'edit' | null;
const modalMode = ref<ModalMode>(null);
const modalVisible = ref(false);
const submitting = ref(false);

const formRef = ref<InstanceType<typeof Form> | null>(null);
const imageUploading = ref(false);

// 详情视图当前记录
const currentRecord = ref<any>(null);

function emptyForm(): MonthlyFormState {
  return {
    destinationName: '',
    monthId: 1,
    recommendedDays: 1,
    isRecommended: 1,
    sortOrder: 1,
    tags: '',
    imageUrl: '',
    climateInfo: '',
    seasonFeatures: '',
    travelTips: '',
    description: '',
  };
}

const formData = reactive<MonthlyFormState>(emptyForm());

const rules = computed(() => ({
  destinationName: [
    {
      required: true,
      message: $t('travel.monthly_list.form.destination_required'),
      trigger: 'blur',
    },
  ],
}));

// =========================
// 图片 URL 规范化 / 工具
// =========================
const BACKEND_STATIC_BASE = 'http://localhost:8080';

function normalizeImageUrl(url?: null | string): string {
  if (!url) return '';
  const raw = String(url).trim();
  if (!raw || raw === 'null' || raw === 'undefined') return '';
  // base64 data URL 直接返回
  if (/^data:/i.test(raw)) return raw;
  if (/^https?:\/\//i.test(raw)) return raw;
  if (raw.startsWith('/')) return BACKEND_STATIC_BASE + raw;
  return `${BACKEND_STATIC_BASE}/${raw}`;
}

/**
 * 标签解析：兼容 JSON 数组字符串（"[\"冰雪\", \"冰雕\"]"）与逗号分隔字符串
 */
function parseTags(value: null | string | undefined): string[] {
  if (!value) return [];
  const raw = String(value).trim();
  if (raw.startsWith('[')) {
    try {
      const arr = JSON.parse(raw);
      if (Array.isArray(arr)) {
        return arr.map((v) => String(v).trim()).filter((v) => v.length > 0);
      }
    } catch {
      // JSON 解析失败，回退到逗号拆分
    }
  }
  return raw
    .split(',')
    .map((s) => s.trim())
    .filter((s) => s.length > 0);
}

// =========================
// 数据加载 + 规范化
// 全部：{ records, pages, current }（MyBatis-Plus 分页）
// 按月份：数组（走前端分页）
// =========================
async function fetchData() {
  loading.value = true;
  errorMsg.value = '';
  try {
    const res: any =
      filterMonth.value === 'all'
        ? await getRecommendationsPage(pageNum.value, pageSize.value)
        : await getRecommendationsByMonth(filterMonth.value);
    const records: any[] = Array.isArray(res)
      ? res
      : (res?.records ?? res?.list ?? res?.items ?? res?.rows ?? []);
    dataSource.value = normalizeList(records);
    if (Array.isArray(res)) {
      pages.value = 0;
      totalCount.value = 0;
    } else {
      pageNum.value = Number(res?.current ?? res?.pageNum ?? pageNum.value);
      pages.value = Number(res?.pages ?? res?.totalPages ?? 0);
      totalCount.value = Number(
        res?.total ??
          (pages.value ? pages.value * pageSize.value : records.length),
      );
    }
    imageErrorSet.clear();
  } catch (error: any) {
    errorMsg.value = error?.message || $t('travel.monthly_list.error_desc');
  } finally {
    loading.value = false;
  }
}

/** 行数据规范化：imageUrl 兼容 base64，tags 预解析 */
function normalizeList(records: any[]): any[] {
  return (records ?? []).map((raw) => {
    const row: any = { ...(raw ?? {}) };
    row.imageUrl = normalizeImageUrl(row.imageUrl);
    row.tagList = parseTags(row.tags);
    return row;
  });
}

onMounted(() => {
  fetchData();
});

// =========================
// 月份筛选
// =========================
function handleFilterMonth(value: string) {
  if (filterMonth.value === value) return;
  filterMonth.value = value;
  pageNum.value = 1;
  fetchData();
}

// =========================
// 分页（月份筛选时为前端分页）
// =========================
const total = computed(() => {
  if (filterMonth.value !== 'all') return dataSource.value.length;
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
  showTotal: (t: number) => $t('travel.monthly_list.total', { total: t }),
}));

function handleTableChange(pag: any) {
  pageNum.value = pag.current;
  pageSize.value = pag.pageSize;
  if (filterMonth.value === 'all') {
    fetchData();
  }
}

// =========================
// 表格列定义
// =========================
const columns = computed<TableColumnsType>(() => [
  {
    title: $t('travel.monthly_list.fields.id'),
    dataIndex: 'id',
    width: 70,
    fixed: 'left',
  },
  {
    title: $t('travel.monthly_list.fields.imageUrl'),
    dataIndex: 'imageUrl',
    width: 100,
    fixed: 'left',
  },
  {
    title: $t('travel.monthly_list.fields.destinationName'),
    dataIndex: 'destinationName',
    width: 140,
    fixed: 'left',
    ellipsis: true,
  },
  {
    title: $t('travel.monthly_list.fields.monthId'),
    dataIndex: 'monthId',
    width: 90,
  },
  {
    title: $t('travel.monthly_list.fields.recommendedDays'),
    dataIndex: 'recommendedDays',
    width: 110,
  },
  {
    title: $t('travel.monthly_list.fields.isRecommended'),
    dataIndex: 'isRecommended',
    width: 100,
  },
  {
    title: $t('travel.monthly_list.fields.sortOrder'),
    dataIndex: 'sortOrder',
    width: 80,
  },
  {
    title: $t('travel.monthly_list.fields.viewCount'),
    dataIndex: 'viewCount',
    width: 100,
  },
  {
    title: $t('travel.monthly_list.fields.tags'),
    dataIndex: 'tagList',
    width: 220,
  },
  {
    title: $t('travel.monthly_list.fields.createdAt'),
    dataIndex: 'createdAt',
    width: 180,
  },
  {
    title: $t('travel.monthly_list.action.operation'),
    key: 'actions',
    width: 220,
    fixed: 'right',
  },
]);

// =========================
// 辅助函数
// =========================
function isImageFailed(id: number | string): boolean {
  return imageErrorSet.has(id);
}

function handleImageError(id: number | string) {
  imageErrorSet.add(id);
}

/** 推荐：1 推荐 / 0 未推荐 */
function isRecommendedValue(value: null | number | string | undefined): boolean {
  return Number(value) === 1;
}

/**
 * 把后端单行记录转换为弹窗表单字段
 */
function recordToForm(rec: any): MonthlyFormState {
  return {
    id: rec.id,
    destinationName: String(rec.destinationName ?? ''),
    monthId: Number(rec.monthId ?? 1),
    recommendedDays:
      rec.recommendedDays === null || rec.recommendedDays === undefined
        ? undefined
        : Number(rec.recommendedDays),
    isRecommended: Number(rec.isRecommended ?? 1),
    sortOrder:
      rec.sortOrder === null || rec.sortOrder === undefined
        ? undefined
        : Number(rec.sortOrder),
    tags: parseTags(rec.tags).join(','),
    imageUrl: normalizeImageUrl(rec.imageUrl),
    climateInfo: String(rec.climateInfo ?? ''),
    seasonFeatures: String(rec.seasonFeatures ?? ''),
    travelTips: String(rec.travelTips ?? ''),
    description: String(rec.description ?? ''),
  };
}

// 表单内图片预览 URL（实时规范化）
const formImageDisplayUrl = computed(() =>
  normalizeImageUrl(formData.imageUrl),
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
    return $t('travel.monthly_list.modal.create_title');
  if (modalMode.value === 'edit')
    return $t('travel.monthly_list.modal.edit_title');
  return $t('travel.monthly_list.modal.detail_title');
});

// =========================
// 图片上传
// =========================
async function handleImageUpload(file: File) {
  if (!file) return;
  if (!file.type.startsWith('image/')) {
    message.error($t('travel.monthly_list.form.image_invalid_image'));
    return;
  }
  imageUploading.value = true;
  try {
    const res: any = await uploadFile(file, '/monthly-recommendations/upload');
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
      message.error($t('travel.monthly_list.form.image_upload_failed'));
      return;
    }
    formData.imageUrl = String(relativePath);
    message.success($t('travel.monthly_list.form.image_upload_success'));
  } catch (error: any) {
    const msg =
      error?.message || $t('travel.monthly_list.form.image_upload_failed');
    message.error(msg);
  } finally {
    imageUploading.value = false;
  }
}

function handleImageChange(event: any) {
  const file = event?.fileList?.[0]?.originFileObj;
  if (file) handleImageUpload(file as File);
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
    const tagList = parseTags(formData.tags);
    const payload: any = {
      destinationName: formData.destinationName,
      monthId: Number(formData.monthId),
      recommendedDays: formData.recommendedDays ?? null,
      isRecommended: Number(formData.isRecommended),
      sortOrder: formData.sortOrder ?? null,
      // 对齐后端存储格式：JSON 数组字符串
      tags: JSON.stringify(tagList),
      imageUrl: formData.imageUrl || null,
      climateInfo: formData.climateInfo || null,
      seasonFeatures: formData.seasonFeatures || null,
      travelTips: formData.travelTips || null,
      description: formData.description || null,
    };
    if (formData.id !== undefined && formData.id !== null) {
      payload.id = formData.id;
      await updateRecommendation(payload);
      message.success($t('travel.monthly_list.action.update_success'));
    } else {
      await addRecommendation(payload);
      message.success($t('travel.monthly_list.action.create_success'));
    }
    closeModal();
    pageNum.value = 1;
    await fetchData();
  } catch (err: any) {
    const msg = err?.message || err?.msg || String(err || '');
    message.error(
      modalMode.value === 'create'
        ? msg || $t('travel.monthly_list.action.create_failed')
        : msg || $t('travel.monthly_list.action.update_failed'),
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
    await deleteRecommendation(record.id);
    message.success($t('travel.monthly_list.action.delete_success'));
    // 删除后若当前页空了，回退一页
    if (dataSource.value.length === 1 && pageNum.value > 1) {
      pageNum.value -= 1;
    }
    await fetchData();
  } catch (err: any) {
    const msg =
      err?.message ||
      err?.msg ||
      $t('travel.monthly_list.action.delete_failed');
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
          :title="$t('travel.monthly_list.error_title')"
          :sub-title="errorMsg"
        >
          <template #extra>
            <Button type="primary" @click="fetchData">
              {{ $t('travel.monthly_list.retry') }}
            </Button>
          </template>
        </Result>
      </Card>

      <!-- 月份推荐列表 -->
      <Card
        v-else
        :title="$t('travel.monthly_list.title')"
        :styles="{ body: { padding: '0' } }"
      >
        <template #extra>
          <Button type="primary" :loading="loading" @click="openCreate">
            <template #icon>
              <Plus class="mr-1 size-4 align-middle" />
            </template>
            {{ $t('travel.monthly_list.action.add') }}
          </Button>
        </template>

        <!-- 月份筛选按钮组 -->
        <div class="filter-bar">
          <span class="filter-label">
            {{ $t('travel.monthly_list.filter_label') }}：
          </span>
          <Button
            v-for="opt in monthFilterOptions"
            :key="opt.value"
            :type="filterMonth === opt.value ? 'primary' : 'default'"
            size="small"
            class="filter-pill"
            @click="handleFilterMonth(opt.value)"
          >
            {{ opt.label }}
          </Button>
        </div>

        <Table
          :columns="columns"
          :data-source="dataSource"
          :pagination="pagination"
          :loading="loading"
          :scroll="{ x: 1810 }"
          :bordered="true"
          size="middle"
          row-key="id"
          @change="handleTableChange"
        >
          <template #bodyCell="{ column, record }">
            <!-- 图片缩略图（圆形，兼容 base64） -->
            <template v-if="column.dataIndex === 'imageUrl'">
              <div class="image-cell">
                <img
                  v-if="record.imageUrl && !isImageFailed(record.id)"
                  :src="record.imageUrl"
                  :alt="record.destinationName"
                  loading="lazy"
                  class="image-thumb"
                  @error="handleImageError(record.id)"
                />
                <span v-else class="image-fallback">
                  {{ $t('travel.monthly_list.no_image') }}
                </span>
              </div>
            </template>

            <!-- 月份 -->
            <template v-else-if="column.dataIndex === 'monthId'">
              <Tag color="blue">{{ monthLabel(record.monthId) }}</Tag>
            </template>

            <!-- 建议天数 -->
            <template v-else-if="column.dataIndex === 'recommendedDays'">
              <template v-if="record.recommendedDays !== null && record.recommendedDays !== undefined">
                {{ record.recommendedDays }}
                {{ $t('travel.monthly_list.unit.days') }}
              </template>
              <span v-else class="text-xs text-muted-foreground">-</span>
            </template>

            <!-- 推荐：1 推荐 / 0 未推荐 -->
            <template v-else-if="column.dataIndex === 'isRecommended'">
              <Tag
                :color="isRecommendedValue(record.isRecommended) ? 'green' : 'default'"
              >
                {{
                  isRecommendedValue(record.isRecommended)
                    ? $t('travel.monthly_list.recommended.on')
                    : $t('travel.monthly_list.recommended.off')
                }}
              </Tag>
            </template>

            <!-- 浏览量 -->
            <template v-else-if="column.dataIndex === 'viewCount'">
              <span class="stat-cell">
                <Eye class="size-3.5 text-muted-foreground" />
                {{ record.viewCount ?? 0 }}
              </span>
            </template>

            <!-- 标签（JSON 数组字符串已预解析） -->
            <template v-else-if="column.dataIndex === 'tagList'">
              <template v-if="record.tagList?.length > 0">
                <Tag v-for="tag in record.tagList" :key="tag" color="orange">
                  {{ tag }}
                </Tag>
              </template>
              <span v-else class="text-xs text-muted-foreground">-</span>
            </template>

            <!-- 创建时间 -->
            <template v-else-if="column.dataIndex === 'createdAt'">
              {{ record.createdAt || '-' }}
            </template>

            <!-- 操作列：详情 / 修改 / 删除 -->
            <template v-else-if="column.key === 'actions'">
              <Space :size="4" wrap>
                <Button type="link" size="small" @click="openDetail(record)">
                  {{ $t('travel.monthly_list.action.detail') }}
                </Button>
                <Button type="link" size="small" @click="openEdit(record)">
                  {{ $t('travel.monthly_list.action.edit') }}
                </Button>
                <Popconfirm
                  :title="$t('travel.monthly_list.action.confirm_delete_title')"
                  :description="$t('travel.monthly_list.action.confirm_delete')"
                  :ok-text="$t('travel.monthly_list.action.delete_text')"
                  :cancel-text="$t('travel.monthly_list.action.cancel')"
                  :ok-button-props="{ danger: true }"
                  @confirm="handleDelete(record)"
                >
                  <Button
                    type="link"
                    size="small"
                    danger
                    :loading="deletingId === record.id"
                  >
                    {{ $t('travel.monthly_list.action.delete') }}
                  </Button>
                </Popconfirm>
              </Space>
            </template>
          </template>

          <template #emptyText>
            <Empty :description="$t('travel.monthly_list.empty')" />
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
      :ok-text="$t('travel.monthly_list.action.save')"
      :cancel-text="$t('travel.monthly_list.action.cancel')"
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
        <!-- 头部：图片 + 目的地 + 标签 -->
        <div class="mb-4 flex items-start gap-4">
          <Avatar
            v-if="formImageDisplayUrl"
            :src="formImageDisplayUrl"
            :size="96"
            shape="circle"
            class="border border-border overflow-hidden object-cover flex-shrink-0"
          />
          <div v-else class="image-placeholder-circle">
            <ImageOff class="size-8 text-muted-foreground" />
          </div>
          <div class="min-w-0">
            <div class="text-xl font-semibold truncate">
              {{ formData.destinationName }}
            </div>
            <Space class="mt-1" :size="6">
              <Tag color="blue">{{ monthLabel(formData.monthId) }}</Tag>
              <Tag
                :color="isRecommendedValue(formData.isRecommended) ? 'green' : 'default'"
              >
                {{
                  isRecommendedValue(formData.isRecommended)
                    ? $t('travel.monthly_list.recommended.on')
                    : $t('travel.monthly_list.recommended.off')
                }}
              </Tag>
              <span class="text-sm text-muted-foreground">
                {{ formData.recommendedDays ?? '-' }}
                {{ $t('travel.monthly_list.unit.days') }}
              </span>
            </Space>
            <div
              v-if="parseTags(formData.tags).length"
              class="mt-1 flex flex-wrap gap-1"
            >
              <Tag
                v-for="tag in parseTags(formData.tags)"
                :key="tag"
                color="orange"
              >
                {{ tag }}
              </Tag>
            </div>
          </div>
        </div>

        <Divider class="my-4">
          {{ $t('travel.monthly_list.detail.basic_info') }}
        </Divider>
        <Descriptions :column="2" bordered size="small">
          <Descriptions.Item :label="$t('travel.monthly_list.fields.id')">
            {{ (formData as any).id ?? '-' }}
          </Descriptions.Item>
          <Descriptions.Item :label="$t('travel.monthly_list.fields.monthId')">
            {{ monthLabel(formData.monthId) }}
          </Descriptions.Item>
          <Descriptions.Item
            :label="$t('travel.monthly_list.fields.recommendedDays')"
          >
            {{ formData.recommendedDays ?? '-' }}
          </Descriptions.Item>
          <Descriptions.Item :label="$t('travel.monthly_list.fields.sortOrder')">
            {{ formData.sortOrder ?? '-' }}
          </Descriptions.Item>
        </Descriptions>

        <Divider class="my-4">
          {{ $t('travel.monthly_list.detail.climate_season') }}
        </Divider>
        <Descriptions :column="1" bordered size="small">
          <Descriptions.Item
            :label="$t('travel.monthly_list.fields.climateInfo')"
          >
            {{ formData.climateInfo || '-' }}
          </Descriptions.Item>
          <Descriptions.Item
            :label="$t('travel.monthly_list.fields.seasonFeatures')"
          >
            {{ formData.seasonFeatures || '-' }}
          </Descriptions.Item>
        </Descriptions>

        <Divider class="my-4">
          {{ $t('travel.monthly_list.detail.tips_info') }}
        </Divider>
        <p
          class="m-0 whitespace-pre-wrap text-sm leading-6 text-muted-foreground"
        >
          {{ formData.travelTips || '-' }}
        </p>

        <Divider class="my-4">
          {{ $t('travel.monthly_list.detail.desc_info') }}
        </Divider>
        <p
          class="m-0 whitespace-pre-wrap text-sm leading-6 text-muted-foreground"
        >
          {{ formData.description || '-' }}
        </p>
      </div>

      <!-- 创建 / 修改：Form -->
      <div
        v-if="modalMode === 'create' || modalMode === 'edit'"
        class="monthly-form-wrapper"
        style="display: block; width: 100%;"
      >
        <!-- 编辑模式：展示 ID -->
        <div v-if="modalMode === 'edit' && (formData as any).id" class="mb-4">
          <Descriptions :column="2" size="small" bordered>
            <Descriptions.Item
              :label="$t('travel.monthly_list.fields.id')"
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
              :label="$t('travel.monthly_list.form.destination_label')"
              name="destinationName"
              prop="destinationName"
              style="display: block; width: 100%; margin-bottom: 16px;"
            >
              <Input
                v-model:value="formData.destinationName"
                class="w-full"
                :placeholder="$t('travel.monthly_list.form.destination_required')"
              />
            </FormItem>

            <FormItem
              :label="$t('travel.monthly_list.form.month_label')"
              name="monthId"
              prop="monthId"
              style="display: block; width: 100%; margin-bottom: 16px;"
            >
              <Select
                v-model:value="formData.monthId"
                class="w-full"
                :options="monthSelectOptions"
              />
            </FormItem>

            <FormItem
              :label="$t('travel.monthly_list.form.days_label')"
              name="recommendedDays"
              prop="recommendedDays"
              style="display: block; width: 100%; margin-bottom: 16px;"
            >
              <InputNumber
                v-model:value="formData.recommendedDays"
                class="w-full"
                :min="1"
                :precision="0"
                :placeholder="$t('travel.monthly_list.form.days_label')"
              />
            </FormItem>

            <FormItem
              :label="$t('travel.monthly_list.form.recommended_label')"
              name="isRecommended"
              prop="isRecommended"
              style="display: block; width: 100%; margin-bottom: 16px;"
            >
              <Select
                v-model:value="formData.isRecommended"
                class="w-full"
                :options="recommendedOptions"
              />
            </FormItem>

            <FormItem
              :label="$t('travel.monthly_list.form.sort_label')"
              name="sortOrder"
              prop="sortOrder"
              style="display: block; width: 100%; margin-bottom: 16px;"
            >
              <InputNumber
                v-model:value="formData.sortOrder"
                class="w-full"
                :min="0"
                :precision="0"
                :placeholder="$t('travel.monthly_list.form.sort_label')"
              />
            </FormItem>

            <FormItem
              :label="$t('travel.monthly_list.form.tags_label')"
              name="tags"
              prop="tags"
              :extra="$t('travel.monthly_list.form.tags_tip')"
              style="display: block; width: 100%; margin-bottom: 16px;"
            >
              <Input
                v-model:value="formData.tags"
                class="w-full"
                :placeholder="$t('travel.monthly_list.form.tags_tip')"
              />
            </FormItem>

            <!-- 图片上传：全宽 -->
            <div class="col-span-2" style="width: 100%;">
              <FormItem
                :label="$t('travel.monthly_list.form.image_label')"
                name="imageUrl"
                prop="imageUrl"
                style="display: block; width: 100%; margin-bottom: 16px;"
              >
                <Upload
                  :max-count="1"
                  :show-upload-list="false"
                  accept="image/*"
                  :before-upload="() => false"
                  @change="handleImageChange"
                >
                  <Button
                    class="image-upload-trigger flex w-full items-center gap-3 border-dashed"
                    :loading="imageUploading"
                    style="height: auto; padding: 8px 12px;"
                  >
                    <img
                      v-if="formImageDisplayUrl"
                      :src="formImageDisplayUrl"
                      :alt="$t('travel.monthly_list.form.image_label')"
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
                          formData.imageUrl
                            ? $t('travel.monthly_list.form.image_change')
                            : $t('travel.monthly_list.form.image_upload')
                        }}
                      </span>
                      <span class="text-xs text-muted-foreground">
                        {{ $t('travel.monthly_list.form.image_tip') }}
                      </span>
                    </div>
                  </Button>
                </Upload>
              </FormItem>
            </div>

            <div class="col-span-2" style="width: 100%;">
              <FormItem
                :label="$t('travel.monthly_list.form.climate_label')"
                name="climateInfo"
                prop="climateInfo"
                style="display: block; width: 100%; margin-bottom: 16px;"
              >
                <Input
                  v-model:value="formData.climateInfo"
                  class="w-full"
                  :placeholder="$t('travel.monthly_list.form.climate_label')"
                />
              </FormItem>
            </div>

            <div class="col-span-2" style="width: 100%;">
              <FormItem
                :label="$t('travel.monthly_list.form.season_label')"
                name="seasonFeatures"
                prop="seasonFeatures"
                style="display: block; width: 100%; margin-bottom: 16px;"
              >
                <Input
                  v-model:value="formData.seasonFeatures"
                  class="w-full"
                  :placeholder="$t('travel.monthly_list.form.season_label')"
                />
              </FormItem>
            </div>

            <div class="col-span-2" style="width: 100%;">
              <FormItem
                :label="$t('travel.monthly_list.form.tips_label')"
                name="travelTips"
                prop="travelTips"
                style="display: block; width: 100%; margin-bottom: 16px;"
              >
                <Input.TextArea
                  v-model:value="formData.travelTips"
                  :rows="3"
                  class="w-full"
                  :placeholder="$t('travel.monthly_list.form.tips_label')"
                />
              </FormItem>
            </div>

            <div class="col-span-2" style="width: 100%;">
              <FormItem
                :label="$t('travel.monthly_list.form.desc_label')"
                name="description"
                prop="description"
                style="display: block; width: 100%; margin-bottom: 16px;"
              >
                <Input.TextArea
                  v-model:value="formData.description"
                  :rows="3"
                  class="w-full"
                  :placeholder="$t('travel.monthly_list.form.desc_label')"
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
.filter-bar {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  border-bottom: 1px solid var(--border-color, #f0f0f0);
}

.filter-label {
  font-size: 13px;
  font-weight: 500;
  color: var(--foreground-color, rgb(0 0 0 / 88%));
  white-space: nowrap;
}

.filter-pill {
  border-radius: 999px;
}

.image-cell {
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

.image-thumb {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.image-fallback {
  color: var(--muted-foreground-color, rgb(0 0 0 / 45%));
  font-size: 11px;
  text-align: center;
}

.image-placeholder-circle {
  display: flex;
  width: 96px;
  height: 96px;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  background-color: var(--app-fill-color, rgb(0 0 0 / 4%));
  flex-shrink: 0;
}

.image-upload-trigger {
  text-align: left;
}

.stat-cell {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
}
</style>