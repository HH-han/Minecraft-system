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
  addWorldCharacteristics,
  deleteWorldCharacteristics,
  getWorldCharacteristicsPage,
  updateWorldCharacteristics,
} from '#/api/management/travel/worldcharacteristics';
import { uploadFile } from '#/api/management/user/upload';

defineOptions({ name: 'WorldCharacteristicsManagement' });

// =========================
// 世界特色数据类型
// =========================
interface WorldFormState {
  id?: number | string;
  name: string;
  country: string;
  category: string;
  color: string;
  features: string;
  culture: string;
  history: string;
  tags: string;
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

function emptyForm(): WorldFormState {
  return {
    name: '',
    country: '',
    category: '',
    color: '',
    features: '',
    culture: '',
    history: '',
    tags: '',
    image: '',
    description: '',
  };
}

const formData = reactive<WorldFormState>(emptyForm());

const rules = computed(() => ({
  name: [
    {
      required: true,
      message: $t('travel.world_list.form.name_required'),
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
  if (/^https?:\/\//i.test(raw)) return raw;
  if (raw.startsWith('/')) return BACKEND_STATIC_BASE + raw;
  return `${BACKEND_STATIC_BASE}/${raw}`;
}

/** 逗号分隔字符串 → 标签数组 */
function splitList(value: null | string | undefined): string[] {
  if (!value) return [];
  return String(value)
    .split(',')
    .map((s) => s.trim())
    .filter((s) => s.length > 0);
}

// =========================
// 数据加载 + 规范化
// 响应结构（MyBatis-Plus Page）：{ records, total, size, current, pages }
// =========================
async function fetchData() {
  loading.value = true;
  errorMsg.value = '';
  try {
    const res: any = await getWorldCharacteristicsPage(
      pageNum.value,
      pageSize.value,
    );
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
    errorMsg.value = error?.message || $t('travel.world_list.error_desc');
  } finally {
    loading.value = false;
  }
}

/** 行数据规范化：image 补全域名 */
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
  showTotal: (t: number) => $t('travel.world_list.total', { total: t }),
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
    title: $t('travel.world_list.fields.id'),
    dataIndex: 'id',
    width: 70,
    fixed: 'left',
  },
  {
    title: $t('travel.world_list.fields.image'),
    dataIndex: 'image',
    width: 100,
    fixed: 'left',
  },
  {
    title: $t('travel.world_list.fields.name'),
    dataIndex: 'name',
    width: 150,
    fixed: 'left',
    ellipsis: true,
  },
  {
    title: $t('travel.world_list.fields.country'),
    dataIndex: 'country',
    width: 110,
  },
  {
    title: $t('travel.world_list.fields.category'),
    dataIndex: 'category',
    width: 120,
  },
  {
    title: $t('travel.world_list.fields.features'),
    dataIndex: 'features',
    width: 160,
    ellipsis: true,
  },
  {
    title: $t('travel.world_list.fields.culture'),
    dataIndex: 'culture',
    width: 200,
    ellipsis: true,
  },
  {
    title: $t('travel.world_list.fields.tags'),
    dataIndex: 'tags',
    width: 240,
  },
  {
    title: $t('travel.world_list.fields.color'),
    dataIndex: 'color',
    width: 120,
  },
  {
    title: $t('travel.world_list.action.operation'),
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

/** 主题色是否为有效色值（用于渲染色块） */
function isValidColor(color: null | string | undefined): boolean {
  return (
    !!color && /^#([0-9a-f]{3}|[0-9a-f]{6}|[0-9a-f]{8})$/i.test(String(color))
  );
}

/**
 * 把后端单行记录转换为弹窗表单字段
 */
function recordToForm(rec: any): WorldFormState {
  return {
    id: rec.id,
    name: String(rec.name ?? ''),
    country: String(rec.country ?? ''),
    category: String(rec.category ?? ''),
    color: String(rec.color ?? ''),
    features: String(rec.features ?? ''),
    culture: String(rec.culture ?? ''),
    history: String(rec.history ?? ''),
    tags: String(rec.tags ?? ''),
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
    return $t('travel.world_list.modal.create_title');
  if (modalMode.value === 'edit')
    return $t('travel.world_list.modal.edit_title');
  return $t('travel.world_list.modal.detail_title');
});

// =========================
// 图片上传
// =========================
async function handleImageUpload(file: File) {
  if (!file) return;
  if (!file.type.startsWith('image/')) {
    message.error($t('travel.world_list.form.image_invalid_image'));
    return;
  }
  imageUploading.value = true;
  try {
    const res: any = await uploadFile(file, '/world-characteristics/upload');
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
      message.error($t('travel.world_list.form.image_upload_failed'));
      return;
    }
    formData.image = String(relativePath);
    message.success($t('travel.world_list.form.image_upload_success'));
  } catch (error: any) {
    const msg =
      error?.message || $t('travel.world_list.form.image_upload_failed');
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
    const payload: any = {
      name: formData.name,
      country: formData.country || null,
      category: formData.category || null,
      color: formData.color || null,
      features: formData.features || null,
      culture: formData.culture || null,
      history: formData.history || null,
      tags: formData.tags || null,
      image: formData.image || null,
      description: formData.description || null,
    };
    if (formData.id !== undefined && formData.id !== null) {
      payload.id = formData.id;
      await updateWorldCharacteristics(payload);
      message.success($t('travel.world_list.action.update_success'));
    } else {
      await addWorldCharacteristics(payload);
      message.success($t('travel.world_list.action.create_success'));
    }
    closeModal();
    pageNum.value = 1;
    await fetchData();
  } catch (err: any) {
    const msg = err?.message || err?.msg || String(err || '');
    message.error(
      modalMode.value === 'create'
        ? msg || $t('travel.world_list.action.create_failed')
        : msg || $t('travel.world_list.action.update_failed'),
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
    await deleteWorldCharacteristics(record.id);
    message.success($t('travel.world_list.action.delete_success'));
    // 删除后若当前页空了，回退一页
    if (dataSource.value.length === 1 && pageNum.value > 1) {
      pageNum.value -= 1;
    }
    await fetchData();
  } catch (err: any) {
    const msg =
      err?.message || err?.msg || $t('travel.world_list.action.delete_failed');
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
          :title="$t('travel.world_list.error_title')"
          :sub-title="errorMsg"
        >
          <template #extra>
            <Button type="primary" @click="fetchData">
              {{ $t('travel.world_list.retry') }}
            </Button>
          </template>
        </Result>
      </Card>

      <!-- 世界特色列表 -->
      <Card
        v-else
        :title="$t('travel.world_list.title')"
        :styles="{ body: { padding: '0' } }"
      >
        <template #extra>
          <Button type="primary" :loading="loading" @click="openCreate">
            <template #icon>
              <Plus class="mr-1 size-4 align-middle" />
            </template>
            {{ $t('travel.world_list.action.add') }}
          </Button>
        </template>

        <Table
          :columns="columns"
          :data-source="dataSource"
          :pagination="pagination"
          :loading="loading"
          :scroll="{ x: 1790 }"
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
                  {{ $t('travel.world_list.no_image') }}
                </span>
              </div>
            </template>

            <!-- 分类 -->
            <template v-else-if="column.dataIndex === 'category'">
              <Tag v-if="record.category" color="blue">
                {{ record.category }}
              </Tag>
              <span v-else class="text-xs text-muted-foreground">-</span>
            </template>

            <!-- 标签（逗号字符串拆分，纯文本 Tag） -->
            <template v-else-if="column.dataIndex === 'tags'">
              <template v-if="splitList(record.tags).length > 0">
                <Tag v-for="tag in splitList(record.tags)" :key="tag" color="orange">
                  {{ tag }}
                </Tag>
              </template>
              <span v-else class="text-xs text-muted-foreground">-</span>
            </template>

            <!-- 主题色：色块 + 色值 -->
            <template v-else-if="column.dataIndex === 'color'">
              <span class="color-cell">
                <span
                  v-if="isValidColor(record.color)"
                  class="color-dot"
                  :style="{ backgroundColor: record.color }"
                ></span>
                {{ record.color || '-' }}
              </span>
            </template>

            <!-- 操作列：详情 / 修改 / 删除 -->
            <template v-else-if="column.key === 'actions'">
              <Space :size="4" wrap>
                <Button type="link" size="small" @click="openDetail(record)">
                  {{ $t('travel.world_list.action.detail') }}
                </Button>
                <Button type="link" size="small" @click="openEdit(record)">
                  {{ $t('travel.world_list.action.edit') }}
                </Button>
                <Popconfirm
                  :title="$t('travel.world_list.action.confirm_delete_title')"
                  :description="$t('travel.world_list.action.confirm_delete')"
                  :ok-text="$t('travel.world_list.action.delete_text')"
                  :cancel-text="$t('travel.world_list.action.cancel')"
                  :ok-button-props="{ danger: true }"
                  @confirm="handleDelete(record)"
                >
                  <Button
                    type="link"
                    size="small"
                    danger
                    :loading="deletingId === record.id"
                  >
                    {{ $t('travel.world_list.action.delete') }}
                  </Button>
                </Popconfirm>
              </Space>
            </template>
          </template>

          <template #emptyText>
            <Empty :description="$t('travel.world_list.empty')" />
          </template>
        </Table>
      </Card>
    </div>

    <!-- 新增 / 修改 / 详情 弹窗 -->
    <Modal
      v-model:open="modalVisible"
      :title="modalTitle"
      :width="680"
      :mask-closable="false"
      :confirm-loading="submitting"
      :ok-text="$t('travel.world_list.action.save')"
      :cancel-text="$t('travel.world_list.action.cancel')"
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
              {{ formData.name }}
            </div>
            <Space class="mt-1" :size="6">
              <Tag v-if="formData.country" color="blue">
                {{ formData.country }}
              </Tag>
              <Tag v-if="formData.category" color="gold">
                {{ formData.category }}
              </Tag>
              <span
                v-if="isValidColor(formData.color)"
                class="color-dot"
                :style="{ backgroundColor: formData.color }"
              ></span>
            </Space>
            <div
              v-if="splitList(formData.tags).length"
              class="mt-1 flex flex-wrap gap-1"
            >
              <Tag v-for="tag in splitList(formData.tags)" :key="tag" color="orange">
                {{ tag }}
              </Tag>
            </div>
          </div>
        </div>

        <Divider class="my-4">
          {{ $t('travel.world_list.detail.basic_info') }}
        </Divider>
        <Descriptions :column="2" bordered size="small">
          <Descriptions.Item :label="$t('travel.world_list.fields.id')">
            {{ (formData as any).id ?? '-' }}
          </Descriptions.Item>
          <Descriptions.Item :label="$t('travel.world_list.fields.country')">
            {{ formData.country || '-' }}
          </Descriptions.Item>
          <Descriptions.Item :label="$t('travel.world_list.fields.category')">
            {{ formData.category || '-' }}
          </Descriptions.Item>
          <Descriptions.Item :label="$t('travel.world_list.fields.color')">
            {{ formData.color || '-' }}
          </Descriptions.Item>
          <Descriptions.Item
            :label="$t('travel.world_list.fields.features')"
            :span="2"
          >
            {{ formData.features || '-' }}
          </Descriptions.Item>
        </Descriptions>

        <Divider class="my-4">
          {{ $t('travel.world_list.detail.culture_history') }}
        </Divider>
        <Descriptions :column="1" bordered size="small">
          <Descriptions.Item :label="$t('travel.world_list.fields.culture')">
            {{ formData.culture || '-' }}
          </Descriptions.Item>
          <Descriptions.Item :label="$t('travel.world_list.fields.history')">
            {{ formData.history || '-' }}
          </Descriptions.Item>
        </Descriptions>

        <Divider class="my-4">
          {{ $t('travel.world_list.detail.desc_info') }}
        </Divider>
        <p class="m-0 whitespace-pre-wrap text-sm leading-6 text-muted-foreground">
          {{ formData.description || '-' }}
        </p>
      </div>

      <!-- 创建 / 修改：Form -->
      <div
        v-if="modalMode === 'create' || modalMode === 'edit'"
        class="world-form-wrapper"
        style="display: block; width: 100%;"
      >
        <!-- 编辑模式：展示 ID -->
        <div v-if="modalMode === 'edit' && (formData as any).id" class="mb-4">
          <Descriptions :column="2" size="small" bordered>
            <Descriptions.Item
              :label="$t('travel.world_list.fields.id')"
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
              :label="$t('travel.world_list.form.name_label')"
              name="name"
              prop="name"
              style="display: block; width: 100%; margin-bottom: 16px;"
            >
              <Input
                v-model:value="formData.name"
                class="w-full"
                :placeholder="$t('travel.world_list.form.name_required')"
              />
            </FormItem>

            <FormItem
              :label="$t('travel.world_list.form.country_label')"
              name="country"
              prop="country"
              style="display: block; width: 100%; margin-bottom: 16px;"
            >
              <Input
                v-model:value="formData.country"
                class="w-full"
                :placeholder="$t('travel.world_list.form.country_label')"
              />
            </FormItem>

            <FormItem
              :label="$t('travel.world_list.form.category_label')"
              name="category"
              prop="category"
              style="display: block; width: 100%; margin-bottom: 16px;"
            >
              <Input
                v-model:value="formData.category"
                class="w-full"
                :placeholder="$t('travel.world_list.form.category_label')"
              />
            </FormItem>

            <FormItem
              :label="$t('travel.world_list.form.color_label')"
              name="color"
              prop="color"
              :extra="$t('travel.world_list.form.color_tip')"
              style="display: block; width: 100%; margin-bottom: 16px;"
            >
              <Input
                v-model:value="formData.color"
                class="w-full"
                placeholder="#FF0000"
              />
            </FormItem>

            <FormItem
              :label="$t('travel.world_list.form.features_label')"
              name="features"
              prop="features"
              style="display: block; width: 100%; margin-bottom: 16px;"
            >
              <Input
                v-model:value="formData.features"
                class="w-full"
                :placeholder="$t('travel.world_list.form.features_label')"
              />
            </FormItem>

            <FormItem
              :label="$t('travel.world_list.form.tags_label')"
              name="tags"
              prop="tags"
              :extra="$t('travel.world_list.form.tags_tip')"
              style="display: block; width: 100%; margin-bottom: 16px;"
            >
              <Input
                v-model:value="formData.tags"
                class="w-full"
                :placeholder="$t('travel.world_list.form.tags_tip')"
              />
            </FormItem>

            <FormItem
              :label="$t('travel.world_list.form.culture_label')"
              name="culture"
              prop="culture"
              style="display: block; width: 100%; margin-bottom: 16px;"
            >
              <Input
                v-model:value="formData.culture"
                class="w-full"
                :placeholder="$t('travel.world_list.form.culture_label')"
              />
            </FormItem>

            <FormItem
              :label="$t('travel.world_list.form.history_label')"
              name="history"
              prop="history"
              style="display: block; width: 100%; margin-bottom: 16px;"
            >
              <Input
                v-model:value="formData.history"
                class="w-full"
                :placeholder="$t('travel.world_list.form.history_label')"
              />
            </FormItem>

            <!-- 图片上传：全宽 -->
            <div class="col-span-2" style="width: 100%;">
              <FormItem
                :label="$t('travel.world_list.form.image_label')"
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
                      :alt="$t('travel.world_list.form.image_label')"
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
                            ? $t('travel.world_list.form.image_change')
                            : $t('travel.world_list.form.image_upload')
                        }}
                      </span>
                      <span class="text-xs text-muted-foreground">
                        {{ $t('travel.world_list.form.image_tip') }}
                      </span>
                    </div>
                  </Button>
                </Upload>
              </FormItem>
            </div>

            <!-- 简介：大文本域，全宽 -->
            <div class="col-span-2" style="width: 100%;">
              <FormItem
                :label="$t('travel.world_list.form.desc_label')"
                name="description"
                prop="description"
                style="display: block; width: 100%; margin-bottom: 16px;"
              >
                <Input.TextArea
                  v-model:value="formData.description"
                  :rows="4"
                  class="w-full"
                  :placeholder="$t('travel.world_list.form.desc_label')"
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

.color-cell {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
}

.color-dot {
  display: inline-block;
  width: 14px;
  height: 14px;
  border-radius: 50%;
  border: 1px solid rgb(0 0 0 / 12%);
  flex-shrink: 0;
}
</style>