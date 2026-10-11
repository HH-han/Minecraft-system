<script lang="ts" setup>
import type { TableColumnsType } from 'antdv-next';

import { computed, onMounted, reactive, ref } from 'vue';

import { Page } from '@vben/common-ui';
import { RefreshCw, Search } from '@vben/icons';

import {
  Avatar as AntAvatar,
  Button,
  Empty,
  Form,
  FormItem,
  Input,
  InputNumber,
  message,
  Modal,
  Popconfirm,
  Space,
  Table,
  Tag,
} from 'antdv-next';

import { $t } from '@vben/locales';

import {
  deleteChatGroup,
  getGroupMembers,
  getGroupPage,
  removeGroupMember,
  updateChatGroup,
} from '#/api/management/im/group';

defineOptions({ name: 'ImGroupManagement' });

interface GroupRow {
  id: number;
  name?: null | string;
  description?: null | string;
  creatorId?: null | number;
  creatorName?: null | string;
  maxMembers?: null | number;
  memberCount?: null | number;
  avatar?: null | string;
  createTime?: null | string;
  updateTime?: null | string;
}

interface MemberRow {
  id: number;
  userId?: null | number;
  username?: null | string;
  nickname?: null | string;
  avatar?: null | string;
  role?: null | string;
  status?: null | number;
  groupNickname?: null | string;
  joinTime?: null | string;
}

const loading = ref(false);
const rows = ref<GroupRow[]>([]);
const pageNum = ref(1);
const pageSize = ref(10);
const total = ref(0);
const keyword = ref('');

const showEdit = ref(false);
const submitting = ref(false);
const form = reactive({
  id: null as null | number,
  name: '',
  description: '',
  maxMembers: 200 as null | number,
});

const showMembers = ref(false);
const memberLoading = ref(false);
const members = ref<MemberRow[]>([]);
const currentGroup = ref<GroupRow | null>(null);

function formatTime(value?: null | string): string {
  if (!value) return '-';
  return String(value).replace('T', ' ').slice(0, 19);
}
function roleText(role?: null | string): string {
  const key = String(role || '').toLowerCase();
  const map: Record<string, string> = {
    owner: $t('im.group.role.owner'),
    admin: $t('im.group.role.admin'),
    member: $t('im.group.role.member'),
  };
  return map[key] ?? (role || '-');
}
function roleColor(role?: null | string): string {
  const key = String(role || '').toLowerCase();
  return { admin: 'blue', member: 'default', owner: 'gold' }[key] ?? 'default';
}
function displayName(name?: null | string, username?: null | string): string {
  return name || username || '-';
}

const columns = computed<TableColumnsType>(() => [
  { title: 'ID', dataIndex: 'id', width: 70 },
  { title: $t('im.group.columns.avatar'), dataIndex: 'avatar', width: 80 },
  { title: $t('im.group.columns.name'), dataIndex: 'name', width: 180, ellipsis: true },
  {
    title: $t('im.group.columns.description'),
    dataIndex: 'description',
    width: 200,
    ellipsis: true,
  },
  { title: $t('im.group.columns.creator'), key: 'creator', width: 120 },
  { title: $t('im.group.columns.members'), key: 'members', width: 110 },
  { title: $t('im.group.columns.createTime'), key: 'createTime', width: 170 },
  { title: $t('im.group.columns.action'), key: 'action', width: 240, fixed: 'right' },
]);

async function fetchData() {
  loading.value = true;
  try {
    const res: any = await getGroupPage({
      page: pageNum.value,
      size: pageSize.value,
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

function openEdit(row: any) {
  form.id = row.id;
  form.name = row.name ?? '';
  form.description = row.description ?? '';
  form.maxMembers = row.maxMembers ?? 200;
  showEdit.value = true;
}

async function handleSave() {
  if (!form.name.trim()) {
    message.error($t('im.group.form.name_required'));
    return;
  }
  submitting.value = true;
  try {
    await updateChatGroup(form.id, {
      name: form.name,
      description: form.description,
      maxMembers: form.maxMembers,
    });
    message.success($t('im.group.save_success'));
    showEdit.value = false;
    await fetchData();
  } catch (e: any) {
    message.error(e?.message || $t('im.group.action.failed'));
  } finally {
    submitting.value = false;
  }
}

async function handleDissolve(row: any) {
  try {
    await deleteChatGroup(row.id);
    message.success($t('im.group.action.dissolve_success'));
    await fetchData();
  } catch (e: any) {
    message.error(e?.message || $t('im.group.action.failed'));
  }
}

async function openMembers(row: any) {
  currentGroup.value = row;
  showMembers.value = true;
  memberLoading.value = true;
  try {
    const res: any = await getGroupMembers(row.id);
    members.value = res ?? [];
  } catch {
    members.value = [];
  } finally {
    memberLoading.value = false;
  }
}

async function handleRemoveMember(member: any) {
  if (!currentGroup.value) return;
  try {
    await removeGroupMember(currentGroup.value.id, member.userId);
    message.success($t('im.group.action.remove_success'));
    const res: any = await getGroupMembers(currentGroup.value.id);
    members.value = res ?? [];
    await fetchData();
  } catch (e: any) {
    message.error(e?.message || $t('im.group.action.failed'));
  }
}

onMounted(fetchData);
</script>

<template>
  <Page :title="$t('im.group.title')" :description="$t('im.group.description')">
    <div class="bg-card rounded-lg p-4">
      <div class="mb-4 flex flex-wrap items-center gap-2">
        <Input
          v-model:value="keyword"
          :placeholder="$t('im.group.search_placeholder')"
          class="w-52"
          allow-clear
          @press-enter="handleSearch"
        />
        <Button type="primary" @click="handleSearch">
          <template #icon><Search class="size-4" /></template>
          {{ $t('im.group.search') }}
        </Button>
        <div class="flex-1"></div>
        <Button @click="handleReset">
          <template #icon><RefreshCw class="size-4" /></template>
          {{ $t('im.group.reset') }}
        </Button>
      </div>

      <Table
        :columns="columns"
        :data-source="rows"
        :loading="loading"
        :pagination="{
          current: pageNum,
          pageSize,
          total,
          showSizeChanger: true,
          showTotal: (t: number) => $t('im.group.total', { total: t }),
          onChange: handlePageChange,
        }"
        :scroll="{ x: 1200 }"
        row-key="id"
        size="middle"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.dataIndex === 'avatar'">
            <AntAvatar
              :src="record.avatar || undefined"
              :size="40"
              shape="square"
              class="overflow-hidden"
            >
              {{ (record.name || '群').slice(0, 1) }}
            </AntAvatar>
          </template>
          <template v-else-if="column.key === 'creator'">
            {{ record.creatorName || `#${record.creatorId}` }}
          </template>
          <template v-else-if="column.key === 'members'">
            <Tag color="blue">{{ record.memberCount ?? 0 }} / {{ record.maxMembers ?? '-' }}</Tag>
          </template>
          <template v-else-if="column.key === 'createTime'">
            {{ formatTime(record.createTime) }}
          </template>
          <template v-else-if="column.key === 'action'">
            <Space :size="4" wrap>
              <Button type="link" size="small" @click="openMembers(record)">
                {{ $t('im.group.action.members') }}
              </Button>
              <Button type="link" size="small" @click="openEdit(record)">
                {{ $t('im.group.action.edit') }}
              </Button>
              <Popconfirm
                :title="$t('im.group.action.dissolve_confirm')"
                @confirm="handleDissolve(record)"
              >
                <Button type="link" size="small" danger>
                  {{ $t('im.group.action.dissolve') }}
                </Button>
              </Popconfirm>
            </Space>
          </template>
        </template>
        <template #emptyText>
          <Empty :description="$t('im.group.empty')" />
        </template>
      </Table>
    </div>

    <!-- 编辑群组 -->
    <Modal
      v-model:open="showEdit"
      :title="$t('im.group.modal.edit_title')"
      width="560px"
      :mask-closable="false"
      :confirm-loading="submitting"
      :ok-text="$t('im.group.save')"
      :cancel-text="$t('im.group.cancel')"
      @ok="handleSave"
    >
      <Form layout="horizontal" :label-col="{ span: 5 }" :wrapper-col="{ span: 18 }">
        <FormItem :label="$t('im.group.form.name')" required>
          <Input v-model:value="form.name" :maxlength="100" />
        </FormItem>
        <FormItem :label="$t('im.group.form.description')">
          <Input.TextArea v-model:value="form.description" :rows="3" :maxlength="500" />
        </FormItem>
        <FormItem :label="$t('im.group.form.maxMembers')">
          <InputNumber v-model:value="form.maxMembers" :min="1" :precision="0" class="w-full" />
        </FormItem>
      </Form>
    </Modal>

    <!-- 群成员 -->
    <Modal
      v-model:open="showMembers"
      :title="`${$t('im.group.modal.members_title')} · ${currentGroup?.name ?? ''}`"
      width="720px"
      :footer="null"
    >
      <Table
        :columns="[
          { title: 'ID', dataIndex: 'userId', width: 70 },
          { title: $t('im.group.columns.member'), key: 'member', width: 200 },
          { title: $t('im.group.columns.groupNickname'), dataIndex: 'groupNickname', width: 130 },
          { title: $t('im.group.columns.role'), key: 'role', width: 100 },
          { title: $t('im.group.columns.joinTime'), key: 'joinTime', width: 170 },
          { title: $t('im.group.columns.action'), key: 'action', width: 100 },
        ]"
        :data-source="members"
        :loading="memberLoading"
        :pagination="false"
        size="small"
        row-key="id"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'member'">
            <div class="flex items-center gap-2">
              <AntAvatar :src="record.avatar || undefined" :size="28">
                {{ (record.username || '?').slice(0, 1) }}
              </AntAvatar>
              <span>{{ displayName(record.nickname, record.username) }}</span>
            </div>
          </template>
          <template v-else-if="column.key === 'role'">
            <Tag :color="roleColor(record.role)">{{ roleText(record.role) }}</Tag>
          </template>
          <template v-else-if="column.key === 'joinTime'">
            {{ formatTime(record.joinTime) }}
          </template>
          <template v-else-if="column.key === 'action'">
            <Popconfirm
              :title="$t('im.group.action.remove_confirm')"
              @confirm="handleRemoveMember(record)"
            >
              <Button type="link" size="small" danger>
                {{ $t('im.group.action.member_remove') }}
              </Button>
            </Popconfirm>
          </template>
        </template>
        <template #emptyText>
          <Empty :description="$t('im.group.member_empty')" />
        </template>
      </Table>
    </Modal>
  </Page>
</template>
