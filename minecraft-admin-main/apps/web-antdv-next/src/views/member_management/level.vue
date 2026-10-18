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
  Space,
  Switch,
  Table,
  Tag,
} from 'antdv-next';

import {
  createMemberLevel,
  deleteMemberLevel,
  getMemberLevels,
  updateMemberLevel,
} from '#/api/management/member/member';

defineOptions({ name: 'MemberLevel' });

interface LevelItem {
  id?: number | string;
  level?: number;
  name?: string;
  icon?: string;
  growthMin?: number;
  growthMax?: number;
  discount?: number;
  pointsRate?: number;
  benefits?: string;
  sort?: number;
  status?: number;
}

const loading = ref(false);
const rows = ref<LevelItem[]>([]);

const modalOpen = ref(false);
const submitting = ref(false);
const editingId = ref<number | string | null>(null);
const formRef = ref();

const form = reactive<any>(emptyForm());

function emptyForm() {
  return {
    level: 1,
    name: '',
    icon: '',
    growthMin: 0,
    growthMax: 0,
    discount: 1,
    pointsRate: 1,
    benefitsText: '',
    sort: 0,
    status: 1,
  };
}

const columns = computed<TableColumnsType>(() => [
  { title: $t('member.level_page.level'), dataIndex: 'level', width: 90 },
  { title: $t('member.level_page.name'), dataIndex: 'name', width: 140 },
  {
    title: $t('member.level_page.growthRange'),
    key: 'growthRange',
    width: 170,
  },
  { title: $t('member.level_page.discount'), dataIndex: 'discount', width: 100 },
  {
    title: $t('member.level_page.pointsRate'),
    dataIndex: 'pointsRate',
    width: 100,
  },
  {
    title: $t('member.level_page.benefits'),
    key: 'benefits',
    width: 280,
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
    const res: any = await getMemberLevels();
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

function openEdit(record: LevelItem) {
  editingId.value = record.id ?? null;
  Object.assign(form, emptyForm(), {
    level: record.level ?? 1,
    name: record.name ?? '',
    icon: record.icon ?? '',
    growthMin: record.growthMin ?? 0,
    growthMax: record.growthMax ?? 0,
    discount: Number(record.discount ?? 1),
    pointsRate: Number(record.pointsRate ?? 1),
    benefitsText: parseBenefits(record.benefits),
    sort: record.sort ?? 0,
    status: record.status ?? 1,
  });
  modalOpen.value = true;
}

function parseBenefits(value?: string) {
  if (!value) return '';
  try {
    const list = JSON.parse(value);
    return Array.isArray(list) ? list.join(',') : '';
  } catch {
    return value;
  }
}

function benefitList(record: LevelItem) {
  if (!record.benefits) return [];
  try {
    const list = JSON.parse(record.benefits);
    return Array.isArray(list) ? list : [];
  } catch {
    return [record.benefits];
  }
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
    delete payload.benefitsText;
    payload.benefits = JSON.stringify(
      String(form.benefitsText || '')
        .split(',')
        .map((item) => item.trim())
        .filter(Boolean),
    );
    if (editingId.value === null) {
      await createMemberLevel(payload);
    } else {
      await updateMemberLevel(editingId.value, payload);
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

async function toggleStatus(record: LevelItem, checked: boolean) {
  try {
    await updateMemberLevel(record.id, { ...record, status: checked ? 1 : 0 });
    message.success($t('member.common.saveSuccess'));
    await fetchData();
  } catch (error: any) {
    message.error(error?.message || $t('member.common.saveFailed'));
  }
}

async function handleDelete(record: LevelItem) {
  try {
    await deleteMemberLevel(record.id);
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
    :title="$t('member.level_page.title')"
    :description="$t('member.level_page.description')"
  >
    <Card :styles="{ body: { padding: '16px' } }">
      <div class="mb-4 flex flex-wrap items-center justify-end gap-2">
        <Button :loading="loading" @click="fetchData">
          <template #icon><RefreshCw class="size-4" /></template>
          {{ $t('member.common.refresh') }}
        </Button>
        <Button type="primary" @click="openCreate">
          <template #icon><Plus class="size-4" /></template>
          {{ $t('member.level_page.add') }}
        </Button>
      </div>

      <Table
        :columns="columns"
        :data-source="rows"
        :loading="loading"
        :pagination="false"
        :scroll="{ x: 1200 }"
        row-key="id"
        size="middle"
        :bordered="true"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'growthRange'">
            {{ record.growthMin }} ~
            {{ record.growthMax ? record.growthMax : '∞' }}
          </template>
          <template v-else-if="column.key === 'benefits'">
            <Space :size="4" wrap>
              <Tag v-for="item in benefitList(record)" :key="item" color="blue">
                {{ item }}
              </Tag>
              <span v-if="benefitList(record).length === 0">-</span>
            </Space>
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
                :title="$t('member.level_page.confirmDelete')"
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
          <Empty :description="$t('member.level_page.empty')" />
        </template>
      </Table>
    </Card>

    <Modal
      v-model:open="modalOpen"
      :title="
        editingId === null
          ? $t('member.level_page.add')
          : $t('member.level_page.edit')
      "
      :width="640"
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
          level: {
            required: true,
            message: $t('member.level_page.level'),
          },
          name: {
            required: true,
            message: $t('member.level_page.name'),
          },
        }"
      >
        <div class="grid grid-cols-2 gap-x-4">
          <FormItem :label="$t('member.level_page.level')" name="level">
            <InputNumber v-model:value="form.level" :min="0" class="w-full" />
          </FormItem>
          <FormItem :label="$t('member.level_page.name')" name="name">
            <Input v-model:value="form.name" />
          </FormItem>
          <FormItem :label="$t('member.level_page.growthMin')" name="growthMin">
            <InputNumber v-model:value="form.growthMin" :min="0" class="w-full" />
          </FormItem>
          <FormItem :label="$t('member.level_page.growthMax')" name="growthMax">
            <InputNumber v-model:value="form.growthMax" :min="0" class="w-full" />
          </FormItem>
          <FormItem :label="$t('member.level_page.discount')" name="discount">
            <InputNumber
              v-model:value="form.discount"
              :min="0.01"
              :max="1"
              :step="0.01"
              class="w-full"
            />
          </FormItem>
          <FormItem :label="$t('member.level_page.pointsRate')" name="pointsRate">
            <InputNumber
              v-model:value="form.pointsRate"
              :min="0.1"
              :step="0.1"
              class="w-full"
            />
          </FormItem>
          <FormItem :label="$t('member.level_page.icon')" name="icon">
            <Input v-model:value="form.icon" />
          </FormItem>
          <FormItem :label="$t('member.common.sort')" name="sort">
            <InputNumber v-model:value="form.sort" :min="0" class="w-full" />
          </FormItem>
        </div>
        <FormItem
          :label="$t('member.level_page.benefits')"
          name="benefitsText"
        >
          <Input
            v-model:value="form.benefitsText"
            :placeholder="$t('member.level_page.benefitsPlaceholder')"
          />
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
