<script lang="ts" setup>
import type { TableColumnsType } from 'antdv-next';

import { computed, onMounted, ref } from 'vue';

import { Page } from '@vben/common-ui';
import { $t } from '@vben/locales';

import {
  Avatar,
  Button,
  Card,
  Input,
  message,
  Popconfirm,
  Result,
  Skeleton,
  Table,
  Tag,
} from 'antdv-next';

import { forceLogout, getOnlineUsers } from '#/api/management/service/onlineUsers';

defineOptions({ name: 'OnlineUsersMonitoring' });

interface OnlineUser {
  browser?: null | string;
  id: number;
  lastActivityTime?: null | string;
  loginIp?: null | string;
  loginTime?: null | string;
  nickname?: null | string;
  os?: null | string;
  role?: null | string;
  status?: null | string;
  username?: null | string;
}

// =========================
// 状态
// =========================
const loading = ref(false);
const errorMsg = ref('');
const userList = ref<OnlineUser[]>([]);
const keyword = ref('');
const loggingOutId = ref<number | null>(null);

// =========================
// 工具
// =========================
function displayValue(value: any): string {
  if (value === null || value === undefined || value === '') return '-';
  return String(value);
}

/** 角色文案 */
function roleText(role?: null | string): string {
  if (role === 'admin') return $t('service.online_page.role_admin');
  if (role === 'vip') return $t('service.online_page.role_vip');
  return $t('service.online_page.role_user');
}

/** 角色 Tag 颜色 */
function roleColor(role?: null | string): string {
  if (role === 'admin') return 'red';
  if (role === 'vip') return 'gold';
  return 'blue';
}

/** 用户首字头像背景色（按 id 稳定分配） */
const avatarBgColors = ['#1677ff', '#722ed1', '#13c2c2', '#52c41a', '#fa8c16', '#eb2f96'];
function avatarColor(id: number): string {
  return avatarBgColors[Math.abs(id) % avatarBgColors.length] ?? '#1677ff';
}

// =========================
// 统计（从列表计算）
// =========================
const adminCount = computed(
  () => userList.value.filter((u) => u.role === 'admin').length,
);
const vipCount = computed(
  () => userList.value.filter((u) => u.role === 'vip').length,
);
const otherCount = computed(
  () =>
    userList.value.filter((u) => u.role !== 'admin' && u.role !== 'vip')
      .length,
);

const statCards = computed(() => [
  {
    key: 'online',
    label: $t('service.online_page.stat_online'),
    value: userList.value.length,
    icon: 'i-lucide-wifi',
    color: '#52c41a',
  },
  {
    key: 'admin',
    label: $t('service.online_page.stat_admin'),
    value: adminCount.value,
    icon: 'i-lucide-shield',
    color: '#f5222d',
  },
  {
    key: 'vip',
    label: $t('service.online_page.stat_vip'),
    value: vipCount.value,
    icon: 'i-lucide-crown',
    color: '#fa8c16',
  },
  {
    key: 'other',
    label: $t('service.online_page.stat_other'),
    value: otherCount.value,
    icon: 'i-lucide-users',
    color: '#1677ff',
  },
]);

// =========================
// 过滤
// =========================
const filteredUsers = computed(() => {
  const kw = keyword.value.trim().toLowerCase();
  if (!kw) return userList.value;
  return userList.value.filter(
    (u) =>
      (u.username ?? '').toLowerCase().includes(kw) ||
      (u.nickname ?? '').toLowerCase().includes(kw),
  );
});

// =========================
// 列定义
// =========================
const columns = computed<TableColumnsType>(() => [
  {
    title: $t('service.online_page.fields.user'),
    key: 'user',
    width: 220,
  },
  {
    title: $t('service.online_page.fields.role'),
    key: 'role',
    width: 110,
  },
  {
    title: $t('service.online_page.fields.status'),
    key: 'status',
    width: 90,
  },
  {
    title: $t('service.online_page.fields.loginIp'),
    key: 'loginIp',
    width: 150,
  },
  {
    title: $t('service.online_page.fields.os'),
    key: 'os',
    width: 100,
  },
  {
    title: $t('service.online_page.fields.browser'),
    key: 'browser',
    width: 100,
  },
  {
    title: $t('service.online_page.fields.loginTime'),
    dataIndex: 'loginTime',
    width: 170,
  },
  {
    title: $t('service.online_page.fields.lastActivityTime'),
    dataIndex: 'lastActivityTime',
    width: 170,
  },
  {
    title: $t('service.online_page.fields.action'),
    key: 'action',
    width: 120,
    fixed: 'right',
  },
]);

// =========================
// 数据加载
// =========================
async function fetchData() {
  loading.value = true;
  errorMsg.value = '';
  try {
    const res: any = await getOnlineUsers({});
    // 兼容数组直接返回 / 分页包装 / 未解包（code 字符串由全局拦截器规范化）
    userList.value = Array.isArray(res)
      ? res
      : (res?.records ?? res?.data ?? res?.list ?? []);
  } catch (error: any) {
    errorMsg.value = error?.message || $t('service.online_page.error_desc');
  } finally {
    loading.value = false;
  }
}

onMounted(() => {
  fetchData();
});

// =========================
// 强制下线
// =========================
async function handleForceLogout(record: any) {
  loggingOutId.value = record.id;
  try {
    await forceLogout(record.id);
    message.success($t('service.online_page.logout_success'));
    await fetchData();
  } catch (error: any) {
    message.error(error?.message || $t('service.online_page.logout_failed'));
  } finally {
    loggingOutId.value = null;
  }
}
</script>

<template>
  <Page :auto-content-height="true">
    <div class="mx-auto w-full">
      <!-- 加载中：骨架屏 -->
      <Card
        v-if="loading && userList.length === 0"
        :title="$t('service.online_page.title')"
        :styles="{ body: { padding: '24px' } }"
      >
        <Skeleton :active="true" :paragraph="{ rows: 8 }" />
      </Card>

      <!-- 加载失败：错误占位 + 重试 -->
      <Card
        v-else-if="errorMsg && userList.length === 0"
        :title="$t('service.online_page.title')"
        :styles="{ body: { padding: '24px' } }"
      >
        <Result
          status="error"
          :title="$t('service.online_page.error_title')"
          :sub-title="errorMsg"
        >
          <template #extra>
            <Button type="primary" @click="fetchData">
              {{ $t('service.online_page.retry') }}
            </Button>
          </template>
        </Result>
      </Card>

      <template v-else>
        <!-- 统计卡 -->
        <div class="mb-4 grid grid-cols-2 gap-4 lg:grid-cols-4">
          <Card
            v-for="card in statCards"
            :key="card.key"
            :styles="{ body: { padding: '16px 20px' } }"
          >
            <div class="flex items-center gap-4">
              <div
                class="flex size-11 shrink-0 items-center justify-center rounded-full"
                :style="{ backgroundColor: `${card.color}1a` }"
              >
                <span
                  :class="card.icon"
                  class="size-5"
                  :style="{ color: card.color }"
                ></span>
              </div>
              <div class="min-w-0">
                <div class="truncate text-xs text-muted-foreground">
                  {{ card.label }}
                </div>
                <div class="text-xl font-semibold tabular-nums">
                  {{ card.value }}
                  <span class="text-xs font-normal text-muted-foreground">
                    {{ $t('service.online_page.unit_people') }}
                  </span>
                </div>
              </div>
            </div>
          </Card>
        </div>

        <!-- 在线用户列表 -->
        <Card
          :title="$t('service.online_page.title')"
          :styles="{ body: { padding: '0' } }"
        >
          <template #extra>
            <div class="flex items-center gap-2">
              <Input
                v-model:value="keyword"
                :placeholder="$t('service.online_page.search_placeholder')"
                allow-clear
                class="w-56"
              >
                <template #prefix>
                  <span class="i-lucide-search size-4 text-muted-foreground"></span>
                </template>
              </Input>
              <Button :loading="loading" @click="fetchData">
                <template #icon>
                  <span class="i-lucide-refresh-cw mr-1 size-4 align-middle"></span>
                </template>
                {{ $t('service.online_page.refresh') }}
              </Button>
            </div>
          </template>

          <Table
            :columns="columns"
            :data-source="filteredUsers"
            :loading="loading"
            :pagination="{
              pageSize: 10,
              showSizeChanger: false,
              showTotal: (total: number) => `${total}`,
            }"
            :scroll="{ x: 1230 }"
            size="small"
            row-key="id"
          >
            <template #bodyCell="{ column, record }">
              <!-- 用户（首字头像 + 用户名 + 昵称） -->
              <template v-if="column.key === 'user'">
                <div class="flex items-center gap-3">
                  <Avatar
                    :size="36"
                    :style="{
                      backgroundColor: avatarColor(record.id),
                      color: '#fff',
                      fontSize: '14px',
                      flexShrink: 0,
                    }"
                  >
                    {{ (record.nickname || record.username || '?').slice(0, 1) }}
                  </Avatar>
                  <div class="min-w-0">
                    <div class="truncate text-sm font-medium">
                      {{ displayValue(record.nickname) }}
                    </div>
                    <div class="truncate text-xs text-muted-foreground">
                      {{ displayValue(record.username) }}
                    </div>
                  </div>
                </div>
              </template>

              <!-- 角色 -->
              <template v-else-if="column.key === 'role'">
                <Tag :color="roleColor(record.role)">
                  {{ roleText(record.role) }}
                </Tag>
              </template>

              <!-- 状态 -->
              <template v-else-if="column.key === 'status'">
                <Tag :color="record.status === 'online' ? 'green' : 'default'">
                  {{
                    record.status === 'online'
                      ? $t('service.online_page.online')
                      : $t('service.online_page.offline')
                  }}
                </Tag>
              </template>

              <!-- IP -->
              <template v-else-if="column.key === 'loginIp'">
                <code class="text-xs">{{ displayValue(record.loginIp) }}</code>
              </template>

              <!-- 操作系统 / 浏览器 -->
              <template v-else-if="column.key === 'os'">
                <span class="inline-flex items-center gap-1 text-xs">
                  <span class="i-lucide-monitor size-3.5 text-muted-foreground"></span>
                  {{ displayValue(record.os) }}
                </span>
              </template>
              <template v-else-if="column.key === 'browser'">
                <span class="inline-flex items-center gap-1 text-xs">
                  <span class="i-lucide-globe size-3.5 text-muted-foreground"></span>
                  {{ displayValue(record.browser) }}
                </span>
              </template>

              <!-- 操作：强制下线 -->
              <template v-else-if="column.key === 'action'">
                <Popconfirm
                  :title="$t('service.online_page.logout_confirm_title')"
                  :description="$t('service.online_page.logout_confirm')"
                  :ok-text="$t('service.online_page.logout')"
                  :cancel-text="$t('service.online_page.cancel')"
                  :ok-button-props="{ danger: true }"
                  @confirm="handleForceLogout(record)"
                >
                  <Button
                    type="link"
                    size="small"
                    danger
                    :loading="loggingOutId === record.id"
                  >
                    <template #icon>
                      <span class="i-lucide-log-out mr-1 size-3.5 align-middle"></span>
                    </template>
                    {{ $t('service.online_page.logout') }}
                  </Button>
                </Popconfirm>
              </template>
            </template>

            <template #emptyText>
              <span class="text-sm text-muted-foreground">
                {{ $t('service.online_page.empty') }}
              </span>
            </template>
          </Table>
        </Card>
      </template>
    </div>
  </Page>
</template>
