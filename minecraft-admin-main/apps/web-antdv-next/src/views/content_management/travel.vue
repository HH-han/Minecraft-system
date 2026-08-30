<script lang="ts" setup>
import type { TableColumnsType } from 'antdv-next';

import { computed, onMounted, reactive, ref } from 'vue';

import { Page } from '@vben/common-ui';
import { MapPin, Plus, RefreshCw, Search } from '@vben/icons';
import { $t } from '@vben/locales';

import {
  Button,
  Card,
  Descriptions,
  Form,
  FormItem,
  Input,
  message,
  Modal,
  Popconfirm,
  Result,
  Select,
  Skeleton,
  Table,
  Tag,
} from 'antdv-next';

import {
  createTravelPlan,
  deleteTravelPlan,
  getTravelPlanList,
  updateTravelPlan,
} from '#/api/management/content/travel';

defineOptions({ name: 'TravelPlanManagement' });

interface TravelPlan {
  city?: null | string;
  content?: null | string;
  createTime?: null | string;
  description?: null | string;
  endDate?: null | string;
  id: number;
  startDate?: null | string;
  status?: null | number;
  title?: null | string;
  updateTime?: null | string;
  userId?: null | number;
}

// =========================
// 状态
// =========================
const loading = ref(false);
const errorMsg = ref('');
const planList = ref<TravelPlan[]>([]);
const pageNum = ref(1);
const pageSize = ref(10);
const pages = ref(0);
const total = ref(0);
const deletingId = ref<null | number>(null);
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

/** 前端过滤（标题 / 目的地城市） */
const filteredPlans = computed(() => {
  const kw = keyword.value.trim().toLowerCase();
  if (!kw) return planList.value;
  return planList.value.filter(
    (p) =>
      (p.title ?? '').toLowerCase().includes(kw) ||
      (p.city ?? '').toLowerCase().includes(kw),
  );
});

// =========================
// 列定义
// =========================
const columns = computed<TableColumnsType>(() => [
  { title: 'ID', dataIndex: 'id', width: 70 },
  { title: $t('content.travel_page.fields.title'), key: 'title', width: 200, ellipsis: true },
  { title: $t('content.travel_page.fields.city'), key: 'city', width: 130 },
  { title: $t('content.travel_page.fields.description'), dataIndex: 'description', ellipsis: true },
  { title: $t('content.travel_page.schedule'), key: 'schedule', width: 220 },
  { title: $t('content.travel_page.fields.status'), key: 'status', width: 90 },
  { title: $t('content.travel_page.fields.createTime'), key: 'createTime', width: 170 },
  { title: $t('content.travel_page.fields.action'), key: 'action', width: 170, fixed: 'right' },
]);

// =========================
// 数据加载（服务端分页，兼容数组）
// =========================
async function fetchData() {
  loading.value = true;
  errorMsg.value = '';
  try {
    const res: any = await getTravelPlanList({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
    });
    if (Array.isArray(res)) {
      planList.value = res;
      pages.value = 0;
      total.value = res.length;
    } else {
      planList.value = res?.records ?? [];
      pages.value = toNumber(res?.pages);
      total.value = toNumber(res?.total);
    }
  } catch (error: any) {
    errorMsg.value = error?.message || $t('content.travel_page.error_desc');
  } finally {
    loading.value = false;
  }
}

onMounted(() => {
  fetchData();
});

function handlePageChange(page: number, size: number) {
  pageNum.value = page;
  pageSize.value = size;
  fetchData();
}

function handlePageSizeChange(_page: number, size: number) {
  pageSize.value = size;
  pageNum.value = 1;
  fetchData();
}

// =========================
// 详情弹窗
// =========================
const detailVisible = ref(false);
const detailRecord = ref<null | TravelPlan>(null);

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

const formRecord = reactive({
  city: '',
  content: '',
  description: '',
  endDate: '',
  startDate: '',
  status: 1 as number,
  title: '',
});

const modalTitle = computed(() =>
  editingId.value === null
    ? $t('content.travel_page.create_title')
    : $t('content.travel_page.edit_title'),
);

const statusOptions = computed(() => [
  { label: $t('content.travel_page.status.on'), value: 1 },
  { label: $t('content.travel_page.status.off'), value: 0 },
]);

function openCreate() {
  editingId.value = null;
  formRecord.title = '';
  formRecord.city = '';
  formRecord.startDate = '';
  formRecord.endDate = '';
  formRecord.description = '';
  formRecord.content = '';
  formRecord.status = 1;
  modalVisible.value = true;
}

function openEdit(record: any) {
  editingId.value = record.id;
  formRecord.title = record.title ?? '';
  formRecord.city = record.city ?? '';
  formRecord.startDate = record.startDate ?? '';
  formRecord.endDate = record.endDate ?? '';
  formRecord.description = record.description ?? '';
  formRecord.content = record.content ?? '';
  formRecord.status = Number(record.status ?? 1);
  modalVisible.value = true;
}

function closeModal() {
  modalVisible.value = false;
  editingId.value = null;
  formRef.value?.resetFields?.();
}

const DATE_PATTERN = /^\d{4}-\d{2}-\d{2}$/;

// =========================
// 保存
// =========================
async function handleSave() {
  try {
    await formRef.value?.validate();
  } catch {
    return;
  }
  if (
    formRecord.startDate &&
    formRecord.endDate &&
    DATE_PATTERN.test(formRecord.startDate) &&
    DATE_PATTERN.test(formRecord.endDate) &&
    formRecord.endDate < formRecord.startDate
  ) {
    message.warning($t('content.travel_page.end_before_start'));
    return;
  }
  submitting.value = true;
  try {
    const payload: any = { ...formRecord };
    if (editingId.value !== null) payload.id = editingId.value;
    if (editingId.value === null) {
      await createTravelPlan(payload);
    } else {
      await updateTravelPlan(payload);
    }
    message.success($t('content.travel_page.save_success'));
    closeModal();
    await fetchData();
  } catch (err: any) {
    message.error(err?.message || $t('content.travel_page.save_failed'));
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
    await deleteTravelPlan(record.id);
    message.success($t('content.travel_page.delete_success'));
    if (pages.value > 0 && pageNum.value > 1 && planList.value.length <= 1) {
      pageNum.value -= 1;
    }
    await fetchData();
  } catch (err: any) {
    message.error(err?.message || $t('content.travel_page.delete_failed'));
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
        v-if="loading && planList.length === 0"
        :title="$t('content.travel_page.title')"
        :styles="{ body: { padding: '24px' } }"
      >
        <Skeleton :active="true" :paragraph="{ rows: 8 }" />
      </Card>

      <!-- 加载失败：错误占位 + 重试 -->
      <Card
        v-else-if="errorMsg && planList.length === 0"
        :title="$t('content.travel_page.title')"
        :styles="{ body: { padding: '24px' } }"
      >
        <Result
          status="error"
          :title="$t('content.travel_page.error_title')"
          :sub-title="errorMsg"
        >
          <template #extra>
            <Button type="primary" @click="fetchData">
              {{ $t('content.travel_page.retry') }}
            </Button>
          </template>
        </Result>
      </Card>

      <!-- 列表 -->
      <Card
        v-else
        :title="$t('content.travel_page.title')"
        :styles="{ body: { padding: '0' } }"
      >
        <template #extra>
          <div class="flex items-center gap-2">
            <Input
              v-model:value="keyword"
              :placeholder="$t('content.travel_page.search_placeholder')"
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
              {{ $t('content.travel_page.refresh') }}
            </Button>
            <Button type="primary" @click="openCreate">
              <template #icon>
                <Plus class="mr-1 size-4 align-middle" />
              </template>
              {{ $t('content.travel_page.add') }}
            </Button>
          </div>
        </template>

        <Table
          :columns="columns"
          :data-source="filteredPlans"
          :loading="loading"
          :pagination="
            pages === 0
              ? { pageSize: 10, showSizeChanger: false }
              : {
                  current: pageNum,
                  pageSize: pageSize,
                  showSizeChanger: true,
                  pageSizeOptions: ['10', '20', '50'],
                  total: total,
                  showTotal: (t: number) =>
                    `${t} ${$t('content.travel_page.total_suffix')}`,
                  onChange: handlePageChange,
                  onShowSizeChange: handlePageSizeChange,
                }
          "
          :scroll="{ x: 1280 }"
          size="small"
          row-key="id"
        >
          <template #bodyCell="{ column, record }">
            <!-- 标题 -->
            <template v-if="column.key === 'title'">
              <span class="font-medium">{{ displayValue(record.title) }}</span>
            </template>

            <!-- 目的地 -->
            <template v-else-if="column.key === 'city'">
              <span class="inline-flex items-center gap-1">
                <MapPin class="size-3.5 text-muted-foreground" />
                {{ displayValue(record.city) }}
              </span>
            </template>

            <!-- 行程 -->
            <template v-else-if="column.key === 'schedule'">
              <span class="text-xs tabular-nums">
                {{ displayValue(record.startDate) }}
                <span class="mx-1 text-muted-foreground">→</span>
                {{ displayValue(record.endDate) }}
              </span>
            </template>

            <!-- 状态 -->
            <template v-else-if="column.key === 'status'">
              <Tag :color="isOn(record.status) ? 'green' : 'default'">
                {{
                  isOn(record.status)
                    ? $t('content.travel_page.status.on')
                    : $t('content.travel_page.status.off')
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
                  {{ $t('content.travel_page.detail') }}
                </Button>
                <Button type="link" size="small" @click="openEdit(record)">
                  {{ $t('content.travel_page.edit') }}
                </Button>
                <Popconfirm
                  :title="$t('content.travel_page.delete_confirm_title')"
                  :description="$t('content.travel_page.delete_confirm')"
                  :ok-text="$t('content.travel_page.delete')"
                  :cancel-text="$t('content.travel_page.cancel')"
                  :ok-button-props="{ danger: true }"
                  @confirm="handleDelete(record)"
                >
                  <Button
                    type="link"
                    size="small"
                    danger
                    :loading="deletingId === record.id"
                  >
                    {{ $t('content.travel_page.delete') }}
                  </Button>
                </Popconfirm>
              </div>
            </template>
          </template>

          <template #emptyText>
            <span class="text-sm text-muted-foreground">
              {{ $t('content.travel_page.list_empty') }}
            </span>
          </template>
        </Table>
      </Card>
    </div>

    <!-- 详情弹窗 -->
    <Modal
      v-model:open="detailVisible"
      :title="$t('content.travel_page.detail_title')"
      :width="640"
      :footer="null"
      :styles="{ body: { padding: '20px 24px' } }"
    >
      <template v-if="detailRecord">
        <div class="mb-4">
          <div class="text-base font-semibold">
            {{ displayValue(detailRecord.title) }}
          </div>
          <div class="mt-2 flex flex-wrap items-center gap-2">
            <Tag :color="isOn(detailRecord.status) ? 'green' : 'default'">
              {{
                isOn(detailRecord.status)
                  ? $t('content.travel_page.status.on')
                  : $t('content.travel_page.status.off')
              }}
            </Tag>
            <Tag v-if="detailRecord.city" color="blue">
              {{ detailRecord.city }}
            </Tag>
          </div>
        </div>

        <div
          v-if="detailRecord.description"
          class="mb-4 whitespace-pre-wrap rounded-md border border-border bg-accent/40 p-3 text-sm leading-6"
        >
          {{ detailRecord.description }}
        </div>

        <div
          v-if="detailRecord.content"
          class="mb-4 whitespace-pre-wrap rounded-md border border-border bg-accent/40 p-3 text-sm leading-6"
        >
          {{ detailRecord.content }}
        </div>

        <Descriptions :column="2" size="small" bordered>
          <Descriptions.Item :label="$t('content.travel_page.fields.id')">
            {{ detailRecord.id }}
          </Descriptions.Item>
          <Descriptions.Item :label="$t('content.travel_page.fields.userId')">
            {{ displayValue(detailRecord.userId) }}
          </Descriptions.Item>
          <Descriptions.Item :label="$t('content.travel_page.fields.startDate')">
            {{ displayValue(detailRecord.startDate) }}
          </Descriptions.Item>
          <Descriptions.Item :label="$t('content.travel_page.fields.endDate')">
            {{ displayValue(detailRecord.endDate) }}
          </Descriptions.Item>
          <Descriptions.Item :label="$t('content.travel_page.fields.createTime')">
            {{ formatTime(detailRecord.createTime) }}
          </Descriptions.Item>
          <Descriptions.Item :label="$t('content.travel_page.fields.updateTime')">
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
      :ok-text="$t('content.travel_page.save')"
      :cancel-text="$t('content.travel_page.cancel')"
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
        <Descriptions :column="2" size="small" bordered>
          <Descriptions.Item :label="$t('content.travel_page.fields.id')">
            {{ editingId }}
          </Descriptions.Item>
        </Descriptions>
      </div>

      <Form ref="formRef" :model="formRecord" layout="vertical" class="w-full">
        <FormItem
          :label="$t('content.travel_page.fields.title')"
          name="title"
          :rules="[
            {
              required: true,
              message: $t('content.travel_page.title_required'),
            },
          ]"
        >
          <Input
            v-model:value="formRecord.title"
            class="w-full"
            :placeholder="$t('content.travel_page.fields.title')"
          />
        </FormItem>

        <div class="grid grid-cols-1 gap-x-6 md:grid-cols-2">
          <FormItem :label="$t('content.travel_page.fields.city')" name="city">
            <Input
              v-model:value="formRecord.city"
              class="w-full"
              :placeholder="$t('content.travel_page.fields.city')"
            />
          </FormItem>

          <FormItem :label="$t('content.travel_page.fields.status')" name="status">
            <Select
              v-model:value="formRecord.status"
              class="w-full"
              :options="statusOptions"
            />
          </FormItem>

          <FormItem :label="$t('content.travel_page.fields.startDate')" name="startDate">
            <Input
              v-model:value="formRecord.startDate"
              class="w-full"
              :placeholder="$t('content.travel_page.date_hint')"
            />
          </FormItem>

          <FormItem :label="$t('content.travel_page.fields.endDate')" name="endDate">
            <Input
              v-model:value="formRecord.endDate"
              class="w-full"
              :placeholder="$t('content.travel_page.date_hint')"
            />
          </FormItem>
        </div>

        <FormItem :label="$t('content.travel_page.fields.description')" name="description">
          <Input.TextArea
            v-model:value="formRecord.description"
            :rows="3"
            class="w-full"
            :placeholder="$t('content.travel_page.fields.description')"
          />
        </FormItem>

        <FormItem :label="$t('content.travel_page.fields.content')" name="content">
          <Input.TextArea
            v-model:value="formRecord.content"
            :rows="5"
            class="w-full"
            :placeholder="$t('content.travel_page.fields.content')"
          />
        </FormItem>
      </Form>
    </Modal>
  </Page>
</template>