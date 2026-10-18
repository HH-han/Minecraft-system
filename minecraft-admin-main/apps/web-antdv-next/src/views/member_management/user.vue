<script lang="ts" setup>
import type { TableColumnsType } from 'antdv-next';

import { computed, onMounted, reactive, ref } from 'vue';

import { Page } from '@vben/common-ui';
import { RefreshCw, Search } from '@vben/icons';
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
  Select,
  Space,
  Table,
  Tag,
} from 'antdv-next';
import dayjs from 'dayjs';

import {
  adjustMemberGrowth,
  getMemberLevels,
  getMemberUserPage,
  grantMemberBenefit,
  setMemberLevel,
} from '#/api/management/member/member';

defineOptions({ name: 'MemberUser' });

interface MemberUserItem {
  userId: number | string;
  account?: string;
  username?: string;
  nickname?: string;
  growth?: number;
  level?: number;
  levelName?: string;
  discount?: number;
  pointsRate?: number;
  isPaid?: number;
  expireTime?: string;
  lastSignDate?: string;
  signContinuous?: number;
}

type ActionMode = 'benefit' | 'growth' | 'level' | null;

const loading = ref(false);
const rows = ref<MemberUserItem[]>([]);
const keyword = ref('');
const pageNum = ref(1);
const pageSize = ref(10);
const total = ref(0);
const levels = ref<any[]>([]);

const actionMode = ref<ActionMode>(null);
const actionOpen = ref(false);
const submitting = ref(false);
const currentUser = ref<MemberUserItem | null>(null);
const formRef = ref();
const form = reactive<any>({
  changeValue: 0,
  remark: '',
  levelId: null,
  benefitType: 'BIRTHDAY',
  content: '',
});

const levelOptions = computed(() =>
  levels.value.map((item) => ({
    label: `${item.level} - ${item.name}`,
    value: item.id,
  })),
);

const benefitOptions = computed(() => [
  { label: $t('member.user_page.benefitBirthday'), value: 'BIRTHDAY' },
  { label: $t('member.user_page.benefitCoupon'), value: 'COUPON' },
  { label: $t('member.user_page.benefitOther'), value: 'OTHER' },
]);

const actionTitle = computed(() => {
  if (actionMode.value === 'growth') return $t('member.user_page.adjustGrowth');
  if (actionMode.value === 'level') return $t('member.user_page.setLevel');
  return $t('member.user_page.grantBenefit');
});

function formatTime(value?: string) {
  return value ? dayjs(value).format('YYYY-MM-DD HH:mm:ss') : '-';
}

const columns = computed<TableColumnsType>(() => [
  { title: $t('member.common.userId'), dataIndex: 'userId', width: 100 },
  { title: $t('member.common.user'), key: 'user', width: 180 },
  { title: $t('member.common.level'), key: 'level', width: 130 },
  { title: $t('member.user_page.growth'), dataIndex: 'growth', width: 110 },
  { title: $t('member.user_page.discount'), dataIndex: 'discount', width: 100 },
  {
    title: $t('member.user_page.pointsRate'),
    dataIndex: 'pointsRate',
    width: 100,
  },
  { title: $t('member.user_page.isPaid'), key: 'isPaid', width: 110 },
  { title: $t('member.user_page.expireTime'), key: 'expireTime', width: 170 },
  {
    title: $t('member.user_page.signContinuous'),
    dataIndex: 'signContinuous',
    width: 110,
  },
  {
    title: $t('member.user_page.lastSignDate'),
    key: 'lastSignDate',
    width: 130,
  },
  {
    title: $t('member.common.operation'),
    key: 'actions',
    width: 240,
    fixed: 'right',
  },
]);

async function fetchData() {
  loading.value = true;
  try {
    const res: any = await getMemberUserPage({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      keyword: keyword.value.trim() || undefined,
    });
    rows.value = res?.records ?? [];
    total.value = Number(res?.total ?? 0);
  } catch {
    rows.value = [];
    total.value = 0;
  } finally {
    loading.value = false;
  }
}

async function fetchLevels() {
  try {
    const res: any = await getMemberLevels();
    levels.value = Array.isArray(res) ? res : (res?.records ?? []);
  } catch {
    levels.value = [];
  }
}

function handleSearch() {
  pageNum.value = 1;
  fetchData();
}

function handleReset() {
  keyword.value = '';
  pageNum.value = 1;
  fetchData();
}

function handlePageChange(page: number, size: number) {
  pageNum.value = page;
  pageSize.value = size;
  fetchData();
}

function openAction(record: MemberUserItem, mode: ActionMode) {
  currentUser.value = record;
  actionMode.value = mode;
  form.changeValue = 0;
  form.remark = '';
  form.levelId = null;
  form.benefitType = 'BIRTHDAY';
  form.content = '';
  actionOpen.value = true;
}

async function handleSubmit() {
  if (!currentUser.value) return;
  submitting.value = true;
  try {
    if (actionMode.value === 'growth') {
      await adjustMemberGrowth(currentUser.value.userId, {
        changeValue: form.changeValue,
        remark: form.remark,
      });
      message.success($t('member.user_page.adjustSuccess'));
    } else if (actionMode.value === 'level') {
      if (!form.levelId) {
        message.warning($t('member.user_page.levelSelect'));
        return;
      }
      await setMemberLevel(currentUser.value.userId, {
        levelId: form.levelId,
      });
      message.success($t('member.user_page.setLevelSuccess'));
    } else {
      await grantMemberBenefit(currentUser.value.userId, {
        benefitType: form.benefitType,
        content: form.content,
      });
      message.success($t('member.user_page.grantSuccess'));
    }
    actionOpen.value = false;
    await fetchData();
  } catch (error: any) {
    message.error(error?.message || $t('member.common.saveFailed'));
  } finally {
    submitting.value = false;
  }
}

onMounted(() => {
  fetchLevels();
  fetchData();
});
</script>

<template>
  <Page
    :title="$t('member.user_page.title')"
    :description="$t('member.user_page.description')"
  >
    <Card :styles="{ body: { padding: '16px' } }">
      <div class="mb-4 flex flex-wrap items-center gap-2">
        <Input
          v-model:value="keyword"
          :placeholder="$t('member.user_page.searchPlaceholder')"
          class="w-64"
          allow-clear
          @press-enter="handleSearch"
        />
        <Button type="primary" @click="handleSearch">
          <template #icon><Search class="size-4" /></template>
          {{ $t('member.common.search') }}
        </Button>
        <Button @click="handleReset">
          <template #icon><RefreshCw class="size-4" /></template>
          {{ $t('member.common.reset') }}
        </Button>
      </div>

      <Table
        :columns="columns"
        :data-source="rows"
        :loading="loading"
        :scroll="{ x: 1450 }"
        row-key="userId"
        size="middle"
        :bordered="true"
        :pagination="{
          current: pageNum,
          pageSize,
          total,
          showSizeChanger: true,
          pageSizeOptions: ['5', '10', '20', '50'],
          showTotal: (value: number) =>
            $t('member.common.total', { total: value }),
          onChange: handlePageChange,
        }"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'user'">
            <div class="flex flex-col">
              <span>{{ record.nickname || record.username || record.account || '-' }}</span>
              <span class="text-xs text-gray-400">
                {{ record.account }}
              </span>
            </div>
          </template>
          <template v-else-if="column.key === 'level'">
            <Tag color="blue">
              {{ record.levelName || '-' }}
            </Tag>
          </template>
          <template v-else-if="column.key === 'isPaid'">
            <Tag :color="record.isPaid === 1 ? 'green' : 'default'">
              {{ record.isPaid === 1 ? $t('member.common.yes') : $t('member.common.no') }}
            </Tag>
          </template>
          <template v-else-if="column.key === 'expireTime'">
            {{ formatTime(record.expireTime) }}
          </template>
          <template v-else-if="column.key === 'lastSignDate'">
            {{ record.lastSignDate || '-' }}
          </template>
          <template v-else-if="column.key === 'actions'">
            <Space :size="4" wrap>
              <Button
                type="link"
                size="small"
                @click="openAction(record, 'growth')"
              >
                {{ $t('member.user_page.adjustGrowth') }}
              </Button>
              <Button
                type="link"
                size="small"
                @click="openAction(record, 'level')"
              >
                {{ $t('member.user_page.setLevel') }}
              </Button>
              <Button
                type="link"
                size="small"
                @click="openAction(record, 'benefit')"
              >
                {{ $t('member.user_page.grantBenefit') }}
              </Button>
            </Space>
          </template>
        </template>
        <template #emptyText>
          <Empty :description="$t('member.common.empty')" />
        </template>
      </Table>
    </Card>

    <Modal
      v-model:open="actionOpen"
      :title="actionTitle"
      :width="480"
      :mask-closable="false"
      :confirm-loading="submitting"
      :ok-text="$t('member.common.save')"
      :cancel-text="$t('member.common.cancel')"
      @ok="handleSubmit"
      @cancel="actionOpen = false"
    >
      <Form ref="formRef" :model="form" layout="vertical">
        <template v-if="actionMode === 'growth'">
          <FormItem
            :label="$t('member.user_page.changeValue')"
            name="changeValue"
          >
            <InputNumber v-model:value="form.changeValue" class="w-full" />
          </FormItem>
          <FormItem
            :label="$t('member.user_page.adjustRemark')"
            name="remark"
          >
            <Input v-model:value="form.remark" />
          </FormItem>
        </template>

        <template v-else-if="actionMode === 'level'">
          <FormItem
            :label="$t('member.user_page.levelSelect')"
            name="levelId"
          >
            <Select
              v-model:value="form.levelId"
              :options="levelOptions"
              class="w-full"
            />
          </FormItem>
        </template>

        <template v-else>
          <FormItem
            :label="$t('member.user_page.benefitType')"
            name="benefitType"
          >
            <Select
              v-model:value="form.benefitType"
              :options="benefitOptions"
              class="w-full"
            />
          </FormItem>
          <FormItem
            :label="$t('member.user_page.benefitContent')"
            name="content"
          >
            <Input v-model:value="form.content" />
          </FormItem>
        </template>
      </Form>
    </Modal>
  </Page>
</template>
