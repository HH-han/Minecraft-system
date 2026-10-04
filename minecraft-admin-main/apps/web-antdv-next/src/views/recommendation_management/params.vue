<script lang="ts" setup>
// @ts-nocheck
import type { TableColumnsType } from 'antdv-next';

import { computed, onMounted, ref } from 'vue';

import { Page } from '@vben/common-ui';
import { $t } from '@vben/locales';

import {
  Alert,
  Button,
  Card,
  Empty,
  Input,
  InputNumber,
  Popconfirm,
  Select,
  Skeleton,
  Space,
  Switch,
  Table,
  Tabs,
  Tag,
  message,
} from 'antdv-next';

import {
  getRecommendationConfig,
  resetRecommendationConfig,
  updateRecommendationConfig,
} from '#/api/management/recommendation';

defineOptions({ name: 'RecommendationParams' });

const STRATEGY_OPTIONS = ['HYBRID', 'POPULAR', 'CONTENT', 'COLLABORATIVE'].map(
  (v) => ({ value: v, label: v }),
);

const filterTabs = [
  { key: 'ALL', label: $t('recommendation.allParams') },
  { key: 'GLOBAL', label: $t('recommendation.scopeGlobal') },
  { key: 'attraction', label: $t('recommendation.categoryAttraction') },
  { key: 'hotel', label: $t('recommendation.categoryHotel') },
  { key: 'food', label: $t('recommendation.categoryRestaurant') },
  { key: 'product', label: $t('recommendation.categorySouvenir') },
];

const activeTab = ref('ALL');
const loading = ref(false);
const saving = ref(false);
const rows = ref<any[]>([]);
// id -> 编辑后的字符串值
const edits = ref<Record<string, string>>({});

async function load() {
  loading.value = true;
  try {
    rows.value = (await getRecommendationConfig()) ?? [];
    edits.value = {};
  } catch {
    message.error($t('recommendation.loadFailed'));
  } finally {
    loading.value = false;
  }
}

const filteredRows = computed(() => {
  if (activeTab.value === 'ALL') return rows.value;
  if (activeTab.value === 'GLOBAL') {
    return rows.value.filter((r) => r.scope === 'GLOBAL');
  }
  return rows.value.filter(
    (r) => r.scope === 'CATEGORY' && r.category === activeTab.value,
  );
});

const changedCount = computed(() => Object.keys(edits.value).length);

function currentValue(row: any): string {
  return Object.prototype.hasOwnProperty.call(edits.value, row.id)
    ? edits.value[row.id]
    : String(row.paramValue ?? '');
}

function setValue(row: any, value: any) {
  edits.value[row.id] = String(value);
}

function isIntegerKey(key: string): boolean {
  return [
    'cf.neighbor.k',
    'behavior.recency.days',
    'retention.days',
    'exposure.attribution.hours',
  ].includes(key);
}

async function save() {
  const changedIds = Object.keys(edits.value);
  if (changedIds.length === 0) {
    message.info($t('recommendation.noChanges'));
    return;
  }
  const payload = rows.value
    .filter((r) => changedIds.includes(r.id))
    .map((r) => ({
      scope: r.scope,
      category: r.category ?? '',
      paramKey: r.paramKey,
      paramValue: edits.value[r.id],
    }));
  saving.value = true;
  try {
    await updateRecommendationConfig(payload);
    message.success($t('recommendation.saveSuccess'));
    await load();
  } catch {
    // 响应拦截器已统一提示错误
  } finally {
    saving.value = false;
  }
}

async function reset(scope: 'category' | 'global') {
  try {
    await resetRecommendationConfig(scope === 'global' ? undefined : activeTab.value);
    message.success($t('recommendation.resetSuccess'));
    await load();
  } catch {
    // ignore
  }
}

const columns: TableColumnsType = [
  { title: $t('recommendation.paramsScope'), dataIndex: 'scope', width: 100 },
  { title: $t('recommendation.paramKey'), dataIndex: 'paramKey', width: 240 },
  { title: $t('recommendation.paramValue'), dataIndex: 'paramValue', width: 240 },
  { title: $t('recommendation.description'), dataIndex: 'description' },
];

onMounted(load);
</script>

<template>
  <Page auto-content-height>
    <Card :bordered="false">
      <Tabs v-model:activeKey="activeTab">
        <Tabs.TabPane
          v-for="tab in filterTabs"
          :key="tab.key"
          :tab="tab.label"
        />
      </Tabs>

      <Alert
        :message="$t('recommendation.weightHint')"
        type="info"
        show-icon
        style="margin-bottom: 16px"
      />

      <Space wrap style="margin-bottom: 16px">
        <Button type="primary" :loading="saving" @click="save">
          {{ $t('recommendation.save') }}
          <template v-if="changedCount > 0">
            ({{ $t('recommendation.changedCount', { count: changedCount }) }})
          </template>
        </Button>
        <Popconfirm
          :title="$t('recommendation.resetConfirm')"
          @confirm="reset('global')"
        >
          <Button>{{ $t('recommendation.resetGlobal') }}</Button>
        </Popconfirm>
        <Popconfirm
          v-if="activeTab !== 'ALL' && activeTab !== 'GLOBAL'"
          :title="$t('recommendation.resetConfirm')"
          @confirm="reset('category')"
        >
          <Button>{{ $t('recommendation.resetCategory') }}</Button>
        </Popconfirm>
      </Space>

      <Skeleton v-if="loading" active :paragraph="{ rows: 10 }" />
      <Empty v-else-if="filteredRows.length === 0" :description="$t('recommendation.noData')" />
      <Table
        v-else
        :columns="columns"
        :data-source="filteredRows"
        :pagination="{ pageSize: 20, showSizeChanger: false }"
        size="small"
        row-key="id"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.dataIndex === 'scope'">
            <Tag :color="record.scope === 'GLOBAL' ? 'blue' : 'geekblue'">
              {{
                record.scope === 'GLOBAL'
                  ? $t('recommendation.scopeGlobal')
                  : $t('recommendation.scopeCategory')
              }}
            </Tag>
          </template>
          <template v-else-if="column.dataIndex === 'paramKey'">
            <Space direction="vertical" :size="0">
              <span class="param-key">{{ record.paramKey }}</span>
              <span v-if="record.scope === 'CATEGORY'" class="param-category">
                {{ record.category }}
              </span>
            </Space>
          </template>
          <template v-else-if="column.dataIndex === 'paramValue'">
            <Switch
              v-if="record.valueType === 'BOOLEAN'"
              :checked="currentValue(record) === 'true'"
              checked-children="true"
              un-checked-children="false"
              @change="(v: any) => setValue(record, v)"
            />
            <Select
              v-else-if="record.paramKey === 'strategy'"
              :value="currentValue(record)"
              :options="STRATEGY_OPTIONS"
              style="width: 180px"
              @change="(v: any) => setValue(record, v)"
            />
            <InputNumber
              v-else-if="record.valueType === 'NUMBER'"
              :value="Number(currentValue(record))"
              :step="isIntegerKey(record.paramKey) ? 1 : 0.05"
              :min="0"
              style="width: 180px"
              @change="(v: any) => setValue(record, v ?? 0)"
            />
            <Input
              v-else
              :value="currentValue(record)"
              style="width: 220px"
              @change="(e: any) => setValue(record, e.target.value)"
            />
          </template>
        </template>
      </Table>
    </Card>
  </Page>
</template>

<style scoped>
.param-key {
  font-family: ui-monospace, monospace;
  font-size: 12px;
}

.param-category {
  color: rgb(107 114 128);
  font-size: 11px;
}
</style>
