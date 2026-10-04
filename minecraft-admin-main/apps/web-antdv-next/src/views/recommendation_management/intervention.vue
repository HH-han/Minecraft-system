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
  Tag,
  TextArea,
  Radio,
  RadioGroup,
  message,
} from 'antdv-next';

import {
  createInterventionRule,
  deleteInterventionRule,
  getInterventionRules,
  updateInterventionRule,
} from '#/api/management/recommendation';

defineOptions({ name: 'RecommendationIntervention' });

// 作用范围过滤（含通用规则 ALL）
const scopeOptions = [
  { value: '', label: $t('recommendation.allParams') },
  { value: 'ALL', label: $t('recommendation.ivScopeAll') },
  { value: 'attraction', label: $t('recommendation.categoryAttraction') },
  { value: 'hotel', label: $t('recommendation.categoryHotel') },
  { value: 'food', label: $t('recommendation.categoryRestaurant') },
  { value: 'product', label: $t('recommendation.categorySouvenir') },
];

const statusOptions = [
  { value: '', label: $t('recommendation.allParams') },
  { value: '1', label: $t('recommendation.statusEnabled') },
  { value: '0', label: $t('recommendation.statusDisabled') },
];

const ruleTypeOptions = [
  { value: 'RANK', label: $t('recommendation.ivTypeRank'), color: 'gold' },
  { value: 'TIME', label: $t('recommendation.ivTypeTime'), color: 'cyan' },
  { value: 'SORT', label: $t('recommendation.ivTypeSort'), color: 'blue' },
  { value: 'HIDE', label: $t('recommendation.ivTypeHide'), color: 'red' },
  { value: 'PIN', label: $t('recommendation.ivTypePin'), color: 'purple' },
  { value: 'BOOST', label: $t('recommendation.ivTypeBoost'), color: 'orange' },
];

const actionTypeOptions = [
  { value: 'FILTER', label: $t('recommendation.ivActionFilter'), color: 'blue' },
  { value: 'SORT', label: $t('recommendation.ivActionSort'), color: 'cyan' },
  { value: 'HIDE', label: $t('recommendation.ivActionHide'), color: 'red' },
  { value: 'PIN', label: $t('recommendation.ivActionPin'), color: 'gold' },
  { value: 'BOOST', label: $t('recommendation.ivActionBoost'), color: 'orange' },
];

// 条件类型预设（选择框），custom 兜底兼容任意 JSON
const conditionOptions = [
  { value: 'all', label: $t('recommendation.ivCondAll') },
  { value: 'top_recommendation_score', label: $t('recommendation.ivCondTopScore') },
  { value: 'top_price', label: $t('recommendation.ivCondTopPrice') },
  { value: 'top_rating', label: $t('recommendation.ivCondTopRating') },
  { value: 'cmp_price_lte', label: $t('recommendation.ivCondPriceLte') },
  { value: 'cmp_price_gte', label: $t('recommendation.ivCondPriceGte') },
  { value: 'cmp_rating_gte', label: $t('recommendation.ivCondRatingGte') },
  { value: 'cmp_rating_lte', label: $t('recommendation.ivCondRatingLte') },
  { value: 'cmp_score_gte', label: $t('recommendation.ivCondScoreGte') },
  { value: 'cmp_score_lte', label: $t('recommendation.ivCondScoreLte') },
  { value: 'custom', label: $t('recommendation.ivCondCustom') },
];

const sortFieldOptions = [
  { value: 'score', label: $t('recommendation.ivFieldScore') },
  { value: 'price', label: $t('recommendation.ivFieldPrice') },
  { value: 'rating', label: $t('recommendation.ivFieldRating') },
];

const sortDirOptions = [
  { value: 'DESC', label: $t('recommendation.ivSortDesc') },
  { value: 'ASC', label: $t('recommendation.ivSortAsc') },
];

const conditionTypeSet = new Set(conditionOptions.map((o) => o.value));

// 条件 JSON -> 表单结构化字段
function parseCondition(json?: string) {
  const fallback = { condType: 'custom', condValue: 5, condJson: json ?? '{}' };
  if (!json || !json.trim()) {
    return { condType: 'all', condValue: 5, condJson: '' };
  }
  let obj: any;
  try {
    obj = JSON.parse(json);
  } catch {
    return fallback;
  }
  if (!obj || typeof obj !== 'object' || Array.isArray(obj)) {
    return fallback;
  }
  if (!obj.operator && !obj.field && !obj.metric) {
    return { condType: 'all', condValue: 5, condJson: '' };
  }
  if (obj.operator === 'TOP_N' && typeof obj.metric === 'string') {
    const key = `top_${obj.metric}`;
    if (conditionTypeSet.has(key)) {
      return { condType: key, condValue: Number(obj.value) || 0, condJson: '' };
    }
    return fallback;
  }
  if (typeof obj.field === 'string' && typeof obj.operator === 'string') {
    const key = `cmp_${obj.field}_${obj.operator.toLowerCase()}`;
    if (conditionTypeSet.has(key)) {
      return { condType: key, condValue: Number(obj.value) || 0, condJson: '' };
    }
  }
  return fallback;
}

// 表单结构化字段 -> 条件 JSON
function buildCondition(f: any): string {
  if (f.condType === 'custom') {
    return f.condJson.trim();
  }
  if (f.condType === 'all') {
    return '{}';
  }
  if (f.condType.startsWith('top_')) {
    return JSON.stringify({
      metric: f.condType.slice(4),
      operator: 'TOP_N',
      value: Number(f.condValue),
    });
  }
  const [, field, op] = f.condType.split('_');
  return JSON.stringify({ field, operator: op.toUpperCase(), value: Number(f.condValue) });
}

// 表单结构化字段 -> 动作参数 JSON
function buildActionParams(f: any): null | string {
  if (f.actionType === 'SORT') {
    return JSON.stringify({ orderBy: f.sortBy, order: f.sortDir });
  }
  if (f.actionType === 'BOOST') {
    return JSON.stringify({ weight: Number(f.boostWeight) });
  }
  return null;
}

const scopeFilter = ref('');
const statusFilter = ref('');
const ruleTypeFilter = ref('');
const loading = ref(false);
const rows = ref<any[]>([]);

// 弹窗
const modalVisible = ref(false);
const editingId = ref<number | string>('');
const submitting = ref(false);

function emptyForm() {
  return {
    ruleCode: '',
    ruleName: '',
    ruleType: 'RANK',
    description: '',
    scopeCategory: 'ALL',
    condType: 'top_recommendation_score',
    condValue: 5,
    condJson: '',
    actionType: 'FILTER',
    sortBy: 'score',
    sortDir: 'DESC',
    boostWeight: 0.6,
    priority: 10,
    effectiveStart: '',
    effectiveEnd: '',
    status: 1,
    remark: '',
  };
}

const form = ref(emptyForm());

async function load() {
  loading.value = true;
  try {
    const params: any = {};
    if (scopeFilter.value) params.scopeCategory = scopeFilter.value;
    if (statusFilter.value !== '') params.status = Number(statusFilter.value);
    if (ruleTypeFilter.value) params.ruleType = ruleTypeFilter.value;
    rows.value = (await getInterventionRules(params)) ?? [];
  } catch {
    message.error($t('recommendation.loadFailed'));
  } finally {
    loading.value = false;
  }
}

function ruleMeta(type: string) {
  return ruleTypeOptions.find((t) => t.value === type) ?? ruleTypeOptions[0];
}

function actionMeta(type: string) {
  return actionTypeOptions.find((t) => t.value === type) ?? actionTypeOptions[0];
}

function scopeLabel(scope: string) {
  const found = scopeOptions.find((s) => s.value === scope);
  return found ? found.label : scope;
}

// 后端返回 yyyy-MM-ddTHH:mm:ss，datetime-local 需要 yyyy-MM-ddTHH:mm
function toLocalInput(value?: string): string {
  return value ? String(value).slice(0, 16) : '';
}

function formatTimeRange(row: any): string {
  const s = row.effectiveStart
    ? String(row.effectiveStart).replace('T', ' ').slice(0, 16)
    : '';
  const e = row.effectiveEnd
    ? String(row.effectiveEnd).replace('T', ' ').slice(0, 16)
    : '';
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
  const cond = parseCondition(row.conditionJson);
  // 动作参数反解析
  let sortBy = 'score';
  let sortDir = 'DESC';
  let boostWeight = 0.6;
  if (row.actionParams) {
    try {
      const ap = JSON.parse(row.actionParams);
      if (typeof ap.orderBy === 'string') sortBy = ap.orderBy;
      if (typeof ap.order === 'string') sortDir = ap.order;
      if (ap.weight !== undefined) boostWeight = Number(ap.weight);
    } catch {
      // 旧数据格式异常时保持默认
    }
  }
  form.value = {
    ruleCode: row.ruleCode ?? '',
    ruleName: row.ruleName ?? '',
    ruleType: row.ruleType ?? 'RANK',
    description: row.description ?? '',
    scopeCategory: row.scopeCategory ?? 'ALL',
    condType: cond.condType,
    condValue: cond.condValue,
    condJson: cond.condJson,
    actionType: row.actionType ?? 'FILTER',
    sortBy,
    sortDir,
    boostWeight,
    priority: row.priority ?? 0,
    effectiveStart: toLocalInput(row.effectiveStart),
    effectiveEnd: toLocalInput(row.effectiveEnd),
    status: row.status ?? 1,
    remark: row.remark ?? '',
  };
  modalVisible.value = true;
}

// 校验 JSON 字符串是否为对象
function isJsonObject(text: string): boolean {
  if (!text || !text.trim()) return false;
  try {
    const parsed = JSON.parse(text);
    return parsed !== null && typeof parsed === 'object' && !Array.isArray(parsed);
  } catch {
    return false;
  }
}

function validateForm(): string {
  if (!editingId.value) {
    if (!form.value.ruleCode.trim() || !/^[A-Za-z0-9_-]{1,64}$/.test(form.value.ruleCode.trim())) {
      return $t('recommendation.ivCodeRequired');
    }
  }
  if (!form.value.ruleName.trim()) {
    return $t('recommendation.ivNameRequired');
  }
  const f = form.value as any;
  if (f.condType === 'custom') {
    if (!isJsonObject(f.condJson)) {
      return $t('recommendation.ivConditionInvalid');
    }
  } else {
    const value = Number(f.condValue);
    if (!Number.isFinite(value)) {
      return $t('recommendation.ivCondValueRequired');
    }
    if (f.condType.startsWith('top_') && value < 1) {
      return $t('recommendation.ivTopValueInvalid');
    }
  }
  if (f.actionType === 'BOOST') {
    const weight = Number(f.boostWeight);
    if (!Number.isFinite(weight) || weight <= 0 || weight > 1) {
      return $t('recommendation.ruleBoostRequired');
    }
  }
  if (f.effectiveStart && f.effectiveEnd
      && f.effectiveEnd < f.effectiveStart) {
    return $t('recommendation.ivTimeInvalid');
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
    ruleName: form.value.ruleName.trim(),
    ruleType: form.value.ruleType,
    description: form.value.description || null,
    scopeCategory: form.value.scopeCategory,
    conditionJson: buildCondition(form.value),
    actionType: form.value.actionType,
    actionParams: buildActionParams(form.value),
    priority: Number(form.value.priority ?? 0),
    effectiveStart: form.value.effectiveStart || null,
    effectiveEnd: form.value.effectiveEnd || null,
    status: form.value.status ? 1 : 0,
    remark: form.value.remark || null,
  };
  submitting.value = true;
  try {
    if (editingId.value) {
      await updateInterventionRule(editingId.value, payload);
    } else {
      payload.ruleCode = form.value.ruleCode.trim();
      await createInterventionRule(payload);
    }
    message.success($t('recommendation.ivSaveSuccess'));
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
    await deleteInterventionRule(row.id);
    message.success($t('recommendation.ivDeleteSuccess'));
    await load();
  } catch {
    // ignore
  }
}

// 表格内快速启停
async function toggleStatus(row: any, enabled: boolean) {
  try {
    await updateInterventionRule(row.id, {
      ruleName: row.ruleName,
      ruleType: row.ruleType,
      scopeCategory: row.scopeCategory,
      conditionJson: row.conditionJson,
      actionType: row.actionType,
      priority: row.priority ?? 0,
      status: enabled ? 1 : 0,
    });
    await load();
  } catch {
    // ignore
  }
}

const conditionPlaceholder = JSON.stringify({
  metric: 'recommendation_score',
  operator: 'TOP_N',
  value: 5,
});

const columns: TableColumnsType = [
  { title: $t('recommendation.ivRuleCode'), dataIndex: 'ruleCode', width: 150, ellipsis: true },
  { title: $t('recommendation.ivRuleName'), dataIndex: 'ruleName', width: 150, ellipsis: true },
  { title: $t('recommendation.ivRuleType'), dataIndex: 'ruleType', width: 100 },
  { title: $t('recommendation.ivScope'), dataIndex: 'scopeCategory', width: 100 },
  { title: $t('recommendation.ivCondition'), dataIndex: 'conditionJson', width: 280, ellipsis: true },
  { title: $t('recommendation.ivAction'), dataIndex: 'actionType', width: 90 },
  { title: $t('recommendation.ivActionParams'), dataIndex: 'actionParams', width: 170, ellipsis: true },
  { title: $t('recommendation.ivPriority'), dataIndex: 'priority', width: 80, align: 'center' },
  {
    title: `${$t('recommendation.startTime')} / ${$t('recommendation.endTime')}`,
    dataIndex: 'timeRange',
    width: 250,
  },
  { title: $t('recommendation.ruleStatus'), dataIndex: 'status', width: 80, align: 'center' },
  { title: $t('recommendation.ruleRemark'), dataIndex: 'remark', width: 150, ellipsis: true },
  { title: '', dataIndex: 'actions', width: 130, fixed: 'right' },
];

onMounted(load);
</script>

<template>
  <Page auto-content-height>
    <Card variant="borderless">
      <Space wrap style="margin: 12px 0">
        <Button type="primary" @click="openCreate">
          + {{ $t('recommendation.ivAddRule') }}
        </Button>
        <Select
          v-model:value="scopeFilter"
          :options="scopeOptions"
          style="width: 150px"
          @change="load"
        />
        <Select
          v-model:value="statusFilter"
          :options="statusOptions"
          style="width: 120px"
          @change="load"
        />
        <Select
          v-model:value="ruleTypeFilter"
          :options="[{ value: '', label: $t('recommendation.allParams') }, ...ruleTypeOptions.map((t) => ({ value: t.value, label: t.label }))]"
          style="width: 140px"
          @change="load"
        />
      </Space>

      <Alert
        :message="$t('recommendation.ivFormHint')"
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
        :scroll="{ x: 1600 }"
        size="small"
        row-key="id"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.dataIndex === 'ruleType'">
            <Tag :color="ruleMeta(record.ruleType).color">
              {{ ruleMeta(record.ruleType).label }}
            </Tag>
          </template>
          <template v-else-if="column.dataIndex === 'scopeCategory'">
            {{ scopeLabel(record.scopeCategory) }}
          </template>
          <template v-else-if="column.dataIndex === 'conditionJson'">
            <code class="json-cell">{{ record.conditionJson }}</code>
          </template>
          <template v-else-if="column.dataIndex === 'actionType'">
            <Tag :color="actionMeta(record.actionType).color">
              {{ actionMeta(record.actionType).label }}
            </Tag>
          </template>
          <template v-else-if="column.dataIndex === 'actionParams'">
            <code v-if="record.actionParams" class="json-cell">{{ record.actionParams }}</code>
            <span v-else>-</span>
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
                :title="$t('recommendation.ivDeleteConfirm')"
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
      :title="editingId ? $t('recommendation.ivEditRule') : $t('recommendation.ivAddRule')"
      :confirm-loading="submitting"
      destroy-on-hidden
      :width="560"
      @ok="submit"
      @cancel="modalVisible = false"
    >
      <Form layout="vertical" style="margin-top: 12px">
        <FormItem :label="$t('recommendation.ivRuleCode')">
          <Input
            v-model:value="form.ruleCode"
            :disabled="!!editingId"
            :maxlength="64"
            placeholder="top5_by_score"
          />
        </FormItem>
        <FormItem :label="$t('recommendation.ivRuleName')">
          <Input v-model:value="form.ruleName" :maxlength="100" />
        </FormItem>
        <FormItem :label="$t('recommendation.ivDescription')">
          <TextArea v-model:value="form.description" :rows="2" :maxlength="500" />
        </FormItem>
        <FormItem :label="$t('recommendation.ivScope')">
          <Select v-model:value="form.scopeCategory" :options="scopeOptions.slice(1)" />
        </FormItem>
        <FormItem :label="$t('recommendation.ivRuleType')">
          <RadioGroup v-model:value="form.ruleType">
            <Radio v-for="t in ruleTypeOptions" :key="t.value" :value="t.value">
              {{ t.label }}
            </Radio>
          </RadioGroup>
        </FormItem>
        <FormItem :label="$t('recommendation.ivCondType')">
          <Select v-model:value="form.condType" :options="conditionOptions" />
          <p class="field-hint">{{ $t('recommendation.ivConditionHint') }}</p>
        </FormItem>
        <FormItem
          v-if="form.condType !== 'all' && form.condType !== 'custom'"
          :label="$t('recommendation.ivCondValue')"
        >
          <InputNumber v-model:value="form.condValue" :min="0" style="width: 100%" />
        </FormItem>
        <FormItem v-if="form.condType === 'custom'" :label="$t('recommendation.ivCondition')">
          <TextArea
            v-model:value="form.condJson"
            :rows="3"
            :placeholder="conditionPlaceholder"
          />
        </FormItem>
        <FormItem :label="$t('recommendation.ivAction')">
          <RadioGroup v-model:value="form.actionType">
            <Radio v-for="t in actionTypeOptions" :key="t.value" :value="t.value">
              {{ t.label }}
            </Radio>
          </RadioGroup>
        </FormItem>
        <FormItem
          v-if="form.actionType === 'SORT'"
          :label="$t('recommendation.ivActionParams')"
        >
          <Space wrap>
            <Select
              v-model:value="form.sortBy"
              :options="sortFieldOptions"
              style="width: 160px"
            />
            <Select
              v-model:value="form.sortDir"
              :options="sortDirOptions"
              style="width: 120px"
            />
          </Space>
        </FormItem>
        <FormItem
          v-if="form.actionType === 'BOOST'"
          :label="$t('recommendation.boostWeight')"
        >
          <InputNumber
            v-model:value="form.boostWeight"
            :min="0.01"
            :max="1"
            :step="0.05"
            style="width: 100%"
          />
        </FormItem>
        <FormItem :label="$t('recommendation.ivPriority')">
          <InputNumber v-model:value="form.priority" :min="0" :precision="0" style="width: 100%" />
          <p class="field-hint">{{ $t('recommendation.ivPriorityHint') }}</p>
        </FormItem>
        <FormItem :label="$t('recommendation.startTime')">
          <Input v-model:value="form.effectiveStart" type="datetime-local" />
        </FormItem>
        <FormItem :label="$t('recommendation.endTime')">
          <Input v-model:value="form.effectiveEnd" type="datetime-local" />
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

.field-hint {
  margin-top: 4px;
  color: rgb(107 114 128);
  font-size: 12px;
  word-break: break-all;
}

.json-cell {
  font-family: ui-monospace, monospace;
  font-size: 12px;
}
</style>
