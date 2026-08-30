<script lang="ts" setup>
import type { TableColumnsType } from 'antdv-next';

import type { UserDetail } from '#/api/management/user/user';

import { computed, onMounted, ref } from 'vue';

import { Page } from '@vben/common-ui';
import { $t } from '@vben/locales';
import { preferences } from '@vben/preferences';

import { Button, Card, Result, Skeleton, Table, Tag } from 'antdv-next';
import dayjs from 'dayjs';

import { getUserDetail } from '#/api/management/user/user';

defineOptions({ name: 'UserInfo' });

const loading = ref(false);
const errorMsg = ref('');
const user = ref<UserDetail>();

async function fetchData() {
  loading.value = true;
  errorMsg.value = '';
  try {
    // 调用后端真实接口获取用户数据（非模拟数据）
    user.value = await getUserDetail();
  } catch (error: any) {
    errorMsg.value = error?.message || $t('user.detail.error_desc');
  } finally {
    loading.value = false;
  }
}

onMounted(() => {
  fetchData();
});

/**
 * 日期时间本地化处理
 * - 数字视为毫秒时间戳
 * - 字符串尝试用 dayjs 解析；解析失败原样返回
 */
function formatDateTime(value: number | string | undefined): string {
  if (value === null || value === undefined || value === '') {
    return '-';
  }
  if (typeof value === 'number') {
    return dayjs(value).format('YYYY-MM-DD HH:mm:ss');
  }
  const parsed = dayjs(value);
  return parsed.isValid() ? parsed.format('YYYY-MM-DD HH:mm:ss') : value;
}

function genderText(gender: number | undefined): string {
  switch (gender) {
    case 1: {
      return $t('user.detail.gender.male');
    }
    case 2: {
      return $t('user.detail.gender.female');
    }
    default: {
      return $t('user.detail.gender.unknown');
    }
  }
}

type FieldType =
  | 'avatar'
  | 'date'
  | 'online'
  | 'status'
  | 'tags'
  | 'text';

interface FieldItem {
  key: string;
  label: string;
  type: FieldType;
  value: any;
}

// 将后端用户数据转换为列表数据源
const fields = computed<FieldItem[]>(() => {
  const u = user.value;
  if (!u) {
    return [];
  }
  return [
    { key: 'id', label: $t('user.detail.fields.id'), type: 'text', value: u.id },
    { key: 'account', label: $t('user.detail.fields.account'), type: 'text', value: u.account },
    { key: 'username', label: $t('user.detail.fields.username'), type: 'text', value: u.username },
    { key: 'nickname', label: $t('user.detail.fields.nickname'), type: 'text', value: u.nickname },
    { key: 'gender', label: $t('user.detail.fields.gender'), type: 'text', value: genderText(u.gender) },
    { key: 'age', label: $t('user.detail.fields.age'), type: 'text', value: u.age },
    { key: 'email', label: $t('user.detail.fields.email'), type: 'text', value: u.email },
    { key: 'phone', label: $t('user.detail.fields.phone'), type: 'text', value: u.phone },
    {
      key: 'avatar',
      label: $t('user.detail.fields.avatar'),
      type: 'avatar',
      value: u.avatar,
    },
    { key: 'bio', label: $t('user.detail.fields.bio'), type: 'text', value: u.bio },
    {
      key: 'hobbies',
      label: $t('user.detail.fields.hobbies'),
      type: 'tags',
      value: Array.isArray(u.hobbies) ? u.hobbies : [],
    },
    { key: 'occupation', label: $t('user.detail.fields.occupation'), type: 'text', value: u.occupation },
    {
      key: 'experience',
      label: $t('user.detail.fields.experience'),
      type: 'text',
      value: `${u.experience}${$t('user.detail.experience_unit')}`,
    },
    { key: 'signature', label: $t('user.detail.fields.signature'), type: 'text', value: u.signature },
    {
      key: 'points',
      label: $t('user.detail.fields.points'),
      type: 'text',
      value: `${u.points} ${$t('user.detail.points_unit')}`,
    },
    { key: 'status', label: $t('user.detail.fields.status'), type: 'status', value: u.status },
    { key: 'online', label: $t('user.detail.fields.online'), type: 'online', value: u.online },
    {
      key: 'permissions',
      label: $t('user.detail.fields.permissions'),
      type: 'tags',
      value: Array.isArray(u.permissions) ? u.permissions : [],
    },
    { key: 'createTime', label: $t('user.detail.fields.createTime'), type: 'date', value: u.createTime },
    { key: 'updateTime', label: $t('user.detail.fields.updateTime'), type: 'date', value: u.updateTime },
  ];
});

const columns = computed<TableColumnsType>(() => [
  {
    title: $t('user.detail.column_field'),
    dataIndex: 'label',
    width: 180,
  },
  {
    title: $t('user.detail.column_value'),
    dataIndex: 'value',
  },
]);

function emptyTagsText(key: string): string {
  return key === 'hobbies'
    ? $t('user.detail.no_hobbies')
    : $t('user.detail.no_permissions');
}
</script>

<template>
  <Page :auto-content-height="true">
    <div class="mx-auto w-full max-w-4xl">
      <!-- 加载中：骨架屏 -->
      <Card v-if="loading" :styles="{ body: { padding: '24px' } }">
        <Skeleton :active="true" :paragraph="{ rows: 10 }" />
      </Card>

      <!-- 加载失败：错误占位 + 重试 -->
      <Card v-else-if="errorMsg" :styles="{ body: { padding: '24px' } }">
        <Result
          status="error"
          :title="$t('user.detail.error_title')"
          :sub-title="errorMsg"
        >
          <template #extra>
            <Button type="primary" @click="fetchData">
              {{ $t('user.detail.retry') }}
            </Button>
          </template>
        </Result>
      </Card>

      <!-- 列表展示用户数据 -->
      <Card
        v-else-if="user"
        :title="$t('user.detail.title')"
        :styles="{ body: { padding: '0' } }"
      >
        <Table
          :columns="columns"
          :data-source="fields"
          :pagination="false"
          :bordered="true"
          :show-header="true"
          size="middle"
          row-key="key"
          class="user-detail-table"
        >
          <template #bodyCell="{ column, record }">
            <template v-if="column.dataIndex === 'value'">
              <!-- 头像：原生 loading="lazy" 实现懒加载 -->
              <img
                v-if="record.type === 'avatar'"
                :src="record.value || preferences.app.defaultAvatar"
                loading="lazy"
                class="size-12 rounded-full border border-border object-cover"
                alt="avatar"
              />
              <!-- 状态 Tag -->
              <Tag
                v-else-if="record.type === 'status'"
                :color="record.value === 1 ? 'success' : 'error'"
              >
                {{
                  $t(
                    record.value === 1
                      ? 'user.detail.status.active'
                      : 'user.detail.status.disabled',
                  )
                }}
              </Tag>
              <!-- 在线状态 Tag -->
              <Tag
                v-else-if="record.type === 'online'"
                :color="record.value ? 'processing' : 'default'"
              >
                {{
                  $t(
                    record.value
                      ? 'user.detail.online.online'
                      : 'user.detail.online.offline',
                  )
                }}
              </Tag>
              <!-- 标签组：爱好 / 权限 -->
              <span v-else-if="record.type === 'tags'" class="inline-flex flex-wrap gap-1">
                <template v-if="record.value && record.value.length">
                  <Tag v-for="item in record.value" :key="item" color="blue">
                    {{ item }}
                  </Tag>
                </template>
                <span v-else class="text-muted-foreground">
                  {{ emptyTagsText(record.key) }}
                </span>
              </span>
              <!-- 日期：本地化处理 -->
              <span v-else-if="record.type === 'date'">
                {{ formatDateTime(record.value) }}
              </span>
              <!-- 普通文本 -->
              <span v-else>{{ record.value ?? '-' }}</span>
            </template>
          </template>
        </Table>
      </Card>

      <!-- 无数据兜底 -->
      <Card v-else :styles="{ body: { padding: '24px' } }">
        <Result status="info" :title="$t('user.detail.empty')" />
      </Card>
    </div>
  </Page>
</template>

<style scoped>
.user-detail-table :deep(.ant-table-tbody > tr > td) {
  vertical-align: middle;
}
</style>
