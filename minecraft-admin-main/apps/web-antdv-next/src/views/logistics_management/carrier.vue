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
  deleteCarrier,
  getCarrierList,
  saveCarrier,
} from '#/api/management/logistics/carrier';

defineOptions({ name: 'LogisticsCarrier' });

const loading = ref(false);
const list = ref<any[]>([]);

const columns = computed<TableColumnsType>(() => [
  { title: 'ID', dataIndex: 'id', width: 70 },
  { title: $t('logistics.carrier_page.fields.code'), dataIndex: 'code', width: 120 },
  { title: $t('logistics.carrier_page.fields.name'), dataIndex: 'name', width: 180 },
  { title: $t('logistics.carrier_page.fields.apiType'), dataIndex: 'apiType', width: 130 },
  { title: $t('logistics.carrier_page.fields.phone'), dataIndex: 'phone', width: 140 },
  { title: $t('logistics.carrier_page.fields.website'), dataIndex: 'website', ellipsis: true },
  { title: $t('logistics.carrier_page.fields.sort'), dataIndex: 'sort', width: 80 },
  { title: $t('logistics.carrier_page.fields.status'), key: 'status', width: 90 },
  { title: $t('logistics.common.action'), key: 'action', width: 150, fixed: 'right' },
]);

const apiTypeOptions = computed(() => [
  { label: $t('logistics.carrier_page.api.kuaidi100'), value: 'kuaidi100' },
  { label: $t('logistics.carrier_page.api.kuaidiniao'), value: 'kuaidiniao' },
  { label: $t('logistics.carrier_page.api.aliyun'), value: 'aliyun' },
]);

const statusOptions = computed(() => [
  { label: $t('logistics.carrier_page.status.on'), value: 1 },
  { label: $t('logistics.carrier_page.status.off'), value: 0 },
]);

async function fetchList() {
  loading.value = true;
  try {
    const res: any = await getCarrierList();
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
  code: '',
  name: '',
  logoUrl: '',
  apiType: 'kuaidi100',
  apiKey: '',
  apiSecret: '',
  apiCustomer: '',
  phone: '',
  website: '',
  sort: 0,
  status: 1,
});

const modalTitle = computed(() =>
  editingId.value === null
    ? $t('logistics.carrier_page.create_title')
    : `${$t('logistics.common.edit')} · ${$t('logistics.carrier_page.create_title')}`,
);

function openCreate() {
  editingId.value = null;
  Object.assign(form, {
    code: '',
    name: '',
    logoUrl: '',
    apiType: 'kuaidi100',
    apiKey: '',
    apiSecret: '',
    apiCustomer: '',
    phone: '',
    website: '',
    sort: 0,
    status: 1,
  });
  modalVisible.value = true;
}

function openEdit(record: any) {
  editingId.value = record.id;
  Object.assign(form, {
    code: record.code ?? '',
    name: record.name ?? '',
    logoUrl: record.logoUrl ?? '',
    apiType: record.apiType ?? 'kuaidi100',
    apiKey: record.apiKey ?? '',
    apiSecret: record.apiSecret ?? '',
    apiCustomer: record.apiCustomer ?? '',
    phone: record.phone ?? '',
    website: record.website ?? '',
    sort: record.sort ?? 0,
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
    await saveCarrier(payload);
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
  await deleteCarrier(record.id);
  message.success($t('logistics.common.delete_success'));
  fetchList();
}
</script>

<template>
  <Page :auto-content-height="true">
    <Card :title="$t('logistics.carrier_page.title')" :styles="{ body: { padding: '0' } }">
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
        :scroll="{ x: 1100 }"
        size="small"
        row-key="id"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'status'">
            <Tag :color="record.status === 1 ? 'green' : 'default'">
              {{
                record.status === 1
                  ? $t('logistics.carrier_page.status.on')
                  : $t('logistics.carrier_page.status.off')
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
      :width="640"
      :mask-closable="false"
      :confirm-loading="submitting"
      :ok-text="$t('logistics.common.save')"
      :cancel-text="$t('logistics.common.cancel')"
      @ok="submit"
    >
      <Form ref="formRef" :model="form" layout="vertical">
        <div class="grid grid-cols-1 gap-x-6 md:grid-cols-2">
          <FormItem
            :label="$t('logistics.carrier_page.fields.code')"
            name="code"
            :rules="[{ required: true, message: $t('logistics.carrier_page.code_required') }]"
          >
            <Input v-model:value="form.code" class="w-full" placeholder="SF/YTO/ZTO" />
          </FormItem>
          <FormItem
            :label="$t('logistics.carrier_page.fields.name')"
            name="name"
            :rules="[{ required: true, message: $t('logistics.carrier_page.name_required') }]"
          >
            <Input v-model:value="form.name" class="w-full" />
          </FormItem>
          <FormItem :label="$t('logistics.carrier_page.fields.apiType')" name="apiType">
            <Select v-model:value="form.apiType" class="w-full" :options="apiTypeOptions" />
          </FormItem>
          <FormItem :label="$t('logistics.carrier_page.fields.phone')" name="phone">
            <Input v-model:value="form.phone" class="w-full" />
          </FormItem>
          <FormItem :label="$t('logistics.carrier_page.fields.website')" name="website">
            <Input v-model:value="form.website" class="w-full" />
          </FormItem>
          <FormItem :label="$t('logistics.carrier_page.fields.sort')" name="sort">
            <InputNumber v-model:value="form.sort" :min="0" class="w-full" />
          </FormItem>
          <FormItem label="ApiKey" name="apiKey">
            <Input v-model:value="form.apiKey" class="w-full" />
          </FormItem>
          <FormItem label="ApiSecret" name="apiSecret">
            <Input.Password v-model:value="form.apiSecret" class="w-full" />
          </FormItem>
          <FormItem :label="$t('logistics.carrier_page.fields.status')" name="status">
            <Select v-model:value="form.status" class="w-full" :options="statusOptions" />
          </FormItem>
        </div>
      </Form>
    </Modal>
  </Page>
</template>
