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
  Select,
  Skeleton,
  Space,
  Table,
  Tag,
  Upload,
} from 'antdv-next';

import {
  addRecommend,
  deleteRecommend,
  getAllRecommendList,
  updateRecommend,
} from '#/api/management/travel/recommend';
import { uploadFile } from '#/api/management/user/upload';

defineOptions({ name: 'TravelRecommend' });

// =========================
// 推荐数据类型（简单结构）
// =========================
interface RecommendItem {
  id: number | string;
  name: string;
  image?: string;
  description?: string;
  details?: string;
  // 分类（后端可能为 null）
  type?: null | number | string;
  // 状态："0" 显示 / "1" 隐藏
  state?: null | number | string;
}

interface RecommendFormState {
  id?: number | string;
  name: string;
  image: string;
  description: string;
  details: string;
  type: string;
  state: string;
}

// =========================
// 基础状态
// =========================
const loading = ref(false);
const errorMsg = ref('');
const dataSource = ref<RecommendItem[]>([]);

// 分页状态：后端返回数组（无分页包装），pages=0 走前端分页
const pageNum = ref(1);
const pageSize = ref(10);
const pages = ref(0);

// 图片加载失败的推荐 id 集合
const imageErrorSet = reactive<Set<number | string>>(new Set());

// =========================
// 弹窗 / 表单状态
// =========================
type ModalMode = 'create' | 'detail' | 'edit' | null;
const modalMode = ref<ModalMode>(null);
const modalVisible = ref(false);
const submitting = ref(false);

const formRef = ref<InstanceType<typeof Form> | null>(null);

// 图片上传中
const imageUploading = ref(false);

function emptyForm(): RecommendFormState {
  return {
    name: '',
    image: '',
    description: '',
    details: '',
    type: '',
    // 新增默认：显示
    state: '0',
  };
}

const formData = reactive<RecommendFormState>(emptyForm());

const rules = computed(() => ({
  name: [
    {
      required: true,
      message: $t('travel.recommend_mgmt.form.name_required'),
      trigger: 'blur',
    },
  ],
}));

const stateOptions = computed(() => [
  { label: $t('travel.recommend_mgmt.state.on'), value: '0' },
  { label: $t('travel.recommend_mgmt.state.off'), value: '1' },
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
// 数据加载 + 规范化
// 响应结构：data 直接是数组（无 pageNum/pages 包装）→ 前端分页
// =========================
async function fetchData() {
  loading.value = true;
  errorMsg.value = '';
  try {
    // 管理端使用 /recommend/list/all，返回全部数据（含 state=1 隐藏项），
    // 修改 state 只改变赋值，不会导致行从列表中消失
    const res: any = await getAllRecommendList();
    const records: any[] = Array.isArray(res)
      ? res
      : (res?.records ?? res?.list ?? res?.items ?? res?.rows ?? []);
    dataSource.value = normalizeList(records);
    pageNum.value = Number(res?.pageNum ?? 1);
    pageSize.value = Number(res?.pageSize ?? pageSize.value);
    pages.value = Array.isArray(res)
      ? 0
      : Number(res?.pages ?? res?.totalPages ?? 0);
    imageErrorSet.clear();
  } catch (error: any) {
    errorMsg.value = error?.message || $t('travel.recommend_mgmt.error_desc');
  } finally {
    loading.value = false;
  }
}

function normalizeList(records: any[]): RecommendItem[] {
  return (records ?? []).map((raw) => {
    const row: any = { ...(raw ?? {}) };
    row.image = normalizeImageUrl(row.image);
    return row as RecommendItem;
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
  showTotal: (t: number) => $t('travel.recommend_mgmt.total', { total: t }),
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
    title: $t('travel.recommend_mgmt.fields.id'),
    dataIndex: 'id',
    width: 80,
    fixed: 'left',
  },
  {
    title: $t('travel.recommend_mgmt.fields.image'),
    dataIndex: 'image',
    width: 110,
    fixed: 'left',
  },
  {
    title: $t('travel.recommend_mgmt.fields.name'),
    dataIndex: 'name',
    width: 200,
    fixed: 'left',
    ellipsis: true,
  },
  {
    title: $t('travel.recommend_mgmt.fields.description'),
    dataIndex: 'description',
    width: 340,
    ellipsis: true,
  },
  {
    title: $t('travel.recommend_mgmt.fields.type'),
    dataIndex: 'type',
    width: 90,
  },
  {
    title: $t('travel.recommend_mgmt.fields.state'),
    dataIndex: 'state',
    width: 100,
  },
  {
    title: $t('travel.recommend_mgmt.action.operation'),
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

/** 状态："0" 显示 / "1" 隐藏 */
function stateText(state: null | number | string | undefined): string {
  return String(state) === '0'
    ? $t('travel.recommend_mgmt.state.on')
    : $t('travel.recommend_mgmt.state.off');
}

/** 状态是否为显示（"0"） */
function isShown(state: null | number | string | undefined): boolean {
  return String(state) === '0';
}

/**
 * 把后端单行记录转换为弹窗表单字段
 */
function recordToForm(rec: any): RecommendFormState {
  return {
    id: rec.id,
    name: String(rec.name ?? ''),
    image: normalizeImageUrl(rec.image),
    description: String(rec.description ?? ''),
    details: String(rec.details ?? ''),
    type: rec.type === null || rec.type === undefined ? '' : String(rec.type),
    state: String(rec.state ?? '0'),
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
  if (modalMode.value === 'create')
    return $t('travel.recommend_mgmt.modal.create_title');
  if (modalMode.value === 'edit')
    return $t('travel.recommend_mgmt.modal.edit_title');
  return $t('travel.recommend_mgmt.modal.detail_title');
});

// =========================
// 图片上传
// =========================
async function handleImageUpload(file: File) {
  if (!file) return;
  if (!file.type.startsWith('image/')) {
    message.error($t('travel.recommend_mgmt.form.image_invalid_image'));
    return;
  }
  imageUploading.value = true;
  try {
    const res: any = await uploadFile(file, '/recommend/upload');
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
      message.error($t('travel.recommend_mgmt.form.image_upload_failed'));
      return;
    }
    // 存相对路径，由 normalizeImageUrl 统一补全展示
    formData.image = String(relativePath);
    message.success($t('travel.recommend_mgmt.form.image_upload_success'));
  } catch (error: any) {
    const msg =
      error?.message || $t('travel.recommend_mgmt.form.image_upload_failed');
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
      image: formData.image,
      description: formData.description,
      details: formData.details,
      // 分类为空时提交 null，对齐后端 type 可空字段
      type: formData.type === '' ? null : formData.type,
      state: formData.state,
    };
    if (formData.id !== undefined && formData.id !== null) {
      payload.id = formData.id;
      await updateRecommend(payload);
      message.success($t('travel.recommend_mgmt.action.update_success'));
    } else {
      await addRecommend(payload);
      message.success($t('travel.recommend_mgmt.action.create_success'));
    }
    closeModal();
    // 新增/修改后回到第一页，确保新数据可见
    pageNum.value = 1;
    await fetchData();
  } catch (err: any) {
    const msg = err?.message || err?.msg || String(err || '');
    message.error(
      modalMode.value === 'create'
        ? msg || $t('travel.recommend_mgmt.action.create_failed')
        : msg || $t('travel.recommend_mgmt.action.update_failed'),
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
    await deleteRecommend(record.id);
    message.success($t('travel.recommend_mgmt.action.delete_success'));
    // 删除后若当前页空了，回退一页
    if (dataSource.value.length === 1 && pageNum.value > 1) {
      pageNum.value -= 1;
    }
    await fetchData();
  } catch (err: any) {
    const msg =
      err?.message ||
      err?.msg ||
      $t('travel.recommend_mgmt.action.delete_failed');
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
          :title="$t('travel.recommend_mgmt.error_title')"
          :sub-title="errorMsg"
        >
          <template #extra>
            <Button type="primary" @click="fetchData">
              {{ $t('travel.recommend_mgmt.retry') }}
            </Button>
          </template>
        </Result>
      </Card>

      <!-- 推荐列表 -->
      <Card
        v-else
        :title="$t('travel.recommend_mgmt.title')"
        :styles="{ body: { padding: '0' } }"
      >
        <template #extra>
          <Button type="primary" :loading="loading" @click="openCreate">
            <template #icon>
              <Plus class="mr-1 size-4 align-middle" />
            </template>
            {{ $t('travel.recommend_mgmt.action.add') }}
          </Button>
        </template>

        <Table
          :columns="columns"
          :data-source="dataSource"
          :pagination="pagination"
          :loading="loading"
          :scroll="{ x: 1180 }"
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
                  {{ $t('travel.recommend_mgmt.no_image') }}
                </span>
              </div>
            </template>

            <!-- 分类 -->
            <template v-else-if="column.dataIndex === 'type'">
              <span
                v-if="record.type !== null && record.type !== undefined && record.type !== ''"
              >
                {{ record.type }}
              </span>
              <span v-else class="text-xs text-muted-foreground">-</span>
            </template>

            <!-- 状态："0" 显示 / "1" 隐藏 -->
            <template v-else-if="column.dataIndex === 'state'">
              <Tag :color="isShown(record.state) ? 'green' : 'default'">
                {{ stateText(record.state) }}
              </Tag>
            </template>

            <!-- 操作列：详情 / 修改 / 删除 -->
            <template v-else-if="column.key === 'actions'">
              <Space :size="4" wrap>
                <Button type="link" size="small" @click="openDetail(record)">
                  {{ $t('travel.recommend_mgmt.action.detail') }}
                </Button>
                <Button type="link" size="small" @click="openEdit(record)">
                  {{ $t('travel.recommend_mgmt.action.edit') }}
                </Button>
                <Popconfirm
                  :title="$t('travel.recommend_mgmt.action.confirm_delete_title')"
                  :description="$t('travel.recommend_mgmt.action.confirm_delete')"
                  :ok-text="$t('travel.recommend_mgmt.action.delete_text')"
                  :cancel-text="$t('travel.recommend_mgmt.action.cancel')"
                  :ok-button-props="{ danger: true }"
                  @confirm="handleDelete(record)"
                >
                  <Button
                    type="link"
                    size="small"
                    danger
                    :loading="deletingId === record.id"
                  >
                    {{ $t('travel.recommend_mgmt.action.delete') }}
                  </Button>
                </Popconfirm>
              </Space>
            </template>
          </template>

          <template #emptyText>
            <Empty :description="$t('travel.recommend_mgmt.empty')" />
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
      :ok-text="$t('travel.recommend_mgmt.action.save')"
      :cancel-text="$t('travel.recommend_mgmt.action.cancel')"
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
            <div class="text-xl font-semibold truncate">{{ formData.name }}</div>
            <Space class="mt-1" :size="6">
              <Tag :color="isShown(formData.state) ? 'green' : 'default'">
                {{ stateText(formData.state) }}
              </Tag>
              <Tag v-if="formData.type" color="blue">
                {{ $t('travel.recommend_mgmt.fields.type') }} {{ formData.type }}
              </Tag>
            </Space>
          </div>
        </div>

        <Divider class="my-4">
          {{ $t('travel.recommend_mgmt.fields.description') }}
        </Divider>
        <p class="m-0 text-sm leading-6 text-muted-foreground">
          {{ formData.description || '-' }}
        </p>

        <Divider class="my-4">
          {{ $t('travel.recommend_mgmt.fields.details') }}
        </Divider>
        <p class="m-0 whitespace-pre-wrap text-sm leading-6 text-muted-foreground">
          {{ formData.details || '-' }}
        </p>

        <Divider class="my-4">
          {{ $t('travel.recommend_mgmt.action.operation') }}
        </Divider>
        <Descriptions :column="2" bordered size="small">
          <Descriptions.Item :label="$t('travel.recommend_mgmt.fields.id')">
            {{ (formData as any).id ?? '-' }}
          </Descriptions.Item>
          <Descriptions.Item :label="$t('travel.recommend_mgmt.fields.type')">
            {{ formData.type || '-' }}
          </Descriptions.Item>
          <Descriptions.Item :label="$t('travel.recommend_mgmt.fields.state')">
            {{ stateText(formData.state) }}
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
              :label="$t('travel.recommend_mgmt.fields.id')"
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
              :label="$t('travel.recommend_mgmt.form.name_label')"
              name="name"
              prop="name"
              style="display: block; width: 100%; margin-bottom: 16px;"
            >
              <Input
                v-model:value="formData.name"
                class="w-full"
                :placeholder="$t('travel.recommend_mgmt.form.name_required')"
              />
            </FormItem>

            <FormItem
              :label="$t('travel.recommend_mgmt.form.type_label')"
              name="type"
              prop="type"
              style="display: block; width: 100%; margin-bottom: 16px;"
            >
              <Input
                v-model:value="formData.type"
                class="w-full"
                :placeholder="$t('travel.recommend_mgmt.form.type_label')"
              />
            </FormItem>

            <FormItem
              :label="$t('travel.recommend_mgmt.form.state_label')"
              name="state"
              prop="state"
              style="display: block; width: 100%; margin-bottom: 16px;"
            >
              <Select
                v-model:value="formData.state"
                class="w-full"
                :options="stateOptions"
              />
            </FormItem>

            <FormItem
              :label="$t('travel.recommend_mgmt.form.description_label')"
              name="description"
              prop="description"
              style="display: block; width: 100%; margin-bottom: 16px;"
            >
              <Input
                v-model:value="formData.description"
                class="w-full"
                :placeholder="$t('travel.recommend_mgmt.form.description_label')"
              />
            </FormItem>

            <!-- 图片上传：全宽 -->
            <div class="col-span-2" style="width: 100%;">
              <FormItem
                :label="$t('travel.recommend_mgmt.form.image_label')"
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
                      :alt="$t('travel.recommend_mgmt.form.image_label')"
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
                            ? $t('travel.recommend_mgmt.form.image_change')
                            : $t('travel.recommend_mgmt.form.image_upload')
                        }}
                      </span>
                      <span class="text-xs text-muted-foreground">
                        {{ $t('travel.recommend_mgmt.form.image_tip') }}
                      </span>
                    </div>
                  </Button>
                </Upload>
              </FormItem>
            </div>

            <!-- 详情：大文本域，全宽 -->
            <div class="col-span-2" style="width: 100%;">
              <FormItem
                :label="$t('travel.recommend_mgmt.form.details_label')"
                name="details"
                prop="details"
                style="display: block; width: 100%; margin-bottom: 16px;"
              >
                <Input.TextArea
                  v-model:value="formData.details"
                  :rows="5"
                  class="w-full"
                  :placeholder="$t('travel.recommend_mgmt.form.details_label')"
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