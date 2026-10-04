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
  Col,
  Descriptions,
  DescriptionsItem,
  Empty,
  Form,
  FormItem,
  Input,
  Row,
  Select,
  Skeleton,
  Space,
  Switch,
  Table,
  Tag,
  message,
} from 'antdv-next';

import {
  getRecommendationJobs,
  getRecommendationSchedule,
  updateRecommendationConfig,
} from '#/api/management/recommendation';

defineOptions({ name: 'RecommendationSchedule' });

const loading = ref(false);
const saving = ref(false);
const status = ref<any>(null);
const enabled = ref(false);
const cron = ref('');

const categoryOptions = [
  { value: '', label: $t('recommendation.categoryAll') },
  { value: 'attraction', label: $t('recommendation.categoryAttraction') },
  { value: 'hotel', label: $t('recommendation.categoryHotel') },
  { value: 'food', label: $t('recommendation.categoryRestaurant') },
  { value: 'product', label: $t('recommendation.categorySouvenir') },
  { value: 'ALL', label: 'ALL' },
];

const jobLimitOptions = [10, 20, 50, 100, 200].map((v) => ({
  value: v,
  label: String(v),
}));

const jobCategory = ref('');
const jobLimit = ref(20);
const jobs = ref<any[]>([]);

async function loadSchedule() {
  loading.value = true;
  try {
    status.value = await getRecommendationSchedule();
    enabled.value = !!status.value?.scheduled;
    cron.value = status.value?.activeCron ?? '';
  } catch {
    message.error($t('recommendation.loadFailed'));
  } finally {
    loading.value = false;
  }
}

async function loadJobs() {
  try {
    jobs.value =
      (await getRecommendationJobs(jobCategory.value || undefined, jobLimit.value)) ?? [];
  } catch {
    message.error($t('recommendation.loadFailed'));
  }
}

async function loadAll() {
  await Promise.all([loadSchedule(), loadJobs()]);
}

async function saveSchedule() {
  saving.value = true;
  try {
    await updateRecommendationConfig([
      {
        scope: 'GLOBAL',
        category: '',
        paramKey: 'schedule.enabled',
        paramValue: String(enabled.value),
      },
      {
        scope: 'GLOBAL',
        category: '',
        paramKey: 'schedule.cron',
        paramValue: cron.value,
      },
    ]);
    message.success($t('recommendation.scheduleSuccess'));
    await loadSchedule();
  } catch {
    // 非法 cron 等错误由拦截器统一提示
  } finally {
    saving.value = false;
  }
}

function statusColor(s: string): string {
  if (s === 'SUCCESS') return 'success';
  if (s === 'FAILED') return 'error';
  return 'processing';
}

const jobColumns: TableColumnsType = [
  { title: $t('recommendation.jobCategory'), dataIndex: 'category', width: 100 },
  { title: $t('recommendation.triggerType'), dataIndex: 'triggerType', width: 90 },
  { title: $t('recommendation.operator'), dataIndex: 'operatorId', width: 90 },
  { title: '', dataIndex: 'status', width: 90 },
  { title: $t('recommendation.itemCount'), dataIndex: 'itemCount', width: 80 },
  { title: $t('recommendation.edgeCount'), dataIndex: 'edgeCount', width: 80 },
  { title: $t('recommendation.duration'), dataIndex: 'durationMs', width: 110 },
  { title: $t('recommendation.startTime'), dataIndex: 'startTime', width: 170 },
  { title: $t('recommendation.endTime'), dataIndex: 'endTime', width: 170 },
];

onMounted(loadAll);
</script>

<template>
  <Page auto-content-height>
    <Row :gutter="16">
      <Col :xs="24" :lg="12">
        <Card
          :title="$t('recommendation.schedule')"
          :bordered="false"
          style="margin-bottom: 16px"
        >
          <Skeleton v-if="loading" active :paragraph="{ rows: 3 }" />
          <template v-else>
            <Descriptions :column="1" size="small" style="margin-bottom: 16px">
              <DescriptionsItem :label="$t('recommendation.scheduledOn')">
                <Tag :color="status?.scheduled ? 'success' : 'default'">
                  {{
                    status?.scheduled
                      ? $t('recommendation.scheduledOn')
                      : $t('recommendation.scheduledOff')
                  }}
                </Tag>
              </DescriptionsItem>
              <DescriptionsItem :label="$t('recommendation.cron')">
                <code>{{ status?.activeCron || '-' }}</code>
              </DescriptionsItem>
              <DescriptionsItem :label="$t('recommendation.nextRunTime')">
                {{ status?.nextRunTime || '-' }}
              </DescriptionsItem>
            </Descriptions>

            <Form layout="vertical">
              <FormItem :label="$t('recommendation.scheduledOn')">
                <Switch v-model:checked="enabled" />
              </FormItem>
              <FormItem :label="$t('recommendation.cron')">
                <Input v-model:value="cron" placeholder="0 0 3 * * ?" allow-clear />
              </FormItem>
              <Alert
                :message="$t('recommendation.cronHint')"
                type="info"
                show-icon
                style="margin-bottom: 12px"
              />
              <Button type="primary" :loading="saving" @click="saveSchedule">
                {{ $t('recommendation.saveSchedule') }}
              </Button>
            </Form>
          </template>
        </Card>
      </Col>
      <Col :xs="24" :lg="12">
        <Card :title="$t('recommendation.recentJobs')" :bordered="false">
          <Space wrap style="margin-bottom: 12px">
            <Select
              v-model:value="jobCategory"
              :options="categoryOptions"
              style="width: 160px"
              @change="loadJobs"
            />
            <Select
              v-model:value="jobLimit"
              :options="jobLimitOptions"
              style="width: 100px"
              @change="loadJobs"
            />
          </Space>
          <Empty v-if="jobs.length === 0" :description="$t('recommendation.noData')" />
          <Table
            v-else
            :columns="jobColumns"
            :data-source="jobs"
            :pagination="false"
            size="small"
            row-key="id"
            :scroll="{ x: 1000 }"
          >
            <template #bodyCell="{ column, record }">
              <template v-if="column.dataIndex === 'triggerType'">
                <Tag :color="record.triggerType === 'MANUAL' ? 'orange' : 'blue'">
                  {{
                    record.triggerType === 'MANUAL'
                      ? $t('recommendation.triggerManual')
                      : $t('recommendation.triggerSystem')
                  }}
                </Tag>
              </template>
              <template v-else-if="column.dataIndex === 'status'">
                <Tag :color="statusColor(record.status)">
                  {{ $t(`recommendation.status${record.status[0]}${record.status.slice(1).toLowerCase()}`) }}
                </Tag>
              </template>
              <template v-else-if="column.dataIndex === 'durationMs'">
                {{ record.durationMs ?? '-' }}
              </template>
              <template v-else-if="column.dataIndex === 'operatorId'">
                {{ record.operatorId ?? 'SYSTEM' }}
              </template>
            </template>
            <template #expandedRowRender="{ record }">
              <div class="job-error">{{ record.errorMessage || '-' }}</div>
            </template>
          </Table>
        </Card>
      </Col>
    </Row>
  </Page>
</template>

<style scoped>
.job-error {
  padding: 8px 12px;
  color: rgb(185 28 28);
  font-size: 12px;
  white-space: pre-wrap;
  word-break: break-all;
}
</style>
