<script lang="ts" setup>
import type { TableColumnsType } from 'antdv-next';

import { computed, onMounted, reactive, ref } from 'vue';

import { Page } from '@vben/common-ui';
import { Plus, RefreshCw } from '@vben/icons';
import { $t } from '@vben/locales';

import {
  Button,
  Card,
  Empty,
  Form,
  FormItem,
  Input,
  InputNumber,
  message,
  Modal,
  Popconfirm,
  Select,
  Space,
  Switch,
  Table,
  Tag,
} from 'antdv-next';

import {
  createMemberPackage,
  deleteMemberPackage,
  getMemberPackages,
  updateMemberPackage,
} from '#/api/management/member/member';

defineOptions({ name: 'MemberPackage' });

interface PackageItem {
  id?: number | string;
  name?: string;
  type?: number;
  durationDays?: number;
  price?: number;
  originalPrice?: number;
  givePoints?: number;
  giveGrowth?: number;
  description?: string;
  sort?: number;
  status?: number;
}

const loading = ref(false);
const rows = ref<PackageItem[]>([]);
const modalOpen = ref(false);
const submitting = ref(false);
const editingId = ref<number | string | null>(null);
const formRef = ref();
const form = reactive<any>(emptyForm());

function emptyForm() {
  return {
    name: '',
    type: 1,
    durationDays: 30,
    price: 0,
    originalPrice: 0,
    givePoints: 0,
    giveGrowth: 0,
    description: '',
    sort: 0,
    status: 1,
  };
}

const typeOptions = computed(() => [
  { label: $t('member.package_page.typeMonthly'), value: 1 },
  { label: $t('member.package_page.typeQuarterly'), value: 2 },
  { label: $t('member.package_page.typeYearly'), value: 3 },
]);

function typeText(type?: number) {
  return typeOptions.value.find((item) => item.value === type)?.label ?? '-';
}

const columns = computed<TableColumnsType>(() => [
  { title: $t('member.package_page.name'), dataIndex: 'name', width: 140 },
  { title: $t('member.package_page.type'), key: 'type', width: 100 },
  {
    title: $t('member.package_page.durationDays'),
    dataIndex: 'durationDays',
    width: 100,
  },
  { title: $t('member.package_page.price'), dataIndex: 'price', width: 100 },
  {
    title: $t('member.package_page.originalPrice'),
    dataIndex: 'originalPrice',
    width: 100,
  },
  {
    title: $t('member.package_page.givePoints'),
    dataIndex: 'givePoints',
    width: 110,
  },
  {
    title: $t('member.package_page.giveGrowth'),
    dataIndex: 'giveGrowth',
    width: 110,
  },
  {
    title: $t('member.common.description'),
    dataIndex: 'description',
    width: 220,
    ellipsis: true,
  },
  { title: $t('member.common.sort'), dataIndex: 'sort', width: 80 },
  { title: $t('member.common.status'), key: 'status', width: 90 },
  {
    title: $t('member.common.operation'),
    key: 'actions',
    width: 150,
    fixed: 'right',
  },
]);

async function fetchData() {
  loading.value = true;
  try {
    const res: any = await getMemberPackages();
    rows.value = Array.isArray(res) ? res : (res?.records ?? []);
  } catch {
    rows.value = [];
  } finally {
    loading.value = false;
  }
}

function openCreate() {
  editingId.value = null;
  Object.assign(form, emptyForm());
  modalOpen.value = true;
}

function openEdit(record: PackageItem) {
  editingId.value = record.id ?? null;
  Object.assign(form, emptyForm(), {
    name: record.name ?? '',
    type: record.type ?? 1,
    durationDays: record.durationDays ?? 30,
    price: Number(record.price ?? 0),
    originalPrice: Number(record.originalPrice ?? 0),
    givePoints: record.givePoints ?? 0,
    giveGrowth: record.giveGrowth ?? 0,
    description: record.description ?? '',
    sort: record.sort ?? 0,
    status: record.status ?? 1,
  });
  modalOpen.value = true;
}

async function handleSubmit() {
  try {
    await formRef.value?.validate();
  } catch {
    return;
  }
  submitting.value = true;
  try {
    const payload: any = { ...form };
    if (editingId.value === null) {
      await createMemberPackage(payload);
    } else {
      await updateMemberPackage(editingId.value, payload);
    }
    message.success($t('member.common.saveSuccess'));
    modalOpen.value = false;
    await fetchData();
  } catch (error: any) {
    message.error(error?.message || $t('member.common.saveFailed'));
  } finally {
    submitting.value = false;
  }
}

async function toggleStatus(record: PackageItem, checked: boolean) {
  try {
    await updateMemberPackage(record.id, { ...record, status: checked ? 1 : 0 });
    message.success($t('member.common.saveSuccess'));
    await fetchData();
  } catch (error: any) {
    message.error(error?.message || $t('member.common.saveFailed'));
  }
}

async function handleDelete(record: PackageItem) {
  try {
    await deleteMemberPackage(record.id);
    message.success($t('member.common.deleteSuccess'));
    await fetchData();
  } catch (error: any) {
    message.error(error?.message || $t('member.common.saveFailed'));
  }
}

onMounted(fetchData);
</script>

<template>
  <Page
    :title="$t('member.package_page.title')"
    :description="$t('member.package_page.description')"
  >
    <Card :styles="{ body: { padding: '16px' } }">
      <div class="mb-4 flex flex-wrap items-center justify-end gap-2">
        <Button :loading="loading" @click="fetchData">
          <template #icon><RefreshCw class="size-4" /></template>
          {{ $t('member.common.refresh') }}
        </Button>
        <Button type="primary" @click="openCreate">
          <template #icon><Plus class="size-4" /></template>
          {{ $t('member.package_page.add') }}
        </Button>
      </div>

      <Table
        :columns="columns"
        :data-source="rows"
        :loading="loading"
        :pagination="false"
        :scroll="{ x: 1300 }"
        row-key="id"
        size="middle"
        :bordered="true"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'type'">
            <Tag color="blue">{{ typeText(record.type) }}</Tag>
          </template>
          <template v-else-if="column.key === 'status'">
            <Switch
              :checked="record.status === 1"
              size="small"
              @change="(value: any) => toggleStatus(record, value)"
            />
          </template>
          <template v-else-if="column.key === 'actions'">
            <Space :size="4">
              <Button type="link" size="small" @click="openEdit(record)">
                {{ $t('member.common.edit') }}
              </Button>
              <Popconfirm
                :title="$t('member.package_page.confirmDelete')"
                :ok-text="$t('member.common.delete')"
                :cancel-text="$t('member.common.cancel')"
                :ok-button-props="{ danger: true }"
                @confirm="handleDelete(record)"
              >
                <Button type="link" size="small" danger>
                  {{ $t('member.common.delete') }}
                </Button>
              </Popconfirm>
            </Space>
          </template>
        </template>
        <template #emptyText>
          <Empty :description="$t('member.package_page.empty')" />
        </template>
      </Table>
    </Card>

    <Modal
      v-model:open="modalOpen"
      :title="
        editingId === null
          ? $t('member.package_page.add')
          : $t('member.package_page.edit')
      "
      :width="680"
      :mask-closable="false"
      :confirm-loading="submitting"
      :ok-text="$t('member.common.save')"
      :cancel-text="$t('member.common.cancel')"
      @ok="handleSubmit"
      @cancel="modalOpen = false"
    >
      <Form
        ref="formRef"
        :model="form"
        layout="vertical"
        :rules="{
          name: {
            required: true,
            message: $t('member.package_page.name'),
          },
        }"
      >
        <div class="grid grid-cols-2 gap-x-4">
          <FormItem :label="$t('member.package_page.name')" name="name">
            <Input v-model:value="form.name" />
          </FormItem>
          <FormItem :label="$t('member.package_page.type')" name="type">
            <Select
              v-model:value="form.type"
              :options="typeOptions"
              class="w-full"
            />
          </FormItem>
          <FormItem
            :label="$t('member.package_page.durationDays')"
            name="durationDays"
          >
            <InputNumber
              v-model:value="form.durationDays"
              :min="1"
              class="w-full"
            />
          </FormItem>
          <FormItem :label="$t('member.package_page.price')" name="price">
            <InputNumber
              v-model:value="form.price"
              :min="0"
              :step="0.01"
              class="w-full"
            />
          </FormItem>
          <FormItem
            :label="$t('member.package_page.originalPrice')"
            name="originalPrice"
          >
            <InputNumber
              v-model:value="form.originalPrice"
              :min="0"
              :step="0.01"
              class="w-full"
            />
          </FormItem>
          <FormItem
            :label="$t('member.package_page.givePoints')"
            name="givePoints"
          >
            <InputNumber v-model:value="form.givePoints" :min="0" class="w-full" />
          </FormItem>
          <FormItem
            :label="$t('member.package_page.giveGrowth')"
            name="giveGrowth"
          >
            <InputNumber v-model:value="form.giveGrowth" :min="0" class="w-full" />
          </FormItem>
          <FormItem :label="$t('member.common.sort')" name="sort">
            <InputNumber v-model:value="form.sort" :min="0" class="w-full" />
          </FormItem>
        </div>
        <FormItem
          :label="$t('member.package_page.description')"
          name="description"
        >
          <Input v-model:value="form.description" />
        </FormItem>
        <FormItem :label="$t('member.common.status')" name="status">
          <Switch
            v-model:checked="form.status"
            :checked-value="1"
            :un-checked-value="0"
          />
        </FormItem>
      </Form>
    </Modal>
  </Page>
</template>
