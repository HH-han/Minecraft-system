<script lang="ts" setup>
// @ts-nocheck
import type { TableColumnsType } from 'antdv-next';

import { computed, onMounted, ref } from 'vue';

import { Page } from '@vben/common-ui';
import { $t } from '@vben/locales';

import {
  Button,
  Card,
  Col,
  Empty,
  Progress,
  Row,
  Select,
  Skeleton,
  Space,
  Statistic,
  Table,
  Tag,
  message,
} from 'antdv-next';

import { getRecommendationAnalytics } from '#/api/management/recommendation';

defineOptions({ name: 'RecommendationDashboard' });

const categoryOptions = [
  { value: '', label: $t('recommendation.categoryAll') },
  { value: 'attraction', label: $t('recommendation.categoryAttraction') },
  { value: 'hotel', label: $t('recommendation.categoryHotel') },
  { value: 'food', label: $t('recommendation.categoryRestaurant') },
  { value: 'product', label: $t('recommendation.categorySouvenir') },
];

const windowOptions = [1, 7, 30, 90].map((d) => ({
  value: d,
  label: `${d} ${$t('recommendation.days')}`,
}));

const category = ref('');
const windowDays = ref(7);
const loading = ref(false);
const data = ref<any>(null);

async function load() {
  loading.value = true;
  try {
    data.value = await getRecommendationAnalytics(
      category.value || undefined,
      windowDays.value,
    );
  } catch {
    message.error($t('recommendation.loadFailed'));
  } finally {
    loading.value = false;
  }
}

const overview = computed(() => data.value?.overview ?? {});
const jobStats = computed(() => data.value?.jobStats ?? {});
const scoreBuckets = computed(() => data.value?.scoreBuckets ?? []);
const topItems = computed(() => data.value?.topItems ?? []);
const maxBucketCount = computed(() =>
  Math.max(1, ...scoreBuckets.value.map((b: any) => Number(b.count) || 0)),
);

function percent(v: any): number {
  return Math.round((Number(v) || 0) * 1000) / 10;
}

const topColumns: TableColumnsType = [
  { title: $t('recommendation.rank'), dataIndex: 'rank', width: 70, align: 'center' },
  { title: $t('recommendation.itemId'), dataIndex: 'itemId', width: 100 },
  { title: $t('recommendation.itemName'), dataIndex: 'name', ellipsis: true },
  { title: $t('recommendation.city'), dataIndex: 'city', width: 110 },
  { title: $t('recommendation.exposures'), dataIndex: 'exposures', width: 110, align: 'right' },
];

const topRows = computed(() =>
  topItems.value.map((item: any, index: number) => ({ ...item, rank: index + 1 })),
);

const jobColumns: TableColumnsType = [
  { title: $t('recommendation.jobLastStatus'), dataIndex: 'lastStatus', width: 110 },
  { title: $t('recommendation.jobTotal'), dataIndex: 'total', width: 90 },
  { title: $t('recommendation.jobSuccess'), dataIndex: 'success', width: 90 },
  { title: $t('recommendation.jobFailed'), dataIndex: 'failed', width: 90 },
  { title: $t('recommendation.jobRunning'), dataIndex: 'running', width: 90 },
  { title: $t('recommendation.jobAvgDuration'), dataIndex: 'avgDurationText', width: 120 },
  { title: $t('recommendation.jobLastRun'), dataIndex: 'lastStartTime', ellipsis: true },
];

function statusColor(status: string): string {
  if (status === 'SUCCESS') return 'success';
  if (status === 'FAILED') return 'error';
  return 'processing';
}

function statusText(status: string): string {
  if (!status) return '-';
  const key =
    'recommendation.status' +
    status[0] +
    status.slice(1).toLowerCase();
  return $t(key);
}

const jobRows = computed(() => {
  const s = jobStats.value;
  if (!s || s.total === 0) return [];
  return [
    {
      ...s,
      avgDurationText:
        s.avgDurationMs == null ? '-' : `${Math.round(s.avgDurationMs)} ms`,
    },
  ];
});

onMounted(load);
</script>

<template>
  <Page auto-content-height>
    <Card :bordered="false">
      <Space wrap>
        <Select
          v-model:value="category"
          :options="categoryOptions"
          style="width: 180px"
          @change="load"
        />
        <Select
          v-model:value="windowDays"
          :options="windowOptions"
          style="width: 130px"
          @change="load"
        />
        <Button type="primary" :loading="loading" @click="load">
          {{ $t('recommendation.refresh') }}
        </Button>
      </Space>
    </Card>

    <Skeleton v-if="loading" active :paragraph="{ rows: 8 }" style="margin-top: 16px" />

    <template v-else>
      <Row :gutter="16" style="margin-top: 16px">
        <Col :xs="24" :sm="12" :lg="8" :xl="4">
          <Card>
            <Statistic
              :title="$t('recommendation.totalExposures')"
              :value="Number(overview.totalExposures) || 0"
            />
          </Card>
        </Col>
        <Col :xs="24" :sm="12" :lg="8" :xl="4">
          <Card>
            <Statistic
              :title="$t('recommendation.uniqueUsers')"
              :value="Number(overview.uniqueUsers) || 0"
            />
          </Card>
        </Col>
        <Col :xs="24" :sm="12" :lg="8" :xl="4">
          <Card>
            <Statistic
              :title="$t('recommendation.exposedItems')"
              :value="Number(overview.exposedItems) || 0"
              :suffix="`/ ${Number(overview.catalogSize) || 0}`"
            />
          </Card>
        </Col>
        <Col :xs="24" :sm="12" :lg="8" :xl="4">
          <Card>
            <Statistic :title="$t('recommendation.coverage')" :value="percent(overview.coverage)" suffix="%" />
          </Card>
        </Col>
        <Col :xs="24" :sm="12" :lg="8" :xl="4">
          <Card>
            <Statistic
              :title="$t('recommendation.interactionRate')"
              :value="percent(overview.interactionRate)"
              suffix="%"
            />
          </Card>
        </Col>
        <Col :xs="24" :sm="12" :lg="8" :xl="4">
          <Card>
            <Statistic
              :title="$t('recommendation.conversionRate')"
              :value="percent(overview.conversionRate)"
              suffix="%"
            />
          </Card>
        </Col>
      </Row>

      <Row :gutter="16" style="margin-top: 16px">
        <Col :xs="24" :lg="12">
          <Card :title="$t('recommendation.scoreBuckets')" :bordered="false">
            <Empty v-if="scoreBuckets.length === 0" :description="$t('recommendation.noData')" />
            <div v-else class="bucket-list">
              <div v-for="b in scoreBuckets" :key="b.bucket" class="bucket-row">
                <span class="bucket-label">{{ b.rangeStart.toFixed(1) }}-{{ b.rangeEnd.toFixed(1) }}</span>
                <Progress
                  :percent="Math.round(((Number(b.count) || 0) / maxBucketCount) * 100)"
                  :show-info="false"
                  size="small"
                  class="bucket-bar"
                />
                <span class="bucket-count">{{ b.count }}</span>
              </div>
            </div>
          </Card>
        </Col>
        <Col :xs="24" :lg="12">
          <Card :title="$t('recommendation.jobStats')" :bordered="false">
            <Empty
              v-if="jobRows.length === 0"
              :description="$t('recommendation.noData')"
            />
            <Table
              v-else
              :columns="jobColumns"
              :data-source="jobRows"
              :pagination="false"
              size="small"
              row-key="total"
            >
              <template #bodyCell="{ column, record }">
                <template v-if="column.dataIndex === 'lastStatus'">
                  <Tag v-if="record.lastStatus" :color="statusColor(record.lastStatus)">
                    {{ statusText(record.lastStatus) }}
                  </Tag>
                  <span v-else>-</span>
                </template>
              </template>
            </Table>
          </Card>
        </Col>
      </Row>

      <Card
        :title="$t('recommendation.topItems')"
        :bordered="false"
        style="margin-top: 16px"
      >
        <Empty v-if="topRows.length === 0" :description="$t('recommendation.noData')" />
        <Table
          v-else
          :columns="topColumns"
          :data-source="topRows"
          :pagination="false"
          size="small"
          row-key="itemId"
        />
      </Card>
    </template>
  </Page>
</template>

<style scoped>
.bucket-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.bucket-row {
  display: flex;
  align-items: center;
  gap: 12px;
}

.bucket-label {
  width: 72px;
  flex-shrink: 0;
  color: rgb(107 114 128);
  font-size: 12px;
}

.bucket-bar {
  flex: 1;
}

.bucket-count {
  width: 56px;
  flex-shrink: 0;
  text-align: right;
  font-variant-numeric: tabular-nums;
}
</style>
