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
  message,
  Modal,
  Popconfirm,
  Table,
} from 'antdv-next';

import {
  addStation,
  deleteStation,
  getStationList,
  updateStation,
} from '#/api/management/railway';

defineOptions({ name: 'RailwayStationManagement' });

interface StationInfo {
  cityName?: string;
  id: number;
  initial?: string;
  pinyin?: string;
  stationCode?: string;
  stationName?: string;
}

const loading = ref(false);
const list = ref<StationInfo[]>([]);
const keyword = ref('');

const columns = computed<TableColumnsType>(() => [
  { title: 'ID', dataIndex: 'id', width: 70 },
  { title: $t('railway.station_page.code'), dataIndex: 'stationCode', width: 110 },
  { title: $t('railway.station_page.name'), key: 'stationName', width: 160 },
  { title: $t('railway.station_page.city'), dataIndex: 'cityName', width: 120 },
  { title: $t('railway.station_page.pinyin'), dataIndex: 'pinyin', width: 160 },
  { title: $t('railway.station_page.initial'), dataIndex: 'initial', width: 90 },
  {
    title: $t('railway.common.actions'),
    key: 'action',
    width: 150,
    fixed: 'right',
  },
]);

async function fetchData() {
  loading.value = true;
  try {
    const res: any = await getStationList({ keyword: keyword.value || undefined });
    list.value = Array.isArray(res) ? res : (res?.records ?? []);
  } catch {
    list.value = [];
  } finally {
    loading.value = false;
  }
}

onMounted(fetchData);

// =========================
// 新增 / 编辑
// =========================
const modalVisible = ref(false);
const submitting = ref(false);
const formRef = ref<InstanceType<typeof Form> | null>(null);
const editingId = ref<null | number>(null);

const formRecord = reactive({
  stationCode: '',
  stationName: '',
  cityName: '',
  pinyin: '',
  initial: '',
});

const modalTitle = computed(() =>
  editingId.value === null
    ? $t('railway.station_page.create_title')
    : $t('railway.station_page.edit_title'),
);

function openCreate() {
  editingId.value = null;
  formRecord.stationCode = '';
  formRecord.stationName = '';
  formRecord.cityName = '';
  formRecord.pinyin = '';
  formRecord.initial = '';
  modalVisible.value = true;
}

function openEdit(record: StationInfo) {
  editingId.value = record.id;
  formRecord.stationCode = record.stationCode ?? '';
  formRecord.stationName = record.stationName ?? '';
  formRecord.cityName = record.cityName ?? '';
  formRecord.pinyin = record.pinyin ?? '';
  formRecord.initial = record.initial ?? '';
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
    const payload: any = {
      ...formRecord,
      stationCode: formRecord.stationCode.trim().toUpperCase(),
      stationName: formRecord.stationName.trim(),
      cityName: formRecord.cityName.trim() || formRecord.stationName.trim(),
      pinyin: formRecord.pinyin.trim(),
      initial: formRecord.initial.trim().toUpperCase(),
    };
    if (editingId.value === null) {
      await addStation(payload);
    } else {
      await updateStation(editingId.value, payload);
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

async function handleDelete(record: StationInfo) {
  try {
    await deleteStation(record.id);
    message.success($t('railway.common.delete_success'));
    await fetchData();
  } catch {
    message.error($t('railway.common.delete_failed'));
  }
}
</script>

<template>
  <Page :auto-content-height="true">
    <Card :title="$t('railway.station_page.title')" :styles="{ body: { padding: '0' } }">
      <template #extra>
        <div class="flex items-center gap-2">
          <Input
            v-model:value="keyword"
            :placeholder="$t('railway.station_page.search_placeholder')"
            allow-clear
            class="w-56"
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
        :scroll="{ x: 900 }"
        row-key="id"
        size="small"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'stationName'">
            <span class="font-medium">{{ record.stationName }}</span>
          </template>
          <template v-else-if="column.key === 'action'">
            <div class="flex flex-nowrap items-center whitespace-nowrap">
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

    <Modal
      v-model:open="modalVisible"
      :title="modalTitle"
      :width="560"
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
            :label="$t('railway.station_page.name')"
            name="stationName"
            :rules="[
              {
                required: true,
                message: $t('railway.station_page.name_required'),
              },
            ]"
          >
            <Input v-model:value="formRecord.stationName" />
          </FormItem>
          <FormItem
            :label="$t('railway.station_page.code')"
            name="stationCode"
            :rules="[
              {
                required: true,
                message: $t('railway.station_page.code_required'),
              },
            ]"
          >
            <Input v-model:value="formRecord.stationCode" />
          </FormItem>
          <FormItem :label="$t('railway.station_page.city')" name="cityName">
            <Input v-model:value="formRecord.cityName" />
          </FormItem>
          <FormItem :label="$t('railway.station_page.pinyin')" name="pinyin">
            <Input v-model:value="formRecord.pinyin" />
          </FormItem>
          <FormItem :label="$t('railway.station_page.initial')" name="initial">
            <Input v-model:value="formRecord.initial" :maxlength="1" />
          </FormItem>
        </div>
      </Form>
    </Modal>
  </Page>
</template>
