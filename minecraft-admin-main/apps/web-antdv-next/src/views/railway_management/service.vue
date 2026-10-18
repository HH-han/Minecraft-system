<script lang="ts" setup>
import type { TableColumnsType } from 'antdv-next';

import { computed, onMounted, reactive, ref } from 'vue';

import { Page } from '@vben/common-ui';
import { RefreshCw, Search, Ticket } from '@vben/icons';
import { $t } from '@vben/locales';

import {
  Button,
  Card,
  Input,
  InputNumber,
  message,
  Modal,
  Table,
  Tag,
} from 'antdv-next';

import {
  generateServices,
  getInventory,
  getServiceList,
} from '#/api/management/railway';

defineOptions({ name: 'RailwayServiceManagement' });

interface ServiceInfo {
  id: number;
  trainId: number;
  trainNo: string;
  trainType: string;
  serviceDate: string;
  status: number;
  startStationName?: string;
  endStationName?: string;
  secondClassLeft?: number;
  firstClassLeft?: number;
  sleeperLeft?: number;
}

interface InventoryInfo {
  fromStopSeq: number;
  toStopSeq: number;
  fromStationName: string;
  toStationName: string;
  seatType: string;
  totalSeats: number;
  leftSeats: number;
  priceCents: number;
}

const formatDate = (offsetDays: number) => {
  const date = new Date();
  date.setDate(date.getDate() + offsetDays);
  const m = String(date.getMonth() + 1).padStart(2, '0');
  const d = String(date.getDate()).padStart(2, '0');
  return `${date.getFullYear()}-${m}-${d}`;
};

const loading = ref(false);
const list = ref<ServiceInfo[]>([]);
const filters = reactive({
  startDate: formatDate(0),
  endDate: formatDate(15),
  trainNo: '',
});

const generateForm = reactive({
  startDate: formatDate(0),
  days: 15,
});
const generating = ref(false);

const columns = computed<TableColumnsType>(() => [
  { title: $t('railway.service_page.date'), dataIndex: 'serviceDate', width: 120 },
  { title: $t('railway.service_page.train_no'), key: 'trainNo', width: 130 },
  { title: $t('railway.service_page.route'), key: 'route', width: 230 },
  { title: $t('railway.service_page.second_left'), key: 'secondLeft', width: 120 },
  { title: $t('railway.service_page.first_left'), key: 'firstLeft', width: 120 },
  { title: $t('railway.service_page.sleeper_left'), key: 'sleeperLeft', width: 120 },
  {
    title: $t('railway.common.actions'),
    key: 'action',
    width: 120,
    fixed: 'right',
  },
]);

const inventoryColumns = computed<TableColumnsType>(() => [
  { title: $t('railway.service_page.from_seg'), dataIndex: 'fromStationName', width: 130 },
  { title: $t('railway.service_page.to_seg'), dataIndex: 'toStationName', width: 130 },
  { title: $t('railway.service_page.seat_type'), key: 'seatType', width: 120 },
  { title: $t('railway.service_page.total_seats'), dataIndex: 'totalSeats', width: 100 },
  { title: $t('railway.service_page.left_seats'), key: 'leftSeats', width: 100 },
  { title: $t('railway.service_page.price'), key: 'price', width: 110 },
]);

const typeColor = (type?: string) => {
  if (type === 'G') return 'blue';
  if (type === 'D') return 'green';
  if (type === 'Z') return 'purple';
  if (type === 'T') return 'orange';
  return 'default';
};

const leftClass = (value?: number) =>
  Number(value || 0) <= 0 ? 'font-medium text-red-500' : '';

async function fetchData() {
  loading.value = true;
  try {
    const res: any = await getServiceList({
      startDate: filters.startDate || undefined,
      endDate: filters.endDate || undefined,
      trainNo: filters.trainNo || undefined,
    });
    list.value = Array.isArray(res) ? res : [];
  } catch {
    list.value = [];
  } finally {
    loading.value = false;
  }
}

onMounted(fetchData);

async function handleGenerate() {
  generating.value = true;
  try {
    const res: any = await generateServices({
      startDate: generateForm.startDate || undefined,
      days: generateForm.days || 15,
    });
    message.success(
      $t('railway.service_page.generate_success', { count: Number(res || 0) }),
    );
    await fetchData();
  } catch {
    message.error($t('railway.common.save_failed'));
  } finally {
    generating.value = false;
  }
}

// =========================
// 库存明细
// =========================
const inventoryVisible = ref(false);
const inventoryLoading = ref(false);
const inventoryList = ref<InventoryInfo[]>([]);
const inventoryService = ref<null | ServiceInfo>(null);

async function openInventory(record: ServiceInfo) {
  inventoryService.value = record;
  inventoryVisible.value = true;
  inventoryLoading.value = true;
  inventoryList.value = [];
  try {
    const res: any = await getInventory({ serviceId: record.id });
    inventoryList.value = Array.isArray(res) ? res : [];
  } catch {
    message.error($t('railway.common.load_failed'));
  } finally {
    inventoryLoading.value = false;
  }
}

function closeInventory() {
  inventoryVisible.value = false;
  inventoryService.value = null;
  inventoryList.value = [];
}

const formatYuan = (cents?: number) => (Number(cents || 0) / 100).toFixed(2);

const inventoryRowKey = (record: InventoryInfo) =>
  `${record.fromStopSeq}-${record.toStopSeq}-${record.seatType}`;
</script>

<template>
  <Page :auto-content-height="true">
    <div class="flex flex-col gap-4">
      <!-- 生成排班 -->
      <Card :title="$t('railway.service_page.generate_title')">
        <div class="flex flex-wrap items-end gap-3">
          <div class="flex flex-col gap-1">
            <span class="text-xs text-muted-foreground">
              {{ $t('railway.service_page.start_date') }}
            </span>
            <Input
              v-model:value="generateForm.startDate"
              :placeholder="$t('railway.service_page.start_date_placeholder')"
              class="w-44"
            />
          </div>
          <div class="flex flex-col gap-1">
            <span class="text-xs text-muted-foreground">
              {{ $t('railway.service_page.days') }}
            </span>
            <InputNumber v-model:value="generateForm.days" :min="1" :max="30" class="w-28" />
          </div>
          <Button type="primary" :loading="generating" @click="handleGenerate">
            <template #icon>
              <Ticket class="mr-1 size-4 align-middle" />
            </template>
            {{ $t('railway.service_page.generate') }}
          </Button>
        </div>
      </Card>

      <!-- 排班列表 -->
      <Card :title="$t('railway.service_page.title')" :styles="{ body: { padding: '0' } }">
        <template #extra>
          <div class="flex flex-wrap items-center gap-2">
            <Input
              v-model:value="filters.startDate"
              :placeholder="$t('railway.service_page.start_date_placeholder')"
              allow-clear
              class="w-36"
            />
            <Input
              v-model:value="filters.endDate"
              :placeholder="$t('railway.service_page.start_date_placeholder')"
              allow-clear
              class="w-36"
            />
            <Input
              v-model:value="filters.trainNo"
              :placeholder="$t('railway.service_page.train_no')"
              allow-clear
              class="w-32"
              @press-enter="fetchData"
            >
              <template #prefix>
                <Search class="size-4 text-muted-foreground" />
              </template>
            </Input>
            <Button :loading="loading" @click="fetchData">
              <template #icon>
                <RefreshCw class="mr-1 size-4 align-middle" />
              </template>
              {{ $t('railway.common.refresh') }}
            </Button>
          </div>
        </template>

        <Table
          :columns="columns"
          :data-source="list"
          :loading="loading"
          :pagination="{ pageSize: 20, showSizeChanger: false }"
          :scroll="{ x: 1000 }"
          row-key="id"
          size="small"
        >
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'trainNo'">
              <div class="flex items-center gap-2">
                <span class="font-medium">{{ record.trainNo }}</span>
                <Tag :color="typeColor(record.trainType)">{{ record.trainType }}</Tag>
              </div>
            </template>
            <template v-else-if="column.key === 'route'">
              <span class="text-xs">
                {{ record.startStationName || '-' }}
                <span class="mx-1 text-muted-foreground">→</span>
                {{ record.endStationName || '-' }}
              </span>
            </template>
            <template v-else-if="column.key === 'secondLeft'">
              <span class="tabular-nums" :class="leftClass(record.secondClassLeft)">
                {{ record.secondClassLeft ?? 0 }}
              </span>
            </template>
            <template v-else-if="column.key === 'firstLeft'">
              <span class="tabular-nums" :class="leftClass(record.firstClassLeft)">
                {{ record.firstClassLeft ?? 0 }}
              </span>
            </template>
            <template v-else-if="column.key === 'sleeperLeft'">
              <span class="tabular-nums" :class="leftClass(record.sleeperLeft)">
                {{ record.sleeperLeft ?? 0 }}
              </span>
            </template>
            <template v-else-if="column.key === 'action'">
              <Button type="link" size="small" @click="openInventory(record)">
                {{ $t('railway.service_page.inventory') }}
              </Button>
            </template>
          </template>
          <template #emptyText>
            <span class="text-sm text-muted-foreground">
              {{ $t('railway.service_page.no_service') }}
            </span>
          </template>
        </Table>
      </Card>
    </div>

    <!-- 库存明细 -->
    <Modal
      v-model:open="inventoryVisible"
      :title="`${$t('railway.service_page.inventory_title')} · ${inventoryService?.trainNo ?? ''} ${inventoryService?.serviceDate ?? ''}`"
      :width="900"
      :footer="null"
      @cancel="closeInventory"
    >
      <Table
        :columns="inventoryColumns"
        :data-source="inventoryList"
        :loading="inventoryLoading"
        :pagination="false"
        :scroll="{ x: 750 }"
        :row-key="inventoryRowKey"
        size="small"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'seatType'">
            <Tag color="blue">{{ $t(`railway.seat.${record.seatType}`) }}</Tag>
          </template>
          <template v-else-if="column.key === 'leftSeats'">
            <span class="tabular-nums" :class="leftClass(record.leftSeats)">
              {{ record.leftSeats }}
            </span>
          </template>
          <template v-else-if="column.key === 'price'">
            <span class="tabular-nums">¥{{ formatYuan(record.priceCents) }}</span>
          </template>
        </template>
        <template #emptyText>
          <span class="text-sm text-muted-foreground">{{ $t('railway.common.empty') }}</span>
        </template>
      </Table>
    </Modal>
  </Page>
</template>
