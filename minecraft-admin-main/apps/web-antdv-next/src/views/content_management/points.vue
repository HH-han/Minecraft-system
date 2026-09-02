<script lang="ts" setup>
import type { TableColumnsType } from 'antdv-next';

import { computed, markRaw, onMounted, reactive, ref } from 'vue';

import { Page } from '@vben/common-ui';
import {
  CircleCheck,
  ImageOff,
  Package,
  PackageX,
  Plus,
  RefreshCw,
  Search,
} from '@vben/icons';
import { $t } from '@vben/locales';

import {
  Button,
  Card,
  Descriptions,
  Image,
  Input,
  Modal,
  Result,
  Skeleton,
  Table,
  Tag,
  Upload,
  message,
} from 'antdv-next';

import {
  getProducts,
} from '#/api/management/content/points';
import { uploadFile } from '#/api/management/user/upload';

defineOptions({ name: 'PointsProductsManagement' });

/** 库存紧张阈值 */
const LOW_STOCK_THRESHOLD = 10;

interface PointsProduct {
  createTime?: null | string;
  description?: null | string;
  id: number;
  imageUrl?: null | string;
  name?: null | string;
  pointsPrice?: null | number;
  status?: null | number;
  stock?: null | number;
  updateTime?: null | string;
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
const productList = ref<PointsProduct[]>([]);
const imageErrorSet = reactive<Set<number>>(new Set());
const keyword = ref('');

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

function toNumber(value: any): number {
  const n = Number(value);
  return Number.isFinite(n) ? n : 0;
}

// =========================
// 统计（从列表计算）
// =========================
const activeCount = computed(
  () => productList.value.filter((p) => isOn(p.status)).length,
);
const lowStockCount = computed(
  () =>
    productList.value.filter(
      (p) => toNumber(p.stock) < LOW_STOCK_THRESHOLD,
    ).length,
);

const statCards = computed(() => [
  {
    key: 'total',
    label: $t('content.points_page.stat_total'),
    value: productList.value.length,
    icon: markRaw(Package),
    color: '#1677ff',
  },
  {
    key: 'active',
    label: $t('content.points_page.stat_active'),
    value: activeCount.value,
    icon: markRaw(CircleCheck),
    color: '#52c41a',
  },
  {
    key: 'low',
    label: $t('content.points_page.stat_low_stock'),
    value: lowStockCount.value,
    icon: markRaw(PackageX),
    color: '#f5222d',
  },
]);

// =========================
// 过滤（名称 / 描述）
// =========================
const filteredProducts = computed(() => {
  const kw = keyword.value.trim().toLowerCase();
  if (!kw) return productList.value;
  return productList.value.filter(
    (p) =>
      (p.name ?? '').toLowerCase().includes(kw) ||
      (p.description ?? '').toLowerCase().includes(kw),
  );
});

// =========================
// 列定义（数组 → 前端分页）
// =========================
const columns = computed<TableColumnsType>(() => [
  { title: 'ID', dataIndex: 'id', width: 70 },
  { title: $t('content.points_page.fields.imageUrl'), key: 'imageUrl', width: 90 },
  { title: $t('content.points_page.fields.name'), key: 'name', width: 200, ellipsis: true },
  { title: $t('content.points_page.fields.description'), dataIndex: 'description', ellipsis: true },
  { title: $t('content.points_page.fields.pointsPrice'), key: 'pointsPrice', width: 110 },
  { title: $t('content.points_page.fields.stock'), key: 'stock', width: 100 },
  { title: $t('content.points_page.fields.status'), key: 'status', width: 90 },
  { title: $t('content.points_page.fields.createTime'), key: 'createTime', width: 170 },
  { title: $t('content.points_page.fields.action'), key: 'action', width: 170, fixed: 'right' },
]);

// =========================
// 数据加载（data 直接为数组）
// =========================
async function fetchData() {
  loading.value = true;
  errorMsg.value = '';
  try {
    const res: any = await getProducts();
    productList.value = Array.isArray(res)
      ? res
      : (res?.records ?? res?.data ?? res?.list ?? []);
    imageErrorSet.clear();
  } catch (error: any) {
    errorMsg.value = error?.message || $t('content.points_page.error_desc');
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
// 新增 / 编辑
// =========================
const modalVisible = ref(false);
const submitting = ref(false);
const formRef = ref<InstanceType<typeof Form> | null>(null);
const editingId = ref<null | number>(null);

const formRecord = reactive({
  description: '',
  imageUrl: '',
  name: '',
  pointsPrice: 0 as number,
  status: 0 as number,
  stock: 0 as number,
});

// =========================
// 图片上传（单图：商品封面）
// =========================
const imageUploading = ref(false);

async function handleImageUpload(file: File) {
  if (!file.type.startsWith('image/')) {
    message.error('请选择图片文件');
    return;
  }
  if (file.size > 2 * 1024 * 1024) {
    message.error('图片大小不能超过 2MB');
    return;
  }
  imageUploading.value = true;
  try {
    const res: any = await uploadFile(file, '/upload/file');
    // requestClient 解包成功响应后 data 即 URL 字符串，兼容对象形式
    const relativePath =
      typeof res === 'string'
        ? res
        : String(
            res?.url ??
              res?.path ??
              res?.data ??
              '',
          );
    if (!relativePath) {
      message.error('图片上传失败');
      return;
    }
    formRecord.imageUrl = String(relativePath);
    message.success('图片上传成功');
  } catch (error: any) {
    message.error(error?.message || '图片上传失败');
  } finally {
    imageUploading.value = false;
  }
}

function handleImageChange(event: any) {
  // AntDV 在 before-upload=false 模式下文件通过 originFileObj 暴露
  const file =
    event?.fileList?.[0]?.originFileObj ?? event?.fileList?.[0]?.raw;
  if (file) handleImageUpload(file as File);
}

function removeCoverImage() {
  formRecord.imageUrl = '';
}

const modalTitle = computed(() =>
  editingId.value === null
    ? $t('content.points_page.create_title')
    : $t('content.points_page.edit_title'),
);

const statusOptions = computed(() => [
  { label: $t('content.points_page.status.on'), value: 1 },
  { label: $t('content.points_page.status.off'), value: 0 },
]);

function validatePrice(_rule: any, value: any): Promise<void> {
  const n = Number(value);
  if (
    value === null ||
    value === undefined ||
    value === '' ||
    !Number.isFinite(n) ||
    n < 0
  ) {
    return Promise.reject(new Error($t('content.points_page.price_invalid')));
  }
  return Promise.resolve();
}

function validateStock(_rule: any, value: any): Promise<void> {
  const n = Number(value);
  if (
    value === null ||
    value === undefined ||
    value === '' ||
    !Number.isFinite(n) ||
    n < 0 ||
    !Number.isInteger(n)
  ) {
    return Promise.reject(new Error($t('content.points_page.stock_invalid')));
  }
  return Promise.resolve();
}

function openCreate() {
  editingId.value = null;
  formRecord.name = '';
  formRecord.pointsPrice = 0;
  formRecord.stock = 0;
  formRecord.status = 0; // 后端默认下架
  formRecord.imageUrl = '';
  formRecord.description = '';
  modalVisible.value = true;
}

function openEdit(record: any) {
  editingId.value = record.id;
  formRecord.name = record.name ?? '';
  formRecord.pointsPrice = toNumber(record.pointsPrice);
  formRecord.stock = toNumber(record.stock);
  formRecord.status = Number(record.status ?? 0);
  formRecord.imageUrl = record.imageUrl ?? '';
  formRecord.description = record.description ?? '';
  modalVisible.value = true;
}

function closeModal() {
  modalVisible.value = false;
  editingId.value = null;
  formRef.value?.resetFields?.();
}

async function handleSave() {
  try {
    await formRef.value?.validate();
  } catch {
    return;
  }
  submitting.value = true;
  try {
    const payload: any = {
      name: formRecord.name.trim(),
      description: formRecord.description || null,
      imageUrl: formRecord.imageUrl || null,
      pointsPrice: formRecord.pointsPrice,
      stock: formRecord.stock,
      status: formRecord.status,
    };
    if (editingId.value === null) {
      await addProduct(payload);
    } else {
      payload.id = editingId.value;
      await updateProduct(editingId.value, payload);
    }
    message.success($t('content.points_page.save_success'));
    closeModal();
    await fetchData();
  } catch (err: any) {
    message.error(err?.message || $t('content.points_page.save_failed'));
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
    await deleteProduct(record.id);
    message.success($t('content.points_page.delete_success'));
    await fetchData();
  } catch (err: any) {
    message.error(err?.message || $t('content.points_page.delete_failed'));
  } finally {
    deletingId.value = null;
  }
}

// =========================
// 详情弹窗
// =========================
const detailVisible = ref(false);
const detailRecord = ref<null | PointsProduct>(null);

function openDetail(record: any) {
  detailRecord.value = record;
  detailVisible.value = true;
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
</script>

<template>
  <Page :auto-content-height="true">
    <div class="mx-auto w-full">
      <!-- 加载中：骨架屏（仅首次加载） -->
      <Card
        v-if="loading && productList.length === 0"
        :title="$t('content.points_page.title')"
        :styles="{ body: { padding: '24px' } }"
      >
        <Skeleton :active="true" :paragraph="{ rows: 8 }" />
      </Card>

      <!-- 加载失败：错误占位 + 重试 -->
      <Card
        v-else-if="errorMsg && productList.length === 0"
        :title="$t('content.points_page.title')"
        :styles="{ body: { padding: '24px' } }"
      >
        <Result
          status="error"
          :title="$t('content.points_page.error_title')"
          :sub-title="errorMsg"
        >
          <template #extra>
            <Button type="primary" @click="fetchData">
              {{ $t('content.points_page.retry') }}
            </Button>
          </template>
        </Result>
      </Card>

      <template v-else>
        <!-- 统计卡 -->
        <div class="mb-4 grid grid-cols-3 gap-4">
          <Card
            v-for="card in statCards"
            :key="card.key"
            :styles="{ body: { padding: '16px 20px' } }"
          >
            <div class="flex items-center gap-4">
              <div
                class="flex size-11 shrink-0 items-center justify-center rounded-full"
                :style="{ backgroundColor: `${card.color}1a` }"
              >
                <component
                  :is="card.icon"
                  class="size-5"
                  :style="{ color: card.color }"
                />
              </div>
              <div class="min-w-0">
                <div class="truncate text-xs text-muted-foreground">
                  {{ card.label }}
                </div>
                <div class="text-xl font-semibold tabular-nums">
                  {{ card.value }}
                  <span class="text-xs font-normal text-muted-foreground">
                    {{ $t('content.points_page.unit_item') }}
                  </span>
                </div>
              </div>
            </div>
          </Card>
        </div>

        <!-- 商品列表 -->
        <Card
          :title="$t('content.points_page.title')"
          :styles="{ body: { padding: '0' } }"
        >
          <template #extra>
            <div class="flex items-center gap-2">
              <Input
                v-model:value="keyword"
                :placeholder="$t('content.points_page.search_placeholder')"
                allow-clear
                class="w-56"
              >
                <template #prefix>
                  <Search class="size-4 text-muted-foreground" />
                </template>
              </Input>
              <Button :loading="loading" @click="fetchData">
                <template #icon>
                  <RefreshCw class="mr-1 size-4 align-middle" />
                </template>
                {{ $t('content.points_page.refresh') }}
              </Button>
              <Button type="primary" @click="openCreate">
                <template #icon>
                  <Plus class="mr-1 size-4 align-middle" />
                </template>
                {{ $t('content.points_page.add') }}
              </Button>
            </div>
          </template>

          <Table
            :columns="columns"
            :data-source="filteredProducts"
            :loading="loading"
            :pagination="{
              pageSize: 10,
              showSizeChanger: false,
            }"
            :scroll="{ x: 1200 }"
            size="small"
            row-key="id"
          >
            <template #bodyCell="{ column, record }">
              <!-- 商品图（圆形缩略图，空 URL 走占位） -->
              <template v-if="column.key === 'imageUrl'">
                <img
                  v-if="record.imageUrl && !imageErrorSet.has(record.id)"
                  :src="normalizeImageUrl(record.imageUrl)"
                  :alt="record.name || 'product'"
                  loading="lazy"
                  class="size-13.5 rounded-full border border-border object-cover"
                  @error="handleImageError(record.id)"
                />
                <div
                  v-else
                  class="flex size-13.5 items-center justify-center rounded-full border border-border bg-accent"
                >
                  <ImageOff class="size-5 text-muted-foreground" />
                </div>
              </template>

              <!-- 名称 -->
              <template v-else-if="column.key === 'name'">
                <span class="font-medium">{{ displayValue(record.name) }}</span>
              </template>

              <!-- 兑换积分 -->
              <template v-else-if="column.key === 'pointsPrice'">
                <Tag color="gold" class="tabular-nums">
                  {{ toNumber(record.pointsPrice) }}
                  {{ $t('content.points_page.points_unit') }}
                </Tag>
              </template>

              <!-- 库存 -->
              <template v-else-if="column.key === 'stock'">
                <span
                  class="tabular-nums"
                  :class="
                    toNumber(record.stock) < LOW_STOCK_THRESHOLD
                      ? 'font-medium text-red-500'
                      : ''
                  "
                >
                  {{ toNumber(record.stock) }}
                </span>
              </template>

              <!-- 状态 -->
              <template v-else-if="column.key === 'status'">
                <Tag :color="isOn(record.status) ? 'green' : 'default'">
                  {{
                    isOn(record.status)
                      ? $t('content.points_page.status.on')
                      : $t('content.points_page.status.off')
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
                <div class="flex flex-nowrap items-center whitespace-nowrap">
                  <Button type="link" size="small" @click="openDetail(record)">
                    {{ $t('content.points_page.detail') }}
                  </Button>
                  <Button type="link" size="small" @click="openEdit(record)">
                    {{ $t('content.points_page.edit') }}
                  </Button>
                  <Popconfirm
                    :title="$t('content.points_page.delete_confirm_title')"
                    :description="$t('content.points_page.delete_confirm')"
                    :ok-text="$t('content.points_page.delete')"
                    :cancel-text="$t('content.points_page.cancel')"
                    :ok-button-props="{ danger: true }"
                    @confirm="handleDelete(record)"
                  >
                    <Button
                      type="link"
                      size="small"
                      danger
                      :loading="deletingId === record.id"
                    >
                      {{ $t('content.points_page.delete') }}
                    </Button>
                  </Popconfirm>
                </div>
              </template>
            </template>

            <template #emptyText>
              <span class="text-sm text-muted-foreground">
                {{ $t('content.points_page.empty') }}
              </span>
            </template>
          </Table>
        </Card>
      </template>
    </div>

    <!-- 详情弹窗 -->
    <Modal
      v-model:open="detailVisible"
      :title="$t('content.points_page.detail_title')"
      :width="640"
      :footer="null"
      :styles="{ body: { padding: '20px 24px' } }"
    >
      <template v-if="detailRecord">
        <div class="mb-4 flex items-center gap-4">
          <img
            v-if="detailRecord.imageUrl"
            :src="normalizeImageUrl(detailRecord.imageUrl)"
            :alt="detailRecord.name || 'product'"
            class="size-20 rounded-full border border-border object-cover"
            @error="hideBrokenImage"
          />
          <div
            v-else
            class="flex size-20 items-center justify-center rounded-full border border-border bg-accent"
          >
            <ImageOff class="size-7 text-muted-foreground" />
          </div>
          <div class="min-w-0">
            <div class="text-base font-semibold">
              {{ displayValue(detailRecord.name) }}
            </div>
            <div class="mt-2 flex flex-wrap items-center gap-2">
              <Tag :color="isOn(detailRecord.status) ? 'green' : 'default'">
                {{
                  isOn(detailRecord.status)
                    ? $t('content.points_page.status.on')
                    : $t('content.points_page.status.off')
                }}
              </Tag>
              <Tag color="gold" class="tabular-nums">
                {{ toNumber(detailRecord.pointsPrice) }}
                {{ $t('content.points_page.fields.pointsPrice') }}
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
          <Descriptions.Item :label="$t('content.points_page.fields.id')">
            {{ detailRecord.id }}
          </Descriptions.Item>
          <Descriptions.Item :label="$t('content.points_page.fields.stock')">
            {{ toNumber(detailRecord.stock) }}
          </Descriptions.Item>
          <Descriptions.Item
            :label="$t('content.points_page.fields.createTime')"
          >
            {{ formatTime(detailRecord.createTime) }}
          </Descriptions.Item>
          <Descriptions.Item
            :label="$t('content.points_page.fields.updateTime')"
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
      :ok-text="$t('content.points_page.save')"
      :cancel-text="$t('content.points_page.cancel')"
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
      <div v-if="editingId !== null" class="mb-4">
        <Descriptions :column="1" size="small" bordered>
          <Descriptions.Item :label="$t('content.points_page.fields.id')">
            {{ editingId }}
          </Descriptions.Item>
        </Descriptions>
      </div>

      <Form ref="formRef" :model="formRecord" layout="vertical" class="w-full">
        <FormItem
          :label="$t('content.points_page.fields.name')"
          name="name"
          :rules="[
            {
              required: true,
              message: $t('content.points_page.name_required'),
            },
          ]"
        >
          <Input
            v-model:value="formRecord.name"
            class="w-full"
            :placeholder="$t('content.points_page.fields.name')"
          />
        </FormItem>

        <div class="grid grid-cols-1 gap-x-6 md:grid-cols-2">
          <FormItem
            :label="$t('content.points_page.fields.pointsPrice')"
            name="pointsPrice"
            :rules="[{ required: true, validator: validatePrice }]"
          >
            <InputNumber
              v-model:value="formRecord.pointsPrice"
              :min="0"
              class="w-full"
              :placeholder="$t('content.points_page.fields.pointsPrice')"
            />
          </FormItem>

          <FormItem
            :label="$t('content.points_page.fields.stock')"
            name="stock"
            :rules="[{ required: true, validator: validateStock }]"
          >
            <InputNumber
              v-model:value="formRecord.stock"
              :min="0"
              :precision="0"
              class="w-full"
              :placeholder="$t('content.points_page.fields.stock')"
            />
          </FormItem>

          <FormItem
            :label="$t('content.points_page.fields.status')"
            name="status"
          >
            <Select
              v-model:value="formRecord.status"
              class="w-full"
              :options="statusOptions"
            />
          </FormItem>

          <FormItem
            :label="$t('content.points_page.fields.imageUrl')"
            name="imageUrl"
            class="md:col-span-2"
          >
            <div class="mb-2 flex flex-wrap items-center gap-2">
              <Input
                v-model:value="formRecord.imageUrl"
                class="min-w-0 flex-1"
                :placeholder="$t('content.points_page.image_placeholder')"
              />
              <Upload
                :max-count="1"
                :show-upload-list="false"
                accept="image/*"
                :before-upload="() => false"
                @change="handleImageChange"
              >
                <Button :loading="imageUploading">上传图片</Button>
              </Upload>
            </div>
            <!-- 图片预览（单图），点击缩略图打开 AntD Preview 大图 -->
            <div
              v-if="formRecord.imageUrl"
              class="group relative inline-flex"
            >
              <Image
                :src="normalizeImageUrl(formRecord.imageUrl)"
                alt="商品图片预览"
                :preview="{ maskClosable: true }"
                class="!size-20 rounded-md border border-border object-cover shadow-sm"
                @error="hideBrokenImage"
              />
              <button
                type="button"
                class="absolute -right-2 -top-2 flex size-5 items-center justify-center rounded-full bg-red-500 text-xs font-bold text-white shadow transition-transform hover:scale-110"
                title="移除图片"
                @click="removeCoverImage"
              >
                ×
              </button>
            </div>
            <span v-else class="text-xs text-muted-foreground">
              暂未设置商品图片，可直接输入 URL 或点击上方按钮上传
            </span>
          </FormItem>
        </div>

        <FormItem
          :label="$t('content.points_page.fields.description')"
          name="description"
        >
          <Input.TextArea
            v-model:value="formRecord.description"
            :rows="3"
            class="w-full"
            :placeholder="$t('content.points_page.fields.description')"
          />
        </FormItem>
      </Form>
    </Modal>
  </Page>
</template>