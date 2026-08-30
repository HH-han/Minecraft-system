<script lang="ts" setup>
import type { TableColumnsType } from 'antdv-next';

import { computed, onMounted, reactive, ref } from 'vue';

import { Page } from '@vben/common-ui';
import { $t } from '@vben/locales';

import {
  Button,
  Card,
  Descriptions,
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
  Table,
  Tag,
  Upload,
} from 'antdv-next';

import safetyTipsApi from '#/api/management/content/safetytips';
import { uploadFile } from '#/api/management/user/upload';

defineOptions({ name: 'SafetyTipsManagement' });

interface SafetyTip {
  categoryId?: null | number;
  createTime?: null | string;
  creatorId?: null | number;
  description?: null | string;
  id: number;
  imageUrl?: null | string;
  sortWeight?: null | number;
  status?: null | number;
  title?: null | string;
  updateTime?: null | string;
  updaterId?: null | number;
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

// =========================
// 状态
// =========================
const loading = ref(false);
const errorMsg = ref('');
const tipList = ref<SafetyTip[]>([]);
const imageErrorSet = reactive<Set<number>>(new Set());
const deletingId = ref<null | number>(null);

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

function isOn(status?: null | number): boolean {
  return Number(status) === 1;
}

// =========================
// 列定义（数组 → 前端分页）
// =========================
const columns = computed<TableColumnsType>(() => [
  { title: 'ID', dataIndex: 'id', width: 70 },
  { title: $t('content.safety_page.fields.imageUrl'), key: 'imageUrl', width: 90 },
  { title: $t('content.safety_page.fields.title'), key: 'title', width: 220, ellipsis: true },
  { title: $t('content.safety_page.fields.description'), dataIndex: 'description', ellipsis: true },
  { title: $t('content.safety_page.fields.categoryId'), key: 'categoryId', width: 90 },
  { title: $t('content.safety_page.fields.sortWeight'), key: 'sortWeight', width: 100 },
  { title: $t('content.safety_page.fields.status'), key: 'status', width: 90 },
  { title: $t('content.safety_page.fields.createTime'), key: 'createTime', width: 170 },
  { title: $t('content.safety_page.fields.action'), key: 'action', width: 180, fixed: 'right' },
]);

// =========================
// 数据加载（data 直接为数组）
// =========================
async function fetchData() {
  loading.value = true;
  errorMsg.value = '';
  try {
    const res: any = await safetyTipsApi.getActiveSafetyTips();
    tipList.value = Array.isArray(res)
      ? res
      : (res?.records ?? res?.data ?? res?.list ?? []);
    imageErrorSet.clear();
  } catch (error: any) {
    errorMsg.value = error?.message || $t('content.safety_page.error_desc');
  } finally {
    loading.value = false;
  }
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
const detailRecord = ref<null | SafetyTip>(null);

function openDetail(record: any) {
  detailRecord.value = record;
  detailVisible.value = true;
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
  description: '',
  imageUrl: '',
  sortWeight: 0 as null | number,
  status: 1 as number,
  title: '',
});

const modalTitle = computed(() =>
  editingId.value === null
    ? $t('content.safety_page.create_title')
    : $t('content.safety_page.edit_title'),
);

const statusOptions = computed(() => [
  { label: $t('content.safety_page.status.on'), value: 1 },
  { label: $t('content.safety_page.status.off'), value: 0 },
]);

function openCreate() {
  editingId.value = null;
  formRecord.title = '';
  formRecord.description = '';
  formRecord.imageUrl = '';
  formRecord.sortWeight = 0;
  formRecord.status = 1;
  modalVisible.value = true;
}

function openEdit(record: any) {
  editingId.value = record.id;
  formRecord.title = record.title ?? '';
  formRecord.description = record.description ?? '';
  formRecord.imageUrl = record.imageUrl ?? '';
  formRecord.sortWeight = record.sortWeight ?? 0;
  formRecord.status = Number(record.status ?? 1);
  modalVisible.value = true;
}

function closeModal() {
  modalVisible.value = false;
  editingId.value = null;
  formRef.value?.resetFields?.();
}

// =========================
// 图片上传
// =========================
async function handleImageUpload(file: File) {
  if (!file.type.startsWith('image/')) {
    message.error($t('content.safety_page.image_invalid_image'));
    return;
  }
  imageUploading.value = true;
  try {
    const res: any = await uploadFile(file);
    const relativePath =
      res?.url ?? res?.path ?? res?.filePath ?? res?.data?.url ?? '';
    if (!relativePath) {
      message.error($t('content.safety_page.image_upload_failed'));
      return;
    }
    formRecord.imageUrl = String(relativePath);
    message.success($t('content.safety_page.image_upload_success'));
  } catch (error: any) {
    message.error(
      error?.message || $t('content.safety_page.image_upload_failed'),
    );
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
    const payload: any = { ...formRecord };
    if (editingId.value !== null) payload.id = editingId.value;
    if (editingId.value === null) {
      await safetyTipsApi.addSafetyTip(payload);
    } else {
      await safetyTipsApi.updateSafetyTip(payload);
    }
    message.success($t('content.safety_page.save_success'));
    closeModal();
    await fetchData();
  } catch (err: any) {
    message.error(err?.message || $t('content.safety_page.save_failed'));
  } finally {
    submitting.value = false;
  }
}

// =========================
// 删除
// =========================
async function handleDelete(record: any) {
  deletingId.value = record.id;
  try {
    await safetyTipsApi.deleteSafetyTip(record.id);
    message.success($t('content.safety_page.delete_success'));
    await fetchData();
  } catch (err: any) {
    message.error(err?.message || $t('content.safety_page.delete_failed'));
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
        v-if="loading && tipList.length === 0"
        :title="$t('content.safety_page.title')"
        :styles="{ body: { padding: '24px' } }"
      >
        <Skeleton :active="true" :paragraph="{ rows: 8 }" />
      </Card>

      <!-- 加载失败：错误占位 + 重试 -->
      <Card
        v-else-if="errorMsg && tipList.length === 0"
        :title="$t('content.safety_page.title')"
        :styles="{ body: { padding: '24px' } }"
      >
        <Result
          status="error"
          :title="$t('content.safety_page.error_title')"
          :sub-title="errorMsg"
        >
          <template #extra>
            <Button type="primary" @click="fetchData">
              {{ $t('content.safety_page.retry') }}
            </Button>
          </template>
        </Result>
      </Card>

      <!-- 列表 -->
      <Card
        v-else
        :title="$t('content.safety_page.title')"
        :styles="{ body: { padding: '0' } }"
      >
        <template #extra>
          <Button type="primary" @click="openCreate">
            <template #icon>
              <span class="i-lucide-plus mr-1 size-4 align-middle"></span>
            </template>
            {{ $t('content.safety_page.add') }}
          </Button>
        </template>

        <Table
          :columns="columns"
          :data-source="tipList"
          :loading="loading"
          :pagination="{
            pageSize: 10,
            showSizeChanger: false,
          }"
          :scroll="{ x: 1290 }"
          size="small"
          row-key="id"
        >
          <template #bodyCell="{ column, record }">
            <!-- 配图（圆形缩略图） -->
            <template v-if="column.key === 'imageUrl'">
              <img
                v-if="record.imageUrl && !imageErrorSet.has(record.id)"
                :src="normalizeImageUrl(record.imageUrl)"
                :alt="record.title || 'tip'"
                loading="lazy"
                class="size-13.5 rounded-full border border-border object-cover"
                @error="handleImageError(record.id)"
              />
              <div
                v-else
                class="flex size-13.5 items-center justify-center rounded-full border border-border bg-accent"
              >
                <span class="i-lucide-image-off size-5 text-muted-foreground"></span>
              </div>
            </template>

            <!-- 标题 -->
            <template v-else-if="column.key === 'title'">
              <span class="font-medium">{{ displayValue(record.title) }}</span>
            </template>

            <!-- 分类 -->
            <template v-else-if="column.key === 'categoryId'">
              {{ displayValue(record.categoryId) }}
            </template>

            <!-- 排序权重 -->
            <template v-else-if="column.key === 'sortWeight'">
              <span class="tabular-nums">{{ record.sortWeight ?? 0 }}</span>
            </template>

            <!-- 状态 -->
            <template v-else-if="column.key === 'status'">
              <Tag :color="isOn(record.status) ? 'green' : 'default'">
                {{
                  isOn(record.status)
                    ? $t('content.safety_page.status.on')
                    : $t('content.safety_page.status.off')
                }}
              </Tag>
            </template>

            <!-- 创建时间 -->
            <template v-else-if="column.key === 'createTime'">
              <span class="text-xs text-muted-foreground">
                {{ formatTime(record.createTime) }}
              </span>
            </template>

            <!-- 操作 -->
            <template v-else-if="column.key === 'action'">
              <Button type="link" size="small" @click="openDetail(record)">
                {{ $t('content.safety_page.detail') }}
              </Button>
              <Button type="link" size="small" @click="openEdit(record)">
                {{ $t('content.safety_page.edit') }}
              </Button>
              <Popconfirm
                :title="$t('content.safety_page.delete_confirm_title')"
                :description="$t('content.safety_page.delete_confirm')"
                :ok-text="$t('content.safety_page.delete')"
                :cancel-text="$t('content.safety_page.cancel')"
                :ok-button-props="{ danger: true }"
                @confirm="handleDelete(record)"
              >
                <Button
                  type="link"
                  size="small"
                  danger
                  :loading="deletingId === record.id"
                >
                  {{ $t('content.safety_page.delete') }}
                </Button>
              </Popconfirm>
            </template>
          </template>

          <template #emptyText>
            <span class="text-sm text-muted-foreground">
              {{ $t('content.safety_page.list_empty') }}
            </span>
          </template>
        </Table>
      </Card>
    </div>

    <!-- 详情弹窗 -->
    <Modal
      v-model:open="detailVisible"
      :title="$t('content.safety_page.detail_title')"
      :width="640"
      :footer="null"
      :styles="{ body: { padding: '20px 24px' } }"
    >
      <template v-if="detailRecord">
        <div class="mb-4 flex items-center gap-4">
          <img
            v-if="detailRecord.imageUrl"
            :src="normalizeImageUrl(detailRecord.imageUrl)"
            :alt="detailRecord.title || 'tip'"
            class="size-20 rounded-full border border-border object-cover"
            @error="hideBrokenImage"
          />
          <div class="min-w-0">
            <div class="text-base font-semibold">
              {{ displayValue(detailRecord.title) }}
            </div>
            <div class="mt-2 flex flex-wrap items-center gap-2">
              <Tag :color="isOn(detailRecord.status) ? 'green' : 'default'">
                {{
                  isOn(detailRecord.status)
                    ? $t('content.safety_page.status.on')
                    : $t('content.safety_page.status.off')
                }}
              </Tag>
              <Tag v-if="detailRecord.categoryId !== null && detailRecord.categoryId !== undefined" color="blue">
                {{ $t('content.safety_page.fields.categoryId') }}: {{ detailRecord.categoryId }}
              </Tag>
            </div>
          </div>
        </div>

        <div
          v-if="detailRecord.description"
          class="mb-4 whitespace-pre-wrap rounded-md border border-border bg-accent/40 p-3 text-sm leading-6"
        >
          {{ detailRecord.description }}
        </div>

        <Descriptions :column="2" size="small" bordered>
          <Descriptions.Item :label="$t('content.safety_page.fields.id')">
            {{ detailRecord.id }}
          </Descriptions.Item>
          <Descriptions.Item
            :label="$t('content.safety_page.fields.sortWeight')"
          >
            {{ detailRecord.sortWeight ?? 0 }}
          </Descriptions.Item>
          <Descriptions.Item
            :label="$t('content.safety_page.fields.categoryId')"
          >
            {{ displayValue(detailRecord.categoryId) }}
          </Descriptions.Item>
          <Descriptions.Item
            :label="$t('content.safety_page.fields.createTime')"
          >
            {{ formatTime(detailRecord.createTime) }}
          </Descriptions.Item>
          <Descriptions.Item
            :label="$t('content.safety_page.fields.updateTime')"
          >
            {{ formatTime(detailRecord.updateTime) }}
          </Descriptions.Item>
        </Descriptions>
      </template>
    </Modal>

    <!-- 新增 / 编辑弹窗 -->
    <Modal
      v-model:open="modalVisible"
      :title="modalTitle"
      :width="600"
      :mask-closable="false"
      :confirm-loading="submitting"
      :ok-text="$t('content.safety_page.save')"
      :cancel-text="$t('content.safety_page.cancel')"
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
      <div
        v-if="editingId !== null"
        class="mb-4"
      >
        <Descriptions :column="2" size="small" bordered>
          <Descriptions.Item :label="$t('content.safety_page.fields.id')">
            {{ editingId }}
          </Descriptions.Item>
        </Descriptions>
      </div>

      <Form
        ref="formRef"
        :model="formRecord"
        layout="vertical"
        class="w-full"
      >
        <FormItem
          :label="$t('content.safety_page.fields.title')"
          name="title"
          :rules="[
            { required: true, message: $t('content.safety_page.title_required') },
          ]"
        >
          <Input
            v-model:value="formRecord.title"
            class="w-full"
            :placeholder="$t('content.safety_page.fields.title')"
          />
        </FormItem>

        <FormItem :label="$t('content.safety_page.fields.description')" name="description">
          <Input.TextArea
            v-model:value="formRecord.description"
            :rows="5"
            class="w-full"
            :placeholder="$t('content.safety_page.fields.description')"
          />
        </FormItem>

        <div class="grid grid-cols-1 gap-x-6 md:grid-cols-2">
          <FormItem :label="$t('content.safety_page.fields.sortWeight')" name="sortWeight">
            <InputNumber
              v-model:value="formRecord.sortWeight"
              class="w-full"
              :min="0"
              :placeholder="$t('content.safety_page.weight_placeholder')"
            />
          </FormItem>

          <FormItem :label="$t('content.safety_page.fields.status')" name="status">
            <Select v-model:value="formRecord.status" class="w-full" :options="statusOptions" />
          </FormItem>

          <FormItem :label="$t('content.safety_page.fields.imageUrl')" name="imageUrl" class="md:col-span-2">
            <div class="flex items-center gap-2">
              <Input
                v-model:value="formRecord.imageUrl"
                class="w-full"
                :placeholder="$t('content.safety_page.fields.imageUrl')"
              />
              <Upload
                :max-count="1"
                :show-upload-list="false"
                accept="image/*"
                :before-upload="() => false"
                @change="handleImageChange"
              >
                <Button :loading="imageUploading">
                  {{ $t('content.safety_page.image_upload') }}
                </Button>
              </Upload>
            </div>
            <img
              v-if="formRecord.imageUrl"
              :src="normalizeImageUrl(formRecord.imageUrl)"
              alt="preview"
              class="mt-2 size-12 rounded-full border border-border object-cover"
              @error="hideBrokenImage"
            />
          </FormItem>
        </div>
      </Form>
    </Modal>
  </Page>
</template>
