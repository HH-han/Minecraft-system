<script lang="ts" setup>
// @ts-nocheck
import type { TableColumnsType } from 'antdv-next';

import { computed, onMounted, ref } from 'vue';

import { Page } from '@vben/common-ui';
import { $t } from '@vben/locales';

import {
  Button,
  Card,
  Empty,
  InputNumber,
  Popconfirm,
  Skeleton,
  Space,
  Switch,
  Table,
  Tabs,
  Tag,
  message,
} from 'antdv-next';

import {
  getRecommendationItems,
  setRecommendationFeature,
  triggerRecommendationRecalc,
} from '#/api/management/recommendation';

defineOptions({ name: 'RecommendationItems' });

const categoryTabs = [
  { key: 'attraction', label: $t('recommendation.categoryAttraction') },
  { key: 'hotel', label: $t('recommendation.categoryHotel') },
  { key: 'food', label: $t('recommendation.categoryRestaurant') },
  { key: 'product', label: $t('recommendation.categorySouvenir') },
];

const PAGE_SIZE = 20;

const category = ref('attraction');
const page = ref(1);
const loading = ref(false);
const rows = ref<any[]>([]);
// itemId -> 本地干预草稿
const drafts = ref<Record<string, any>>({});
const savingId = ref<number | string>('');
const recalcLoading = ref(false);

async function load() {
  loading.value = true;
  try {
    rows.value =
      (await getRecommendationItems(category.value, page.value, PAGE_SIZE)) ?? [];
    drafts.value = {};
  } catch {
    message.error($t('recommendation.loadFailed'));
  } finally {
    loading.value = false;
  }
}

function switchCategory(key: string) {
  category.value = key;
  page.value = 1;
  load();
}

function draftOf(row: any) {
  if (!drafts.value[row.itemId]) {
    drafts.value[row.itemId] = {
      featured: !!row.isFeatured,
      featureWeight: Number(row.featureWeight ?? 0),
      status: Number(row.status ?? 1),
    };
  }
  return drafts.value[row.itemId];
}

function dirty(row: any): boolean {
  const d = draftOf(row);
  return (
    d.featured !== !!row.isFeatured ||
    Math.abs(d.featureWeight - Number(row.featureWeight ?? 0)) > 1e-9 ||
    d.status !== Number(row.status ?? 1)
  );
}

async function save(row: any) {
  const d = draftOf(row);
  savingId.value = row.itemId;
  try {
    await setRecommendationFeature(category.value, row.itemId, {
      featured: d.featured,
      featureWeight: d.featureWeight,
      status: d.status,
    });
    message.success($t('recommendation.featureSuccess'));
    await load();
  } catch {
    // 错误提示由拦截器统一处理
  } finally {
    savingId.value = '';
  }
}

async function recalc(target?: string) {
  recalcLoading.value = true;
  try {
    const res = await triggerRecommendationRecalc(target);
    if (res?.status === 'FAILED') {
      message.error(res.errorMessage || $t('recommendation.recalcRunning'));
    } else {
      message.success(
        $t('recommendation.recalcSuccess', {
          category: res?.category ?? '-',
          items: res?.itemCount ?? 0,
          duration: res?.durationMs ?? 0,
        }),
      );
    }
    await load();
  } catch {
    // ignore
  } finally {
    recalcLoading.value = false;
  }
}

function fmt(v: any): string {
  const n = Number(v ?? 0);
  return n.toFixed(3);
}

const columns = computed<TableColumnsType>(() => [
  { title: $t('recommendation.itemId'), dataIndex: 'itemId', width: 90 },
  { title: $t('recommendation.itemName'), dataIndex: 'name', ellipsis: true },
  { title: $t('recommendation.city'), dataIndex: 'city', width: 100 },
  { title: $t('recommendation.finalScore'), dataIndex: 'recommendationScore', width: 100, align: 'right' },
  { title: $t('recommendation.popularityScore'), dataIndex: 'popularityIndex', width: 90, align: 'right' },
  { title: $t('recommendation.collaborativeScore'), dataIndex: 'collaborativeScore', width: 90, align: 'right' },
  { title: $t('recommendation.contentScore'), dataIndex: 'contentScore', width: 90, align: 'right' },
  { title: $t('recommendation.seasonalScore'), dataIndex: 'seasonalScore', width: 90, align: 'right' },
  { title: $t('recommendation.qualityScore'), dataIndex: 'qualityScore', width: 90, align: 'right' },
  { title: $t('recommendation.featured'), dataIndex: 'featured', width: 80, align: 'center' },
  { title: $t('recommendation.featureWeight'), dataIndex: 'featureWeightEdit', width: 130 },
  { title: $t('recommendation.rowStatus'), dataIndex: 'statusEdit', width: 90, align: 'center' },
  { title: '', dataIndex: 'actions', width: 110, fixed: 'right' },
]);

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
        <Popconfirm
          :title="$t('recommendation.recalcConfirmCategory', {
            category: categoryTabs.find((t) => t.key === category)?.label,
          })"
          @confirm="recalc(category)"
        >
          <Button type="primary" :loading="recalcLoading">
            {{ $t('recommendation.recalcCategory') }}
          </Button>
        </Popconfirm>
        <Popconfirm
          :title="$t('recommendation.recalcConfirmAll')"
          @confirm="recalc()"
        >
          <Button :loading="recalcLoading">
            {{ $t('recommendation.recalcAll') }}
          </Button>
        </Popconfirm>
      </Space>

      <Skeleton v-if="loading" active :paragraph="{ rows: 8 }" />
      <Empty v-else-if="rows.length === 0" :description="$t('recommendation.noData')" />
      <Table
        v-else
        :columns="columns"
        :data-source="rows"
        :pagination="false"
        :scroll="{ x: 1500 }"
        size="small"
        row-key="itemId"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.dataIndex === 'recommendationScore'">
            <strong>{{ fmt(record.recommendationScore) }}</strong>
          </template>
          <template v-else-if="['popularityIndex', 'collaborativeScore', 'contentScore', 'seasonalScore', 'qualityScore'].includes(column.dataIndex)">
            {{ fmt(record[column.dataIndex]) }}
          </template>
          <template v-else-if="column.dataIndex === 'featured'">
            <Tag v-if="record.isFeatured" color="gold">
              {{ $t('recommendation.featured') }}
            </Tag>
            <span v-else>-</span>
          </template>
          <template v-else-if="column.dataIndex === 'featuredEdit'">
            <Switch
              :checked="draftOf(record).featured"
              size="small"
              @change="(v: any) => (draftOf(record).featured = v)"
            />
          </template>
          <template v-else-if="column.dataIndex === 'featureWeightEdit'">
            <InputNumber
              v-model:value="draftOf(record).featureWeight"
              :min="0"
              :max="1"
              :step="0.05"
              size="small"
              style="width: 100px"
            />
          </template>
          <template v-else-if="column.dataIndex === 'statusEdit'">
            <Switch
              :checked="draftOf(record).status === 1"
              checked-children="on"
              un-checked-children="off"
              size="small"
              @change="(v: any) => (draftOf(record).status = v ? 1 : 0)"
            />
          </template>
          <template v-else-if="column.dataIndex === 'actions'">
            <Button
              type="link"
              size="small"
              :disabled="!dirty(record)"
              :loading="savingId === record.itemId"
              @click="save(record)"
            >
              {{ $t('recommendation.saveFeature') }}
            </Button>
          </template>
        </template>
      </Table>

      <Space style="margin-top: 16px; justify-content: flex-end; width: 100%">
        <Button :disabled="page <= 1" @click="page--; load()">‹</Button>
        <span>{{ $t('recommendation.page', { page }) }}</span>
        <Button :disabled="rows.length < 20" @click="page++; load()">›</Button>
      </Space>
    </Card>
  </Page>
</template>
