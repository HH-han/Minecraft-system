<script lang="ts" setup>
// @ts-nocheck
import type { TableColumnsType } from 'antdv-next';

import { onMounted, ref } from 'vue';

import { Page } from '@vben/common-ui';
import { $t } from '@vben/locales';

import {
  Alert,
  Button,
  Card,
  Empty,
  Form,
  FormItem,
  Input,
  InputNumber,
  Modal,
  Popconfirm,
  Select,
  Skeleton,
  Space,
  Switch,
  Table,
  Tabs,
  Tag,
  TextArea,
  Radio,
  RadioGroup,
  message,
} from 'antdv-next';

import {
  createRecommendationRule,
  deleteRecommendationRule,
  getRecommendationRules,
  updateRecommendationRule,
} from '#/api/management/recommendation';

defineOptions({ name: 'RecommendationRules' });

const categoryTabs = [
  { key: 'attraction', label: $t('recommendation.categoryAttraction') },
  { key: 'hotel', label: $t('recommendation.categoryHotel') },
  { key: 'food', label: $t('recommendation.categoryRestaurant') },
  { key: 'product', label: $t('recommendation.categorySouvenir') },
];

const ruleTypeOptions = [
  { value: 'PIN', label: $t('recommendation.rulePin'), color: 'gold' },
  { value: 'BOOST', label: $t('recommendation.ruleBoost'), color: 'blue' },
  { value: 'HIDE', label: $t('recommendation.ruleHide'), color: 'default' },
];

const statusOptions = [
  { value: '', label: $t('recommendation.allParams') },
  { value: '1', label: $t('recommendation.statusEnabled') },
  { value: '0', label: $t('recommendation.statusDisabled') },
];

const category = ref('attraction');
const statusFilter = ref('');
const loading = ref(false);
const rows = ref<any[]>([]);

// 弹窗
const modalVisible = ref(false);
const editingId = ref<number | string>('');
const submitting = ref(false);

function emptyForm() {
  return {
    itemId: null as null | number,
    ruleType: 'PIN',
    sortOrder: 0,
    boostWeight: 0.1,
    startTime: '',
    endTime: '',
    status: 1,
    remark: '',
  };
}

const form = ref(emptyForm());

async function load() {
  loading.value = true;
  try {
    const status = statusFilter.value === '' ? undefined : Number(statusFilter.value);
    rows.value = (await getRecommendationRules(category.value, status)) ?? [];
  } catch {
    message.error($t('recommendation.loadFailed'));
  } finally {
    loading.value = false;
  }
}

function switchCategory(key: string) {
  category.value = key;
  load();
}

function ruleMeta(type: string) {
  return ruleTypeOptions.find((t) => t.value === type) ?? ruleTypeOptions[0];
}

// 后端返回 yyyy-MM-ddTHH:mm:ss，datetime-local 需要 yyyy-MM-ddTHH:mm
function toLocalInput(value?: string): string {
  return value ? String(value).slice(0, 16) : '';
}

function toBackendTime(value: string) {
  return value ? value : null;
}

function formatTimeRange(row: any): string {
  const s = row.startTime ? String(row.startTime).replace('T', ' ').slice(0, 16) : '';
  const e = row.endTime ? String(row.endTime).replace('T', ' ').slice(0, 16) : '';
  if (!s && !e) return '-';
  return `${s || '立即'} ~ ${e || '长期'}`;
}

function openCreate() {
  editingId.value = '';
  form.value = emptyForm();
  modalVisible.value = true;
}

function openEdit(row: any) {
  editingId.value = row.id;
  form.value = {
    itemId: row.itemId,
    ruleType: row.ruleType,
    sortOrder: row.sortOrder ?? 0,
    boostWeight: Number(row.boostWeight ?? 0),
    startTime: toLocalInput(row.startTime),
    endTime: toLocalInput(row.endTime),
    status: row.status ?? 1,
    remark: row.remark ?? '',
  };
  modalVisible.value = true;
}

function validateForm(): string {
  if (!editingId.value && !form.value.itemId) {
    return $t('recommendation.ruleItemRequired');
  }
  if (form.value.ruleType === 'BOOST') {
    const w = Number(form.value.boostWeight);
    if (!Number.isFinite(w) || w <= 0 || w > 1) {
      return $t('recommendation.ruleBoostRequired');
    }
  }
  if (form.value.startTime && form.value.endTime
      && form.value.endTime < form.value.startTime) {
    return $t('recommendation.ruleTimeInvalid');
  }
  return '';
}

async function submit() {
  const err = validateForm();
  if (err) {
    message.warning(err);
    return;
  }
  const payload: any = {
    ruleType: form.value.ruleType,
    sortOrder: form.value.ruleType === 'PIN' ? Number(form.value.sortOrder ?? 0) : 0,
    boostWeight: form.value.ruleType === 'BOOST' ? Number(form.value.boostWeight) : 0,
    startTime: toBackendTime(form.value.startTime),
    endTime: toBackendTime(form.value.endTime),
    status: form.value.status ? 1 : 0,
    remark: form.value.remark || null,
  };
  submitting.value = true;
  try {
    if (editingId.value) {
      await updateRecommendationRule(editingId.value, payload);
    } else {
      payload.category = category.value;
      payload.itemId = Number(form.value.itemId);
      await createRecommendationRule(payload);
    }
    message.success($t('recommendation.ruleSaveSuccess'));
    modalVisible.value = false;
    await load();
  } catch {
    // 错误提示由响应拦截器统一处理
  } finally {
    submitting.value = false;
  }
}

async function remove(row: any) {
  try {
    await deleteRecommendationRule(row.id);
    message.success($t('recommendation.ruleDeleteSuccess'));
    await load();
  } catch {
    // ignore
  }
}

// 表格内快速启停
async function toggleStatus(row: any, enabled: boolean) {
  try {
    await updateRecommendationRule(row.id, {
      ruleType: row.ruleType,
      sortOrder: row.sortOrder ?? 0,
      boostWeight: Number(row.boostWeight ?? 0),
      status: enabled ? 1 : 0,
    });
    await load();
  } catch {
    // ignore
  }
}

const columns: TableColumnsType = [
  { title: $t('recommendation.ruleItemId'), dataIndex: 'itemId', width: 90 },
  { title: $t('recommendation.ruleItemName'), dataIndex: 'itemName', ellipsis: true },
  { title: $t('recommendation.city'), dataIndex: 'city', width: 100 },
  { title: $t('recommendation.ruleType'), dataIndex: 'ruleType', width: 90 },
  { title: $t('recommendation.sortOrder'), dataIndex: 'sortOrder', width: 90, align: 'center' },
  { title: $t('recommendation.boostWeight'), dataIndex: 'boostWeight', width: 100, align: 'right' },
  { title: $t('recommendation.startTime') + ' / ' + $t('recommendation.endTime'), dataIndex: 'timeRange', width: 260 },
  { title: $t('recommendation.ruleStatus'), dataIndex: 'status', width: 80, align: 'center' },
  { title: $t('recommendation.ruleRemark'), dataIndex: 'remark', ellipsis: true, width: 160 },
  { title: '', dataIndex: 'actions', width: 130, fixed: 'right' },
];

onMounted(load);
</script>

<template>
  <Page auto-content-height>
    <Card :bordered="false">
      <Tabs :active-key="category" @change="switchCategory">
        <Tabs.TabPane
          v-for="tab in categoryTabs"
          :key="tab.key"
          :tab="tab.label"
        />
      </Tabs>

      <Space wrap style="margin: 12px 0">
        <Button type="primary" @click="openCreate">
          + {{ $t('recommendation.addRule') }}
        </Button>
        <Select
          v-model:value="statusFilter"
          :options="statusOptions"
          style="width: 140px"
          @change="load"
        />
      </Space>

      <Alert
        :message="$t('recommendation.ruleFormHint')"
        type="info"
        show-icon
        style="margin-bottom: 16px"
      />

      <Skeleton v-if="loading" active :paragraph="{ rows: 8 }" />
      <Empty v-else-if="rows.length === 0" :description="$t('recommendation.noData')" />
      <Table
        v-else
        :columns="columns"
        :data-source="rows"
        :pagination="{ pageSize: 20, showSizeChanger: false }"
        :scroll="{ x: 1300 }"
        size="small"
        row-key="id"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.dataIndex === 'ruleType'">
            <Tag :color="ruleMeta(record.ruleType).color">
              {{ ruleMeta(record.ruleType).label }}
            </Tag>
          </template>
          <template v-else-if="column.dataIndex === 'sortOrder'">
            {{ record.ruleType === 'PIN' ? record.sortOrder : '-' }}
          </template>
          <template v-else-if="column.dataIndex === 'boostWeight'">
            {{ record.ruleType === 'BOOST' ? Number(record.boostWeight).toFixed(2) : '-' }}
          </template>
          <template v-else-if="column.dataIndex === 'timeRange'">
            {{ formatTimeRange(record) }}
          </template>
          <template v-else-if="column.dataIndex === 'status'">
            <Switch
              :checked="record.status === 1"
              size="small"
              @change="(v: any) => toggleStatus(record, v)"
            />
          </template>
          <template v-else-if="column.dataIndex === 'actions'">
            <Space :size="4">
              <Button type="link" size="small" @click="openEdit(record)">
                {{ $t('recommendation.editRule') }}
              </Button>
              <Popconfirm
                :title="$t('recommendation.ruleDeleteConfirm')"
                @confirm="remove(record)"
              >
                <Button type="link" size="small" danger>
                  {{ $t('recommendation.delete') }}
                </Button>
              </Popconfirm>
            </Space>
          </template>
        </template>
      </Table>
    </Card>

    <Modal
      :open="modalVisible"
      :title="editingId ? $t('recommendation.editRule') : $t('recommendation.addRule')"
      :confirm-loading="submitting"
      :destroy-on-close="true"
      @ok="submit"
      @cancel="modalVisible = false"
    >
      <Form layout="vertical" style="margin-top: 12px">
        <FormItem :label="$t('recommendation.ruleItemId')">
          <InputNumber
            v-model:value="form.itemId"
            :min="1"
            :precision="0"
            style="width: 100%"
            :disabled="!!editingId"
            :placeholder="String($t('recommendation.ruleItemId'))"
          />
        </FormItem>
        <FormItem :label="$t('recommendation.ruleType')">
          <RadioGroup v-model:value="form.ruleType">
            <Radio v-for="t in ruleTypeOptions" :key="t.value" :value="t.value">
              {{ t.label }}
            </Radio>
          </RadioGroup>
        </FormItem>
        <FormItem v-if="form.ruleType === 'PIN'" :label="$t('recommendation.sortOrder')">
          <InputNumber v-model:value="form.sortOrder" :min="0" :precision="0" style="width: 100%" />
        </FormItem>
        <FormItem v-if="form.ruleType === 'BOOST'" :label="$t('recommendation.boostWeight')">
          <InputNumber v-model:value="form.boostWeight" :min="0.01" :max="1" :step="0.05" style="width: 100%" />
        </FormItem>
        <FormItem :label="$t('recommendation.startTime')">
          <Input v-model:value="form.startTime" type="datetime-local" />
        </FormItem>
        <FormItem :label="$t('recommendation.endTime')">
          <Input v-model:value="form.endTime" type="datetime-local" />
        </FormItem>
        <p class="time-hint">{{ $t('recommendation.timeWindowHint') }}</p>
        <FormItem :label="$t('recommendation.ruleStatus')">
          <Switch v-model:checked="form.status" :checked-value="1" :un-checked-value="0" />
        </FormItem>
        <FormItem :label="$t('recommendation.ruleRemark')">
          <TextArea v-model:value="form.remark" :rows="2" :maxlength="255" show-count />
        </FormItem>
      </Form>
    </Modal>
  </Page>
</template>

<style scoped>
.time-hint {
  margin-top: -8px;
  color: rgb(107 114 128);
  font-size: 12px;
}
</style>
