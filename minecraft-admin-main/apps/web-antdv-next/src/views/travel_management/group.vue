<script lang="ts" setup>
import type { TableColumnsType } from 'antdv-next';

import { computed, onMounted, reactive, ref } from 'vue';

import { Page } from '@vben/common-ui';
import { ImageIcon, ImageOff, MapPin, Plus } from '@vben/icons';
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
  createGroup,
  deleteGroup,
  getGroupList,
  updateGroup,
} from '#/api/management/travel/group';
import { uploadFile } from '#/api/management/user/upload';

defineOptions({ name: 'GroupTourManagement' });

// =========================
// 旅行团数据类型
// =========================
interface GroupFormState {
  id?: number | string;
  name: string;
  coverUrl: string;
  destination: string;
  description: string;
  startDate: string;
  endDate: string;
  maxMembers?: null | number;
  // "1" 进行中 / "0" 已结束
  status: number;
  // "1" 公开 / "0" 私密
  isPublic: string;
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

// 详情视图当前记录
const currentRecord = ref<any>(null);

function emptyForm(): GroupFormState {
  return {
    name: '',
    coverUrl: '',
    destination: '',
    description: '',
    startDate: '',
    endDate: '',
    maxMembers: undefined,
    status: 1,
    isPublic: '1',
  };
}

const formData = reactive<GroupFormState>(emptyForm());

const rules = computed(() => ({
  name: [
    {
      required: true,
      message: $t('travel.group_list.form.name_required'),
      trigger: 'blur',
    },
  ],
}));

const statusOptions = computed(() => [
  { label: $t('travel.group_list.status.on'), value: 1 },
  { label: $t('travel.group_list.status.off'), value: 0 },
]);

const visibilityOptions = computed(() => [
  { label: $t('travel.group_list.visibility.public'), value: '1' },
  { label: $t('travel.group_list.visibility.private'), value: '0' },
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
// 状态辅助
// =========================
/** 状态："1" 进行中 / "0" 已结束 */
function statusText(status: null | number | string | undefined): string {
  return String(status) === '1'
    ? $t('travel.group_list.status.on')
    : $t('travel.group_list.status.off');
}

function isActive(status: null | number | string | undefined): boolean {
  return String(status) === '1';
}

// =========================
// 数据加载 + 规范化
// 响应结构：data 直接是数组（无分页包装）→ 前端分页
// =========================
async function fetchData() {
  loading.value = true;
  errorMsg.value = '';
  try {
    const res: any = await getGroupList({});
    const records: any[] = Array.isArray(res)
      ? res
      : (res?.records ?? res?.list ?? res?.items ?? res?.rows ?? []);
    dataSource.value = normalizeList(records);
    pages.value = Array.isArray(res)
      ? 0
      : Number(res?.pages ?? res?.totalPages ?? 0);
    coverErrorSet.clear();
  } catch (error: any) {
    errorMsg.value = error?.message || $t('travel.group_list.error_desc');
  } finally {
    loading.value = false;
  }
}

/** 行数据规范化：coverUrl 补全域名 */
function normalizeList(records: any[]): any[] {
  return (records ?? []).map((raw) => {
    const row: any = { ...(raw ?? {}) };
    row.coverUrl = normalizeImageUrl(row.coverUrl);
    return row;
  });
}

onMounted(() => {
  fetchData();
});

// =========================
// 分页：pages=0 时由 Table 前端分页（total=当前数组长度）
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
  showTotal: (t: number) => $t('travel.group_list.total', { total: t }),
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
    title: $t('travel.group_list.fields.id'),
    dataIndex: 'id',
    width: 70,
    fixed: 'left',
  },
  {
    title: $t('travel.group_list.fields.coverUrl'),
    dataIndex: 'coverUrl',
    width: 110,
    fixed: 'left',
  },
  {
    title: $t('travel.group_list.fields.name'),
    dataIndex: 'name',
    width: 200,
    fixed: 'left',
    ellipsis: true,
  },
  {
    title: $t('travel.group_list.fields.destination'),
    dataIndex: 'destination',
    width: 180,
    ellipsis: true,
  },
  {
    title: $t('travel.group_list.fields.members'),
    key: 'members',
    width: 100,
  },
  {
    title: $t('travel.group_list.fields.startDate'),
    dataIndex: 'startDate',
    width: 120,
  },
  {
    title: $t('travel.group_list.fields.endDate'),
    dataIndex: 'endDate',
    width: 120,
  },
  {
    title: $t('travel.group_list.fields.status'),
    dataIndex: 'status',
    width: 100,
  },
  {
    title: $t('travel.group_list.fields.isPublic'),
    dataIndex: 'isPublic',
    width: 100,
  },
  {
    title: $t('travel.group_list.fields.creator'),
    key: 'creator',
    width: 120,
    ellipsis: true,
  },
  {
    title: $t('travel.group_list.fields.createdAt'),
    dataIndex: 'createdAt',
    width: 180,
  },
  {
    title: $t('travel.group_list.action.operation'),
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

/** 创建人显示：优先 creatorName，否则 #createdBy */
function creatorText(rec: any): string {
  if (rec.creatorName) return String(rec.creatorName);
  if (rec.createdBy !== null && rec.createdBy !== undefined)
    return `#${rec.createdBy}`;
  return '-';
}

/** 成员是否已满 */
function isMembersFull(rec: any): boolean {
  return (
    Number(rec.currentMembers) >= Number(rec.maxMembers) &&
    Number(rec.maxMembers) > 0
  );
}

/**
 * 把后端单行记录转换为弹窗表单字段
 */
function recordToForm(rec: any): GroupFormState {
  return {
    id: rec.id,
    name: String(rec.name ?? ''),
    coverUrl: normalizeImageUrl(rec.coverUrl),
    destination: String(rec.destination ?? ''),
    description: String(rec.description ?? ''),
    startDate: String(rec.startDate ?? ''),
    endDate: String(rec.endDate ?? ''),
    maxMembers:
      rec.maxMembers === null || rec.maxMembers === undefined
        ? undefined
        : Number(rec.maxMembers),
    status: Number(rec.status ?? 1),
    isPublic: rec.isPublic ? '1' : '0',
  };
}

// 表单内封面预览 URL（实时规范化）
const formCoverDisplayUrl = computed(() =>
  normalizeImageUrl(formData.coverUrl),
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
    return $t('travel.group_list.modal.create_title');
  if (modalMode.value === 'edit')
    return $t('travel.group_list.modal.edit_title');
  return $t('travel.group_list.modal.detail_title');
});

// =========================
// 封面图片上传
// =========================
async function handleCoverUpload(file: File) {
  if (!file) return;
  if (!file.type.startsWith('image/')) {
    message.error($t('travel.group_list.form.cover_invalid_image'));
    return;
  }
  coverUploading.value = true;
  try {
    const res: any = await uploadFile(file, '/group/upload');
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
      message.error($t('travel.group_list.form.cover_upload_failed'));
      return;
    }
    formData.coverUrl = String(relativePath);
    message.success($t('travel.group_list.form.cover_upload_success'));
  } catch (error: any) {
    const msg =
      error?.message || $t('travel.group_list.form.cover_upload_failed');
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
// 提交：新增（create）/ 修改（update）
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
      coverUrl: formData.coverUrl || null,
      destination: formData.destination || null,
      description: formData.description || null,
      startDate: formData.startDate || null,
      endDate: formData.endDate || null,
      maxMembers: formData.maxMembers ?? null,
      status: Number(formData.status),
      isPublic: formData.isPublic === '1',
    };
    if (formData.id !== undefined && formData.id !== null) {
      payload.id = formData.id;
      await updateGroup(payload);
      message.success($t('travel.group_list.action.update_success'));
    } else {
      await createGroup(payload);
      message.success($t('travel.group_list.action.create_success'));
    }
    closeModal();
    pageNum.value = 1;
    await fetchData();
  } catch (err: any) {
    const msg = err?.message || err?.msg || String(err || '');
    message.error(
      modalMode.value === 'create'
        ? msg || $t('travel.group_list.action.create_failed')
        : msg || $t('travel.group_list.action.update_failed'),
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
    await deleteGroup(record.id);
    message.success($t('travel.group_list.action.delete_success'));
    // 删除后若当前页空了，回退一页
    if (dataSource.value.length === 1 && pageNum.value > 1) {
      pageNum.value -= 1;
    }
    await fetchData();
  } catch (err: any) {
    const msg =
      err?.message || err?.msg || $t('travel.group_list.action.delete_failed');
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
          :title="$t('travel.group_list.error_title')"
          :sub-title="errorMsg"
        >
          <template #extra>
            <Button type="primary" @click="fetchData">
              {{ $t('travel.group_list.retry') }}
            </Button>
          </template>
        </Result>
      </Card>

      <!-- 旅行团列表 -->
      <Card
        v-else
        :title="$t('travel.group_list.title')"
        :styles="{ body: { padding: '0' } }"
      >
        <template #extra>
          <Button type="primary" :loading="loading" @click="openCreate">
            <template #icon>
              <Plus class="mr-1 size-4 align-middle" />
            </template>
            {{ $t('travel.group_list.action.add') }}
          </Button>
        </template>

        <Table
          :columns="columns"
          :data-source="dataSource"
          :pagination="pagination"
          :loading="loading"
          :scroll="{ x: 1830 }"
          :bordered="true"
          size="middle"
          row-key="id"
          @change="handleTableChange"
        >
          <template #bodyCell="{ column, record }">
            <!-- 封面缩略图（圆形） -->
            <template v-if="column.dataIndex === 'coverUrl'">
              <div class="cover-cell">
                <img
                  v-if="record.coverUrl && !isCoverFailed(record.id)"
                  :src="record.coverUrl"
                  :alt="record.name"
                  loading="lazy"
                  class="cover-thumb"
                  @error="handleCoverError(record.id)"
                />
                <span v-else class="cover-fallback">
                  {{ $t('travel.group_list.no_cover') }}
                </span>
              </div>
            </template>

            <!-- 成员 currentMembers/maxMembers -->
            <template v-else-if="column.key === 'members'">
              <Tag :color="isMembersFull(record) ? 'red' : 'green'">
                {{ record.currentMembers ?? 0 }}/{{ record.maxMembers ?? '-' }}
                {{
                  isMembersFull(record)
                    ? $t('travel.group_list.members_full')
                    : ''
                }}
              </Tag>
            </template>

            <!-- 状态："1" 进行中 / "0" 已结束 -->
            <template v-else-if="column.dataIndex === 'status'">
              <Tag :color="isActive(record.status) ? 'green' : 'default'">
                {{ statusText(record.status) }}
              </Tag>
            </template>

            <!-- 可见性：isPublic -->
            <template v-else-if="column.dataIndex === 'isPublic'">
              <Tag :color="record.isPublic ? 'blue' : 'default'">
                {{
                  record.isPublic
                    ? $t('travel.group_list.visibility.public')
                    : $t('travel.group_list.visibility.private')
                }}
              </Tag>
            </template>

            <!-- 创建人 -->
            <template v-else-if="column.key === 'creator'">
              {{ creatorText(record) }}
            </template>

            <!-- 创建时间 -->
            <template v-else-if="column.dataIndex === 'createdAt'">
              {{ record.createdAt || '-' }}
            </template>

            <!-- 操作列：详情 / 修改 / 删除 -->
            <template v-else-if="column.key === 'actions'">
              <Space :size="4" wrap>
                <Button type="link" size="small" @click="openDetail(record)">
                  {{ $t('travel.group_list.action.detail') }}
                </Button>
                <Button type="link" size="small" @click="openEdit(record)">
                  {{ $t('travel.group_list.action.edit') }}
                </Button>
                <Popconfirm
                  :title="$t('travel.group_list.action.confirm_delete_title')"
                  :description="$t('travel.group_list.action.confirm_delete')"
                  :ok-text="$t('travel.group_list.action.delete_text')"
                  :cancel-text="$t('travel.group_list.action.cancel')"
                  :ok-button-props="{ danger: true }"
                  @confirm="handleDelete(record)"
                >
                  <Button
                    type="link"
                    size="small"
                    danger
                    :loading="deletingId === record.id"
                  >
                    {{ $t('travel.group_list.action.delete') }}
                  </Button>
                </Popconfirm>
              </Space>
            </template>
          </template>

          <template #emptyText>
            <Empty :description="$t('travel.group_list.empty')" />
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
      :ok-text="$t('travel.group_list.action.save')"
      :cancel-text="$t('travel.group_list.action.cancel')"
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
        <!-- 头部：封面 + 名称 + 标签 -->
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
              <Tag :color="isActive(formData.status) ? 'green' : 'default'">
                {{ statusText(formData.status) }}
              </Tag>
              <Tag :color="formData.isPublic === '1' ? 'blue' : 'default'">
                {{
                  formData.isPublic === '1'
                    ? $t('travel.group_list.visibility.public')
                    : $t('travel.group_list.visibility.private')
                }}
              </Tag>
              <Tag
                :color="isMembersFull(currentRecord) ? 'red' : 'green'"
              >
                {{ currentRecord.currentMembers ?? 0 }}/{{ currentRecord.maxMembers ?? '-' }}
                {{
                  isMembersFull(currentRecord)
                    ? $t('travel.group_list.members_full')
                    : ''
                }}
              </Tag>
            </Space>
            <div
              v-if="formData.destination"
              class="mt-1 inline-flex items-center gap-1 text-sm text-muted-foreground"
            >
              <MapPin class="size-4" />
              {{ formData.destination }}
            </div>
          </div>
        </div>

        <Divider class="my-4">
          {{ $t('travel.group_list.detail.trip_info') }}
        </Divider>
        <Descriptions :column="2" bordered size="small">
          <Descriptions.Item :label="$t('travel.group_list.fields.startDate')">
            {{ formData.startDate || '-' }}
          </Descriptions.Item>
          <Descriptions.Item :label="$t('travel.group_list.fields.endDate')">
            {{ formData.endDate || '-' }}
          </Descriptions.Item>
          <Descriptions.Item :label="$t('travel.group_list.fields.members')">
            {{ currentRecord.currentMembers ?? 0 }} /
            {{ currentRecord.maxMembers ?? '-' }}
          </Descriptions.Item>
          <Descriptions.Item :label="$t('travel.group_list.fields.destination')">
            {{ formData.destination || '-' }}
          </Descriptions.Item>
        </Descriptions>

        <Divider class="my-4">
          {{ $t('travel.group_list.detail.creator_info') }}
        </Divider>
        <Descriptions :column="2" bordered size="small">
          <Descriptions.Item :label="$t('travel.group_list.fields.creator')">
            {{ creatorText(currentRecord) }}
          </Descriptions.Item>
          <Descriptions.Item :label="$t('travel.group_list.fields.id')">
            {{ (formData as any).id ?? '-' }}
          </Descriptions.Item>
          <Descriptions.Item :label="$t('travel.group_list.fields.createdAt')">
            {{ currentRecord.createdAt || '-' }}
          </Descriptions.Item>
          <Descriptions.Item :label="$t('travel.group_list.fields.updatedAt')">
            {{ currentRecord.updatedAt || '-' }}
          </Descriptions.Item>
        </Descriptions>

        <Divider class="my-4">
          {{ $t('travel.group_list.detail.desc_info') }}
        </Divider>
        <p class="m-0 whitespace-pre-wrap text-sm leading-6 text-muted-foreground">
          {{ formData.description || '-' }}
        </p>
      </div>

      <!-- 创建 / 修改：Form -->
      <div
        v-if="modalMode === 'create' || modalMode === 'edit'"
        class="group-form-wrapper"
        style="display: block; width: 100%;"
      >
        <!-- 编辑模式：展示 ID -->
        <div v-if="modalMode === 'edit' && (formData as any).id" class="mb-4">
          <Descriptions :column="2" size="small" bordered>
            <Descriptions.Item
              :label="$t('travel.group_list.fields.id')"
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
              :label="$t('travel.group_list.form.name_label')"
              name="name"
              prop="name"
              style="display: block; width: 100%; margin-bottom: 16px;"
            >
              <Input
                v-model:value="formData.name"
                class="w-full"
                :placeholder="$t('travel.group_list.form.name_required')"
              />
            </FormItem>

            <FormItem
              :label="$t('travel.group_list.form.destination_label')"
              name="destination"
              prop="destination"
              style="display: block; width: 100%; margin-bottom: 16px;"
            >
              <Input
                v-model:value="formData.destination"
                class="w-full"
                :placeholder="$t('travel.group_list.form.destination_label')"
              />
            </FormItem>

            <FormItem
              :label="$t('travel.group_list.form.startDate_label')"
              name="startDate"
              prop="startDate"
              :extra="$t('travel.group_list.form.startDate_tip')"
              style="display: block; width: 100%; margin-bottom: 16px;"
            >
              <Input
                v-model:value="formData.startDate"
                class="w-full"
                :placeholder="$t('travel.group_list.form.startDate_tip')"
              />
            </FormItem>

            <FormItem
              :label="$t('travel.group_list.form.endDate_label')"
              name="endDate"
              prop="endDate"
              :extra="$t('travel.group_list.form.endDate_tip')"
              style="display: block; width: 100%; margin-bottom: 16px;"
            >
              <Input
                v-model:value="formData.endDate"
                class="w-full"
                :placeholder="$t('travel.group_list.form.endDate_tip')"
              />
            </FormItem>

            <FormItem
              :label="$t('travel.group_list.form.max_label')"
              name="maxMembers"
              prop="maxMembers"
              style="display: block; width: 100%; margin-bottom: 16px;"
            >
              <InputNumber
                v-model:value="formData.maxMembers"
                class="w-full"
                :min="1"
                :precision="0"
                :placeholder="$t('travel.group_list.form.max_label')"
              />
            </FormItem>

            <FormItem
              :label="$t('travel.group_list.form.status_label')"
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
              :label="$t('travel.group_list.form.visibility_label')"
              name="isPublic"
              prop="isPublic"
              style="display: block; width: 100%; margin-bottom: 16px;"
            >
              <Select
                v-model:value="formData.isPublic"
                class="w-full"
                :options="visibilityOptions"
              />
            </FormItem>

            <!-- 封面上传：全宽 -->
            <div class="col-span-2" style="width: 100%;">
              <FormItem
                :label="$t('travel.group_list.form.cover_label')"
                name="coverUrl"
                prop="coverUrl"
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
                      :alt="$t('travel.group_list.form.cover_label')"
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
                          formData.coverUrl
                            ? $t('travel.group_list.form.cover_change')
                            : $t('travel.group_list.form.cover_upload')
                        }}
                      </span>
                      <span class="text-xs text-muted-foreground">
                        {{ $t('travel.group_list.form.cover_tip') }}
                      </span>
                    </div>
                  </Button>
                </Upload>
              </FormItem>
            </div>

            <!-- 简介：大文本域，全宽 -->
            <div class="col-span-2" style="width: 100%;">
              <FormItem
                :label="$t('travel.group_list.form.desc_label')"
                name="description"
                prop="description"
                style="display: block; width: 100%; margin-bottom: 16px;"
              >
                <Input.TextArea
                  v-model:value="formData.description"
                  :rows="4"
                  class="w-full"
                  :placeholder="$t('travel.group_list.form.desc_label')"
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