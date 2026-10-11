<script lang="ts" setup>
import type { TableColumnsType } from 'antdv-next';

import { computed, onMounted, reactive, ref } from 'vue';

import { Page } from '@vben/common-ui';
import { Plus, RefreshCw } from '@vben/icons';
import { $t } from '@vben/locales';

import {
  Button,
  Card,
  Form,
  FormItem,
  Input,
  InputNumber,
  Modal,
  Popconfirm,
  Select,
  Table,
  Tag,
  message,
} from 'antdv-next';

import {
  deleteFreight,
  getFreightList,
  saveFreight,
} from '#/api/management/logistics/freight';

defineOptions({ name: 'LogisticsFreight' });

const loading = ref(false);
const list = ref<any[]>([]);

const columns = computed<TableColumnsType>(() => [
  { title: 'ID', dataIndex: 'id', width: 70 },
  { title: $t('logistics.freight_page.fields.name'), dataIndex: 'name', width: 180 },
  { title: $t('logistics.freight_page.fields.chargeType'), key: 'chargeType', width: 110 },
  { title: $t('logistics.freight_page.fields.firstUnit'), dataIndex: 'firstUnit', width: 110 },
  { title: $t('logistics.freight_page.fields.firstPrice'), dataIndex: 'firstPrice', width: 110 },
  { title: $t('logistics.freight_page.fields.extraUnit'), dataIndex: 'extraUnit', width: 110 },
  { title: $t('logistics.freight_page.fields.extraPrice'), dataIndex: 'extraPrice', width: 110 },
  { title: $t('logistics.freight_page.fields.status'), key: 'status', width: 90 },
  { title: $t('logistics.common.action'), key: 'action', width: 150, fixed: 'right' },
]);

const chargeOptions = computed(() => [
  { label: $t('logistics.freight_page.charge.byPiece'), value: 1 },
  { label: $t('logistics.freight_page.charge.byWeight'), value: 2 },
  { label: $t('logistics.freight_page.charge.byVolume'), value: 3 },
]);

const statusOptions = computed(() => [
  { label: $t('logistics.freight_page.status.on'), value: 1 },
  { label: $t('logistics.freight_page.status.off'), value: 0 },
]);

const chargeText = (v?: number) => {
  if (v === 2) return $t('logistics.freight_page.charge.byWeight');
  if (v === 3) return $t('logistics.freight_page.charge.byVolume');
  return $t('logistics.freight_page.charge.byPiece');
};

async function fetchList() {
  loading.value = true;
  try {
    const res: any = await getFreightList();
    list.value = Array.isArray(res) ? res : (res?.records ?? []);
  } catch {
    // 统一错误提示
  } finally {
    loading.value = false;
  }
}

onMounted(fetchList);

const modalVisible = ref(false);
const submitting = ref(false);
const formRef = ref<any>(null);
const editingId = ref<null | number>(null);
const form = reactive<any>({
  name: '',
  chargeType: 1,
  firstUnit: undefined,
  firstPrice: undefined,
  extraUnit: undefined,
  extraPrice: undefined,
  status: 1,
});

const modalTitle = computed(() =>
  editingId.value === null
    ? $t('logistics.freight_page.create_title')
    : `${$t('logistics.common.edit')} · ${$t('logistics.freight_page.create_title')}`,
);

function openCreate() {
  editingId.value = null;
  Object.assign(form, {
    name: '',
    chargeType: 1,
    firstUnit: undefined,
    firstPrice: undefined,
    extraUnit: undefined,
    extraPrice: undefined,
    status: 1,
  });
  modalVisible.value = true;
}

function openEdit(record: any) {
  editingId.value = record.id;
  Object.assign(form, {
    name: record.name ?? '',
    chargeType: record.chargeType ?? 1,
    firstUnit: record.firstUnit ?? undefined,
    firstPrice: record.firstPrice ?? undefined,
    extraUnit: record.extraUnit ?? undefined,
    extraPrice: record.extraPrice ?? undefined,
    status: record.status ?? 1,
  });
  modalVisible.value = true;
}

async function submit() {
  try {
    await formRef.value?.validate();
  } catch {
    return;
  }
  submitting.value = true;
  try {
    const payload: any = { ...form };
    if (editingId.value !== null) payload.id = editingId.value;
    await saveFreight(payload);
    message.success($t('logistics.common.save_success'));
    modalVisible.value = false;
    await fetchList();
  } catch {
    // 统一错误提示
  } finally {
    submitting.value = false;
  }
}

async function handleDelete(record: any) {
  await deleteFreight(record.id);
  message.success($t('logistics.common.delete_success'));
  fetchList();
}
</script>

<template>
  <Page :auto-content-height="true">
    <Card :title="$t('logistics.freight_page.title')" :styles="{ body: { padding: '0' } }">
      <template #extra>
        <div class="flex items-center gap-2">
          <Button :loading="loading" @click="fetchList">
            <template #icon><RefreshCw class="mr-1 size-4 align-middle" /></template>
            {{ $t('logistics.common.refresh') }}
          </Button>
          <Button type="primary" @click="openCreate">
            <template #icon><Plus class="mr-1 size-4 align-middle" /></template>
            {{ $t('logistics.common.add') }}
          </Button>
        </div>
      </template>

      <Table
        :columns="columns"
        :data-source="list"
        :loading="loading"
        :pagination="{ pageSize: 10, showSizeChanger: false }"
        :scroll="{ x: 1000 }"
        size="small"
        row-key="id"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'chargeType'">
            <Tag>{{ chargeText(record.chargeType) }}</Tag>
          </template>
          <template v-else-if="column.key === 'status'">
            <Tag :color="record.status === 1 ? 'green' : 'default'">
              {{
                record.status === 1
                  ? $t('logistics.freight_page.status.on')
                  : $t('logistics.freight_page.status.off')
              }}
            </Tag>
          </template>
          <template v-else-if="column.key === 'action'">
            <div class="flex flex-nowrap items-center whitespace-nowrap">
              <Button type="link" size="small" @click="openEdit(record)">
                {{ $t('logistics.common.edit') }}
              </Button>
              <Popconfirm
                :title="$t('logistics.common.delete_confirm')"
                :ok-text="$t('logistics.common.delete')"
                :cancel-text="$t('logistics.common.cancel')"
                :ok-button-props="{ danger: true }"
                @confirm="handleDelete(record)"
              >
                <Button type="link" size="small" danger>
                  {{ $t('logistics.common.delete') }}
                </Button>
              </Popconfirm>
            </div>
          </template>
        </template>
        <template #emptyText>
          <span class="text-sm text-muted-foreground">{{ $t('logistics.common.empty') }}</span>
        </template>
      </Table>
    </Card>

    <Modal
      v-model:open="modalVisible"
      :title="modalTitle"
      :width="600"
      :mask-closable="false"
      :confirm-loading="submitting"
      :ok-text="$t('logistics.common.save')"
      :cancel-text="$t('logistics.common.cancel')"
      @ok="submit"
    >
      <Form ref="formRef" :model="form" layout="vertical">
        <div class="grid grid-cols-1 gap-x-6 md:grid-cols-2">
          <FormItem
            :label="$t('logistics.freight_page.fields.name')"
            name="name"
            :rules="[{ required: true, message: $t('logistics.freight_page.name_required') }]"
          >
            <Input v-model:value="form.name" class="w-full" />
          </FormItem>
          <FormItem :label="$t('logistics.freight_page.fields.chargeType')" name="chargeType">
            <Select v-model:value="form.chargeType" class="w-full" :options="chargeOptions" />
          </FormItem>
          <FormItem :label="$t('logistics.freight_page.fields.firstUnit')" name="firstUnit">
            <InputNumber v-model:value="form.firstUnit" :min="0" class="w-full" />
          </FormItem>
          <FormItem :label="$t('logistics.freight_page.fields.firstPrice')" name="firstPrice">
            <InputNumber v-model:value="form.firstPrice" :min="0" :precision="2" class="w-full" />
          </FormItem>
          <FormItem :label="$t('logistics.freight_page.fields.extraUnit')" name="extraUnit">
            <InputNumber v-model:value="form.extraUnit" :min="0" class="w-full" />
          </FormItem>
          <FormItem :label="$t('logistics.freight_page.fields.extraPrice')" name="extraPrice">
            <InputNumber v-model:value="form.extraPrice" :min="0" :precision="2" class="w-full" />
          </FormItem>
          <FormItem :label="$t('logistics.freight_page.fields.status')" name="status">
            <Select v-model:value="form.status" class="w-full" :options="statusOptions" />
          </FormItem>
        </div>
      </Form>
    </Modal>
  </Page>
</template>
