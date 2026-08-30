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
  Table,
  Tag,
  Upload,
} from 'antdv-next';

import {
  addCarousel,
  deleteCarousel,
  getAllCarousels,
  getCarouselsByPosition,
  updateCarousel,
} from '#/api/management/travel/carousel';
import { uploadFile } from '#/api/management/user/upload';

defineOptions({ name: 'CarouselManagement' });

// =========================
// 轮播图数据类型
// =========================
interface CarouselFormState {
  id?: number | string;
  title: string;
  subtitle: string;
  position: string;
  deviceType: string;
  linkType: string;
  linkUrl: string;
  linkTarget: string;
  targetId?: null | number;
  imageUrl: string;
  mobileImageUrl: string;
  sortOrder?: null | number;
  // "1" 启用 / "0" 停用
  isActive: string;
  // "1" 永久显示 / "0" 定时显示
  isAlwaysShow: string;
  startTime: string;
  endTime: string;
  buttonText: string;
  textColor: string;
  overlayOpacity?: null | number;
  remark: string;
}

// =========================
// 基础状态
// =========================
const loading = ref(false);
const errorMsg = ref('');
const dataSource = ref<any[]>([]);

// 分页状态：后端返回数组（无分页包装），pages=0 走前端分页
const pageNum = ref(1);
const pageSize = ref(10);
const pages = ref(0);

// 图片加载失败的记录 id 集合
const imageErrorSet = reactive<Set<number | string>>(new Set());

// =========================
// 位置筛选状态
// =========================
const filterPosition = ref('all');

const positionFilterOptions = computed(() =>
  [
    'all',
    'home_top',
    'home_middle',
    'destination',
    'attraction',
    'hotel',
    'food',
    'souvenir',
    'strategy',
    'community',
  ].map((v) => ({
    value: v,
    label:
      v === 'all'
        ? $t('travel.carousel_list.filter.all')
        : $t(`travel.carousel_list.filter.${v}`),
  })),
);

function handleFilterChange(value: string) {
  if (filterPosition.value === value) return;
  filterPosition.value = value;
  pageNum.value = 1;
  fetchData();
}

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

function emptyForm(): CarouselFormState {
  return {
    title: '',
    subtitle: '',
    position: 'home_top',
    deviceType: 'all',
    linkType: 'url',
    linkUrl: '',
    linkTarget: '_self',
    targetId: undefined,
    imageUrl: '',
    mobileImageUrl: '',
    sortOrder: 1,
    isActive: '1',
    isAlwaysShow: '1',
    startTime: '',
    endTime: '',
    buttonText: '',
    textColor: '',
    overlayOpacity: undefined,
    remark: '',
  };
}

const formData = reactive<CarouselFormState>(emptyForm());

const rules = computed(() => ({
  title: [
    {
      required: true,
      message: $t('travel.carousel_list.form.title_required'),
      trigger: 'blur',
    },
  ],
}));

const positionOptions = computed(() =>
  [
    'home_top',
    'home_middle',
    'destination',
    'attraction',
    'hotel',
    'food',
    'souvenir',
    'strategy',
    'community',
  ].map((v) => ({ label: dictText('position', v), value: v })),
);

const deviceOptions = computed(() =>
  ['all', 'pc', 'mobile'].map((v) => ({
    label: dictText('device', v),
    value: v,
  })),
);

const linkTypeOptions = computed(() =>
  [
    'url',
    'attraction',
    'hotel',
    'food',
    'souvenir',
    'strategy',
    'community',
  ].map((v) => ({ label: dictText('linkType', v), value: v })),
);

const linkTargetOptions = computed(() => [
  { label: $t('travel.carousel_list.form.linkTarget_self'), value: '_self' },
  { label: $t('travel.carousel_list.form.linkTarget_new'), value: '_blank' },
]);

const activeOptions = computed(() => [
  { label: $t('travel.carousel_list.active.on'), value: '1' },
  { label: $t('travel.carousel_list.active.off'), value: '0' },
]);

const alwaysOptions = computed(() => [
  { label: $t('travel.carousel_list.always.yes'), value: '1' },
  { label: $t('travel.carousel_list.always.no'), value: '0' },
]);

// =========================
// 图片 URL 规范化 / 字典翻译
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

/** 字典翻译：字典值 → 本地化文案；未登记的值原样返回 */
function dictText(dict: string, value: null | string | undefined): string {
  if (value === null || value === undefined || value === '') return '-';
  const key = `travel.carousel_list.${dict}.${value}`;
  const text = $t(key);
  return text === key ? String(value) : text;
}

/** ISO 时间显示：去掉 T */
function formatDateTime(value: null | string | undefined): string {
  if (!value) return '-';
  return String(value).replace('T', ' ');
}

// =========================
// 数据加载 + 规范化
// 响应结构：data 直接是数组（无分页包装）→ 前端分页
// =========================
async function fetchData() {
  loading.value = true;
  errorMsg.value = '';
  try {
    // 全部位置 → 全量接口；指定位置 → 按位置查询接口
    const res: any =
      filterPosition.value === 'all'
        ? await getAllCarousels()
        : await getCarouselsByPosition(filterPosition.value);
    const records: any[] = Array.isArray(res)
      ? res
      : (res?.records ?? res?.list ?? res?.items ?? res?.rows ?? []);
    dataSource.value = normalizeList(records);
    pages.value = Array.isArray(res)
      ? 0
      : Number(res?.pages ?? res?.totalPages ?? 0);
    imageErrorSet.clear();
  } catch (error: any) {
    errorMsg.value = error?.message || $t('travel.carousel_list.error_desc');
  } finally {
    loading.value = false;
  }
}

/** 行数据规范化：imageUrl 补全域名 */
function normalizeList(records: any[]): any[] {
  return (records ?? []).map((raw) => {
    const row: any = { ...(raw ?? {}) };
    row.imageUrl = normalizeImageUrl(row.imageUrl);
    return row;
  });
}

onMounted(() => {
  fetchData();
});

// =========================
// 分页：pages=0 时由 Table 前端分页
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
  showTotal: (t: number) => $t('travel.carousel_list.total', { total: t }),
}));

function handleTableChange(pag: any) {
  pageNum.value = pag.current;
  pageSize.value = pag.pageSize;
  // 服务端分页模式才重新请求；数组响应由 Table 本地分页
  if (pages.value > 0) fetchData();
}

// =========================
// 表格列定义
// =========================
const columns = computed<TableColumnsType>(() => [
  {
    title: $t('travel.carousel_list.fields.id'),
    dataIndex: 'id',
    width: 70,
    fixed: 'left',
  },
  {
    title: $t('travel.carousel_list.fields.imageUrl'),
    dataIndex: 'imageUrl',
    width: 100,
    fixed: 'left',
  },
  {
    title: $t('travel.carousel_list.fields.title'),
    dataIndex: 'title',
    width: 190,
    fixed: 'left',
    ellipsis: true,
  },
  {
    title: $t('travel.carousel_list.fields.position'),
    dataIndex: 'position',
    width: 110,
  },
  {
    title: $t('travel.carousel_list.fields.deviceType'),
    dataIndex: 'deviceType',
    width: 100,
  },
  {
    title: $t('travel.carousel_list.fields.sortOrder'),
    dataIndex: 'sortOrder',
    width: 80,
  },
  {
    title: $t('travel.carousel_list.fields.showTime'),
    key: 'showTime',
    width: 330,
    ellipsis: true,
  },
  {
    title: $t('travel.carousel_list.fields.isActive'),
    dataIndex: 'isActive',
    width: 90,
  },
  {
    title: $t('travel.carousel_list.fields.impressionCount'),
    dataIndex: 'impressionCount',
    width: 90,
  },
  {
    title: $t('travel.carousel_list.fields.clickCount'),
    dataIndex: 'clickCount',
    width: 90,
  },
  {
    title: $t('travel.carousel_list.fields.updatedAt'),
    dataIndex: 'updatedAt',
    width: 180,
  },
  {
    title: $t('travel.carousel_list.action.operation'),
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

/** 展示时段单元格：永久显示 → 永久 Tag；否则显示起止时间 */
function showTimeText(rec: any): string {
  if (rec.isAlwaysShow) return '';
  return `${formatDateTime(rec.startTime)} ~ ${formatDateTime(rec.endTime)}`;
}

/**
 * 把后端单行记录转换为弹窗表单字段
 */
function recordToForm(rec: any): CarouselFormState {
  return {
    id: rec.id,
    title: String(rec.title ?? ''),
    subtitle: String(rec.subtitle ?? ''),
    position: String(rec.position ?? 'home_top'),
    deviceType: String(rec.deviceType ?? 'all'),
    linkType: String(rec.linkType ?? 'url'),
    linkUrl: String(rec.linkUrl ?? ''),
    linkTarget: String(rec.linkTarget ?? '_self'),
    targetId:
      rec.targetId === null || rec.targetId === undefined
        ? undefined
        : Number(rec.targetId),
    imageUrl: normalizeImageUrl(rec.imageUrl),
    mobileImageUrl: String(rec.mobileImageUrl ?? ''),
    sortOrder:
      rec.sortOrder === null || rec.sortOrder === undefined
        ? undefined
        : Number(rec.sortOrder),
    isActive: rec.isActive ? '1' : '0',
    isAlwaysShow: rec.isAlwaysShow ? '1' : '0',
    startTime: String(rec.startTime ?? ''),
    endTime: String(rec.endTime ?? ''),
    buttonText: String(rec.buttonText ?? ''),
    textColor: String(rec.textColor ?? ''),
    overlayOpacity:
      rec.overlayOpacity === null || rec.overlayOpacity === undefined
        ? undefined
        : Number(rec.overlayOpacity),
    remark: String(rec.remark ?? ''),
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
    return $t('travel.carousel_list.modal.create_title');
  if (modalMode.value === 'edit')
    return $t('travel.carousel_list.modal.edit_title');
  return $t('travel.carousel_list.modal.detail_title');
});

// =========================
// 图片上传
// =========================
async function handleImageUpload(file: File) {
  if (!file) return;
  if (!file.type.startsWith('image/')) {
    message.error($t('travel.carousel_list.form.image_invalid_image'));
    return;
  }
  imageUploading.value = true;
  try {
    const res: any = await uploadFile(file);
    const relativePath =
      res?.url ?? res?.path ?? res?.filePath ?? res?.data?.url ?? '';
    if (!relativePath) {
      message.error($t('travel.carousel_list.form.image_upload_failed'));
      return;
    }
    formData.imageUrl = String(relativePath);
    message.success($t('travel.carousel_list.form.image_upload_success'));
  } catch (error: any) {
    const msg =
      error?.message || $t('travel.carousel_list.form.image_upload_failed');
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
      title: formData.title,
      subtitle: formData.subtitle || null,
      position: formData.position || null,
      deviceType: formData.deviceType || null,
      linkType: formData.linkType || null,
      linkUrl: formData.linkUrl || null,
      linkTarget: formData.linkTarget || null,
      targetId: formData.targetId ?? null,
      imageUrl: formData.imageUrl || null,
      mobileImageUrl: formData.mobileImageUrl || null,
      sortOrder: formData.sortOrder ?? null,
      isActive: formData.isActive === '1',
      isAlwaysShow: formData.isAlwaysShow === '1',
      startTime: formData.startTime || null,
      endTime: formData.endTime || null,
      buttonText: formData.buttonText || null,
      textColor: formData.textColor || null,
      overlayOpacity: formData.overlayOpacity ?? null,
      remark: formData.remark || null,
    };
    if (formData.id !== undefined && formData.id !== null) {
      payload.id = formData.id;
      await updateCarousel(payload);
      message.success($t('travel.carousel_list.action.update_success'));
    } else {
      await addCarousel(payload);
      message.success($t('travel.carousel_list.action.create_success'));
    }
    closeModal();
    pageNum.value = 1;
    await fetchData();
  } catch (err: any) {
    const msg = err?.message || err?.msg || String(err || '');
    message.error(
      modalMode.value === 'create'
        ? msg || $t('travel.carousel_list.action.create_failed')
        : msg || $t('travel.carousel_list.action.update_failed'),
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
    await deleteCarousel(record.id);
    message.success($t('travel.carousel_list.action.delete_success'));
    // 删除后若当前页空了，回退一页
    if (dataSource.value.length === 1 && pageNum.value > 1) {
      pageNum.value -= 1;
    }
    await fetchData();
  } catch (err: any) {
    const msg =
      err?.message ||
      err?.msg ||
      $t('travel.carousel_list.action.delete_failed');
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
          :title="$t('travel.carousel_list.error_title')"
          :sub-title="errorMsg"
        >
          <template #extra>
            <Button type="primary" @click="fetchData">
              {{ $t('travel.carousel_list.retry') }}
            </Button>
          </template>
        </Result>
      </Card>

      <!-- 轮播图列表 -->
      <Card
        v-else
        :title="$t('travel.carousel_list.title')"
        :styles="{ body: { padding: '0' } }"
      >
        <template #extra>
          <Button type="primary" :loading="loading" @click="openCreate">
            <template #icon>
              <span class="i-lucide-plus mr-1 size-4 align-middle"></span>
            </template>
            {{ $t('travel.carousel_list.action.add') }}
          </Button>
        </template>

        <!-- 位置筛选按钮组 -->
        <div class="filter-bar">
          <Button
            v-for="opt in positionFilterOptions"
            :key="opt.value"
            :type="filterPosition === opt.value ? 'primary' : 'default'"
            size="small"
            class="filter-pill"
            @click="handleFilterChange(opt.value)"
          >
            {{ opt.label }}
          </Button>
        </div>

        <Table
          :columns="columns"
          :data-source="dataSource"
          :pagination="pagination"
          :loading="loading"
          :scroll="{ x: 1910 }"
          :bordered="true"
          size="middle"
          row-key="id"
          @change="handleTableChange"
        >
          <template #bodyCell="{ column, record }">
            <!-- 图片缩略图（圆形） -->
            <template v-if="column.dataIndex === 'imageUrl'">
              <div class="image-cell">
                <img
                  v-if="record.imageUrl && !isImageFailed(record.id)"
                  :src="record.imageUrl"
                  :alt="record.title"
                  loading="lazy"
                  class="image-thumb"
                  @error="handleImageError(record.id)"
                />
                <span v-else class="image-fallback">
                  {{ $t('travel.carousel_list.no_image') }}
                </span>
              </div>
            </template>

            <!-- 展示位置 -->
            <template v-else-if="column.dataIndex === 'position'">
              <Tag v-if="record.position" color="blue">
                {{ dictText('position', record.position) }}
              </Tag>
              <span v-else class="text-xs text-muted-foreground">-</span>
            </template>

            <!-- 设备类型 -->
            <template v-else-if="column.dataIndex === 'deviceType'">
              <Tag v-if="record.deviceType" color="cyan">
                {{ dictText('device', record.deviceType) }}
              </Tag>
              <span v-else class="text-xs text-muted-foreground">-</span>
            </template>

            <!-- 展示时段：永久显示 → 永久 Tag；否则起止时间 -->
            <template v-else-if="column.key === 'showTime'">
              <Tag v-if="record.isAlwaysShow" color="green">
                {{ $t('travel.carousel_list.always.yes') }}
              </Tag>
              <span v-else class="text-xs">
                {{ showTimeText(record) }}
              </span>
            </template>

            <!-- 启用状态 -->
            <template v-else-if="column.dataIndex === 'isActive'">
              <Tag :color="record.isActive ? 'green' : 'default'">
                {{
                  record.isActive
                    ? $t('travel.carousel_list.active.on')
                    : $t('travel.carousel_list.active.off')
                }}
              </Tag>
            </template>

            <!-- 曝光量 -->
            <template v-else-if="column.dataIndex === 'impressionCount'">
              <span class="inline-flex items-center gap-1 text-sm">
                <span class="i-lucide-eye size-3.5 text-muted-foreground"></span>
                {{ record.impressionCount ?? 0 }}
              </span>
            </template>

            <!-- 点击量 -->
            <template v-else-if="column.dataIndex === 'clickCount'">
              <span class="inline-flex items-center gap-1 text-sm">
                <span
                  class="i-lucide-mouse-pointer-click size-3.5 text-muted-foreground"
                ></span>
                {{ record.clickCount ?? 0 }}
              </span>
            </template>

            <!-- 更新时间 -->
            <template v-else-if="column.dataIndex === 'updatedAt'">
              {{ formatDateTime(record.updatedAt) }}
            </template>

            <!-- 操作列：详情 / 修改 / 删除 -->
            <template v-else-if="column.key === 'actions'">
              <Space :size="4" wrap>
                <Button type="link" size="small" @click="openDetail(record)">
                  {{ $t('travel.carousel_list.action.detail') }}
                </Button>
                <Button type="link" size="small" @click="openEdit(record)">
                  {{ $t('travel.carousel_list.action.edit') }}
                </Button>
                <Popconfirm
                  :title="$t('travel.carousel_list.action.confirm_delete_title')"
                  :description="$t('travel.carousel_list.action.confirm_delete')"
                  :ok-text="$t('travel.carousel_list.action.delete_text')"
                  :cancel-text="$t('travel.carousel_list.action.cancel')"
                  :ok-button-props="{ danger: true }"
                  @confirm="handleDelete(record)"
                >
                  <Button
                    type="link"
                    size="small"
                    danger
                    :loading="deletingId === record.id"
                  >
                    {{ $t('travel.carousel_list.action.delete') }}
                  </Button>
                </Popconfirm>
              </Space>
            </template>
          </template>

          <template #emptyText>
            <Empty :description="$t('travel.carousel_list.empty')" />
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
      :ok-text="$t('travel.carousel_list.action.save')"
      :cancel-text="$t('travel.carousel_list.action.cancel')"
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
        <!-- 头部：图片 + 标题 + 标签 -->
        <div class="mb-4 flex items-start gap-4">
          <Avatar
            v-if="formImageDisplayUrl"
            :src="formImageDisplayUrl"
            :size="96"
            shape="circle"
            class="border border-border overflow-hidden object-cover flex-shrink-0"
          />
          <div v-else class="image-placeholder-circle">
            <span class="i-lucide-image-off size-8 text-muted-foreground"></span>
          </div>
          <div class="min-w-0">
            <div class="text-xl font-semibold truncate">
              {{ formData.title }}
            </div>
            <div
              v-if="formData.subtitle"
              class="mt-0.5 truncate text-sm text-muted-foreground"
            >
              {{ formData.subtitle }}
            </div>
            <Space class="mt-1" :size="6">
              <Tag v-if="formData.position" color="blue">
                {{ dictText('position', formData.position) }}
              </Tag>
              <Tag v-if="formData.deviceType" color="cyan">
                {{ dictText('device', formData.deviceType) }}
              </Tag>
              <Tag v-if="formData.linkType" color="purple">
                {{ dictText('linkType', formData.linkType) }}
              </Tag>
              <Tag :color="formData.isActive === '1' ? 'green' : 'default'">
                {{
                  formData.isActive === '1'
                    ? $t('travel.carousel_list.active.on')
                    : $t('travel.carousel_list.active.off')
                }}
              </Tag>
            </Space>
          </div>
        </div>

        <Divider class="my-4">
          {{ $t('travel.carousel_list.detail.content_info') }}
        </Divider>
        <Descriptions :column="2" bordered size="small">
          <Descriptions.Item :label="$t('travel.carousel_list.fields.id')">
            {{ (formData as any).id ?? '-' }}
          </Descriptions.Item>
          <Descriptions.Item :label="$t('travel.carousel_list.fields.sortOrder')">
            {{ formData.sortOrder ?? '-' }}
          </Descriptions.Item>
          <Descriptions.Item
            :label="$t('travel.carousel_list.fields.remark')"
            :span="2"
          >
            {{ formData.remark || '-' }}
          </Descriptions.Item>
        </Descriptions>

        <Divider class="my-4">
          {{ $t('travel.carousel_list.detail.link_info') }}
        </Divider>
        <Descriptions :column="2" bordered size="small">
          <Descriptions.Item :label="$t('travel.carousel_list.fields.linkType')">
            {{ dictText('linkType', formData.linkType) }}
          </Descriptions.Item>
          <Descriptions.Item :label="$t('travel.carousel_list.fields.linkTarget')">
            {{
              formData.linkTarget === '_blank'
                ? $t('travel.carousel_list.form.linkTarget_new')
                : $t('travel.carousel_list.form.linkTarget_self')
            }}
          </Descriptions.Item>
          <Descriptions.Item
            :label="$t('travel.carousel_list.fields.linkUrl')"
            :span="2"
          >
            {{ formData.linkUrl || '-' }}
          </Descriptions.Item>
          <Descriptions.Item :label="$t('travel.carousel_list.fields.targetId')">
            {{ formData.targetId ?? '-' }}
          </Descriptions.Item>
          <Descriptions.Item :label="$t('travel.carousel_list.fields.buttonText')">
            {{ formData.buttonText || '-' }}
          </Descriptions.Item>
        </Descriptions>

        <Divider class="my-4">
          {{ $t('travel.carousel_list.detail.display_info') }}
        </Divider>
        <Descriptions :column="2" bordered size="small">
          <Descriptions.Item
            :label="$t('travel.carousel_list.fields.isAlwaysShow')"
          >
            {{
              formData.isAlwaysShow === '1'
                ? $t('travel.carousel_list.always.yes')
                : $t('travel.carousel_list.always.no')
            }}
          </Descriptions.Item>
          <Descriptions.Item :label="$t('travel.carousel_list.fields.userType')">
            {{ currentRecord.userType || '-' }}
          </Descriptions.Item>
          <Descriptions.Item :label="$t('travel.carousel_list.fields.startTime')">
            {{ formatDateTime(formData.startTime) }}
          </Descriptions.Item>
          <Descriptions.Item :label="$t('travel.carousel_list.fields.endTime')">
            {{ formatDateTime(formData.endTime) }}
          </Descriptions.Item>
        </Descriptions>

        <Divider class="my-4">
          {{ $t('travel.carousel_list.detail.style_info') }}
        </Divider>
        <Descriptions :column="2" bordered size="small">
          <Descriptions.Item :label="$t('travel.carousel_list.fields.textColor')">
            {{ formData.textColor || '-' }}
          </Descriptions.Item>
          <Descriptions.Item
            :label="$t('travel.carousel_list.fields.textShadow')"
          >
            {{
              currentRecord.textShadow
                ? $t('travel.carousel_list.active.on')
                : $t('travel.carousel_list.active.off')
            }}
          </Descriptions.Item>
          <Descriptions.Item
            :label="$t('travel.carousel_list.fields.overlayColor')"
          >
            {{ currentRecord.overlayColor || '-' }}
          </Descriptions.Item>
          <Descriptions.Item
            :label="$t('travel.carousel_list.fields.overlayOpacity')"
          >
            {{ formData.overlayOpacity ?? '-' }}
          </Descriptions.Item>
          <Descriptions.Item
            :label="$t('travel.carousel_list.fields.mobileImageUrl')"
            :span="2"
          >
            {{ formData.mobileImageUrl || '-' }}
          </Descriptions.Item>
        </Descriptions>

        <Divider class="my-4">
          {{ $t('travel.carousel_list.detail.stat_info') }}
        </Divider>
        <Descriptions :column="2" bordered size="small">
          <Descriptions.Item
            :label="$t('travel.carousel_list.fields.impressionCount')"
          >
            {{ currentRecord.impressionCount ?? 0 }}
          </Descriptions.Item>
          <Descriptions.Item
            :label="$t('travel.carousel_list.fields.clickCount')"
          >
            {{ currentRecord.clickCount ?? 0 }}
          </Descriptions.Item>
        </Descriptions>

        <Divider class="my-4">
          {{ $t('travel.carousel_list.detail.time_info') }}
        </Divider>
        <Descriptions :column="2" bordered size="small">
          <Descriptions.Item :label="$t('travel.carousel_list.fields.createdBy')">
            {{ currentRecord.createdBy ?? '-' }}
          </Descriptions.Item>
          <Descriptions.Item :label="$t('travel.carousel_list.fields.createdAt')">
            {{ formatDateTime(currentRecord.createdAt) }}
          </Descriptions.Item>
          <Descriptions.Item :label="$t('travel.carousel_list.fields.updatedAt')">
            {{ formatDateTime(currentRecord.updatedAt) }}
          </Descriptions.Item>
        </Descriptions>
      </div>

      <!-- 创建 / 修改：Form -->
      <div
        v-if="modalMode === 'create' || modalMode === 'edit'"
        class="carousel-form-wrapper"
        style="display: block; width: 100%;"
      >
        <!-- 编辑模式：展示 ID -->
        <div v-if="modalMode === 'edit' && (formData as any).id" class="mb-4">
          <Descriptions :column="2" size="small" bordered>
            <Descriptions.Item
              :label="$t('travel.carousel_list.fields.id')"
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
              :label="$t('travel.carousel_list.form.title_label')"
              name="title"
              prop="title"
              style="display: block; width: 100%; margin-bottom: 16px;"
            >
              <Input
                v-model:value="formData.title"
                class="w-full"
                :placeholder="$t('travel.carousel_list.form.title_required')"
              />
            </FormItem>

            <FormItem
              :label="$t('travel.carousel_list.form.subtitle_label')"
              name="subtitle"
              prop="subtitle"
              style="display: block; width: 100%; margin-bottom: 16px;"
            >
              <Input
                v-model:value="formData.subtitle"
                class="w-full"
                :placeholder="$t('travel.carousel_list.form.subtitle_label')"
              />
            </FormItem>

            <FormItem
              :label="$t('travel.carousel_list.form.position_label')"
              name="position"
              prop="position"
              style="display: block; width: 100%; margin-bottom: 16px;"
            >
              <Select
                v-model:value="formData.position"
                class="w-full"
                :options="positionOptions"
              />
            </FormItem>

            <FormItem
              :label="$t('travel.carousel_list.form.device_label')"
              name="deviceType"
              prop="deviceType"
              style="display: block; width: 100%; margin-bottom: 16px;"
            >
              <Select
                v-model:value="formData.deviceType"
                class="w-full"
                :options="deviceOptions"
              />
            </FormItem>

            <FormItem
              :label="$t('travel.carousel_list.form.linkType_label')"
              name="linkType"
              prop="linkType"
              style="display: block; width: 100%; margin-bottom: 16px;"
            >
              <Select
                v-model:value="formData.linkType"
                class="w-full"
                :options="linkTypeOptions"
              />
            </FormItem>

            <FormItem
              :label="$t('travel.carousel_list.form.linkTarget_label')"
              name="linkTarget"
              prop="linkTarget"
              style="display: block; width: 100%; margin-bottom: 16px;"
            >
              <Select
                v-model:value="formData.linkTarget"
                class="w-full"
                :options="linkTargetOptions"
              />
            </FormItem>

            <FormItem
              :label="$t('travel.carousel_list.form.linkUrl_label')"
              name="linkUrl"
              prop="linkUrl"
              style="display: block; width: 100%; margin-bottom: 16px;"
            >
              <Input
                v-model:value="formData.linkUrl"
                class="w-full"
                :placeholder="$t('travel.carousel_list.form.linkUrl_label')"
              />
            </FormItem>

            <FormItem
              :label="$t('travel.carousel_list.form.targetId_label')"
              name="targetId"
              prop="targetId"
              style="display: block; width: 100%; margin-bottom: 16px;"
            >
              <InputNumber
                v-model:value="formData.targetId"
                class="w-full"
                :min="0"
                :precision="0"
                :placeholder="$t('travel.carousel_list.form.targetId_label')"
              />
            </FormItem>

            <FormItem
              :label="$t('travel.carousel_list.form.sort_label')"
              name="sortOrder"
              prop="sortOrder"
              style="display: block; width: 100%; margin-bottom: 16px;"
            >
              <InputNumber
                v-model:value="formData.sortOrder"
                class="w-full"
                :min="0"
                :precision="0"
                :placeholder="$t('travel.carousel_list.form.sort_label')"
              />
            </FormItem>

            <FormItem
              :label="$t('travel.carousel_list.form.overlayOpacity_label')"
              name="overlayOpacity"
              prop="overlayOpacity"
              style="display: block; width: 100%; margin-bottom: 16px;"
            >
              <InputNumber
                v-model:value="formData.overlayOpacity"
                class="w-full"
                :min="0"
                :max="1"
                :step="0.1"
                :placeholder="$t('travel.carousel_list.form.overlayOpacity_label')"
              />
            </FormItem>

            <FormItem
              :label="$t('travel.carousel_list.form.active_label')"
              name="isActive"
              prop="isActive"
              style="display: block; width: 100%; margin-bottom: 16px;"
            >
              <Select
                v-model:value="formData.isActive"
                class="w-full"
                :options="activeOptions"
              />
            </FormItem>

            <FormItem
              :label="$t('travel.carousel_list.form.always_label')"
              name="isAlwaysShow"
              prop="isAlwaysShow"
              style="display: block; width: 100%; margin-bottom: 16px;"
            >
              <Select
                v-model:value="formData.isAlwaysShow"
                class="w-full"
                :options="alwaysOptions"
              />
            </FormItem>

            <FormItem
              :label="$t('travel.carousel_list.form.startTime_label')"
              name="startTime"
              prop="startTime"
              :extra="$t('travel.carousel_list.form.time_tip')"
              style="display: block; width: 100%; margin-bottom: 16px;"
            >
              <Input
                v-model:value="formData.startTime"
                class="w-full"
                :placeholder="$t('travel.carousel_list.form.time_tip')"
              />
            </FormItem>

            <FormItem
              :label="$t('travel.carousel_list.form.endTime_label')"
              name="endTime"
              prop="endTime"
              style="display: block; width: 100%; margin-bottom: 16px;"
            >
              <Input
                v-model:value="formData.endTime"
                class="w-full"
                :placeholder="$t('travel.carousel_list.form.time_tip')"
              />
            </FormItem>

            <FormItem
              :label="$t('travel.carousel_list.form.buttonText_label')"
              name="buttonText"
              prop="buttonText"
              style="display: block; width: 100%; margin-bottom: 16px;"
            >
              <Input
                v-model:value="formData.buttonText"
                class="w-full"
                :placeholder="$t('travel.carousel_list.form.buttonText_label')"
              />
            </FormItem>

            <FormItem
              :label="$t('travel.carousel_list.form.textColor_label')"
              name="textColor"
              prop="textColor"
              style="display: block; width: 100%; margin-bottom: 16px;"
            >
              <Input
                v-model:value="formData.textColor"
                class="w-full"
                placeholder="#FFFFFF"
              />
            </FormItem>

            <!-- 图片上传：全宽 -->
            <div class="col-span-2" style="width: 100%;">
              <FormItem
                :label="$t('travel.carousel_list.form.image_label')"
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
                      :alt="$t('travel.carousel_list.form.image_label')"
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
                          formData.imageUrl
                            ? $t('travel.carousel_list.form.image_change')
                            : $t('travel.carousel_list.form.image_upload')
                        }}
                      </span>
                      <span class="text-xs text-muted-foreground">
                        {{ $t('travel.carousel_list.form.image_tip') }}
                      </span>
                    </div>
                  </Button>
                </Upload>
              </FormItem>
            </div>

            <!-- 移动端图片：全宽 -->
            <div class="col-span-2" style="width: 100%;">
              <FormItem
                :label="$t('travel.carousel_list.form.mobile_label')"
                name="mobileImageUrl"
                prop="mobileImageUrl"
                :extra="$t('travel.carousel_list.form.mobile_tip')"
                style="display: block; width: 100%; margin-bottom: 16px;"
              >
                <Input
                  v-model:value="formData.mobileImageUrl"
                  class="w-full"
                  :placeholder="$t('travel.carousel_list.form.mobile_tip')"
                />
              </FormItem>
            </div>

            <!-- 备注：大文本域，全宽 -->
            <div class="col-span-2" style="width: 100%;">
              <FormItem
                :label="$t('travel.carousel_list.form.remark_label')"
                name="remark"
                prop="remark"
                style="display: block; width: 100%; margin-bottom: 16px;"
              >
                <Input.TextArea
                  v-model:value="formData.remark"
                  :rows="3"
                  class="w-full"
                  :placeholder="$t('travel.carousel_list.form.remark_label')"
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
  gap: 8px;
  padding: 12px 16px;
  border-bottom: 1px solid var(--border-color, #f0f0f0);
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
</style>
