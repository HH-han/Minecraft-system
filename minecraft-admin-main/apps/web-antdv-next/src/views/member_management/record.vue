<script lang="ts" setup>
import type { TableColumnsType } from 'antdv-next';

import { computed, onMounted, ref } from 'vue';

import { Page } from '@vben/common-ui';
import { RefreshCw, Search } from '@vben/icons';
import { $t } from '@vben/locales';

import {
  Button,
  Card,
  Empty,
  Input,
  Table,
  Tag,
} from 'antdv-next';
import dayjs from 'dayjs';

import {
  getMemberGrowthPage,
  getMemberSignPage,
} from '#/api/management/member/member';

defineOptions({ name: 'MemberRecord' });

// ==================== 成长值流水 ====================
const growthLoading = ref(false);
const growthRows = ref<any[]>([]);
const growthUserId = ref('');
const growthKeyword = ref('');
const growthPageNum = ref(1);
const growthPageSize = ref(10);
const growthTotal = ref(0);

// ==================== 签到记录 ====================
const signLoading = ref(false);
const signRows = ref<any[]>([]);
const signUserId = ref('');
const signPageNum = ref(1);
const signPageSize = ref(10);
const signTotal = ref(0);

function formatTime(value?: string) {
  return value ? dayjs(value).format('YYYY-MM-DD HH:mm:ss') : '-';
}

function sourceText(source?: number) {
  const map: Record<number, string> = {
    1: $t('member.record_page.sourceConsume'),
    2: $t('member.record_page.sourceSign'),
    3: $t('member.record_page.sourceComment'),
    4: $t('member.record_page.sourceShare'),
    5: $t('member.record_page.sourceOpen'),
    6: $t('member.record_page.sourceAdmin'),
  };
  return source ? (map[source] ?? String(source)) : '-';
}

const growthColumns = computed<TableColumnsType>(() => [
  { title: $t('member.common.userId'), dataIndex: 'userId', width: 100 },
  { title: $t('member.common.user'), key: 'user', width: 160 },
  { title: $t('member.record_page.changeValue'), key: 'changeValue', width: 100 },
  {
    title: $t('member.record_page.afterValue'),
    dataIndex: 'afterValue',
    width: 130,
  },
  { title: $t('member.record_page.source'), key: 'source', width: 110 },
  { title: $t('member.record_page.bizId'), dataIndex: 'bizId', width: 200 },
  { title: $t('member.common.remark'), dataIndex: 'remark', width: 200 },
  { title: $t('member.record_page.createTime'), key: 'createTime', width: 170 },
]);

const signColumns = computed<TableColumnsType>(() => [
  { title: $t('member.common.userId'), dataIndex: 'userId', width: 100 },
  { title: $t('member.record_page.signDate'), key: 'signDate', width: 130 },
  { title: $t('member.record_page.continuous'), dataIndex: 'continuous', width: 110 },
  {
    title: $t('member.record_page.giveGrowth'),
    dataIndex: 'giveGrowth',
    width: 120,
  },
  {
    title: $t('member.record_page.givePoints'),
    dataIndex: 'givePoints',
    width: 120,
  },
  { title: $t('member.record_page.createTime'), key: 'createTime', width: 170 },
]);

function parseUserId(value: string) {
  const trimmed = value.trim();
  if (!trimmed) return undefined;
  const num = Number(trimmed);
  return Number.isFinite(num) ? num : undefined;
}

async function fetchGrowth() {
  growthLoading.value = true;
  try {
    const res: any = await getMemberGrowthPage({
      pageNum: growthPageNum.value,
      pageSize: growthPageSize.value,
      userId: parseUserId(growthUserId.value),
      keyword: growthKeyword.value.trim() || undefined,
    });
    growthRows.value = res?.records ?? [];
    growthTotal.value = Number(res?.total ?? 0);
  } catch {
    growthRows.value = [];
    growthTotal.value = 0;
  } finally {
    growthLoading.value = false;
  }
}

async function fetchSign() {
  signLoading.value = true;
  try {
    const res: any = await getMemberSignPage({
      pageNum: signPageNum.value,
      pageSize: signPageSize.value,
      userId: parseUserId(signUserId.value),
    });
    signRows.value = res?.records ?? [];
    signTotal.value = Number(res?.total ?? 0);
  } catch {
    signRows.value = [];
    signTotal.value = 0;
  } finally {
    signLoading.value = false;
  }
}

function handleGrowthSearch() {
  growthPageNum.value = 1;
  fetchGrowth();
}

function handleGrowthReset() {
  growthUserId.value = '';
  growthKeyword.value = '';
  growthPageNum.value = 1;
  fetchGrowth();
}

function handleGrowthPageChange(page: number, size: number) {
  growthPageNum.value = page;
  growthPageSize.value = size;
  fetchGrowth();
}

function handleSignSearch() {
  signPageNum.value = 1;
  fetchSign();
}

function handleSignReset() {
  signUserId.value = '';
  signPageNum.value = 1;
  fetchSign();
}

function handleSignPageChange(page: number, size: number) {
  signPageNum.value = page;
  signPageSize.value = size;
  fetchSign();
}

onMounted(() => {
  fetchGrowth();
  fetchSign();
});
</script>

<template>
  <Page
    :title="$t('member.record_page.title')"
    :description="$t('member.record_page.description')"
  >
    <Card
      :title="$t('member.record_page.growthTitle')"
      :styles="{ body: { padding: '16px' } }"
    >
      <div class="mb-4 flex flex-wrap items-center gap-2">
        <Input
          v-model:value="growthUserId"
          :placeholder="$t('member.record_page.filterUserId')"
          class="w-44"
          allow-clear
          @press-enter="handleGrowthSearch"
        />
        <Input
          v-model:value="growthKeyword"
          :placeholder="$t('member.record_page.filterKeyword')"
          class="w-56"
          allow-clear
          @press-enter="handleGrowthSearch"
        />
        <Button type="primary" @click="handleGrowthSearch">
          <template #icon><Search class="size-4" /></template>
          {{ $t('member.common.search') }}
        </Button>
        <Button @click="handleGrowthReset">
          <template #icon><RefreshCw class="size-4" /></template>
          {{ $t('member.common.reset') }}
        </Button>
      </div>

      <Table
        :columns="growthColumns"
        :data-source="growthRows"
        :loading="growthLoading"
        :scroll="{ x: 1300 }"
        row-key="id"
        size="middle"
        :bordered="true"
        :pagination="{
          current: growthPageNum,
          pageSize: growthPageSize,
          total: growthTotal,
          showSizeChanger: true,
          showTotal: (value: number) =>
            $t('member.common.total', { total: value }),
          onChange: handleGrowthPageChange,
        }"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'user'">
            {{ record.nickname || record.account || '-' }}
          </template>
          <template v-else-if="column.key === 'changeValue'">
            <span :class="record.changeValue >= 0 ? 'text-green-600' : 'text-red-500'">
              {{ record.changeValue > 0 ? '+' : '' }}{{ record.changeValue }}
            </span>
          </template>
          <template v-else-if="column.key === 'source'">
            <Tag color="blue">{{ sourceText(record.source) }}</Tag>
          </template>
          <template v-else-if="column.key === 'createTime'">
            {{ formatTime(record.createTime) }}
          </template>
        </template>
        <template #emptyText>
          <Empty :description="$t('member.common.empty')" />
        </template>
      </Table>
    </Card>

    <Card
      class="mt-4"
      :title="$t('member.record_page.signTitle')"
      :styles="{ body: { padding: '16px' } }"
    >
      <div class="mb-4 flex flex-wrap items-center gap-2">
        <Input
          v-model:value="signUserId"
          :placeholder="$t('member.record_page.filterUserId')"
          class="w-44"
          allow-clear
          @press-enter="handleSignSearch"
        />
        <Button type="primary" @click="handleSignSearch">
          <template #icon><Search class="size-4" /></template>
          {{ $t('member.common.search') }}
        </Button>
        <Button @click="handleSignReset">
          <template #icon><RefreshCw class="size-4" /></template>
          {{ $t('member.common.reset') }}
        </Button>
      </div>

      <Table
        :columns="signColumns"
        :data-source="signRows"
        :loading="signLoading"
        :scroll="{ x: 900 }"
        row-key="id"
        size="middle"
        :bordered="true"
        :pagination="{
          current: signPageNum,
          pageSize: signPageSize,
          total: signTotal,
          showSizeChanger: true,
          showTotal: (value: number) =>
            $t('member.common.total', { total: value }),
          onChange: handleSignPageChange,
        }"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'signDate'">
            {{ record.signDate || '-' }}
          </template>
          <template v-else-if="column.key === 'createTime'">
            {{ formatTime(record.createTime) }}
          </template>
        </template>
        <template #emptyText>
          <Empty :description="$t('member.common.empty')" />
        </template>
      </Table>
    </Card>
  </Page>
</template>
