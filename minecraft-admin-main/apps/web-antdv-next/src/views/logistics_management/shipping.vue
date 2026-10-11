<script lang="ts" setup>
import type { TableColumnsType } from 'antdv-next';

import { computed, onMounted, reactive, ref } from 'vue';

import { Page } from '@vben/common-ui';
import { Plus, RefreshCw, Search } from '@vben/icons';
import { $t } from '@vben/locales';

import {
  Button,
  Card,
  Descriptions,
  Form,
  FormItem,
  Input,
  InputNumber,
  Modal,
  Select,
  Table,
  Tag,
  message,
} from 'antdv-next';

import { getCarrierList } from '#/api/management/logistics/carrier';
import {
  batchShipping,
  createShipping,
  getShippingPage,
  getShippingTrack,
} from '#/api/management/logistics/shipping';

defineOptions({ name: 'LogisticsShipping' });

const loading = ref(false);
const list = ref<any[]>([]);
const total = ref(0);
const pageNum = ref(1);
const pageSize = ref(10);
const keyword = ref('');
const carriers = ref<any[]>([]);

const columns = computed<TableColumnsType>(() => [
  { title: $t('logistics.shipping_page.fields.shippingNo'), dataIndex: 'shippingNo', width: 170 },
  { title: $t('logistics.shipping_page.fields.orderNo'), dataIndex: 'orderNo', width: 140 },
  { title: $t('logistics.shipping_page.fields.itemName'), dataIndex: 'itemName', ellipsis: true },
  { title: $t('logistics.shipping_page.fields.quantity'), dataIndex: 'quantity', width: 70 },
  { title: $t('logistics.shipping_page.fields.carrier'), key: 'carrier', width: 110 },
  { title: $t('logistics.shipping_page.fields.trackingNo'), dataIndex: 'trackingNo', width: 150 },
  { title: $t('logistics.shipping_page.fields.receiver'), dataIndex: 'receiverName', width: 100 },
  { title: $t('logistics.shipping_page.fields.status'), key: 'status', width: 100 },
  { title: $t('logistics.shipping_page.fields.shipTime'), key: 'shipTime', width: 170 },
  { title: $t('logistics.common.action'), key: 'action', width: 90, fixed: 'right' },
]);

const filteredList = computed(() => {
  const kw = keyword.value.trim().toLowerCase();
  if (!kw) return list.value;
  return list.value.filter((r) =>
    [r.shippingNo, r.orderNo, r.trackingNo, r.receiverName, r.itemName]
      .filter(Boolean)
      .some((v: string) => String(v).toLowerCase().includes(kw)),
  );
});

const carrierOptions = computed(() =>
  carriers.value.map((c) => ({ label: c.name, value: c.id })),
);

function formatTime(v?: string) {
  return v ? String(v).replace('T', ' ') : '-';
}

function statusColor(status?: number) {
  if (status === 4) return 'success';
  if (status === 5 || status === 6 || status === 7) return 'error';
  if (status === 0) return 'default';
  return 'processing';
}

async function fetchList() {
  loading.value = true;
  try {
    const res: any = await getShippingPage({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
    });
    list.value = res?.records ?? [];
    total.value = res?.total ?? 0;
  } catch {
    // 统一错误提示
  } finally {
    loading.value = false;
  }
}

async function fetchCarriers() {
  try {
    const res: any = await getCarrierList();
    carriers.value = Array.isArray(res) ? res : (res?.records ?? []);
  } catch {
    carriers.value = [];
  }
}

function handleTableChange(pagination: any) {
  pageNum.value = pagination.current;
  fetchList();
}

onMounted(() => {
  fetchList();
  fetchCarriers();
});

// ===== 创建发货单 =====
const createVisible = ref(false);
const submitting = ref(false);
const formRef = ref<any>(null);
const form = reactive<any>({
  orderId: undefined,
  carrierId: undefined,
  carrierCode: '',
  trackingNo: '',
  receiverName: '',
  receiverPhone: '',
  receiverAddress: '',
  quantity: undefined,
  freight: undefined,
  remark: '',
});

function openCreate() {
  Object.assign(form, {
    orderId: undefined,
    carrierId: undefined,
    carrierCode: '',
    trackingNo: '',
    receiverName: '',
    receiverPhone: '',
    receiverAddress: '',
    quantity: undefined,
    freight: undefined,
    remark: '',
  });
  createVisible.value = true;
}

function onCarrierChange(val: number) {
  const carrier = carriers.value.find((c) => c.id === val);
  form.carrierCode = carrier?.code ?? '';
}

async function submitCreate() {
  try {
    await formRef.value?.validate();
  } catch {
    return;
  }
  submitting.value = true;
  try {
    await createShipping({ ...form });
    message.success($t('logistics.common.save_success'));
    createVisible.value = false;
    await fetchList();
  } catch {
    // 统一错误提示
  } finally {
    submitting.value = false;
  }
}

// ===== 批量发货 =====
const batchVisible = ref(false);
const batchSubmitting = ref(false);
const batchForm = reactive<any>({ orderIds: '', carrierId: undefined, carrierCode: '' });

function openBatch() {
  batchForm.orderIds = '';
  batchForm.carrierId = undefined;
  batchForm.carrierCode = '';
  batchVisible.value = true;
}

function onBatchCarrierChange(val: number) {
  const carrier = carriers.value.find((c) => c.id === val);
  batchForm.carrierCode = carrier?.code ?? '';
}

async function submitBatch() {
  const ids = String(batchForm.orderIds || '')
    .split(',')
    .map((s) => Number(s.trim()))
    .filter((n) => Number.isFinite(n) && n > 0);
  if (ids.length === 0) {
    message.warning($t('logistics.shipping_page.batch_order_ids_required'));
    return;
  }
  batchSubmitting.value = true;
  try {
    await batchShipping({
      orderIds: ids,
      carrierId: batchForm.carrierId,
      carrierCode: batchForm.carrierCode,
    });
    message.success($t('logistics.common.save_success'));
    batchVisible.value = false;
    await fetchList();
  } catch {
    // 统一错误提示
  } finally {
    batchSubmitting.value = false;
  }
}

// ===== 轨迹 =====
const trackVisible = ref(false);
const trackLoading = ref(false);
const trackList = ref<any[]>([]);

async function openTrack(record: any) {
  trackVisible.value = true;
  trackLoading.value = true;
  trackList.value = [];
  try {
    const res: any = await getShippingTrack(record.id);
    trackList.value = Array.isArray(res) ? res : [];
  } finally {
    trackLoading.value = false;
  }
}
</script>

<template>
  <Page :auto-content-height="true">
    <Card :title="$t('logistics.shipping_page.title')" :styles="{ body: { padding: '0' } }">
      <template #extra>
        <div class="flex items-center gap-2">
          <Input
            v-model:value="keyword"
            :placeholder="$t('logistics.common.search_placeholder')"
            allow-clear
            class="w-56"
          >
            <template #prefix>
              <Search class="size-4 text-muted-foreground" />
            </template>
          </Input>
          <Button :loading="loading" @click="fetchList">
            <template #icon><RefreshCw class="mr-1 size-4 align-middle" /></template>
            {{ $t('logistics.common.refresh') }}
          </Button>
          <Button @click="openBatch">{{ $t('logistics.shipping_page.batch_title') }}</Button>
          <Button type="primary" @click="openCreate">
            <template #icon><Plus class="mr-1 size-4 align-middle" /></template>
            {{ $t('logistics.shipping_page.create_title') }}
          </Button>
        </div>
      </template>

      <Table
        :columns="columns"
        :data-source="filteredList"
        :loading="loading"
        :pagination="{
          current: pageNum,
          pageSize,
          total,
          showSizeChanger: false,
        }"
        :scroll="{ x: 1280 }"
        size="small"
        row-key="id"
        @change="handleTableChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'carrier'">
            {{ record.carrierName || record.carrierCode || '-' }}
          </template>
          <template v-else-if="column.key === 'status'">
            <Tag :color="statusColor(record.status)">{{ record.statusDesc || '-' }}</Tag>
          </template>
          <template v-else-if="column.key === 'shipTime'">
            <span class="text-xs text-muted-foreground">{{ formatTime(record.shipTime) }}</span>
          </template>
          <template v-else-if="column.key === 'action'">
            <Button type="link" size="small" @click="openTrack(record)">
              {{ $t('logistics.shipping_page.track_title') }}
            </Button>
          </template>
        </template>
        <template #emptyText>
          <span class="text-sm text-muted-foreground">{{ $t('logistics.common.empty') }}</span>
        </template>
      </Table>
    </Card>

    <!-- 创建发货单 -->
    <Modal
      v-model:open="createVisible"
      :title="$t('logistics.shipping_page.create_title')"
      :width="620"
      :mask-closable="false"
      :confirm-loading="submitting"
      :ok-text="$t('logistics.common.save')"
      :cancel-text="$t('logistics.common.cancel')"
      @ok="submitCreate"
    >
      <Form ref="formRef" :model="form" layout="vertical">
        <div class="grid grid-cols-1 gap-x-6 md:grid-cols-2">
          <FormItem
            :label="$t('logistics.shipping_page.fields.orderId')"
            name="orderId"
            :rules="[{ required: true, message: $t('logistics.shipping_page.order_id_required') }]"
          >
            <InputNumber v-model:value="form.orderId" :min="1" class="w-full" />
          </FormItem>
          <FormItem :label="$t('logistics.shipping_page.fields.carrier')" name="carrierId">
            <Select
              v-model:value="form.carrierId"
              class="w-full"
              :options="carrierOptions"
              allow-clear
              @change="onCarrierChange"
            />
          </FormItem>
          <FormItem :label="$t('logistics.shipping_page.fields.trackingNo')" name="trackingNo">
            <Input v-model:value="form.trackingNo" class="w-full" />
          </FormItem>
          <FormItem :label="$t('logistics.shipping_page.fields.quantity')" name="quantity">
            <InputNumber v-model:value="form.quantity" :min="1" class="w-full" />
          </FormItem>
          <FormItem
            :label="$t('logistics.shipping_page.fields.receiverName')"
            name="receiverName"
            :rules="[{ required: true, message: $t('logistics.shipping_page.receiver_required') }]"
          >
            <Input v-model:value="form.receiverName" class="w-full" />
          </FormItem>
          <FormItem :label="$t('logistics.shipping_page.fields.receiverPhone')" name="receiverPhone">
            <Input v-model:value="form.receiverPhone" class="w-full" />
          </FormItem>
          <FormItem
            :label="$t('logistics.shipping_page.fields.receiverAddress')"
            name="receiverAddress"
            class="md:col-span-2"
          >
            <Input v-model:value="form.receiverAddress" class="w-full" />
          </FormItem>
          <FormItem :label="$t('logistics.shipping_page.fields.freight')" name="freight">
            <InputNumber v-model:value="form.freight" :min="0" :precision="2" class="w-full" />
          </FormItem>
          <FormItem :label="$t('logistics.shipping_page.fields.remark')" name="remark">
            <Input v-model:value="form.remark" class="w-full" />
          </FormItem>
        </div>
      </Form>
    </Modal>

    <!-- 批量发货 -->
    <Modal
      v-model:open="batchVisible"
      :title="$t('logistics.shipping_page.batch_title')"
      :width="520"
      :mask-closable="false"
      :confirm-loading="batchSubmitting"
      :ok-text="$t('logistics.common.save')"
      :cancel-text="$t('logistics.common.cancel')"
      @ok="submitBatch"
    >
      <Form layout="vertical">
        <FormItem :label="$t('logistics.shipping_page.batch_order_ids')" required>
          <Input.TextArea
            v-model:value="batchForm.orderIds"
            :rows="3"
            :placeholder="$t('logistics.shipping_page.batch_order_ids')"
          />
        </FormItem>
        <FormItem :label="$t('logistics.shipping_page.fields.carrier')">
          <Select
            v-model:value="batchForm.carrierId"
            class="w-full"
            :options="carrierOptions"
            allow-clear
            @change="onBatchCarrierChange"
          />
        </FormItem>
      </Form>
    </Modal>

    <!-- 轨迹 -->
    <Modal
      v-model:open="trackVisible"
      :title="$t('logistics.shipping_page.track_title')"
      :width="560"
      :footer="null"
    >
      <div v-if="trackLoading" class="py-8 text-center text-muted-foreground">
        {{ $t('logistics.common.refresh') }}
      </div>
      <div v-else-if="trackList.length === 0" class="py-8 text-center text-muted-foreground">
        {{ $t('logistics.shipping_page.empty_track') }}
      </div>
      <div v-else class="max-h-96 overflow-y-auto">
        <div
          v-for="(t, i) in trackList"
          :key="i"
          class="border-l-2 py-2 pl-4"
          :class="i === 0 ? 'border-primary' : 'border-border'"
        >
          <div class="text-sm">{{ t.context }}</div>
          <div class="mt-1 text-xs text-muted-foreground">
            {{ formatTime(t.trackTime) }}
            <span v-if="t.location"> · {{ t.location }}</span>
          </div>
        </div>
      </div>
    </Modal>
  </Page>
</template>
