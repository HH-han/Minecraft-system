<script lang="ts" setup>
import type { TableColumnsType } from 'antdv-next';

import { computed, onMounted, reactive, ref } from 'vue';

import { Page } from '@vben/common-ui';
import { ImageIcon, ImageOff, Plus } from '@vben/icons';
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

import {
  addCountries,
  deleteCountries,
  getCountriesList,
  updateCountries,
} from '#/api/management/travel/countries';
import { uploadFile } from '#/api/management/user/upload';

defineOptions({ name: 'CountryManagement' });

// =========================
// 国家数据类型
// =========================
interface CountryFormState {
  id?: number | string;
  name: string;
  chineseName: string;
  countryCode: string;
  flagEmoji: string;
  capital: string;
  continentId?: null | number;
  population?: null | number;
  area?: null | number;
  currency: string;
  language: string;
  timezone: string;
  phoneCode: string;
  image: string;
  description: string;
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

// 图片加载失败的记录 id 集合
const imageErrorSet = reactive<Set<number | string>>(new Set());

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

function emptyForm(): CountryFormState {
  return {
    name: '',
    chineseName: '',
    countryCode: '',
    flagEmoji: '',
    capital: '',
    continentId: undefined,
    population: undefined,
    area: undefined,
    currency: '',
    language: '',
    timezone: '',
    phoneCode: '',
    image: '',
    description: '',
  };
}

const formData = reactive<CountryFormState>(emptyForm());

const rules = computed(() => ({
  name: [
    {
      required: true,
      message: $t('travel.country_list.form.name_required'),
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
// 数字格式化
// =========================
function formatNumber(value: null | number | undefined): string {
  if (value === null || value === undefined) return '-';
  return Number(value).toLocaleString('en-US');
}

// =========================
// 数据加载 + 规范化
// 响应结构（MyBatis-Plus Page）：{ records, pages, current, ... }
// =========================
async function fetchData() {
  loading.value = true;
  errorMsg.value = '';
  try {
    // 同时携带 pageNum/pageSize 与 current/size，兼容两种后端参数命名
    const res: any = await getCountriesList({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      current: pageNum.value,
      size: pageSize.value,
    });
    const records: any[] = Array.isArray(res)
      ? res
      : (res?.records ?? res?.list ?? res?.items ?? res?.rows ?? []);
    dataSource.value = normalizeList(records);
    pageNum.value = Number(res?.current ?? res?.pageNum ?? pageNum.value);
    pages.value = Number(res?.pages ?? res?.totalPages ?? 0);
    totalCount.value = Number(
      res?.total ??
        (pages.value ? pages.value * pageSize.value : records.length),
    );
    imageErrorSet.clear();
  } catch (error: any) {
    errorMsg.value = error?.message || $t('travel.country_list.error_desc');
  } finally {
    loading.value = false;
  }
}

/** 行数据规范化：图片补全域名 */
function normalizeList(records: any[]): any[] {
  return (records ?? []).map((raw) => {
    const row: any = { ...(raw ?? {}) };
    row.image = normalizeImageUrl(row.image);
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
  showTotal: (t: number) => $t('travel.country_list.total', { total: t }),
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
    title: $t('travel.country_list.fields.id'),
    dataIndex: 'id',
    width: 70,
    fixed: 'left',
  },
  {
    title: $t('travel.country_list.fields.image'),
    dataIndex: 'image',
    width: 100,
    fixed: 'left',
  },
  {
    title: $t('travel.country_list.fields.flagEmoji'),
    dataIndex: 'flagEmoji',
    width: 80,
  },
  {
    title: $t('travel.country_list.fields.name'),
    dataIndex: 'name',
    width: 150,
    fixed: 'left',
    ellipsis: true,
  },
  {
    title: $t('travel.country_list.fields.chineseName'),
    dataIndex: 'chineseName',
    width: 110,
  },
  {
    title: $t('travel.country_list.fields.countryCode'),
    dataIndex: 'countryCode',
    width: 100,
  },
  {
    title: $t('travel.country_list.fields.capital'),
    dataIndex: 'capital',
    width: 110,
  },
  {
    title: $t('travel.country_list.fields.continentId'),
    dataIndex: 'continentId',
    width: 90,
  },
  {
    title: $t('travel.country_list.fields.population'),
    dataIndex: 'population',
    width: 140,
  },
  {
    title: $t('travel.country_list.fields.area'),
    dataIndex: 'area',
    width: 110,
  },
  {
    title: $t('travel.country_list.fields.currency'),
    dataIndex: 'currency',
    width: 90,
  },
  {
    title: $t('travel.country_list.fields.timezone'),
    dataIndex: 'timezone',
    width: 100,
  },
  {
    title: $t('travel.country_list.fields.phoneCode'),
    dataIndex: 'phoneCode',
    width: 100,
  },
  {
    title: $t('travel.country_list.fields.createdAt'),
    dataIndex: 'createdAt',
    width: 180,
  },
  {
    title: $t('travel.country_list.action.operation'),
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

/**
 * 把后端单行记录转换为弹窗表单字段
 */
function recordToForm(rec: any): CountryFormState {
  return {
    id: rec.id,
    name: String(rec.name ?? ''),
    chineseName: String(rec.chineseName ?? ''),
    countryCode: String(rec.countryCode ?? ''),
    flagEmoji: String(rec.flagEmoji ?? ''),
    capital: String(rec.capital ?? ''),
    continentId:
      rec.continentId === null || rec.continentId === undefined
        ? undefined
        : Number(rec.continentId),
    population:
      rec.population === null || rec.population === undefined
        ? undefined
        : Number(rec.population),
    area:
      rec.area === null || rec.area === undefined
        ? undefined
        : Number(rec.area),
    currency: String(rec.currency ?? ''),
    language: String(rec.language ?? ''),
    timezone: String(rec.timezone ?? ''),
    phoneCode: String(rec.phoneCode ?? ''),
    image: normalizeImageUrl(rec.image),
    description: String(rec.description ?? ''),
  };
}

// 表单内图片预览 URL（实时规范化）
const formImageDisplayUrl = computed(() =>
  normalizeImageUrl(formData.image),
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
    return $t('travel.country_list.modal.create_title');
  if (modalMode.value === 'edit')
    return $t('travel.country_list.modal.edit_title');
  return $t('travel.country_list.modal.detail_title');
});

// =========================
// 图片上传
// =========================
async function handleImageUpload(file: File) {
  if (!file) return;
  if (!file.type.startsWith('image/')) {
    message.error($t('travel.country_list.form.image_invalid_image'));
    return;
  }
  imageUploading.value = true;
  try {
    const res: any = await uploadFile(file);
    const relativePath =
      res?.url ?? res?.path ?? res?.filePath ?? res?.data?.url ?? '';
    if (!relativePath) {
      message.error($t('travel.country_list.form.image_upload_failed'));
      return;
    }
    formData.image = String(relativePath);
    message.success($t('travel.country_list.form.image_upload_success'));
  } catch (error: any) {
    const msg =
      error?.message || $t('travel.country_list.form.image_upload_failed');
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
      name: formData.name,
      chineseName: formData.chineseName || null,
      countryCode: formData.countryCode || null,
      flagEmoji: formData.flagEmoji || null,
      capital: formData.capital || null,
      continentId: formData.continentId ?? null,
      population: formData.population ?? null,
      area: formData.area ?? null,
      currency: formData.currency || null,
      language: formData.language || null,
      timezone: formData.timezone || null,
      phoneCode: formData.phoneCode || null,
      image: formData.image || null,
      description: formData.description || null,
    };
    if (formData.id !== undefined && formData.id !== null) {
      payload.id = formData.id;
      await updateCountries(payload);
      message.success($t('travel.country_list.action.update_success'));
    } else {
      await addCountries(payload);
      message.success($t('travel.country_list.action.create_success'));
    }
    closeModal();
    pageNum.value = 1;
    await fetchData();
  } catch (err: any) {
    const msg = err?.message || err?.msg || String(err || '');
    message.error(
      modalMode.value === 'create'
        ? msg || $t('travel.country_list.action.create_failed')
        : msg || $t('travel.country_list.action.update_failed'),
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
    await deleteCountries(record.id);
    message.success($t('travel.country_list.action.delete_success'));
    if (dataSource.value.length === 1 && pageNum.value > 1) {
      pageNum.value -= 1;
    }
    await fetchData();
  } catch (err: any) {
    const msg =
      err?.message ||
      err?.msg ||
      $t('travel.country_list.action.delete_failed');
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
          :title="$t('travel.country_list.error_title')"
          :sub-title="errorMsg"
        >
          <template #extra>
            <Button type="primary" @click="fetchData">
              {{ $t('travel.country_list.retry') }}
            </Button>
          </template>
        </Result>
      </Card>

      <!-- 国家列表 -->
      <Card
        v-else
        :title="$t('travel.country_list.title')"
        :styles="{ body: { padding: '0' } }"
      >
        <template #extra>
          <Button type="primary" :loading="loading" @click="openCreate">
            <template #icon>
              <Plus class="mr-1 size-4 align-middle" />
            </template>
            {{ $t('travel.country_list.action.add') }}
          </Button>
        </template>

        <Table
          :columns="columns"
          :data-source="dataSource"
          :pagination="pagination"
          :loading="loading"
          :scroll="{ x: 2080 }"
          :bordered="true"
          size="middle"
          row-key="id"
          @change="handleTableChange"
        >
          <template #bodyCell="{ column, record }">
            <!-- 图片缩略图（圆形） -->
            <template v-if="column.dataIndex === 'image'">
              <div class="image-cell">
                <img
                  v-if="record.image && !isImageFailed(record.id)"
                  :src="record.image"
                  :alt="record.name"
                  loading="lazy"
                  class="image-thumb"
                  @error="handleImageError(record.id)"
                />
                <span v-else class="image-fallback">
                  {{ $t('travel.country_list.no_image') }}
                </span>
              </div>
            </template>

            <!-- 国旗 Emoji -->
            <template v-else-if="column.dataIndex === 'flagEmoji'">
              <span v-if="record.flagEmoji" class="text-lg leading-none">
                {{ record.flagEmoji }}
              </span>
              <span v-else class="text-xs text-muted-foreground">-</span>
            </template>

            <!-- 国家代码 -->
            <template v-else-if="column.dataIndex === 'countryCode'">
              <Tag v-if="record.countryCode" color="blue">
                {{ record.countryCode }}
              </Tag>
              <span v-else class="text-xs text-muted-foreground">-</span>
            </template>

            <!-- 人口 -->
            <template v-else-if="column.dataIndex === 'population'">
              {{ formatNumber(record.population) }}
            </template>

            <!-- 面积 -->
            <template v-else-if="column.dataIndex === 'area'">
              {{ record.area !== null && record.area !== undefined ? record.area : '-' }}
            </template>

            <!-- 电话区号 -->
            <template v-else-if="column.dataIndex === 'phoneCode'">
              {{ record.phoneCode ? `+${record.phoneCode}` : '-' }}
            </template>

            <!-- 创建时间 -->
            <template v-else-if="column.dataIndex === 'createdAt'">
              {{ record.createdAt || '-' }}
            </template>

            <!-- 操作列：详情 / 修改 / 删除 -->
            <template v-else-if="column.key === 'actions'">
              <Space :size="4" wrap>
                <Button type="link" size="small" @click="openDetail(record)">
                  {{ $t('travel.country_list.action.detail') }}
                </Button>
                <Button type="link" size="small" @click="openEdit(record)">
                  {{ $t('travel.country_list.action.edit') }}
                </Button>
                <Popconfirm
                  :title="$t('travel.country_list.action.confirm_delete_title')"
                  :description="$t('travel.country_list.action.confirm_delete')"
                  :ok-text="$t('travel.country_list.action.delete_text')"
                  :cancel-text="$t('travel.country_list.action.cancel')"
                  :ok-button-props="{ danger: true }"
                  @confirm="handleDelete(record)"
                >
                  <Button
                    type="link"
                    size="small"
                    danger
                    :loading="deletingId === record.id"
                  >
                    {{ $t('travel.country_list.action.delete') }}
                  </Button>
                </Popconfirm>
              </Space>
            </template>
          </template>

          <template #emptyText>
            <Empty :description="$t('travel.country_list.empty')" />
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
      :ok-text="$t('travel.country_list.action.save')"
      :cancel-text="$t('travel.country_list.action.cancel')"
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
        <!-- 头部：图片 + 名称 + 标签 -->
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
              {{ currentRecord.flagEmoji }}
              {{ formData.name }}
              <span class="ml-1 text-base text-muted-foreground">
                {{ formData.chineseName }}
              </span>
            </div>
            <Space class="mt-1" :size="6">
              <Tag v-if="formData.countryCode" color="blue">
                {{ formData.countryCode }}
              </Tag>
              <Tag v-if="formData.capital" color="cyan">
                {{ formData.capital }}
              </Tag>
              <Tag v-if="formData.currency" color="gold">
                {{ formData.currency }}
              </Tag>
              <Tag v-if="formData.timezone" color="purple">
                {{ formData.timezone }}
              </Tag>
            </Space>
          </div>
        </div>

        <Divider class="my-4">
          {{ $t('travel.country_list.detail.basic_info') }}
        </Divider>
        <Descriptions :column="2" bordered size="small">
          <Descriptions.Item :label="$t('travel.country_list.fields.id')">
            {{ (formData as any).id ?? '-' }}
          </Descriptions.Item>
          <Descriptions.Item :label="$t('travel.country_list.fields.continentId')">
            {{ formData.continentId ?? '-' }}
          </Descriptions.Item>
          <Descriptions.Item :label="$t('travel.country_list.fields.capital')">
            {{ formData.capital || '-' }}
          </Descriptions.Item>
          <Descriptions.Item :label="$t('travel.country_list.fields.language')">
            {{ formData.language || '-' }}
          </Descriptions.Item>
          <Descriptions.Item :label="$t('travel.country_list.fields.population')">
            {{ formatNumber(formData.population) }}
          </Descriptions.Item>
          <Descriptions.Item :label="$t('travel.country_list.fields.area')">
            {{ formData.area ?? '-' }}
          </Descriptions.Item>
          <Descriptions.Item :label="$t('travel.country_list.fields.currency')">
            {{ formData.currency || '-' }}
          </Descriptions.Item>
          <Descriptions.Item :label="$t('travel.country_list.fields.phoneCode')">
            {{ formData.phoneCode ? `+${formData.phoneCode}` : '-' }}
          </Descriptions.Item>
        </Descriptions>

        <Divider class="my-4">
          {{ $t('travel.country_list.detail.desc_info') }}
        </Divider>
        <p class="m-0 whitespace-pre-wrap text-sm leading-6 text-muted-foreground">
          {{ formData.description || '-' }}
        </p>

        <Divider class="my-4">
          {{ $t('travel.country_list.detail.time_info') }}
        </Divider>
        <Descriptions :column="1" bordered size="small">
          <Descriptions.Item :label="$t('travel.country_list.fields.createdAt')">
            {{ currentRecord.createdAt || '-' }}
          </Descriptions.Item>
        </Descriptions>
      </div>

      <!-- 创建 / 修改：Form -->
      <div
        v-if="modalMode === 'create' || modalMode === 'edit'"
        class="country-form-wrapper"
        style="display: block; width: 100%;"
      >
        <!-- 编辑模式：展示 ID -->
        <div v-if="modalMode === 'edit' && (formData as any).id" class="mb-4">
          <Descriptions :column="2" size="small" bordered>
            <Descriptions.Item
              :label="$t('travel.country_list.fields.id')"
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
              :label="$t('travel.country_list.form.name_label')"
              name="name"
              prop="name"
              style="display: block; width: 100%; margin-bottom: 16px;"
            >
              <Input
                v-model:value="formData.name"
                class="w-full"
                :placeholder="$t('travel.country_list.form.name_required')"
              />
            </FormItem>

            <FormItem
              :label="$t('travel.country_list.form.chinese_label')"
              name="chineseName"
              prop="chineseName"
              style="display: block; width: 100%; margin-bottom: 16px;"
            >
              <Input
                v-model:value="formData.chineseName"
                class="w-full"
                :placeholder="$t('travel.country_list.form.chinese_label')"
              />
            </FormItem>

            <FormItem
              :label="$t('travel.country_list.form.code_label')"
              name="countryCode"
              prop="countryCode"
              style="display: block; width: 100%; margin-bottom: 16px;"
            >
              <Input
                v-model:value="formData.countryCode"
                class="w-full"
                :placeholder="$t('travel.country_list.form.code_label')"
              />
            </FormItem>

            <FormItem
              :label="$t('travel.country_list.form.emoji_label')"
              name="flagEmoji"
              prop="flagEmoji"
              style="display: block; width: 100%; margin-bottom: 16px;"
            >
              <Input
                v-model:value="formData.flagEmoji"
                class="w-full"
                :placeholder="$t('travel.country_list.form.emoji_label')"
              />
            </FormItem>

            <FormItem
              :label="$t('travel.country_list.form.capital_label')"
              name="capital"
              prop="capital"
              style="display: block; width: 100%; margin-bottom: 16px;"
            >
              <Input
                v-model:value="formData.capital"
                class="w-full"
                :placeholder="$t('travel.country_list.form.capital_label')"
              />
            </FormItem>

            <FormItem
              :label="$t('travel.country_list.form.continent_label')"
              name="continentId"
              prop="continentId"
              style="display: block; width: 100%; margin-bottom: 16px;"
            >
              <InputNumber
                v-model:value="formData.continentId"
                class="w-full"
                :min="1"
                :placeholder="$t('travel.country_list.form.continent_label')"
              />
            </FormItem>

            <FormItem
              :label="$t('travel.country_list.form.population_label')"
              name="population"
              prop="population"
              style="display: block; width: 100%; margin-bottom: 16px;"
            >
              <InputNumber
                v-model:value="formData.population"
                class="w-full"
                :min="0"
                :precision="0"
                :placeholder="$t('travel.country_list.form.population_label')"
              />
            </FormItem>

            <FormItem
              :label="$t('travel.country_list.form.area_label')"
              name="area"
              prop="area"
              style="display: block; width: 100%; margin-bottom: 16px;"
            >
              <InputNumber
                v-model:value="formData.area"
                class="w-full"
                :min="0"
                :placeholder="$t('travel.country_list.form.area_label')"
              />
            </FormItem>

            <FormItem
              :label="$t('travel.country_list.form.currency_label')"
              name="currency"
              prop="currency"
              style="display: block; width: 100%; margin-bottom: 16px;"
            >
              <Input
                v-model:value="formData.currency"
                class="w-full"
                :placeholder="$t('travel.country_list.form.currency_label')"
              />
            </FormItem>

            <FormItem
              :label="$t('travel.country_list.form.language_label')"
              name="language"
              prop="language"
              style="display: block; width: 100%; margin-bottom: 16px;"
            >
              <Input
                v-model:value="formData.language"
                class="w-full"
                :placeholder="$t('travel.country_list.form.language_label')"
              />
            </FormItem>

            <FormItem
              :label="$t('travel.country_list.form.timezone_label')"
              name="timezone"
              prop="timezone"
              style="display: block; width: 100%; margin-bottom: 16px;"
            >
              <Input
                v-model:value="formData.timezone"
                class="w-full"
                :placeholder="$t('travel.country_list.form.timezone_label')"
              />
            </FormItem>

            <FormItem
              :label="$t('travel.country_list.form.phone_label')"
              name="phoneCode"
              prop="phoneCode"
              style="display: block; width: 100%; margin-bottom: 16px;"
            >
              <Input
                v-model:value="formData.phoneCode"
                class="w-full"
                :placeholder="$t('travel.country_list.form.phone_label')"
              />
            </FormItem>

            <!-- 图片上传：全宽 -->
            <div class="col-span-2" style="width: 100%;">
              <FormItem
                :label="$t('travel.country_list.form.image_label')"
                name="image"
                prop="image"
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
                      :alt="$t('travel.country_list.form.image_label')"
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
                          formData.image
                            ? $t('travel.country_list.form.image_change')
                            : $t('travel.country_list.form.image_upload')
                        }}
                      </span>
                      <span class="text-xs text-muted-foreground">
                        {{ $t('travel.country_list.form.image_tip') }}
                      </span>
                    </div>
                  </Button>
                </Upload>
              </FormItem>
            </div>

            <!-- 简介：大文本域，全宽 -->
            <div class="col-span-2" style="width: 100%;">
              <FormItem
                :label="$t('travel.country_list.form.desc_label')"
                name="description"
                prop="description"
                style="display: block; width: 100%; margin-bottom: 16px;"
              >
                <Input.TextArea
                  v-model:value="formData.description"
                  :rows="4"
                  class="w-full"
                  :placeholder="$t('travel.country_list.form.desc_label')"
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