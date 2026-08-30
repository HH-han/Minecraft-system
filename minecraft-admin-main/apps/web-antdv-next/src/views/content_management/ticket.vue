<script lang="ts" setup>
import type { TableColumnsType } from 'antdv-next';

import { computed, onMounted, reactive, ref } from 'vue';

import { Page } from '@vben/common-ui';
import { ArrowRight, Plus, RefreshCw, Search } from '@vben/icons';
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
} from 'antdv-next';

import {
  addTicket,
  deleteTicket,
  getTicketList,
  updateTicket,
} from '#/api/management/content/ticket';

defineOptions({ name: 'TicketManagement' });

interface TicketInfo {
  arrivalCity?: null | string;
  arrivalTime?: null | string;
  carrier?: null | string;
  createTime?: null | string;
  departureCity?: null | string;
  departureTime?: null | string;
  id: number;
  price?: null | number;
  seatClass?: null | string;
  status?: null | number;
  stock?: null | number;
  type?: null | string;
  updateTime?: null | string;
}

const TICKET_TYPES = ['train', 'flight', 'bus', 'car'] as const;

// =========================
// 状态
// =========================
const loading = ref(false);
const errorMsg = ref('');
const ticketList = ref<TicketInfo[]>([]);
const pageNum = ref(1);
const pageSize = ref(10);
const pages = ref(0);
const total = ref(0);
const deletingId = ref<null | number>(null);
const keyword = ref('');
const typeFilter = ref<null | string>(null);

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

/** 类型 → 显示文本 */
function typeText(type?: null | string): string {
  const t = String(type ?? '').toLowerCase();
  const key = `content.ticket_page.type.${t}`;
  const text = $t(key);
  // 未登记类型原样显示
  return text === key ? String(type ?? '-') : text;
}

/** 类型 → Tag 颜色 */
function typeColor(type?: null | string): string {
  const t = String(type ?? '').toLowerCase();
  if (t === 'train') return 'green';
  if (t === 'flight') return 'blue';
  if (t === 'bus') return 'orange';
  if (t === 'car') return 'cyan';
  return 'default';
}

/** 前端过滤（城市 / 承运方 / 坐席）+ 类型下拉 */
const filteredTickets = computed(() => {
  const kw = keyword.value.trim().toLowerCase();
  const tf = (typeFilter.value ?? '').toLowerCase();
  return ticketList.value.filter((t) => {
    if (tf && String(t.type ?? '').toLowerCase() !== tf) return false;
    if (!kw) return true;
    return (
      (t.departureCity ?? '').toLowerCase().includes(kw) ||
      (t.arrivalCity ?? '').toLowerCase().includes(kw) ||
      (t.carrier ?? '').toLowerCase().includes(kw) ||
      (t.seatClass ?? '').toLowerCase().includes(kw)
    );
  });
});

// =========================
// 列定义
// =========================
const columns = computed<TableColumnsType>(() => [
  { title: 'ID', dataIndex: 'id', width: 70 },
  { title: $t('content.ticket_page.fields.type'), key: 'type', width: 110 },
  { title: $t('content.ticket_page.route'), key: 'route', width: 200 },
  { title: $t('content.ticket_page.schedule'), key: 'schedule', width: 140 },
  { title: $t('content.ticket_page.fields.carrier'), key: 'carrier', width: 130, ellipsis: true },
  { title: $t('content.ticket_page.fields.seatClass'), key: 'seatClass', width: 100 },
  { title: $t('content.ticket_page.fields.price'), key: 'price', width: 110 },
  { title: $t('content.ticket_page.fields.stock'), key: 'stock', width: 80 },
  { title: $t('content.ticket_page.fields.status'), key: 'status', width: 80 },
  { title: $t('content.ticket_page.fields.updateTime'), key: 'updateTime', width: 170 },
  { title: $t('content.ticket_page.fields.action'), key: 'action', width: 170, fixed: 'right' },
]);

// =========================
// 数据加载（服务端分页，兼容数组）
// =========================
async function fetchData() {
  loading.value = true;
  errorMsg.value = '';
  try {
    const res: any = await getTicketList({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
    });
    if (Array.isArray(res)) {
      ticketList.value = res;
      pages.value = 0;
      total.value = res.length;
    } else {
      ticketList.value = res?.records ?? [];
      pages.value = toNumber(res?.pages);
      total.value = toNumber(res?.total);
    }
  } catch (error: any) {
    errorMsg.value = error?.message || $t('content.ticket_page.error_desc');
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
const detailRecord = ref<null | TicketInfo>(null);

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
  arrivalCity: '',
  arrivalTime: '',
  carrier: '',
  departureCity: '',
  departureTime: '',
  price: 0 as null | number,
  seatClass: '',
  status: 1 as number,
  stock: 0 as null | number,
  type: null as null | string,
});

const modalTitle = computed(() =>
  editingId.value === null
    ? $t('content.ticket_page.create_title')
    : $t('content.ticket_page.edit_title'),
);

const statusOptions = computed(() => [
  { label: $t('content.ticket_page.status.on'), value: 1 },
  { label: $t('content.ticket_page.status.off'), value: 0 },
]);

const typeOptions = computed(() =>
  TICKET_TYPES.map((t) => ({
    label: $t(`content.ticket_page.type.${t}`),
    value: t,
  })),
);

function openCreate() {
  editingId.value = null;
  formRecord.type = null;
  formRecord.departureCity = '';
  formRecord.arrivalCity = '';
  formRecord.departureTime = '';
  formRecord.arrivalTime = '';
  formRecord.carrier = '';
  formRecord.seatClass = '';
  formRecord.price = 0;
  formRecord.stock = 0;
  formRecord.status = 1;
  modalVisible.value = true;
}

function openEdit(record: any) {
  editingId.value = record.id;
  formRecord.type = record.type ? String(record.type) : null;
  formRecord.departureCity = record.departureCity ?? '';
  formRecord.arrivalCity = record.arrivalCity ?? '';
  formRecord.departureTime = record.departureTime ?? '';
  formRecord.arrivalTime = record.arrivalTime ?? '';
  formRecord.carrier = record.carrier ?? '';
  formRecord.seatClass = record.seatClass ?? '';
  formRecord.price = toNumber(record.price);
  formRecord.stock = toNumber(record.stock);
  formRecord.status = Number(record.status ?? 1);
  modalVisible.value = true;
}

function closeModal() {
  modalVisible.value = false;
  editingId.value = null;
  formRef.value?.resetFields?.();
}

const TIME_PATTERN = /^\d{2}:\d{2}$/;

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
      await addTicket(payload);
    } else {
      await updateTicket(payload);
    }
    message.success($t('content.ticket_page.save_success'));
    closeModal();
    await fetchData();
  } catch (err: any) {
    message.error(err?.message || $t('content.ticket_page.save_failed'));
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
    await deleteTicket(record.id);
    message.success($t('content.ticket_page.delete_success'));
    if (pages.value > 0 && pageNum.value > 1 && ticketList.value.length <= 1) {
      pageNum.value -= 1;
    }
    await fetchData();
  } catch (err: any) {
    message.error(err?.message || $t('content.ticket_page.delete_failed'));
  } finally {
    deletingId.value = null;
  }
}

const TIME_HINT = computed(() => $t('content.ticket_page.time_hint'));
</script>

<template>
  <Page :auto-content-height="true">
    <div class="mx-auto w-full">
      <!-- 加载中：骨架屏（仅首次加载） -->
      <Card
        v-if="loading && ticketList.length === 0"
        :title="$t('content.ticket_page.title')"
        :styles="{ body: { padding: '24px' } }"
      >
        <Skeleton :active="true" :paragraph="{ rows: 8 }" />
      </Card>

      <!-- 加载失败：错误占位 + 重试 -->
      <Card
        v-else-if="errorMsg && ticketList.length === 0"
        :title="$t('content.ticket_page.title')"
        :styles="{ body: { padding: '24px' } }"
      >
        <Result
          status="error"
          :title="$t('content.ticket_page.error_title')"
          :sub-title="errorMsg"
        >
          <template #extra>
            <Button type="primary" @click="fetchData">
              {{ $t('content.ticket_page.retry') }}
            </Button>
          </template>
        </Result>
      </Card>

      <!-- 列表 -->
      <Card
        v-else
        :title="$t('content.ticket_page.title')"
        :styles="{ body: { padding: '0' } }"
      >
        <template #extra>
          <div class="flex items-center gap-2">
            <Select
              v-model:value="typeFilter"
              :placeholder="$t('content.ticket_page.fields.type')"
              allow-clear
              class="w-32"
              :options="typeOptions"
            />
            <Input
              v-model:value="keyword"
              :placeholder="$t('content.ticket_page.search_placeholder')"
              allow-clear
              class="w-52"
            >
              <template #prefix>
                <Search class="size-4 text-muted-foreground" />
              </template>
            </Input>
            <Button :loading="loading" @click="fetchData">
              <template #icon>
                <RefreshCw class="mr-1 size-4 align-middle" />
              </template>
              {{ $t('content.ticket_page.refresh') }}
            </Button>
            <Button type="primary" @click="openCreate">
              <template #icon>
                <Plus class="mr-1 size-4 align-middle" />
              </template>
              {{ $t('content.ticket_page.add') }}
            </Button>
          </div>
        </template>

        <Table
          :columns="columns"
          :data-source="filteredTickets"
          :loading="loading"
          :pagination="
            pages === 0
              ? { pageSize: 10, showSizeChanger: false }
              : {
                  current: pageNum,
                  pageSize,
                  showSizeChanger: true,
                  pageSizeOptions: ['10', '20', '50'],
                  total,
                  showTotal: (t: number) =>
                    `${t} ${$t('content.ticket_page.total_suffix')}`,
                  onChange: handlePageChange,
                  onShowSizeChange: handlePageSizeChange,
                }
          "
          :scroll="{ x: 1500 }"
          size="small"
          row-key="id"
        >
          <template #bodyCell="{ column, record }">
            <!-- 出行类型 -->
            <template v-if="column.key === 'type'">
              <Tag :color="typeColor(record.type)">
                {{ typeText(record.type) }}
              </Tag>
            </template>

            <!-- 行程（出发 → 到达） -->
            <template v-else-if="column.key === 'route'">
              <span class="text-xs">
                {{ displayValue(record.departureCity) }}
                <span class="mx-1 text-muted-foreground">→</span>
                {{ displayValue(record.arrivalCity) }}
              </span>
            </template>

            <!-- 时间 -->
            <template v-else-if="column.key === 'schedule'">
              <span class="text-xs tabular-nums">
                {{ displayValue(record.departureTime) }}
                <span class="mx-1 text-muted-foreground">→</span>
                {{ displayValue(record.arrivalTime) }}
              </span>
            </template>

            <!-- 坐席 -->
            <template v-else-if="column.key === 'seatClass'">
              <Tag color="blue">{{ displayValue(record.seatClass) }}</Tag>
            </template>

            <!-- 票价 -->
            <template v-else-if="column.key === 'price'">
              <span class="font-medium tabular-nums text-red-500">
                ¥{{ toNumber(record.price) }}
              </span>
            </template>

            <!-- 余票 -->
            <template v-else-if="column.key === 'stock'">
              <span
                class="tabular-nums"
                :class="toNumber(record.stock) <= 0 ? 'font-medium text-red-500' : ''"
              >
                {{ toNumber(record.stock) }}
              </span>
            </template>

            <!-- 状态 -->
            <template v-else-if="column.key === 'status'">
              <Tag :color="isOn(record.status) ? 'green' : 'default'">
                {{
                  isOn(record.status)
                    ? $t('content.ticket_page.status.on')
                    : $t('content.ticket_page.status.off')
                }}
              </Tag>
            </template>

            <!-- 更新时间 -->
            <template v-else-if="column.key === 'updateTime'">
              <span class="text-xs text-muted-foreground">
                {{ formatTime(record.updateTime) }}
              </span>
            </template>

            <!-- 操作 -->
            <template v-else-if="column.key === 'action'">
              <div class="flex flex-nowrap items-center whitespace-nowrap">
                <Button type="link" size="small" @click="openDetail(record)">
                  {{ $t('content.ticket_page.detail') }}
                </Button>
                <Button type="link" size="small" @click="openEdit(record)">
                  {{ $t('content.ticket_page.edit') }}
                </Button>
                <Popconfirm
                  :title="$t('content.ticket_page.delete_confirm_title')"
                  :description="$t('content.ticket_page.delete_confirm')"
                  :ok-text="$t('content.ticket_page.delete')"
                  :cancel-text="$t('content.ticket_page.cancel')"
                  :ok-button-props="{ danger: true }"
                  @confirm="handleDelete(record)"
                >
                  <Button
                    type="link"
                    size="small"
                    danger
                    :loading="deletingId === record.id"
                  >
                    {{ $t('content.ticket_page.delete') }}
                  </Button>
                </Popconfirm>
              </div>
            </template>
          </template>

          <template #emptyText>
            <span class="text-sm text-muted-foreground">
              {{ $t('content.ticket_page.list_empty') }}
            </span>
          </template>
        </Table>
      </Card>
    </div>

    <!-- 详情弹窗 -->
    <Modal
      v-model:open="detailVisible"
      :title="$t('content.ticket_page.detail_title')"
      :width="640"
      :footer="null"
      :styles="{ body: { padding: '20px 24px' } }"
    >
      <template v-if="detailRecord">
        <!-- 行程大字展示 -->
        <div
          class="mb-4 rounded-md border border-border bg-accent/40 p-4 text-center"
        >
          <div class="flex items-center justify-center gap-4">
            <div>
              <div class="text-xl font-semibold">
                {{ displayValue(detailRecord.departureCity) }}
              </div>
              <div class="mt-1 text-xs tabular-nums text-muted-foreground">
                {{ displayValue(detailRecord.departureTime) }}
              </div>
            </div>
            <ArrowRight class="size-5 text-muted-foreground" />
            <div>
              <div class="text-xl font-semibold">
                {{ displayValue(detailRecord.arrivalCity) }}
              </div>
              <div class="mt-1 text-xs tabular-nums text-muted-foreground">
                {{ displayValue(detailRecord.arrivalTime) }}
              </div>
            </div>
          </div>
        </div>

        <div class="mb-4 flex flex-wrap items-center gap-2">
          <Tag :color="typeColor(detailRecord.type)">
            {{ typeText(detailRecord.type) }}
          </Tag>
          <Tag :color="isOn(detailRecord.status) ? 'green' : 'default'">
            {{
              isOn(detailRecord.status)
                ? $t('content.ticket_page.status.on')
                : $t('content.ticket_page.status.off')
            }}
          </Tag>
          <Tag v-if="detailRecord.seatClass" color="blue">
            {{ detailRecord.seatClass }}
          </Tag>
          <span class="font-medium tabular-nums text-red-500">
            ¥{{ toNumber(detailRecord.price) }}
          </span>
        </div>

        <Descriptions :column="2" size="small" bordered>
          <Descriptions.Item :label="$t('content.ticket_page.fields.id')">
            {{ detailRecord.id }}
          </Descriptions.Item>
          <Descriptions.Item :label="$t('content.ticket_page.fields.carrier')">
            {{ displayValue(detailRecord.carrier) }}
          </Descriptions.Item>
          <Descriptions.Item :label="$t('content.ticket_page.fields.stock')">
            {{ toNumber(detailRecord.stock) }}
          </Descriptions.Item>
          <Descriptions.Item :label="$t('content.ticket_page.fields.updateTime')">
            {{ formatTime(detailRecord.updateTime) }}
          </Descriptions.Item>
          <Descriptions.Item
            :label="$t('content.ticket_page.fields.createTime')"
            :span="2"
          >
            {{ formatTime(detailRecord.createTime) }}
          </Descriptions.Item>
        </Descriptions>
      </template>
    </Modal>

    <!-- 新增 / 编辑弹窗 -->
    <Modal
      v-model:open="modalVisible"
      :title="modalTitle"
      :width="640"
      :mask-closable="false"
      :confirm-loading="submitting"
      :ok-text="$t('content.ticket_page.save')"
      :cancel-text="$t('content.ticket_page.cancel')"
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
          <Descriptions.Item :label="$t('content.ticket_page.fields.id')">
            {{ editingId }}
          </Descriptions.Item>
        </Descriptions>
      </div>

      <Form ref="formRef" :model="formRecord" layout="vertical" class="w-full">
        <div class="grid grid-cols-1 gap-x-6 md:grid-cols-2">
          <FormItem
            :label="$t('content.ticket_page.fields.type')"
            name="type"
            :rules="[
              {
                required: true,
                message: $t('content.ticket_page.type_required'),
              },
            ]"
          >
            <Select
              v-model:value="formRecord.type"
              class="w-full"
              :options="typeOptions"
              :placeholder="$t('content.ticket_page.fields.type')"
            />
          </FormItem>

          <FormItem :label="$t('content.ticket_page.fields.status')" name="status">
            <Select
              v-model:value="formRecord.status"
              class="w-full"
              :options="statusOptions"
            />
          </FormItem>

          <FormItem
            :label="$t('content.ticket_page.fields.departureCity')"
            name="departureCity"
            :rules="[
              {
                required: true,
                message: $t('content.ticket_page.departure_required'),
              },
            ]"
          >
            <Input
              v-model:value="formRecord.departureCity"
              class="w-full"
              :placeholder="$t('content.ticket_page.fields.departureCity')"
            />
          </FormItem>

          <FormItem
            :label="$t('content.ticket_page.fields.arrivalCity')"
            name="arrivalCity"
            :rules="[
              {
                required: true,
                message: $t('content.ticket_page.arrival_required'),
              },
            ]"
          >
            <Input
              v-model:value="formRecord.arrivalCity"
              class="w-full"
              :placeholder="$t('content.ticket_page.fields.arrivalCity')"
            />
          </FormItem>

          <FormItem
            :label="$t('content.ticket_page.fields.departureTime')"
            name="departureTime"
          >
            <Input
              v-model:value="formRecord.departureTime"
              class="w-full"
              :placeholder="TIME_HINT"
            />
          </FormItem>

          <FormItem
            :label="$t('content.ticket_page.fields.arrivalTime')"
            name="arrivalTime"
          >
            <Input
              v-model:value="formRecord.arrivalTime"
              class="w-full"
              :placeholder="TIME_HINT"
            />
          </FormItem>

          <FormItem :label="$t('content.ticket_page.fields.carrier')" name="carrier">
            <Input
              v-model:value="formRecord.carrier"
              class="w-full"
              :placeholder="$t('content.ticket_page.fields.carrier')"
            />
          </FormItem>

          <FormItem :label="$t('content.ticket_page.fields.seatClass')" name="seatClass">
            <Input
              v-model:value="formRecord.seatClass"
              class="w-full"
              :placeholder="$t('content.ticket_page.fields.seatClass')"
            />
          </FormItem>

          <FormItem :label="$t('content.ticket_page.fields.price')" name="price">
            <InputNumber
              v-model:value="formRecord.price"
              class="w-full"
              :min="0"
              :precision="2"
              :placeholder="$t('content.ticket_page.fields.price')"
            />
          </FormItem>

          <FormItem :label="$t('content.ticket_page.fields.stock')" name="stock">
            <InputNumber
              v-model:value="formRecord.stock"
              class="w-full"
              :min="0"
              :precision="0"
              :placeholder="$t('content.ticket_page.fields.stock')"
            />
          </FormItem>
        </div>
      </Form>
    </Modal>
  </Page>
</template>