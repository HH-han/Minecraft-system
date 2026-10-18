<script lang="ts" setup>
import type { TableColumnsType } from 'antdv-next';

import { computed, onMounted, reactive, ref } from 'vue';

import { Page } from '@vben/common-ui';
import { Plus, RefreshCw, Search } from '@vben/icons';
import { $t } from '@vben/locales';

import {
  Button,
  Card,
  Form,
  FormItem,
  Input,
  InputNumber,
  message,
  Modal,
  Popconfirm,
  Select,
  Table,
  Tag,
} from 'antdv-next';

import {
  addTrain,
  deleteTrain,
  getAllStations,
  getTrainList,
  getTrainStops,
  saveTrainStops,
  updateTrain,
} from '#/api/management/railway';

defineOptions({ name: 'RailwayTrainManagement' });

interface TrainInfo {
  endStationId?: null | number;
  endStationName?: string;
  firstClassSeats?: number;
  id: number;
  secondClassSeats?: number;
  sleeperSeats?: number;
  startStationId?: null | number;
  startStationName?: string;
  status?: number;
  trainNo: string;
  trainType: string;
}

interface StopRow {
  arriveTime: string;
  dayOffset: number;
  departTime: string;
  firstPrice: number;
  rowKey: number;
  secondPrice: number;
  sleeperPrice: number;
  stationId: null | number;
  stopOver: number;
}

const TRAIN_TYPES = ['G', 'D', 'Z', 'T', 'K'];

const loading = ref(false);
const list = ref<TrainInfo[]>([]);
const keyword = ref('');
const stations = ref<any[]>([]);

const stationOptions = computed(() =>
  stations.value.map((station) => ({
    label: `${station.stationName} · ${station.cityName}`,
    value: station.id,
  })),
);

const typeText = (type?: string) =>
  type ? $t(`railway.train_type.${type}`) : '-';

const typeColor = (type?: string) => {
  if (type === 'G') return 'blue';
  if (type === 'D') return 'green';
  if (type === 'Z') return 'purple';
  if (type === 'T') return 'orange';
  return 'default';
};

const columns = computed<TableColumnsType>(() => [
  { title: $t('railway.train_page.train_no'), dataIndex: 'trainNo', width: 110 },
  { title: $t('railway.train_page.type'), key: 'trainType', width: 100 },
  { title: $t('railway.train_page.route'), key: 'route', width: 230 },
  {
    title: $t('railway.train_page.second_seats'),
    dataIndex: 'secondClassSeats',
    width: 120,
  },
  {
    title: $t('railway.train_page.first_seats'),
    dataIndex: 'firstClassSeats',
    width: 120,
  },
  {
    title: $t('railway.train_page.sleeper_seats'),
    dataIndex: 'sleeperSeats',
    width: 120,
  },
  { title: $t('railway.common.status'), key: 'status', width: 90 },
  {
    title: $t('railway.common.actions'),
    key: 'action',
    width: 220,
    fixed: 'right',
  },
]);

async function fetchData() {
  loading.value = true;
  try {
    const res: any = await getTrainList({ keyword: keyword.value || undefined });
    list.value = Array.isArray(res) ? res : (res?.records ?? []);
  } catch {
    list.value = [];
  } finally {
    loading.value = false;
  }
}

async function fetchStations() {
  try {
    const res: any = await getAllStations();
    stations.value = Array.isArray(res) ? res : [];
  } catch {
    stations.value = [];
  }
}

onMounted(() => {
  fetchStations();
  fetchData();
});

// =========================
// 车次表单
// =========================
const modalVisible = ref(false);
const submitting = ref(false);
const formRef = ref<InstanceType<typeof Form> | null>(null);
const editingId = ref<null | number>(null);

const formRecord = reactive({
  trainNo: '',
  trainType: 'G',
  startStationId: null as null | number,
  endStationId: null as null | number,
  secondClassSeats: 0,
  firstClassSeats: 0,
  sleeperSeats: 0,
  status: 1,
});

const modalTitle = computed(() =>
  editingId.value === null
    ? $t('railway.train_page.create_title')
    : $t('railway.train_page.edit_title'),
);

const typeOptions = computed(() =>
  TRAIN_TYPES.map((type) => ({
    label: `${type} · ${$t(`railway.train_type.${type}`)}`,
    value: type,
  })),
);

const statusOptions = computed(() => [
  { label: $t('railway.common.enabled'), value: 1 },
  { label: $t('railway.common.disabled'), value: 0 },
]);

function openCreate() {
  editingId.value = null;
  formRecord.trainNo = '';
  formRecord.trainType = 'G';
  formRecord.startStationId = null;
  formRecord.endStationId = null;
  formRecord.secondClassSeats = 0;
  formRecord.firstClassSeats = 0;
  formRecord.sleeperSeats = 0;
  formRecord.status = 1;
  modalVisible.value = true;
}

function openEdit(record: TrainInfo) {
  editingId.value = record.id;
  formRecord.trainNo = record.trainNo ?? '';
  formRecord.trainType = record.trainType ?? 'G';
  formRecord.startStationId = record.startStationId ?? null;
  formRecord.endStationId = record.endStationId ?? null;
  formRecord.secondClassSeats = record.secondClassSeats ?? 0;
  formRecord.firstClassSeats = record.firstClassSeats ?? 0;
  formRecord.sleeperSeats = record.sleeperSeats ?? 0;
  formRecord.status = record.status ?? 1;
  modalVisible.value = true;
}

function closeModal() {
  modalVisible.value = false;
  editingId.value = null;
}

async function handleSave() {
  try {
    await formRef.value?.validate();
  } catch {
    return;
  }
  submitting.value = true;
  try {
    const payload = {
      ...formRecord,
      trainNo: formRecord.trainNo.trim().toUpperCase(),
    };
    if (editingId.value === null) {
      await addTrain(payload);
    } else {
      await updateTrain(editingId.value, payload);
    }
    message.success($t('railway.common.save_success'));
    closeModal();
    await fetchData();
  } catch {
    message.error($t('railway.common.save_failed'));
  } finally {
    submitting.value = false;
  }
}

async function handleDelete(record: TrainInfo) {
  try {
    await deleteTrain(record.id);
    message.success($t('railway.common.delete_success'));
    await fetchData();
  } catch {
    message.error($t('railway.common.delete_failed'));
  }
}

// =========================
// 经停站配置
// =========================
const stopsVisible = ref(false);
const stopsLoading = ref(false);
const stopsSaving = ref(false);
const stopsTrain = ref<null | TrainInfo>(null);
const stopRows = ref<StopRow[]>([]);
let rowSeed = 0;

const stopColumns = computed<TableColumnsType>(() => [
  { title: $t('railway.train_page.seq'), key: 'seq', width: 60 },
  { title: $t('railway.train_page.station'), key: 'station', width: 200 },
  { title: $t('railway.train_page.arrive'), key: 'arrive', width: 100 },
  { title: $t('railway.train_page.depart'), key: 'depart', width: 100 },
  { title: $t('railway.train_page.stop_over'), key: 'stopOver', width: 100 },
  { title: $t('railway.train_page.day_offset'), key: 'dayOffset', width: 90 },
  { title: $t('railway.train_page.second_price'), key: 'secondPrice', width: 140 },
  { title: $t('railway.train_page.first_price'), key: 'firstPrice', width: 140 },
  { title: $t('railway.train_page.sleeper_price'), key: 'sleeperPrice', width: 140 },
  { title: $t('railway.common.actions'), key: 'action', width: 80, fixed: 'right' },
]);

function createStopRow(data: Partial<StopRow> = {}): StopRow {
  return {
    rowKey: ++rowSeed,
    stationId: data.stationId ?? null,
    arriveTime: data.arriveTime ?? '',
    departTime: data.departTime ?? '',
    stopOver: data.stopOver ?? 0,
    dayOffset: data.dayOffset ?? 0,
    secondPrice: data.secondPrice ?? 0,
    firstPrice: data.firstPrice ?? 0,
    sleeperPrice: data.sleeperPrice ?? 0,
  };
}

function centsToYuan(cents?: number) {
  return Number(cents || 0) / 100;
}

async function openStops(record: TrainInfo) {
  stopsTrain.value = record;
  stopsVisible.value = true;
  stopsLoading.value = true;
  stopRows.value = [];
  try {
    const res: any = await getTrainStops(record.id);
    const rows = Array.isArray(res) ? res : [];
    if (rows.length === 0) {
      stopRows.value = [
        createStopRow({ stationId: record.startStationId ?? null }),
        createStopRow({ stationId: record.endStationId ?? null }),
      ];
    } else {
      stopRows.value = rows.map((row: any) =>
        createStopRow({
          stationId: row.stationId,
          arriveTime: row.arriveTime ?? '',
          departTime: row.departTime ?? '',
          stopOver: row.stopOver ?? 0,
          dayOffset: row.dayOffset ?? 0,
          secondPrice: centsToYuan(row.secondClassPriceCents),
          firstPrice: centsToYuan(row.firstClassPriceCents),
          sleeperPrice: centsToYuan(row.sleeperPriceCents),
        }),
      );
    }
  } catch {
    message.error($t('railway.common.load_failed'));
  } finally {
    stopsLoading.value = false;
  }
}

function addStopRow() {
  stopRows.value.push(createStopRow());
}

function removeStopRow(index: number) {
  stopRows.value.splice(index, 1);
}

function closeStops() {
  stopsVisible.value = false;
  stopsTrain.value = null;
  stopRows.value = [];
}

const TIME_PATTERN = /^\d{2}:\d{2}$/;

async function handleSaveStops() {
  if (!stopsTrain.value) return;
  const rows = stopRows.value.filter((row) => row.stationId !== null);
  if (rows.length < 2) {
    message.warning($t('railway.train_page.stops_hint'));
    return;
  }
  for (const row of rows) {
    if (row.arriveTime && !TIME_PATTERN.test(row.arriveTime)) {
      message.warning(`${$t('railway.train_page.arrive')}: HH:mm`);
      return;
    }
    if (row.departTime && !TIME_PATTERN.test(row.departTime)) {
      message.warning(`${$t('railway.train_page.depart')}: HH:mm`);
      return;
    }
  }
  stopsSaving.value = true;
  try {
    const payload = rows.map((row) => ({
      stationId: row.stationId,
      arriveTime: row.arriveTime || null,
      departTime: row.departTime || null,
      stopOver: row.stopOver ?? 0,
      dayOffset: row.dayOffset ?? 0,
      secondClassPriceCents: Math.round((row.secondPrice ?? 0) * 100),
      firstClassPriceCents: Math.round((row.firstPrice ?? 0) * 100),
      sleeperPriceCents: Math.round((row.sleeperPrice ?? 0) * 100),
    }));
    await saveTrainStops(stopsTrain.value.id, payload);
    message.success($t('railway.train_page.stops_saved'));
    closeStops();
    await fetchData();
  } catch {
    message.error($t('railway.common.save_failed'));
  } finally {
    stopsSaving.value = false;
  }
}
</script>

<template>
  <Page :auto-content-height="true">
    <Card :title="$t('railway.train_page.title')" :styles="{ body: { padding: '0' } }">
      <template #extra>
        <div class="flex items-center gap-2">
          <Input
            v-model:value="keyword"
            :placeholder="$t('railway.train_page.search_placeholder')"
            allow-clear
            class="w-48"
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
          <Button type="primary" @click="openCreate">
            <template #icon>
              <Plus class="mr-1 size-4 align-middle" />
            </template>
            {{ $t('railway.common.add') }}
          </Button>
        </div>
      </template>

      <Table
        :columns="columns"
        :data-source="list"
        :loading="loading"
        :pagination="false"
        :scroll="{ x: 1150 }"
        row-key="id"
        size="small"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'trainType'">
            <Tag :color="typeColor(record.trainType)">
              {{ typeText(record.trainType) }}
            </Tag>
          </template>
          <template v-else-if="column.key === 'route'">
            <span class="text-xs">
              {{ record.startStationName || '-' }}
              <span class="mx-1 text-muted-foreground">→</span>
              {{ record.endStationName || '-' }}
            </span>
          </template>
          <template v-else-if="column.key === 'status'">
            <Tag :color="Number(record.status) === 1 ? 'green' : 'default'">
              {{
                Number(record.status) === 1
                  ? $t('railway.common.enabled')
                  : $t('railway.common.disabled')
              }}
            </Tag>
          </template>
          <template v-else-if="column.key === 'action'">
            <div class="flex flex-nowrap items-center whitespace-nowrap">
              <Button type="link" size="small" @click="openStops(record)">
                {{ $t('railway.train_page.stops') }}
              </Button>
              <Button type="link" size="small" @click="openEdit(record)">
                {{ $t('railway.common.edit') }}
              </Button>
              <Popconfirm
                :title="$t('railway.common.delete_confirm')"
                :ok-text="$t('railway.common.delete')"
                :cancel-text="$t('railway.common.cancel')"
                :ok-button-props="{ danger: true }"
                @confirm="handleDelete(record)"
              >
                <Button type="link" size="small" danger>
                  {{ $t('railway.common.delete') }}
                </Button>
              </Popconfirm>
            </div>
          </template>
        </template>
        <template #emptyText>
          <span class="text-sm text-muted-foreground">{{ $t('railway.common.empty') }}</span>
        </template>
      </Table>
    </Card>

    <!-- 车次表单 -->
    <Modal
      v-model:open="modalVisible"
      :title="modalTitle"
      :width="640"
      :mask-closable="false"
      :confirm-loading="submitting"
      :ok-text="$t('railway.common.save')"
      :cancel-text="$t('railway.common.cancel')"
      @ok="handleSave"
      @cancel="closeModal"
    >
      <Form ref="formRef" :model="formRecord" layout="vertical">
        <div class="grid grid-cols-1 gap-x-6 md:grid-cols-2">
          <FormItem
            :label="$t('railway.train_page.train_no')"
            name="trainNo"
            :rules="[
              {
                required: true,
                message: $t('railway.train_page.train_no_required'),
              },
            ]"
          >
            <Input v-model:value="formRecord.trainNo" placeholder="G101" />
          </FormItem>
          <FormItem
            :label="$t('railway.train_page.type')"
            name="trainType"
            :rules="[
              {
                required: true,
                message: $t('railway.train_page.type_required'),
              },
            ]"
          >
            <Select
              v-model:value="formRecord.trainType"
              :options="typeOptions"
              class="w-full"
            />
          </FormItem>
          <FormItem
            :label="$t('railway.train_page.start')"
            name="startStationId"
            :rules="[
              {
                required: true,
                message: $t('railway.train_page.start_required'),
              },
            ]"
          >
            <Select
              v-model:value="formRecord.startStationId"
              :options="stationOptions"
              show-search
              option-filter-prop="label"
              class="w-full"
            />
          </FormItem>
          <FormItem
            :label="$t('railway.train_page.end')"
            name="endStationId"
            :rules="[
              {
                required: true,
                message: $t('railway.train_page.end_required'),
              },
            ]"
          >
            <Select
              v-model:value="formRecord.endStationId"
              :options="stationOptions"
              show-search
              option-filter-prop="label"
              class="w-full"
            />
          </FormItem>
          <FormItem :label="$t('railway.train_page.second_seats')" name="secondClassSeats">
            <InputNumber v-model:value="formRecord.secondClassSeats" :min="0" class="w-full" />
          </FormItem>
          <FormItem :label="$t('railway.train_page.first_seats')" name="firstClassSeats">
            <InputNumber v-model:value="formRecord.firstClassSeats" :min="0" class="w-full" />
          </FormItem>
          <FormItem :label="$t('railway.train_page.sleeper_seats')" name="sleeperSeats">
            <InputNumber v-model:value="formRecord.sleeperSeats" :min="0" class="w-full" />
          </FormItem>
          <FormItem :label="$t('railway.common.status')" name="status">
            <Select v-model:value="formRecord.status" :options="statusOptions" class="w-full" />
          </FormItem>
        </div>
      </Form>
    </Modal>

    <!-- 经停站 -->
    <Modal
      v-model:open="stopsVisible"
      :title="`${$t('railway.train_page.stops_title')} · ${stopsTrain?.trainNo ?? ''}`"
      :width="1100"
      :mask-closable="false"
      :confirm-loading="stopsSaving"
      :ok-text="$t('railway.train_page.save_stops')"
      :cancel-text="$t('railway.common.cancel')"
      :styles="{
        body: {
          maxHeight: 'calc(100vh - 300px)',
          overflowY: 'auto',
        },
      }"
      @ok="handleSaveStops"
      @cancel="closeStops"
    >
      <p class="mb-3 text-xs text-muted-foreground">
        {{ $t('railway.train_page.stops_hint') }}
      </p>
      <Table
        :columns="stopColumns"
        :data-source="stopRows"
        :loading="stopsLoading"
        :pagination="false"
        :scroll="{ x: 1050 }"
        row-key="rowKey"
        size="small"
      >
        <template #bodyCell="{ column, record, index }">
          <template v-if="column.key === 'seq'">
            <span class="tabular-nums">{{ index + 1 }}</span>
          </template>
          <template v-else-if="column.key === 'station'">
            <Select
              v-model:value="record.stationId"
              :options="stationOptions"
              show-search
              option-filter-prop="label"
              class="w-full"
              size="small"
            />
          </template>
          <template v-else-if="column.key === 'arrive'">
            <Input v-model:value="record.arriveTime" size="small" placeholder="HH:mm" />
          </template>
          <template v-else-if="column.key === 'depart'">
            <Input v-model:value="record.departTime" size="small" placeholder="HH:mm" />
          </template>
          <template v-else-if="column.key === 'stopOver'">
            <InputNumber v-model:value="record.stopOver" :min="0" size="small" class="w-full" />
          </template>
          <template v-else-if="column.key === 'dayOffset'">
            <InputNumber v-model:value="record.dayOffset" :min="0" size="small" class="w-full" />
          </template>
          <template v-else-if="column.key === 'secondPrice'">
            <InputNumber
              v-model:value="record.secondPrice"
              :min="0"
              :precision="2"
              size="small"
              class="w-full"
            />
          </template>
          <template v-else-if="column.key === 'firstPrice'">
            <InputNumber
              v-model:value="record.firstPrice"
              :min="0"
              :precision="2"
              size="small"
              class="w-full"
            />
          </template>
          <template v-else-if="column.key === 'sleeperPrice'">
            <InputNumber
              v-model:value="record.sleeperPrice"
              :min="0"
              :precision="2"
              size="small"
              class="w-full"
            />
          </template>
          <template v-else-if="column.key === 'action'">
            <Button type="link" size="small" danger @click="removeStopRow(index)">
              {{ $t('railway.common.delete') }}
            </Button>
          </template>
        </template>
      </Table>
      <Button class="mt-3" type="dashed" block @click="addStopRow">
        <template #icon>
          <Plus class="mr-1 size-4 align-middle" />
        </template>
        {{ $t('railway.train_page.add_stop') }}
      </Button>
    </Modal>
  </Page>
</template>
