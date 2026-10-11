<script lang="ts" setup>
import type { TableColumnsType } from 'antdv-next';

import { computed, ref } from 'vue';

import { Page } from '@vben/common-ui';
import { Search } from '@vben/icons';
import { $t } from '@vben/locales';

import {
  Avatar as AntAvatar,
  Button,
  Empty,
  Image,
  Input,
  message,
  Modal,
  Table,
  Tag,
} from 'antdv-next';

import { getUserGroups } from '#/api/management/im/userGroup';
import { getGroupMessagePage } from '#/api/management/im/message';

defineOptions({ name: 'ImUserGroupManagement' });

interface UserGroupRow {
  id: number;
  name?: null | string;
  avatar?: null | string;
  role?: null | string;
  creatorId?: null | number;
  creatorName?: null | string;
  memberCount?: null | number;
  maxMembers?: null | number;
  createTime?: null | string;
}

interface GroupMessageRow {
  id: number;
  senderId?: null | number;
  senderName?: null | string;
  senderAvatar?: null | string;
  content?: null | string;
  messageType?: null | string;
  createTime?: null | string;
}

const userIdInput = ref<null | number>(null);
const searchedUserId = ref<null | number>(null);
const loading = ref(false);
const rows = ref<UserGroupRow[]>([]);

const showMessages = ref(false);
const msgLoading = ref(false);
const messages = ref<GroupMessageRow[]>([]);
const currentGroup = ref<UserGroupRow | null>(null);
const msgPage = ref(1);
const msgSize = ref(10);
const msgTotal = ref(0);

function formatTime(value?: null | string): string {
  if (!value) return '-';
  return String(value).replace('T', ' ').slice(0, 19);
}
function roleText(role?: null | string): string {
  const key = String(role || '').toLowerCase();
  const map: Record<string, string> = {
    owner: $t('im.userGroup.role.owner'),
    admin: $t('im.userGroup.role.admin'),
    member: $t('im.userGroup.role.member'),
  };
  return map[key] ?? (role || '-');
}
function roleColor(role?: null | string): string {
  const key = String(role || '').toLowerCase();
  return { admin: 'blue', member: 'default', owner: 'gold' }[key] ?? 'default';
}
function isImage(type?: null | string, content?: null | string): boolean {
  const t = String(type || '').toLowerCase();
  const c = String(content || '');
  if (t === 'image') return true;
  if (t === 'emoji' && /^https?:\/\//i.test(c)) return true;
  return /\.(png|jpe?g|gif|webp|bmp)(\?.*)?$/i.test(c);
}
function typeText(type?: null | string): string {
  const key = String(type || '').toLowerCase();
  const map: Record<string, string> = {
    text: $t('im.message.type.text'),
    image: $t('im.message.type.image'),
    emoji: $t('im.message.type.emoji'),
    video: $t('im.message.type.video'),
    audio: $t('im.message.type.audio'),
    file: $t('im.message.type.file'),
  };
  return map[key] ?? (type || '-');
}

const columns = computed<TableColumnsType>(() => [
  { title: 'ID', dataIndex: 'id', width: 70 },
  { title: $t('im.userGroup.columns.avatar'), dataIndex: 'avatar', width: 80 },
  { title: $t('im.userGroup.columns.name'), dataIndex: 'name', width: 180, ellipsis: true },
  { title: $t('im.userGroup.columns.role'), key: 'role', width: 100 },
  { title: $t('im.userGroup.columns.members'), key: 'members', width: 110 },
  { title: $t('im.userGroup.columns.createTime'), key: 'createTime', width: 170 },
  { title: $t('im.userGroup.columns.action'), key: 'action', width: 120, fixed: 'right' },
]);

const messageColumns = computed<TableColumnsType>(() => [
  { title: 'ID', dataIndex: 'id', width: 70 },
  { title: $t('im.message.columns.sender'), key: 'sender', width: 160 },
  { title: $t('im.message.columns.content'), key: 'content', width: 260 },
  { title: $t('im.message.columns.type'), key: 'type', width: 90 },
  { title: $t('im.message.columns.time'), key: 'createTime', width: 170 },
]);

async function fetchGroups() {
  if (userIdInput.value == null) {
    message.warning($t('im.userGroup.id_required'));
    return;
  }
  searchedUserId.value = userIdInput.value;
  loading.value = true;
  try {
    const res: any = await getUserGroups(userIdInput.value);
    rows.value = res ?? [];
  } catch {
    rows.value = [];
  } finally {
    loading.value = false;
  }
}

async function openMessages(row: any) {
  currentGroup.value = row;
  showMessages.value = true;
  msgPage.value = 1;
  await fetchMessages();
}

async function fetchMessages() {
  if (!currentGroup.value) return;
  msgLoading.value = true;
  try {
    const res: any = await getGroupMessagePage({
      page: msgPage.value,
      size: msgSize.value,
      groupId: currentGroup.value.id,
    });
    messages.value = res?.records ?? [];
    msgTotal.value = Number(res?.total ?? 0);
  } catch {
    messages.value = [];
    msgTotal.value = 0;
  } finally {
    msgLoading.value = false;
  }
}

function handleMsgPageChange(page: number, size: number) {
  msgPage.value = page;
  msgSize.value = size;
  fetchMessages();
}
</script>

<template>
  <Page :title="$t('im.userGroup.title')" :description="$t('im.userGroup.description')">
    <div class="bg-card rounded-lg p-4">
      <div class="mb-4 flex flex-wrap items-center gap-2">
        <Input
          v-model:value="userIdInput"
          :placeholder="$t('im.userGroup.search_placeholder')"
          class="w-52"
          :min="1"
          type="number"
          allow-clear
          @press-enter="fetchGroups"
        />
        <Button type="primary" :loading="loading" @click="fetchGroups">
          <template #icon><Search class="size-4" /></template>
          {{ $t('im.userGroup.search') }}
        </Button>
        <span v-if="searchedUserId != null" class="text-sm text-muted-foreground">
          {{ $t('im.userGroup.current_user', { userId: searchedUserId }) }}
        </span>
      </div>

      <Table
        :columns="columns"
        :data-source="rows"
        :loading="loading"
        :pagination="false"
        :scroll="{ x: 900 }"
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
          <template v-else-if="column.key === 'role'">
            <Tag :color="roleColor(record.role)">{{ roleText(record.role) }}</Tag>
          </template>
          <template v-else-if="column.key === 'members'">
            {{ record.memberCount ?? 0 }} / {{ record.maxMembers ?? '-' }}
          </template>
          <template v-else-if="column.key === 'createTime'">
            {{ formatTime(record.createTime) }}
          </template>
          <template v-else-if="column.key === 'action'">
            <Button type="link" size="small" @click="openMessages(record)">
              {{ $t('im.userGroup.action.viewMessages') }}
            </Button>
          </template>
        </template>
        <template #emptyText>
          <Empty
            :description="
              searchedUserId == null
                ? $t('im.userGroup.hint')
                : $t('im.userGroup.empty')
            "
          />
        </template>
      </Table>
    </div>

    <!-- 群聊消息 -->
    <Modal
      v-model:open="showMessages"
      :title="`${$t('im.userGroup.messages_title')} · ${currentGroup?.name ?? ''}`"
      width="760px"
      :footer="null"
    >
      <Table
        :columns="messageColumns"
        :data-source="messages"
        :loading="msgLoading"
        :pagination="{
          current: msgPage,
          pageSize: msgSize,
          total: msgTotal,
          showSizeChanger: true,
          showTotal: (t: number) => $t('im.message.total', { total: t }),
          onChange: handleMsgPageChange,
        }"
        size="small"
        row-key="id"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'sender'">
            <div class="flex items-center gap-2">
              <AntAvatar :src="record.senderAvatar || undefined" :size="26">
                {{ (record.senderName || '?').slice(0, 1) }}
              </AntAvatar>
              <span class="truncate">{{ record.senderName || `#${record.senderId}` }}</span>
            </div>
          </template>
          <template v-else-if="column.key === 'content'">
            <Image
              v-if="isImage(record.messageType, record.content)"
              :src="record.content"
              :width="44"
              :height="44"
              class="rounded object-cover"
            />
            <span v-else class="line-clamp-2 break-all">{{ record.content }}</span>
          </template>
          <template v-else-if="column.key === 'type'">
            <Tag>{{ typeText(record.messageType) }}</Tag>
          </template>
          <template v-else-if="column.key === 'createTime'">
            {{ formatTime(record.createTime) }}
          </template>
        </template>
        <template #emptyText>
          <Empty :description="$t('im.userGroup.message_empty')" />
        </template>
      </Table>
    </Modal>
  </Page>
</template>
